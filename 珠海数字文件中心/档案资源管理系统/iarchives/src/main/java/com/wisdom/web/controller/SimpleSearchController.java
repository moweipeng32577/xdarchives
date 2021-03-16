package com.wisdom.web.controller;

import com.wisdom.secondaryDataSource.entity.Tb_entry_detail_sx;
import com.wisdom.secondaryDataSource.entity.Tb_entry_index_sx;
import com.wisdom.secondaryDataSource.repository.SecondaryEntryDetailRepository;
import com.wisdom.secondaryDataSource.repository.SecondaryEntryIndexRepository;
import com.wisdom.util.ExportUtil;
import com.wisdom.util.LogAop;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.EntryService;
import com.wisdom.web.service.NodesettingService;
import com.wisdom.web.service.SimpleSearchDirectoryService;
import com.wisdom.web.service.SimpleSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 简单检索控制器 Created by RonJiang on 2017/10/24 0024.
 */
@Controller
@RequestMapping(value = "/simpleSearch")
public class SimpleSearchController {

	@Autowired
	LogAop logAop;

	@Autowired
	EntryService entryService;

	@Autowired
	EntryIndexRepository entryIndexRepository;

	@Autowired
	SimpleSearchService simpleSearchService;

	@Autowired
	EntryIndexManageRepository entryIndexManageRepository;

	@Autowired
	EntryDetailRepository entryDetailRepository;

	@Autowired
	EntryDetailManageRepository entryDetailManageRepository;

	@Autowired
	NodesettingService nodesettingService;

	@Autowired
	SecondaryEntryIndexRepository secondaryEntryIndexRepository;

	@Autowired
	SecondaryEntryDetailRepository secondaryEntryDetailRepository;

	@Autowired
	WorkRepository workRepository;

    @Autowired
    SimpleSearchDirectoryService simpleSearchDirectoryService;

    @Autowired
    EntryBookmarksRepository bookmarksRepository;

    @Autowired
    ClassifySearchDirectoryController classifySearchDirectoryController;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${system.report.server}")
	private String reportServer;//报表服务

	@Value("${system.instantSearch.opened}")
	private String instantSearch;//判断是否开启即时搜索

    @Value("${find.sx.data}")
    private Boolean opensxData;//是否可检索声像系统的声像数据

	@Value("${system.loginType}")
	private String systemType;//政务网1  局域网0

	@Value("${find.sx.Miedata}")
	private String opensxMiedata;//声像开放是否缩列图显示

	@RequestMapping("/main")
	public String index(Model model, String flag,String titleflag) {
		model.addAttribute("buttonflag", flag);
		model.addAttribute("reportServer",reportServer);
		model.addAttribute("titleflag",titleflag);
		model.addAttribute("instantSearch",instantSearch);
		return "/inlet/simpleSearch";
	}

	/**
	 * 是否是声像的利用查询页面
	 * @param model
	 * @param flag
	 * @param resultType "1" 为是，空或者是 "0"为不是
	 * @return
	 */
	//解决利用平台与管理平台公用页面权限控制问题
	@RequestMapping("/mainly")
	public String indexly(Model model, String flag,String titleflag,String type, String resultType){
		model.addAttribute("resultType",resultType);
		model.addAttribute("buttonflag",flag);
		model.addAttribute("reportServer",reportServer);
		model.addAttribute("titleflag",titleflag);
		model.addAttribute("instantSearch",instantSearch);
		model.addAttribute("type",type);  //判断是否自主查询
		model.addAttribute("systemType",systemType);  //政务网1  局域网0
		model.addAttribute("opensxMiedata",opensxMiedata);  //政务网1  局域网0
		Tb_work workBorrrow = workRepository.findByWorktext("查档审批");
		Tb_work workPrint = workRepository.findByWorktext("电子打印审批");
		if(workBorrrow!=null){
			model.addAttribute("borrrowSendmsg","1".equals(workBorrrow.getSendmsgstate())?true:false);  //查档是否短信通知
		}else{
			model.addAttribute("borrrowSendmsg",false);
		}
		if(workPrint!=null){
			model.addAttribute("printSendmsg","1".equals(workPrint.getSendmsgstate())?true:false);  //电子打印是否短信通知
		}else{
			model.addAttribute("printSendmsg",false);
		}
		return "/inlet/simpleSearch";
	}

