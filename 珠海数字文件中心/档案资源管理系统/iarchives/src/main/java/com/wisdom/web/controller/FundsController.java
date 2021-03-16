package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.wisdom.util.FunctionUtil;
import com.wisdom.util.LogAnnotation;
import com.wisdom.util.LogAop;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_data_node;
import com.wisdom.web.entity.Tb_funds;
import com.wisdom.web.entity.Tb_right_organ;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.repository.DataNodeRepository;
import com.wisdom.web.repository.EntryIndexRepository;
import com.wisdom.web.repository.FundsRepository;
import com.wisdom.web.repository.RightOrganRepository;
import com.wisdom.web.service.EntryIndexService;
import com.wisdom.web.service.FundsService;

import java.util.List;

import com.wisdom.web.service.NodesettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 全宗管理控制器
 * Created by RonJiang on 2018/04/08
 */
@Controller
@RequestMapping(value = "/funds")
public class FundsController {

    @Autowired
    LogAop logAop;

    @Autowired
    FundsService fundsService;
    
    @Autowired
    RightOrganRepository rightOrganRepository;
    
    @Autowired
    DataNodeRepository dataNodeRepository;
    
    @Autowired
    EntryIndexService entryIndexService;
    
    @Autowired
    EntryIndexRepository entryIndexRepository;
    
    @Autowired
    FundsRepository fundsRepository;

    @Autowired
    NodesettingService nodesettingService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${system.report.server}")
    private String reportServer;//报表服务

    @RequestMapping("/main")
    public String index(Model model, String isp) {
        Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
        model.addAttribute("functionButton",functionButton);
        model.addAttribute("reportServer",reportServer);
        return "/inlet/funds";
    }
    
    @RequestMapping("/initFunds")
    @ResponseBody
    public ExtMsg initFunds(){
    	int value = 0;
    	List<Tb_right_organ> info = rightOrganRepository.findUnitInfo();
    	Tb_funds funds = null;
    	for (int i = 0; i < info.size(); i++) {
    		funds = new Tb_funds();
    		funds.setOrganid(info.get(i).getOrganid());
    		funds.setOrganname(info.get(i).getOrganname());
    		fundsService.saveFunds(funds);
    		value++;
    	}
    	return new ExtMsg(true, "同步完成，共"+value+"条数据", null);
    }

    @RequestMapping("/getFunds")
    @ResponseBody
    public Page<Tb_funds> getFunds(int page, int start, int limit, String condition,String operator,String content,String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        logger.info("page:" + page + ";start:" + start + ";limt:" + limit);
        return fundsService.findBySearch(condition,operator,content,page,limit,sortobj);
    }

    @RequestMapping(value = "/fundss/{fundsid}", method = RequestMethod.GET)
    @ResponseBody
    public Tb_funds getFunds(@PathVariable String fundsid){
        return fundsService.getFunds(fundsid);
    }

