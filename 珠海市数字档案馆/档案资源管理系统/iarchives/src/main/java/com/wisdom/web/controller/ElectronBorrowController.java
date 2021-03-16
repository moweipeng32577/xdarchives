package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wisdom.secondaryDataSource.entity.Tb_entry_index_sx;
import com.wisdom.secondaryDataSource.repository.SecondaryEntryIndexRepository;
import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.ElectronApproveService;
import com.wisdom.web.service.ElectronBorrowService;
import com.xdtech.smsclient.SMSService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 电子查档控制器
 * Created by Administrator on 2017/10/30 0030.
 */
@Controller
@RequestMapping(value = "/electron")
public class ElectronBorrowController {

    @Autowired
    ElectronBorrowService electronBorrowService;
    
    @Autowired
    ElectronApproveService electronApproveService;
    
    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    FileNoneRepository fileNoneRepository;

    @Autowired
    BorrowDocRepository borrowDocRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SMSService smsService;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Autowired
    RightOrganRepository rightOrganRepository;

    @Autowired
    StBoxRepository stBoxRepository;

    @Autowired
    SecondaryEntryIndexRepository secondaryEntryIndexRepository;

    @Value("${system.loginType}")
    private String systemType;//政务网1  局域网0

    @RequestMapping("/main")
    public String index() {
        return "/inlet/borrow";
    }

    //解决利用平台与管理平台公用页面权限控制问题
    @RequestMapping("/mainly")
    public String indexly() {
        return "/inlet/borrow";
    }

    @RequestMapping("/getEntryIndex")
    @ResponseBody
    public Page<Tb_entry_index> getEntryIndex(String dataids,int page,int limit) {
        return electronBorrowService.getEntryIndex(dataids,page,limit,"");
    }

    @RequestMapping("/getTreeEntryIndex")
    @ResponseBody
    public Page<Tb_entry_index> getTreeEntryIndex(String nodeid, int page, int start, int limit) {
        return electronBorrowService.getTreeEntryIndex(nodeid,(page-1),limit);
    }

