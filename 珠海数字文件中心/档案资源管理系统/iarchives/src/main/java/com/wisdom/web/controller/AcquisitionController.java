package com.wisdom.web.controller;

import com.alibaba.fastjson.JSON;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.wisdom.service.websocket.WebSocketService;
import com.wisdom.util.*;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.security.SecurityUser;
import com.wisdom.web.service.*;
import com.xdtech.smsclient.SMSService;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import net.netca.pdfSign.ISignatureVerifier;
import net.netca.pdfSign.impl.SignatureVerifierImpl;
import net.netca.pki.SignedData;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.solr.common.util.ContentStreamBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.net.www.protocol.http.HttpURLConnection;

import javax.mail.internet.MimeUtility;
import javax.persistence.Convert;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.print.DocFlavor;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.hankcs.hanlp.corpus.tag.Nature.*;

/**
 * 数据采集控制器 Created by Rong on 2017/10/24.
 */
@Controller
@RequestMapping(value = "/acquisition")
public class AcquisitionController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final String DEFAULT_ENTRYRETENTION = "短期";

	@Value("${system.document.rootpath}")
	private String rootpath;// 系统文件根目录

	@Value("${system.loginType}")
	private String systemType;//政务网1  局域网0

	private static  String flag;

	@Value("${system.showElectronicRename.opened}")
	private String showElectronicRename;//是否显示电子文件重名命名设置

	@Value("${system.loginType}")
	private String systemLoginType;//登录系统设置  政务网1  局域网0

	@Value("${CA.netcat.use}")
	private String netcatUse;//是否使用网证通电子签章  1使用  0禁用

	@Autowired
	private ElectronicCaptureRepository electronicCaptureRepository;

	@Autowired
	LogAop logAop;

	@Autowired
	AcquisitionService acquisitionService;

	@Autowired
	EntryIndexService entryIndexService;

	@Autowired
	EntryCaptureService entryCaptureService;

	@Autowired
	EntryIndexCaptureService entryIndexCaptureService;

	@Autowired
	EntryIndexTempService entryIndexTempService;

	@Autowired
	CodesettingService codesettingService;

	@Autowired
	AuditService auditService;

	@Autowired
	OrganService organService;

	@Autowired
	FundsService fundsService;

	@Autowired
	TemplateService templateService;

	@Autowired
	AppraisalStandardService appraisalStandardService;

	@Autowired
	EntryIndexRepository entryIndexRepository;

	@Autowired
	EntryIndexCaptureRepository entryIndexCaptureRepository;
	
	@Autowired
	EntryDetailCaptureRepository entryDetailCaptureRepository;

	@Autowired
	EntryIndexSqTempRepository entryIndexSqTempRepository;

	@Autowired
	CodesetRepository codesetRepository;

	@Autowired
	DataNodeRepository dataNodeRepository;

	@Autowired
	RightOrganRepository rightOrganRepository;

	@Autowired
	TemplateRepository templateRepository;

	@Autowired
	TransdocRepository transdocRepository;

	@Autowired
	SystemConfigRepository systemConfigRepository;

	@Autowired
	WebSocketService webSocketService;

	@Autowired
	PublicUtilService publicUtilService;

	@Autowired
	DataNodeExtRepository dataNodeExtRepository;

	@Autowired
	EntryIndexTempRepository entryIndexTempRepository;

	@Autowired
	BatchModifyService batchModifyService;

	@Autowired
	MetadataLogService metadataLogService;

	@Autowired
	TaskService taskService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	WorkflowService workflowService;

	@Autowired
	SMSService smsService;

	@Autowired
	CaTransforRepository caTransforRepository;

	@Autowired
	CaRepository caRepository;

	@Autowired
	UserController userController;

	@Autowired
	WatermarkUtil watermarkUtil;

	@Autowired
	WatermarkRepository watermarkRepository;

	@Autowired
	LongRetentionRepository longRetentionRepository;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	TransdocPreviewRepository transdocPreviewRepository;

	@Value("${system.audit.opened}")
	private String auditOpened;// 是否打开数据审核

	@Value("${system.report.server}")
	private String reportServer;//报表服务

	@Value("${workflow.acquisitionTransfer.approve.workid}")
	private String  transferWorkId;//移交审批节点编号

	@RequestMapping("/main")
	public String acquisition(Model model, String isp) {
		Object functionButton = JSON.toJSON(FunctionUtil.getQxFunction(isp));
		SecurityUser userDetails=(SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Object wjqxFunctionButton = JSON.toJSON(userController.getWJQXbtn());//文件权限
		model.addAttribute("wjqxFunctionButton", wjqxFunctionButton);
		model.addAttribute("functionButton", functionButton);
		model.addAttribute("auditOpened",auditOpened);
		model.addAttribute("reportServer",reportServer);
		List<String> mediaNodeids = dataNodeExtRepository.findMediaNodeid();
		//去除空格
		for(int i = 0; i < mediaNodeids.size(); i ++) {
			mediaNodeids.set(i, mediaNodeids.get(i).trim());
		}
		model.addAttribute("mediaNodeids", mediaNodeids);
		model.addAttribute("userid", userDetails.getUserid());
		model.addAttribute("userRealname",userDetails.getRealname());
		model.addAttribute("caUserid", userDetails.getNickname());
		model.addAttribute("showElectronicRename",showElectronicRename);
		model.addAttribute("systemType",systemType);
		model.addAttribute("systemLoginType",systemLoginType);
		model.addAttribute("netcatUse",netcatUse);
		return "/inlet/acquisition";
	}

	//根据id查询审批节点
	@RequestMapping("/findByWorkId")
	@ResponseBody
	public ExtMsg findByWorkId() {
		Tb_work work= workflowService.findByWorkid(transferWorkId);
		return new ExtMsg(true,"",work);
	}

	/*
	 * @RequestMapping(value = "/entries", method = RequestMethod.GET)
	 * 
	 * @ResponseBody public Page<Tb_entry_index_capture> getEntries(String
	 * nodeid,String condition,String docid,String operator,String content,int
	 * page,int limit,String sort){ Sort sortobj = WebSort.getSortByJson(sort);
	 * return
	 * entryCaptureService.getEntries(this.getClass().getSimpleName(),nodeid,
	 * Tb_transdoc_entry.STATUS_AUDIT,docid,condition,operator,content,page,
	 * limit,sortobj); }
	 */

	@RequestMapping(value = "/entries", method = RequestMethod.GET)
	@ResponseBody
	public Page<Tb_index_detail_capture> getEntries(String nodeid, String basicCondition, String basicOperator,
			String basicContent, String condition, String docid, String operator, String content, String info, int page,
			int limit, String sort,String parententryid) {
		Sort sortobj = WebSort.getSortByJson(sort);
		Page<Tb_entry_index_capture> list = null;
		if (info != null && "批量操作".equals(info)) {
			return entryCaptureService.getEntries(this.getClass().getSimpleName(), nodeid,
					Tb_transdoc_entry.STATUS_AUDIT, docid, basicCondition, basicOperator, basicContent, page, limit, sortobj,parententryid);
		} else {
			return entryCaptureService.getEntries(this.getClass().getSimpleName(), nodeid,
					Tb_transdoc_entry.STATUS_AUDIT, docid, condition, operator, content, page, limit, sortobj,parententryid);
		}
	}

	@RequestMapping(value = "/entriesWg")
	@ResponseBody
	public Page<Tb_index_detail_capture> getEntriesWg(String nodeid, String basicCondition, String basicOperator,
													  String basicContent, String condition, String docid, String operator, String content, String info, int page,
													  int limit, String sort,String parententryid) {
		Sort sortobj = WebSort.getSortByJson(sort);
		Page<Tb_entry_index_capture> list = null;
		return entryCaptureService.getEntries("预归档未归", nodeid,
				"", docid, condition, operator, content, page, limit, sortobj,parententryid);
	}

	@RequestMapping(value = "/entryIndexYgd")
	@ResponseBody
	public ExtMsg entryIndexYgd(String entryids,String nodeid,String condition,String operator,String content,String selectAll,String targetNodeid,String addType) {//添加预归档
		String uniquetag=BatchModifyService.getUniquetagByType("cjgd");
		//首次加载或者切换节点要删除之前的个人数据
		if("1".equals(addType)){//首次加载
			//batchModifyService.deleteEntryIndexTempByUniquetagByType("cjgd");
		}else if("2".equals(addType)){//切换节点
			List<String> stringList=entryIndexTempRepository.findEntryidByUniquetag(uniquetag);//获取临时表个人数据
			if(stringList.size()>0){//获取到entryids后重新加载采集表数据到临时表
				entryids=String.join(",",stringList);
				batchModifyService.deleteEntryIndexTempByUniquetagByType("cjgd");
			}
		}
		int num=entryCaptureService.entryIndexYgd(entryids,nodeid,condition,operator,content,uniquetag,selectAll,targetNodeid);
		//保存完后临时表排序
		entryIndexCaptureService.setSortsequence(nodeid,uniquetag);
		return new ExtMsg(true, "成功增加"+num+"条数据到预归档", null);
	}

	@RequestMapping(value = "/entryIndexInsertYgd")
	@ResponseBody
	public ExtMsg entryIndexInsertYgd(String entryids,String insertLine,String targetNodeid) {//插入预归档
		int num=entryCaptureService.entryIndexInsertYgd(entryids,insertLine,targetNodeid);
		return new ExtMsg(true, "成功插入"+num+"条数据到预归档", null);
	}

	@RequestMapping(value = "/entryIndexYgdDel")
	@ResponseBody
	public ExtMsg entryIndexYgdDel(String entryids,String nodeid) {//取消预归档
		String uniquetag=BatchModifyService.getUniquetagByType("cjgd");
		int num=entryCaptureService.entryIndexYgdDel(entryids,uniquetag);
		//保存完后临时表排序
		entryIndexCaptureService.setSortsequence(nodeid,uniquetag);
		return new ExtMsg(true, "成功取消"+num+"条数据", null);
	}

	@RequestMapping(value = "/entriesTemp")
	@ResponseBody
	public ExtMsg entryTempEdit(@ModelAttribute("form") EntryCapture entry,String dataNodeid) {//预归档修改
		return entryCaptureService.entryTempEdit(entry,dataNodeid);
	}

	@RequestMapping(value = "/delTempByUniquetag")
	@ResponseBody
	public void deleTemp(String archiveType ) {//无选择条目进入预归档时进行条目删除
		batchModifyService.deleteEntryIndexTempByUniquetagByType(archiveType);
	}

	@RequestMapping(value = "/entriesPost")
	@ResponseBody
	public Page<Tb_index_detail_capture> getEntriesPost(String nodeid, String basicCondition, String basicOperator,
													String basicContent, String condition, String docid, String operator, String content, String info, int page,
													int limit, String sort,String parententryid) {
		Sort sortobj = WebSort.getSortByJson(sort);
		Page<Tb_entry_index_capture> list = null;
		if (info != null && "批量操作".equals(info)) {
			return entryCaptureService.getEntries(this.getClass().getSimpleName(), nodeid,
					Tb_transdoc_entry.STATUS_AUDIT, docid, basicCondition, basicOperator, basicContent, page, limit, sortobj,parententryid);
		} else {
			return entryCaptureService.getEntries(this.getClass().getSimpleName(), nodeid,
					Tb_transdoc_entry.STATUS_AUDIT, docid, condition, operator, content, page, limit, sortobj,parententryid);
		}
	}

	@RequestMapping(value = "/entries/{entryid}", method = RequestMethod.GET)
	@ResponseBody
	public EntryCapture getEntry(@PathVariable String entryid) {
		return entryCaptureService.getEntry(entryid);
	}

	@RequestMapping(value = "/entries/innerfile/{entryid}/"/*, method = RequestMethod.GET*/)
	@ResponseBody
	public Page<Tb_entry_index_capture> getEntryInnerFile(@PathVariable String entryid, String nodeid, Integer page,
			Integer start, Integer limit, String sort) {
		logger.info("nodeid:" + nodeid + ";page:" + page + ";start:" + start + ";limt:" + limit);
		Sort sortobj = WebSort.getSortByJson(sort);
		PageRequest pageRequest = new PageRequest(page - 1, limit);
		List list = entryIndexCaptureService.findAllByNodeidAndArchivecodeLike(start, limit, nodeid, entryid, sortobj);
		return new PageImpl((List<Tb_entry_index_capture>) list.get(1), pageRequest, (int) list.get(0));
	}

	/**
	 * 获取需归档的记录至归档预览列表
	 * @param dataNodeid  未归节点
	 * @param nodeid  归档目标节点
	 * @param entryids 选定的记录的条目ID
	 * @param ygType    ""首次加载，   "ygd"非首次加载且nodeid不变   "ygdChange"非首次加载且nodeid改变
	 */
	@RequestMapping("/entryIndexCaptures")
	@ResponseBody
	public Page<Tb_entry_index_temp> getEntryIndexCaptures(String entryids, String allEntryids, String dataSource, String isSelectAll,
														   String dataNodeid, String condition, String operator, String content,String type, String ygType,String nodeid, int page, int start, int limit,
														   String sort) {
		if ("true".equals(isSelectAll)) {
            /*String ids = "";
            List<Tb_entry_index_capture> entryIndexCaptures = entryCaptureService.getEntryCaptureList(dataNodeid,
                    condition, operator, content);
            for (int i = 0; i < entryIndexCaptures.size(); i++) {
                String entryid = entryIndexCaptures.get(i).getEntryid();
                if (entryids.indexOf(entryid) == -1) {
                    if (i == entryIndexCaptures.size() - 1) {
                        ids += entryid;
                    } else {
                        ids += entryid + ",";
                    }
                }
            }
            entryids = ids;*/
			entryids = allEntryids;
		}

		logger.info("entryids:" + entryids + ";page:" + page + ";start:" + start + ";limt:" + limit);
		Sort sortobj = WebSort.getSortByJson(sort);
		Page<Tb_entry_index_temp> list = entryIndexCaptureService.getEntryIndexCaptures(entryids, dataSource, page,
				limit, sortobj,ygType,dataNodeid);
		logger.info(list.toString());
		return list;
	}

	/**
	 * 获取需要调序的记录至调序列表
	 * 
	 * @param entryids
	 *            选定的记录的条目ID
	 */
	@RequestMapping("/entryIndexSqCaptures")
	@ResponseBody
	public Page<Tb_entry_index_sqtemp> getEntryIndexSqCaptures(String entryids, String dataSource, String nodeid,
			int page, int start, int limit, String sort) {
		logger.info("entryids:" + entryids + ";page:" + page + ";start:" + start + ";limt:" + limit);
		Sort sortobj = WebSort.getSortByJson(sort);
		Page<Tb_entry_index_sqtemp> list = entryIndexCaptureService.getSqtempEntryIndexCaptures(entryids, dataSource,
				nodeid, page, limit, sortobj);
		logger.info(list.toString());
		return list;
	}

	/**
	 * 判断需要调序的卷内文件档号(除末尾计算项)是否相同
	 * 
	 * @return
	 */
	@RequestMapping("/getFilecode")
	@ResponseBody
	public ExtMsg getFilecode(String ids, String type) {
		String[] idList = ids.split(",");
		List<Tb_entry_index> entry_indexs = new ArrayList<>();
		List<Tb_entry_index_capture> entry_index_captures = new ArrayList<>();
		for (int i = 0; i < idList.length; i++) {
			if (type.equals("数据采集")) {
				Tb_entry_index_capture capture = entryIndexCaptureRepository.findByEntryid(idList[i]);
				entry_index_captures.add(capture);
			} else {
				Tb_entry_index index = entryIndexRepository.findByEntryid(idList[i]);
				entry_indexs.add(index);
			}
		}
		List<String> archiveCode = new ArrayList<>();
		if (type.equals("数据采集")) {
			for (int i = 0; i < entry_index_captures.size(); i++) {
				String value = "";
				List<String> fieldCode = codesetRepository
						.findFieldcodeByDatanodeid(entry_index_captures.get(i).getNodeid());
				List<String> spList = codesetRepository
						.findSplitcodeByDatanodeid(entry_index_captures.get(i).getNodeid());
				for (int j = 0; j < fieldCode.size() - 1; j++) {
					if (j < fieldCode.size() - 2) {
						value += (String) GainField.getFieldValueByName(fieldCode.get(j), entry_index_captures.get(i))
								+ spList.get(j);
					} else {
						value += (String) GainField.getFieldValueByName(fieldCode.get(j), entry_index_captures.get(i));
					}
				}
				archiveCode.add(value);
			}
		} else {
			for (int i = 0; i < entry_indexs.size(); i++) {
				String value = "";
				List<String> fieldCode = codesetRepository.findFieldcodeByDatanodeid(entry_indexs.get(i).getNodeid());
				List<String> spList = codesetRepository.findSplitcodeByDatanodeid(entry_indexs.get(i).getNodeid());
				for (int j = 0; j < fieldCode.size() - 1; j++) {
					if (j < fieldCode.size() - 2) {
						value += (String) GainField.getFieldValueByName(fieldCode.get(j), entry_indexs.get(i))
								+ spList.get(j);
					} else {
						value += (String) GainField.getFieldValueByName(fieldCode.get(j), entry_indexs.get(i));
					}
				}
				archiveCode.add(value);
			}
		}
		for (int i = 0; i < archiveCode.size() - 1; i++) {
			if (!archiveCode.get(i).equals(archiveCode.get(i + 1))) {
				return new ExtMsg(false, "案卷不同", null);
			}
		}
		return new ExtMsg(true, "案卷相同", null);
	}

	/**
	 * 文件归档
	 *
	 * @param entryids 选定记录的条目ID
	 * @return
	 */
	// @LogAnnotation(module = "数据采集",sites = "1,2",startDesc =
	// "归档操作，条目id为：",connect = ",目标节点id为：")
	@RequestMapping("/entryIndexCaptures/filing")
	@ResponseBody
	public ExtMsg filingEntryIndexCaptures(String entryids,String nodeid) {
		SecurityUser userDetails=(SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String[] entryidArr = entryids.split(",");//将前端传过来的entryid字符串切割成字符数组做批量处理
		String nameChange="";//重命名标志
		if ("true".equals(flag)) {
			nameChange="true";
		}
		List<Tb_entry_detail_capture> result = entryIndexCaptureService.filingEntryIndexCaptures(entryids,userDetails.getUserid());
		if (result != null && result.size() > 0) {
			String[] gdEntryidArray = GainField.getFieldValues(result, "entryid").length == 0 ? new String[] { "" }
					: GainField.getFieldValues(result, "entryid");
			SolidifyThread solidifyThread = new SolidifyThread(gdEntryidArray, "capture",nameChange);
			solidifyThread.start(); //开启固化线程
			//增加日志记录
			String ipAddress=LogAop.getIpAddress();
			LogThread logThread = new LogThread(gdEntryidArray, nodeid,"数据采集",userDetails.getLoginname(),userDetails.getRealname(),ipAddress);// 开启固化线程
			logThread.start();
			return new ExtMsg(true, "保存成功", result);
		}
		return new ExtMsg(false, "保存失败", null);
	}

	@RequestMapping("/checkTempArchivecode")
	@ResponseBody
	public ExtMsg checkTempArchivecode(String nodeid) {//判断所有预归档个人条目有档号未生成
		String uniquetag=BatchModifyService.getUniquetagByType("cjgd");
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
		String uniquetag=BatchModifyService.getUniquetagByType("cjgd");
		List<Tb_entry_index_temp> result = entryIndexTempRepository.findByUniquetagOrderBySortsequence(uniquetag);
		String msg="0";
		if(result.size()>0){//还有档号未设置
			msg=result.size()+"";
		}
		return new ExtMsg(false, msg, null);
	}

	@PostMapping("/getParam")
	@ResponseBody
	public ExtMsg getParam(@RequestParam("param") String param) {
		flag = param;
		return new ExtMsg(true, "ok", null);
	}

	private Tb_entry_index_capture alignArchivecode(Tb_entry_index_capture entry, String operate, List<Tb_codeset> codeSettingList) {
		String nodeid = entry.getNodeid();
		// 处理需对齐字段
		List<String> alignFieldList = new ArrayList<>();
		List<String> codeSettingFields = new ArrayList<>();
		List<String> codeSettingSplits = new ArrayList<>();
		if(codeSettingList==null){
			codeSettingList=  codesettingService.findCodesetByDatanodeid(nodeid);
		}
		codeSettingList.forEach(codeset -> {
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
				return null;
			}
			if (entryCaptureService.isNumeric(alignFieldValue)) {
				int currentFieldlength = alignFieldValue.length();// 字段值当前的长度
				if (alignFieldlength != currentFieldlength && alignFieldValue.length() > 0) {
					alignedFieldValue = entryIndexService.alignValue(alignFieldlength,
							Integer.valueOf(alignFieldValue));
					GainField.setFieldValueByName(alignFieldcode, entry, alignedFieldValue);
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
					if (right_organ.getCode() != null && !"".equals(right_organ.getCode())) {
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
		return entry;
	}

	// 判断档号是否存在
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

	@LogAnnotation(module = "数据采集", sites = "1", fields = "title,archivecode", connect = "##题名；,##档号；", startDesc = "保存操作，条目详情：")
	@RequestMapping(value = "/entries", method = RequestMethod.POST)
	@ResponseBody
	public ExtMsg saveEntry(@ModelAttribute("form") EntryCapture entry, String type, String operate) {
		entry.setEntryIndex(entry.getRawEntryIndex());
		entry.setEntryDetial(entry.getRawEntryDetail());
		Tb_data_node node = entryIndexService.getNodeLevel(entry.getNodeid());
		String code = alignArchivecode(entry.getEntryIndex(), operate,null).getArchivecode();
		if (!code.isEmpty()) {// 如果档号不为空
			// 查询当前节点所有数据的档号,判断档号的唯一性
			List<String> archivecode = entryIndexCaptureRepository.findCodeByNodeid(entry.getNodeid());
			if (archivecode.size() > 0) {
				if (type.equals("add") && isExist(code, archivecode)) {
					return new ExtMsg(false, "保存失败，档号重复！", null);
				}
				if (type.equals("modify")) {
					Tb_entry_index_capture entryIndex = entryIndexCaptureRepository.findByEntryid(entry.getEntryid());
					// 如果修改了档号
					if (entryIndex.getArchivecode() != null && !code.equals(entryIndex.getArchivecode())
							&& isExist(code, archivecode)) {
						return new ExtMsg(false, "保存失败，档号重复！", null);
					}
				}
			}
		} else {
			// 如果档号为空,且非未归管理
			if (!node.getNodename().equals("未归管理") && node.getNodename().equals("文件管理") && node.getNodename().equals("资料管理")) {
				return new ExtMsg(false, "保存失败，档号为空", null);
			}
		}
		EntryCapture result = entryCaptureService.saveEntry(entry, type);
		return new ExtMsg(result != null ? true : false, result != null ? "保存成功" : "保存失败", result);
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
		EntryBase capture = new EntryBase();
		List<Tb_data_template> templates = templateRepository.findByNodeid(nodeid);// 查找到当前节点的模板信息
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String organid = entryIndexService.getOrganidByNodeidLoop(nodeid);// 机构id
		List<String> codeInfo = new ArrayList<>();
		codeInfo.add("filecode");// 案卷号
		codeInfo.add("catalog");// 目录号
		codeInfo.add("responsible");// 责任者
		EntryCapture entryCapture = entryCaptureService.getEntry(entryid);
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
					String value = (String) GainField.getFieldValueByName(codesets.get(j),entryCapture);
					GainField.setFieldValueByName(codesets.get(j), capture, value);
				}
			}
			if (template.getFieldcode().equals("filingyear")) {// 归档年度
				if (year != null && !"".equals(year)) {
					capture.setFilingyear(year);
				} else {
					capture.setFilingyear(String.valueOf(cal.get(Calendar.YEAR)));
				}
			} else if (template.getFieldcode().equals("descriptiondate")) {// 著录时间
				capture.setDescriptiondate(df.format(System.currentTimeMillis()));
			} else if (template.getFieldcode().equals("descriptionuser")) {// 著录人
				capture.setDescriptionuser(userDetails.getRealname());
			} else if (template.getFieldcode().equals("organ")) {// 机构
				capture.setOrgan(organ == null ? "" : organ);
			} else if (template.getFieldcode().equals("funds")) {// 全宗号
				if (defaultFunds != null && !"".equals(defaultFunds)) {
					capture.setFunds(defaultFunds);
				} else {
					if (funds != null) {// 如果是单位机构的话,直接填充获取到的全宗号
						capture.setFunds(funds);
					} else {// 如果是部门机构的话,需要获取到所属单位的全宗号
						capture.setFunds(unitFunds == null ? "" : unitFunds);
					}
				}
			} else {
				// 如果模板当中的默认值不为空的话,卷内著录or案卷著录时填充模板默认值
				if (template.getFdefault() != null && !template.getFdefault().equals("")) {
					GainField.setFieldValueByName(template.getFieldcode(), capture, template.getFdefault());
				}
			}
		}
		return new ExtMsg(true, "获取初始值成功", capture);
	}

    @RequestMapping(value = "/getDefaultInfos", method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg getDefaultInfos(String nodeid, String entryid, String type) {
        EntryBase capture = new EntryBase();
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
                capture.setOrgan(organ == null ? "" : organ);
            } else if (template.getFieldcode().equals("funds")) {// 全宗号
                if (defaultFunds != null && !"".equals(defaultFunds)) {
                    capture.setFunds(defaultFunds);
                } else {
                    if (funds != null) {// 如果是单位机构的话,直接填充获取到的全宗号
                        capture.setFunds(funds);
                    } else {// 如果是部门机构的话,需要获取到所属单位的全宗号
                        capture.setFunds(unitFunds == null ? "" : unitFunds);
                    }
                }
            }
        }
        return new ExtMsg(true, "获取初始值成功", capture);
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
									  String appraisaltype) {
		List<Tb_entry_index_temp> result = new ArrayList<>();
		Map<String, String> entryidEntryretentionMap = new HashMap<>();
		String uniquetag=BatchModifyService.getUniquetagByType("cjgd");
		List<String> entryids=entryIndexTempRepository.findEntryidByUniquetag(uniquetag);
		if (appraisaltype != null && !"".equals(appraisaltype)) {
			List<String> originalList = Arrays.asList(filingValuesStrArr);
			List<String> filingValuesStrList = new ArrayList<>();
			filingValuesStrList.addAll(originalList);
			for (String entryid : entryids) {
				String entryretentionValue = appraisalStandardService
						.getEntryretentionByEntryidAndAppraisaltype(entryid, appraisaltype, "数据采集");
				entryidEntryretentionMap.put(entryid.trim(), entryretentionValue);
			}
		}
		boolean value = false;
		List<String> codeSettingFieldList = codesettingService.getCodeSettingFields(nodeid);
		if (filingValuesStrArr.length < codeSettingFieldList.size()) {
			value = true;
		}
		Object entry = entryIndexCaptureService.generateArchivecode(String.join(",",entryids), nodeid, filingValuesStrArr,
				entryidEntryretentionMap,uniquetag);
		if (!entry.getClass().toString().equals("class java.lang.String")) {
			result = (List<Tb_entry_index_temp>) entry;
			if (result != null && result.size() > 0) {
				if (value) {
					return new ExtMsg(true, "需要调整计算项值", result);// 此处生成的档号需进一步调整计算项值
				}
			}
			return new ExtMsg(true, "不需要调整计算项值", result);
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

	@RequestMapping(value = "/ajustAllCalData", method = RequestMethod.POST)
	@ResponseBody
	public ExtMsg ajustAllCalData(String nodeid, String info) {//生成档号
		String uniquetag=BatchModifyService.getUniquetagByType("cjgd");
		List<Tb_entry_index_temp> result = entryIndexTempService.ajustAllCalData(uniquetag, info,"数据采集",nodeid);
		if (result != null && result.size() > 0) {
			return new ExtMsg(true, "生成档号成功", result);
		}
		return new ExtMsg(false, "生成档号失败", null);
	}

	/**
	* 移交操作——单据状态改变
	*
	* @param transdoc 移交的form信息
	* @param nodeid  节点
	* @param condition 检索条件
	* @param operator 检索操作
	* @param spman 审核
	* @param content 检索内容
	* @param isSynch 是否同步卷内 只有案卷节点有权限置为true
	* @return {@link com.wisdom.web.entity.ExtMsg}
	* @throws
	*/
	@RequestMapping("/sendformSubmit")
	@ResponseBody
	public ExtMsg sendformSubmit(Tb_transdoc transdoc, String nodeid, String condition, String operator,String spman,
								 String content,boolean  isSynch,String taskid,String approvenodeid,String volumeNodeId,
								 HttpServletRequest request) {
		String[] entryids =(String[])request.getSession().getAttribute("choiceEntryIds");//获取最终选择的非卷内条目;
		List<String> jnEntryIds=(List<String>)request.getSession().getAttribute("jnEntryIds");//所有的卷内条目
		if("true".equals(auditOpened)){ // 当数据审核打开时 选择移交的数据会生成审批单 送给审核人进行审核。
			Tb_transdoc tb_transdoc =  acquisitionService.transdocFormSubmit(entryids,transdoc, nodeid, Tb_transdoc.STATE_TRANSFOR,spman,"档案",isSynch,approvenodeid,volumeNodeId);
			webSocketService.noticeRefresh();
			return new ExtMsg(true, "成功移交到数据审核", tb_transdoc);
		}else{ //当数据审核没有打开时 直接将条目移交到数据管理
			return acquisitionService.transforAllEntry(entryids,transdoc,nodeid,isSynch,taskid,jnEntryIds);
		}
	}

	/**
	 * 移交操作——条目状态改变
	 * 
	 * @param entryids
	 * @param innserids
	 * @param transdocid
	 * @return
	 */
	@RequestMapping(value = "/entries/transfor")
	@ResponseBody
	public ExtMsg modifyEntry(String entryids, String innserids, String transdocid, String nodeid, String condition, String operator,
							  String content,String volumeNodeId,String parententryids,HttpServletRequest request) {
		String[] entryidData = (String[])request.getSession().getAttribute("choiceEntryIds");//获取最终选择的非卷内条目;;
		List<String> jnEntryIds=(List<String>)request.getSession().getAttribute("jnEntryIds");//所有的卷内条目
		int num = acquisitionService.transfor(entryidData, transdocid,Tb_transdoc_entry.STATUS_AUDIT,jnEntryIds);
		List<String[]> subChoiceAry = new InformService().subArray(entryidData, 1000);// 处理参数超出问题
		for (String[] strings : subChoiceAry) {//移除加入移交案卷的条目
			transdocPreviewRepository.deleteByEntryidIn(strings);
		}
		entryidData=new String[jnEntryIds.size()];
		List<String[]> subJnAry = new InformService().subArray(jnEntryIds.toArray(entryidData), 1000);// 处理参数超出问题
		for (String[] strings : subJnAry) {//移除加入移交卷内的条目
			transdocPreviewRepository.deleteByEntryidIn(strings);
		}
		delTransWriteLog(entryidData, "数据采集", "数据移交");// 写日志
		String[] jnDate=new String[jnEntryIds.size()];
		delTransWriteLog(jnEntryIds.toArray(jnDate), "数据采集", "数据移交");// 卷内条目写日志
		if (num > 0) {
			return new ExtMsg(true, "移交成功", num);
		}
		return new ExtMsg(false, "移交失败", null);
	}

	/**
	 * 导出ureport报表PDF  docids:ids.join(","),  reportName:'移交单据管理_已审核'
	 * @param docids
	 * @param reportName
	 * @param
	 * @param
	 */
	@RequestMapping("/getUreportPdf")
	@ResponseBody
	public ExtMsg writeCataloguePdf(String docids,String reportName){
		return acquisitionService.writeCataloguePdf(docids,reportName);
	}

	/**
	 * 生成查无此档报表水印文件，返回base64文件流
	 */
	@RequestMapping("/getWaterprintPdf")
	@ResponseBody
	public ExtMsg getWaterprintPdf(HttpServletRequest request,String filenoneid,String reportName,String watermarkName){
		//String url = "/ureport/pdf?entryid=" + entryid + "&booknumber=" + booknumber + "&_u=file:JNWJML.ureport.xml&_t=1,4,5,6,7&_i=1";
		String url = "/ureport/pdf?filenoneid=" + filenoneid + "&_u=file:"+reportName+".ureport.xml";
		String dataStr=DateUtil.getCurrentTimeStr();
		String filePath=rootpath+ File.separator +"temp"+ File.separator +"ureport"+ File.separator +dataStr+".pdf";
		//下载pdf报表文件到指定路径
		acquisitionService.writeUreportPdf(url,filePath);
		//对该文件加【原件】水印
		//List<Tb_watermark> watermarks = watermarkRepository.findByTitle("报表原件水印");
		List<Tb_watermark> watermarks = watermarkRepository.findByTitle(watermarkName);
		if(watermarks.size()>0){
			Tb_watermark watermark=watermarks.get(0);
			String waterFilePath = watermarkUtil.getWatermarkPdf(watermark,filePath,null,2052,request);
			ExtMsg newExtMsg= acquisitionService.getFileBase64(waterFilePath);
			try{//最后删除临时pdf报表文件
				File pdfFile=new File(filePath);
				pdfFile.delete();
			}catch(Exception e){
				e.printStackTrace();
			}
			return newExtMsg;
		}else{
			return new ExtMsg(false, "水印设置不存在，生成水印报表失败", null);
		}
	}

	/**
	 * 获取pdf文件的base64码
	 * @param docid
	 * @param
	 */
	@RequestMapping("/getFileBase64")
	@ResponseBody
	public ExtMsg getFileBase64(String docid,String taskid){
		if(taskid!=null&&!"".equals(taskid)){
			docid=transdocRepository.getByTaskid(taskid).getDocid();
		}
		String filepath=rootpath+ File.separator +"transdoc"+ File.separator +docid.trim()+".pdf";
		return acquisitionService.getFileBase64(filepath);
	}

	/**
	 * 获取移交单的的电子签章base64码
	 * @param docid 移交单id
	 */
	@RequestMapping("/getSigncode")
	@ResponseBody
	public ExtMsg getSigncode(String docid){
		List<Tb_ca_transfor> caTransforList=caTransforRepository.findByDocid(docid);
		if(caTransforList.size()<1){
			return new ExtMsg(false, "没有相关数据", null);
		}
		Tb_ca_transfor caTransfor=caTransforList.get(0);
		String transforcaid=caTransfor.getTransforcaid();//移交证书id
		String editcaid=caTransfor.getEditcaid();//审核证书id
		String transforSign="";//移交签章Base64编码
		String editSign="";//审核签章Base64编码
		if(transforcaid!=null){
			transforSign=caRepository.findSigncodeByCaid(transforcaid);
		}
		/*if(editcaid!=null){
			editSign=caRepository.findSigncodeByCaid(editcaid);
		}*/
		Tb_ca_transfor copy=new Tb_ca_transfor();
		BeanUtils.copyProperties(caTransfor,copy);
		copy.setTransforcaid(transforSign);
		//copy.setEditcaid(editSign);
		return new ExtMsg(true, "获取成功", copy);
	}

	/**
	 * base64码转存为pdf文件
	 * @param docid 移交单id
	 * @param pdfData pdf文件BASE64文件流
	 * @param type 移交签章 1   审核签章  2
	 * @param usrCertNO 数字证书编号
	 */
	@RequestMapping("/generatePdf")
	@ResponseBody
	public ExtMsg generatePdf(String docid,String pdfData,String type,String usrCertNO){
		BufferedInputStream bin = null;
		FileOutputStream fout = null;
		BufferedOutputStream bout = null;
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] bytes = decoder.decodeBuffer(pdfData);
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			bin = new BufferedInputStream(bais);
			String filePath=rootpath+ File.separator +"transdoc"+ File.separator +docid+".pdf";
			File thumDir = new File(filePath).getParentFile();
			if (!thumDir.exists()) {// 创建文件夹，防止下面生成文件不成功
				thumDir.mkdirs();
			}
			File file = new File(filePath);
			//File file = new File("D:/169.pdf");
			fout = new FileOutputStream(file);
			bout = new BufferedOutputStream(fout);
			byte[] buffers = new byte[1024];
			int len = bin.read(buffers);
			while(len != -1){
				bout.write(buffers, 0, len);
				len = bin.read(buffers);
			}
			bout.flush();
			//生成pdf文件后，标记已生成pdf移交签章文件  E:\document\transdoc\402881d0737501ba0173752ef2c4000f.pdf
			Tb_transdoc transdoc=transdocRepository.findOne(docid);
			String descri="";
			if("1".equals(type)){//1 移交签章 ； 2审核签章
				//transdoc.setTransforcasign("Y");//标记移交签章完成
				descri="移交签章";
			}else{
				//transdoc.setEditcasign("Y");//标记审核签章完成
				descri="审核签章";
			}
			transdocRepository.save(transdoc);
			//记录使用签章日志
			transSignLog(usrCertNO,transdoc,descri);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				bin.close();
				fout.close();
				bout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ExtMsg(true, "生成签章PDF成功", null);
	}

	@RequestMapping("/openPdfFile/{fileName}")
	@ResponseBody
	public void openSipFile(@PathVariable String fileName,  HttpServletResponse response) {

		try {
			if(fileName!=null&&!"".equals(fileName)&&!fileName.endsWith(".pdf")){
				fileName+=".pdf";
			}
			String filePath=rootpath+ File.separator +"transdoc"+ File.separator +fileName;
			File html_file = new File(filePath);
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + new String(fileName.getBytes("GBK"), "ISO-8859-1") + "\"");
			//response.setContentType("application/octet-stream");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/pdf");
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
			e.printStackTrace();
		}
	}

	/**
	 * PDF签章验证
	 */
	@RequestMapping("/PDFVerifySign")
	@ResponseBody
	public ExtMsg PDFVerifySign(String docid) throws ServletException, IOException {
		String pdfFilePath = "D:/project/网证通Demo/out/artifacts/CryptoDemo_war_exploded/pdfVerify/t2.pdf";//验证文档
		if(!"".equals(docid)){
			pdfFilePath=rootpath+ File.separator +"transdoc"+ File.separator +docid+".pdf";
		}
		try {
			ISignatureVerifier iSignatureVerifier = new SignatureVerifierImpl(pdfFilePath, null);
			int sCount = iSignatureVerifier.getSignaturesCount();
			System.out.println("======================================");
			System.out.println("签名数量:"+sCount);
			for(int i = 1;i<=sCount;i++){
				System.out.println("======================================");
				System.out.println("正在验证第"+i+"个签名");
				//验证级别：0.仅解释，不验证；1.仅验证签名本身，不验证证书；2.验证签名和证书，但是不验证证书是否作废；3.验证签名和证书，并验证签名证书是否作废。但不验证CA证书是否作废；4.验证签名和整个证书路径，包括包括它们是否作废。
				if(iSignatureVerifier.verifySignature(i, SignedData.VERIFY_LEVEL_VERIFY_CERT)==1){
					System.out.println("签名验证成功!");
					System.out.println("==================");
					System.out.println("以下是签名相关信息：");
					System.out.println("签名者:"+iSignatureVerifier.getSignName());
					System.out.println("签名日期:"+iSignatureVerifier.getSignDate());
					System.out.println("签名证书:\r\n"+iSignatureVerifier.getSignCert());
					System.out.println("签名域的域名:"+iSignatureVerifier.getSignFieldName(i));
					System.out.println("签名地点:"+iSignatureVerifier.getLocation());
					System.out.println("签名理由:"+iSignatureVerifier.getReason());
					System.out.println("签名Hash算法:"+iSignatureVerifier.getHashAlgorithm());
					return new ExtMsg(true, "签名验证成功!"+" 签名者:"+iSignatureVerifier.getSignName()+" 签名日期:"+iSignatureVerifier.getSignDate(), null);
				}else {
					System.out.println("PDF第"+i+"个签名验证失败！");
					return new ExtMsg(true, "PDF第"+i+"个签名验证失败！", null);
				}
			}
			//out.write("PDF签名验证完成！");
		} catch (Exception e) {
			e.printStackTrace();
			//out.write(e.getMessage());
		}
		return new ExtMsg(true, "验证成功", null);
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
			//当状态是全选的时候，entryids为取消选择的条目
			String ids="";
			List<Tb_entry_index_capture> entryIndexCaptures = entryCaptureService.getEntryCaptureList(nodeid, condition,
					operator, content);
			for (int i = 0; i < entryIndexCaptures.size(); i++) {
				if(entryids.indexOf(entryIndexCaptures.get(i).getEntryid()) == -1){
					if (i == entryIndexCaptures.size() - 1) {
						ids += entryIndexCaptures.get(i).getEntryid();
					} else {
						ids += entryIndexCaptures.get(i).getEntryid() + "、";
					}
				}
			}
			entryidData = ids.split("、");
			info= acquisitionService.pgNumCorrect(ids);
		}else{
			entryidData = entryids.split("、");// 1.所选数据
			info= acquisitionService.pgNumCorrect(entryids);
		}
		for (String entryid : entryidData) {
			if (info.indexOf(entryid) == -1) {
				logAop.generateManualLog(startTime, LogAop.getCurrentSystemTime(),
						System.currentTimeMillis() - startMillis, "数据采集", "页数矫正，条目id为：" + entryid);
			}
		}
		if ("".equals(info)) {
			return new ExtMsg(true, "操作成功", null);
		} else {
			return new ExtMsg(false, info, null);
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
			logger.info("=================开始进行统计项更新(数据采集)==========================\n" + entryids);
			info = acquisitionService.statisticUpdate(entryidData);
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

	/**
	 * 上调
	 * 
	 * @param currentId
	 * @param nodeid
	 * @return
	 */
	@RequestMapping("/upInnerFile")
	@ResponseBody
	public ExtMsg upInnerFile(String currentId, String nodeid, String type) {
		String uniquetag = BatchModifyService.getUniquetag();
		// 修改当前条目卷内顺序号 - 1(如果结果<=0,那么默认为1)
		Tb_entry_index_sqtemp currentSqtem = entryIndexSqTempRepository.findByEntryidAndUniquetag(currentId, uniquetag);// 找到当前条目数据
		if (currentSqtem.getCalvalue() == null) {
			return new ExtMsg(false, "请完善当前卷内文件信息", null);
		}
		if (Integer.valueOf(currentSqtem.getCalvalue()) == 1) {
			return new ExtMsg(false, "第一条数据无法进行上调操作", null);
		}
		List<String> sqtemps = entryIndexSqTempRepository.findEntryidByNodeidAndUniquetag(nodeid, uniquetag);
		String lastId = "";
		if (sqtemps.indexOf(currentId) >= 1) {
			lastId = sqtemps.get(sqtemps.indexOf(currentId) - 1);// 找到上一条的条目信息
		}
		List<String> codeSet = codesetRepository.findFieldcodeByDatanodeid(nodeid);
        String calvalue = "";
        if (codeSet.size() > 0) {
        	calvalue = codeSet.get(codeSet.size() - 1);
        }
		Integer currentInnerfile = Integer.valueOf(currentSqtem.getCalvalue()) - 1;
		Integer currentLength = codesetRepository.findFieldlengthByDatanodeidAndFieldcode(currentSqtem.getNodeid(),
				calvalue);
		String currentValue = entryIndexService.alignValue(currentLength, currentInnerfile);
		entryIndexSqTempRepository.updateCalvalueByEntryidAndUniquetag(currentValue, currentId, uniquetag);
		// 重新生成档号
		String currentArchivecode = getArchivecode(nodeid, currentValue, currentSqtem.getEntryid(), type);
		entryIndexSqTempRepository.updateNewarchivecodeByEntryidAndUniquetag(currentArchivecode, currentId, uniquetag);
		if (lastId != null && !lastId.equals("")) {
			// 修改上一条目卷内顺序号 + 1
			Tb_entry_index_sqtemp lastSqtem = entryIndexSqTempRepository.findByEntryidAndUniquetag(lastId, uniquetag);// 找到上一条目数据
			Integer lastInnerfile = Integer.valueOf(lastSqtem.getCalvalue()) + 1;
			Integer lastLength = codesetRepository.findFieldlengthByDatanodeidAndFieldcode(lastSqtem.getNodeid(),
					calvalue);
			String lastValue = entryIndexService.alignValue(lastLength, lastInnerfile);
			entryIndexSqTempRepository.updateCalvalueByEntryidAndUniquetag(lastValue, lastId, uniquetag);
			// 重新生成档号
			String lastArchivecode = getArchivecode(nodeid, lastValue, lastSqtem.getEntryid(), type);
			entryIndexSqTempRepository.updateNewarchivecodeByEntryidAndUniquetag(lastArchivecode, lastId, uniquetag);
		}
		return new ExtMsg(true, "上调成功", null);
	}

	/**
	 * 获取新的档号值
	 * 
	 * @param value
	 *            新计算项值
	 * @return
	 */
	private String getArchivecode(String nodeid, String value, String entryid, String type) {
		List<String> fieldCode = codesetRepository.findFieldcodeByDatanodeid(nodeid);
		List<String> spList = codesetRepository.findSplitcodeByDatanodeid(nodeid);
		String currentArchivecode = "";
		if ("数据采集".equals(type)) {
			Tb_entry_index_capture entry_index_capture = entryIndexCaptureRepository.findByEntryid(entryid);
			// 重新生成档号
			for (int i = 0; i < fieldCode.size(); i++) {
				if (i < fieldCode.size() - 1) {
					currentArchivecode += GainField.getFieldValueByName(fieldCode.get(i), entry_index_capture)
							+ spList.get(i);
				} else {
					currentArchivecode += value;
				}
			}

		} else {
			Tb_entry_index entry_index = entryIndexRepository.findByEntryid(entryid);
			// 重新生成档号
			for (int i = 0; i < fieldCode.size(); i++) {
				if (i < fieldCode.size() - 1) {
					currentArchivecode += GainField.getFieldValueByName(fieldCode.get(i), entry_index) + spList.get(i);
				} else {
					currentArchivecode += value;
				}
			}
		}
		return currentArchivecode;
	}

	/**
	 * 下调
	 * 
	 * @param currentId
	 * @param nodeid
	 * @return
	 */
	@RequestMapping("/downInnerFile")
	@ResponseBody
	public ExtMsg downInnerFile(String currentId, String nodeid, String type) {
		String uniquetag = BatchModifyService.getUniquetag();
		Tb_entry_index_sqtemp currentSqtem = entryIndexSqTempRepository.findByEntryidAndUniquetag(currentId, uniquetag);// 找到需要进行下调的条目数据
		if (currentSqtem.getCalvalue() == null) {
			return new ExtMsg(false, "请完善当前卷内文件信息", null);
		}
		List<String> sqtemps = entryIndexSqTempRepository.findEntryidByNodeidAndUniquetag(nodeid, uniquetag);
		String nextId = "";
		if (sqtemps.indexOf(currentId) + 1 < sqtemps.size()) {
			nextId = sqtemps.get(sqtemps.indexOf(currentId) + 1);// 找到下一条的条目信息
		}
		// 修改当前条目卷内顺序号 + 1
		List<String> codeSet = codesetRepository.findFieldcodeByDatanodeid(nodeid);
        String calvalue = "";
        if (codeSet.size() > 0) {
        	calvalue = codeSet.get(codeSet.size() - 1);
        }
		Integer currentInnerfile = Integer.valueOf(currentSqtem.getCalvalue()) + 1;
		Integer currentLength = codesetRepository.findFieldlengthByDatanodeidAndFieldcode(currentSqtem.getNodeid(),
				calvalue);
		String currentValue = entryIndexService.alignValue(currentLength, currentInnerfile);
		entryIndexSqTempRepository.updateCalvalueByEntryidAndUniquetag(currentValue, currentId, uniquetag);// 修改当前的条目信息卷内顺序号
																											// +1
		// 重新生成档号
		String currentArchivecode = getArchivecode(nodeid, currentValue, currentSqtem.getEntryid(), type);
		entryIndexSqTempRepository.updateNewarchivecodeByEntryidAndUniquetag(currentArchivecode,
				currentSqtem.getEntryid(), uniquetag);

		if (nextId != null && !nextId.equals("")) {
			// 修改下一条目卷内顺序号 - 1(如果结果<=0,那么默认为1)
			Tb_entry_index_sqtemp nextSqtem = entryIndexSqTempRepository.findByEntryidAndUniquetag(nextId, uniquetag);// 找到下一条目数据
			Integer nextInnerfile;
			if(nextSqtem.getCalvalue() != null){
				nextInnerfile = Integer.valueOf(nextSqtem.getCalvalue()) - 1;
			}else{
				nextInnerfile = 1;
			}
			Integer nextLength = codesetRepository.findFieldlengthByDatanodeidAndFieldcode(nextSqtem.getNodeid(),
					calvalue);
			String nextValue = entryIndexService.alignValue(nextLength, nextInnerfile);
			entryIndexSqTempRepository.updateCalvalueByEntryidAndUniquetag(nextValue, nextId, uniquetag);// 修改下一条目信息卷内顺序号
																											// -
																											// 1
			// 重新生成档号
			String nextArchivecode = getArchivecode(nodeid, nextValue, nextSqtem.getEntryid(), type);
			entryIndexSqTempRepository.updateNewarchivecodeByEntryidAndUniquetag(nextArchivecode,
					nextSqtem.getEntryid(), uniquetag);
		}
		return new ExtMsg(true, "下调成功", null);
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
			List<Tb_entry_index_capture> captures = entryIndexCaptureRepository.findByNodeid(nodeid);// 找到当前节点所有条目信息
			String repeat = "";
			for (int j = 0; j < captures.size(); j++) {
				Tb_entry_index_capture capture = captures.get(j);
				for (int i = 0; i < sqtemps.size(); i++) {
					Tb_entry_index_sqtemp sqtemp = sqtemps.get(i);
					// 如果档号相同且entryid不同,相同档号的数据采集条目不在临时调序表中的话
					if (sqtemp.getNewarchivecode().equals(capture.getArchivecode())
							&& !sqtemp.getEntryid().equals(capture.getEntryid())
							&& !Arrays.asList(entryList).contains(capture.getEntryid())) {
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
	 * 修改可编辑框数据时更新临时调序表
	 * 
	 * @param entryid
	 * @param field
	 * @param order
	 * @return
	 */
	@RequestMapping("/updateSqtempArchivecode")
	@ResponseBody
	public ExtMsg updateSqtempArchivecode(String entryid, String field, String order, String type) {
		if (entryid != null && !entryid.equals("") && order != null && !order.equals("")
				&& entryCaptureService.isNumeric(order)) {
			String uniquetag = BatchModifyService.getUniquetag();
			if (field.equals("innerfile")) {
				// 1.保存可编辑框输入的卷内顺序号信息,并重新生成档号
				Tb_entry_index_sqtemp sqtempInfo = entryIndexSqTempRepository.findByEntryidAndUniquetag(entryid,
						uniquetag);
				Integer innerfile = Integer.valueOf(order);
				Integer length = codesetRepository.findFieldlengthByDatanodeidAndFieldcode(sqtempInfo.getNodeid(),
						"innerfile");
				String value = entryIndexService.alignValue(length, innerfile);
				String archivecode = getArchivecode(sqtempInfo.getNodeid(), value, sqtempInfo.getEntryid(), type);// 通过用户输入的顺序号生成新档号
				entryIndexSqTempRepository.updateNewarchivecodeByEntryidAndUniquetag(archivecode, entryid, uniquetag);// 修改临时调序表的新档号
				entryIndexSqTempRepository.updateCalvalueByEntryidAndUniquetag(value, entryid, uniquetag);// 修改临时调序表的卷内顺序号
			} else if (field.equals("pageno")) {// 页号
				entryIndexSqTempRepository.updatePagenoByEntryidAndUniquetag(order, entryid, uniquetag);
			} else {// 页数
				entryIndexSqTempRepository.updatePagesByEntryidAndUniquetag(order, entryid, uniquetag);
			}
			return new ExtMsg(true, "修改成功", null);
		}
		return new ExtMsg(false, "输入错误，不能为空，且只能输入整数！", null);
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
			List<Tb_entry_index_capture> captures = entryIndexCaptureRepository.findByEntryidIn(idList);
			for (int i = 0; i < sqtemps.size(); i++) {
				Tb_entry_index_sqtemp sqtemp = sqtemps.get(i);
				Tb_entry_index_capture capture = captures.get(i);
				if (!sqtemp.getNewarchivecode().equals(capture.getArchivecode())) {
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
		for (int i = 0; i < sqtemps.size(); i++) {
			Tb_entry_index_sqtemp sqtemp = sqtemps.get(i);
			Tb_entry_index_capture capture = entryIndexCaptureRepository.findByEntryid(sqtemp.getEntryid());
			if (capture != null) {// 如果数据采集表中还存在当前条目
				// 更新数据采集表中对应条目的卷内顺序号&档号&页号&页数
				entryIndexCaptureRepository.updateInfoByEntryid(sqtemp.getCalvalue(), sqtemp.getNewarchivecode(),
						sqtemp.getPageno(), sqtemp.getPages(), capture.getEntryid());
			}
		}
	}

	/**
	 * 重新移交
	 * 
	 * @param docids
	 * @return
	 */
	@RequestMapping(value = "/reTransfor")
	@ResponseBody
	public ExtMsg reTransfor(String docids) {
		String[] docidData = docids.split(",");
		ExtMsg result = acquisitionService.reTransforDocs(docidData);
		if (!result.isSuccess() && result.getData() != null) {//// 单据中包含已移交过的条目,所有操作均中断，不进行后续日志记录
			return result;
		}
		delTransWriteLog(docidData, "数据采集", "重新移交数据");// 写日志
		return result;
	}

	// @LogAnnotation(module = "数据采集",startDesc = "删除操作，条目id为：",sites = "1")
	@RequestMapping("/delete")
	@ResponseBody
	public ExtMsg delEntry(String entryids, String isSelectAll, String nodeid, String condition, String operator,
						   String content,String model) {
		String[] entryInfo = entryids.split(",");
		String[] entryidData;
		if ("true".equals(isSelectAll)) {
			List<Tb_entry_index> entryIndexCaptures = entryIndexService.getLognIndexCapture(nodeid, condition, operator, content);
			if (entryIndexCaptures.size() > 0) {
				List<String> tempEntry = new ArrayList<>();
				List<String> entryList = Arrays.asList(entryInfo);
				for (int i = 0; i < entryIndexCaptures.size(); i++) {
					String entryid = entryIndexCaptures.get(i).getEntryid();
					if (!entryList.contains(entryid)) {
						tempEntry.add(entryid);
					}
				}
				entryidData = tempEntry.toArray(new String[tempEntry.size()]);
			} else {
				entryidData = new String[] {};
			}
		} else {
			entryidData = entryInfo;
		}
		Integer del = 0;
		List<String[]> subAry = new InformService().subArray(entryidData, 1000);// 处理ORACLE1000参数问题
		for (String[] ary : subAry) {
			del += entryCaptureService.delEntryOnly(ary);
		}
		delTransWriteLog(entryidData, "数据采集", "删除数据");// 写日志
		if (del > 0) {
			//删除条目成功后，再启用线程默默的删除关联的电子文件等
			DelThread delThread = new DelThread(entryidData,"数据采集");// 开启线程
			delThread.start();
			return new ExtMsg(true, "删除成功", del);
		}
		return new ExtMsg(false, "删除失败", null);
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
		return entryIndexCaptureService.getFileNodeidAndEntryid(nodeid, entryid);
	}

	/**
	 * 获取 计算项字段名 或 字段名及数值
	 * 
	 * @param entryIndexCapture
	 * @return
	 */
	@RequestMapping("/getCalValue")
	@ResponseBody
	public ExtMsg getCalValue(Tb_entry_index_capture entryIndexCapture, String nodeid, String nodename, String type) {
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
			// if(number==null || number==0){
			// return new ExtMsg(false,"请检查计算项单位长度是否设置正确",null);
			// }
			String calValueStr = "";
			String codeSettingFieldValues = "";//还没有拼接统计项的档号字段
			if (!GainField.objectIsNull(entryIndexCapture, 0) || codeSettingList.size() == 1) {
				Integer calValue = null;
				try {
					calValue = entryIndexCaptureService.getCalValue(entryIndexCapture, nodeid, codeSettingList,type);
				} catch (NumberFormatException e) {
					return new ExtMsg(false, "获取档号失败，请检查档号构成字段（" + value + "）是否包含非数字字符", null);
				}
				if (calValue == null) {
					return new ExtMsg(false, "获取档号失败，请检查档号构成字段（" + value + "）输入值是否为空。", null);
				}
				// 将计算项数值补0到指定位数，若calValue为null,且number数值大于4,则生成的字符串为：空格+"null",需去除空格
				calValueStr = entryIndexService.alignValue(number, calValue);
				GainField.setFieldValueByName(calFieldName, entryIndexCapture, calValueStr);
				//archiveCode = alignArchivecode(entryIndexCapture, "数据采集",codeSettingList).getArchivecode();\
				if(codeSettingList.size()==1){
					archiveCode = calValueStr;
				}else{
					archiveCode = entryIndexCapture.getArchivecode()+codeSettingList.get(size-1).getSplitcode()+calValueStr;
				}
				GainField.setFieldValueByName("archivecode", entryIndexCapture, archiveCode);
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
	 * 获取需要加载至移交表单的单据
	 * 
	 * @param entryids
	 * @return
	 */
	@RequestMapping("/getNewDoc")
	@ResponseBody
	public ExtMsg getNewDoc(String entryids, String isSelectAll, String nodeid, long totalCount, String condition, String operator,
							String content,String nodefullname,HttpServletRequest request,String transforType) {
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Tb_transdoc transdoc = new Tb_transdoc();
		transdoc.setTransuser(userDetails.getRealname());
		transdoc.setTransorgan(userDetails.getOrganid());
		List<Tb_electronic_capture> electronicCaptures=new ArrayList<>();
		//获取执行验证保存的卷内条目
		List<String> volumeEntryIds=(List<String>)request.getSession().getAttribute("volumeEntryIds");
		if(volumeEntryIds==null) {
			volumeEntryIds = new ArrayList<>();
		}
		List<String> jnEntryIds=new ArrayList<>();
		String antiElectionEntryIds=(String)request.getSession().getAttribute("AntiElectionEntryIds");//获取列表反选的所有条目id
		if(antiElectionEntryIds==null){
			antiElectionEntryIds="";
		}
		String gridChoiceEntryIds=(String)request.getSession().getAttribute("gridChoiceEntryIds");//获取列表选择的条目id
		if(gridChoiceEntryIds==null){
			gridChoiceEntryIds="";
		}
		List<String> allIds=new ArrayList<>();//案卷和卷内id;
		if ("true".equals(isSelectAll)) {
			entryids+=antiElectionEntryIds;
			//当状态是全选的时候，entryids为取消选择的条目
			List<Tb_entry_index> entryIndexCaptures;
			if("1".equals(transforType)){//判断处理移交还是直接移交 2 直接移交 1 处理移交
				entryIndexCaptures= entryIndexService.getDocPreviewEntry(nodeid, condition, operator, content);
			}else {
				entryIndexCaptures=entryIndexService.getLognIndexCapture(nodeid, condition, operator, content);
			}
			List<String> entryList = entryIndexCaptures.stream().map(c -> c.getEntryid()).collect(Collectors.toList());
			if(entryList.size()>0){
				List<String> ids=new ArrayList<>();//非卷内
				if(entryids!=null&&entryids.length()>1){
					for (String string : entryList) {
						if("".equals(gridChoiceEntryIds)) {//列表选了所有页
							if (!entryids.contains(string.trim())) {
								ids.add(string);
								allIds.add(string);
							}
						}else {
							if (gridChoiceEntryIds.contains(string.trim())) {//列表没有选所有页
								ids.add(string);
								allIds.add(string);
							}
						}
					}
					if(ids.size()==0){//没有条目直接不通过
						return new ExtMsg(false, "请选择验证通过的数据", null);
					}
					for (String volumeEntryId : volumeEntryIds) {//卷内条目
						if(!entryids.contains(volumeEntryId.trim())){
							allIds.add(volumeEntryId);
							jnEntryIds.add(volumeEntryId);
						}
					}
				}else {
					return new ExtMsg(false, "请选择验证通过的数据", null);
				}

				String[] allStrings = new String[allIds.size()];
				allIds.toArray(allStrings);
				List<String[]> subAry = new InformService().subArray(allStrings, 1000);// 处理参数超出问题
				Long count=Long.valueOf(0);
				for (String[] s : subAry) {
					electronicCaptures.addAll(electronicCaptureRepository.findByEntryidIn(s));
					count+= longRetentionRepository.getAllCountByState(s);
				}
				if(count>0) {
					return new ExtMsg(false, "请选择验证通过的数据", null);
				}
				String[] strings = new String[ids.size()];
				request.getSession().setAttribute("choiceEntryIds",ids.toArray(strings));//最终选择的非卷内条目
				transdoc.setTranscount(Long.parseLong(allStrings.length+""));
			}else {
				return new ExtMsg(false, "请选择验证通过的数据", null);
			}
			transdoc.setDocid("isSelectAll"+entryids);// 条目数太多时会让前端加载不了，改为返回全选标记和取消选择的entryid
		}else{
			String[] entryList=entryids.split(",");
			String ids="",jnIds="";
			for (String volumeEntryId : volumeEntryIds) {//过滤卷内反选的条目
				if(entryids.contains(volumeEntryId.trim())){
					allIds.add(volumeEntryId);
					jnEntryIds.add(volumeEntryId);
					jnIds+=volumeEntryId.trim()+",";
				}
			}
			antiElectionEntryIds+=jnIds;
			for (String string : entryList) {//过滤列表反选的条目
				if(!antiElectionEntryIds.contains(string.trim())){
					ids+=string+",";
					allIds.add(string);
				}
			}
			transdoc.setTranscount(Long.parseLong(ids.split(",").length + ""));
			transdoc.setDocid(ids);// 将选择的条目id临时存放在opendocID上
			electronicCaptures = electronicCaptureRepository.findByEntryidIn(ids.split(","));
			request.getSession().setAttribute("choiceEntryIds",ids.split(","));//最终选择的非卷内条目
		}
		request.getSession().setAttribute("jnEntryIds",jnEntryIds);//所有卷内条目
		long totalsize = 0;  //所有文件总大小
		for(Tb_electronic_capture electronicCapture : electronicCaptures){
			totalsize = totalsize + Integer.parseInt(electronicCapture.getFilesize());
		}
		double totalsizeDoule = (double) totalsize / 1024 / 1024;
		//保存三位小数
		String totalsizeStr =  String.format("%.3f", totalsizeDoule);
		transdoc.setTransfersize(totalsizeStr); //设置移交数据量
		return new ExtMsg(true, "成功", transdoc);
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
		String nodeid = entryIndexCaptureService.findNodeidByEntryid(entryid);
		Tb_entry_index_capture entryIndexCapture = entryIndexCaptureService.findEntryIndexCapture(entryid);
		ExtMsg result = entryIndexCaptureService.modifyJnEntryindexcaptureOrder(entryIndexCapture,
				Integer.parseInt(targetorder), filearchivecode, nodeid);
		if (result != null) {
			if (!result.isSuccess()) {
				return result;
			}
		}
		return new ExtMsg(true, "顺序修改成功", null);
	}

	/**
	 * 根据已选择的卷内文件条目id获取其对应的案卷的条目id及案卷所属节点id
	 * 
	 * @param entryid
	 * @return
	 */
	@RequestMapping("/fileNodeidAndEntryid")
	@ResponseBody
	public Map<String, Object> getFileNodeidAndEntryid(String entryid) {
		return entryIndexCaptureService.getFileNodeidAndEntryidByInnerfileEntryid(entryid);
	}

	/**
	 * 获取当前登陆用户指定数据节点的所有移交单据，包括“已移交”、“已审核”、“已退回”的全部单据
	 * 
	 * @param nodeid
	 *            数据节点id
	 * @return
	 */
	@RequestMapping("/getNodeTransdoc")
	@ResponseBody
	public Page<Tb_transdoc> getNodeReport(String nodeid, int page, int start, int limit, String condition,
			String operator, String content, String sort) {
		Sort sortobj = WebSort.getSortByJson(sort);
		logger.info("nodeid:" + nodeid + ";page:" + page + ";start:" + start + ";limt:" + limit);
		return acquisitionService.findTransdocBySearch(page, limit, condition, operator, content, nodeid, sortobj);
	}

	/**
	 * 删除移交单据
	 * 
	 * @param docids
	 * @return
	 */
	@RequestMapping("/deleteNodeTransdoc")
	@ResponseBody
	public ExtMsg deleteNodeTransdoc(String docids,String taskid) {
		String[] docid = docids.split(",");
		Integer value = transdocRepository.deleteByDocidIn(docid);
		if (value > 0) {
			for (int i = 0; i < docid.length; i++) {
				int[] num = auditService.sendback(docid[i], "",taskid);
				if (num[0] < 0 && num[1] < 0) {
					return new ExtMsg(false, "第" + (i + 1) + "条单据退回失败", null);
				}
			}
			return new ExtMsg(true, "成功删除移交单据", null);
		}
		return new ExtMsg(false, "删除移交单据失败", null);
	}

	/**
	 * 查看单据的详细内容（详细条目列举）
	 * 
	 * @param docid
	 *            单据id
	 * @param docState
	 *            单据状态
	 */
	@RequestMapping("/docEntry")
	@ResponseBody
	public Page getDocEntry(String docid, String docState, int page, int start, int limit, String condition,
			String operator, String content, String sort) {
		logger.info("docid:" + docid + ";page:" + page + ";start:" + start + ";limt:" + limit);
		Sort sortobj = WebSort.getSortByJson(sort);
		String[] entryidData = auditService.getEntryidsByDocid(docid);
		if (Tb_transdoc.STATE_AUDIT.equals(docState)) {// 单据状态为“已审核”，从管理表中获取数据
			return acquisitionService.findDocEntryindexBySearch(page, limit, condition, operator, content, entryidData,
					sortobj);
		}
		return acquisitionService.findDocEntryindexcaptureBySearch(page, limit, condition, operator, content,
				entryidData, sortobj);// 未入库，从采集表中获取数据
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
		Tb_entry_index_capture entryIndexCapture = entryIndexCaptureRepository.findByEntryid(entryid);
		String nodeid = entryIndexCapture.getNodeid();
		List<String> codeSettingFieldList = codesettingService.getCodeSettingFields(nodeid);// 获取档号设置字段集合
		if (codeSettingFieldList.size() == 0) {// 档号字段未设置
			return new ExtMsg(false, "条目保存成功，但插件后续更新统计项及档号操作异常，请检查档号设置信息是否正确", null);
		}
		try {
			entryIndexCaptureService.updateSubsequentData(entryIndexCapture, codeSettingFieldList, flag, pages);
		} catch (NumberFormatException e) {
			return new ExtMsg(false, "条目保存成功，但插件后续更新统计项及档号操作异常，请检查历史计算项或页号输入值是否包含非数字字符", null);
		}
		return new ExtMsg(true, "更新数据成功", null);
	}

	/**
	 * 条目拆除——删除或拆到其它节点（改变数据节点id，设置档号为null）
	 * 
	 * @param entryid
	 *            需拆除条目的条目id
	 * @param dismantleType
	 *            拆除类型（删除或拆到其它节点）
	 * @param nodeid
	 *            拆除的目标节点id
	 * @return
	 */
	@LogAnnotation(module = "数据采集", startDesc = "拆件操作，拆除条目id为：", sites = "1,2,3", connect = "，拆件方式为：,，拆件目标分类节点id为：,。")
	@RequestMapping("/dismantle")
	@ResponseBody
	public ExtMsg dismantle(String entryid, String dismantleType, String nodeid, String title, String syncType) {
		String msg = "";
		try {
			if ("delete".equals(dismantleType)) {
				// entryCaptureService.delEntry(new
				// String[]{entryid});//删除当前拆除条目

				// 先删除卷内，然后删除案卷，最后案卷之后的案卷号全部减一，包括卷内文件
				msg = entryIndexCaptureService.delEntry(entryid, nodeid, title, syncType);
			}
			if ("node".equals(dismantleType)) {
				msg = entryIndexCaptureService.dismantle(entryid, nodeid, title, syncType);// 改变数据节点id，设置档号为null
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

	@RequestMapping("/getSelectAllEntryid")
	@ResponseBody
	public String[] getSelectAllEntryid(String nodeid, String condition, String operator, String content) {
		String[] entryids;
		List<Tb_entry_index_capture> entryIndexCaptures = entryCaptureService.getEntryCaptureList(nodeid, condition,
				operator, content);
		if (entryIndexCaptures.size() > 0) {
			entryids = new String[entryIndexCaptures.size()];
			for (int i = 0; i < entryIndexCaptures.size(); i++) {
				entryids[i] = entryIndexCaptures.get(i).getEntryid();
			}
		} else {
			entryids = new String[] {};
		}
		return entryids;
	}

	/**
	 * 另开线程写日志,避免请求响应时间过久失败问题(删除与移交用)
	 * 
	 * @param entryidData
	 *            数据id数组
	 */
	public void delTransWriteLog(String[] entryidData, String module, String desciStart) {
		String startTime = LogAop.getCurrentSystemTime();// 开始时间
		long startMillis = System.currentTimeMillis();// 开始毫秒数
		SecurityUser securityUser = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal());
		String ip = LogAop.getIpAddress();
		Thread thread = new Thread(() -> {
			List<Tb_log_msg> log_msgs = new ArrayList<>();
			String organ = organService.findFullOrgan("", securityUser.getReplaceOrganid());
			for (String entryid : entryidData) {
				Tb_log_msg logMsg = new Tb_log_msg();
				logMsg.setOrgan(organ);
				logMsg.setRealname(securityUser.getRealname());
				logMsg.setOperate_user(securityUser.getLoginname());
				logMsg.setIp(ip);
				logMsg.setModule(module);
				logMsg.setDesci(desciStart + "，条目id为：" + entryid);
				logMsg.setStartTime(startTime);
				logMsg.setEnd_time(LogAop.getCurrentSystemTime());
				logMsg.setConsume_time(System.currentTimeMillis() - startMillis + "ms");
				log_msgs.add(logMsg);
			}
			logAop.generateManualLog(log_msgs);
		});
		thread.start();
	}

	/**
	 * 另开线程写签章使用日志
	 * @param usrCertNO  证书编号
	 * @param transdoc   移交单据
	 */
	public void transSignLog(String usrCertNO,Tb_transdoc transdoc,String desciStart) {
		String startTime = LogAop.getCurrentSystemTime();// 开始时间
		long startMillis = System.currentTimeMillis();// 开始毫秒数
		SecurityUser securityUser = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal());
		String ip = LogAop.getIpAddress();
		Thread thread = new Thread(() -> {
			List<Tb_log_msg> log_msgs = new ArrayList<>();
			String organ = organService.findFullOrgan("", securityUser.getOrganid());
			String caStr="";
			Tb_user user=userRepository.findByNickname(usrCertNO);//证书绑定用户
			if(user!=null){
				caStr="，证书拥有者为："+user.getLoginname();
			}
			String title=transdoc.getTransfertitle();
			if(title!=null&&title.length()>100){
				title=title.substring(0,100)+"...";
			}
			Tb_log_msg logMsg = new Tb_log_msg();
			logMsg.setOrgan(organ);
			logMsg.setRealname(securityUser.getRealname());
			logMsg.setOperate_user(securityUser.getLoginname());
			logMsg.setIp(ip);
			logMsg.setModule("CA签章");
			logMsg.setDesci(desciStart + "，数值证书编号为：" +usrCertNO+caStr+"，移交单据id为："+transdoc.getDocid()+",题名为："+title);//500字符
			logMsg.setStartTime(startTime);
			logMsg.setEnd_time(LogAop.getCurrentSystemTime());
			logMsg.setConsume_time(System.currentTimeMillis() - startMillis + "ms");
			log_msgs.add(logMsg);
			logAop.generateManualLog(log_msgs);
		});
		thread.start();
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
		return entryCaptureService.getNewFileFormData(entryid, archivecode, nodeid);
	}

	@RequestMapping("/getMissPageCheck")
	@ResponseBody
	public List<RebackMissPageCheck> getMissPageCheck(String[] ids) {
		List<String[]> subAry = new InformService().subArray(ids, 1000);
		List<RebackMissPageCheck> rebackMissPageChecks = new ArrayList<>();
		for (String[] ary : subAry) {
			List<RebackMissPageCheck> MissPageChecks = entryCaptureService.getMissPageCheck(ary);
			rebackMissPageChecks.addAll(MissPageChecks);
		}
		return rebackMissPageChecks;
	}

	@RequestMapping("/getMissPageCheckTotal")
	@ResponseBody
	public int[] getMissPageCheckTotal(String[] ids) {
		return entryCaptureService.getMissPageCheckTotal(ids);
	}


	/**
	 * 根据电子文件id获取电子文件历史版本
	 *
	 * @param eleid
	 * @return
	 */

	@RequestMapping("/getEleVersion")
	@ResponseBody
	public Page<Tb_electronic_version_capture> getElectronicVersion(String eleid,int page,int limit,Sort sort) {
		return entryIndexCaptureService.getElectronicVersion(eleid,page,limit,sort);
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
		int count = entryIndexCaptureService.delElectronicVersion(eleVersionids);
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
		entryIndexCaptureService.rebackElectronicVersion(eleVersionid);
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
		List<Tb_electronic_version_capture> electronic_versions = entryIndexCaptureService.getEleVersionByids(eleVersionids);
		boolean ifFileExists = true;
		String notExistsFilesStr = "";
		for (Tb_electronic_version_capture eleVersion : electronic_versions) {
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
		Tb_electronic_version_capture eleversion = entryIndexCaptureService.getEleVersionByid(eleVersionid);
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
		String zipPath = entryIndexCaptureService.transFiles(eleVersionids);
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

	@RequestMapping(value = "/captureMediaEntries", method = RequestMethod.GET)
	@ResponseBody
	public Page<MediaEntry> getCaptureMediaEntries(String nodeid, String basicCondition, String basicOperator,String parententryid,
												   String basicContent, String condition, String docid, String operator, String content, String info, int page,
												   int limit, String sort, String[] labels,String[] filingyear,String[] entryretention, String groupid) {
		Sort sortobj = WebSort.getSortByJson(sort);
		Page<Tb_index_detail_capture> list;
		if (info != null && "批量操作".equals(info)) {
			list = entryCaptureService.getEntries(this.getClass().getSimpleName(), nodeid,
					Tb_transdoc_entry.STATUS_AUDIT, docid, basicCondition, basicOperator, basicContent, page, limit,
					sortobj,parententryid);
		} else {
			list = entryCaptureService.getMediaEntries(nodeid, Tb_transdoc_entry.STATUS_AUDIT, docid, condition,
					operator, content, page, limit, sortobj, labels,filingyear,entryretention, groupid,parententryid);
		}
		List<Tb_index_detail_capture> teiList = list.getContent();
		List<MediaEntry> eList = entryCaptureService.getMediaEntryCaptures(teiList);
		PageRequest pageRequest = new PageRequest(page - 1, limit);
//        for(MediaEntry lists : eList){
//            if(lists.getIsprint()!=null && "是".equals(lists.getIsprint())){
//                lists.setTitle("<span style='color:blue;'>"+lists.getTitle()+"</span>");
//            }
//        }
		return new PageImpl<MediaEntry>(eList, pageRequest, list.getTotalElements());
	}

	/**
	 * 获取修改选中的临时条目
	 * @param entryid
	 * @return
	 */
	@RequestMapping(value = "/entryTemp/{entryid}", method = RequestMethod.GET)
	@ResponseBody
	public Tb_entry_index_temp  getEntryTemp(@PathVariable String entryid) {
		String uniquetag = BatchModifyService.getUniquetagByType("cjgd");
		return entryIndexTempRepository.findByEntryidInAndUniquetag(entryid,uniquetag);// 找到当前条目数据
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
	public ExtMsg moveup(String currentId, String nodeid) {
		String uniquetag = BatchModifyService.getUniquetagByType("cjgd");
		Tb_entry_index_temp currentTemp = entryIndexTempRepository.findByEntryidInAndUniquetag(currentId,uniquetag);// 找到当前条目数据
		if(currentTemp.getSortsequence() == 0){
			return new ExtMsg(false, "操作失败", null);
		}
		if(Integer.valueOf(currentTemp.getSortsequence()) == 1){
			return new ExtMsg(false, "第一条数据无法进行上移操作", null);
		}
		//查找比选中的顺序号上一条的临时条目的顺序号跟选中的顺序号互相置换
		List<String> temps = entryIndexTempRepository.findEntryidByUniquetag(uniquetag);
		String lastId = "";
		if(temps.indexOf(currentId)>=1){
			lastId = temps.get(temps.indexOf(currentId)-1);// 找到上一条的条目信息
		}else if(temps.indexOf(currentId+"    ")>=1){//sqlserver的char主键多四个空格
			lastId = temps.get(temps.indexOf(currentId+"    ")-1);// 找到上一条的条目信息
		}

		Integer current = Integer.valueOf(currentTemp.getSortsequence());
		if (lastId != null && !lastId.equals("")) {
			Tb_entry_index_temp lastTemp = entryIndexTempRepository.findByEntryidInAndUniquetag(lastId,uniquetag);// 找到上一条目数据
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
	public ExtMsg movedown(String currentId, String nodeid) {
		String uniquetag = BatchModifyService.getUniquetagByType("cjgd");
		Tb_entry_index_temp currentTemp = entryIndexTempRepository.findByEntryidInAndUniquetag(currentId,uniquetag);// 找到当前条目数据
		if(currentTemp.getSortsequence() == 0){
			return new ExtMsg(false, "操作失败", null);
		}
		//查找比选中的顺序号下一条的临时条目的顺序号跟选中的顺序号互相置换
		List<String> temps = entryIndexTempRepository.findEntryidByUniquetag(uniquetag);
		if(Integer.valueOf(currentTemp.getSortsequence()) == temps.size()){
			return new ExtMsg(false, "最后一条数据无法进行下移操作", null);
		}
		String nextId = "";
		if(temps.indexOf(currentId)>=0){
			nextId = temps.get(temps.indexOf(currentId)+1);// 找到下一条的条目信息
		}else if(temps.indexOf(currentId+"    ")>=0){//sqlserver的char主键多四个空格
			nextId = temps.get(temps.indexOf(currentId+"    ")+1);// 找到上一条的条目信息
		}
		Integer current = Integer.valueOf(currentTemp.getSortsequence());
		if (nextId != null && !nextId.equals("")) {
			Tb_entry_index_temp lastTemp = entryIndexTempRepository.findByEntryidInAndUniquetag(nextId,uniquetag);// 找到下一条目数据
			Integer last = Integer.valueOf(lastTemp.getSortsequence());
			//顺序号互相置换
			entryIndexTempRepository.updateSortsequenceByEntryidAndUniquetag(current, nextId, uniquetag);
			entryIndexTempRepository.updateSortsequenceByEntryidAndUniquetag(last, currentId, uniquetag);
		}
		return new ExtMsg(true, "下移成功", null);
	}


	@RequestMapping("/getTempEntryids")
	@ResponseBody
	public ExtMsg getTempEntryids(String nodeid,String type) {//判断所有预归档个人条目有档号未生成
		String uniquetag = "";
		if("capture".equals(type)){
			uniquetag=BatchModifyService.getUniquetagByType("cjgd");
		}else{
			uniquetag=BatchModifyService.getUniquetagByType("glgd");
		}
		List<Tb_entry_index_temp> results = entryIndexTempRepository.getTempEntryids(nodeid,uniquetag);
		return new ExtMsg(true, "", results);
	}

	/**
	 * 启用线程写日志
	 * @param entryidList  entryid集合
	 * @param module  功能模块
	 * @param desciStart  功能操作描述
	 */
	public void delWriteLog(List<String> entryidList, String module, String desciStart) {
		String startTime = LogAop.getCurrentSystemTime();// 开始时间
		long startMillis = System.currentTimeMillis();// 开始毫秒数
		SecurityUser securityUser = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal());
		String ip = LogAop.getIpAddress();
		Thread thread = new Thread(() -> {
			List<Tb_log_msg> log_msgs = new ArrayList<>();
			List index=new ArrayList();//条目集合
			String[] entryidArr = new String[entryidList.size()];
			entryidList.toArray(entryidArr);
			if("简单检索".equals(module)){
				index=entryIndexRepository.findByEntryidIn(entryidArr);
			}
			String organ = organService.findFullOrgan("", securityUser.getOrganid());
			for (Object entryid : index) {
				Tb_log_msg logMsg = new Tb_log_msg();
				/*List<String> roles = roleRepository.findByuserid(securityUser.getUserid());
				if (roles.size() != 0) {
					logMsg.setRolename(StringUtils.strip(roles.toString(), "[]"));
				}*/
				logMsg.setOrgan(organ);
				logMsg.setRealname(securityUser.getRealname());
				logMsg.setOperate_user(securityUser.getLoginname());
				logMsg.setIp(ip);
				logMsg.setModule(module);
				try {
					logMsg.setDesci(desciStart + "，条目详情：##题名" + ValueUtil.getPoFieldValue("title",entryid)
							+ "##档号" +ValueUtil.getPoFieldValue("archivecode",entryid) + "，条目id为：" +
							ValueUtil.getPoFieldValue("entryid",entryid));
				}catch (Exception e){
					e.printStackTrace();
				}
				logMsg.setStartTime(startTime);
				logMsg.setEnd_time(LogAop.getCurrentSystemTime());
				logMsg.setConsume_time(System.currentTimeMillis() - startMillis + "ms");
				log_msgs.add(logMsg);
			}
			logAop.generateManualLog(log_msgs);
		});
		thread.start();
	}

	//设置排序
	@RequestMapping("/setSortSequence")
	@ResponseBody
	public ExtMsg setSortSequence(String[] sortStr,String nodeid) {
		entryIndexCaptureService.setSortSequence(sortStr,nodeid);
		return new ExtMsg(true, "", null);
	}

	//设置排序
	@RequestMapping("/getUserNodeSort")
	@ResponseBody
	public List<Tb_user_node_sort> getUserNodeSort(String nodeid) {
		return entryIndexCaptureService.getUserNodeSort(nodeid);
	}

	//手动催办
	@RequestMapping("/manualUrging")
	@ResponseBody
	public ExtMsg manualUrging(String transfercode,String sendMsg) {
		if (transfercode == null) {
			return new ExtMsg(false, "催办失败", null);
		}
		Tb_flows billApproval = taskService.manualUrging(transfercode);
		String returnStr = "";
		if (billApproval != null) {
			Tb_user spuser = userRepository.findByUserid(billApproval.getSpman());
			if (sendMsg != null && "true".equals(sendMsg) && spuser != null) {
				try {
					returnStr = smsService.SendSMS(spuser.getPhone(), "您有一条档案系统的移交审批，请登录档案系统管理平台及时处理！");
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


	//查看加入移交的条目
	@RequestMapping("/docPreviewEntry")
	@ResponseBody
	@Transactional
	public Page docPreviewEntry(int page, int limit, String condition, String operator, String content, String sort,String nodeid){
		Sort sortobj = WebSort.getSortByJson(sort);
		return entryCaptureService.findPreviewEntryindexcaptureBySearch(nodeid,condition, operator, content,page,limit,sortobj);
	}

	//删除加入移交的条目
	@RequestMapping("/deleteTransfor")
	@ResponseBody
	@Transactional
	public ExtMsg deleteTransfor(String entryids,String nodeid,Boolean isSelectAll,String condition, String operator, String content){
		List<String> allIds = new ArrayList<>();
		String[] ids;
		if(isSelectAll) {
			List<Tb_entry_index> entryIndexCaptures = entryIndexService.getDocPreviewEntry(nodeid, condition, operator, content);
			List<String> entryList = entryIndexCaptures.stream().map(c -> c.getEntryid()).collect(Collectors.toList());
			if (entryList.size() > 0) {
				if (entryids != null && entryids.length() > 1) {
					for (String string : entryList) {
						if (!entryids.contains(string.trim())) {
							allIds.add(string);
						}
					}
				} else {
					allIds.addAll(entryList);
				}
			}
			ids=new String[allIds.size()];
			allIds.toArray(ids);
		}else {
			ids=entryids.split(",");
		}
		List<String[]> subJnAry = new InformService().subArray(ids, 1000);// 处理参数超出问题
		for (String[] strings : subJnAry) {//移除加入移交卷内的条目
			transdocPreviewRepository.deleteByEntryidIn(strings);
		}
		return new ExtMsg(true, "删除成功", ids.length);
	}

		//加入移交
	@RequestMapping("/addtransfor")
	@ResponseBody
	@Transactional
	public ExtMsg addtransfor(String[] entryids,String nodeid,Boolean isSelectAll,String condition,String operator,String content) {
		Tb_transdoc_preview transdoc_preview;
		List<Tb_transdoc_preview> previewList=new ArrayList<>();
		List<String> choiceEntryIds=new ArrayList<>();
		String ids = "";
		List<String> allIds = new ArrayList<>();
		if(isSelectAll) {
			List<Tb_entry_index> entryIndexCaptures = entryIndexService.getLognIndexCapture(nodeid, condition, operator, content);
			List<String> entryList = entryIndexCaptures.stream().map(c -> c.getEntryid()).collect(Collectors.toList());
			if(entryList.size()>0) {
				if (entryids != null && entryids.length > 1) {
					ids = String.join(",", entryids);
					for (String string : entryList) {
						if (!ids.contains(string.trim())) {
							allIds.add(string);
						}
					}
				} else {
					allIds.addAll(entryList);
				}
				String[] alls = new String[allIds.size()];
				allIds.toArray(alls);
				List<String[]> subAry = new InformService().subArray(alls, 1000);// 处理参数超出问题
				for (String[] strings : subAry) {
					choiceEntryIds.addAll(transdocPreviewRepository.findByEntryid(strings));
				}
			}
		}else {
			allIds= new ArrayList<>(Arrays.asList(entryids));
			choiceEntryIds =transdocPreviewRepository.findByEntryid(entryids);
		}
		ids = String.join(",", choiceEntryIds);
		for (String id : allIds) {
			if (!ids.contains(id.trim())) {
				transdoc_preview = new Tb_transdoc_preview();
				transdoc_preview.setEntryid(id);
				transdoc_preview.setNodeid(nodeid);
				previewList.add(transdoc_preview);
			}
		}
		transdocPreviewRepository.save(previewList);
		return new ExtMsg(true, "加入移交成功", previewList.size());
	}

}