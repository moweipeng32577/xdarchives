package com.wisdom.util;

import com.wisdom.secondaryDataSource.entity.*;
import com.wisdom.secondaryDataSource.repository.*;
import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yl on 2018/1/12.
 * 定时任务
 */
@Component
public class TimeScheduled {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录

    @Value("${task.oa.entry.opened}")
    private String oaEntryOpened;//oa数据导入开关

    @Value("${task.oa.organ.opened}")
    private String oaOrganOpened;//oa机构导入开关

    @Value("${task.oa.user.opened}")
    private String oaUserOpened;//oa用户导入开关

    @Value("${task.backup.setting.opened}")
    String backupSettingOpened;//设置数据备份开关

    @Value("${task.backup.data.opened}")
    String backupDataOpened;//设置数据备份开关

    @Value("${task.clear.entryindextemp.opened}")
    String clearEntryindextempOpened;

    @Value("${task.oa.filepath}")
    private String filepath;//oa文件根目录

    @Value("${task.solidify.opened}")
    private String solidifyOpened;//固化任务开关

    @Value("${task.solidify.stop.time}")
    private String solidifyStopTime;//固化任务开关

    @Value("${ftpHost}")
    private String ftpHost;
    @Value("${ftpUserName}")
    private String ftpUserName;
    @Value("${ftpPassword}")
    private String ftpPassword;
    @Value("${ftpPath}")
    private String ftpPath;
    @Value("${ftpPort}")
    private String ftpPort;
    @Value("${ftpFileManage}")
    private String ftpFileManage;
    @Value("${ftpOpen}")
    private String ftpOpen;
    @Value("${ftpFormat}")
    private String ftpFormat;

    @Autowired
    private JyAdminsService jyAdminsService;

    @Autowired
    private UserService userService;

    @Autowired
    private BackupService backupService;

    @Autowired
    BackupStrategyService backupStrategyService;

    @Autowired
    EntryIndexTempService entryIndexTempService;

    @Autowired
    DiskspaceService diskspaceService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private OrgUnitRepository orgUnitRepository;

    @Autowired
    private RightOrganRepository rightOrganRepository;

    @Autowired
    private InterDataRepository interDataRepository;

    @Autowired
    private OrganService organservice;

    @Autowired
    private EntryIndexRepository entryIndexRepository;

    @Autowired
    private ElectronicRepository electronicRepository;

    @Autowired
    private EntryDetailRepository entryDetailRepository;

    @Autowired
    private DataNodeRepository dataNodeRepository;

    @Autowired
    private AppDocRepository appDocRepository;

    @Autowired
    private OAUserRepository oaUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFunctionRepository userFunctionRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserDataNodeRepository userDataNodeRepository;

    @Autowired
    private UserNodeRepository userNodeRepository;

    @Autowired
    private PersonalizedRepository personalizedRepository;

    @Autowired
    private UserOrganRepository userOrganRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private NodesettingService nodesettingService;

    @Autowired
    ElectronicService electronicService;

    @Autowired
    TemplateService templateService;

    @Autowired
    Ftpservice ftpservice;

    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    DataDeleteRepository dataDeleteRepository;

    @Autowired
    RoleFunctionRepository roleFunctionRepository;

    @Autowired
    CaptureMetadataService captureMetadataService;

    @PersistenceContext
    EntityManager entityManager;

    public static List<String> saveFileName = new ArrayList<>();
    /**
     * 每天早上8点30分(08:30)执行，查询实体查档的到期时间是否已到或者是前一天时间
     * 1.审批通过时间+查档天数==当前时间(推送"该条目已到期"通知给发起人和审批人)
     * 2.审批通过时间+(查档天数-1)==当前时间(推送"该条目即将到期"通知给发起人和审批人)
     */
    @Scheduled(cron = "${task.borrow.cron}")
    @Transactional
    public void borrowExpireRemind() {
        logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "------borrowExpireRemind 定时任务准备执行------");
        List<Tb_borrowmsg> overdueMsgs = jyAdminsService.getOverdueMsg();
        //List<Tb_borrowmsg> imminentMsgs=jyAdminsService.getImminentMsg();//即将到期的条目
        Set<String> userids = new HashSet<>();//保存审批人id
        Map<String, Tb_task> taskMap = new HashMap<>();
        for (Tb_borrowmsg tb_borrowmsg : overdueMsgs) {
            if (!taskMap.containsKey(tb_borrowmsg.getApprover())) {//一个审批人只允许有一条查档到期提醒
                Tb_task taskForApprover = new Tb_task();
                taskForApprover.setLoginname(tb_borrowmsg.getApprover());
                taskForApprover.setState(Tb_task.STATE_WAIT_HANDLE);//待处理
                taskForApprover.setText(tb_borrowmsg.getBorrowman() + "有到期记录!");
                taskForApprover.setType("查档到期提醒");
                taskForApprover.setTime(new Date());
                taskForApprover.setBorrowmsgid(tb_borrowmsg.getId());
                taskMap.put(tb_borrowmsg.getApprover(), taskForApprover);
                userids.add(tb_borrowmsg.getApprover());
            }
        }
        if (taskMap.size() > 0) {
            String[] userArray = userids.toArray(new String[userids.size()]);
            taskRepository.deleteByLoginnameInAndTasktype(userArray, "查档到期提醒");//删除之前的到期提醒通知
            taskRepository.save(taskMap.values());//生成到期提醒通知
        }