    @RequestMapping("/getBoxEntryIndex")
    @ResponseBody
    public void getBoxEntryIndex(int page,int limit, String borrowType,String isFlag ,HttpServletResponse httpServletResponse) {
        Page<Tb_entry_index> result = electronBorrowService.getBoxEntryIndex(page,limit,borrowType,isFlag);
        //不使用框架自带的json转换，避免循环引用
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("utf-8");
        String json = JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
        try {
            httpServletResponse.getWriter().write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 利用平台-查档申请时判断提交的条目是否属于同一单位
     * @param organids
     * @param flagtype 平台标志
     * @return
     */
    @RequestMapping("/isSameOrgan")
    @ResponseBody
    public ExtMsg isSameOrgan(String organids,String flagtype) {
        if("1".equals(flagtype)){//利用平台
            String[] organid = organids.split(",");
            List<Tb_right_organ> organlist = rightOrganRepository.findByOrganid(organid);
            //查找当前条目所属单位，需要往上找，当前机构可能为部门
            Set<String> sets = new HashSet<>();
            for(int i=0;i<organlist.size();i++){
                Tb_right_organ organ = organlist.get(i);
                while (organ.getOrgantype() != null && organ.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {
                    organ = rightOrganRepository.findOne(organ.getParentid());
                }
                sets.add(organ.getOrganid());
            }
            boolean flag = true;
            if(sets.size()>1){
                flag = false;
            }
            return new ExtMsg(flag,null,null);
        }
        return new ExtMsg(true,null,null);

//        String[] nodeid = nodeids.split(",");
//        List<Tb_data_node> list = dataNodeRepository.findByNodeidIn(nodeid);
////        //去重条目所属nodeid
////        List<String> nodeidlist = Arrays.asList(nodeid).stream().distinct().collect(Collectors.toList());
//        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
//        //当前登录者所属单位
//        String organid =  userRepository.findOrganidByUserid(userDetails.getUserid()).trim();
//        boolean flag = true;
//        for(int i=0;i<list.size();i++){
//            if(!organid.equals(list.get(i).getOrganid().trim())){
//                flag = false;
//            }
//        }
//        return new ExtMsg(flag,null,null);
    }

    /**
     * 获取单据数据,自动填充表单默认项
     * @param dataids 选择数据id
     * @return
     */
    @RequestMapping("/getBorrowDocByIds")
    @ResponseBody
    public ExtMsg getBorrowDocByIds(String dataids,String isFlag) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        String uid = new String();
        String nowdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Tb_borrowdoc borrowdoc = new Tb_borrowdoc();
        borrowdoc.setBorrowman(userDetails.getRealname());//获取用户姓名
        borrowdoc.setBorroworgan(userDetails.getOrganid());
        borrowdoc.setBorrowmantel(userDetails.getPhone());
        borrowdoc.setBorrowts(3);//设置默认查档3天
        borrowdoc.setId(dataids);
        borrowdoc.setBorrowmantime("1");
        borrowdoc.setBorrowdate(nowdate);
        borrowdoc.setAcceptdate(nowdate);
        String[] entrys = dataids.split(",");
        List<Tb_entry_index> entry_indexList=new ArrayList<>();
        if("1".equals(isFlag)) {
            List<Tb_entry_index_sx> entry_index_sx = secondaryEntryIndexRepository.findByEntryidIn(entrys);
            if(entry_index_sx.size()>0){
                for (Tb_entry_index_sx entryIndexSx : entry_index_sx) {
                    Tb_entry_index entryIndex=new Tb_entry_index();
                    BeanUtils.copyProperties(entryIndexSx,entryIndex);
                    entry_indexList.add(entryIndex);
                }
            }
        }else {
            entry_indexList = electronBorrowService.getBorrowEntry(entrys);
        }
        String archivecodes = null;
        for(int i=0;i<entry_indexList.size();i++){
            Tb_entry_index index = entry_indexList.get(i);
            if(i==0){
                archivecodes = index.getArchivecode();
            }else {
                archivecodes = archivecodes + "，" + index.getArchivecode();
            }
        }
        archivecodes = "档号："+archivecodes;
        borrowdoc.setDesci(archivecodes);
        if(userDetails.getOrganid().equals("外来人员部门")){
            Tb_user user =  userRepository.findByLoginname(userDetails.getLoginname());
            borrowdoc.setBorrowmantel(user.getPhone());
            borrowdoc.setComaddress(user.getAddress());
          borrowdoc.setCertificatenumber(userDetails.getLoginname());
        }
        return new ExtMsg(true,"成功",borrowdoc);
    }

    /**
     * 电子查档表单提交
     * @param borrowdoc 表单实例
     * @return
     */
    @RequestMapping("/electronBill")
    @ResponseBody
    public ExtMsg electronBill(Tb_borrowdoc borrowdoc,String[] eleids,String sendMsg,String dataSourceType) {
        String spmanid = borrowdoc.getBorrowcode();
        Tb_borrowdoc borrowdoc1= electronBorrowService.addBorrow(borrowdoc, eleids,dataSourceType);
        if(borrowdoc1 != null){
            Tb_user user = userRepository.findByUserid(spmanid);
            webSocketService.noticeRefresh();
            String returnStr = "";
            if(sendMsg!=null&&"true".equals(sendMsg)&&user!=null){   //短信提醒
                try {
                    returnStr = smsService.SendSMS(user.getPhone(),"您有一条档案系统的待办审批，请登录档案系统管理平台及时处理！");
                }catch (Exception e){
                    e.printStackTrace();
                    return new ExtMsg(true,"单据提交成功，短信发送失败",null);
                }
            }
            if("".equals(returnStr)){
                return new ExtMsg(true,"单据提交成功",null);
            }else{
                return new ExtMsg(true,"单据提交成功，短信发送结果为："+returnStr,null);
            }
        }
        return new ExtMsg(false,"单据提交失败",null);
    }

    @RequestMapping("/getJypurpose")
    @ResponseBody
    public List<Tb_system_config> getJypurpose(String type) {
        List<Tb_system_config> returnList = new ArrayList<>();
        if("0".equals(type)){//js渲染时先不加载数据
            return returnList;
        }
        return electronBorrowService.getJypurpose();
    }

    @RequestMapping("/getApproveMan")
    @ResponseBody
    public List<Tb_user> getApproveMan(String worktext,boolean istemporary,String organids,String findOrganid) {
        return electronBorrowService.getApproveMan(worktext,istemporary,organids,findOrganid);
    }

    @RequestMapping("/setStJyBox")
    @ResponseBody
    public ExtMsg setStJyBox(String[] dataids, String borrowType){
        return new ExtMsg(true,electronBorrowService.setStJyBox(dataids,borrowType),null);
    }

    @RequestMapping("/checkKccount")
    @ResponseBody
    public ExtMsg checkKccount(String[] dataids){
        return new ExtMsg(electronBorrowService.checkKccount(dataids),"",null);
    }

    @RequestMapping("/stAddFormBill")
    @ResponseBody
    public ExtMsg stAddFormBill(Tb_borrowdoc borrowdoc,String[] eleids,String sendMsg) {
        String spmanid = borrowdoc.getBorrowcode();
        electronBorrowService.addStBorrow(borrowdoc,eleids);
        webSocketService.noticeRefresh();// 刷新通知
        Tb_user spuser = userRepository.findByUserid(spmanid);
        String returnStr = "";
        if(sendMsg!=null&&"true".equals(sendMsg)&&spuser!=null){   //短信提醒
            try {
                returnStr = smsService.SendSMS(spuser.getPhone(),"您有一条档案系统的待办审批，请登录档案系统管理平台及时处理！");
            }catch (Exception e){
                e.printStackTrace();
                return new ExtMsg(true,"单据提交成功，短信发送失败",null);
            }
        }
        if("".equals(returnStr)){
            return new ExtMsg(true,"单据提交成功",null);
        }else{
            return new ExtMsg(true,"单据提交成功，短信发送结果为："+returnStr,null);
        }
    }

    /**
     * 续借
     * @param ids 数据id数组
     * @param ts 续借天数
     * @param xjapprove 续借描述
     * @return
     */
    @RequestMapping("/stXjAddFormBill")
    @ResponseBody
    public ExtMsg stXjAddFormBill(String[] ids,int ts,String xjapprove,boolean flag) {
        try {
            int result = electronBorrowService.addStXjBorrow(ids,ts,xjapprove,flag);
            if(result>0){
                return new ExtMsg(true,"成功",result);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ExtMsg(false,"失败",null);

    }

    @RequestMapping("/getBorrowDocLyTable")
    @ResponseBody
    public IndexMsg getBorrowDoc(int page,int limit) {
        Page<Tb_borrowdoc> pages = electronBorrowService.getBorrowDoc(page,limit);
        List<Tb_borrowdoc> tb_borrowdocs = pages.getContent();
        return new IndexMsg(true,"0","成功",tb_borrowdocs);
    }

    @RequestMapping("/deleteBorrowbox")
    @ResponseBody
    public void deleteBorrowbox(String[] ids,String borrowType) {
        electronBorrowService.deleteBorrowbox(ids,borrowType);
    }

    /**
     * 获取单据数据,自动填充表单默认项
     *
     * @return
     */
    @RequestMapping("/getBorrowDocByUser")
    @ResponseBody
    public ExtMsg getBorrowDocByUser(String realname) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String nowdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Tb_borrowdoc borrowdoc = new Tb_borrowdoc();
        borrowdoc.setBorrowman(userDetails.getRealname());// 获取用户姓名
        borrowdoc.setBorroworgan(userDetails.getOrganid());
        borrowdoc.setBorrowmantel(userDetails.getPhone());
        borrowdoc.setBorrowts(3);// 设置默认查档3天
        borrowdoc.setBorrowmantime("1");
        borrowdoc.setBorrowdate(nowdate);
        borrowdoc.setAcceptdate(nowdate);
        if(realname!=null&&!"".equals(realname)){
            borrowdoc.setBorrowman(realname);
        }
        if(userDetails.getOrganid().equals("外来人员部门")){
            borrowdoc.setCertificatenumber(userDetails.getLoginname());
            Tb_user user =  userRepository.findByLoginname(userDetails.getLoginname());
            borrowdoc.setBorrowmantel(user.getPhone());
            borrowdoc.setComaddress(user.getAddress());
        }
        return new ExtMsg(true, "成功", borrowdoc);
    }

    @RequestMapping("/stLookAddForm")
    @ResponseBody
    public ExtMsg stLookAddForm(Tb_borrowdoc borrowdoc,Tb_user user, String[] eleids,String userid,String sendMsg,String dataSourceType) {
        String spmanid = borrowdoc.getBorrowcode();
        electronBorrowService.AddStLookBorrow(borrowdoc,user,eleids,userid,dataSourceType);
        webSocketService.noticeRefresh();//刷新通知
        Tb_user spuser = userRepository.findByUserid(spmanid);
        String returnStr = "";
        if(sendMsg!=null&&"true".equals(sendMsg)&&spuser!=null){   //短信提醒
            try {
                returnStr = smsService.SendSMS(spuser.getPhone(),"您有一条档案系统的待办审批，请登录档案系统管理平台及时处理！");
            }catch (Exception e){
                e.printStackTrace();
                return new ExtMsg(true,"单据提交成功，短信发送失败",null);
            }
        }
        if("".equals(returnStr)){
            return new ExtMsg(true,"单据提交成功",null);
        }else{
            return new ExtMsg(true,"单据提交成功，短信发送结果为："+returnStr,null);
        }
    }

    @RequestMapping("/electronAddForm")
    @ResponseBody
    public ExtMsg electronAddForm(Tb_borrowdoc borrowdoc,Tb_user user,String[] eleids,String userid,String sendMsg,String nation,String submittype,String dataSourceType) {
        String spmanid = borrowdoc.getBorrowcode();
        if("身份证".equals(borrowdoc.getCertificatetype())&&!"true".equals(submittype)){
            user.setBirthday(borrowdoc.getCertificatenumber().substring(6,14));
        }
        user.setEthnic(nation);
        user.setPhone(borrowdoc.getBorrowmantel());
        Tb_borrowdoc borrowdoc1 = electronBorrowService.AddElectronBorrow(borrowdoc,user,eleids,userid,dataSourceType);
        if(borrowdoc1!=null){
            Tb_user spuser = userRepository.findByUserid(spmanid);
            String returnStr = "";
            webSocketService.noticeRefresh();//刷新通知
            if(sendMsg!=null&&"true".equals(sendMsg)&&spuser!=null){   //短信提醒
                try {
                    returnStr = smsService.SendSMS(spuser.getPhone(),"您有一条档案系统的待办审批，请登录档案系统管理平台及时处理！");
                }catch (Exception e){
                    e.printStackTrace();
                    return new ExtMsg(true,"单据提交成功，短信发送失败",null);
                }
            }
            if("".equals(returnStr)){
                return new ExtMsg(true,"单据提交成功",null);
            }else{
                return new ExtMsg(true,"单据提交成功，短信发送结果为："+returnStr,null);
            }
        }
        return new ExtMsg(false, "单据提交失败", null);
    }

    @RequestMapping("/deleteEvidence")
    @ResponseBody
    public ExtMsg deleteEvidence(String[] eleids) {
        electronBorrowService.deleEvicence(eleids);
        return new ExtMsg(true, "成功", null);
    }

    @RequestMapping("/setPrintBox")
    @ResponseBody
    public ExtMsg setPrintBox(String[] dataids, String borrowType){
        return new ExtMsg(true,electronBorrowService.setPrintBox(dataids,borrowType),null);
    }

    /**
     * 电子打印表单提交
     * @param borrowdoc 表单实例
     * @return
     */
    @RequestMapping("/electronPrintSubmit")
    @ResponseBody
    public ExtMsg electronPrintSubmit(Tb_borrowdoc borrowdoc,String[] eleids,String sendMsg) {
        String spmanid = borrowdoc.getBorrowcode();
        Tb_borrowdoc borrowdoc1 = electronBorrowService.electronPrintSubmit(borrowdoc,eleids);
        if(borrowdoc1 != null){
            Tb_user spuser = userRepository.findByUserid(spmanid);
            webSocketService.noticeRefresh();//刷新通知
            String returnStr = "";
            if(sendMsg!=null&&"true".equals(sendMsg)&&spuser!=null){   //短信提醒
                try {
                    returnStr = smsService.SendSMS(spuser.getPhone(),"您有一条档案系统的待办审批，请登录档案系统管理平台及时处理！");
                }catch (Exception e){
                    e.printStackTrace();
                    return new ExtMsg(true,"单据提交成功，短信发送失败",null);
                }
            }
            if("".equals(returnStr)){
                return new ExtMsg(true,"单据提交成功",null);
            }else{
                return new ExtMsg(true,"单据提交成功，短信发送结果为："+returnStr,null);
            }
        }
        return new ExtMsg(false,"单据提交失败",null);
    }

    /**
     * 获取单据数据,自动填充表单默认项
     * @param dataids 选择数据id
     * @return
     */
    @RequestMapping("/getPrintBorrowDocByIds")
    @ResponseBody
    public ExtMsg getPrintBorrowDocByIds(String dataids) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        String nowdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Tb_borrowdoc borrowdoc = new Tb_borrowdoc();
        borrowdoc.setBorrowman(userDetails.getRealname());//获取用户姓名
        borrowdoc.setBorroworgan(userDetails.getOrganid());
        borrowdoc.setBorrowmantel(userDetails.getPhone());
        borrowdoc.setBorrowts(3);//设置默认查档3天
        borrowdoc.setId(dataids);
        borrowdoc.setBorrowmantime("1");
        borrowdoc.setBorrowdate(nowdate);
        String[] entrys = dataids.split(",");
        List<Tb_entry_index> entry_indexList = electronBorrowService.getBorrowEntry(entrys);
        String archivecodes = null;
        for(int i=0;i<entry_indexList.size();i++){
            Tb_entry_index index = entry_indexList.get(i);
            if(i==0){
                archivecodes = index.getArchivecode();
            }else {
                archivecodes = archivecodes + "，" + index.getArchivecode();
            }
        }
        archivecodes = "档号："+archivecodes;
        borrowdoc.setDesci(archivecodes);
        List<Tb_electronic_print> electronic_prints = electronBorrowService.getElectronPrint(entrys);
        int copiesCount = 0;
        for(Tb_electronic_print electronicPrint : electronic_prints){
            copiesCount +=electronicPrint.getCopies();
        }
        borrowdoc.setCopies(copiesCount);
        if(userDetails.getOrganid().equals("外来人员部门")){
            Tb_user user =  userRepository.findByLoginname(userDetails.getLoginname());
            borrowdoc.setBorrowmantel(user.getPhone());
            borrowdoc.setComaddress(user.getAddress());
            borrowdoc.setCertificatenumber(userDetails.getLoginname());
        }
        return new ExtMsg(true,"成功",borrowdoc);
    }
    /**
     * 获取查无此档单据数据
     * @param docid 选择查档单据docid
     * @return
     */
    @RequestMapping("/getfileNoneData")
    @ResponseBody
    public ExtMsg getfileNoneData(String docid) {
        String state = "初始登记";
        Tb_filenone tb_filenone = new Tb_filenone();
        Tb_filenone filenoneBill= fileNoneRepository.findByDocid(docid);
        if(filenoneBill != null){
            state = "已登记";
            tb_filenone = filenoneBill;
        }
        else{
            Tb_borrowdoc borrowdoc = borrowDocRepository.findByDocid(docid);
            tb_filenone.setDocid(borrowdoc.getId()); //查档单ID
            tb_filenone.setPersonname(borrowdoc.getBorrowman());//查档人
            tb_filenone.setOrganname(borrowdoc.getBorroworgan());//单位名称

            Date d = new Date();
            // 这里也个用SimpleDateFormat的format（）进行格式化，然后以字符串形式返回格式化后的date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String date = dateFormat.format(d).toString();
            tb_filenone.setTime(date);//默认当前时间
            String fileOrder =  fileNoneRepository.findOrder(date);
            if (fileOrder != null) {
               tb_filenone.setFilenum( date + String.format("%04d",Integer.parseInt(fileOrder) + 1) ); //获取当前日期最大顺序号
            }
            else {
                tb_filenone.setFilenum(date+"0001");
            }
        }
        return new ExtMsg(true,state,tb_filenone);
    }
    /**
     * 保存/修改查无此档单据数据
     * @param tb_filenone 表单数据
     * @return
     */
    @RequestMapping("/savafileNoneData")
    @ResponseBody
    public ExtMsg getfileNoneData(Tb_filenone tb_filenone){
        Tb_filenone fileNone =  electronBorrowService.savaFileNone(tb_filenone);
        if(fileNone!=null){
            return new ExtMsg(true,"",fileNone);
        }
        return new ExtMsg(false,"",null);
    }

    /**
     * 新增临时中获取单据数据,自动填充表单默认项
     *
     * @return
     */
    @RequestMapping("/getBorrowDocByTemporaryUser")
    @ResponseBody
    public ExtMsg getBorrowDocByTemporaryUser(String userid) {
//        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String nowdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
//        Tb_borrowdoc borrowdoc = new Tb_borrowdoc();
        Map<String,Object> map =new HashMap<>();
        Tb_user temuser = userRepository.findByUserid(userid);
        if(temuser!=null) {
            map.put("borrowman",temuser.getRealname());
            map.put("borrowmantel",temuser.getPhone());
            map.put("borrowts",3);
            map.put("borrowmantime",1);
            map.put("borrowdate",nowdate);
            map.put("acceptdate",nowdate);
            map.put("certificatenumber",temuser.getLoginname());
            map.put("borrowmantel",temuser.getPhone());
            map.put("comaddress",temuser.getAddress());
            map.put("sex",temuser.getSex());
            map.put("nation",temuser.getEthnic());
//            borrowdoc.setBorrowman(temuser.getRealname());// 获取用户姓名
//            borrowdoc.setBorrowmantel(temuser.getPhone());
//            borrowdoc.setBorrowts(3);// 设置默认查档3天
//            borrowdoc.setBorrowmantime("1");
//            borrowdoc.setBorrowdate(nowdate);
//            borrowdoc.setAcceptdate(nowdate);
//            borrowdoc.setCertificatenumber(temuser.getLoginname());
//            borrowdoc.setBorrowmantel(temuser.getPhone());
//            borrowdoc.setComaddress(temuser.getAddress());
        }
        return new ExtMsg(true, "成功", map);
    }

    //设置查档类型
    @RequestMapping("/setBorrowType")
    @ResponseBody
    public ExtMsg setBorrowType(String[] entryids, String borrowType,String settype,String isFlag){
        String msg = electronBorrowService.setBorrowType(entryids,borrowType,settype,isFlag);
        return new ExtMsg(true,msg,null);
    }

    @RequestMapping("/getBorrowEntryIndex")
    @ResponseBody
    public Page<Tb_entry_index> getBorrowEntryIndex(String dataids,String borrowtype,String loadtype,int page,int limit,String isFlag) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<Tb_entry_index> entryIndexPage = electronBorrowService.getEntryIndex(dataids,page,limit,isFlag);
        List<Tb_entry_index> entryIndexList = entryIndexPage.getContent();
        List<Tb_entry_index> returnList = new ArrayList<>();
        List<Tb_st_box> saveBox = new ArrayList<>();
        for(Tb_entry_index entryIndex : entryIndexList){
            Tb_entry_index copyEntry = new Tb_entry_index();
            BeanUtils.copyProperties(entryIndex,copyEntry);
            List<Tb_st_box> st_boxs = stBoxRepository.findByUseridAndEntryidAndBorrowtype(userDetails.getUserid(),entryIndex.getEntryid(),borrowtype);
            if(st_boxs.size()>0){
                if(loadtype!=null&&"firstload".equals(loadtype)){  //初始默认设置
                    if((copyEntry.getEleid()!=null&&!"".equals(copyEntry.getEleid()))||"1".equals(isFlag)) {
                        copyEntry.setFlagopen("电子查档");  //设置查档类型
                        st_boxs.get(0).setSettype("电子查档");
                    }else{
                        copyEntry.setFlagopen("实体查档");  //设置查档类型
                        st_boxs.get(0).setSettype("实体查档");
                    }
                    saveBox.add(st_boxs.get(0));
                }else{
                    copyEntry.setFlagopen(st_boxs.get(0).getSettype());  //设置查档类型
                }
            }
            returnList.add(copyEntry);
        }
        if(saveBox.size()>0){
            electronBorrowService.saveBorrowType(saveBox);  //保存默认查档类型设置
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        return new PageImpl<Tb_entry_index>(returnList,pageRequest,entryIndexPage.getTotalElements());
    }

    //根据id获取单据
    @RequestMapping("/getBorrowdocById")
    @ResponseBody
    public ExtMsg getBorrowdocById(String borrowdocid) {
        Tb_borrowdoc borrowdoc = borrowDocRepository.findByDocid(borrowdocid);
        if(borrowdoc!=null){
            return new ExtMsg(true,"",borrowdoc);
        }else {
            return new ExtMsg(false,"",null);
        }
    }

    //获取所有单位机构
    @RequestMapping("/getUnitOrganAll")
    @ResponseBody
    public List<Tb_right_organ> getUnitOrganAll(String type,String taskid,String worktext,String nodeid,String approveType) {
        return electronBorrowService.getUnitOrganAll(type,taskid,worktext,nodeid,approveType);
    }

    @RequestMapping("getWorkTextNode")
    @ResponseBody
    public List<Tb_node> getWorkTextNode(String workText){
        return electronBorrowService.getWorkTextNode(workText);
    }
}
