package com.wisdom.web.controller;

import com.google.gson.Gson;
import com.wisdom.service.timejob.service.TimeJobTask;
import com.wisdom.util.GainField;
import com.wisdom.util.LogAop;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.service.*;
import com.xdtech.project.foursexverify.entity.DataView;
import org.checkerframework.checker.units.qual.A;
import org.jaudiotagger.tag.datatype.BooleanString;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yl on 2019/11/20.
 * 长期保管控制器
 */
@Controller
@RequestMapping(value = "/longRetention")
public class LongRetentionController {

    @Autowired
    LongRetentionService longRetentionService;

    @Autowired
    EntryIndexService entryIndexService;

    @Autowired
    EntryIndexCaptureService entryIndexCaptureService;

    @Autowired
    LongRetentionRepository longRetentionRepository;

    @Autowired
    TimeJobService timeJobService;

    @Autowired
    TimeJobTask timeJobTask;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Autowired
    NodesettingService nodesettingService;

    @Autowired
    PublicUtilService publicUtilService;

    @Autowired
    LongRetentionSettingRepository longRetentionSettingRepository;

    @Autowired
    CodesettingService codesettingService;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    EntryIndexCaptureRepository entryIndexCaptureRepository;

    @RequestMapping("/main")
    public String longRetention() {
        return "/inlet/longRetention";
    }

