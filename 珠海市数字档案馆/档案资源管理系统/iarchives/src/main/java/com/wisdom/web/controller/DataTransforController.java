package com.wisdom.web.controller;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wisdom.util.GainField;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_data_node;
import com.wisdom.web.entity.Tb_entry_detail;
import com.wisdom.web.entity.Tb_entry_detail_capture;
import com.wisdom.web.entity.Tb_entry_index;
import com.wisdom.web.entity.Tb_entry_index_capture;
import com.wisdom.web.repository.DataNodeRepository;
import com.wisdom.web.repository.ElectronicCaptureRepository;
import com.wisdom.web.repository.ElectronicRepository;
import com.wisdom.web.repository.EntryDetailCaptureRepository;
import com.wisdom.web.repository.EntryDetailRepository;
import com.wisdom.web.repository.EntryIndexCaptureRepository;
import com.wisdom.web.repository.EntryIndexRepository;
import com.wisdom.web.repository.TemplateRepository;
import com.wisdom.web.service.EntryCaptureService;
import com.wisdom.web.service.EntryIndexService;

/**
 * 数据转移控制器
 * 
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = "/transfor")
public class DataTransforController {

	@Autowired
	EntryIndexService entryIndexService;

	@Autowired
	EntryCaptureService entryCaptureService;

	@Autowired
	EntryIndexRepository entryIndexRepository;

	@Autowired
	ElectronicRepository electronicRepository;

	@Autowired
	ElectronicCaptureRepository electronicCaptureRepository;

	@Autowired
	EntryIndexCaptureRepository entryIndexCaptureRepository;

	@Autowired
	EntryDetailRepository entryDetailRepository;

	@Autowired
	EntryDetailCaptureRepository entryDetailCaptureRepository;

	@Autowired
	TemplateRepository templateRepository;

	@Autowired
	DataNodeRepository dataNodeRepository;

	@RequestMapping("/main")
	public String transfor() {
		return "/inlet/dataTransfor";
	}

	@RequestMapping(value = "/getNodeidInfo")
	@ResponseBody
	public ExtMsg getNodeidInfo(String nodeid) {
		Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
		if (node.getNodetype() == 2) {
			return new ExtMsg(false, "当前节点不能进行数据转移操作！", null);
		}
		return new ExtMsg(true, "成功获取节点信息！", null);
	}

	/**
	 * 数据管理 - 数据转移
	 * 
	 * @param nodeid
	 * @param targetNodeid
	 * @param fieldCodes
	 * @param targetFieldCodes
	 * @param entryids
	 * @param isSelectAll
	 * @return
	 */
	@RequestMapping(value = "/entriesTransfer")
	@ResponseBody
	public ExtMsg entriesTransfer(String nodeid, String targetNodeid, String fieldCodes, String targetFieldCodes,
			String entryids, String condition, String operator, String content, boolean isSelectAll) {
		String[] fieldInfo = fieldCodes.split(",");
		String[] targetFieldInfo = targetFieldCodes.split(",");

		List<Tb_entry_index> indexs = entryIndexService.getEntryIndexList(nodeid, condition, operator, content);
		if (isSelectAll) {
			if (entryids != null && !"".equals(entryids)) {
				String[] entryList = entryids.split(",");
				List<Tb_entry_index> removeEntry = entryIndexRepository.findByEntryidIn(entryList);
				indexs.removeAll(removeEntry);
			}
		} else {
			if (entryids != null && !"".equals(entryids)) {
				String[] entryList = entryids.split(",");
				indexs = entryIndexRepository.findByEntryidIn(entryList);
			}
		}

		for (int i = 0; i < indexs.size(); i++) {
			Tb_entry_index index = indexs.get(i);// 找到源模板的条目信息
			Tb_entry_index tempIndex = new Tb_entry_index();
			BeanUtils.copyProperties(index, tempIndex);
			Tb_entry_detail detail = entryDetailRepository.findByEntryid(index.getEntryid());// 找到源模板的副表信息
			// 将原节点的节点id改成目的节点id
			GainField.setFieldValueByName("nodeid", index, targetNodeid);
			//对数组的长度进行判断，以防出现ArrayIndexOutOfBoundsException
            int arrayLength = targetFieldInfo.length > fieldInfo.length ? fieldInfo.length : targetFieldInfo.length;
			for (int a = 0; a < arrayLength; a++) {
				// 如果进行了调整目的字段操作，那么就将原模板字段值更新到指定目的字段
				if (fieldInfo.length > 0 && !"".equals(fieldInfo[a]) && targetFieldInfo.length > 0 && !"".equals(targetFieldInfo[a]) && !fieldInfo[a].equals(targetFieldInfo[a])) {
					// 区分是主表字段还是副表字段
					String value = index.toFieldnameString().contains(fieldInfo[a])
							? (String) GainField.getFieldValueByName(fieldInfo[a], tempIndex)
							: (String) GainField.getFieldValueByName(fieldInfo[a], detail);
					if (index.toFieldnameString().contains(targetFieldInfo[a])) {
						GainField.setFieldValueByName(targetFieldInfo[a], index, value);
					} else {
						if (detail != null && value != null  && !value.equals("")) {
							GainField.setFieldValueByName(targetFieldInfo[a], detail, value);
						}
					}
				}
			}
			entryIndexRepository.save(index);
			if (detail != null) {
				entryDetailRepository.save(detail);
			}
		}
		return new ExtMsg(true, "数据移交成功！", null);
	}

	@RequestMapping(value = "/captureEntriesTransfer")
	@ResponseBody
	public ExtMsg captureEntriesTransfer(String nodeid, String targetNodeid, String fieldCodes, String targetFieldCodes,
			String entryids, String condition, String operator, String content, boolean isSelectAll) {
		String[] fieldInfo = fieldCodes.split(",");
		String[] targetFieldInfo = targetFieldCodes.split(",");

		List<Tb_entry_index_capture> indexs = entryCaptureService.getEntryCaptureList(nodeid, condition, operator, content);
		if (isSelectAll) {
			if (entryids != null && !entryids.equals("")) {
				String[] entryList = entryids.split(",");
				List<Tb_entry_index> removeEntry = entryIndexRepository.findByEntryidIn(entryList);
				indexs.removeAll(removeEntry);
			}
		} else {
			if (entryids != null && !entryids.equals("")) {
				String[] entryList = entryids.split(",");
				indexs = entryIndexCaptureRepository.findByEntryidIn(entryList);
			}
		}

		for (int i = 0; i < indexs.size(); i++) {
			Tb_entry_index_capture index = indexs.get(i);// 找到源模板的条目信息
			Tb_entry_index_capture tempIndex = new Tb_entry_index_capture();
			BeanUtils.copyProperties(index, tempIndex);
			Tb_entry_detail_capture detail = entryDetailCaptureRepository.findByEntryid(index.getEntryid());// 找到源模板的副表信息
			// 将原节点的节点id改成目的节点id
			GainField.setFieldValueByName("nodeid", index, targetNodeid);
			for (int a = 0; a < targetFieldInfo.length; a++) {
				// 如果进行了调整目的字段操作，那么就将原模板字段值更新到指定目的字段
				if (fieldInfo.length > 0 && !fieldInfo[a].equals("") && targetFieldInfo.length > 0
						&& !targetFieldInfo[a].equals("") && !fieldInfo[a].equals(targetFieldInfo[a])) {
					// 区分是主表字段还是副表字段
					String value = index.toFieldnameString().contains(fieldInfo[a])
							? (String) GainField.getFieldValueByName(fieldInfo[a], tempIndex)
							: (String) GainField.getFieldValueByName(fieldInfo[a], detail);
					if (index.toFieldnameString().contains(targetFieldInfo[a])) {
						GainField.setFieldValueByName(targetFieldInfo[a], index, value);
					} else {
						if (detail != null) {
							GainField.setFieldValueByName(targetFieldInfo[a], detail, value);
						}
					}
				}
			}
			entryIndexCaptureRepository.save(index);
			if (detail != null) {
				entryDetailCaptureRepository.save(detail);
			}
		}
		return new ExtMsg(true, "数据移交成功！", null);
	}
}