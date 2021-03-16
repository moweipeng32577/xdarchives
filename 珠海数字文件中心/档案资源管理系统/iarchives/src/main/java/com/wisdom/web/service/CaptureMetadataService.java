package com.wisdom.web.service;

import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by SunK on 2020/5/9 0009.
 */
//业务元数据采集类
@Service
@Transactional
public class CaptureMetadataService {

    @Autowired
    AsyncService asyncService;

    @Autowired
    ServiceMetadataRepositort serviceMetadataRepositort;
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    SystemConfigService systemConfigService;
    @Autowired
    SystemConfigRepository systemConfigRepository;
    @Autowired
    AccreditRepository accreditRepository;
    @Autowired
    ServiceConfigRepository serviceConfigRepository;
    @Autowired
    EntryCaptureService entryCaptureService;
    @Autowired
    TransdocEntryRepository transdocEntryRepository;
    @Autowired
    UserRepository userRepository;

    @Value("${system.document.rootpath}")
    private String rootpath;

    /**
     *
     * @param entryids  条目id集合
     * @param module    模块
     * @param operation 动作
     * @return 记录的条目数
     */

    public int captureServiceMetadataByZL(String[] entryids, String module, String operation) {
        if (null == entryids || entryids.length == 0) {
            return -1;
        }
        SecurityUser userDetiles = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        asyncService.saveserviceConfig(entryids,module,operation,userDetiles.getUserid());
        return entryids.length;
    }


    /**
     * @param entryids  条目id集合
     * @param module    模块
     * @param operation 动作
     * @return 记录的条目数
     */
    public int saveServiceMetadata(String entryids, String module, String operation) {
        if (null == entryids || "".equals(entryids)) {
            return -1;
        }
        if (null == module || "".equals(module)) {
            return -1;
        }
        SecurityUser userDetiles = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        //组装业务元数据对象：
        // 业务状态（赋值：历史行为、计划任务、编辑）、
        // 业务行为（如：著录、归档、移交、入库、导入、导出、开发、销毁鉴定、借阅利用等操作）、
        // 行为时间（当个时间/时间段，著录时间、利用时间段）、
        // 行为描述、
        // 行为依据（授权标识-相关业务操作有对应的授权标识）、
        // 机构人员代码（用户管理-账号）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Tb_service_metadata> list = new ArrayList<>();
        //业务元数据维护
        Tb_service_config service_config = serviceConfigRepository.findByOperationAndParentidIsNull(operation);
        String status = "";
        String msg = "";
        String accredId = "";
        if (service_config != null) {
            status = service_config.getMstatus();
            msg = service_config.getOperationmsg();
            accredId = service_config.getAid();
        }
        // TODO: 2020/5/9 0009 行为依据
        //通过业务行为去参数配置中获取配置的（1.业务状态-mstatus、2.行为描述-operationmsg、3.行为依据-依据简称）
        //业务状态:赋值：历史行为、计划任务、编辑
        List<Tb_system_config> system_configs = systemConfigService.findbyparentvalue(operation);
        Tb_service_metadata service_metadata = new Tb_service_metadata
                (operation, "".equals(status) ? "" : status, sdf.format(new Date()),
                        "".equals(msg) ? "" : msg, "".equals(accredId) ? "" : accredId,
                        userDetiles.getUserid(), entryids);
        list.add(service_metadata);
        serviceMetadataRepositort.save(list);
        return 1;
    }

    /**
     * @param entryidList  条目id集合
     * @param module    模块
     * @param operation 动作
     * @return 记录的条目数
     */
    public int saveServiceMetadataList(List<String> entryidList, String module, String operation, String userid) {
        if (entryidList == null || entryidList.size()==0) {
            return -1;
        }
        if (null == module || "".equals(module)) {
            return -1;
        }
        //组装业务元数据对象：
        // 业务状态（赋值：历史行为、计划任务、编辑）、
        // 业务行为（如：著录、归档、移交、入库、导入、导出、开发、销毁鉴定、借阅利用等操作）、
        // 行为时间（当个时间/时间段，著录时间、利用时间段）、
        // 行为描述、
        // 行为依据（授权标识-相关业务操作有对应的授权标识）、
        // 机构人员代码（用户管理-账号）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String curTime=sdf.format(new Date());
        List<Tb_service_metadata> list = new ArrayList<>();
        //业务元数据维护
        Tb_service_config service_config = serviceConfigRepository.findByOperationAndParentidIsNull(operation);
        String status = "";
        String msg = "";
        String accredId = "";
        if (service_config != null) {
            status = service_config.getMstatus();
            msg = service_config.getOperationmsg();
            accredId = service_config.getAid();
        }
        // TODO: 2020/5/9 0009 行为依据
        //通过业务行为去参数配置中获取配置的（1.业务状态-mstatus、2.行为描述-operationmsg、3.行为依据-依据简称）
        //业务状态:赋值：历史行为、计划任务、编辑
        List<Tb_system_config> system_configs = systemConfigService.findbyparentvalue(operation);
        for(String entryid:entryidList){
            Tb_service_metadata service_metadata = new Tb_service_metadata
                    (operation, "".equals(status) ? "" : status, curTime,
                            "".equals(msg) ? "" : msg, "".equals(accredId) ? "" : accredId,
                            userid, entryid);
            list.add(service_metadata);
        }
        serviceMetadataRepositort.save(list);
        return 1;
    }

