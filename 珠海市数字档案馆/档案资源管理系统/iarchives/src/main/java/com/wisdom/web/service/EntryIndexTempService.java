package com.wisdom.web.service;

import com.wisdom.util.DBCompatible;
import com.wisdom.util.GainField;
import com.wisdom.web.entity.Tb_data_node;
import com.wisdom.web.entity.Tb_data_template;
import com.wisdom.web.entity.Tb_entry_detail;
import com.wisdom.web.entity.Tb_entry_detail_capture;
import com.wisdom.web.entity.Tb_entry_index_temp;
import com.wisdom.web.entity.Tb_right_organ;
import com.wisdom.web.repository.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by RonJiang on 2018/2/1 0001.
 */
@Service
@Transactional
public class EntryIndexTempService {

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	EntryIndexTempRepository entryIndexTempRepository;

	@Autowired
	EntryIndexService entryIndexService;

	@Autowired
	CodesettingService codesettingService;

	@Autowired
	EntryIndexTempService entryIndexTempService;

	@Autowired
	EntryIndexCaptureService entryIndexCaptureService;

	@Autowired
	CodesetRepository codesetRepository;

	@Autowired
	DataNodeRepository dataNodeRepository;

	@Autowired
	RightOrganRepository rightOrganRepository;

	@Autowired
	TemplateRepository templateRepository;

	@Autowired
	SystemConfigRepository systemConfigRepository;

	@Autowired
	EntryDetailRepository entryDetailRepository;

	@Autowired
	EntryDetailCaptureRepository entryDetailCaptureRepository;

	public Page<Tb_entry_index_temp> findByEntryidAndSearchTemp(String[] entryidArr, String condition, String operator,
			String content, int page, int limit) {
		PageRequest pageRequest = new PageRequest(page - 1, limit);
		Specification<Tb_entry_index_temp> searchEntryID = getTempSearchEntryidCondition(entryidArr);
		Specifications specifications = Specifications.where(searchEntryID);
		if (content != null) {
			specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
		}
		return entryIndexTempRepository.findAll(specifications, pageRequest);
	}

	public void deleteAllEntryindex() {
		entryIndexTempRepository.deleteAll();
	}