        webSocketService.noticeRefresh();//通知全部链接用户更新通知
    }

    /**
     * 每天早上8点30分(08:30)执行，若电子文件存储磁盘已用空间超过阈值，则发送消息给系统管理员
     *
     * @throws IOException
     */
    @Scheduled(cron = "0 30 08 ? * *")
    public void diskspaceFullRemind() throws IOException {
        logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "------diskspaceFullRemind 定时任务准备执行------");
        if (diskspaceService.diskspaceOverThresholdvalue()) {//磁盘空间超出设定阈值
            Tb_task taskForSystemAdministrator = new Tb_task();
            Tb_user systemAdministratorUser = userRepository.findByLoginname(Tb_log_msg.getSystem());
            taskForSystemAdministrator.setLoginname(systemAdministratorUser != null ? systemAdministratorUser.getUserid() : "");
            taskForSystemAdministrator.setState(Tb_task.STATE_WAIT_HANDLE);//待处理

            Properties prop = new Properties();// 属性集合对象
            String path = TimeScheduled.class.getClassLoader().getResource("application.properties").getPath();
            path = java.net.URLDecoder.decode(path, "utf-8");
            // 属性文件输入流
            FileInputStream fis = new FileInputStream(path);
            prop.load(fis);// 将属性文件流装载到Properties对象中
            fis.close();// 关闭流
            taskForSystemAdministrator.setText("达到阈值" + prop.getProperty("system.diskspace.threshold") + "%！");
            taskForSystemAdministrator.setType("磁盘存储空间不足提醒");
            taskForSystemAdministrator.setTime(new Date());
            taskRepository.save(taskForSystemAdministrator);//生成任务
            webSocketService.noticeRefresh(systemAdministratorUser.getUserid());//刷新系统管理员用户消息
        }
    }

    /**
     * 从0点开始，每小时检测系统时间是否为备份策略中设置时间，
     * 若日期和时间匹配符合，则执行设置数据备份
     *
     * @throws Exception
     */
//    @Scheduled(cron = "00 00 18 ? * FRI")//每周五18点执行一次
    @Scheduled(cron = "0 0 0/1 * * ?")//每小时执行一次
    public void backupSetting() throws Exception {
        if (!"true".equals(backupSettingOpened.trim())) {// 判断设置数据备份开关
            return;
        }
        boolean timeOut = backupStrategyService.checkTimeOut("setting");
        if (timeOut) {
            backupService.backupAll("setting");
        }
    }

    /**
     * 从0点开始，每小时检测系统时间是否为备份策略中设置时间，
     * 若日期和时间匹配符合，则执行业务数据备份
     *
     * @throws Exception
     */
//    @Scheduled(cron = "00 00 18 ? * FRI")//每周五18点执行一次
    @Scheduled(cron = "0 0 0/1 * * ?")//每小时执行一次
    public void backupData() throws Exception {
        if (!"true".equals(backupDataOpened.trim())) {// 判断业务数据备份开关
            return;
        }
        boolean timeOut = backupStrategyService.checkTimeOut("data");
        if (timeOut) {
            backupService.backupAll("data");
        }
    }

    /**
     * 　每周一0点清空一次条目临时表数据，防止批量修改预览操作造成无用数据大量堆积
     *
     * @throws Exception
     */
    @Scheduled(cron = "00 00 00 ? * MON")//每周一0点执行一次
    public void deleteEntryindexTempData() {
        if (!"true".equals(clearEntryindextempOpened.trim())) {
            return;
        }
        entryIndexTempService.deleteAllEntryindex();
    }

    /**
     *  每天0点更新一次到期鉴定的数量
     */
