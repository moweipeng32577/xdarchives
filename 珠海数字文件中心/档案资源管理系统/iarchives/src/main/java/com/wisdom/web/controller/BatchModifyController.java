package com.wisdom.web.controller;

import com.wisdom.util.*;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import com.wisdom.web.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by RonJiang on 2018/1/22 0022.
 */

@Controller
@RequestMapping(value = "/batchModify")
public class BatchModifyController {

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	LogAop logAop;

	@Autowired
	BatchModifyService batchModifyService;

	@Autowired
	TemplateService templateService;

	@Autowired
	EntryIndexTempService entryIndexTempService;

	@Autowired
	CodesettingService codesettingService;

	@Autowired
	TemplateRepository templateRepository;

	@Autowired
	EntryIndexRepository entryIndexRepository;

	@Autowired
	EntryIndexCaptureRepository entryIndexCaptureRepository;
	
	@Autowired
	EntryDetailRepository entryDetailRepository;

	@Autowired
	EntryIndexService entryIndexService;

	@Autowired
	EntryCaptureService entryCaptureService;

	@Autowired
	EntryIndexAcceptRepository entryIndexAcceptRepository;

	@Autowired
	EntryDetailAcceptRepository entryDetailAcceptRepository;

	@Autowired
	EntryIndexManageRepository entryIndexManageRepository;

	@Autowired
	EntryDetailManageRepository entryDetailManageRepository;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String OPERATE_MODIFY = "批量修改";
	private static final String OPERATE_REPLACE = "批量替换";
	private static final String OPERATE_ADD = "批量增加";