	private boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/**
	 * 调整保管期限、计算项、档号值
	 * 
	 * @param entryids
	 *            需调整记录的条目id
	 * @param entryretention
	 *            调整后的保管期限值
	 * @param nodeid
	 *            节点id
	 * @param calAdjustParam
	 *            计算项调整参数
	 * @return
	 */
	public List<Tb_entry_index_temp> retentionAjust(String[] entryids, String entryretention, String nodeid,
			Integer calAdjustParam, String type) {
		boolean isExist = false;
		List<String> codeSettingFieldList = codesettingService.getCodeSettingFields(nodeid);
		for (int i = 0; i < codeSettingFieldList.size(); i++) {
			if (codeSettingFieldList.get(i).equals("entryretention")) {
				isExist = true;
			}
		}
		String uniquetag;
		if("数据采集".equals(type)){//数据采集
			uniquetag=BatchModifyService.getUniquetagByType("cjgd");
		}else{//数据管理
			uniquetag=BatchModifyService.getUniquetagByType("glgd");
		}
		List<Tb_entry_index_temp> entryIndexTempList = entryIndexTempRepository.findByEntryidInAndUniquetag(entryids, uniquetag);
		List<Tb_entry_index_temp> entryIndexTempList_copy = new ArrayList<>();
		for (int i = 0; i < entryIndexTempList.size(); i++) {
			Tb_entry_index_temp entryIndexTemp_copy = new Tb_entry_index_temp();
			BeanUtils.copyProperties(entryIndexTempList.get(i), entryIndexTemp_copy);
			if (type != null && type.equals("数据管理")) {
				Tb_entry_detail detail = entryDetailRepository.findByEntryid(entryIndexTempList.get(i).getEntryid());
				if (detail != null) {
					BeanUtils.copyProperties(detail, entryIndexTemp_copy);
				}
			} else {
				Tb_entry_detail_capture capture = entryDetailCaptureRepository
						.findByEntryid(entryIndexTempList.get(i).getEntryid());
				if (capture != null) {
					BeanUtils.copyProperties(capture, entryIndexTemp_copy);
				}
			}
			if (entryretention != null) {
				entryIndexTemp_copy.setEntryretention(entryretention);// 设置保管期限值
			}
			if (isExist) {// 如果存在保管期限
				/* 重新设置计算项及档号值 */
				List<String> codeSettingFieldValues = getCodeSettingFieldValues(entryIndexTemp_copy, nodeid);
				List<String> codeSettingSplits = codesettingService.getCodeSettingSplitCodes(nodeid);
				List<Object> fieldLength = codesetRepository.findFieldlengthByDatanodeid(nodeid);// 获取档号组成字段单位长度
				String code = "";
				Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
				Tb_right_organ right_organ = rightOrganRepository.findByOrganid(node.getRefid());
				for (int j = 0; j < codeSettingFieldValues.size() - 1; j++) {
					if (!"".equals(codeSettingFieldValues.get(j))) {
						if (j < codeSettingFieldValues.size() - 2) {
							if (codeSettingFieldList.get(j).equals("organ")) {
								if (right_organ.getCode() == null || right_organ.getCode().equals("")) {
									code += codeSettingFieldValues.get(j) + codeSettingSplits.get(j);
								} else if (codeSettingFieldList.get(j).equals("entryretention")) {
									code += systemConfigRepository
											.findConfigvalueByConfigcode(codeSettingFieldValues.get(j)).get(0)
											+ codeSettingSplits.get(j);
								} else {
									code += right_organ.getCode() + codeSettingSplits.get(j);
								}
							} else {
								code += codeSettingFieldValues.get(j) + codeSettingSplits.get(j);
							}
						}
						if (j == codeSettingFieldValues.size() - 2) {
							if (codeSettingFieldList.get(j).equals("organ")) {
								if (right_organ.getCode() == null || right_organ.getCode().equals("")) {
									code += codeSettingFieldValues.get(j);
								} else if (codeSettingFieldList.get(j).equals("entryretention")) {
									code += systemConfigRepository
											.findConfigvalueByConfigcode(codeSettingFieldValues.get(j)).get(0)
											+ codeSettingSplits.get(j);
								} else {
									code += right_organ.getCode();
								}
							} else {
								code += codeSettingFieldValues.get(j);
							}
						}
					} else {
						return null;
					}
				}
				// 获取到计算项的最大值
				Integer calIntValue = 0;
				if ("数据采集".equals(type)) {
					String sql = "select max("
							+ DBCompatible.getInstance()
									.findExpressionOfToNumber(codeSettingFieldList.get(codeSettingFieldList.size() - 1))
							+ ") from tb_entry_index_capture where archivecode like '" + code + "%' and nodeid='"
							+ nodeid + "'";
					Query query = entityManager.createNativeQuery(sql);
					int maxCalValue = query.getSingleResult() == null ? 0
							: Integer.valueOf(query.getSingleResult().toString());
					if (maxCalValue == 0) {
						calIntValue = 1;
					}
					calIntValue = maxCalValue + 1;
				} else if ("数据管理".equals(type)) {
					String sql = "select max("
							+ DBCompatible.getInstance()
									.findExpressionOfToNumber(codeSettingFieldList.get(codeSettingFieldList.size() - 1))
							+ ") from tb_entry_index where archivecode like '" + code + "%' and nodeid='" + nodeid
							+ "'";
					Query query = entityManager.createNativeQuery(sql);
					int maxCalValue = query.getSingleResult() == null ? 0
							: Integer.valueOf(query.getSingleResult().toString());
					if (maxCalValue == 0) {
						calIntValue = 1;
					}
					calIntValue = maxCalValue + 1;
				} else {
					calIntValue = entryIndexTempService.getCalValue(entryIndexTemp_copy, nodeid, codeSettingFieldList)
							+ calAdjustParam;
				}
				Integer maxCalValue = entryIndexTempService.getCalValue(entryIndexTemp_copy, nodeid,
						codeSettingFieldList);
				if (maxCalValue != null) {
					// if (calIntValue <= maxCalValue+calAdjustParam) {
					// calIntValue += 1;
					// }
					if (calIntValue <= maxCalValue + calAdjustParam) {
						calIntValue = maxCalValue + calAdjustParam;
						if (calIntValue == 0) {
							calIntValue += 1;
						}
					}
				} else {
					return null;
				}
				String calvalue = entryIndexService.alignValue(fieldLength.get(fieldLength.size() - 1), calIntValue);
				if (codeSettingFieldValues.size() == 1) {
					// 若档号构成字段只有一个，则此档号字段值传入produceArchivecode方法前，将集合清空，保证档号生成正确
					codeSettingFieldValues.remove(codeSettingFieldValues.get(0));
				}
				String archivecode = entryIndexService.produceArchivecode(codeSettingFieldValues, codeSettingSplits,
						calvalue, nodeid);// 档号
				List<String> codeList = entryIndexTempRepository.findByNodeid(archivecode, nodeid,uniquetag);
				if (codeList.size() >= 1) {
					if (type != null) {
						if (type.equals("数据采集") || type.equals("数据管理")) {
							calIntValue = entryIndexTempService.getCalValue(entryIndexTemp_copy, nodeid,
									codeSettingFieldList);
							calvalue = entryIndexService.alignValue(fieldLength.get(fieldLength.size() - 1),
									calIntValue);
						}
					} else {
						if (i > 0) {
							calvalue = entryIndexService.alignValue(fieldLength.get(fieldLength.size() - 1),
									calIntValue + 1);
						}
					}
					archivecode = entryIndexService.produceArchivecode(codeSettingFieldValues, codeSettingSplits,
							calvalue, nodeid);// 重新生成档号
				}
				GainField.setFieldValueByName(codeSettingFieldList.get(codeSettingFieldList.size() - 1),
						entryIndexTemp_copy, calvalue);
				entryIndexTemp_copy.setArchivecode(archivecode);
				entryIndexTempRepository.delete(entryIndexTemp_copy.getEntryid());
			}
			entryIndexTempRepository.save(entryIndexTemp_copy);
			entryIndexTempList_copy.add(entryIndexTemp_copy);
		}
		return entryIndexTempList_copy;
	}

