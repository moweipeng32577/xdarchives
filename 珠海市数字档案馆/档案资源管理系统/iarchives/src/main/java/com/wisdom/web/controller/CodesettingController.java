package com.wisdom.web.controller;

import com.wisdom.secondaryDataSource.entity.Tb_codeset_sx;
import com.wisdom.secondaryDataSource.entity.Tb_data_node_sx;
import com.wisdom.secondaryDataSource.entity.Tb_data_template_sx;
import com.wisdom.secondaryDataSource.repository.SecondaryDataNodeRepository;
import com.wisdom.secondaryDataSource.repository.SxCodesetRepository;
import com.wisdom.secondaryDataSource.repository.SxDataNodeRepository;
import com.wisdom.secondaryDataSource.repository.SxTemplateRepository;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_codeset;
import com.wisdom.web.entity.Tb_data_node;
import com.wisdom.web.entity.Tb_data_template;
import com.wisdom.web.entity.Tb_entry_index;
import com.wisdom.web.repository.DataNodeRepository;
import com.wisdom.web.service.CodesettingService;
import com.wisdom.web.service.EntryIndexService;
import com.wisdom.web.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * 档号设置控制器 Created by tanly on 2017/10/24 0024.
 */
@Controller
@RequestMapping(value = "/codesetting")
public class CodesettingController {

	@Value("${find.sx.data}")
	private Boolean openSxData;//是否可检索声像系统的声像数据

	@Autowired
	CodesettingService codesettingService;

	@Autowired
	TemplateService templateService;

	@Autowired
	EntryIndexService entryIndexService;

	@Autowired
	DataNodeRepository dataNodeRepository;
	@Autowired
	SecondaryDataNodeRepository secondaryDataNodeRepository;
	@Autowired
	SxTemplateRepository sxTemplateRepository;

	@Autowired
	SxDataNodeRepository sxDataNodeRepository;

	@Autowired
	SxCodesetRepository sxCodesetRepository;

	@RequestMapping("/main")
	public String userGroup(Model model) {
		model.addAttribute("openSxData",openSxData);
		return "/inlet/codesetting";
	}

	@RequestMapping("/getTemplateField")
	@ResponseBody
	public List getTemplateField(String datanodeid,String nodeType, String xtType) {
		if("声像系统".equals(xtType)){
			return getSxTemplateField(datanodeid, nodeType);
		}else{
			List<Tb_data_template> data_templatelists = templateService.findPartialTemplateByNodeidAndExclude(datanodeid,
					new String[] { "archivecode", "fscount", "kccount" });
			List<Tb_codeset> codesetlists = codesettingService.findCodesetByDatanodeid(datanodeid);

			Map<Long, Tb_data_template> map = new TreeMap<>();
			for (int i = 0; i < data_templatelists.size(); i++) {
				Tb_data_template data_template = data_templatelists.get(i);
				String oldFieldcode = data_template.getFieldcode();
				String oldFieldname = data_template.getFieldname();
				boolean flag = false;
				for (Tb_codeset codeset : codesetlists) {
					if (data_template.getFieldcode().equals(codeset.getFieldcode())) {
						data_template.setFieldcode(codeset.getCodeid() + "∪" + oldFieldname + "∪" + codeset.getSplitcode()
								+ "∪" + codeset.getFieldlength() + "∪" + oldFieldcode);
						data_template.setFieldname(oldFieldcode + "_" + oldFieldname);
						map.put(codeset.getOrdernum(), data_template);
						data_templatelists.remove(data_template);// 删除codeset对应字段，利用treemap排序
						i--;// 删除对象，索引前移
						flag = true;// 找到
						break;
					}
				}
				if (!flag) {
					data_template.setFieldcode("∪" + oldFieldcode + "∪" + oldFieldname + "∪-∪4");// 默认长度为4
					data_template.setFieldname(oldFieldcode + "_" + oldFieldname);
				}
			}
			Iterator<Long> iter = map.keySet().iterator();
			while (iter.hasNext()) {
				data_templatelists.add(map.get(iter.next()));// 接到data_templatelists后面
			}
			return data_templatelists;
		}
	}