    @RequestMapping("/verification")
    @ResponseBody
    public ExtMsg verification(String entryids, String isSelectAll, String nodeid, String condition, String operator,
                               String content, String module, String nodefullname, HttpServletRequest request,String transforType) {
        String[] entryInfo = entryids.split(",");
        List<Tb_entry_index> entryIndexList=new ArrayList<>() ;
        if ("true".equals(isSelectAll)) {
            List<Tb_entry_index> entry_indexList;
            if("capture".equals(module)){//数据采集
                if("1".equals(transforType)) {//判断处理移交还是直接移交 2 直接移交 1 处理移交
                    entry_indexList = entryIndexService.getDocPreviewEntry(nodeid, condition, operator, content);
                }else {
                    entry_indexList = entryIndexService.getLognIndexCapture(nodeid, condition, operator, content);
                }
            }else{//数据管理
                entry_indexList = entryIndexService.getLongEntryIndex(nodeid, condition, operator, content, null, null, null);
            }
            if (entry_indexList.size() > 0) {
                for (int i = 0; i < entry_indexList.size(); i++) {
                    if (!entryids.contains(entry_indexList.get(i).getEntryid().trim())) {
                        Tb_entry_index entry_index= new Tb_entry_index();
                        BeanUtils.copyProperties( entry_indexList.get(i),entry_index);
                        entryIndexList.add(entry_index);
                    }
                }
            }
            request.getSession().setAttribute("AntiElectionEntryIds",entryids);//反选的所有条目id
            request.getSession().setAttribute("gridChoiceEntryIds",null);//选择移交的所有条目id
        } else {
            if("capture".equals(module)) {//数据采集
                List<Tb_entry_index_capture> entryIndexCapturesList=entryIndexCaptureRepository.findByEntryidIn(entryInfo);
                entryIndexCapturesList.stream().forEach(capture->{
                    Tb_entry_index tb_entry_index=new Tb_entry_index();
                    BeanUtils.copyProperties(capture, tb_entry_index);
                    entryIndexList.add(tb_entry_index);
                });
            }else {
                List<Tb_entry_index> entryIndices=entryIndexRepository.findAllByAddstateIn(entryInfo);
                for (Tb_entry_index entryIndex : entryIndices) {
                    Tb_entry_index entry_index= new Tb_entry_index();
                    BeanUtils.copyProperties(entryIndex,entry_index);
                    entryIndexList.add(entry_index);
                }
            }
            request.getSession().setAttribute("AntiElectionEntryIds",null);//反选的所有条目id
            request.getSession().setAttribute("gridChoiceEntryIds",entryids);//选择移交的所有条目id
        }
        //String nodeName=nodesettingService.getNodefullnameLoop(nodeid,"_","");//根据节点全称判断是案卷或卷内
        List<String> volumeEntryIds=new ArrayList<>();//卷内条目
        String volumeNodeId=""; //卷内节点id
        Tb_long_retention_setting setting = longRetentionSettingRepository.findByNodeid(nodeid);
        System.out.println("--------------------------四性验证开始-------------------------");
        long startMillis=System.currentTimeMillis();
        Map<String,Tb_long_retention> long_retentionMap=new HashMap<>();
        Boolean isWsNode=false;
        if(entryIndexList.size()>0){
            //判断是否为文书档案节点
            if(nodefullname.contains("文书")&&nodefullname.contains("归档")){
                isWsNode=true;
            }
            String[] strings = GainField.getFieldValues(entryIndexList, "entryid");
            if(strings!=null) {
                //longRetentionRepository.deleteByEntryidIn(entryList.toArray(strings));
                List<String[]> subAry = new InformService().subArray(strings, 1000);// 处理参数超出问题
                for (String[] s : subAry) {
                    List<Tb_long_retention> longRetentionList = longRetentionRepository.findByEntryidIn(s);
                    longRetentionList.stream().forEach(long_retention -> {
                        long_retentionMap.put(long_retention.getEntryid().trim(), long_retention);
                    });
                }
            }
        }
        if(nodefullname.contains("案卷")&&!nodefullname.contains("卷内")) {
            volumeNodeId= publicUtilService.getNodeid(nodeid);
            // 获取案卷档号设置字段集合
		    List<String> ajCodeSettingFieldList = codesettingService.getCodeSettingFields(nodeid);
            // 获取卷内档号设置字段集合
		    List<String> jnCodeSettingFieldList = codesettingService.getCodeSettingFields(volumeNodeId);
            //获取卷内验证设置
            Tb_long_retention_setting jnSetting = longRetentionSettingRepository.findByNodeid(volumeNodeId);
            for (Tb_entry_index entryid : entryIndexList) {
                volumeEntryIds.addAll(longRetentionService.getVolumeFourSexVerify(entryid, module, nodeid,volumeNodeId,setting,
                        long_retentionMap.get(entryid.getEntryid()),ajCodeSettingFieldList,jnCodeSettingFieldList,jnSetting,isWsNode));
            }
        }else {
            for (Tb_entry_index entryid : entryIndexList) {
                longRetentionService.getFourSexVerify(entryid, module, setting,long_retentionMap.get(entryid.getEntryid()),isWsNode);
            }
        }
        System.out.println("-----------------四性验证结束-----------------数量："+entryIndexList.size()+"---耗时："+(System.currentTimeMillis()-startMillis));
        request.getSession().setAttribute("volumeEntryIds",volumeEntryIds);
        return new ExtMsg(true, volumeNodeId, volumeEntryIds.size());
    }

    @RequestMapping("/showVerifyPackage")
    @ResponseBody
    public List<com.xdtech.project.foursexverify.entity.FileTree> showVerifyPackage(String entryid) {
        return longRetentionService.getVerifyPackage(entryid);
    }