	public List<String> getCodeSettingFieldValues(Tb_entry_index_temp entryIndexTemp, String nodeid) {
		List<String> codeSettingFieldValues = new ArrayList<>();
		Map<String, Map<String, String>> mapFiled = entryIndexService.getConfigMap();// 获取参数设置的MAP
		List<Tb_data_template> enumList = templateRepository.getByNodeidFtype("enum", nodeid);// 获取某节点的模板中属于enum的字段
		List<String> codesettingFieldcodeList = codesettingService.getCodeSettingFields(nodeid);
		for (String codesettingFieldcode : codesettingFieldcodeList) {
			String codeSettingFieldValue = GainField.getFieldValueByName(codesettingFieldcode, entryIndexTemp) != null
					? GainField.getFieldValueByName(codesettingFieldcode, entryIndexTemp) + "" : "";
			codeSettingFieldValue = entryIndexService.getConfigByName(codesettingFieldcode, codeSettingFieldValue,
					enumList, mapFiled);
			codeSettingFieldValues.add(codeSettingFieldValue);
		}
		return codeSettingFieldValues;
	}

	public List<Tb_entry_index_temp> ajustAllCalData(String uniquetag, String info,String type,String nodeid) {
		List<Tb_entry_index_temp> temps=entryIndexTempRepository.findByUniquetagOrderBySortsequence(uniquetag);
		Set<String> entryretentionSet = new HashSet<>();// 保管期限存储
		String[] entryidInfo = null;// 节点id存储
		int i = 0;
		for (Tb_entry_index_temp entryIndexTemp : temps) {
			if (entryIndexTemp.getEntryretention() != null) {
				entryretentionSet.add(entryIndexTemp.getEntryretention());
			}
			if (entryIndexTemp.getEntryretention() == null) {
				entryidInfo = new String[temps.size()];
				entryidInfo[i] = entryIndexTemp.getEntryid();
				i++;
			}
		}
		if (info.equals("ok")) {
			if (entryretentionSet.size() > 0) {// 如果存在保管期限
				Map<String, List<String>> entryretentionEntryidsMap = new HashMap<>();
				for (String entryretention : entryretentionSet) {
					entryretentionEntryidsMap.put(entryretention, entryIndexTempRepository
							.findEntryidByEntryretention(entryretention, uniquetag));
				}
				for (Map.Entry<String, List<String>> entry : entryretentionEntryidsMap.entrySet()) {
					String[] entryidsArr = new String[entry.getValue().size()];
					entry.getValue().toArray(entryidsArr);
					// 此时计算项值已根据采集表中数据生成正确，并保存至临时表，
					// 若不设置调整计算项参数为-1，则执行调整方法后，所有记录的计算项值会根据临时表数据再次重新生成计算项值，即再次增1，
					// 故此处传入参数-1
					entryIndexTempService.retentionAjust(entryidsArr, entry.getKey(), nodeid, -1, type);
				}
			}
			if (entryidInfo != null) {// 如果没有保管期限
				entryIndexTempService.retentionAjust(entryidInfo, null, nodeid, -1, type);
			}
		}
		return entryIndexTempRepository.findByUniquetagOrderBySortsequence(uniquetag);
	}

