package com.wisdom.service.webservice.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.wisdom.service.jms.ActiveMQService;
import com.wisdom.service.jms.MessageProducerService;
import com.wisdom.service.webservice.service.SharedWebService;
import com.wisdom.util.FileUtil;
import com.wisdom.util.MD5;
import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.service.ElectronicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specifications;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

/**
 * 用户服务实现类
 * Created by wjh
 */
public class SharedWebServiceImpl implements SharedWebService {

    private static Logger LOGGER = LoggerFactory.getLogger(SharedWebServiceImpl.class);

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RightOrganRepository rightOrganRepository;

    @Autowired
    SzhElectronicCaptureRepository szhElectronicCaptureRepository;

    @Autowired
    SzhEntryIndexCaptureRepository szhEntryIndexCaptureRepository;

    @Autowired
    MessageProducerService messageProducerService;

    @Autowired
    UserFunctionSxRepository userFunctionSxRepository;

    @Autowired
    UserDataNodeSxRepository userDataNodeSxRepository;

    @Autowired
    RoleFunctionSxRepository roleFunctionSxRepository;

    @Autowired
    RoleDataNodeSxRepository roleDataNodeSxRepository;

    @Autowired
    ActiveMQService activeMQService;

    @Autowired
    ElectronicService electronicService;

    @Autowired
    SzhCalloutEntryRepository szhCalloutEntryRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    DataNodeSxRepository dataNodeSxRepository;

    @Autowired
    TemplateSxRepository templateSxRepository;

    @Autowired
    CodesetSxRepository codesetSxRepository;

    @Autowired
    ClassificationSxRepository classificationSxRepository;

    @Override
    public String login(String user, String password) {
        String token = null;
        Tb_user dbuser = userRepository.findByLoginname(user);
        //用户名不存在或密码错误,直接返回空
        if(dbuser == null || !password.equals(dbuser.getPassword())){
            return token;
        }
        token = MD5.AESencode(user + "&" + password);
        return token;
    }