	@RequestMapping("/getInstantSearch")
	@ResponseBody
	public String getInstantSearch(){
		return instantSearch;
	}

	@RequestMapping("/findBySearch")
	@ResponseBody
	public Page<Tb_index_detail> findBySearch(int page, int limit, String isCollection, String condition,
			String operator, String content, String sort, String datasoure) {
		Sort sortobj = WebSort.getSortByJson(sort);
		if("soundimage".equals(datasoure)){
			return simpleSearchService.findBySearchCompilationSx(page,limit,condition,operator,content,sortobj);
		}
		Page<Tb_index_detail> list = simpleSearchService.findBySearch(page, limit, isCollection, condition, operator,
				content, sortobj,datasoure);
		return list;
	}

	@RequestMapping("/findMediaBySearchPlatform")
	@ResponseBody
	public Page<MediaEntry> getMediaBySearch(int page, int limit, String isCollection, String condition,
											 String operator, String content, String sort){
		Sort sortobj = WebSort.getSortByJson(sort);
		Page<MediaEntry> returnPage =null;
		if(opensxData){
            returnPage = simpleSearchService.findMediaSearchOpen(page, limit, isCollection, condition, operator,
                    content, sortobj);
        }else {
            returnPage =simpleSearchService.findMediaSearchOpen(page, limit, "原文开放,条目开放", isCollection, condition, operator,
                    content, sortobj);
        }
        return returnPage;
	}

	/**
	 *
	 * @param isCollection
	 * @param condition //筛选条件
	 * @param operator 比较关键字(比如like)
	 * @param content
	 * @param sort
	 * @param isCompilationManageSystem 请求是否为编研管理系统,因为编研管理系统要把编研采集录入的数据也显示出来.
	 * @return
	 */
	@RequestMapping("/findBySearchPlatform")
	@ResponseBody
	public Page<Tb_index_detail> findBySearchPlatform(String datasoure,int page, int limit, String isCollection, String condition,
			String operator, String content, String sort,boolean isCompilationManageSystem, String entryids) {
		Sort sortobj = WebSort.getSortByJson(sort);
		String flagOpen = "原文开放,条目开放";
		if(isCompilationManageSystem){
			flagOpen += ",编研开放";
		}
		if("soundimage".equals(datasoure)){//声像开放查询
			return simpleSearchService.findBySearchCompilationSx(page,limit,condition,operator,content,sortobj);
		}
		return simpleSearchService.findBySearchPlatformOpen(page, limit, flagOpen, isCollection, condition, operator,
				content, sortobj,entryids);
	}

	@RequestMapping("/getAllEntry")
	@ResponseBody
	public Page<Tb_entry_index> getAllEntry(int page, int start, int limit) {
		logger.info("page:" + page + ";start:" + start + ";limt:" + limit);
		Page<Tb_entry_index> list = simpleSearchService.getAllEntry(page, limit);
		logger.info(list.toString());
		return list;
	}

