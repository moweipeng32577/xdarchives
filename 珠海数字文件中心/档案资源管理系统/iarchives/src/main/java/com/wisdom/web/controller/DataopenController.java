package com.wisdom.web.controller;

import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.DBCompatible;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.MissionUserRepository;
import com.wisdom.web.repository.OpendocRepository;
import com.wisdom.web.repository.UserRepository;
import com.wisdom.web.repository.WorkRepository;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.*;
import com.xdtech.smsclient.SMSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

/**
 * 数据开放控制器
 * Created by tanly on 2017/12/1 0001.
 */
@Controller
@RequestMapping(value = "/dataopen")
public class DataopenController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    DataopenService dataopenService;

    @Autowired
    ElectronApproveService electronApproveService;

    @Autowired
    AcquisitionService acquisitionService;
    
    @Autowired
    OpendocRepository opendocRepository;
    
    @Autowired
    MissionUserRepository missionUserRepository;
    
    @Autowired
    WorkflowService workflowService;
    
    @Autowired
    WebSocketService webSocketService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SMSService smsService;

    @Autowired
    ExportExcelService exportExcelService;

    @Autowired
    TaskService taskService;

    @Autowired
    WorkRepository workRepository;

    @Value("${system.report.server}")
    private String reportServer;//报表服务

    @Value("${workflow.open.approve.workid}")
    private String openWorkId;//开放审批节点编号

    @RequestMapping("/main")
	public String dataopen(Model model) {
		String state = "";
		try {
			// 将属性文件流装载到Properties对象中
			Properties prop = new Properties();// 属性集合对象
			String path = DataopenController.class.getClassLoader().getResource("application.properties").getPath();
            path = java.net.URLDecoder.decode(path, "utf-8");
            // 属性文件输入流
            FileInputStream fis = new FileInputStream(path);
			prop.load(fis);
			fis.close();// 关闭流
			state = prop.getProperty("open.send.data");
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		model.addAttribute("state", state);
        model.addAttribute("reportServer",reportServer);
        Tb_work workOpen = workRepository.findByWorktext("开放审批");
        if(workOpen!=null){
            model.addAttribute("openSendmsg","1".equals(workOpen.getSendmsgstate())?true:false);  //开放是否短信通知
        }else{
            model.addAttribute("openSendmsg",false);
        }
		return "/inlet/dataOpen";
	}

    @RequestMapping("/addtobox")
    @ResponseBody
    public ExtMsg addtobox(String[] dataids){
        return new ExtMsg(true,dataopenService.addtobox(dataids),null);
    }

    @RequestMapping("/dontopen")
    @ResponseBody
    public ExtMsg dontopen(String[] dataids){
        dataopenService.dontopen(dataids);
        return new ExtMsg(true,"操作成功",null);
    }

    @RequestMapping("/cancelban")
    @ResponseBody
    public ExtMsg cancelban(String[] dataids){
        dataopenService.cancelban(dataids);
        return new ExtMsg(true,"操作成功",null);
    }

    @RequestMapping("/getBoxEntryIndex")
    @ResponseBody
    public Page<Tb_entry_index> getBoxEntryIndex(int page,int limit) {
        return dataopenService.getBoxEntryIndex(page,limit);
    }
    
    /**
     * 通过docid查询单据信息
     * @param docid
     * @return
     */
    @RequestMapping("/getDocumentInfo/{docid}")
    @ResponseBody
    public ExtMsg getDocumentInfo(@PathVariable String docid) {
    	if (docid != null) {
    		Tb_opendoc opendoc = opendocRepository.getDocumentInfo(docid);
    		if (opendoc != null) {
    			return new ExtMsg(true, "success", opendoc);
    		}
    	}
    	return null;
    }

    @RequestMapping("/getOpenDoc")
    @ResponseBody
    public ExtMsg getOpenDoc(String dataids) {
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        Tb_opendoc opendoc = new Tb_opendoc();
        opendoc.setSubmitter(userDetails.getRealname());
        opendoc.setEntrycount(Long.parseLong(dataids.split(",").length+""));
        Calendar cal = Calendar.getInstance();
        String year = String.valueOf(cal.get(Calendar.YEAR));//获取当前年份
        String sql = "select cast(max(batchnum) as "+DBCompatible.getInstance().findIntType()+") from tb_opendoc where " + DBCompatible.getInstance().findExpressionOfRegExp("batchnum") + " and batchnum like concat('"+year+"','%')";
        Query query = entityManager.createNativeQuery(sql);
    	Object result = query.getSingleResult();
    	int maxCalValue = result == null ? 0 : Integer.valueOf(result.toString());
    	if(maxCalValue == 0){
    		opendoc.setBatchnum(year + String.format("%04d", 1));
    	} else {
    		String calValue = String.valueOf(maxCalValue);
    		Integer value = Integer.valueOf(calValue.substring(4,calValue.length()));
            if (value < 9999) {
            	String batchnum = year + String.format("%04d", value+1);
            	opendoc.setBatchnum(batchnum);
            }
    	}
        opendoc.setId(dataids);//将选择的条目id临时存放在opendocID上
        return new ExtMsg(true,"成功",opendoc);
    }
    
    @RequestMapping("/getBatchnum")
    @ResponseBody
    public ExtMsg getBatchnum(String batchnum) {
    	Tb_opendoc doc = opendocRepository.findByBatchnum(batchnum);//判断批次号是否存在
    	if (doc == null) {
    		return new ExtMsg(true,"success",null);
    	}
    	return new ExtMsg(false,"批次号重复",null);
    }

    @RequestMapping("/getNode")
    @ResponseBody
    public List<Tb_node> getNode(String workname) {
        return dataopenService.getNode(workname);
    }

    @RequestMapping("/getNodeuser")
    @ResponseBody
    public List<Tb_user> getNodeuser(String nodeId,String organid) {
        List<Tb_user> userList=new ArrayList<>();
        if(!"".equals(nodeId)&&null!=nodeId)
            userList=electronApproveService.getNextSpman(nodeId,organid,"");
        return userList;
    }
    @RequestMapping("/deleteOpenbox")
    @ResponseBody
    public void deleteOpenbox(String ids) {
        dataopenService.deleteOpenbox(ids.split(","));
    }

    @RequestMapping("/sendformSubmit")
    @ResponseBody
    public ExtMsg sendformSubmit(Tb_opendoc opendoc, String taskname, String nodeid, String nodeuserid,
			String datanodeid,String sendMsg) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		dataopenService.sendformSubmit(opendoc, taskname, nodeid, nodeuserid, datanodeid, userDetails.getUserid());
		// 提交开放审批，通知审批用户信息刷新
        webSocketService.noticeRefresh();//刷新通知
        Tb_user spuser = userRepository.findByUserid(nodeuserid);
        String returnStr = "";
        if(sendMsg!=null&&"true".equals(sendMsg)&&spuser!=null){   //短信提醒
            try {
                returnStr = smsService.SendSMS(spuser.getPhone(),"您有一条档案系统的待办审批，请登录档案系统管理平台及时处理！");
            }catch (Exception e){
                e.printStackTrace();
                return new ExtMsg(true,"送审成功，短信发送失败",null);
            }
        }
        if("".equals(returnStr)){
            return new ExtMsg(true,"送审成功",null);
        }else{
            return new ExtMsg(true,"送审成功，短信发送结果为："+returnStr,null);
        }
	}
    
    @RequestMapping("/submitForm")
	@ResponseBody
	public ExtMsg submitForm(Tb_opendoc opendoc, String nodeid) {
		dataopenService.submitForm(opendoc, nodeid);
		return new ExtMsg(true, "成功", null);
	}

    @RequestMapping(value = "/entriesByOpen", method = RequestMethod.GET)
    @ResponseBody
    public Page<Tb_index_detail> getEntriesByOpen(String nodeid,String opentype,int page,int limit,String condition, String operator, String content, String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        return dataopenService.getEntriesByOpenNew(new String[]{nodeid}, opentype, page, limit, condition, operator, content, sortobj);
    }

    @RequestMapping(value = "/entriesByPower", method = RequestMethod.GET)
    @ResponseBody
    public Page<Tb_index_detail> getEntriesByPower(String nodeid,int page,int limit,String condition, String operator, String content, String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        return dataopenService.getEntriesByPower(new String[]{nodeid}, page, limit, condition, operator, content, sortobj);
    }

    @RequestMapping("/getEntryIndexById")
    @ResponseBody
    public Page<Tb_entry_index> getEntryIndexById(String dataids,int page,int limit) {
        return dataopenService.getEntryIndexById(dataids,page,limit);
    }

    @RequestMapping("/getNodeOpendoc")
	@ResponseBody
	public Page<Tb_opendoc> getNodeOpendoc(String nodeid, String type, int page, int start, int limit, String condition,
			String operator, String content, String sort) {
		logger.info("nodeid:" + nodeid + ";page:" + page + ";start:" + start + ";limt:" + limit);
		Sort sortobj = WebSort.getSortByJson(sort);
		return dataopenService.findOpendocBySearch(type, page, limit, condition, operator, content, nodeid, sortobj);
	}

    @RequestMapping("/docEntry")
    @ResponseBody
    public Page<Tb_entry_index> getDocEntry(String batchnum, int page, int start, int limit, String condition, String
            operator, String content,String sort) {
        logger.info("batchnum:"+batchnum+";page:" + page + ";start:" + start + ";limt:" + limit);
        Sort sortobj = WebSort.getSortByJson(sort);
        String[] entryidData = dataopenService.getEntryidsByBatchnum(batchnum);
        return acquisitionService.findDocEntryindexBySearch(page, limit, condition, operator, content, entryidData,sortobj);
    }

    /**
     * 根据单据获取办理详情
     * @param  opendocid 开放单据id
     * @return
     */
    @RequestMapping("/getDealDetails")
    @ResponseBody
    public Page getDealDetails(String opendocid){
        try {
            return dataopenService.getDealDetails(opendocid);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发布到政务网
     * @param entryids
     * @param nodeid
     * @return
     */
    @RequestMapping("/dataopenRelease")
    @ResponseBody
    public ExtMsg dataopenRelease(String nodeid, String entryids, String currentnode,Tb_datareceive datareceive,HttpServletRequest request) {
        String zipPath=exportExcelService.exportDataopenRelease(nodeid, entryids, currentnode,datareceive);
        request.getSession().setAttribute("downLoadDataOpenRelease",zipPath);
        ExtMsg extMsg = new ExtMsg(true, "发布成功", null);
        return extMsg;
    }

    @RequestMapping("/downLoadDataOpenRelease")
    @ResponseBody
    public void downLoad(HttpServletResponse response, HttpServletRequest request) {
        String zipPath=(String)request.getSession().getAttribute("downLoadDataOpenRelease");
        File html_file = new File(zipPath);
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename="
                    + new String(zipPath.substring(zipPath.lastIndexOf("\\") + 1).getBytes("gbk"), "iso8859-1"));
            response.setContentType("application/zip");
            ServletOutputStream out;
            FileInputStream inputStream = new FileInputStream(html_file);
            out = response.getOutputStream();
            int b = 0;
            byte[] buffer = new byte[1024];
            while ((b = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, b);
            }
            inputStream.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //根据id查询审批节点
    @RequestMapping("/findByWorkId")
    @ResponseBody
    public ExtMsg findByWorkId() {
        Tb_work work= workflowService.findByWorkid(openWorkId);
        return new ExtMsg(true,"",work);
    }

    //手动催办
    @RequestMapping("/manualUrging")
    @ResponseBody
    public ExtMsg manualUrging(String batchnum,String sendMsg){
        if(batchnum==null){
            return new ExtMsg(false, "催办失败", null);
        }
        Tb_flows billApproval= taskService.manualUrging(batchnum);
        String returnStr = "";
        if(billApproval!=null) {
            Tb_user spuser = userRepository.findByUserid(billApproval.getSpman());
            if(sendMsg!=null&&"true".equals(sendMsg)&&spuser!=null) {
                try {
                    returnStr = smsService.SendSMS(spuser.getPhone(), "您有一条档案系统的销毁审批，请登录档案系统管理平台及时处理！");
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ExtMsg(true, "已催办，短信发送失败", null);
                }
            }
            if ("".equals(returnStr)) {
                return new ExtMsg(true, "已催办", null);
            } else {
                return new ExtMsg(true, "已催办，短信发送结果为：" + returnStr, null);
            }
        }
        return new ExtMsg(true, "催办失败", null);
    }

    /**
     * 发布数据包单据
     *
     * @param entryids
     * @return
     */
    @RequestMapping("/getDataopenDoc")
    @ResponseBody
    public ExtMsg getDataopenDoc(String entryids) {
        Tb_datareceive datareceive = dataopenService.getDataopenDoc(entryids);
        return new ExtMsg(true, "成功", datareceive);
    }
}