    private boolean validate(String token){
        Boolean result = false;
        try {
            String[] str = MD5.AESdecode(token).split("&");
            String user = str[0];
            String password = str[1];
            Tb_user dbuser = userRepository.findByLoginname(user);
            //用户名不存在或密码错误,直接返回空
            if(dbuser == null || !password.equals(dbuser.getPassword())){
                return result;
            }
            result = true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String destroyQueue(String token) {
        try {
            activeMQService.destoryDestination(token);
        } catch (Exception e){
            return  null;
        }
        return token;
    }

    @Override
    @Transactional
    public synchronized String clearExsit(String token, String archivecode, String type) {
        if(!validate(token)){
            return null;
        }
        String entryid = szhEntryIndexCaptureRepository.findEntryByArchivecode(archivecode).getEntryid();
        String dir = "";
        switch (type) {
            case "scan":
                String batchname = szhCalloutEntryRepository.queryBatchname(archivecode);
                dir = electronicService.getScanStorageDir(batchname, archivecode);
                break;
            case "other":
                dir = electronicService.getStorageBaseDir("capture", entryid);
                break;
            default:
                break;
        }
        FileUtil.delFolder(dir);
        szhElectronicCaptureRepository.deleteByEntryidIn(new String[]{entryid});
        //删除条目表中页数
        Szh_entry_index_capture entry = szhEntryIndexCaptureRepository.findByEntryid(entryid);
        entry.setEleid(String.valueOf(0));
        entry.setPages(String.valueOf(0));
        szhEntryIndexCaptureRepository.save(entry);
        //删除调档文件页数
        szhCalloutEntryRepository.pageReset(entryid);
        return token;
    }

    @Override
    public synchronized String uploadValidate(String token, String archivecode) {
        List<Szh_entry_index_capture> list = szhEntryIndexCaptureRepository.findByArchivecode(archivecode);
        if(list.size() != 1){
            return null;
        }
        return list.get(0).getEntryid();
    }

    @Override
    @Transactional
    public synchronized String downloadQueue(String token, String archivecodes, String type) {
        List<Object[]> list = szhElectronicCaptureRepository.findByArchivecodeIn(archivecodes.split(","));
        if(list == null || list.size() == 0){
            return null;
        }
        try {
            for (String archivecode : archivecodes.split(",")){
                activeMQService.destoryDestination(token + archivecode);
            }
        for (Object[] obj : list){
                messageProducerService.producerMessage(token, obj, type);
        }
        } catch (Exception e){
            return null;
        }
        return token;
    }

    @Override
    public synchronized String listfiles(String token, String archivecode) {
        List<Object[]> list = szhElectronicCaptureRepository.findByArchivecodeIn(new String[]{archivecode});
        if(list == null || list.size() == 0){
            return null;
        }
        return JSON.toJSONString(list);
    }

    @Override
    public synchronized String countScanFiles(String token, String batchname, String archivecode) {
        String dir = electronicService.getScanStorageDir(batchname, archivecode);
        File dirFile = new File(dir);
        Integer count = dirFile.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return true;
            }
        }).length;
        return count.toString();
    }

    @Override
    public String getSharedData(int dataType,String condition, String operator, String content, Integer page, Integer limit) {
        SharedPage sharedPage = new SharedPage();
        try {
            List list= getCombinationCondition(condition,operator,content,page,limit);
            Specifications sp = (Specifications)list.get(0);
            PageRequest pageRequest = (PageRequest)list.get(1);
            Page p = null;
            switch (dataType){
                case 1:
                    if(sp==null){p = entryIndexRepository.findAll(pageRequest);}
                    else{p = entryIndexRepository.findAll(sp,pageRequest);}break;
                case 2:
                    if(sp==null){ p = userRepository.findAll(pageRequest);}
                    else{p = userRepository.findAll(sp,pageRequest);}break;
                case 3:
                    if(sp==null){p = rightOrganRepository.findAll(pageRequest);}
                    else{p = rightOrganRepository.findAll(sp,pageRequest);}break;
                case 4://用户功能权限
                    if(sp==null){p = userFunctionSxRepository.findAll(pageRequest);}
                    else{
                        String[] userids=content.split(",");
                        p = userFunctionSxRepository.findByUseridIn(userids,pageRequest);
                    }break;
                case 6://数据节点权限
                    if(sp==null){p = userDataNodeSxRepository.findAll(pageRequest);}
                    else{
                        String[] userids=content.split(",");
                        p = userDataNodeSxRepository.findByUseridIn(userids,pageRequest);
                    }break;
                case 7://用户组功能权限
                    if(sp==null){p = roleFunctionSxRepository.findAll(pageRequest);}
                    else{
                        String[] roleids=content.split(",");
                        p = roleFunctionSxRepository.findByRoleidIn(roleids,pageRequest);
                    }break;
                case 8://用户组数据权限
                    if(sp==null){p = roleDataNodeSxRepository.findAll(pageRequest);}
                    else{
                        String[] roleids=content.split(",");
                        p = roleDataNodeSxRepository.findByRoleidIn(roleids,pageRequest);
                    }break;
                case 9://用户组
                    if(sp==null){p = roleRepository.findAll(pageRequest);}
                    else{
                        p = roleRepository.findAll(sp,pageRequest);
                    }break;
                case 31://机构增加后的数据节点同步
                    if(sp==null){p = dataNodeSxRepository.findAll(pageRequest);}
                    else{
                        p = dataNodeSxRepository.findByOrganid(content,pageRequest);
                    }break;
                case 32://机构增加后的模板同步
                    if(sp==null){p = templateSxRepository.findAll(pageRequest);}
                    else{
                        p = templateSxRepository.findByOrganid(content,pageRequest);
                    }break;
                case 33://机构增加后的档号同步
                    if(sp==null){p = codesetSxRepository.findAll(pageRequest);}
                    else{
                        p = codesetSxRepository.findByOrganid(content,pageRequest);
                    }break;
                case 51://声像分类增加后的数据同步
                    if(sp==null){p = classificationSxRepository.findAll(pageRequest);}
                    else{
                        p = classificationSxRepository.findByClassid(content,pageRequest);
                    }break;
                case 52://声像分类增加后的数据节点同步
                    if(sp==null){p = dataNodeSxRepository.findAll(pageRequest);}
                    else{
                        p = dataNodeSxRepository.findByClassid(content,pageRequest);
                    }break;

                default:
                    return "共享数据类型参数有误(dataType类型1为共享业务数据,2为共享用户数据,3为共享机构用户数据)";
            }
            sharedPage.setData(p.getContent());
            sharedPage.setPageSize(limit==null?20:limit);//每页数
            sharedPage.setCurrentPage(page==null?1:page);//页码
            sharedPage.setTotalPage(p.getTotalPages());//总页数
            sharedPage.setTotalCount(Integer.parseInt(p.getTotalElements()+""));//总条目数
            sharedPage.setMsg("获取共享机构数据成功");
        }catch (Exception e){
            LOGGER.error("获取共享机构数据出错",e);
            return "获取共享机构数据出错";
        }
        return JSON.toJSONString(sharedPage);
    }

    public List getCombinationCondition(String condition, String operator, String content, Integer page, Integer limit)throws Exception{
        Specifications sp = null;
        if(condition != null&&operator != null&&content != null){
            String[] conditions = condition.split(",");
            String[] operators = operator.split(",");
            String[] contents = content.split(",");
            for (int i = 0; i < contents.length; i++) {
                sp = sp==null?Specifications.where(new SpecificationUtil(conditions[i],operators[i],contents[i])):sp.and(new SpecificationUtil(conditions[i],operators[i],contents[i]));
            }
        }
        page = page==null?1:page;
        limit = limit==null?20:limit;
        PageRequest pageRequest = new PageRequest(page-1,limit);
        return Arrays.asList(sp,pageRequest);
    }
}