	// @LogAnnotation(module = "简单检索",startDesc = "数据导出操作，导出文件名为：",sites =
	// "1,2",connect = "，导出条目id为：")
	@RequestMapping("/exportData")
	@ResponseBody
	public void exportData(String fileName, String[] entryids,String[] names,String[] keys, HttpServletResponse response,String type) {
		String startTime = LogAop.getCurrentSystemTime();// 开始时间
		long startMillis = System.currentTimeMillis();// 开始毫秒数
		logger.info("fileName:" + fileName + ";entryids:" + entryids);
		List<Tb_entry_index> entryIndexes = new ArrayList<>();
		List<Tb_entry_index_manage> manageIndexes = new ArrayList<>();
		List<Tb_entry_detail>entryDetails = new ArrayList<Tb_entry_detail>();
		List<Tb_entry_detail_manage> manageDetails = new ArrayList<Tb_entry_detail_manage>();
		List<Tb_entry_detail_sx> detailSxes = new ArrayList();
		List<Tb_entry_index_sx> indexSxes = new ArrayList();
		if(type!=null&&"directory".equals(type)){  //判断是否目录简单检索
			manageIndexes = entryIndexManageRepository.findByEntryidIn(entryids);
			manageDetails = entryDetailManageRepository.findByEntryidIn(entryids);
		}else if(type!=null&&"management".equals(type)){
			entryIndexes = entryIndexRepository.findByEntryidIn(entryids);
			entryDetails = entryDetailRepository.findByEntryidIn(entryids);
		}else if(type!=null&&"soundimage".equals(type)){   //声像系统
			indexSxes = secondaryEntryIndexRepository.findByEntryidIn(entryids);
			detailSxes = secondaryEntryDetailRepository.findByEntryidIn(entryids);
		}else {
			entryIndexes = entryIndexRepository.findByEntryidIn(entryids);
			entryDetails = entryDetailRepository.findByEntryidIn(entryids);
		}
		logger.info(entryIndexes.toString());
		List<Map<String, Object>> list = createExcelRecord(entryIndexes,manageIndexes,entryDetails,manageDetails,indexSxes,detailSxes,keys,type);
//		IntStream.range(0,list.size()).mapToObj(i->list.get(i).put("sort",i+1)).collect(Collectors.toList());
		ExportUtil exportUtil = new ExportUtil(fileName, response, list, keys, names);
		exportUtil.exportExcel();
		for (String entryid : entryids) {
			logAop.generateManualLog(startTime, LogAop.getCurrentSystemTime(), System.currentTimeMillis() - startMillis,
					"简单检索", "数据导出操作，导出文件名为：" + fileName + ",导出条目id为：" + entryid);
		}
	}

	@RequestMapping("/getEntryIndexColumn")
	@ResponseBody
	public ExtMsg getEntryIndexColumn() {
		return new ExtMsg();
	}