	@RequestMapping("/main")
	public String index() {
		InetAddress ia = null;
		try {
			ia = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String mac = getMACAddress(ia);
		logger.info("o******" + mac + "&&&&&&&o");
		return "/inlet/batchModify";
	}

	/**
	 * 需修改字段预览列表数据获取
	 * 
	 * @param fieldcode
	 *            需修改字段编码
	 * @param fieldname
	 *            需修改字段名称
	 * @param fieldvalue
	 *            需修改字段值
	 * @param existedDataArr
	 *            点击“加入修改”按钮时传入
	 * @param remainDataArr
	 *            点击“删除修改”按钮时传入
	 * @return
	 */
	@RequestMapping("/getModifyFieldList")
	@ResponseBody
	public List<ModifyField> getModifyFieldList(String fieldcode, String fieldname, String fieldvalue,
												String[] existedDataArr, String[] remainDataArr) {
		List<ModifyField> list = new ArrayList<ModifyField>();
		if (remainDataArr != null) {
			if (remainDataArr.length > 0) {
				for (String remainDataStr : remainDataArr) {
					String[] fieldData = remainDataStr.split("∪");
					if (fieldData.length == 3) {
						list.add(new ModifyField(fieldData[0], fieldData[1], fieldData[2]));
					}
					if (fieldData.length == 2) {
						list.add(new ModifyField(fieldData[0], fieldData[1], ""));
					}
				}
				return list;
			}
		}
		list.add(new ModifyField(fieldcode, fieldname, fieldvalue));
		ModifyField existedModifyField = new ModifyField();
		if (existedDataArr != null) {
			for(String existedDataStr:existedDataArr){
				String[] fieldData = existedDataStr.split("∪");
				if (fieldData.length == 3) {
					existedModifyField = new ModifyField(fieldData[0], fieldData[1], fieldData[2]);
				}
				if (fieldData.length == 2) {
					existedModifyField = new ModifyField(fieldData[0], fieldData[1], "");
				}
				list.add(existedModifyField);
			}
		}
		return list;
	}

	/**
	 * 获取经过过滤后的可选择批量修改的字段集合
	 * 过滤字段包括档号、统计项字段、已加入至预览列表及fieldtable不为tb_entry_index的模板字段
	 * 
	 * @param datanodeidAndFieldcodes
	 *            批量替换或批量增加为节点id，批量修改界面可能为节点id与已加入修改字段编码拼接而成的字符串（初次加载时均仅有节点id）
	 * @return
	 */
	@RequestMapping("/getFilteredTemplateField")
	@ResponseBody
	public List<Tb_data_template> getTemplateField(String datanodeidAndFieldcodes) {
		String[] datanodeidAndFieldcodesArr = datanodeidAndFieldcodes.split("∪");// 节点ID与字段编码参数之间以“∪”分隔
		String datanodeid = datanodeidAndFieldcodesArr[0];
		String fieldcodes[] = null;
		if (datanodeidAndFieldcodesArr.length > 1) {
			fieldcodes = datanodeidAndFieldcodesArr[1].split(",");// 字段编码字符串中各编码以“,”分隔
		}
		List<String> excludesList = new ArrayList<String>();
		excludesList.add("archivecode");// 档号，必排除字段
		for (int j = 0; j < datanodeidAndFieldcodesArr.length; j++) {
			String nodeid = datanodeidAndFieldcodesArr[j];
//			List<String> fields = templateRepository.findFieldcodeByNodeid(nodeid);
//			for (int i = 0; i < fields.size(); i++) {
//				excludesList.add(fields.get(i));// 去除枚举字段
//			}
			List<String> fieldsOnly = templateRepository.findFieldonly(nodeid);
			for (int i = 0; i < fieldsOnly.size(); i++) {
				excludesList.add(fieldsOnly.get(i));// 去除只读字段
			}
		}
		List<String> codesettingFields = codesettingService.getCodeSettingFields(datanodeid);
		String calFiledcode = "";
		if (codesettingFields.size() > 0) {
			calFiledcode = codesettingFields.get(codesettingFields.size() - 1);
		}
		if (calFiledcode != null && !"".equals(calFiledcode)) {
			excludesList.add(calFiledcode);// 档号设置中的计算项字段，排除
		}
		if (fieldcodes != null) {
			if (fieldcodes.length > 0) {
				for (String fieldcode : fieldcodes) {
					excludesList.add(fieldcode);
				}
			}
		}
		//String[] excludes = excludesList.toArray(new String[0]);
		//List<Tb_data_template> data_templatelists = templateService.findPartialTemplateByNodeidAndExclude(datanodeid, excludes);
		List<Tb_data_template> data_templatelistAll = templateRepository.findByNodeid(datanodeid);//所有模板字段
		List<Tb_data_template> data_templatelists =new ArrayList<>();
		for (int i = 0; i < data_templatelistAll.size(); i++) {
			Tb_data_template data_template = data_templatelistAll.get(i);
			String oldFieldcode = data_template.getFieldcode();
			if(!excludesList.contains(oldFieldcode)){//排除字段集合里边没有的就显示
				String oldFieldname = data_template.getFieldname();
				data_template.setFieldname(oldFieldcode + "_" + oldFieldname);
				data_templatelists.add(data_template);
			}
		}
		return data_templatelists;
	}

	/**
	 * 获取批量更新结果预览
	 * 
	 * @param entryidArr
	 *            高级检索结果预览列表中所选择的条目entryid数组
	 * @param nodeid
	 *            批量操作模块进入时最初选择的数据节点的节点id
	 * @param fieldModifyData
	 *            批量更新字段详情（包括需更新的值）
	 * @param ifContainSpace
	 *            批量替换模块中：是否包含前后空格
	 * @param condition
	 * @param operator
	 * @param content
	 * @param flag
	 *            标记模块为：批量修改、批量替换、批量增加
	 * @param page
	 * @param start
	 * @param limit
	 * @param isSelectAll
	 * @param formConditions
	 *            高级检索表单检索条件
	 * @param formOperators
	 *            高级检索表单检索操作符
	 * @param daterangedata
	 *            高级检索表单中日期范围检索数据
	 * @param logic
	 *            高级检索表单中逻辑关系符（and或or）
	 * @param ifSearchLeafNode
	 *            是否包含叶子节点
	 * @param ifContainSelfNode
	 *            是否包含本身非叶子节点
	 * @param basicCondition
	 * @param basicOperator
	 * @param basicContent
	 * @return
	 */
	@RequestMapping(value="/getResultPreview",method = RequestMethod.POST)
	@ResponseBody
	public Page<Tb_entry_index_temp> getResultPreview(String docid, String entryidArr, String nodeid,
			String fieldModifyData, String fieldReplaceData, boolean ifContainSpace, String condition, String operator,
			String content, String flag, int page, int start, int limit, String isSelectAll, Object formConditions,
			ExtOperators formOperators, ExtDateRangeData daterangedata, String logic, boolean ifSearchLeafNode,
			boolean ifContainSelfNode, String basicCondition, String basicOperator, String basicContent, String sort,
			String type,String pageState) {
		logger.info("page:" + page + ";start:" + start + ";limt:" + limit + ";entryidArr:" + entryidArr + ";nodeid:"
				+ nodeid);
		// 数据采集、数据审核、数据管理中的批量操作都是在这个方法获取数据，数据审核模块会传多一个审核单据ID，用于获取该单据下的条目
		String[] entryidInfo = entryidArr.split(",");
		if (docid != null && "true".equals(isSelectAll)) {
			String entryids = "";
			List<Tb_entry_index_capture> list = entryCaptureService.getEntries("AuditController", nodeid,
					Tb_transdoc_entry.STATUS_AUDIT, docid, basicCondition, basicOperator, basicContent);
			for (Tb_entry_index_capture entry_index : list) {
				boolean found = false;
				for (String entryid : entryidInfo) {
					if (entryid.equals(entry_index.getEntryid())) {
						found = true;
						break;
					}
				}
				if (!found) {
					entryids += entry_index.getEntryid() + ",";
				}
			}
			entryidInfo = entryids.split(",");
			isSelectAll = "false";
		}
		Sort sortobj = WebSort.getSortByJson(sort);
		Page<Tb_entry_index_temp> indextemp = null;
		if (OPERATE_MODIFY.equals(flag)) {
			indextemp = batchModifyService.getModifyResultPreview(entryidInfo, condition, operator, content, nodeid,
					fieldModifyData, page, limit, isSelectAll, formConditions, formOperators, daterangedata, logic,
					ifSearchLeafNode, ifContainSelfNode, basicCondition, basicOperator, basicContent, sortobj, type,pageState);
		}
		if (OPERATE_REPLACE.equals(flag)) {
			indextemp = batchModifyService.getReplaceResultPreview(entryidInfo, condition, operator, content, nodeid,
					fieldReplaceData, ifContainSpace, page, limit, isSelectAll, formConditions, formOperators,
					daterangedata, logic, ifSearchLeafNode, ifContainSelfNode, basicCondition, basicOperator,
					basicContent, sortobj, type,pageState);
		}
		if (OPERATE_ADD.equals(flag)) {
			indextemp = batchModifyService.getAddResultPreview(entryidInfo, condition, operator, content, nodeid,
					fieldModifyData, page, limit, isSelectAll, formConditions, formOperators, daterangedata, logic,
					ifSearchLeafNode, ifContainSelfNode, basicCondition, basicOperator, basicContent, sortobj, type,pageState);
		}
		if (indextemp != null) {
			return indextemp;
		}
		return null;
	}

	/**
	 * 库房导入数据预览
	 * @return
	 */
	@RequestMapping("/getKfResultPreview")
	@ResponseBody
	public Page<Tb_entry_index_temp> getKfResultPreview(String condition, String operator,String content, String flag, int page, int start, int limit, String isSelectAll, String sort,String resultType) {

		Sort sortobj = WebSort.getSortByJson(sort);
		Page<Tb_entry_index_temp> indextemp = null;
		indextemp = batchModifyService.getKfResultPreview(condition, operator, content, page, limit, isSelectAll, sortobj,resultType);
		if (indextemp != null) {
			return indextemp;
		}
		return null;
	}

	/**
	 * 删除临时表中所有数据
	 */
	@RequestMapping(value = "/tempEntryindex", method = RequestMethod.DELETE)
	@ResponseBody
	public void delTempEntryindex() {
		entryIndexTempService.deleteAllEntryindex();
	}

	/**
	 * 根据类型删除临时表中指定数据
	 */
	@RequestMapping(value = "/delTempByUniqueType")
	@ResponseBody
	public void deleteEntryIndexTempByUniquetag(String batchType) {
		batchModifyService.deleteEntryIndexTempByUniquetagByType(batchType);
	}

	/**
	 * 删除临时表中指定数据
	 */
	@RequestMapping(value = "/delTempByUniquetag", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteEntryIndexTempByUniquetag() {
		batchModifyService.deleteEntryIndexTempByUniquetag();
	}

	@RequestMapping(value = "/delSqTempByUniquetag", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteEntryIndexSqTempByUniquetag() {
		batchModifyService.deleteSqEntryIndexTempByUniquetag();
	}

	// @LogAnnotation(module = "批量修改",startDesc = "批量更新操作，操作类型为：",sites =
	// "1,2",connect = "；条目id为：,。")
	@RequestMapping("/updateEntryindex")
	@ResponseBody
	public ExtMsg updateEntryindex(String flag, String[] entryidArr, String nodeid, String fieldModifyData,
			String fieldReplaceData, String type,String batchtype) {
		String startTime = LogAop.getCurrentSystemTime();// 开始时间
		long startMillis = System.currentTimeMillis();// 开始毫秒数
		boolean result = false;
		if (OPERATE_MODIFY.equals(flag)) {
			result = batchModifyService.updateReplaceEntryindex(entryidArr, nodeid, fieldModifyData, type,batchtype);
		} else if (OPERATE_REPLACE.equals(flag)) {
			result = batchModifyService.updateReplaceEntryindex(entryidArr, nodeid, fieldReplaceData, type,batchtype);
		} else if (OPERATE_ADD.equals(flag)) {
			result = batchModifyService.updateReplaceEntryindex(entryidArr, nodeid, fieldModifyData, type,batchtype);
		} else {
			return new ExtMsg(false, "未传入正确的flag参数", null);
		}
		if (result) {
			String currentOperateuser = logAop.getCurrentOperateuser();
			String currentOperateuserRealname = logAop.getCurrentOperateuserRealname();
			String ipAddress = logAop.getIpAddress();
			new Thread() {
				@Override
				public void run() {
					for (String entryid : entryidArr) {
						logAop.generateManualLog2(startTime, LogAop.getCurrentSystemTime(),
								System.currentTimeMillis() - startMillis, "批量修改",
								flag + "操作，条目id为：" + entryid + ",节点id为：" + nodeid,currentOperateuser,
								currentOperateuserRealname,ipAddress);
					}
				}
			}.start();
			return new ExtMsg(true, flag + "操作成功", null);
		}
		return new ExtMsg(false, "请检查档号构成字段是否为空", null);
	}

	private static String getMACAddress(InetAddress ia) {
		// 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
		byte[] mac = new byte[0];
		try {
			mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
		} catch (SocketException e) {
			e.printStackTrace();
		}

		// 下面代码是把mac地址拼装成String
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < mac.length; i++) {
			if (i != 0) {
				sb.append("-");
			}
			// mac[i] & 0xFF 是为了把byte转化为正整数
			String s = Integer.toHexString(mac[i] & 0xFF);
			sb.append(s.length() == 1 ? 0 + s : s);
		}

		// 把字符串所有小写字母改为大写成为正规的mac地址并返回
		return sb.toString().toUpperCase();
	}

	@RequestMapping("/export")
	@ResponseBody
	public void export(HttpServletResponse response,String docid, String flag, String[] entryidArr, String[]
			columnArray,String fieldReplaceData, String nodeid, String isSelectAll, Tb_entry_index formConditions,
			ExtOperators formOperators, ExtDateRangeData daterangedata, String logic, boolean ifSearchLeafNode,
			boolean ifContainSelfNode, String basicCondition, String basicOperator, String basicContent, String type) {
		//数据采集、数据审核、数据管理中的批量操作都是在这个方法获取数据，数据审核模块会传多一个审核单据ID，用于获取该单据下的条目
		if(docid!=null){
			String entryids="";
			List<Tb_entry_index_capture> list= entryCaptureService.getEntries("AuditController",nodeid,
					Tb_transdoc_entry.STATUS_AUDIT,docid,basicCondition,basicOperator,basicContent);
			for (Tb_entry_index_capture entry_index : list) {
				boolean found = false;
				for (String entryid : entryidArr) {
					if (entryid.equals(entry_index.getEntryid())) {
						found = true;
						break;
					}
				}
				if (!found) {
					entryids += entry_index.getEntryid() + ",";
					entryidArr =  entryids.split(",");
				}
			}
			//当found为false时，entryidArr再重新附值，否则entryids为空，entryidArr会被替换为空
//			entryidArr =  entryids.split(",");
			isSelectAll = "false";
		}
		if ("true".equals(isSelectAll)) {
			String entryids = batchModifyService.getEntryids(nodeid, entryidArr, formConditions, basicCondition,
					basicOperator, basicContent, formOperators, daterangedata, logic, ifSearchLeafNode,
					ifContainSelfNode, type);
			if (!"".equals(entryids)) {
				entryidArr = entryids.substring(0, entryids.length() - 1).split(",");
			}
		}
		List<Tb_entry_index> entry_indexList = new ArrayList<>();
		List<Tb_entry_index_capture> entry_indexList_capture = new ArrayList<>();
		List<Tb_entry_index_accept> index_accepts = new ArrayList<>();
		List<Tb_entry_index_manage> index_manages = new ArrayList<>();
        List<String[]> subAry = new InformService().subArray(entryidArr, 1000);//处理ORACLE1000参数问题
        if ("数据管理".equals(type)) {
            for (String[] ary : subAry) {
                entry_indexList.addAll(entryIndexRepository.findByEntryidIn(ary));
            }
        } else if("目录接收".equals(type)){
            for (String[] ary : subAry) {
                index_accepts.addAll(entryIndexAcceptRepository.findByEntryidIn(ary));
            }
        } else  if("目录管理".equals(type)){
            for (String[] ary : subAry) {
                index_manages.addAll(entryIndexManageRepository.findByEntryidIn(ary));
            }
        } else {
            for (String[] ary : subAry) {
                entry_indexList_capture.addAll(entryIndexCaptureRepository.findByEntryidIn(ary));
            }
        }

		String[] keys = new String[columnArray.length];
		String[] names = new String[columnArray.length];
		for (int i = 0; i < columnArray.length; i++) {
			String[] split = columnArray[i].split("-");
			keys[i] = split[0];
			names[i] = split[1];
		}
		List<Map<String, Object>> list;
		if (type.equals("数据管理")) {
			list = createExcelRecord(entry_indexList, keys);
		} else if("目录接收".equals(type)){
			list = createExcelAccpetRecord(index_accepts,keys);
		} else if("目录管理".equals(type)){
			list = createExcelManagementRecord(index_manages,keys);
		} else {
			list = createCaptureExcelRecord(entry_indexList_capture, keys);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
		String fileName = flag + "前备份" + sdf.format(new Date());
		ExportUtil exportUtil = new ExportUtil(fileName, response, list, keys, names);
		exportUtil.exportExcel();
	}

	//目录接收
	private List<Map<String, Object>> createExcelAccpetRecord(List<Tb_entry_index_accept> entry_indexList, String[] keys) {
		List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
		Tb_entry_index_accept entry_index;
		for (int j = 0; j < entry_indexList.size(); j++) {
			entry_index = entry_indexList.get(j);
			Tb_entry_detail_accept details = entryDetailAcceptRepository.findByEntryid(entry_indexList.get(j).getEntryid());
			Map<String, Object> mapValue = new HashMap<>();
			for (String key : keys) {
				if (details != null && details.toString().contains(key)) {
					mapValue.put(key, GainField.getFieldValueByName(key, details));
				} else {
					mapValue.put(key, GainField.getFieldValueByName(key, entry_index));
				}
			}
			listmap.add(mapValue);
		}
		return listmap;
	}

	//目录管理
	private List<Map<String, Object>> createExcelManagementRecord(List<Tb_entry_index_manage> entry_indexList, String[] keys) {
		List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
		Tb_entry_index_manage entry_index;
		for (int j = 0; j < entry_indexList.size(); j++) {
			entry_index = entry_indexList.get(j);
			Tb_entry_detail_manage details = entryDetailManageRepository.findByEntryid(entry_indexList.get(j).getEntryid());
			Map<String, Object> mapValue = new HashMap<>();
			for (String key : keys) {
				if (details != null && details.toString().contains(key)) {
					mapValue.put(key, GainField.getFieldValueByName(key, details));
				} else {
					mapValue.put(key, GainField.getFieldValueByName(key, entry_index));
				}
			}
			listmap.add(mapValue);
		}
		return listmap;
	}

	private List<Map<String, Object>> createExcelRecord(List<Tb_entry_index> entry_indexList, String[] keys) {
		List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
		Tb_entry_index entry_index;
		for (int j = 0; j < entry_indexList.size(); j++) {
			entry_index = entry_indexList.get(j);
			Tb_entry_detail details = entryDetailRepository.findByEntryid(entry_indexList.get(j).getEntryid());
			Map<String, Object> mapValue = new HashMap<>();
			for (String key : keys) {
				if (details != null && details.toString().contains(key)) {
					mapValue.put(key, GainField.getFieldValueByName(key, details));
				} else {
					mapValue.put(key, GainField.getFieldValueByName(key, entry_index));
				}
			}
			listmap.add(mapValue);
		}
		return listmap;
	}

	private List<Map<String, Object>> createCaptureExcelRecord(List<Tb_entry_index_capture> entry_indexList,
			String[] keys) {
		List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
		Tb_entry_index_capture entry_index;
		for (int j = 0; j < entry_indexList.size(); j++) {
			entry_index = entry_indexList.get(j);
			Tb_entry_detail details = entryDetailRepository.findByEntryid(entry_indexList.get(j).getEntryid());
			Map<String, Object> mapValue = new HashMap<>();
			for (String key : keys) {
				if (details != null && details.toString().contains(key)) {
					mapValue.put(key, GainField.getFieldValueByName(key, details));
				} else {
					mapValue.put(key, GainField.getFieldValueByName(key, entry_index));
				}
			}
			listmap.add(mapValue);
		}
		return listmap;
	}

	/**
	 * 归档时批量操作
	 *
	 * @param entryidArr
	 *            所选择的条目entryid数组
	 * @param fieldModifyData
	 *            批量更新字段详情（包括需更新的值）
	 * @param ifContainSpace
	 *            批量替换模块中：是否包含前后空格
	 * @return
	 */
	@RequestMapping(value="/updateFileModify",method = RequestMethod.POST)
	@ResponseBody
	public ExtMsg updateFileModify(String entryidArr, String nodeid,
								   String fieldModifyData, String fieldReplaceData, boolean ifContainSpace,
								   String flag, String type) {
		String[] entryidInfo = entryidArr.split(",");
		if (OPERATE_MODIFY.equals(flag)) {
			batchModifyService.updateFileModify(entryidInfo, nodeid,fieldModifyData, type);
		}
		if (OPERATE_REPLACE.equals(flag)) {
			batchModifyService.updateFileReplace(entryidInfo, nodeid, fieldReplaceData, ifContainSpace, type);
		}
		if (OPERATE_ADD.equals(flag)) {
			batchModifyService.updateFileAdd(entryidInfo,nodeid, fieldModifyData, type);
		}
		return new ExtMsg(true,"",null);
	}
}