	public List<Tb_data_template_sx> getSxTemplateField(String datanodeid, String nodeType) {
		Tb_data_node_sx node = sxDataNodeRepository.findByNodeid(datanodeid);
		List<Tb_data_template_sx> data_templatelists = new ArrayList<>();
		//判断节点是 件（8,9,10） 还是 组 （5,6,7）
		if(null==node){
			return new ArrayList<>();
		}
		String[] fileTable=templateService.getTableNameByTableType(nodeType);
		if(node.getClasslevel()<=7){
			data_templatelists = sxTemplateRepository.findByNodeidAndFieldtableInAndFieldcodeNotIn(datanodeid,fileTable,
					new String[] { "archivecode", "fscount", "kccount" });
		}else {
			data_templatelists = sxTemplateRepository.findByNodeidAndFieldtableInAndFieldcodeNotIn(datanodeid,fileTable,
					new String[] { "archivecode", "fscount", "kccount" });
		}
		List<Tb_codeset_sx> codesetlists = sxCodesetRepository.findByDatanodeidAndFiledtableInOrderByOrdernum(datanodeid,fileTable);

		Map<Long, Tb_data_template_sx> map = new TreeMap<>();
		for (int i = 0; i < data_templatelists.size(); i++) {
			Tb_data_template_sx data_template = data_templatelists.get(i);
			String oldFieldcode = data_template.getFieldcode();
			String oldFieldname = data_template.getFieldname();
			boolean flag = false;
			for (Tb_codeset_sx codeset : codesetlists) {
				if (data_template.getFieldcode().equals(codeset.getFieldcode())) {
					data_template.setFieldcode(codeset.getCodeid() + "∪" + oldFieldname + "∪" + codeset.getSplitcode()
							+ "∪" + codeset.getFieldlength() + "∪" + oldFieldcode);
					data_template.setFieldname(oldFieldcode + "_" + oldFieldname);
					map.put(codeset.getOrdernum(), data_template);
					data_templatelists.remove(data_template);// 删除codeset对应字段，利用treemap排序
					i--;// 删除对象，索引前移
					flag = true;// 找到
					break;
				}
			}
			if (!flag) {
				data_template.setFieldcode("∪" + oldFieldcode + "∪" + oldFieldname + "∪-∪4");// 默认长度为4
				data_template.setFieldname(oldFieldcode + "_" + oldFieldname);
			}
		}
		Iterator<Long> iter = map.keySet().iterator();
		while (iter.hasNext()) {
			data_templatelists.add(map.get(iter.next()));// 接到data_templatelists后面
		}
		return data_templatelists;
	}

	@RequestMapping("/setCode")
	@ResponseBody
	public ExtMsg setCode(String datanodeid, String[] fieldcodelist, String xtType,String filedtable) {
		if("声像系统".equals(xtType)){
			ExtMsg result = codesettingService.setSxCode(datanodeid, fieldcodelist,templateService.getTabTypeByTableName(filedtable));
			if (result != null) {
				if (!result.isSuccess()) {
					return result;
				}
			}
//			Tb_data_node_sx node =secondaryDataNodeRepository.findByNodeid(datanodeid);
//			if (node.getLuckstate() != null && node.getLuckstate().equals("1")) {
//				templateService.synctemplate(datanodeid, "allChild", "true", xtType,filedtable);
//			}
		}else{
			ExtMsg result = codesettingService.setCode(datanodeid, fieldcodelist);
			if (result != null) {
				if (!result.isSuccess()) {
					return result;
				}
			}
//			Tb_data_node node = dataNodeRepository.findByNodeid(datanodeid);
//			if (node.getLuckstate() != null && node.getLuckstate().equals("1")) {
//				templateService.synctemplate(datanodeid, "allChild", "true", xtType,"");
//			}
		}
		return new ExtMsg(true, "保存成功", null);
	}