    @RequestMapping("/getDataView")
    @ResponseBody
    public void getDataView(String[] childrens, HttpServletResponse response) {
        String jsonStr = "";
        try {
            List<DataView> dataViews = new ArrayList<DataView>();
            if (childrens != null && childrens.length > 0) {
                for (String children : childrens) {
                    DataView dataView = new DataView();
                    dataView.setName(children);
                    if (children.indexOf(".") == -1) {
                        dataView.setUrl("/img/folder.png");
                    } else if (children.toLowerCase().substring(children.lastIndexOf(".") + 1)
                            .equals("jpg")) {
                        dataView.setUrl("/img/jpg.ico");
                    } else if (children.toLowerCase().substring(children.lastIndexOf(".") + 1)
                            .equals("png")) {
                        dataView.setUrl("/img/png.ico");
                    } else if (children.toLowerCase().substring(children.lastIndexOf(".") + 1)
                            .equals("xml")) {
                        dataView.setUrl("/img/xml.ico");
                    } else if (children.toLowerCase().substring(children.lastIndexOf(".") + 1)
                            .equals("pdf")) {
                        dataView.setUrl("/img/pdf.ico");
                    } else if (children.toLowerCase().substring(children.lastIndexOf(".") + 1)
                            .equals("ppt")) {
                        dataView.setUrl("/img/ppt.ico");
                    } else if (children.toLowerCase().substring(children.lastIndexOf(".") + 1)
                            .equals("xls")
                            || children.toLowerCase().substring(children.lastIndexOf(".") + 1)
                            .equals("xlsx")) {
                        dataView.setUrl("/img/xls.ico");
                    } else if (children.toLowerCase().substring(children.lastIndexOf(".") + 1)
                            .equals("docx")
                            || children.toLowerCase().substring(children.lastIndexOf(".") + 1)
                            .equals("doc")) {
                        dataView.setUrl("/img/doc.ico");
                    } else {
                        dataView.setUrl("/img/focus/no.jpg");
                    }
                    dataViews.add(dataView);
                }
            }
            Map<String, Object> jsonObj = new HashMap<String, Object>();
            jsonObj.put("content", dataViews);
            Gson gson = new Gson();
            jsonStr = gson.toJson(jsonObj);
            response.setContentType("application/x-json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = null;
            out = response.getWriter();
            out.print(jsonStr);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/getMetadata")
    @ResponseBody
    public Map<String, String> getMetadata(String entryid, String xmlName) {
        return longRetentionService.getMetadata(entryid, xmlName);
    }

    @RequestMapping("/checkFile")
    @ResponseBody
    public ExtMsg checkFile(String entryid) {
        return longRetentionService.checkFile(entryid);
    }

    @RequestMapping(value = "/entries", method = RequestMethod.GET)
    @ResponseBody
    public Page<Tb_index_detail> getEntrys(String nodeid, String type, String condition, String operator, String content, Tb_index_detail formConditions,
                                           ExtOperators formOperators, ExtDateRangeData daterangedata, String logic, boolean ifSearchLeafNode,
                                           boolean ifContainSelfNode, int page, int limit, String sort) {

        Sort sortobj = WebSort.getSortByJson(sort);
        if (type != null && type.equals("模板预览")) {
            return null;
        }
        Page<Tb_index_detail> details = entryIndexService.getEntries(nodeid, condition, operator, content, formConditions, formOperators,
                daterangedata, logic, ifSearchLeafNode, ifContainSelfNode, page, limit, sortobj);
        details.getContent().stream().forEach(tb_index_detail -> {
            Tb_long_retention longRetention = longRetentionRepository.findByEntryid(tb_index_detail.getEntryid());
            if (longRetention != null) {
                tb_index_detail.setCheckstatus(longRetention.getCheckstatus());
                tb_index_detail.setAuthenticity(longRetention.getAuthenticity());
                tb_index_detail.setIntegrity(longRetention.getIntegrity());
                tb_index_detail.setUsability(longRetention.getUsability());
                tb_index_detail.setSafety(longRetention.getSafety());
            }
        });
        return details;
    }

    /**
     * 获取已四性验证的移交条条目的验证结果
     * @param checkAll 采集页面是否全选
     * @param entryids  采集页面点击选取的条目id
     * @param nodeid  条目节点
     * @param condition  检索
     * @param operator
     * @param content
     * @param page  分页
     * @param limit
     * @param sort
     * @param volumeNodeId 卷内节点编号
     * @return
     */
    @RequestMapping(value = "/captureEntries", method = RequestMethod.GET)
    @ResponseBody
    public Page<Tb_index_detail_capture> getCaptureEntrys(String checkAll, String entryids, String nodeid, String docid, String condition,HttpServletRequest request,
                                                          String operator, String content, int page, int limit, String sort,String volumeNodeId,String transforType) {
        Sort sortobj = WebSort.getSortByJson(sort);
        Page<Tb_index_detail_capture> details;
        if(docid==null){// 移交验证获取
            List<String> volumeEntryIds=(List<String>)request.getSession().getAttribute("volumeEntryIds");
            if(volumeEntryIds==null){
                volumeEntryIds=new ArrayList<>();
            }
            details = entryIndexCaptureService.getEntryCapyures(checkAll, entryids, nodeid, condition, operator, content, page, limit, sortobj,volumeNodeId,volumeEntryIds,transforType);
        }else{//接收验证获取
            details = entryIndexCaptureService.getEntryCapyureByDocid(docid, condition, operator, content, page, limit, sortobj);
        }
        Map<String,Tb_long_retention> long_retentionMap=new HashMap<>();
        String[] strings = GainField.getFieldValues(details.getContent(), "entryid");
        if(strings!=null) {
            List<Tb_long_retention> longRetentionList = longRetentionRepository.findByEntryidIn(strings);
            longRetentionList.forEach(long_retention -> {
                long_retentionMap.put(long_retention.getEntryid().trim(), long_retention);
            });
        }
        details.getContent().forEach(tb_index_detail -> {
            Tb_long_retention longRetention =long_retentionMap.get(tb_index_detail.getEntryid().trim());
            if (longRetention != null) {
                tb_index_detail.setCheckstatus(longRetention.getCheckstatus());
                tb_index_detail.setAuthenticity(longRetention.getAuthenticity());
                tb_index_detail.setIntegrity(longRetention.getIntegrity());
                tb_index_detail.setUsability(longRetention.getUsability());
                tb_index_detail.setSafety(longRetention.getSafety());
            }
        });
        return details;
    }

    /**
     * 获取已四性验证的OA条目的验证结果
     * @param entryid
     * @return
     */
    @RequestMapping(value = "/getFsexRecord")
    @ResponseBody
    public ExtMsg getFsexRecord(String entryid) {
        Tb_long_retention longRetention = longRetentionRepository.findByEntryid(entryid);
        return new ExtMsg(true,"获取成功",longRetention);
    }

    @RequestMapping("/deleteLongRetention")
    @ResponseBody
    public ExtMsg deleteThematicDetail(String entryids, String isSelectAll, String nodeid, String condition, String operator,
                                       String content) {
        String[] entryInfo = entryids.split(",");
        String[] entryidData;
        if ("true".equals(isSelectAll)) {
            List<String> entry_indexList = entryIndexService.getIndexIds(nodeid, condition, operator, content,null,null,null);
            if (entry_indexList.size() > 0) {
                List<String> tempEntry = new ArrayList<>();
                List<String> entryList = Arrays.asList(entryInfo);
                for (int i = 0; i < entry_indexList.size(); i++) {
                    String entryid = entry_indexList.get(i);
                    if (!entryList.contains(entryid)) {
                        tempEntry.add(entryid);
                    }
                }
                entryidData = tempEntry.toArray(new String[tempEntry.size()]);
            } else {
                entryidData = new String[]{};
            }
        } else {
            entryidData = entryInfo;
        }
        longRetentionRepository.deleteByEntryidIn(entryidData);
        ExtMsg extMsg = new ExtMsg(true, "重置成功", null);
        return extMsg;
    }

    @RequestMapping("/getSelectAllData")
    @ResponseBody
    public String[] getSelectAllData(String entryids, String isSelectAll, String nodeid, String condition,
                                     String operator, String content) {
        String[] entryInfo = entryids.split(",");
        String[] entryidData;
        if ("true".equals(isSelectAll)) {
            List<String> entry_indexList = entryIndexService.getIndexIds(nodeid, condition, operator, content,null,null,null);
            if (entry_indexList.size() > 0) {
                List<String> tempEntry = new ArrayList<>();
                List<String> entryList = Arrays.asList(entryInfo);
                for (int i = 0; i < entry_indexList.size(); i++) {
                    String entryid = entry_indexList.get(i);
                    if (!entryList.contains(entryid)) {
                        tempEntry.add(entryid);
                    }
                }
                entryidData = tempEntry.toArray(new String[tempEntry.size()]);
            } else {
                entryidData = new String[]{};
            }
        } else {
            entryidData = entryInfo;
        }
        return entryidData;
    }

    @RequestMapping(value = "/getTimeJob")
    @ResponseBody
    public ExtMsg getTimeJob(String jobname) {
        Tb_time_job timeJob = timeJobService.findByJobname(jobname);
        if (timeJob == null) {
            timeJob = new Tb_time_job();
            timeJob.setRuncycle("day");
            timeJob.setStarttime("00");
            timeJob.setJobstate("0");
        }
        return new ExtMsg(true, "", timeJob);
    }

    @RequestMapping("/timeJobSubmit")
    @ResponseBody
    public ExtMsg workflowAddSubmit(String type, Tb_time_job timeJob) {
        Tb_time_job job = timeJobService.findByJobname("长期保管");
        if (job == null) {
            job = new Tb_time_job();
            job.setJobname("长期保管");
            job.setJobclass("com.wisdom.service.timejob.job.LongRetentionJob");
        }
        job.setRuncycle(timeJob.getRuncycle());
        job.setStarttime(timeJob.getStarttime());
        if ("day".equals(timeJob.getRuncycle())) {
            job.setCronexpression("00 00 " + timeJob.getStarttime() + " ? * *");
            job.setMonthly(null);
            job.setWeekly(null);
        } else if ("month".equals(timeJob.getRuncycle())) {
            job.setMonthly(timeJob.getMonthly());
            job.setWeekly(null);
            job.setCronexpression("00 00 " + timeJob.getStarttime() + " ? " + timeJob.getMonthly() + " *");
        } else {
            job.setWeekly(timeJob.getWeekly());
            job.setMonthly(null);
            job.setCronexpression("00 00 " + timeJob.getStarttime() + " ? * " + timeJob.getWeekly());
        }
        String msg = "";
        boolean result = false;
        //停止
        if ("0".equals(type)) {
            job.setJobstate("0");
            //停止任务
            try {
                timeJobTask.pauseJob(job);
                result = true;
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
            if (result) {
                msg = "停止成功";
            } else {
                msg = "停止失败";
            }
        } else if ("1".equals(type)) {//启动
            job.setJobstate("1");
            //启动任务
            try {
                timeJobTask.startJob(timeJobService.saveJob(job));
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (result) {
                msg = "启动成功";
            } else {
                msg = "启动失败";
            }
        } else {//保存
            if ("未开启".equals(timeJob.getJobstate())) {
                job.setJobstate("0");
                result = true;
                msg = "保存成功";
            } else {
                job.setJobstate("1");
                //更新任务
                try {
                    timeJobTask.updateJob(job);
                    result = true;
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
                if (result) {
                    msg = "保存成功";
                } else {
                    msg = "更新定时任务不成功，保存失败";
                }
            }
        }
        if (result) {
            return new ExtMsg(true, msg, timeJobService.saveJob(job));
        } else {
            return new ExtMsg(false, msg, null);
        }
    }

    @RequestMapping("/settingSubmit")
    @ResponseBody
    public ExtMsg settingSubmit(String nodeid, String authenticity, String integrity, String usability, String safety) {
        Tb_long_retention_setting setting = longRetentionService.getSetting(nodeid);
        if(setting!=null){
            setting.setAuthenticity(authenticity);
            setting.setIntegrity(integrity);
            setting.setUsability(usability);
            setting.setSafety(safety);
        }else{
            setting = new Tb_long_retention_setting();
            setting.setNodeid(nodeid);
            setting.setAuthenticity(authenticity);
            setting.setIntegrity(integrity);
            setting.setUsability(usability);
            setting.setSafety(safety);
        }
        longRetentionService.saveSetting(setting);
        return new ExtMsg(true, "", null);
    }

    @RequestMapping(value = "/getSetting")
    @ResponseBody
    public ExtMsg getSetting(String nodeid) {
        Tb_long_retention_setting setting = longRetentionService.getSetting(nodeid);
        return new ExtMsg(true, "", setting);
    }
}