	public List<Map<String, Object>> createExcelRecord(List<Tb_entry_index> entryIndexes, List<Tb_entry_index_manage> manageIndexes,
							List<Tb_entry_detail>entryDetails, List<Tb_entry_detail_manage> manageDetails,List<Tb_entry_index_sx> indexSxes,
														List<Tb_entry_detail_sx> detailSxes,String keys[], String type) {
		List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
		if(type!=null&&"directory".equals(type)){
			Tb_entry_index_manage entryIndex;
			Tb_entry_detail_manage entryDetail;
			for (int i = 0; i < manageIndexes.size(); i++) {
				Map<String,Object> entryMap = new HashMap<String,Object>();
				// entryIndex实体对象转换成map对象
				entryIndex = manageIndexes.get(i);
				Field[] entryFields = entryIndex.getClass().getDeclaredFields();
				for(Field entryfield  :entryFields){
					entryfield.setAccessible(true); // //在反射时能访问私有变量
					try {
						entryMap.put(entryfield.getName(), entryfield.get(entryIndex));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				// entryDetail实体对象转换成map对象
				entryDetail = manageDetails.get(i);
				Field[] entryCapturefields = entryDetail.getClass().getDeclaredFields();
				for(Field entryCapturefield  :entryCapturefields){
					entryCapturefield.setAccessible(true); // //在反射时能访问私有变量
					try {
						entryMap.put(entryCapturefield.getName(), entryCapturefield.get(entryDetail));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				Map<String, Object> mapValue = new HashMap<String, Object>();
				for(int j=0;j<keys.length;j++){
					if("nodefullname".equals(keys[j])){
						String nodefullname = nodesettingService.getNodefullnameLoop(entryIndex.getNodeid(),"_",""); //数据节点
						mapValue.put("nodefullname",nodefullname);
					}
					else {
						mapValue.put(keys[j],entryMap.get(keys[j]));
					}
				}
				listmap.add(mapValue);
			}
		}else if(type!=null&&"management".equals(type)||"compilation".equals(type)){
			Tb_entry_index entryIndex;
			Tb_entry_detail entryDetail;
			for (int i = 0; i < entryIndexes.size(); i++) {
				Map<String,Object> entryMap = new HashMap<String,Object>();
				// entryIndex实体对象转换成map对象
				entryIndex = entryIndexes.get(i);
				Field[] entryFields = entryIndex.getClass().getDeclaredFields();
				for(Field entryfield  :entryFields){
					entryfield.setAccessible(true); // //在反射时能访问私有变量
					try {
						entryMap.put(entryfield.getName(), entryfield.get(entryIndex));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				// entryDetail实体对象转换成map对象
				entryDetail = entryDetails.get(i);
				Field[] entryCapturefields = entryDetail.getClass().getDeclaredFields();
				for(Field entryCapturefield  :entryCapturefields){
					entryCapturefield.setAccessible(true); // //在反射时能访问私有变量
					try {
						entryMap.put(entryCapturefield.getName(), entryCapturefield.get(entryDetail));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				Map<String, Object> mapValue = new HashMap<String, Object>();
				for(int j=0;j<keys.length;j++){
					if("nodefullname".equals(keys[j])){
						String nodefullname = nodesettingService.getNodefullnameLoop(entryIndex.getNodeid(),"_",""); //数据节点
						mapValue.put("nodefullname",nodefullname);
					}
					else {
						mapValue.put(keys[j],entryMap.get(keys[j]));
					}
				}
				listmap.add(mapValue);
			}
		}else if(type!=null&&"soundimage".equals(type)){  //声像系统
			Tb_entry_index_sx entryIndex;
			Tb_entry_detail_sx entryDetail;
			for (int i = 0; i < indexSxes.size(); i++) {
				Map<String,Object> entryMap = new HashMap<String,Object>();
				// entryIndex实体对象转换成map对象
				entryIndex = indexSxes.get(i);
				Field[] entryFields = entryIndex.getClass().getDeclaredFields();
				for(Field entryfield  :entryFields){
					entryfield.setAccessible(true); // //在反射时能访问私有变量
					try {
						entryMap.put(entryfield.getName(), entryfield.get(entryIndex));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				// entryDetail实体对象转换成map对象
				entryDetail = detailSxes.get(i);
				Field[] entryCapturefields = entryDetail.getClass().getDeclaredFields();
				for(Field entryCapturefield  :entryCapturefields){
					entryCapturefield.setAccessible(true); // //在反射时能访问私有变量
					try {
						entryMap.put(entryCapturefield.getName(), entryCapturefield.get(entryDetail));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				Map<String, Object> mapValue = new HashMap<String, Object>();
				for(int j=0;j<keys.length;j++){
					if("nodefullname".equals(keys[j])){
						String nodefullname = nodesettingService.getSxNodefullnameLoop(entryIndex.getNodeid(),"_",""); //数据节点
						mapValue.put("nodefullname",nodefullname);
					}
					else {
						mapValue.put(keys[j],entryMap.get(keys[j]));
					}
				}
				listmap.add(mapValue);
			}
		}
		return listmap;
	}

	@RequestMapping("/getApplySetPrint")
	@ResponseBody
	public List<Tb_electronic_print> getApplySetPrint(String entryid) {
		return simpleSearchService.getApplySetPrint(entryid);
	}

	@RequestMapping("/setApplySetPrint")
	@ResponseBody
	public ExtMsg setApplySetPrint(String[] applyprintids,Tb_electronic_print electronic_print) {
		simpleSearchService.setApplySetPrint(applyprintids,electronic_print);
		return new ExtMsg(true,"",null);
	}

	@RequestMapping("/cleanScope")
	@ResponseBody
	public ExtMsg cleanScopet(String[] applyprintids) {
		simpleSearchService.cleanScope(applyprintids);
		return new ExtMsg(true,"",null);
	}
}