	@RequestMapping("/getCodeSettingFields")
	@ResponseBody
	public ExtMsg getCodeSettingFields(String nodeid) {
		List<String> codeSettingFields = codesettingService.getCodeSettingFields(nodeid);
		if (codeSettingFields.size() > 0) {
			return new ExtMsg(true, "获取档号设置字段成功", codeSettingFields);
		}
		return new ExtMsg(false, "获取档号设置字段失败", null);
	}

	@RequestMapping("/getCodeSettingSplitCodes")
	@ResponseBody
	public ExtMsg getCodeSettingSplitCodes(String nodeid) {
		List<String> codeSettingSplitCodes = codesettingService.getCodeSettingSplitCodes(nodeid);
		if (codeSettingSplitCodes.size() > 0) {
			return new ExtMsg(true, "获取档号分割符号成功", codeSettingSplitCodes);
		}
		return new ExtMsg(false, "获取档号分割符号失败", null);
	}

	@RequestMapping("/getCalFieldLength")
	@ResponseBody
	public ExtMsg getCalFieldLength(String nodeid) {
		Integer number = codesettingService.getCalFieldLength(nodeid);
		if (number != null) {
			return new ExtMsg(true, "获取计算项字段长度成功", number);
		}
		return new ExtMsg(false, "获取计算项字段长度失败", null);
	}

	@RequestMapping("/getFundsFieldLength")
	@ResponseBody
	public ExtMsg getFundsFieldLength(String nodeid) {
		Integer number = codesettingService.getFundsFieldLength(nodeid);
		if (number != null) {
			return new ExtMsg(true, "获取全宗号字段长度成功", number);
		}
		return new ExtMsg(false, "获取全宗号字段长度失败", null);
	}

	@RequestMapping("/getFileFieldLength")
	@ResponseBody
	public ExtMsg getFileFieldLength(String nodeid) {
		Integer number = codesettingService.getFileFieldLength(nodeid);
		if (number != null) {
			return new ExtMsg(true, "获取案卷号字段长度成功", number);
		}
		return new ExtMsg(false, "获取案卷号字段长度失败", null);
	}

	@RequestMapping("/getArchivecodeValue")
	@ResponseBody
	public ExtMsg getArchivecodeValue(Tb_entry_index entryIndex, String dataNodeid) {
		List<String> codeSettingFieldList = codesettingService.getCodeSettingFields(dataNodeid);// 获取档号设置字段集合
		if (codeSettingFieldList.size() == 0) {// 档号字段未设置
			return new ExtMsg(false, "请检查档号设置信息是否正确", null);
		}
		String archivecode = entryIndexService.getArchivecodeValue(entryIndex, dataNodeid, codeSettingFieldList);
		if (archivecode != null && !("".equals(archivecode))) {
			return new ExtMsg(true, "获取档号成功", archivecode);
		}
		String value = templateService.getFieldName(dataNodeid);
		return new ExtMsg(false, "获取档号失败，请检查档号构成字段（" + value + "）输入值是否为空。", null);
	}

	@RequestMapping("/deleteCodesetByNodeid")
	@ResponseBody
	public ExtMsg deleteCodesetByNodeid(String nodeid, String xtType,String tableType) {
		if("声像系统".equals(xtType)){
			codesettingService.deleteSxCodesettingByNodeid(nodeid,templateService.getTableNameByTableType(tableType));
		}else{
			codesettingService.deleteCodesetByNodeid(nodeid);
		}
		return new ExtMsg(true, "档号设置删除成功", null);
	}

	@RequestMapping(value = "/comparecodeset/{ajNodeid}/{jnNodeid}", method = RequestMethod.GET)
	@ResponseBody
	public ExtMsg compareCodeset(@PathVariable String ajNodeid, @PathVariable String jnNodeid) {
		return codesettingService.compareCodeset(ajNodeid, jnNodeid);
	}

	//获取模拟档号，用于档号对齐的提示
	@RequestMapping("/getSimulationArchivecode")
	@ResponseBody
	public ExtMsg getSimulationArchivecode(String nodeid) {
		String simulationArchivecode = codesettingService.getSimulationArchivecode(nodeid);
		return new ExtMsg(true, "获取模拟档号成功", simulationArchivecode);
	}
}