	/**
	 * 此方法慎用（临时表中数据不全，在临时表中获取计算项的值，可能会返回非预期的结果）
	 */
	public Integer getCalValue(Tb_entry_index_temp entryIndexTemp, String nodeid, List<String> codeSettingFieldList) {
		Integer calValue = null;
		if (codeSettingFieldList.size() == 1) {// 档号设置只有一个计算项字段，无其它字段
			String sql = "select max("
					+ DBCompatible.getInstance().findExpressionOfToNumber(codeSettingFieldList.get(0))
					+ ") from tb_entry_index_temp where nodeid='" + nodeid + "'";
			Query query = entityManager.createNativeQuery(sql);
			int maxCalValue = query.getSingleResult() == null ? 0 : Integer.valueOf(query.getSingleResult().toString());
			if (maxCalValue == 0) {
				return 1;
			}
			calValue = maxCalValue + 1;
			return calValue;
		}
		String codeSettingFieldValues = "";
		List<String> spList = codesetRepository.findSplitcodeByDatanodeid(nodeid);
		for (int i = 0; i < codeSettingFieldList.size() - 1; i++) {
			String value = "";
			// 通过反射获得档号字段的页面输入值，不含最后一个（计算项）
			String codeSettingFieldValue = GainField.getFieldValueByName(codeSettingFieldList.get(i), entryIndexTemp)
					+ "";
			if (isNumeric(codeSettingFieldValue)) {
				Integer length = Integer
						.parseInt(codesetRepository.findFieldlengthByDatanodeid(nodeid).get(i).toString());
				value = entryIndexService.alignValue(length, Integer.valueOf(codeSettingFieldValue));
			} else {
				if (codeSettingFieldList.get(i).equals("organ")) {
					Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
					Tb_right_organ right_organ = rightOrganRepository.findByOrganid(node.getRefid());
					if (right_organ.getCode() == null || right_organ.getCode().equals("")) {
						value = codeSettingFieldValue;
					} else {
						value = right_organ.getCode();
					}
				} else if (codeSettingFieldList.get(i).equals("entryretention")) {
					value = systemConfigRepository.findConfigvalueByConfigcode(codeSettingFieldValue).get(0);
				} else {
					value = codeSettingFieldValue;// 需要将
				}
			}
			if (!"null".equals(codeSettingFieldValue) && !"".equals(codeSettingFieldValue)) {
				if (i < codeSettingFieldList.size() - 2) {
					codeSettingFieldValues += value + spList.get(i);
				}
				if (i == codeSettingFieldList.size() - 2) {
					codeSettingFieldValues += value;
				}
			} else {// 页面中档号设置字段无输入值
				return null;
			}
		}
		String calValueFieldCode = codeSettingFieldList.get(codeSettingFieldList.size() - 1);
		String sql = "select max(" + DBCompatible.getInstance().findExpressionOfToNumber(calValueFieldCode)
				+ ") from tb_entry_index_temp where archivecode like '" + codeSettingFieldValues + "%' and nodeid='"
				+ nodeid + "'";
		Query query = entityManager.createNativeQuery(sql);
		int maxCalValue = query.getSingleResult() == null ? 0 : Integer.valueOf(query.getSingleResult().toString());
		if (maxCalValue == 0) {
			return 1;
		}
		calValue = maxCalValue + 1;
		return calValue;
	}

	public static Specification<Tb_entry_index_temp> getTempSearchEntryidCondition(String[] entryidArr) {
		Specification<Tb_entry_index_temp> searchEntryID = new Specification<Tb_entry_index_temp>() {
			@Override
			public Predicate toPredicate(Root<Tb_entry_index_temp> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				Predicate[] predicates = new Predicate[entryidArr.length];
				for (int i = 0; i < entryidArr.length; i++) {
					predicates[i] = criteriaBuilder.equal(root.get("entryid"), entryidArr[i]);
				}
				return criteriaBuilder.or(predicates);
			}
		};
		return searchEntryID;
	}
}