    //记录采集导入的元数据信息entryid，晚上再开定时任务处理
    public void addCaptureMetadataTxt(List<String> entryidList){
        SecurityUser userDetiles = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String curTime = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String dirPath=rootpath + File.separator + "CaptureMetadataFile";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filename=userDetiles.getLoginname()+"_"+curTime+"_"+userDetiles.getUserid()+"_"+"CaptureMetadata.txt";//文件名
        File file = new File(dirPath+ File.separator +filename);
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;

        try {
            if (!file.exists()) {
                boolean hasFile = file.createNewFile();
                if(hasFile){
                    System.out.println(dirPath+ File.separator +filename+"文件不存在, 创建一个");
                }
                fos = new FileOutputStream(file);
            } else {
                fos = new FileOutputStream(file, true);
            }
            osw = new OutputStreamWriter(fos, "utf-8");
            for(String entryid:entryidList){
                osw.write(entryid);
                osw.write("\r\n");  //换行
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {   //关闭流
            try {
                if (osw != null) {
                    osw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //读取采集导入的元数据信息txt,插入信息到数据库
    public void readCaptureMetadataTxt(){
        try{
            String dirPath=rootpath + File.separator + "CaptureMetadataFile";
            String curTime = new SimpleDateFormat("yyyyMMdd").format(new Date());
            //目录下的sql文件集合
            List<String> fileList = new ArrayList<>();
            File file = new File(dirPath);
            File[] tempList = file.listFiles();
            for (int i = 0; i < tempList.length; i++) {
                if (tempList[i].isFile()) {
                    fileList.add(tempList[i].toString());
                }
            }
            String fileNames="";//导入的sql文件名
            int i=0;//导入的sql文件数量
            String logPath="";//导出的日志记录
            String filename="";
            String userid="";
            FileInputStream fis=null;
            InputStreamReader isr=null;
            BufferedReader br=null;
            for(int j=0;j<fileList.size();j++){
                List<String> entryidList=new ArrayList<>();
                filename=fileList.get(j);
                if(filename.endsWith("CaptureMetadata.txt")&&filename.contains(curTime)){
                    System.out.println("日志读取开始："+filename);
                    userid=filename.substring(0,filename.lastIndexOf("_"));
                    userid=userid.substring(userid.lastIndexOf("_")+1);
                    logPath=filename;
                    fis=new FileInputStream(filename);
                    isr=new InputStreamReader(fis, "UTF-8");
                    br = new BufferedReader(isr);

                    int errSum=0;//异常记录数
                    int okSum=0;//成功更新数量
                    String line="";
                    try {
                        while ((line=br.readLine())!=null) {
                            line=line.trim();
                            if(line.length()>=32&&line.length()<=36){
                                entryidList.add(line);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (br != null) {
                                br.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            if (isr != null) {
                                isr.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            if (fis != null) {
                                fis.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    saveServiceMetadataList(entryidList, "数据采集", "导入", userid);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }


    public List<ExtNcTree> findByParentconfigid(String parentconfigid) {
        List<Tb_service_config> service_configs;
        if ("".equals(parentconfigid) || null == parentconfigid) {
            service_configs = serviceConfigRepository.findByParentidIsNullOrderBySortsequence();
        } else {
            service_configs = serviceConfigRepository.findByParentidOrderBySortsequence(parentconfigid);
        }
        List<ExtNcTree> extNcTrees = new ArrayList<>();
        for (int i = 0; i < service_configs.size(); i++) {
            ExtNcTree tree = new ExtNcTree();
            tree.setFnid(service_configs.get(i).getCid());
            tree.setCls("file");
            tree.setLeaf(true);
            tree.setText(service_configs.get(i).getOperation());
            extNcTrees.add(tree);
        }
        return extNcTrees;
    }

    public Page<Tb_service_config> findBySearch(int page, int limit, String condition, String operator, String content,
                                                String configid, Sort sort) {
        PageRequest pageRequest = new PageRequest(page - 1, limit,
                sort == null ? new Sort(Sort.Direction.ASC, "sortsequence") : sort);
        Specification<Tb_service_config> searchid;
        if ("".equals(configid)) {
            searchid = getSearchParentconfigidIsnullCondition();
        } else {
            searchid = getSearchParentconfigidEqualCondition(configid);
        }
        Specifications specifications = Specifications.where(searchid);
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        return serviceConfigRepository.findAll(specifications, pageRequest);
    }

    public static Specification<Tb_service_config> getSearchParentconfigidIsnullCondition() {
        Specification<Tb_service_config> searchParentidIsnullCondition = new Specification<Tb_service_config>() {
            @Override
            public Predicate toPredicate(Root<Tb_service_config> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.isNull(root.get("parentid"));
                return criteriaBuilder.or(p);
            }
        };
        return searchParentidIsnullCondition;
    }

    public static Specification<Tb_service_config> getSearchParentconfigidEqualCondition(String parentid) {
        Specification<Tb_service_config> searchParentidEqualCondition = new Specification<Tb_service_config>() {
            @Override
            public Predicate toPredicate(Root<Tb_service_config> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.equal(root.get("parentid"), parentid);
                return criteriaBuilder.or(p);
            }
        };
        return searchParentidEqualCondition;
    }


    public Tb_service_config findByConfigid(String configid) {
        return serviceConfigRepository.findByCid(configid);
    }

    public Tb_service_config findByParentidAndParentidIsNull(String parentid) {
        return serviceConfigRepository.findByParentidAndParentidIsNull(parentid);
    }

    public Tb_service_config saveServiceConfig(Tb_service_config service_config) {
        return serviceConfigRepository.save(service_config);
    }

    public Tb_service_config findByParentidAndOperation(String perantid, String code) {
        return serviceConfigRepository.findByParentidAndOperation(perantid, code);
    }

    public Tb_service_config findByOperationAndParentidIsNull(String operation) {
        return serviceConfigRepository.findByOperationAndParentidIsNull(operation);
    }

    public Integer countByParentidAndOperation(String patentid, String operation) {
        return serviceConfigRepository.countByParentidAndOperation(patentid, operation);
    }


    public Integer countByOperationAndParentidIsNull(String operation) {
        return serviceConfigRepository.countByOperationAndParentidIsNull(operation);
    }

    public Integer deleteByCidIn(String[] configids) {
        return serviceConfigRepository.deleteByCidIn(configids);
    }

    public Integer deleteByCid(String configids) {
        return serviceConfigRepository.deleteByCid(configids);
    }


    public List<Tb_system_config> queryConditionTemplate(String metadataType) {
        if (null == metadataType || "".equals(metadataType)) {
            return new ArrayList<>();
        }
        List<Tb_system_config> metadata_temps = systemConfigRepository.findByParentCode(metadataType);
        return metadata_temps;
    }

    public List<Tb_accredit> queryAccreditTemplate() {

        List<Tb_accredit> metadata_temps = accreditRepository.findByParentidIsNullOrderBySortsequence();
        return metadata_temps;
    }

    public String[] getAllId(String entryids, String nodeid, String condition, String operator, String content) {
        String ids = entryids;//选择条目字符串
        //先判断是不是全选操作
        if (entryids.startsWith("isSelectAll")) {//全选操作还要去除取消选择的entryid
            ids = "";
            String delEntryids = "";
            if (entryids.length() > 11) {//截取后边的entryid字符串
                delEntryids = entryids.substring(entryids.indexOf("All") + 3, entryids.length());
            }
            List<Tb_entry_index_capture> entryIndexCaptures = entryCaptureService.getEntryCaptureList(nodeid, condition,
                    operator, content);
            if (entryIndexCaptures.size() > 0) {
                for (int i = 0; i < entryIndexCaptures.size(); i++) {
                    if (delEntryids.indexOf(entryIndexCaptures.get(i).getEntryid()) == -1) {
                        ids += entryIndexCaptures.get(i).getEntryid() + ",";
                    }
                }
                ids = ids.length() > 0 ? ids.substring(0, ids.length() - 1) : "";
            }
        }
        String[] entryidData = ids.split(",");// 1.移交所选数据
        return entryidData;
    }

    public String[] getIdByYJ(String docid, String entry) {
        String[] id = new String[0];
        if ("".equals(docid) || null == docid) {
            return id;
        }
        if ("".equals(entry) || null == entry) {
            return id;
        }
        if ("isSelectAll".equals(entry)) {
            String ids = "";
            List<Tb_transdoc_entry> tb_transdoc_entries = transdocEntryRepository.findByDocid(docid);
            for (Tb_transdoc_entry entry1 : tb_transdoc_entries) {
                ids += entry1.getEntryid() + ",";
            }
            String[] entryidData = ids.split(",");// 1.移交所选数据
            return entryidData;
        } else {
            String[] entryidData = entry.split(",");// 1.移交所选数据
            return entryidData;
        }
    }

    public List<Tb_user> getuserList(String userid){
        if(null!=userid){
            Tb_user user  = userRepository.findByUserid(userid);
            if(null!=user){
                List<Tb_user> users = userRepository.findByOrganid(user.getOrganid());
                return users;
            }
        }
        return null;
    }
}