    @LogAnnotation(module = "全宗管理",sites = "1",fields = "fundsname,funds",connect = "##全宗名称；,##全宗号；",startDesc = "保存操作，全宗详情：")
    @RequestMapping(value = "/fundss", method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg saveFunds(@ModelAttribute("form") Tb_funds funds,String organid, String operate){
    	boolean is = false;
    	List<String> fundsInfo = fundsService.findByFunds();
    	if (funds.getFunds() != null || !"".equals(funds.getFunds())) {// 如果填写了全宗号
    		Tb_funds fundsidfunds = fundsService.getFunds(funds.getFundsid());
	    	if (operate.equals("add")) {// 判断全宗号是否重复
	    		is = true;
	    	} else {
				if (!funds.getFunds().equals(fundsidfunds.getFunds())) {// 如果修改了全宗号,判断全宗号是否重复
					is = true;
				}
	    	}
    	}
    	if (is) {
    		for (int i = 0; i < fundsInfo.size(); i++) {
				if (fundsInfo.get(i).equals(funds.getFunds()))// 如果全宗信息存在
	    			return new ExtMsg(false,"全宗号重复",null);
			}
    	}
    	if (organid != null && !organid.equals("")) {
    		funds.setOrganid(organid);
    	}
    	Tb_funds result = fundsService.saveFunds(funds);
        if(result != null){
            if (organid != null && !organid.equals("")&&is) {
                //更新模板全宗字段默认值
                fundsService.updateTemplateFunds(organid,funds.getFunds());
            }
            return new ExtMsg(true,"保存成功",result);
        }
        return new ExtMsg(false,"保存失败",null);
    }

//    @LogAnnotation(module = "全宗管理",startDesc = "删除全宗记录操作，全宗id为：",sites = "1")
    @RequestMapping(value = "/fundss/{fundsids}", method = RequestMethod.DELETE)
    @ResponseBody
    public ExtMsg delFunds(@PathVariable String fundsids) {
        String startTime = LogAop.getCurrentSystemTime();//开始时间
        long startMillis = System.currentTimeMillis();//开始毫秒数
        String[] fundsidData = fundsids.split(",");
        Integer del = fundsService.delFunds(fundsidData);
        for(String fundsid:fundsidData){
            logAop.generateManualLog(startTime,LogAop.getCurrentSystemTime(),System.currentTimeMillis()-startMillis,"全宗管理","删除全宗记录操作，全宗id为："+fundsid);
        }
        if (del > 0) {
            return new ExtMsg(true, "删除成功", del);
        }
        return new ExtMsg(false, "删除失败", null);
    }
    
    /**
     * 全宗汇总
     * @param info
     */
    @RequestMapping("/summaryInfo")
    @ResponseBody
    public ExtMsg summaryInfo(String info){
    	String[] strings = info.split(",");
    	String dcnodeid = dataNodeRepository.findDCFile();//已归管理-文书档案节点id
    	List<String> filesid = dataNodeRepository.findFiles();//查找到案卷管理中所有案卷信息
    	List<String> innerFilesid = dataNodeRepository.findInnerFiles();//查找到案卷管理中所有卷内信息
    	List<String> dcfiles = dataNodeRepository.findDCFiles();//查找到案卷管理 - 文书档案 - 文书案卷节点信息
    	List<String> dcinnerfiles = dataNodeRepository.findDCInnerFiles();//查找到案卷管理 - 文书档案 - 卷内文件节点信息
        Tb_data_node node = dataNodeRepository.findByNodename("案卷管理");
        //案卷管理下的所有子节点
        List<String> nodeListget = nodesettingService.getNodeidLoop(node.getNodeid(), true, null);
        Tb_data_node nodeReturn = dataNodeRepository.findByNodename("已归管理");
        if(nodeReturn==null){
            nodeReturn = dataNodeRepository.findByNodename("归档管理");
        }
        //已归管理下的所有子节点
        List<String> nodeListReturn = nodesettingService.getNodeidLoop(nodeReturn.getNodeid(), true, null);
    	for (int j = 0; j < strings.length; j++) {
    		String fundsid = strings[j].split("-")[0];
    		String organid = strings[j].split("-")[1];
    		String funds = strings[j].split("-")[2];
			List<Tb_data_node> nodes = dataNodeRepository.findByRefid(organid);//查找到机构id对应节点refid的数据
			String allFiled = entryIndexService.getFileds(nodes, funds,nodeListReturn);//存储归档文件总数
            String[] shortTerm = {"短期","10年"};//存储归档短期（件）(获取已归管理 - 文书档案中保管期限为短期的)
        	String shortTermValue = entryIndexService.getTerm(nodes, dcnodeid, shortTerm, funds);
            String[] longTerm = {"长期","30年"};//存储归档长期（件）
            String longTermValue = entryIndexService.getTerm(nodes, dcnodeid, longTerm, funds);
            String[] permanentTerm = {"永久"};//存储归档永久（件）
            String permanentTermValue = entryIndexService.getTerm(nodes, dcnodeid, permanentTerm, funds);
            //归档文书总数
            String filingNum = String.valueOf(entryIndexService.getFilingNum(dcnodeid, funds));

        	String allNum = entryIndexService.getAllFileds(nodes, filesid, funds,nodeListget);//存储案卷总数
			String allInnerNum = entryIndexService.getAllFileds(nodes, innerFilesid, funds,nodeListget);//存储卷内总数
			String allDCFielsNum = entryIndexService.getAllFileds(nodes, dcfiles, funds,nodeListget);//存储文书案卷数
			String allDCInnerFielsNum = entryIndexService.getAllFileds(nodes, dcinnerfiles, funds,nodeListget);//存储文书卷内份数
        	//存储其它案卷数
			String otheraj = String.valueOf(Integer.valueOf(allNum)-Integer.valueOf(allDCFielsNum));
        	//存储其它卷内份数
			String otherJn = String.valueOf(Integer.valueOf(allInnerNum)-Integer.valueOf(allDCInnerFielsNum));
			//更新全宗数据
        	fundsRepository.modifyFundsByOrganid(allFiled, shortTermValue, longTermValue, permanentTermValue,
        			allNum, allInnerNum, allDCFielsNum, allDCInnerFielsNum, otheraj, otherJn, filingNum, fundsid);
    	}
    	return new ExtMsg(true, "汇总成功", null);
    }

    @RequestMapping("/getNodeFunds")
    @ResponseBody
    public String getNodeFunds(String nodeid){
        return fundsService.getNodeFunds(nodeid);
    }
    
    @RequestMapping("/getOrganFunds/{organid}")
    @ResponseBody
    public ExtMsg getOrganFunds(@PathVariable String organid){
        return new ExtMsg(true, "success", fundsService.getOrganFunds(organid));
    }


    @RequestMapping("/getAllFunds")
    @ResponseBody
    public List<Tb_funds> getAllFunds(){
        return fundsRepository.findAll();
    }
}