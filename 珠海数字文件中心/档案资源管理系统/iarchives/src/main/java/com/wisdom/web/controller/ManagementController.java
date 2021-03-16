package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.*;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.*;

import com.xdtech.component.storeroom.entity.InWare;
import com.xdtech.component.storeroom.entity.OutWare;
import com.xdtech.component.storeroom.entity.Storage;
import com.xdtech.component.storeroom.entity.ZoneShelves;
import com.xdtech.component.storeroom.repository.StorageRepository;
import com.xdtech.component.storeroom.repository.ZoneShelvesRepository;
import com.xdtech.component.storeroom.repository.ZonesRepository;
import com.xdtech.component.storeroom.service.InWareService;
import com.xdtech.component.storeroom.service.OutWareService;
import com.xdtech.component.storeroom.service.StorageService;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import groovy.lang.MetaClassImpl.Index;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.MimeUtility;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 数据管理控制器 Created by Rong on 2017/10/24.
 */
@Controller
@RequestMapping(value = "/management")
public class ManagementController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${system.document.rootpath}")
	private String rootpath;// 系统文件根目录

	private static String flag;

	@Value("${system.showElectronicRename.opened}")
	private String showElectronicRename;//是否显示电子文件重名命名设置

	@Autowired
	ElectronicRepository electronicRepository;

	@Autowired
	LogAop logAop;

	@Autowired
	EntryService entryService;

	@Autowired
	EntryCaptureService entryCaptureService;

	@Autowired
	EntryIndexService entryIndexService;

	@Autowired
	CodesettingService codesettingService;

	@Autowired
	DataopenService dataopenService;

	@Autowired
	TemplateService templateService;

	@Autowired
	OrganService organService;

	@Autowired
	FundsService fundsService;
	
	@Autowired
	EntryIndexCaptureService entryIndexCaptureService;

	@Autowired
	EntryIndexRepository entryIndexRepository;
	
	@Autowired
	BillEntryIndexRepository billEntryIndexRepository;

	@Autowired
	CodesetRepository codesetRepository;

	@Autowired
	DataNodeRepository dataNodeRepository;

	@Autowired
	RightOrganRepository rightOrganRepository;

	@Autowired
	TemplateRepository templateRepository;
	
	@Autowired
	TransdocEntryRepository transdocEntryRepository;

	@Autowired
	AppraisalStandardService appraisalStandardService;

	@Autowired
	EntryIndexTempService entryIndexTempService;

	@Autowired
	EntryIndexSqTempRepository entryIndexSqTempRepository;
	
	@Autowired
	EntryIndexCaptureRepository entryIndexCaptureRepository;

	@Autowired
	BorrowMsgRepository borrowMsgRepository;

	@Autowired
	SystemConfigRepository systemConfigRepository;

	// 库房系统用，暂时注销20180531
	@Autowired
	InWareService inWareService;

	@Autowired
	OutWareService outWareService;

	@Autowired
	ZoneShelvesRepository zoneShelvesRepository;

	@Autowired
	StorageRepository storageRepository;

	@Autowired
	AcquisitionController acquisitionController;

	@Autowired
	EntryDetailRepository entryDetailRepository;

	@Autowired
	StorageService storageService;

	@Autowired
	WebSocketService webSocketService;

	@Autowired
	DataNodeExtRepository dataNodeExtRepository;

	@Autowired
	EntryIndexTempRepository entryIndexTempRepository;

	@Autowired
	BatchModifyService batchModifyService;

	@Autowired
	AlgorithmRetentionService algorithmRetentionService;

	@Autowired
	ZonesRepository zonesRepository;

	@Autowired
	MetadataLogService metadataLogService;

	@Autowired
	ElectronicService electronicService;

	@Autowired
	LogService logService;

	@Autowired
	BackCapturedocEntryRepository backCapturedocEntryRepository;

	@Autowired
	AcquisitionService acquisitionService;

	@Autowired
	ServiceMetadataRepositort serviceMetadataRepositort;

	@Autowired
	UserController userController;

	@Autowired
	EntryBookmarksRepository bookmarksRepository;

	@Value("${system.report.server}")
	private String reportServer;//报表服务

	@RequestMapping("/main")
	public String management(Model model, String isp,String taskid) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
		Object wjqxFunctionButton = JSON.toJSON(userController.getWJQXbtn());//文件权限
		model.addAttribute("wjqxFunctionButton", wjqxFunctionButton);
		model.addAttribute("functionButton", functionButton);
		model.addAttribute("mediaNodeids", dataNodeExtRepository.findMediaNodeid());
		model.addAttribute("reportServer",reportServer);
		model.addAttribute("userRealname",userDetails.getRealname());
		List<String> mediaNodeids = dataNodeExtRepository.findMediaNodeid();
		//去除空格
		for(int i = 0; i < mediaNodeids.size(); i ++) {
			mediaNodeids.set(i, mediaNodeids.get(i).trim());
		}
		model.addAttribute("mediaNodeids", mediaNodeids);
		if(taskid!=null){  //审核入库成功提醒
			// 移交节点的所有父节点
			List<String> parentNodeids = entryIndexService.getNodeidByTaskid(taskid);
			model.addAttribute("parentNodeids",parentNodeids);
		}else{
			List<String> parentNodeids = new ArrayList<>();
			model.addAttribute("parentNodeids",parentNodeids);
		}
		webSocketService.noticeRefresh(); //刷新入库成功提醒申请人
		return "/inlet/management";
	}
	@RequestMapping("/mainTotal")//打开全宗卷管理界面
	public String managementTotal(Model model, String isp) {
		Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		model.addAttribute("functionButton", functionButton);
		model.addAttribute("key",1);
		model.addAttribute("reportServer",reportServer);
		model.addAttribute("userid", userDetails.getUserid());
		model.addAttribute("showElectronicRename",showElectronicRename);
		return "/inlet/management";
	}

	@RequestMapping(value = "/entries", method = RequestMethod.GET)
	@ResponseBody
	public Page<Tb_index_detail> getEntrys(String nodeid, String type, String basicCondition, String basicOperator, String basicContent,
										   String condition, String operator, String content, String info, Tb_index_detail formConditions,
										   ExtOperators formOperators, ExtDateRangeData daterangedata, String logic, boolean ifSearchLeafNode,
										   boolean ifContainSelfNode, int page, int limit, String sort) {
		Sort sortobj = WebSort.getSortByJson(sort);
		if (type != null && type.equals("模板预览")) {
			return null;
		}
		if (info != null && "批量操作".equals(info)) {
			return entryIndexService.getEntries(nodeid, basicCondition, basicOperator, basicContent, formConditions,
					formOperators, daterangedata, logic, ifSearchLeafNode, ifContainSelfNode, page, limit, sortobj);
		} else {
			return entryIndexService.getEntries(nodeid, condition, operator, content, formConditions, formOperators,
					daterangedata, logic, ifSearchLeafNode, ifContainSelfNode, page, limit, sortobj);
		}
	}
	@RequestMapping(value = "/managementMediaEntries", method = RequestMethod.GET)
	@ResponseBody
	public Page<MediaEntry> getManagementMediaEntries(String nodeid, String basicCondition, String basicOperator,
													  String basicContent, String condition, String operator, String content, String info,
													  Tb_index_detail formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic,
													  boolean ifSearchLeafNode, boolean ifContainSelfNode, int page, int limit, String sort, String[] labels,
													  String groupid,String[] filingyear,String[] entryretention) {
		Sort sortobj = WebSort.getSortByJson(sort);
		Page<Tb_index_detail> list;
		if (info != null && "批量操作".equals(info)) {
			list = entryIndexService.getMediaEntries(nodeid, basicCondition, basicOperator, basicContent,
					formConditions, formOperators, daterangedata, logic, ifSearchLeafNode, ifContainSelfNode, page,
					limit, sortobj, labels, groupid);
		} else {
			list = entryIndexService.getMediaEntries(nodeid, condition, operator, content, formConditions,
					formOperators, daterangedata, logic, ifSearchLeafNode, ifContainSelfNode, page, limit, sortobj,
					labels, groupid,filingyear,entryretention);
		}
		List<Tb_index_detail> teiList = list.getContent();
		List<MediaEntry> eList = entryService.getMediaEntry(teiList);
		PageRequest pageRequest = new PageRequest(page - 1, limit);
		return new PageImpl<MediaEntry>(eList, pageRequest, list.getTotalElements());
	}

	@RequestMapping(value = "/entriesWg")
	@ResponseBody
	public Page<Tb_index_detail> getEntryWg(String nodeid, String type, String basicCondition, String basicOperator, String basicContent,
											String condition, String operator, String content, String info, Tb_index_detail formConditions,
											ExtOperators formOperators, ExtDateRangeData daterangedata, String logic, boolean ifSearchLeafNode,
											boolean ifContainSelfNode, int page, int limit, String sort) {

		Sort sortobj = WebSort.getSortByJson(sort);
		logic = "预归档未归";
		return entryIndexService.getEntries(nodeid, condition, operator, content, formConditions, formOperators,
				daterangedata, logic, ifSearchLeafNode, ifContainSelfNode, page, limit, sortobj);
	}

	@RequestMapping(value = "/entryIndexYgd")
	@ResponseBody
	public ExtMsg entryIndexYgd(String entryids, String nodeid, String condition, String operator, String content, String selectAll,String targetNodeid,String addType) {//添加预归档
		String uniquetag = BatchModifyService.getUniquetagByType("glgd");
		//首次加载或者切换节点要删除之前的个人数据
		if("1".equals(addType)){
			//batchModifyService.deleteEntryIndexTempByUniquetagByType("glgd");
		}else if("2".equals(addType)){//切换节点
			List<String> stringList=entryIndexTempRepository.findEntryidByUniquetag(uniquetag);//获取临时表个人数据
			if(stringList.size()>0){//获取到entryids后重新加载管理表数据到临时表
				entryids=String.join(",",stringList);
				batchModifyService.deleteEntryIndexTempByUniquetagByType("glgd");
			}
		}
		int num = entryService.entryIndexYgd(entryids, nodeid, condition, operator, content, uniquetag, selectAll,targetNodeid);
		//保存完后临时表排序
		entryIndexCaptureService.setSortsequence(nodeid, uniquetag);
		return new ExtMsg(true, "成功增加" + num + "条数据到预归档", null);
	}

	@RequestMapping(value = "/entryIndexInsertYgd")
	@ResponseBody
	public ExtMsg entryIndexInsertYgd(String entryids, String insertLine, String targetNodeid) {//插入预归档
		int num = entryService.entryIndexInsertYgd(entryids, insertLine, targetNodeid);
		return new ExtMsg(true, "成功插入" + num + "条数据到预归档", null);
	}

	@RequestMapping(value = "/entryIndexYgdDel")
	@ResponseBody
	public ExtMsg entryIndexYgdDel(String entryids, String nodeid) {//取消预归档
		String uniquetag = BatchModifyService.getUniquetagByType("glgd");
		int num = entryService.entryIndexYgdDel(entryids, uniquetag);
		//保存完后临时表排序
		entryIndexCaptureService.setSortsequence(nodeid, uniquetag);
		return new ExtMsg(true, "成功取消" + num + "条数据", null);
	}

	@RequestMapping(value = "/entriesTemp")
	@ResponseBody
	public ExtMsg entryTempEdit(@ModelAttribute("form") Entry entry, String dataNodeid) {//预归档修改
		return entryService.entryTempEdit(entry, dataNodeid);
	}

	@RequestMapping(value = "/entriesPost")
	@ResponseBody
	public Page<Tb_index_detail> getEntrysPost(String nodeid, String type, String basicCondition, String basicOperator, String basicContent,
											   String condition, String operator, String content, String info, Tb_index_detail formConditions,
											   ExtOperators formOperators, ExtDateRangeData daterangedata, String logic, boolean ifSearchLeafNode,
											   boolean ifContainSelfNode, int page, int limit, String sort) {
		Sort sortobj = WebSort.getSortByJson(sort);
		if (type != null && type.equals("模板预览")) {
			return null;
		}
		if (info != null && "批量操作".equals(info)) {
			return entryIndexService.getEntries(nodeid, basicCondition, basicOperator, basicContent, formConditions,
					formOperators, daterangedata, logic, ifSearchLeafNode, ifContainSelfNode, page, limit, sortobj);
		} else {
			return entryIndexService.getEntries(nodeid, condition, operator, content, formConditions, formOperators,
					daterangedata, logic, ifSearchLeafNode, ifContainSelfNode, page, limit, sortobj);
		}
	}

	@RequestMapping(value = "/entries/{entryid}", method = RequestMethod.GET)
	@ResponseBody
	public Object getEntry(@PathVariable String entryid,String xtType,String datasoure) {
		if("声像系统".equals(xtType)){
			return entryService.getSxEntry(entryid);
		}
		if ("capture".equals(datasoure)){
			return entryCaptureService.getEntry(entryid);
		}
		return entryService.getEntry(entryid);
	}

	@RequestMapping(value = "/entries/innerfile/{entryid}/"/*, method = RequestMethod.POST*/)
	@ResponseBody
	public Page<Tb_entry_index> getEntryInnerFile(@PathVariable String entryid, String nodeid, Integer page,
			Integer start, Integer limit, String sort) {
		logger.info("nodeid:" + nodeid + ";page:" + page + ";start:" + start + ";limt:" + limit);
		Sort sortobj = WebSort.getSortByJson(sort);
		PageRequest pageRequest = new PageRequest(page - 1, limit);
		List list = entryIndexService.findAllByNodeidAndArchivecodeLike(start, limit, nodeid, entryid, sortobj);
		return new PageImpl((List<Tb_entry_index>) list.get(1), pageRequest, (int) list.get(0));
	}

	/**
	 * 获取需归档的记录至归档预览列表
	 *
	 * @param dataNodeid 未归节点
	 * @param nodeid     归档目标节点
	 * @param entryids   选定的记录的条目ID
	 * @param ygType     ""首次加载，   "ygd"非首次加载且nodeid不变   "ygdChange"非首次加载且nodeid改变
	 */
	@RequestMapping("/entryIndexes")
	@ResponseBody
	public Page<Tb_entry_index_temp> getEntryIndexCaptures(String[] entryids, String allEntryids, String dataSource, String isSelectAll,
														   String dataNodeid, String condition, String operator,
														   String content, String type, String ygType, String nodeid, int page, int start,
														   int limit, String sort) {
		if ("true".equals(isSelectAll)) {
            /*List<String> ids = new ArrayList<>();
            List<Tb_entry_index> entryIndexList = entryIndexService.getEntryIndexList(dataNodeid, condition, operator, content);
            entryIndexList.forEach(index -> {
                ids.add(index.getEntryid());
            });
            entryids = ids.toArray(new String[ids.size()]);*/
			entryids = allEntryids.split(",");
		}
		logger.info("entryids:" + entryids + ";page:" + page + ";start:" + start + ";limt:" + limit);
		Sort sortobj = WebSort.getSortByJson(sort);
		Page<Tb_entry_index_temp> list = entryIndexService.getEntryIndex(entryids, dataSource, page, limit, sortobj, ygType, dataNodeid);
		logger.info(list.toString());
		return list;
	}

	/**
	 * 获取需调序的记录至临时调序列表
	 * 
	 * @param entryids
	 *            选定的记录的条目ID
	 */
	@RequestMapping("/sqEntryIndexes")
	@ResponseBody
	public Page<Tb_entry_index_sqtemp> getSqEntryIndexes(String entryids, String dataSource, String nodeid, int page,
			int start, int limit, String sort) {
		logger.info("entryids:" + entryids + ";page:" + page + ";start:" + start + ";limt:" + limit);
		Sort sortobj = WebSort.getSortByJson(sort);
		Page<Tb_entry_index_sqtemp> list = entryIndexService.getSqtempEntryIndex(entryids, dataSource, nodeid, page,
				limit, sortobj);
		logger.info(list.toString());
		return list;
	}

	/**
	 * 生成档号
	 * 

	 *            选定记录的条目ID
	 * @param nodeid
	 *            归档目标节点的节点ID
	 * @param filingValuesStrArr
	 *            档号设置字段的值（表单中的输入值）
	 * @param appraisaltype
	 *            自动鉴定类型（规则）
	 * @return
	 */
	@RequestMapping(value = "/generateArchivecode", method = RequestMethod.POST)
	@ResponseBody
	public ExtMsg generateArchivecode(String nodeid, String[] filingValuesStrArr,
									  String appraisaltype) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		List<Tb_entry_index_temp> result = new ArrayList<>();
		Map<String, String> entryidEntryretentionMap = new HashMap<>();
		String uniquetag = BatchModifyService.getUniquetagByType("glgd");
		List<String> entryids = entryIndexTempRepository.findEntryidByUniquetag(uniquetag);
		if (appraisaltype != null && !"".equals(appraisaltype)) {
			List<String> originalList = Arrays.asList(filingValuesStrArr);
			List<String> filingValuesStrList = new ArrayList<>();
			filingValuesStrList.addAll(originalList);

			for (String entryid : entryids) {
				//智能识别保管期限
				if ("智能识别保管期限".equals(appraisaltype)) {
					/*String entryretentionValue = algorithmRetentionService.<Tb_appraisal_type>getEntryretentionByEntryidAndAppraisaltype(entryid, appraisaltype, "数据管理", entryIndexRepository, appraisalTypeRepository);
					entryidEntryretentionMap.put(entryid, entryretentionValue);*/
				} else {
					String entryretentionValue = appraisalStandardService
							.getEntryretentionByEntryidAndAppraisaltype(entryid, appraisaltype, "数据管理");
					entryidEntryretentionMap.put(entryid.trim(), entryretentionValue);
				}
			}
		}
		boolean value = false;
		List<String> codeSettingFieldList = codesettingService.getCodeSettingFields(nodeid);
		if (filingValuesStrArr.length < codeSettingFieldList.size()) {
			value = true;
		}
		Object entry = entryIndexService.generateArchivecode(String.join(",", entryids), nodeid, filingValuesStrArr,
				entryidEntryretentionMap, uniquetag);
		if (!entry.getClass().toString().equals("class java.lang.String")) {
			result = (List<Tb_entry_index_temp>) entry;
			if (result != null && result.size() > 0) {
				if (value) {
					return new ExtMsg(true, "需要调整计算项值", result);// 此处生成的档号需进一步调整计算项值
				}
				return new ExtMsg(true, "不需要调整计算项值", result);
			}
		}
		return new ExtMsg(false, entry.toString() + "档号重复，请重新设置档号信息。", null);
	}

	@RequestMapping("/getCalculation")
	@ResponseBody
	public ExtMsg getCalculation(String nodeid) {
		List<String> info = codesetRepository.findFieldcodeByDatanodeid(nodeid);
		if (info.size() > 0) {
			return new ExtMsg(true, "成功", info.get(info.size() - 1));
		}
		return new ExtMsg(false, "获取计算值失败", null);
	}

	@RequestMapping(value = "/ajustAllCalData", method = RequestMethod.POST)
	@ResponseBody
	public ExtMsg ajustAllCalData(String info, String nodeid) {//生成档号
		String uniquetag = BatchModifyService.getUniquetagByType("glgd");
		List<Tb_entry_index_temp> result = entryIndexTempService.ajustAllCalData(uniquetag, info, "数据管理", nodeid);
		if (result != null && result.size() > 0) {
			return new ExtMsg(true, "生成档号成功", result);
		}
		return new ExtMsg(false, "生成档号失败", null);
	}

	/**
	 * 文件归档
	 *
	 * @param entryids 选定记录的条目ID
	 * @return
	 */
	// @LogAnnotation(module = "数据采集",sites = "1,2",startDesc =
	// "归档操作，条目id为：",connect = ",目标节点id为：")
	@RequestMapping("/entryIndexes/filing")
	@ResponseBody
	public ExtMsg filingEntryIndexes(String entryids, String nodeid) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String[] entryidArr = entryids.split(",");//将前端传过来的entryid字符串切割成字符数组做批量处理
		String nameChange="";//重命名标志
		if ("true".equals(flag)) {//设置了重命名电子文件则执行以下代码
			nameChange="true";
		}
		List<Tb_entry_detail> result = entryIndexService.filingEntryIndex(entryids, userDetails.getUserid());
		if (result != null && result.size() > 0) {
			String[] gdEntryidArray = GainField.getFieldValues(result, "entryid").length == 0 ? new String[] { "" }
					: GainField.getFieldValues(result, "entryid");
			//--edit by Leo---start	存放title进alg_rention表
			//algorithmRetentionService.AddWordInThread(result, Tb_entry_index.class);
			//--edit by Leo---end
			SolidifyThread solidifyThread = new SolidifyThread(gdEntryidArray, "management",nameChange);// 开启固化线程
			solidifyThread.start();
			////增加日志记录
			String ipAddress = LogAop.getIpAddress();
			LogThread logThread = new LogThread(gdEntryidArray, nodeid, "数据管理", userDetails.getLoginname(), userDetails.getRealname(), ipAddress);// 开启日志线程
			logThread.start();
			return new ExtMsg(true, "保存成功", result);
		}
		return new ExtMsg(false, "保存失败", null);
	}

	@RequestMapping("/checkTempArchivecode")
	@ResponseBody
	public ExtMsg checkTempArchivecode(String nodeid) {//判断所有预归档个人条目有档号未生成
		String uniquetag=BatchModifyService.getUniquetagByType("glgd");
		List<String> result = entryIndexTempRepository.checkTempArchivecode(nodeid,uniquetag);
		String msg="0";
		if(result.size()>0){//还有档号未设置
			msg="1";
		}
		return new ExtMsg(false, msg, null);
	}

	@RequestMapping("/getTempSize")
	@ResponseBody
	public ExtMsg getTempSize() {//判断所有预归档个人条目有档号未生成
		String uniquetag=BatchModifyService.getUniquetagByType("glgd");
		List<Tb_entry_index_temp> result = entryIndexTempRepository.findByUniquetagOrderBySortsequence(uniquetag);
		String msg="0";
		if(result.size()>0){//还有档号未设置
			msg=result.size()+"";
		}
		return new ExtMsg(false, msg, null);
	}

	@PostMapping("/getParam")
	@ResponseBody
	public ExtMsg getParam (@RequestParam("param") String param){
		flag = param;
		return new ExtMsg(true, "ok", null);
	}

	private boolean isExist(String entryCode, List<String> archivecode) {
		for (int i = 0; i < archivecode.size(); i++) {
			String code = archivecode.get(i);
			// 如果档号存在(传过来的档号在节点当中已经存在)
			if (code != null && entryCode.equals(code)) {
				return true;
			}
		}
		return false;
	}

	private boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public Tb_entry_index alignArchivecode(Tb_entry_index entry, String operate) {
		String nodeid = entry.getNodeid();
		// 处理需对齐字段
		List<String> alignFieldList = new ArrayList<>();
		List<String> codeSettingFields = new ArrayList<>();
		List<String> codeSettingSplits = new ArrayList<>();
		List<Tb_codeset> codesetList = codesettingService.findCodesetByDatanodeid(nodeid);
		codesetList.forEach(codeset -> {
			codeSettingFields.add(codeset.getFieldcode());
			codeSettingSplits.add(codeset.getSplitcode());
			alignFieldList.add(codeset.getFieldcode() + "∪" + codeset.getFieldlength());
		});
		if (codeSettingSplits.size() > 1) {
			codeSettingSplits.remove(codeSettingSplits.size() - 1);
		}
		// 执行对齐操作
		String archivecode = "";
		for (int i = 0; i < alignFieldList.size(); i++) {// 档号构成字段值补0
			String alignField = alignFieldList.get(i);
			String[] alignFieldStrs = alignField.split("∪");
			String alignFieldcode = alignFieldStrs[0];
			Integer alignFieldlength = Integer.parseInt(alignFieldStrs[1]);// 档号设置的单位长度
			String alignFieldValue = GainField.getFieldValueByName(alignFieldcode, entry) != null
					? (String) GainField.getFieldValueByName(alignFieldcode, entry) : "";
			String alignedFieldValue = "";
			if ("".equals(alignFieldValue) || alignFieldValue == null) {
				return entry;
			}
			if (isNumeric(alignFieldValue)) {
				int currentFieldlength = alignFieldValue.length();// 字段值当前的长度
				if (alignFieldlength != currentFieldlength && alignFieldValue.length() > 0) {
					alignedFieldValue = entryIndexService.alignValue(alignFieldlength,
							Integer.valueOf(alignFieldValue));
					GainField.setFieldValueByName(alignFieldcode, entry, alignedFieldValue);
					// GainField.setFieldValueByName(alignFieldcode,
					// entry.getEntryIndex(), alignedFieldValue);
				}
			}
		}
		for (int i = 0; i < codeSettingFields.size() - 1; i++) {// 重新生成档号
			String field = codeSettingFields.get(i);
			String codeSettingFieldValue = GainField.getFieldValueByName(codeSettingFields.get(i), entry) != null
					? (String) GainField.getFieldValueByName(codeSettingFields.get(i), entry) : "";
			if ("".equals(codeSettingFieldValue)) {
				throw new RuntimeException("档号构成字段值为空");
			} else {
				// 如果是机构名称
				String type = templateRepository.findOrganFtypeByNodeid(nodeid);
				if (field.equals("organ") && type.equals("string") && type != null) {
					Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
					Tb_right_organ right_organ = rightOrganRepository.findByOrganid(node.getRefid());
					if (right_organ.getCode() != null && !right_organ.getCode().equals("")) {
						archivecode += right_organ.getCode() + codeSettingSplits.get(i);
					} else {
						archivecode += codeSettingFieldValue + codeSettingSplits.get(i);
					}
				} else if (field.equals("entryretention")) {
					List<String> list = systemConfigRepository.findConfigvalueByConfigcode(codeSettingFieldValue);
					if (list.size() == 0) {
						archivecode += codeSettingFieldValue + codeSettingSplits.get(i);
					} else {
						archivecode += list.get(0) + codeSettingSplits.get(i);
					}
				} else {
					archivecode += codeSettingFieldValue + codeSettingSplits.get(i);
				}
			}
		}
		String calFieldvalue = "";
		if (codeSettingFields.size() >= 1) {
			String calFieldcode = codeSettingFields.get(codeSettingFields.size() - 1);
			calFieldvalue = GainField.getFieldValueByName(calFieldcode, entry) != null
					? (String) GainField.getFieldValueByName(calFieldcode, entry) : "";
			if ("".equals(calFieldvalue) && !operate.equals("未归管理")) {
				throw new RuntimeException("计算项字段值为空");
			}
		}
		archivecode += calFieldvalue;
		entry.setArchivecode(archivecode);
		// entry.getEntryIndex().setArchivecode(archivecode);
		return entry;
	}

	/**
	 *
	 * @param isCompilationManageSystem 请求是否为编研管理系统,因为编研管理系统要默认把开放状态变成编研开放
	 */
	@LogAnnotation(module = "数据管理", sites = "1", fields = "title,archivecode", connect = "##题名；,##档号；", startDesc = "保存操作，条目详情：")
	@RequestMapping(value = "/entries", method = RequestMethod.POST)
	@ResponseBody
	public ExtMsg saveEntry(@ModelAttribute("form") Entry entry, String type, String operate,boolean isCompilationManageSystem,Boolean isMedia) {
		entry.setEntryIndex(entry.getRawEntryIndex());
		entry.setEntryDetial(entry.getRawEntryDetail());
		Tb_data_node node = entryIndexService.getNodeLevel(entry.getNodeid());
		String code = alignArchivecode(entry.getEntryIndex(), operate).getArchivecode();
		if (code!=null && !code.isEmpty()) {// 如果档号不为空
			// 查询当前节点所有数据的档号,判断档号的唯一性
			List<String> archivecode = entryIndexRepository.findCodeByNodeid(entry.getNodeid());
			if (archivecode.size() > 0) {
				if (type.equals("add") && isExist(code, archivecode)) {
					return new ExtMsg(false, "保存失败，档号重复！", null);
				}
				if (type.equals("modify")) {
					Tb_entry_index entryIndex = entryIndexRepository.findByEntryid(entry.getEntryid());
					// 如果修改了档号
					if (entryIndex.getArchivecode() != null && !code.equals(entryIndex.getArchivecode())
							&& isExist(code, archivecode)) {
						return new ExtMsg(false, "保存失败，档号重复！", null);
					}
				}
			}
		} else {
			// 如果档号为空且非未归管理
			if (!node.getNodename().equals("未归管理") && !node.getNodename().equals("编研采集") && node.getNodename().equals("文件管理") && node.getNodename().equals("资料管理")) {
				return new ExtMsg(false, "保存失败，档号为空", null);
			}
		}
		//Entry result = entryService.saveEntry(entry, type,isCompilationManageSystem);
		String entryid = entry.getEntryid();
		Entry result = entryService.saveEntry(entry, type,isCompilationManageSystem,isMedia);
		if(entryid==null || "".equals(entryid)){
			metadataLogService.save(new Tb_metadata_log("数据管理著录", "著录", result.getEntryIndex().getEntryid()));//插入档案元数据日志
		}
		return new ExtMsg(result != null ? true : false, result != null ? "保存成功" : "保存失败,到期鉴定时间不为空，不可修改保管期限！", result);
	}

	@RequestMapping(value = "/batchUploadSave/{entrytype}/{filename}", method = RequestMethod.POST)
	@ResponseBody
	public ExtMsg batchUpload(@PathVariable String entrytype,String currentMD5, boolean isMedia,@PathVariable String filename,
							  EntryCapture entry,String type,String operate,String lastModifiedDate, String addHistory,
							  String setcode,String[] filenameLab) {
		try {
			List<Object> list = electronicService.saveElectronic(entrytype, null, filename, isMedia,currentMD5);
			Tb_electronic electronic_capture = (Tb_electronic)list.get(2);
			Tb_electronic_browse eb = (Tb_electronic_browse) list.get(1);
			//更新上传文件源数据
			ExtMsg extMsg = electronicService.saveMetadataByTb_electronic(electronic_capture);
			Tb_entry_detail_capture detail_capture = (Tb_entry_detail_capture)extMsg.getData();
			String filedate = extMsg.getMsg();
			entry.setEleid(electronic_capture.getEleid());
			if(addHistory!=null&&"addHistory".equals(addHistory)){
				entry.setFiledate(filedate);
			}
			entry.setEntryDetial(detail_capture);
//			//档号最后一个组成字段根据上传的位置进行设置档号
//			String setcodeValue = GainField.getFieldValueByName(setcode, entry) != null
//					? (String) GainField.getFieldValueByName(setcode, entry) : "1";//setcodeValue为""会发生异常
//			int setcodeValueInt = Integer.parseInt(setcodeValue)+count;
//			String str = String.format("%"+setcodeValue.length()+"d", setcodeValueInt).replace(" ", "0");
//			GainField.setFieldValueByName(setcode,entry,str);

			if (isMedia) {
				int mediaNum = FileUtil.getMediaNumByFilename(filename);
//                electronicService.compression(eb, mediaNum, currentMD5,entry,type,operate,isMedia,"saveEntry",filenameLab);// 压缩
			}

		}catch (Exception e){
			e.printStackTrace();
		}
		return new ExtMsg(true,"",null);
	}
	
	@RequestMapping(value = "/updateEntryretention", method = RequestMethod.POST)
	@ResponseBody
	public ExtMsg updateEntryretention(String[] billids, String entryretention) {
		Integer value = 0;
		List<String> entryids=new ArrayList<>();
		List<String[]> bidAry = new InformService().subArray(billids, 1000);// 处理ORACLE1000参数问题
		for(String[] ary : bidAry){
			entryids= billEntryIndexRepository.findEntryidByBillidIn(billids);
			billEntryIndexRepository.updateEntryIndex(entryretention, entryids.toArray(new String[entryids.size()]));
			value =entryIndexRepository.updateEntryIndex(entryretention, entryids.toArray(new String[entryids.size()]));//修改实际条目 保管期限
		}
		if (value > 0)
			return new ExtMsg(true, "变更保管期限成功！", null);
		return new ExtMsg(false, "变更保管期限失败！", null);
	}

	/**
	 * 获取案卷or卷内著录的初始数据
	 * 
	 * @param nodeid
	 * @param entryid
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "/getDefaultInfo", method = RequestMethod.POST)
	@ResponseBody
	public ExtMsg getDefaultInfo(String nodeid, String entryid, String type) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		EntryBase entry = new EntryBase();
		List<Tb_data_template> templates = templateRepository.findByNodeid(nodeid);// 查找到当前节点的模板信息
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String organid = entryIndexService.getOrganidByNodeidLoop(nodeid);// 机构id
		List<String> codeInfo = new ArrayList<>();
		codeInfo.add("filecode");// 案卷号
		codeInfo.add("catalog");// 目录号
		codeInfo.add("responsible");// 责任者
		//优化查询速率
		Entry entryclass = entryService.getEntry(entryid);
		// 卷内著录 - 需要获取案卷的档号组成信息
		List<String> codesets = codesetRepository.findFieldcodeByDatanodeid(nodeid);
		String year = templateRepository.findFdefaultByFieldcodeAndNodeid("filingyear", nodeid);
		String organ = organService.findOrganByOrganid(organid);
		String defaultFunds = templateRepository.findFdefaultByFieldcodeAndNodeid("funds", nodeid);
		String funds = fundsService.getOrganFunds(organid);
		String unitOrganid = entryIndexService.getOrganInfo(organid);
		String unitFunds = fundsService.getOrganFunds(unitOrganid);
		for (int i = 0; i < templates.size(); i++) {
			Tb_data_template template = templates.get(i);
			if (type.equals("卷内著录")) {
				codeInfo.removeAll(codesets);
				codesets.addAll(codeInfo);
				for (int j = 0; j < codesets.size(); j++) {
					String value = (String) GainField.getFieldValueByName(codesets.get(j), entryclass);
					GainField.setFieldValueByName(codesets.get(j), entry, value);
				}
			}
			if (template.getFieldcode().equals("filingyear")) {// 归档年度
				if (year != null && !"".equals(year)) {
					entry.setFilingyear(year);
				} else {
					entry.setFilingyear(String.valueOf(cal.get(Calendar.YEAR)));
				}
			} else if (template.getFieldcode().equals("descriptiondate")) {// 著录时间
				entry.setDescriptiondate(df.format(System.currentTimeMillis()));
			} else if (template.getFieldcode().equals("descriptionuser")) {// 著录人
				entry.setDescriptionuser(userDetails.getRealname());
			} else if (template.getFieldcode().equals("organ")) {// 机构
				entry.setOrgan(organ == null ? "" : organ);
			} else if (template.getFieldcode().equals("funds")) {// 全宗号
				if (defaultFunds != null && !"".equals(defaultFunds)) {
					entry.setFunds(defaultFunds);
				} else {
					if (funds != null) {// 如果是单位机构的话,直接填充获取到的全宗号
						entry.setFunds(funds);
					} else {// 如果是部门机构的话,需要获取到所属单位的全宗号
						entry.setFunds(unitFunds == null ? "" : unitFunds);
					}
				}
			} else {
				// 如果模板当中的默认值不为空的话,卷内著录or案卷著录时填充模板默认值
				if (template.getFdefault() != null && !template.getFdefault().equals("")) {
					GainField.setFieldValueByName(template.getFieldcode(), entry, template.getFdefault());
				}
			}
		}
		return new ExtMsg(true, "获取初始值成功", entry);
	}

    @RequestMapping(value = "/getDefaultInfos", method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg getDefaultInfos(String nodeid, String entryid, String type) {
        EntryBase entry = new EntryBase();
        List<Tb_data_template> templates = templateRepository.findByNodeid(nodeid);// 查找到当前节点的模板信息
        String organid = entryIndexService.getOrganidByNodeidLoop(nodeid);// 机构id
        String organ = organService.findOrganByOrganid(organid);
        String defaultFunds = templateRepository.findFdefaultByFieldcodeAndNodeid("funds", nodeid);
        String funds = fundsService.getOrganFunds(organid);
        String unitOrganid = entryIndexService.getOrganInfo(organid);
        String unitFunds = fundsService.getOrganFunds(unitOrganid);
        for (int i = 0; i < templates.size(); i++) {
            Tb_data_template template = templates.get(i);
            if (template.getFieldcode().equals("organ")) {// 机构
                entry.setOrgan(organ == null ? "" : organ);
            } else if (template.getFieldcode().equals("funds")) {// 全宗号
                if (defaultFunds != null && !"".equals(defaultFunds)) {
                    entry.setFunds(defaultFunds);
                } else {
                    if (funds != null) {// 如果是单位机构的话,直接填充获取到的全宗号
                        entry.setFunds(funds);
                    } else {// 如果是部门机构的话,需要获取到所属单位的全宗号
                        entry.setFunds(unitFunds == null ? "" : unitFunds);
                    }
                }
            }
        }
        return new ExtMsg(true, "获取初始值成功", entry);
    }

	@RequestMapping(value = "/entries", method = RequestMethod.PATCH)
	@ResponseBody
	public Tb_entry_detail modifyEntry(String entryid) {
		return null;
	}

	/**
	 * 页数矫正
	 * 
	 * @param entryids
	 * @return
	 */
	@RequestMapping("/pgNumCorrect")
	@ResponseBody
	public ExtMsg pgNumCorrect(String entryids, String isSelectAll, String nodeid, String condition, String operator,
			String content) {
		String[] entryidData;
		String startTime = LogAop.getCurrentSystemTime();// 开始时间
		long startMillis = System.currentTimeMillis();// 开始毫秒数
		String info;
		if ("true".equals(isSelectAll)) {
			// 当状态是全选的时候，entryids为取消选择的条目
			String ids = "";
			List<Tb_entry_index> entryIndexList = entryIndexService.getEntryIndexList(nodeid, condition, operator,
					content);
			for (int i = 0; i < entryIndexList.size(); i++) {
				if (entryids.indexOf(entryIndexList.get(i).getEntryid()) == -1) {
					if (i == entryIndexList.size() - 1) {
						ids += entryIndexList.get(i).getEntryid();
					} else {
						ids += entryIndexList.get(i).getEntryid() + "、";
					}
				}
			}
			entryidData = ids.split("、");
			info = entryIndexService.pgNumCorrect(ids);
		} else {
			entryidData = entryids.split("、");// 1.所选数据
			info = entryIndexService.pgNumCorrect(entryids);
		}
		for (String entryid : entryidData) {
			if (info.indexOf(entryid) == -1) {
				logAop.generateManualLog(startTime, LogAop.getCurrentSystemTime(),
						System.currentTimeMillis() - startMillis, "数据管理", "页数矫正，条目id为：" + entryid);
			}
		}
		if ("".equals(info)) {
			return new ExtMsg(true, "操作成功", null);
		} else {
			return new ExtMsg(false, info, null);
		}
	}

	@RequestMapping("/getArchivecodeValue")
	@ResponseBody
	public ExtMsg getArchivecodeValue(String nodeid, String entryids) {
		if (nodeid != null && !nodeid.equals("") && entryids != null && !entryids.equals("")) {
			String uniquetag = BatchModifyService.getUniquetag();
			String[] entryList = entryids.split("∪");
			// 1.首先判断当前临时调序表中新档号是否重复
			List<String> archiveList = entryIndexSqTempRepository.findNewarchivecodeByEntryidInAndUniquetag(entryList,
					uniquetag);
			String repeatArchive = "";
			for (int i = 0; i < archiveList.size() - 1; i++) {
				for (int j = i + 1; j < archiveList.size(); j++) {
					if (archiveList.get(i).equals(archiveList.get(j))) {
						if (repeatArchive.equals("")) {
							repeatArchive = archiveList.get(i);
						} else {
							repeatArchive += "、" + archiveList.get(i);
						}
					}
				}
			}
			if (!repeatArchive.equals("")) {
				return new ExtMsg(false, "档号重复", repeatArchive);
			}
			// 2.判断当前临时调序表中的新档号是否与数据采集表中的档号重复
			List<Tb_entry_index_sqtemp> sqtemps = entryIndexSqTempRepository.findByUniquetagAndEntryidIn(uniquetag,
					entryList);// 找到所有临时调序条目的信息
			List<Tb_entry_index> entry_indexs = entryIndexRepository.findInfoByNodeid(nodeid);// 找到当前节点所有条目信息
			String repeat = "";
			for (int j = 0; j < entry_indexs.size(); j++) {
				Tb_entry_index index = entry_indexs.get(j);
				for (int i = 0; i < sqtemps.size(); i++) {
					Tb_entry_index_sqtemp sqtemp = sqtemps.get(i);
					// 如果档号相同且entryid不同,相同档号的数据采集条目不在临时调序表中的话
					if (sqtemp.getNewarchivecode().equals(index.getArchivecode())
							&& !sqtemp.getEntryid().equals(index.getEntryid())
							&& !Arrays.asList(entryList).contains(index.getEntryid())) {
						if (repeat.equals("")) {
							repeat = sqtemp.getNewarchivecode();
						} else {
							repeat += "、" + sqtemp.getNewarchivecode();
						}
					}
				}
			}
			if (!repeat.equals("")) {
				return new ExtMsg(false, "档号重复", repeat);
			} else {
				return new ExtMsg(true, "无重复档号", null);
			}
		}
		return new ExtMsg(false, "参数错误", null);
	}

	/**
	 * 判断是否修改了数据
	 * 
	 * @param entries
	 * @return
	 */
	@RequestMapping("/changeState")
	@ResponseBody
	public ExtMsg changeState(String entries) {
		if (!"".equals(entries) && entries != null) {
			String uniquetag = BatchModifyService.getUniquetag();
			String[] idList = entries.split("∪");
			List<Tb_entry_index_sqtemp> sqtemps = entryIndexSqTempRepository.findByUniquetagAndEntryidIn(uniquetag,
					idList);
			List<Tb_entry_index> indexs = entryIndexRepository.findByEntryidIn(idList);
			for (int i = 0; i < sqtemps.size(); i++) {
				Tb_entry_index_sqtemp sqtemp = sqtemps.get(i);
				Tb_entry_index index = indexs.get(i);
				if (!sqtemp.getNewarchivecode().equals(index.getArchivecode())) {
					return new ExtMsg(true, "数据修改", null);
				}
			}
		}
		return new ExtMsg(false, "未进行数据修改", null);
	}

	/**
	 * 保存临时调序表单到数据采集条目表中
	 * 
	 * @param nodeid
	 * @return
	 */
	@RequestMapping("/saveSqtemp")
	@ResponseBody
	public void saveSqtemp(String nodeid) {
		String uniquetag = BatchModifyService.getUniquetag();// 获取当前登录用户的特殊标记
		List<Tb_entry_index_sqtemp> sqtemps = entryIndexSqTempRepository.findByNodeidAndUniquetag(nodeid, uniquetag);
		List<String> codeSet = codesetRepository.findFieldcodeByDatanodeid(nodeid);
		String fieldName = codeSet.get(codeSet.size() - 1);
		for (int i = 0; i < sqtemps.size(); i++) {
			Tb_entry_index_sqtemp sqtemp = sqtemps.get(i);
			Tb_entry_index entry = entryIndexRepository.findByEntryid(sqtemp.getEntryid());
			if (entry != null) {// 如果数据采集表中还存在当前条目
				// 更新数据采集表中对应条目的卷内顺序号&档号&页号&页数
				GainField.setFieldValueByName(fieldName, entry, sqtemp.getCalvalue());
				entryIndexRepository.updateInfoByEntryid(sqtemp.getNewarchivecode(),
						sqtemp.getPageno(), sqtemp.getPages(), entry.getEntryid());
			}
		}
	}

	/**
	 * 统计项更新
	 * 
	 * @param entryids
	 * @return
	 */
	@RequestMapping("/statisticUpdate")
	@ResponseBody
	public ExtMsg statisticUpdate(@RequestParam String entryids) {
		String info = "";
		String[] entryidData = entryids.split(",");// 1.所选数据
		String startTime = LogAop.getCurrentSystemTime();// 开始时间
		long startMillis = System.currentTimeMillis();// 开始毫秒数
		if (entryids != null && !"".equals(entryids)) {
			// 执行页数矫正操作
			logger.info("=================开始进行统计项更新(数据管理)==========================\n" + entryids);
			info = entryIndexService.statisticUpdate(entryidData);
			logger.info(entryidData.toString());
		} else {
			info = "操作失败，未选择操作记录!";
		}
		if (info.indexOf("操作成功!") != -1) {
			for (String entryid : entryidData) {
				logAop.generateManualLog(startTime, LogAop.getCurrentSystemTime(),
						System.currentTimeMillis() - startMillis, "数据采集", "统计项更新，条目id为：" + entryid);
			}
			return new ExtMsg(true, info, null);
		}
		return new ExtMsg(false, info, null);
	}

	@RequestMapping("/delete")
	@ResponseBody
	public ExtMsg delEntry(String entryids, String isSelectAll, String nodeid, String condition, String operator,
			String content, Tb_entry_index formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata,
			String logic, boolean ifSearchLeafNode, boolean ifContainSelfNode,String model) {
		String[] entryInfo = entryids.split(",");
		String[] entryidData;
		if ("true".equals(isSelectAll)) {
			List<Tb_entry_index> entry_indexList = entryIndexService.getEntryList(nodeid, condition, operator, content,
					formConditions, formOperators, daterangedata, logic, ifSearchLeafNode, ifContainSelfNode);
			if (entry_indexList.size() > 0) {
				List<String> tempEntry = new ArrayList<>();
				List<String> entryList = Arrays.asList(entryInfo);
				for (int i = 0; i < entry_indexList.size(); i++) {
					String entryid = entry_indexList.get(i).getEntryid();
					if (!entryList.contains(entryid)) {
						tempEntry.add(entryid);
					}
				}
				entryidData = tempEntry.toArray(new String[tempEntry.size()]);
			} else {
				entryidData = new String[] {};
			}
		}else {
			entryidData = entryInfo;
		}
		List<String> titlelist = new ArrayList<>();
		List<String[]> subAry = new InformService().subArray(entryidData, 1000);// 处理ORACLE1000参数问题
		boolean flag = false;
		for (String[] ary : subAry) {
			List<Tb_entry_index> entry_indexList = entryIndexRepository.findEntryByBorrowmsg(ary);
			if (entry_indexList!=null) {
				for (int i=0;i<entry_indexList.size();i++) {
					titlelist.add(entry_indexList.get(i).getTitle());
					flag = true;
				}
			}
		}
		if (flag) {
			return new ExtMsg(false, "无法删除", titlelist);
		} else {
			Integer dels = 0;
			for (String[] ary : subAry) {
				dels = entryService.delEntryOnly(ary);
				serviceMetadataRepositort.deleteByEntryids(ary);
			}
			acquisitionController.delTransWriteLog(entryidData, "数据管理", "删除数据");// 写日志
			if (dels > 0) {
				//删除条目成功后，再启用线程默默的删除关联的电子文件等
				DelThread delThread = new DelThread(entryidData,"数据管理");// 开启线程
				delThread.start();
				return new ExtMsg(true, "删除成功", dels);
			}
			return new ExtMsg(false, "删除失败", null);
		}
	}

	/**
	 *
	 * @param entryids
	 *            需调整保管期限记录的条目id
	 * @param entryretention
	 *            调整后的保管期限值
	 * @param nodeid
	 *            归档目标节点id
	 * @return
	 */
	@RequestMapping("/retentionAjust")
	@ResponseBody
	public ExtMsg retentionAjust(String entryids, String entryretention, String nodeid, String type) {
		List<Tb_entry_index_temp> result = entryIndexTempService.retentionAjust(entryids.split(","), entryretention,
				nodeid, 0, type);
		if (result == null) {
			return new ExtMsg(false, "请先生成档号！", null);
		} else if (result.size() > 0) {
			return new ExtMsg(true, "保管期限调整成功", result);
		}
		return new ExtMsg(false, "保管期限调整失败", null);
	}

	/**
	 * 获取计算项字段名或字段名及数值
	 * 
	 * @param entryIndex
	 * @param nodename
	 * @return
	 */
	@RequestMapping("/getCalValue")
	@ResponseBody
	public ExtMsg getCalValue(Tb_entry_index entryIndex, String nodeid, String nodename, String docid, String type) {
		List<Tb_codeset> codeSettingList = codesetRepository.findByDatanodeidOrderByOrdernum(nodeid);// 获取档号设置集合
		if (codeSettingList.size() == 0 && !nodename.equals("未归管理")) {// 档号字段未设置
			return new ExtMsg(false, "请检查档号设置信息是否正确", null);
		}
		String calFieldName = "";
		String archiveCode = "";
		Integer number = 0;
		//String value = templateService.getFieldName(nodeid);
		String value = "";
		for(Tb_codeset codeset:codeSettingList){
			if("".equals(value)){
				value=codeset.getFieldname();
			}else{
				value+="、"+codeset.getFieldname();
			}
		}
		if (codeSettingList.size() >= 1) {
			Integer size = codeSettingList.size() - 1;
			calFieldName = codeSettingList.get(size).getFieldcode();// 动态获取计算项字段名
			// 获取计算项单位长度
			number = (int)codeSettingList.get(size).getFieldlength();
			// if (number == null || number == 0) {
			// return new ExtMsg(false, "请检查计算项单位长度是否设置正确", null);
			// }
			String calValueStr = "";
			String codeSettingFieldValues = "";//还没有拼接统计项的档号字段
			if (!GainField.objectIsNull(entryIndex, 0) || codeSettingList.size() == 1) {
				Integer calValue = null;
				try {
					calValue = entryIndexService.getCalValue(entryIndex, nodeid, codeSettingList, type);
				} catch (NumberFormatException e) {
					return new ExtMsg(false, "获取档号失败，请检查档号构成字段（" + value + "）是否包含非数字字符", null);
				}
				if (calValue == null) {
					return new ExtMsg(false, "获取档号失败，请检查档号构成字段（" + value + "）输入值是否为空。", null);
				}
				// 将计算项数值补0到指定位数，若calValue为null,且number数值大于4,则生成的字符串为：空格+"null",需去除空格
				calValueStr = entryIndexService.alignValue(number, calValue);
				GainField.setFieldValueByName(calFieldName, entryIndex,calValueStr);
				//archiveCode = alignArchivecode(entryIndex, "数据管理").getArchivecode();
				if(codeSettingList.size()==1){
					archiveCode = calValueStr;
				}else{
					archiveCode = entryIndex.getArchivecode()+codeSettingList.get(size-1).getSplitcode()+calValueStr;
				}
				if (docid != null) {
					//查找到当前修改的条目所在的移交单据组的所有数据
					List<String> entryIds = transdocEntryRepository.findEntryidByDocid(docid);
					List<Tb_entry_index_capture> captures = entryIndexCaptureRepository.findByEntryidIn(entryIds.toArray(new String[entryIds.size()]));
					for (int i = 0; i < captures.size(); i++) {
						Tb_entry_index_capture capture = captures.get(i);
						if (archiveCode.equals(capture.getArchivecode())) {
							calValue = entryIndexCaptureService.getCalValue(capture, nodeid, codeSettingList,"");
							calValueStr = entryIndexService.alignValue(number, calValue);
							GainField.setFieldValueByName(calFieldName, entryIndex, calValueStr);
							//archiveCode = alignArchivecode(entryIndex, "数据管理").getArchivecode();
							if(codeSettingList.size()==1){
								archiveCode = calValueStr;
							}else{
								archiveCode = entryIndex.getArchivecode()+codeSettingList.get(size-1).getSplitcode()+calValueStr;
							}
						}
					}
				}
				GainField.setFieldValueByName("archivecode", entryIndex, archiveCode);
			} else {// 表单值未传入
				if (!"null".equals(calFieldName) && !"".equals(calFieldName)) {// 若表单值未传入，且获取到的字段名不为空，则返回字段名
					return new ExtMsg(true, "获取计算项字段名成功", calFieldName);
				}
			}
			Map<String, String> result = new HashMap<String, String>();
			result.put("calFieldName", calFieldName);
			result.put("calValueStr", calValueStr);
			result.put("archive", archiveCode);
			if (!"null".equals(calValueStr) && !"".equals(calValueStr) && !"null".equals(calFieldName)
					&& !"".equals(calFieldName)) {
				return new ExtMsg(true, "获取计算项字段名及数值成功", result);
			}
		}
		if (!nodename.equals("未归管理")) {
			return new ExtMsg(false, "获取档号失败，请检查档号构成字段（" + value + "）输入值是否为空。", null);
		}
		return null;
	}

	/**
	 * 卷内文件拖拽排序
	 * 
	 * @param entryid
	 * @param targetorder
	 * @param filearchivecode
	 * @return
	 */
	@RequestMapping("/order/{entryid}/{targetorder}/{filearchivecode}")
	@ResponseBody
	public ExtMsg modifyJnOrder(@PathVariable String entryid, @PathVariable String targetorder,
			@PathVariable String filearchivecode) {
		String nodeid = entryIndexService.findNodeidByEntryid(entryid);
		Tb_entry_index entryIndex = entryIndexService.findEntryIndex(entryid);
		ExtMsg result = entryIndexService.modifyJnEntryindexOrder(entryIndex, Integer.parseInt(targetorder),
				filearchivecode, nodeid);
		if (result != null) {
			if (!result.isSuccess()) {
				return result;
			}
		}
		return new ExtMsg(true, "顺序修改成功", null);
	}

	/**
	 * 更新由于拆件或插件引起的后续条目数据变化
	 * 
	 * @param entryid
	 *            选择拆插件的条目
	 * @param flag
	 *            标识操作类型为拆件或插件
	 * @param pages
	 *            插件操作时，额外传递的页数值（拆件操作时直接从后台获取页数，此处接收值为null）
	 * @return
	 */
	@RequestMapping("/updateSubsequentData")
	@ResponseBody
	public ExtMsg updateSubsequentData(String entryid, String flag, String pages) {
		Tb_entry_index entryIndex = entryIndexRepository.findByEntryid(entryid);
		String nodeid = entryIndex.getNodeid();
		List<String> codeSettingFieldList = codesettingService.getCodeSettingFields(nodeid);// 获取档号设置字段集合
		if (codeSettingFieldList.size() == 0) {// 档号字段未设置
			return new ExtMsg(false, "条目保存成功，但插件后续更新统计项及档号操作异常，请检查档号设置信息是否正确", null);
		}
		try {
			entryIndexService.updateSubsequentData(entryIndex, codeSettingFieldList, flag, pages);
		} catch (NumberFormatException e) {
			return new ExtMsg(false, "条目保存成功，但插件后续更新统计项及档号操作异常，请检查历史计算项或页号输入值是否包含非数字字符", null);
		}
		return new ExtMsg(true, "更新数据成功", null);
	}

	/**
	 * 根据卷内文件的nodeid获取其对应的案卷nodeid
	 * 
	 * @param nodeid
	 * @return
	 */
	@RequestMapping("/newFileNodeidAndEntryid")
	@ResponseBody
	public Map<String, Object> newFileNodeidAndEntryid(String nodeid, String entryid) {
		return entryIndexService.getFileNodeidAndEntryid(nodeid, entryid);
	}

	@RequestMapping("/getSelectAllEntryid")
	@ResponseBody
	public String[] getSelectAllEntryid(String nodeid, String condition, String operator, String content) {
		String[] entryids;
		List<Tb_entry_index> entryIndexList = entryIndexService.getEntryIndexList(nodeid, condition, operator, content);
		if (entryIndexList.size() > 0) {
			entryids = new String[entryIndexList.size()];
			for (int i = 0; i < entryIndexList.size(); i++) {
				entryids[i] = entryIndexList.get(i).getEntryid();
			}
		} else {
			entryids = new String[] {};
		}
		return entryids;
	}

	/**
	 * 条目拆除——删除或拆到其它节点（改变数据节点id，设置档号为null）
	 * 
	 * @param entryid
	 *            拆除条目id
	 * @param dismantleType
	 *            拆除类型
	 * @param nodeid
	 *            目标节点id
	 * @return
	 */
	@LogAnnotation(module = "数据管理", startDesc = "拆件操作，拆除条目id为：", sites = "1,2,3", connect = "，拆件方式为：,，拆件目标分类节点id为：,。")
	@RequestMapping("/dismantle")
	@ResponseBody
	public ExtMsg dismantle(String entryid, String dismantleType, String nodeid, String title, String syncType) {
		String msg = "";
		try {
			if ("delete".equals(dismantleType)) {
				// 先删除卷内，然后删除案卷，最后案卷之后的案卷号全部减一，包括卷内文件
				msg = entryIndexService.delEntry(entryid, nodeid, title, syncType);
				// entryService.delEntry(new String[]{entryid});//删除当前拆除条目
			}
			if ("node".equals(dismantleType)) {
				msg = entryIndexService.dismantle(entryid, nodeid, title, syncType);// 改变数据节点id，设置档号为null
			}

			if (!"拆件成功".equals(msg)) {
				return new ExtMsg(false, msg, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = "操作失败";
		}
		return new ExtMsg(true, msg, null);
	}

	/**
	 * 获取新案卷表单
	 * 
	 * @param entryid
	 * @param archivecode
	 * @return
	 */
	@RequestMapping(value = "/initNewFileFormData/{entryid}/{archivecode}/{nodeid}", method = RequestMethod.GET)
	@ResponseBody
	public EntryCapture initNewFileFormData(@PathVariable String entryid, @PathVariable String archivecode,
			@PathVariable String nodeid) {
		return entryIndexService.getNewFileFormData(entryid, archivecode, nodeid);
	}


	@RequestMapping("/getSelection")
	@ResponseBody
	public ExtMsg getSelection(String entryids, String nodeid, String condition, String operator, String content,
			Tb_entry_index formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic,
			boolean ifSearchLeafNode, boolean ifContainSelfNode) {
		List<Tb_entry_index> entry_indexList = entryIndexService.getEntryList(nodeid, condition, operator, content,
				formConditions, formOperators, daterangedata, logic, ifSearchLeafNode, ifContainSelfNode);
		String[] entryidData = entryids.split(",");
		entryids = "";
		for (Tb_entry_index entry_index : entry_indexList) {
			boolean found = false;
			for (String entryid : entryidData) {
				if (entryid.equals(entry_index.getEntryid())) {
					found = true;
					break;
				}
			}
			if (!found) {
				entryids += entry_index.getEntryid() + ",";
			}
		}
		entryids = entryids.substring(0, entryids.length() - 1);
		return new ExtMsg(true, "", entryids);
	}

	/**
	 * 根据已选择的卷内文件条目id获取其对应的案卷的条目id及案卷所属节点id
	 *
	 * @param entryid
	 *
	 * @return
	 */
	@RequestMapping("/fileNodeidAndEntryid")
	@ResponseBody
	public Map<String, Object> getFileNodeidAndEntryid(String entryid) {
		return entryIndexService.getFileNodeidAndEntryidByInnerfileEntryid(entryid);
	}

	// 库房系统用，暂时注销20180531
	// 库房系统用，暂时注销20180531
	/**
	 * 新增入库 获取未入库出库的条目
	 *
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/storageNoEntries", method = RequestMethod.GET)
	@ResponseBody
	public Page<Tb_index_detail> getStorageNoEntries(String nodeid,String condition, String operator, String content,Tb_index_detail formConditions,
													 ExtOperators formOperators, ExtDateRangeData daterangedata, String logic,int page, int limit, String sort) {
		Sort sortobj = WebSort.getSortByJson(sort);
		return entryIndexService.getStorageNoEntries(nodeid,condition, operator, content,formConditions,
				formOperators, daterangedata, logic, page, limit,sortobj);
	}

	//查看暂时添加入库的条目
	@RequestMapping(value = "/addstorages", method = RequestMethod.GET)
	@ResponseBody
	public Page<Tb_index_detail> getaddStorages(String condition, String operator, String content,int page, int limit, Sort sort) {
		return entryIndexService.getaddStorages(condition, operator, content, page, limit,sort,"1");
	}

	/**
	 * 获取入库状态的条目
	 *
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/storageEntries", method = RequestMethod.GET)
	@ResponseBody
	public Page<Tb_index_detail> getStorageEntries(String nodeid,String condition, String operator, String content,Tb_index_detail formConditions,
												   ExtOperators formOperators, ExtDateRangeData daterangedata, String logic,int page, int limit, String sort) {
		String staIn = Storage.STATUS_IN;
		Sort sortobj = WebSort.getSortByJson(sort);
		return entryIndexService.findStorageEntries(staIn,nodeid,condition, operator, content,formConditions,
				formOperators, daterangedata, logic, page, limit,sortobj);
	}
	//查看暂时添加出库的条目
	@RequestMapping(value = "/addOutwares", method = RequestMethod.GET)
	@ResponseBody
	public Page<Tb_index_detail> getaddOutwares(String condition, String operator, String content,int page, int limit, Sort sort) {
		return entryIndexService.getaddStorages(condition, operator, content, page, limit,sort,"2");
	}
	/**
	 * 出库历史记录 获取出库历史记录
	 *
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/outwares", method = RequestMethod.GET)
	@ResponseBody
	public void getOutwares(HttpServletResponse httpServletResponse,String condition, String operator, String content,int page, int limit, Sort sort) {
		String staOut = Storage.STATUS_OUT;
        Page<Tb_entry_index> result =entryIndexService.getOutwares(staOut,page,limit,condition,operator,content,sort);
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

	@RequestMapping(value = "/returnOutwares")
	@ResponseBody
	public void getReturnOutwares(HttpServletResponse httpServletResponse,String condition, String operator, String content,int page, int limit, Sort sort){
		String staOut = Storage.STATUS_OUT;
        Page<Tb_entry_index> result =  entryIndexService.getreturnOutwares(staOut,page,limit,condition,operator,content,sort);
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
	 * 入库历史记录 获取入库历史记录
	 *
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/inwares", method = RequestMethod.GET)
	@ResponseBody
	public Page<Tb_index_detail> getInwares(String nodeid,String condition, String operator, String content,int page, int limit, Sort sort) {
		String staIn = Storage.STATUS_IN;
		return entryIndexService.getInwares(staIn, nodeid,condition,operator,content,page,limit,sort);
	}

	/**
	 * 获取借阅记录
	 *
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/borrow", method = RequestMethod.GET)
	@ResponseBody
	public Page<Map<String, Object>> findBorrows(int page, int limit){
		return entryIndexService.findBorrows(page, limit);
	}

	/**
	 * 实体档案入库-设置添加状态
	 * @param entryids
	 * @return
	 */
	@RequestMapping("/setAddInwares")
	@ResponseBody
	public ExtMsg setAddInwares(String[] entryids){
		SecurityUser userDetails=(SecurityUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userId=userDetails.getUserid();
		ExtMsg result = entryIndexService.addBookmarks(entryids, userId,"1");//添加入库 1
		return result;
	}

	/**
	 * 实体档案入库-删除已添加待入库的条目
	 * @param entryids
	 * @return
	 */
	@RequestMapping("/deleteInwares")
	@ResponseBody
	public ExtMsg deleteInwares(String[] entryids,String type){
		SecurityUser userDetails=(SecurityUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userId=userDetails.getUserid();
		if("1".equals(type)){
			List<String> list=bookmarksRepository.findEntryidByUseridandAddstate(userId,"1");
			if(list.size()>0)
				entryids=list.toArray(new String[list.size()]);
		}
		ExtMsg result = entryIndexService.deleteBookmarks(entryids, userId,"1");
		if(result!=null){
			return result;
		}
		return new ExtMsg(false,"删除失败",null);
	}

	@RequestMapping("/findAddInwares")
	@ResponseBody
	public ExtMsg findAddInwares(){
		SecurityUser userDetails=(SecurityUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userId=userDetails.getUserid();
		return entryIndexService.findEntryByBookmarks(userId,"1");
	}

	@RequestMapping("/findAddOutwares")
	@ResponseBody
	public ExtMsg findAddOutwares(){
		SecurityUser userDetails=(SecurityUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userId=userDetails.getUserid();
		return entryIndexService.findEntryByBookmarks(userId,"2");
	}
	/**
	 * 实体档案出库添加
	 * @param entryids
	 * @return
	 */
	@RequestMapping("/setAddOutwares")
	@ResponseBody
	public ExtMsg setAddOutwares(String[] entryids){
		SecurityUser userDetails=(SecurityUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userId=userDetails.getUserid();
		ExtMsg result = entryIndexService.addBookmarks(entryids, userId,"2");//出库，标记状态为2
		return result;
	}

	/**
	 * 实体档案出库-删除已添加
	 * @param entryids
	 * @return
	 */
	@RequestMapping("/deleteOutwares")
	@ResponseBody
	public ExtMsg deleteOutwares(String[] entryids){
		SecurityUser userDetails=(SecurityUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userId=userDetails.getUserid();
		ExtMsg result = entryIndexService.deleteBookmarks(entryids, userId,"2");
		if(result!=null){
			return result;
		}
		return new ExtMsg(false,"删除失败",null);
	}
	/**
	 * 按档号找entryid和nodeid 有权限过滤
	 *
	 * @param dhCode
	 * @return
	 */
	@RequestMapping("/findids")
	@ResponseBody
	public ExtMsg findIds(String dhCode) {
		String msg = entryIndexService.findIds(dhCode);
		return new ExtMsg(true, msg, null);
	}

	/**
	 * 按档号找entryid和nodeid 无权限过滤
	 *
	 * @param dhCode
	 * @return
	 */
	@RequestMapping("/findidsAll")
	@ResponseBody
	public ExtMsg findIdsAll(String[] dhCode) {
		List<Tb_entry_index> entrys = entryIndexService.findIdsAll(dhCode);
		if(entrys != null && entrys.size() > 0){
			return new ExtMsg(true, null, entrys);
		}
		return new ExtMsg(false, "操作失败", null);
	}

	/**
	 * 按inid找每条入库记录的entry
	 *
	 * @param
	 * @return
	 */
	@RequestMapping("/findOne/{inid}")
	@ResponseBody
	public Page<Tb_entry_index> findByInid(@PathVariable String inid, int page, int limit) {
		InWare iw = inWareService.findOne(inid);
		String[] entrys = inWareService.findEntryIdsByinid(iw.getInid());
//		Set<Storage> stSet = iw.getStorages();
//		String entryMsg = "";
//		if (stSet.size() > 0) {
//			for (Storage st : stSet) {
//				String entry = st.getEntry();
//				entryMsg += entry + ",";
//			}
//			entryMsg = entryMsg.substring(0, entryMsg.lastIndexOf(","));
//			String[] entrys = entryMsg.split(",");
			PageRequest pageRequest = new PageRequest(page - 1, limit);
			return entryIndexService.findByEntryids(entrys, pageRequest);
//		} else {
//			return null;
//		}

	}

	/**
	 * 按inid找每条出库记录的entry
	 *
	 * @param
	 * @return
	 */
	@RequestMapping("/findOutWareEntry/{outid}")
	@ResponseBody
	public Page<Tb_entry_index> findByOutid(@PathVariable String outid, int page, int limit) {
		OutWare ow = outWareService.findOne(outid);
//		Set<Storage> stSet = ow.getStorages();
//		String entryMsg = "";
//		if (stSet.size() > 0) {
//			for (Storage st : stSet) {
//				String entry = st.getEntry();
//				entryMsg += entry + ",";
//			}
//			entryMsg = entryMsg.substring(0, entryMsg.lastIndexOf(","));
//			String[] entrys = entryMsg.split(",");
			String[] entrys = outWareService.findEntryIdsByOutid(ow.getOutid());
			PageRequest pageRequest = new PageRequest(page - 1, limit);
			return entryIndexService.findByEntryids(entrys, pageRequest);
//		} else {
//			return null;
//		}

	}

	/**
	 * 找出单元格中存放的档案信息
	 * @param zoneid 区id
	 * @param coldisplay 列名
	 * @param sectiondisplay 节名
	 * @param sidedisplay 面名
	 * @param layerdisplay 层名
	 * @return
	 */
	@RequestMapping(value = "/getCellEntry",method = RequestMethod.GET)
	@ResponseBody
	public Page<Tb_entry_index> getCellEntry(String zoneid,String coldisplay,String sectiondisplay,String sidedisplay,
											 String layerdisplay,int page,int limit){
		PageRequest pageRequest = new PageRequest(page - 1, limit);
		ZoneShelves zone =	zoneShelvesRepository.findByDisplay(zoneid, coldisplay, sectiondisplay, layerdisplay, sidedisplay);
		Page<Tb_entry_index> indexList;
		if(zone != null){
			indexList= entryIndexService.getCellEntry(zone.getShid(),pageRequest);
			return indexList;
		}
		return null;
	}

	/**
	 * 按inid找每个单元格的entry
	 *
	 * @param
	 * @return
	 */
	@RequestMapping("/findInShid/{inid}")
	@ResponseBody
	public Page<Tb_entry_index> findEntryInShid(@PathVariable String inid,
												int page, int limit) {
		String zoneid=inid.substring(inid.indexOf("层")+1);
		String coldisplay=inid.substring(0,2);
		String sectiondisplay=inid.substring(3,5);
		String sidedisplay=inid.substring(6,8);
		String layerdisplay=inid.substring(8,10);
		ZoneShelves zs=zoneShelvesRepository.findByDisplay(zoneid,coldisplay,sectiondisplay,layerdisplay,sidedisplay);
		//String[] entryids=storageRepository.findByShidAndStatus(zs.getShid(),Storage.STATUS_IN);
		List<Storage> sts=storageRepository.findStoragesByShid(zs.getShid());
		String[] entryids=storageRepository.findByShid(zs.getShid());
		if(entryids.length>0){
			PageRequest pageRequest = new PageRequest(page - 1, limit);
			Page<Tb_entry_index> list= entryIndexRepository.findByEntryidIn(entryids,pageRequest);
			List<Tb_entry_index> teiList=list.getContent();
			List<Tb_entry_index> tlist=new ArrayList<>();
			for(Tb_entry_index tei:teiList){
				for(Storage st:sts){
					if(st.getEntry().trim().equals(tei.getEntryid().trim())){
						tei.setNodefullname(st.getStorestatus());//标记出入库
						break;
					}
				}
				tlist.add(tei);
			}
			return new PageImpl<Tb_entry_index>(tlist, pageRequest,
					list.getTotalElements());
		}else{
			return null;
		}
	}

	@RequestMapping("/getMissPageCheck")
	@ResponseBody
	public List<RebackMissPageCheck> getMissPageCheck(String[] ids,String isSelectAll, String nodeid, String condition, String operator,
							  String content, Tb_entry_index formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata,
							  String logic, boolean ifSearchLeafNode, boolean ifContainSelfNode) {
		if ("true".equals(isSelectAll)) {
			List<Tb_entry_index> entry_indexList = entryIndexService.getEntryList(nodeid, condition, operator, content,
					formConditions, formOperators, daterangedata, logic, ifSearchLeafNode, ifContainSelfNode);
			if (entry_indexList.size() > 0) {
				ids=entry_indexList.stream().map(e->e.getEntryid()).collect(Collectors.toList()).toArray(new String[entry_indexList.size()]);
			}
		}
		List<String[]> subAry = new InformService().subArray(ids, 1000);
		List<RebackMissPageCheck> rebackMissPageChecks = new ArrayList<>();
		for (String[] ary : subAry) {
			List<RebackMissPageCheck> MissPageChecks = entryIndexService.getMissPageCheck(ary);
			rebackMissPageChecks.addAll(MissPageChecks);
		}
		return rebackMissPageChecks;
	}

	@RequestMapping("/getMissPageCheckTotal")
	@ResponseBody
	public int[] getMissPageCheckTotal(String[] ids,String isSelectAll, String nodeid, String condition, String operator,
									   String content, Tb_entry_index formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata,
									   String logic, boolean ifSearchLeafNode, boolean ifContainSelfNode) {
		if ("true".equals(isSelectAll)) {
			List<Tb_entry_index> entry_indexList = entryIndexService.getEntryList(nodeid, condition, operator, content,
					formConditions, formOperators, daterangedata, logic, ifSearchLeafNode, ifContainSelfNode);
			if (entry_indexList.size() > 0) {
				ids=entry_indexList.stream().map(e->e.getEntryid()).collect(Collectors.toList()).toArray(new String[entry_indexList.size()]);
			}
		}
		return entryIndexService.getMissPageCheckTotal(ids);
	}


	/**
	 * 根据电子文件id获取电子文件历史版本
	 *
	 * @param eleid
	 * @return
	 */

	@RequestMapping("/getEleVersion")
	@ResponseBody
	public Page<Tb_electronic_version> getElectronicVersion(String eleid,int page,int limit,Sort sort) {
		return entryIndexService.getElectronicVersion(eleid,page,limit,sort);
	}

	/**
	 * 根据电子文件版本ids删除电子文件历史版本
	 *
	 * @param eleVersionids
	 * @return
	 */

	@RequestMapping("/delVersion")
	@ResponseBody
	public ExtMsg delElectronicVersion(String[] eleVersionids) {
		int count = entryIndexService.delElectronicVersion(eleVersionids);
		if(count>0){
			return new ExtMsg(true, "", count);
		}else {
			return new ExtMsg(false, "", null);
		}
	}

	/**
	 * 根据电子文件版本id回滚到此版本
	 *
	 * @param eleVersionid
	 * @return
	 */

	@RequestMapping("/rebackVersion")
	@ResponseBody
	public ExtMsg rebackElectronicVersion(String eleVersionid) {
		entryIndexService.rebackElectronicVersion(eleVersionid);
		return new ExtMsg(true, "", null);
	}

	/**
	 * 根据eleVersionids判断文件是否存在于相应目录中，若不存在，给出相应提示
	 *
	 * @param eleVersionids
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/ifVersionFileExist")
	@ResponseBody
	public ExtMsg ifVersionFileExist(String[] eleVersionids) throws Exception {
		List<Tb_electronic_version> electronic_versions = entryIndexService.getEleVersionByids(eleVersionids);
		boolean ifFileExists = true;
		String notExistsFilesStr = "";
		for (Tb_electronic_version eleVersion : electronic_versions) {
			String filename = eleVersion.getFilename();
			File file = new File(rootpath + eleVersion.getFilepath()+ "/" + filename);
			if (!file.exists()) {
				if (notExistsFilesStr.length() > 0) {
					notExistsFilesStr += "、";
				}
				notExistsFilesStr += "“" + filename + "”";
				ifFileExists = false;
			}
		}
		if (ifFileExists) {
			return new ExtMsg(true, "", null);
		} else {
			return new ExtMsg(false, "本地文件" + notExistsFilesStr + "不存在！", null);
		}
	}

	@RequestMapping(value = "/downloadEleVersion/eleVersionid/{eleVersionid}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<byte[]> downfile(@PathVariable String eleVersionid,
										   HttpServletRequest request, HttpServletResponse response) throws Exception {
		Tb_electronic_version eleversion = entryIndexService.getEleVersionByid(eleVersionid);
		String filename = eleversion.getFilename();
		File file = new File(rootpath + eleversion.getFilepath() + "/" + filename);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentLength(Long.parseLong(eleversion.getFilesize()));
		headers.setContentDispositionFormData("attachment", getOutName(request, filename));
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
	}

	/**
	 * 处理文件下载时的中文名
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param name
	 *            文件名
	 * @return 转码过的文件名
	 * @throws Exception
	 */
	public static String getOutName(HttpServletRequest request, String name) throws IOException {
		String outName = MimeUtility.encodeText(name, "UTF8", "B");
		UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
		Browser browser = userAgent.getBrowser();
		String browseName = browser.getName() != null ? browser.getName().toLowerCase() : "";
		if (browseName.indexOf("internet explorer") > -1) {
			outName = URLEncoder.encode(name, "UTF8");
		}
		return outName;
	}

	@RequestMapping(value = "/downloadEleVersion/eleVersionids/{eleVersionids}", method = RequestMethod.GET)
	public void downfiles(@PathVariable String[] eleVersionids, HttpServletRequest request,
						  HttpServletResponse response) throws Exception {
		String zipPath = entryIndexService.transFiles(eleVersionids);
		try {
			File html_file = new File(zipPath);
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + getOutName(request, html_file.getName()) + "\"");
			response.setContentType("application/zip");
			FileInputStream inputStream = new FileInputStream(html_file);
			ServletOutputStream out = response.getOutputStream();
			int b = 0;
			byte[] buffer = new byte[1024];
			while ((b = inputStream.read(buffer)) != -1) {
				out.write(buffer, 0, b);
			}
			inputStream.close();
			out.flush();
			out.close();
		} catch (Exception e) {
		}
	}

	//---二维码生成并压缩成zip包
	@RequestMapping("/createQRcode")
	@ResponseBody
	public ExtMsg createQRcode (String entryids, String isSelectAll, String userFieldCode, String nodeid, String
			condition
			, String operator, String content, HttpServletRequest request, HttpServletResponse response) throws
			Exception {
		String creatPath = rootpath + File.separator + "OAFile" + File.separator + "QRCode";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		Date date = new Date();
		//二维码生成路径
		String QRCodeCreatPath = creatPath + File.separator + sdf.format(date);
		//二维码压缩包生成路径
		String zippath = rootpath + File.separator + "OAFile" + File.separator + "QRCodeExport" + File.separator + sdf.format(date) + ".zip";
		if (!new File(rootpath + File.separator + "OAFile" + File.separator + "QRCodeExport/").exists()) {
			new File(rootpath + File.separator + "OAFile" + File.separator + "QRCodeExport/").mkdirs();
		}
		String[] useFieldcode = userFieldCode.split(",");
		//zippath.replaceAll("/", "\\\\");
		String[] idarr = {};
		if (entryids != null) {
			idarr = entryids.split(",");
		}
		if ("true".equals(isSelectAll)) {
			List<String> entryList = entryIndexService.getIndexIds(nodeid, condition, operator, content, null, null, null);
			idarr = entryList.toArray(new String[entryList.size()]);
		}
		ObjectMapper json = new ObjectMapper();
		for (int i = 0; i < idarr.length; i++) {
			Tb_entry_index index = entryIndexRepository.findByEntryid(idarr[i]);
			Tb_entry_detail detail = entryDetailRepository.findByEntryid(idarr[i]);
			Map<String, String> map = new HashMap<>();
			for (int j = 0; j < useFieldcode.length; j++) {
				String str = useFieldcode[j].substring(1, useFieldcode[j].length());
				if (isNumeric(str)) {
					map.put(useFieldcode[j], ValueUtil.getPoFieldValue(useFieldcode[j], detail) + "");
				} else {
					map.put(useFieldcode[j], ValueUtil.getPoFieldValue(useFieldcode[j], index) + "");
				}
			}
			//QRCodeUtil.createQRcodeAndText(JSONObject.fromObject(index).toString(), null, false, true, index.getArchivecode(), QRCodeCreatPath + File.separator + idarr[i] + ".jpg");
			QRCodeUtil.createQRcodeAndText(json.writeValueAsString(map), null, false, true, index.getArchivecode(), QRCodeCreatPath + File.separator + idarr[i] + ".jpg");
		}
		ZipUtil.zip(QRCodeCreatPath, zippath, true, "");
		FileUtil.delFolder(QRCodeCreatPath);//生成压缩包后删除二维码图片
		ExtMsg extMsg = new ExtMsg(true, "zipPath", zippath);
		return extMsg;
	}

	//打印二维码
	@RequestMapping("/printQRCode")
	@ResponseBody
	public ExtMsg printQRCode (String entryids, String userFieldCode, String isSelectAll, String nodeid, Model
			model, String condition, String content, String operator) throws Exception {
		Map<String, List> map = new HashMap<>();
		String creatPath = rootpath + File.separator + "OAFile" + File.separator + "QRCodePrint";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		Date date = new Date();
		String[] useFieldcode = userFieldCode.split(",");
		//二维码生成路径
		String QRCodeCreatPath = creatPath + File.separator + sdf.format(date);
		//二维码压缩包生成路径
		String zippath = rootpath + File.separator + "OAFile" + File.separator + "QRCodeExport" + File.separator + sdf.format(date) + ".zip";
		if (!new File(rootpath + File.separator + "OAFile" + File.separator + "QRCodeExport/").exists()) {
			new File(rootpath + File.separator + "OAFile" + File.separator + "QRCodeExport/").mkdirs();
		}
		//zippath.replaceAll("/", "\\\\");
		String[] idarr = {};
		if (entryids != null) {
			idarr = entryids.split(",");
		}
		if ("true".equals(isSelectAll)) {
			List<String> entryList = entryIndexService.getIndexIds(nodeid, condition, operator, content,null,null,null);
			idarr = entryList.toArray(new String[entryList.size()]);
		}
		List l = new ArrayList();
		ObjectMapper json = new ObjectMapper();
		for (int i = 0; i < idarr.length; i++) {
			Tb_entry_index index = entryIndexRepository.findByEntryid(idarr[i]);
			Tb_entry_detail detail = entryDetailRepository.findByEntryid(idarr[i]);
			Map<String, String> maps = new HashMap<>();
			for (int j = 0; j < useFieldcode.length; j++) {
				String str = useFieldcode[j].substring(1, useFieldcode[j].length());
				if (isNumeric(str)) {
					maps.put(useFieldcode[j], ValueUtil.getPoFieldValue(useFieldcode[j], detail) + "");
				} else {
					maps.put(useFieldcode[j], ValueUtil.getPoFieldValue(useFieldcode[j], index) + "");
				}
			}
			//QRCodeUtil.createQRcodeAndText(JSONObject.fromObject(index).toString(), null, false, true, index.getArchivecode(), QRCodeCreatPath + File.separator + idarr[i] + ".jpg");
			QRCodeUtil.createQRcodeAndText(json.writeValueAsString(map), null, false, true, index.getArchivecode(), QRCodeCreatPath + File.separator + idarr[i] + ".jpg");
			String path = QRCodeCreatPath + File.separator + idarr[i] + ".jpg";
			l.add(ValueUtil.baseConvertStr(path));
		}
		map.put("QRCodePath", l);
		List delPath = new ArrayList();
		delPath.add(QRCodeCreatPath);
		map.put("delPath", delPath);
		//map.put("rootpath",rootpath);
		ExtMsg extMsg = new ExtMsg(true, "QRCodeMsg", map);
		return extMsg;
	}

	/**
	 * 删除文件
	 * @param delPath
	 */
	@RequestMapping("/delPrintQRCode")
	@ResponseBody
	public void delPrintQRCode (String delPath){
		if (delPath != null || "".equals(delPath)) {
			FileUtil.delFolder(delPath);//删除打印是生成的二维码图片
		}
	}

	/**
	 * 获取修改选中的临时条目
	 *
	 * @param entryid
	 * @return
	 */
	@RequestMapping(value = "/entryTemp/{entryid}", method = RequestMethod.GET)
	@ResponseBody
	public Tb_entry_index_temp getEntryTemp (@PathVariable String entryid){
		String uniquetag = BatchModifyService.getUniquetagByType("glgd");
		return entryIndexTempRepository.findByEntryidInAndUniquetag(entryid, uniquetag);// 找到当前条目数据
	}

	/**
	 * 上移
	 *
	 * @param currentId
	 * @param nodeid
	 * @return
	 */
	@RequestMapping("/moveup")
	@ResponseBody
	public ExtMsg moveup (String currentId, String nodeid){
		String uniquetag = BatchModifyService.getUniquetagByType("glgd");
		Tb_entry_index_temp currentTemp = entryIndexTempRepository.findByEntryidInAndUniquetag(currentId, uniquetag);// 找到当前条目数据
		if (currentTemp.getSortsequence() == 0) {
			return new ExtMsg(false, "操作失败", null);
		}
		if (Integer.valueOf(currentTemp.getSortsequence()) == 1) {
			return new ExtMsg(false, "第一条数据无法进行上移操作", null);
		}
		//查找比选中的顺序号上一条的临时条目的顺序号跟选中的顺序号互相置换
		List<String> temps = entryIndexTempRepository.findEntryidByUniquetag(uniquetag);
		String lastId = "";
		if (temps.indexOf(currentId) >= 1) {
			lastId = temps.get(temps.indexOf(currentId) - 1);// 找到上一条的条目信息
		} else if (temps.indexOf(currentId + "    ") >= 1) {//sqlserver的char主键多四个空格
			lastId = temps.get(temps.indexOf(currentId + "    ") - 1);// 找到上一条的条目信息
		}
		Integer current = Integer.valueOf(currentTemp.getSortsequence());
		if (lastId != null && !lastId.equals("")) {
			Tb_entry_index_temp lastTemp = entryIndexTempRepository.findByEntryidInAndUniquetag(lastId, uniquetag);// 找到上一条目数据
			Integer last = Integer.valueOf(lastTemp.getSortsequence());
			//顺序号互相置换
			entryIndexTempRepository.updateSortsequenceByEntryidAndUniquetag(last, currentId, uniquetag);
			entryIndexTempRepository.updateSortsequenceByEntryidAndUniquetag(current, lastId, uniquetag);
		}
		return new ExtMsg(true, "上移成功", null);
	}

	/**
	 * 下移
	 *
	 * @param currentId
	 * @param nodeid
	 * @return
	 */
	@RequestMapping("/movedown")
	@ResponseBody
	public ExtMsg movedown (String currentId, String nodeid){
		String uniquetag = BatchModifyService.getUniquetagByType("glgd");
		Tb_entry_index_temp currentTemp = entryIndexTempRepository.findByEntryidInAndUniquetag(currentId, uniquetag);// 找到当前条目数据
		if (currentTemp.getSortsequence() == 0) {
			return new ExtMsg(false, "操作失败", null);
		}
		//查找比选中的顺序号下一条的临时条目的顺序号跟选中的顺序号互相置换
		List<String> temps = entryIndexTempRepository.findEntryidByUniquetag(uniquetag);
		if (Integer.valueOf(currentTemp.getSortsequence()) == temps.size()) {
			return new ExtMsg(false, "最后一条数据无法进行下移操作", null);
		}
		String nextId = "";
		if (temps.indexOf(currentId) >= 0) {
			nextId = temps.get(temps.indexOf(currentId) + 1);// 找到下一条的条目信息
		} else if (temps.indexOf(currentId + "    ") >= 0) {//sqlserver的char主键多四个空格
			nextId = temps.get(temps.indexOf(currentId + "    ") + 1);// 找到上一条的条目信息
		}
		Integer current = Integer.valueOf(currentTemp.getSortsequence());
		if (nextId != null && !nextId.equals("")) {
			Tb_entry_index_temp lastTemp = entryIndexTempRepository.findByEntryidInAndUniquetag(nextId, uniquetag);// 找到下一条目数据
			Integer last = Integer.valueOf(lastTemp.getSortsequence());
			//顺序号互相置换
			entryIndexTempRepository.updateSortsequenceByEntryidAndUniquetag(current, nextId, uniquetag);
			entryIndexTempRepository.updateSortsequenceByEntryidAndUniquetag(last, currentId, uniquetag);
		}
		return new ExtMsg(true, "下移成功", null);
	}

	@RequestMapping(value = "/findSearchMetadataLog")
	@ResponseBody
	public Page<Tb_metadata_log> findSearchMetadataLog(String condition, String operator, String content, String entryid, int page,int limit) {
		return metadataLogService.findBySearch(condition,  operator,  content,  entryid,  page, limit);
	}

	/**
	 * 获取需要加载至退回表单的单据
	 *
	 * @param entryids
	 * @return
	 */
	@RequestMapping("/getNewBackCaptureDoc")
	@ResponseBody
	public ExtMsg getNewBackCaptureDoc(String entryids, String isSelectAll, String nodeid, String condition, String operator,
									   String content) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Tb_backcapturedoc backcapturedoc = new Tb_backcapturedoc();
		backcapturedoc.setBacker(userDetails.getRealname());
		//设置退回机构
		Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
		if(node.getOrganid()!=null&&!"".equals(node.getOrganid())){
			Tb_right_organ organ = rightOrganRepository.findByOrganid(node.getOrganid());
			StringBuffer nodefullname = new StringBuffer(node.getNodename());
			//找到当前账号的所在单位
			while (organ.getOrgantype() != null && organ.getOrgantype().equals(Tb_right_organ.ORGAN_TYPE_DEPARTMENT)) {
				organ = rightOrganRepository.findOne(organ.getParentid());
				nodefullname.insert(0, "_");
				nodefullname.insert(0, organ.getOrganname());
			}
			backcapturedoc.setBackorgan(nodefullname.toString());
		}
		backcapturedoc.setBacktime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		if ("true".equals(isSelectAll)) {
			//当状态是全选的时候，entryids为取消选择的条目
			String ids = "";
			List<Tb_entry_index> entryIndexs = entryIndexService.getEntryIndexList(nodeid, condition,
					operator, content);
			if (entryIndexs.size() > 0) {
				for (int i = 0; i < entryIndexs.size(); i++) {
					if (entryids.indexOf(entryIndexs.get(i).getEntryid()) == -1) {
						ids += entryIndexs.get(i).getEntryid() + ",";
					}
				}
				ids = ids.length() > 0 ? ids.substring(0, ids.length() - 1) : "";
			}
			backcapturedoc.setBackcount(ids.split(",").length + "");
			backcapturedoc.setId(ids);// 将选择的条目id临时存放在id上
		} else {
			backcapturedoc.setBackcount(entryids.split(",").length + "");
			backcapturedoc.setId(entryids);// 将选择的条目id临时存放在id上
		}
		return new ExtMsg(true, "成功", backcapturedoc);
	}

	/**
	 * 退回采集
	 *
	 * @return
	 */
	@RequestMapping("/backCaptureSubmit")
	@ResponseBody
	public ExtMsg backCaptureSubmit(String nodeid,String entryids,String backreason,String backer,String backcount,String backorgan,String backtime) {
		String[] entryidData = entryids.split(",");
		List<String[]> subAry = new InformService().subArray(entryidData, 1000);//处理ORACLE1000参数问题
		List<Tb_entry_index> indexs = new ArrayList<>();
		for (String[] ary : subAry) {
			List<Tb_entry_index> captureAry = entryIndexRepository.findByEntryidIn(ary);
			indexs.addAll(captureAry);
		}
		String repeact = "";
		for (int i = 0; i < indexs.size(); i++) {
			if (!"".equals(indexs.get(i).getArchivecode()) && indexs.get(i).getArchivecode() != null) {
				List<Tb_entry_index_capture> entry_capture = entryIndexCaptureRepository.findByArchivecode(indexs.get(i).getArchivecode());
				if (entry_capture.size() > 0) {
					repeact += indexs.get(i).getArchivecode() + "、";
				}
			}
		}
		if (repeact.length() > 0) {
			repeact = repeact.substring(0, repeact.length() - 1);
		}
		Tb_data_node node = entryIndexService.getNodeLevel(nodeid);
		//如果重复档号值不为空且非未归管理,那么就判断档号重复
		if (!repeact.equals("") && node != null && !node.getNodename().equals("未归管理")) {
			return new ExtMsg(false, "档号记录重复", "档号记录:" + repeact + "重复");
		}
		int[] num = entryIndexService.backCaptureSubmit(entryidData,nodeid,backreason,backer,backcount,backorgan,backtime);
		if (num[0] > 0 && num[1] > 0 && num[2] > 0) {
			//记录日志
			logService.recordTextLog("数据管理","退回采集","从数据管理退回数据采集成功");
			return new ExtMsg(true, "退回采集成功", num);
		}
		//记录日志
		logService.recordTextLog("数据管理","退回采集","从数据管理退回数据采集失败");
		return new ExtMsg(false, "退回采集异常", null);
	}

	/**
	 * 获取指定数据节点的所有退回采集单据
	 *
	 * @param nodeid 数据节点id
	 * @return
	 */
	@RequestMapping("/getNodeBackCaptureDoc")
	@ResponseBody
	public Page<Tb_backcapturedoc> getNodeBackCaptureDoc(String nodeid, int page, int limit, String condition,
														 String operator, String content, String sort) {
		Sort sortobj = WebSort.getSortByJson(sort);
		return entryIndexService.getNodeBackCaptureDoc(page, limit, condition, operator, content, nodeid, sortobj);
	}

	/**
	 * 查看退回采集单据详情条目
	 *
	 */
	@RequestMapping("/getBackCaptureEntrys")
	@ResponseBody
	public Page getBackCaptureEntrys(String backdocid, int page, int limit, String condition,
									 String operator, String content, String sort) {
		Sort sortobj = WebSort.getSortByJson(sort);
		String[] entryidData = backCapturedocEntryRepository.getEntryids(backdocid);
		return entryIndexCaptureService.getEntrysByids(page, limit, condition, operator, content, entryidData,
				sortobj);
	}
}