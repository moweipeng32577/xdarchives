package com.wisdom.web.service;

import com.wisdom.web.entity.Tb_electronic;
import com.wisdom.web.entity.Tb_notice;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.repository.ElectronicRepository;
import com.wisdom.web.repository.NoticeRepository;
import com.wisdom.web.security.SecurityUser;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class NoticeService {

    @Value("${system.document.rootpath}")
    private String rootpath;

    private static long chunkSize = 5242880;// 文件分片大小5M

    @Autowired
    ElectronicRepository electronicRepository;

    @Autowired
    NoticeRepository noticeRepository;

    @Autowired
    LogService logService;

    public Page<Tb_notice> getNotices(int page,  int limit, String condition, String operator, String content, String sort, String type) {
        Sort sortobj = WebSort.getSortByJson(sort);
        Specifications specifications = null;
        if ("1".equals(type)) {//利用平台显示已发布的
            Specification<Tb_notice> searchid = new Specification<Tb_notice>() {
                @Override
                public Predicate toPredicate(Root<Tb_notice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    Predicate state = cb.equal(root.get("publishstate"), "1");
                    return cb.and(state);
                }
            };
            specifications = Specifications.where(searchid);
        }
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(new Sort.Order(Sort.Direction.DESC, "stick"));//置顶
        sorts.add(new Sort.Order(Sort.Direction.DESC, "publishtime"));//发布时间
        return noticeRepository.findAll(specifications, new PageRequest(page - 1, limit, (Sort) (sortobj == null ? new Sort(sorts) : sortobj)));
    }

    public Integer deleteElectronic(String eleids) {
        String[] eleidArray = eleids.split(",");
        List<Tb_electronic> electronics = electronicRepository.findByEleidInOrderBySortsequence(eleidArray);// 获取删除电子文件
        for (Tb_electronic electronic : electronics) {
            File file = new File(rootpath + electronic.getFilepath() + "/" + electronic.getFilename());
            file.delete();// 删除电子文件
        }
        return electronicRepository.deleteByEleidIn(eleidArray);
    }

    private String getUploadDir() {
        String dir = "";
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dir = rootpath + "/notice/" + userDetails.getUsername();
        File upDir = new File(dir);
        if (!upDir.exists()) {
            upDir.mkdirs();
        }

        return dir;
    }

    public Map<String, Object> saveElectronic(String informid, String filename) {
        File targetFile = new File(getUploadDir(), filename);
        Map<String, Object> map = new HashMap<>();
        Tb_electronic ele = new Tb_electronic();
        ele.setEntryid(informid == null ? "" : informid);
        ele.setFilename(filename);
        ele.setFilepath(getUploadDir().replace(rootpath, ""));
        ele.setFilesize(String.valueOf(targetFile.length()));
        ele.setFiletype(filename.substring(filename.lastIndexOf('.') + 1));
        ele = electronicRepository.save(ele);// 保存电子文件
        map = ele.getMap();
        return map;
    }

    public void uploadchunk(Map<String, Object> param) throws Exception {
        String tempFileName = param.get("filename") + "_tmp";
        File confFile = new File(getUploadDir(), param.get("filename") + ".conf");
        File tmpFile = new File(getUploadDir(), tempFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "rw");

        long offset = chunkSize * Integer.parseInt((String) param.get("chunk"));
        // 定位到该分片的偏移量
        accessTmpFile.seek(offset);
        // 写入该分片数据
        accessTmpFile.write((byte[]) param.get("content"));

        // 把该分段标记为 true 表示完成
        accessConfFile.setLength(Integer.parseInt((String) param.get("chunks")));
        accessConfFile.seek(Integer.parseInt((String) param.get("chunk")));
        accessConfFile.write(Byte.MAX_VALUE);

        // completeList 检查是否全部完成,如果数组里是否全部都是(全部分片都成功上传)
        byte[] completeList = FileUtils.readFileToByteArray(confFile);
        byte isComplete = Byte.MAX_VALUE;
        for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
            // 与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
            isComplete = (byte) (isComplete & completeList[i]);
        }


    }

    public void uploadfileInform(Map<String, Object> param) throws Exception {
        String targetFileName = (String) param.get("filename");
        File tmpFile = new File(getUploadDir(), targetFileName);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        // 写入数据
        accessTmpFile.write((byte[]) param.get("content"));
        accessTmpFile.close();
    }

    public List<Tb_electronic> getFile(String noticeID) {
        return electronicRepository.findByEntryidOrderBySortsequence(noticeID);
    }

    public Tb_notice addNotice(String noticeID, String title, String organ, String publishstate, String stick, String html, String[] eleids) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();// 获取安全对象
        Tb_notice notice = new Tb_notice();
        notice.setTitle(title);
        notice.setOrgan(organ);
        notice.setUserID(userDetails.getUserid());
        notice.setContent(html);
        if ("1".equals(publishstate)){//已发布
            notice.setPublishtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            notice.setPublishstate(1);
        }
        if (!("".equals(stick)) && 0 != stick.length() && null != stick){//设置置顶登记
            notice.setStick(Integer.valueOf(stick));
        }
        String[] noticeIDs = {noticeID};
        if ((!"".equals(noticeID)) || (null != noticeID)){
            noticeRepository.deleteByNoticeID(noticeIDs);
        }
        Tb_notice noticeSave = noticeRepository.save(notice);
        electronicRepository.updateEntryid(noticeSave.getNoticeID(), eleids);
        String msg = "通知公告;新增公告:"+title+";条目id:"+noticeSave.getNoticeID();
        if (noticeID != null&&!"".equals(noticeID)) {
            msg = "通知公告;修改公告:"+title+"条目id:"+noticeSave.getNoticeID();
        }
        logService.recordTextLog("通知公告",msg);
        return noticeSave;
    }

    public Tb_notice findNotice(String noticeID) {
        return noticeRepository.findByNoticeID(noticeID);
    }

    public boolean deleteNotice(String[] noticeIDs) {
        Integer result = noticeRepository.deleteByNoticeID(noticeIDs);
        if (result < 0){
            return false;
        }else{
            Integer resultElectronic = electronicRepository.deleteByEntryidIn(noticeIDs);
            if (resultElectronic < 0){
                return false;
            }
        }
        return true;
    }

    public Integer publish(String[] noticeIDs, String state) {
        int publishstate = Integer.parseInt(state);
        if (null==noticeIDs||null==state){
            return 0;
        }
        for(String str : noticeIDs){
            if("1".equals(state)){
                logService.recordTextLog("通知公告","发布;条目id:"+str);
            }else {
                logService.recordTextLog("通知公告","取消发布;条目id:"+str);
            }
        }
        return noticeRepository.updatePublishstate(noticeIDs,"1".equals(state) ? publishstate:null,
                "1".equals(state) ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) : "");
    }

    public void setStick(String[] ids, String level) {
        List<Tb_notice> notices = noticeRepository.findByNoticeID(ids);
        for(Tb_notice notice:notices){
            notice.setStick(Integer.parseInt(level));
        }
    }

    public boolean cancelStick(String[] ids) {
        boolean state = false;
        List<Tb_notice> notices = noticeRepository.findByNoticeID(ids);
        for(Tb_notice notice:notices){
            if(notice.getStick()!=null){
                notice.setStick(null);
                state = true;
            }
        }
        return state;
    }

    public List<Tb_notice> getPublishNotices() {
        return noticeRepository.findByPublishstate();
    }
}