//    @Scheduled(cron = "0 0 23 * * ?")
    public void updateAppraisal(){
        System.out.println("-------- 定时更新到期鉴定开始");
        dataNodeMap=new HashMap<>();//清空上一次保存的用户父节点
        //某些数据库无法使用GROUP BY分组查询
        //String dataSql = "select nodeid,Count(nodeid) as 'number' from v_index_detail where " + DBCompatible.getInstance().findAppraisalOverdueData("FileDate","")+" GROUP BY nodeid ";
        //String[] nodeids=entryIndexRepository.findNodeidByEntryretention("永久");
        String dataSql = "select * from v_index_detail where " + DBCompatible.getInstance().findAppraisalOverdueData("FileDate","",0,"","");
        Query query = entityManager.createNativeQuery(dataSql,Tb_index_detail.class);
        //List<Object[]> resultList= new ArrayList<>();
        dataDeleteRepository.deleteDate();//清空数据

        //计算节点的条目数量
        Map<String,Integer> numberMap=new HashMap<>();//到期鉴定
        List<Tb_index_detail> index_detailList=query.getResultList();
        index_detailList.stream().forEach(indexDetail->{
            if(numberMap.get(indexDetail.getNodeid())==null){
                numberMap.put(indexDetail.getNodeid().trim(),1);
            }else {
                Integer number= numberMap.get(indexDetail.getNodeid().trim());
                numberMap.put(indexDetail.getNodeid().trim(),number+1);
            }
        });
        for (String s : numberMap.keySet()) {//查询所有到期鉴定鉴定的节点和数量保存到数据库
            saveDataDelete(s,"",numberMap.get(s));
        }
        List<String> userIds= userFunctionRepository.findByFnid("ntb4ce6e4cca4cfefew341b89ehr56g4");//到期鉴定权限的用户
        List<String> roleUserIds=roleFunctionRepository.findUserIdByFnid("ntb4ce6e4cca4cfefew341b89ehr56g4");//到期鉴定角色权限
        roleUserIds.remove(userIds);
        userIds.addAll(roleUserIds);
        userIds.stream().forEach(uid->{
            List<Tb_data_node> userNodeList=nodesettingService.findUserNodeList(uid);//用户个人权限节点
            List<Tb_data_node> userRoleNodeList=nodesettingService.findRoleNodeList(uid);//用户角色权限节点
            userRoleNodeList.removeAll(userNodeList);
            userNodeList.addAll(userRoleNodeList);
            Integer[] number=new Integer[]{0};
            if(dataNodeMap.get(uid)==null){//创建用户map
                dataNodeMap.put(uid,new HashMap<>());
            }
            userNodeList.forEach(userNode->{
                if(numberMap.get(userNode.getNodeid().trim())!=null){//判断权限
                    getParentNode(userNode.getNodeid().trim(), uid,numberMap);
                    //根据用户节点计算到期鉴定数量
                    number[0]+=Integer.parseInt(numberMap.get(userNode.getNodeid().trim()).toString());
                }
            });
            if(number[0]>0){//有数量才发送消息
                Tb_task task = new Tb_task();
                task.setLoginname(uid);
                task.setState(Tb_task.STATE_WAIT_HANDLE);//待处理
                task.setText("您有"+number[0]+"条到期鉴定记录!");
                task.setType("到期鉴定提醒");
                task.setTime(new Date());
                taskRepository.deleteByLoginnameAndTasktype(uid,"到期鉴定提醒");//删除未查看的
                taskRepository.save(task);
            }
        });
        webSocketService.noticeRefresh();
        System.out.println("-------- 定时更新到期鉴定结束");
    }

    //保存到期鉴定数据
    public Tb_data_delete saveDataDelete(String nodeId,String userId,Integer number){
        Tb_data_delete data_delete=new Tb_data_delete();
        data_delete.setNodeId(nodeId);
        data_delete.setUserId(userId);
        data_delete.setNumber(number);
        return dataDeleteRepository.save(data_delete);
    }

    //保存用户的父节点
    private Map<String,Map<String,String>> dataNodeMap=new HashMap<>();
    //获取节点的所有父节点
    public void getParentNode(String nodeId,String userId,Map<String,Integer> map){
        Tb_data_node dataNode=dataNodeRepository.findByNodeid(nodeId);
        //父节点没有条目，子节点有条目才保存
        if(dataNode!=null&&!"".equals(dataNode.getParentnodeid())&& //避免重复性添加
                dataNodeMap.get(userId)!=null&&dataNodeMap.get(userId).get(dataNode.getParentnodeid().trim())==null){
            dataNodeMap.get(userId).put(dataNode.getParentnodeid().trim(),dataNode.getParentnodeid());
            if(map.get(dataNode.getParentnodeid().trim())==null){
                saveDataDelete(dataNode.getParentnodeid(), userId, 0);
            }
            getParentNode(dataNode.getParentnodeid(),userId,map);
        }
    }

    //查询鉴定过期节点并存到静态区
    public void saveAppraisalNode(){
        if(GuavaCache.getValueByKey(GuavaUsedKeys.APPRAISAL_NODE) == null){//首次启动执行鉴定数量的更新
            List<Tb_data_delete> dataDeleteList=dataDeleteRepository.findAll();
            Map<String,Integer> map=new HashMap<>();
            if(dataDeleteList.size()>0){
                dataDeleteList.parallelStream().forEach(data->{
                    if("".equals(data.getUserId().trim())||data.getUserId()==null){
                        map.put(data.getNodeId().trim(),data.getNumber());
                    }
                });
            }
            GuavaCache.setKeyValue(GuavaUsedKeys.APPRAISAL_NODE, map);
        }
    }

    /**
     * OA条目数据导入
     *
     * @return
     */
    @Scheduled(cron = "${task.oa.cron}")
    public String importOAEntries() {
        if (oaEntryOpened != null && !oaEntryOpened.trim().equals("true")) {
            return "请打开程序开关！";
        }

        long startTime = System.currentTimeMillis();
        String successTip = "成功：";//操作日志标头
        String failTip = "--失败--：";
        List<InterData> interDataList;//查询需要导入的数据
        try {
            interDataList = interDataRepository.getEntryByFilter();
        } catch (Exception e) {
            LogOAImport(failTip + "查询OA数据 （提示：请检查OA的数据库连接！ ，异常信息：" + e.getMessage() + "）" +
                    "\n--------------------------------------------------------------------------------------------------------------", 1);
            e.printStackTrace();
            return "查询OA数据失败：请检查OA的数据库连接！";
        }
        if (interDataList.size() == 0) {
            String restr = "没有找到需要导入的数据！";
            LogOAImport(restr + "\n--------------------------------------------------------------------------------------------------------------", 1);
            return restr;
        }

        LogOAImport("接收数据：开始！ （条目总数：" + interDataList.size() + " 条）\n", 1);

        List<Tb_data_node> findAllNode = dataNodeRepository.findAll();//优化效率
        List<Tb_right_organ> findAllOrgan = rightOrganRepository.findAll();
//        int i = 0;//临时待删
        for (InterData interData : interDataList) {
//            i++;//临时待删
//            if (i > 10) {//临时待删
//                break;//临时待删
//            }//临时待删
            List<AppDoc> appDocList = appDocRepository.getByFileidAndIsdeletedAndUrl(interData.getFileid());
            Tb_entry_index entry_index = new Tb_entry_index();
            try {
                String nodeName = interData.getWjlx().substring(interData.getWjlx().length() - 2, interData.getWjlx().length());
                String parentNodeId = null;
                for (Tb_data_node node : findAllNode) {
                    if (node.getNodename().equals(nodeName)) {//如：“XXX签报”，应有 “签报” 节点，且唯一
                        parentNodeId = node.getNodeid();
                        break;
                    }
                }
                if (parentNodeId == null) {
                    LogOAImport(failTip + "保存条目数据 （接口表ID：" + interData.getId() + "，题名:" + interData.getTitle() + " ，异常信息：没有找到该数据节点：“" + nodeName + "”）", 1);
                    LogOAImport("", 0);
                    continue;
                }
                String organId = null;
                //标记为总行的数据都放在总行节点下
                String unitCode = ("总行".equals(interData.getGdtype()) && "富滇银行".equals(interData.getDraftUnitname())) ? "OU=bgs/OU=zx/O=0000001" : interData.getDraftUnitcode();
                for (Tb_right_organ organ : findAllOrgan) {
                    if (organ.getRefid() != null && organ.getRefid().equals(unitCode)) {
                        organId = organ.getOrganid();
                    }
                }
                if (organId == null) {
                    LogOAImport(failTip + "保存条目数据 （接口表ID：" + interData.getId() + "，题名:" + interData.getTitle() + " ，异常信息：在档案系统的机构中，未找到OA系统对应的机构码 DraftUnitcode：“" + unitCode + "”）", 1);
                    LogOAImport("", 0);
                    continue;
                }
                Tb_user_node_parents unp = nodesettingService.getFindNode(parentNodeId, organId, findAllNode);
                if (unp == null) {
                    LogOAImport(failTip + "保存条目数据 （接口表ID：" + interData.getId() + "，题名:" + interData.getTitle() + " ，异常信息：在该节点 ParentId:" + parentNodeId + " 下没有找到机构 FindId:" + organId + " 节点）", 1);
                    LogOAImport("", 0);
                    continue;
                }

                //1.保存条目index对象
                entry_index.setNodeid(unp.getNodeid());
                entry_index.setEleid(null);
                if (appDocList.size() != 0) {
                    entry_index.setEleid(appDocList.size() + "");
                }
                setIndexValue(entry_index, interData, nodeName);//字段值匹配
//                entry_index.setTitle(interData.getTitle() + "OATEST");//临时待删
                entry_index = entryIndexRepository.save(entry_index);

                //2.保存条目detail对象
                Tb_entry_detail detail = new Tb_entry_detail();
                setDetailValue(detail, interData, nodeName);//字段值匹配
                detail.setEntryid(entry_index.getEntryid());
//                detail.setF01("oatest");//临时待删
                entryDetailRepository.save(detail);

                //3.反馈接收条目信息
                if (entry_index.getEntryid() != null) {
                    interDataRepository.updateInterData(interData.getId(), "oatest", new java.sql.Date(System.currentTimeMillis()));//临时待改
                    LogOAImport(successTip + "保存条目数据 （接口表ID：" + interData.getId() + "，题名:" + interData.getTitle() + " ，entry_index表ID:" + entry_index.getEntryid() + "）", 1);
                }
            } catch (Exception e) {
                LogOAImport(failTip + "保存条目数据 （接口表ID：" + interData.getId() + "，题名:" + interData.getTitle() + " ，异常信息：" + e.getMessage() + "）", 1);
                LogOAImport("", 0);
                e.printStackTrace();
                continue;
            }

            if (appDocList.size() == 0) {
                LogOAImport("", 0);
            }
            for (AppDoc appDoc : appDocList) {
                //4.复制文件
                String docPath = appDoc.getUrl().replace("//", "/");
                File srcFile = new File(filepath + docPath);
                String docName = appDoc.getTitle();
                if ("blank".equals(appDoc.getTitle())) {
                    docName = interData.getTitle();
                    Matcher m = Pattern.compile("[|?/\\\\*\"<>:]").matcher(docName);
                    docName = m.replaceAll(" ");
                }
                if ("稿纸".equals(appDoc.getPrefix())) {
                    docName = "原文稿纸：" + docName;
                }
                String dir = getStorageBaseDir("management") + "/" + entry_index.getEntryid().trim();
                try {
                    File targetFile = new File(rootpath + dir + "/" + docName + "." + appDoc.getExt());
                    FileUtils.copyFile(srcFile, targetFile);
                    LogOAImport(successTip + "下载文件 （路径：" + docPath + "）", 1);
                } catch (IOException e) {
                    e.printStackTrace();
                    LogOAImport(failTip + "下载文件 （路径：" + docPath + " ，异常信息：" + e.getMessage() + "）", 1);
                    continue;
                }

                try {
                    //5.保存电子文件对象
                    Tb_electronic ele = new Tb_electronic();
                    ele.setEntryid(entry_index.getEntryid());
                    ele.setFilename(docName + "." + appDoc.getExt());
                    ele.setFilepath(dir);
                    ele.setFilefolder("");
//                    ele.setFilefolder("/oatest");//临时待删
                    ele.setFilesize(String.valueOf(srcFile.length()));
                    ele.setFiletype(srcFile.getName().substring(srcFile.getName().lastIndexOf('.') + 1));
                    ele = electronicRepository.save(ele);

                    //6.反馈接收附件信息
                    if (ele.getEleid() != null) {
                        appDocRepository.updateAppdoc(appDoc.getId(), "oatest");//临时待改
                        LogOAImport(successTip + "保存文件数据 （附件表ID：" + appDoc.getId() + "，文件名:" + ele.getFilename() + " ，electronic表ID:" + ele.getEleid() + "）", 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogOAImport(failTip + "保存文件数据 （附件表ID：" + appDoc.getId() + "，文件名:" + srcFile.getName() + " ，异常信息：" + e.getMessage() + "）", 1);
                }
            }
            if (appDocList.size() != 0) {
                LogOAImport("", 0);
            }
        }
        String time = (float) (System.currentTimeMillis() - startTime) / 1000 + "s";
        System.out.println("OA条目导入用时：" + time);
        LogOAImport("接收数据：结束！用时：" + time + "\n--------------------------------------------------------------------------------------------------------------", 1);
        return "接收数据结束！用时：" + time;
    }

    /**
     * 打印导入条目的日志信息
     *
     * @param info      打印信息
     * @param printTime 是否打印时间 0：否 1：是
     */
    private void LogOAImport(String info, int printTime) {
        File dir = new File(rootpath + "/OAFile");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(rootpath + "/OAFile/OAImport.log");
        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(file, true), "GBK");
            String time = "";
            if (printTime == 1) {
                time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "：";
            }
            writer.write(time + info + "\n");
            writer.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getStorageBaseDir(String entrytype) {
        Calendar cal = Calendar.getInstance();
        return "/electronics/storages/" + cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DATE) + "/" + entrytype;
    }

    /**
     * 匹配设置字段值
     *
     * @param entry_detail
     * @param interData
     * @param type
     * @return
     */
    private Tb_entry_detail setDetailValue(Tb_entry_detail entry_detail, InterData interData, String type) {
        Map<String, String> mapping = new HashMap<>();
        if ("收文".equals(type)) {
            mapping.put("f01", "swLwdw");
            mapping.put("f04", "swSwdate");
            mapping.put("f06", "swExigency");
            mapping.put("f07", "swZbdw");
            mapping.put("f08", "swYbjlb");
            mapping.put("f02", "swSwdw");
            mapping.put("f09", "drafter");
        } else if ("发文".equals(type)) {
            mapping.put("f01", "fwZs");
            mapping.put("f02", "fwCs");
            mapping.put("f03", "fwYjr");
            mapping.put("f06", "fwSyscode");
            mapping.put("f07", "drafter");
        } else if ("签报".equals(type)) {
            mapping.put("f01", "qbTypes");
            mapping.put("f02", "qbBm");
            mapping.put("f03", "qbEmergency");
            mapping.put("f04", "qbPerson");
            mapping.put("f06", "qbMainsender");
            mapping.put("f08", "qbNgdate");
        }

        for (String key : mapping.keySet()) {
            String value = "";
            if (GainField.getFieldValueByName(mapping.get(key), interData) != null) {
                value = GainField.getFieldValueByName(mapping.get(key), interData) + "";
                if (mapping.get(key).equals("swFileno") || mapping.get(key).equals("fwFileno") || mapping.get(key).equals("qbQbno")) {
                    value = value.replace("〔", "[");
                    value = value.replace("〕", "]");
                }
            }
            GainField.setFieldValueByName(key, entry_detail, value);
        }
        return entry_detail;
    }

    /**
     * 匹配设置字段值
     *
     * @param entry_index
     * @param interData
     * @param type
     * @return
     */
    private Tb_entry_index setIndexValue(Tb_entry_index entry_index, InterData interData, String type) {
        Map<String, String> mapping = new HashMap<>();
        if ("收文".equals(type)) {
            mapping.put("title", "title");
            mapping.put("filenumber", "swFileno");
            mapping.put("filingyear", "nd");
            mapping.put("entrysecurity", "swSecret");
        } else if ("发文".equals(type)) {
            mapping.put("title", "title");
            mapping.put("filingyear", "nd");
            mapping.put("filenumber", "fwFileno");
            mapping.put("entrysecurity", "fwSecret");
        } else if ("签报".equals(type)) {
            mapping.put("title", "title");
            mapping.put("filenumber", "qbQbno");
            mapping.put("filingyear", "nd");
            mapping.put("entrysecurity", "qbSecret");
        }

        for (String key : mapping.keySet()) {
            String value = "";
            if (GainField.getFieldValueByName(mapping.get(key), interData) != null) {
                value = GainField.getFieldValueByName(mapping.get(key), interData) + "";
                if (mapping.get(key).equals("swFileno") || mapping.get(key).equals("fwFileno") || mapping.get(key).equals("qbQbno")) {
                    value = value.replace("〔", "[");
                    value = value.replace("〕", "]");
                }
            }
            GainField.setFieldValueByName(key, entry_index, value);
        }
        return entry_index;
    }

    /**
     * OA机构表数据导入
     */
    public String importOAOrgUnit() {
        if (!oaOrganOpened.trim().equals("true")) {
            return "请先在配置文件中打开对应功能开关！";
        }
        long startTime = System.currentTimeMillis();
        List<OrgUnit> orgUnitList = orgUnitRepository.findByIdParentorgunitIsNull();//提前查询，防止异常

        String[] exceptUsers = {"aqbm", "aqsj", "xitong"};
        List<Tb_user> userList = userRepository.findByLoginnameIn(exceptUsers);//删除所有用户(三个管理员除外)
        String[] ids = GainField.getFieldValues(userList, "userid").length == 0 ?
                new String[]{""} : GainField.getFieldValues(userList, "userid");
        userFunctionRepository.deleteAllByUseridNotIn(ids);//级联删除相关数据
        userRoleRepository.deleteAllByUseridNotIn(ids);
        userGroupRepository.deleteAllByUseridNotIn(ids);
        userDataNodeRepository.deleteAllByUseridNotIn(ids);
        userNodeRepository.deleteAllByUseridNotIn(ids);
        personalizedRepository.deleteAllByUseridNotIn(ids);
        userRepository.deleteAllByUseridNotIn(ids);
        userRepository.setAdminNull(exceptUsers);//设置三员的机构NULL

        roleRepository.deleteByIds(ids);//删除三员所属角色以外的角色
        userOrganRepository.deleteAll();//删除机构权限
        rightOrganRepository.deleteAll();//删除所有机构

        Tb_right_organ organ;
        for (OrgUnit orgUnit : orgUnitList) {
            if (orgUnit.getName() != null && orgUnit.getName().contains("系统维护组")) {
                continue;
            }
            organ = new Tb_right_organ();
            organ.setSystemid("402789f55d54e087015d54e0cfca0000");
            organ.setServicesid("402789f55d54dc21015d54dccadd0000");
            organ.setSystemname("OA");
            organ.setServicesname("");
            organ.setOrgantype("");
            //organ.setOrganlevel(Integer.parseInt(1 + ""));
            organ.setUsestatus(1 + "");
            organ.setParentid(0 + "");
            organ.setOrganname(orgUnit.getName());
            organ.setRefid(orgUnit.getCode());
            organ.setSortsequence(Integer.parseInt(orgUnit.getSort()));
            organ = rightOrganRepository.save(organ);

            saveOrgUnit(orgUnit, organ.getOrganid());
        }

        organservice.updateOrganlevel();

        //授权给安全保密管理员、系统管理员
        String aqbmUserid = userRepository.findByLoginname("aqbm").getUserid();
        String xitongUserid = userRepository.findByLoginname("xitong").getUserid();
        List<Tb_right_organ> rightOrganList = rightOrganRepository.findAll();
        Tb_user_organ userOrgan;
        List<Tb_user_organ> list = new ArrayList<>();
        for (Tb_right_organ rightOrgan : rightOrganList) {
            //为安全保密员 授予 机构 权限
            userOrgan = new Tb_user_organ();
            userOrgan.setUserid(aqbmUserid);
            userOrgan.setOrganid(rightOrgan.getOrganid());
            list.add(userOrgan);

            //为系统管理员授权
            userOrgan = new Tb_user_organ();
            userOrgan.setUserid(xitongUserid);
            userOrgan.setOrganid(rightOrgan.getOrganid());
            list.add(userOrgan);
        }
        userOrganRepository.save(list);

        userRepository.updateAdminOrgan("总行", exceptUsers);//更新三员的机构ID
        long endTime = System.currentTimeMillis();
        String tip = "OA机构导入成功！用时:" + (float) (endTime - startTime) / 1000 + "s";
        System.out.println(tip);
        return tip;
    }

    /**
     * 插入机构数据
     *
     * @param orgUnit
     * @param parentId
     */
    private void saveOrgUnit(OrgUnit orgUnit, String parentId) {
        List<OrgUnit> chlidList = orgUnitRepository.findByIdParentorgunit(orgUnit.getIdOrgunit());
        Tb_right_organ organ;
        for (OrgUnit org : chlidList) {
            if (org.getIdParentorgunit() != null) {
                if (org.getIdParentorgunit().equals(orgUnit.getIdOrgunit())) {
                    organ = new Tb_right_organ();
                    organ.setSystemid("402789f55d54e087015d54e0cfca0000");
                    organ.setServicesid("402789f55d54dc21015d54dccadd0000");
                    organ.setSystemname("OA");
                    organ.setServicesname("");
                    organ.setOrgantype("");
                    //organ.setOrganlevel(Integer.parseInt(1 + ""));
                    organ.setUsestatus(1 + "");
                    organ.setParentid(parentId);
                    organ.setOrganname(org.getName());
                    organ.setRefid(org.getCode());
                    organ.setSortsequence(Integer.parseInt(org.getSort()));
                    organ = rightOrganRepository.save(organ);

                    saveOrgUnit(org, organ.getOrganid());//回调
                }
            }
        }
    }

    /**
     * OA用户表数据导入
     * 需求：二级机构用户并入一级机构中
     */
    public String importOAUser() {
        if (!oaUserOpened.trim().equals("true")) {
            return "请先在配置文件中打开对应功能开关！";
        }
        long startTime = System.currentTimeMillis();

        String firstClass = "各分支行";
        //除了（branch的下3级的用户）
        List<OAUser> oaUserOther = oaUserRepository.getOAUserByParentOther(firstClass);//提前查询，防止异常

        String[] exceptUsers = {"aqbm", "aqsj", "xitong"};
        List<Tb_user> userList = userRepository.findByLoginnameIn(exceptUsers);//删除所有用户(三个管理员除外)
        String[] ids = GainField.getFieldValues(userList, "userid").length == 0 ?
                new String[]{""} : GainField.getFieldValues(userList, "userid");
        userFunctionRepository.deleteAllByUseridNotIn(ids);//级联删除相关数据
        userRoleRepository.deleteAllByUseridNotIn(ids);
        userGroupRepository.deleteAllByUseridNotIn(ids);
        userDataNodeRepository.deleteAllByUseridNotIn(ids);
        userNodeRepository.deleteAllByUseridNotIn(ids);
        userOrganRepository.deleteAllByUseridNotIn(ids);
        personalizedRepository.deleteAllByUseridNotIn(ids);
        userRepository.deleteAllByUseridNotIn(ids);

        List<Tb_right_organ> rightOrganList = rightOrganRepository.findAll();
        Map<String, String> refidMap = new HashMap<>();
        for (Tb_right_organ rightOrgan : rightOrganList) {
            if (rightOrgan.getRefid() != null && !rightOrgan.getRefid().equals("")) {
                refidMap.put(rightOrgan.getRefid(), rightOrgan.getOrganid());//优化导入速率
            }
        }

        List<OrgUnit> orgUnitList = orgUnitRepository.findAll();
        Map<String, String> codeMap = new HashMap<>();
        for (OrgUnit orgUnit : orgUnitList) {
            codeMap.put(orgUnit.getIdOrgunit(), orgUnit.getCode());//优化导入速率
        }

        List<Tb_user> saveUserList = new ArrayList<>();
        Tb_user user;
        for (OAUser oaUser : oaUserOther) {
            String unitCode = codeMap.get(oaUser.getIdOrgunit());
            if (unitCode == null) {//临时待改
                continue;//临时待改
            }//临时待改

            user = new Tb_user();
            String oid = refidMap.get(unitCode);
            if (oid == null) {
                oid = refidMap.get("OU=qt/O=0000001");
                user.setServicesname("UserId:" + oaUser.getIdUser() + " Code:" + oaUser.getIdOrgunit());
            }
            user.setOrganid(oid);
            user.setLoginname(oaUser.getLoginname());
            user.setLoginpassword(MD5.MD5("555"));
            user.setPhone(oaUser.getPhone());
            user.setRealname(oaUser.getName());
            user.setSex(oaUser.getSex());
            user.setStatus(1L);
            user.setUsertype(0 + "");//临时待改
            user.setSortsequence(Integer.valueOf(oaUser.getSort()));
            user.setCreatetime(new Date());
            saveUserList.add(user);
        }

        //branch的下3级的用户
        List<OAUser> oaUserList = oaUserRepository.getOAUserByParent(firstClass);
        for (OAUser oaUser : oaUserList) {
            user = new Tb_user();
            String unitCode = codeMap.get(oaUser.getIdOrgunit());
            String oid = refidMap.get(unitCode);
            if (oid == null) {
                oid = refidMap.get("OU=qt/O=0000001");
                user.setServicesname("UserId:" + oaUser.getIdUser() + " Code:" + oaUser.getIdOrgunit());
            }
            user.setOrganid(oid);
            user.setLoginname(oaUser.getLoginname());
            user.setLoginpassword(MD5.MD5("555"));
            user.setPhone(oaUser.getPhone());
            user.setRealname(oaUser.getName());
            user.setSex(oaUser.getSex());
            user.setStatus(1L);
            user.setUsertype(0 + "");//临时待改
            user.setSortsequence(Integer.valueOf(oaUser.getSort()));
            user.setCreatetime(new Date());
            saveUserList.add(user);
        }
        userRepository.save(saveUserList);

        long endTime = System.currentTimeMillis();
        String tip = "OA用户导入用时：" + (float) (endTime - startTime) / 1000 + "s";
        System.out.println(tip);
        return tip;
    }

    /**
     * 固化程序
     *
     * @return
     */
    @Scheduled(cron = "${task.solidify.cron}")
    public String solidifyFile() {
        if (solidifyOpened != null && !solidifyOpened.trim().equals("true")) {
            System.out.println("固化定时任务开关未开启！");
            return "";
        }
        System.out.println(">>>>>>> 固化定时任务开始！");
        int stopHour = Integer.parseInt(solidifyStopTime);//停止时间：h

        List<Tb_electronic> init = new ArrayList<>();
        List<String> failList = new ArrayList<>();
        SolidifyByEleidThread thread = new SolidifyByEleidThread(init, failList, "management");
        try {
            while (true) {
                if (thread.isAlive()) {
                    Thread.sleep(10000);//等10秒 再检查
                } else {
                    int curHour = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));//当前时间：h
                    if (stopHour <= curHour) {
                        break;//固化结束
                    } else {
                        List<Tb_electronic> elelist = electronicService.getNotSolidified(0, 10, failList);
                        if (elelist.size() > 0) {
                            thread = new SolidifyByEleidThread(elelist, failList, "management");//开启固化线程
                            thread.start();
                        } else {//数据管理的文件固化完成后，再固化数据采集的
                            List<Tb_electronic_capture> list_capture = electronicService.getNotSolidifiedCapture(0, 10, failList);
                            if (list_capture.size() > 0) {
                                thread = new SolidifyByEleidThread(list_capture, failList, "capture");//开启固化线程
                                thread.start();
                            } else {
                                break;//固化结束
                            }
                        }
                        Thread.sleep(10000);//等10秒 预留一点固化时间
                    }
                }
            }
            System.out.println(">>>>>>> 固化定时任务结束！");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * FTP 定时下载数据包 导入
     * @return
     */
    @Scheduled(cron = "${task.ftp.cron}")
    public String scanFtp() {
        boolean isOk = true;
        if (ftpHost == null || ftpHost.equals("")) {
            isOk = false;
            System.out.println("FTP连接ip为空！");
        }
        if (ftpUserName == null || ftpUserName.equals("")) {
            isOk = false;
            System.out.println("FTP连接配置用户名参数为空！");
        }
        if (ftpPassword == null || ftpPassword.equals("")) {
            isOk = false;
            System.out.println("FTP连接配置密码参数为空！");
        }
        if (filepath == null || filepath.equals("")) {
            isOk = false;
            System.out.println("FTP文件存放物理路径为空！");
        }
        if (ftpPort == null || ftpPort.equals("")) {
            isOk = false;
            System.out.println("FTP端口号为空！");
        }
        if(ftpOpen==null||"false".equals(ftpOpen)){
            isOk = false;
        }
        String encodingStr = "";
        if (ftpFormat == null || "".equals(ftpFormat)) {
            encodingStr = "UTF-8";
        } else {
            encodingStr = ftpFormat;
        }
        if (isOk) {//参数满足
            FTPClient ftpClient = null;
            try {
                //1.创建FTP连接
                ftpClient = FtpUtil.getFTPClient(ftpHost, ftpUserName, ftpPassword, Integer.parseInt(ftpPort));
//                ftpClient.setBufferSize(3*1024*1024);//设置ftp的缓存大小-12M
                //2.进行数据包下载
                List<String> fileList = ftpservice.FileList(ftpClient, ftpPath, rootpath + File.separator + "OAFile" + File.separator + "OA接收",
                        rootpath + File.separator + "OAFile" + File.separator + "oa解压目录",saveFileName,ftpFileManage,encodingStr);
                if(fileList==null||fileList.size()==0) {
                    //4.重复读取-防止 第3步没有执行完 导致文件夹没有接收
                    File[] files = new File(rootpath + File.separator + "OAFile" + File.separator + "oa解压目录").listFiles();
                    if (null != files) {
//                        addLog("--if--获取到解压目录数:"+files.length);
                        for (int i = 0; i < files.length; i++) {
                            ftpservice.importFtp(files[i].listFiles(), files[i].getPath(),files[i].getName());
                            FileUtil.delFolder(files[i].getPath());
                        }
                    }
                }else {
                    //3.解析数据包 并插入数据库
//                    addLog("--else--获取到解压目录数:"+fileList.size());
                    for (String s : fileList) {
                        File[] files = new File(s).listFiles();
                        if(null!=files&&files.length>0) {
                            ftpservice.importFtp(files, s,new File(s).getName());
                            addLog("--else----end");
                            FileUtil.delFolder(s);
                        }
                    }
                }
            } catch (Exception e) {
                FtpUtil.addErrorLog("--1023--"+e);
                PrintStream stream = null;
                try{
                    stream = new PrintStream(new File(rootpath+
                            File.separator+"OAFile"+File.separator+"OA接收" + File.separator + "error_log.txt"));
                    e.printStackTrace(stream);
                }catch (Exception e1){
                    e1.printStackTrace(stream);
                }finally {
                    //关闭输出流
                    if(stream!=null) {
                        stream.flush();
                        stream.close();
                    }
                }

            } finally {
                try {
                    //注销FTP连接
                    if(ftpClient!=null){
                        ftpClient.logout();
                    }
                } catch (IOException e) {
                    FtpUtil.addErrorLog("--1031--"+e);
                }
            }
            System.out.println(">>>>>FTP定时检查执行完成！");
        } else {
            //System.out.println(">>>>>参数异常，无法创建FTP链接");
        }
        return "";
    }



    public static void addLog(String addfilename) {
        String dir = ConfigValue.getPath("system.document.rootpath");//E:/document
        String filenmae = dir + File.separator +"OAFile"+File.separator+ "OA接收" + File.separator + "OALog.txt";
        File file = new File(filenmae);
        FileWriter fileWriter = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file, true);

            fileWriter.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+addfilename+ "\r\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 每天20点30分分(20:30)执行，采集管理的数据导入的元数据采集
     *
     * @throws IOException
     */
    @Scheduled(cron = "0 30 20 ? * *")
    public void addCaptureMetadataLog() throws IOException {
        logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "------插入 采集导入的元数据 定时任务准备执行------");
        captureMetadataService.readCaptureMetadataTxt();
    }
}

