package com.wisdom.web.service;

import com.wisdom.util.GainField;
import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by RonJiang on 2018/2/25 0025.
 */
@Service
@Transactional
public class CodesettingAlignService {

    @Autowired
    EntryIndexRepository entryIndexRepository;
    
    @Autowired
    EntryDetailRepository entryDetailRepository;

    @Autowired
    NodesettingService nodesettingService;

    @Autowired
    CodesettingService codesettingService;

    @Autowired
    CodesetRepository codesetRepository;
    
    @Autowired
    TemplateRepository templateRepository;
    
    @Autowired
    DataNodeRepository dataNodeRepository;
    
    @Autowired
    RightOrganRepository rightOrganRepository;

    @Autowired
    EntryIndexService entryIndexService;

    @Autowired
	EntryIndexCaptureRepository entryIndexCaptureRepository;

    @Autowired
	EntryDetailCaptureRepository entryDetailCaptureRepository;

	@Autowired
	IndexDetailCaptureRepository indexDetailCaptureRepository;

	@Autowired
	ClassifySearchService classifySearchService;

	@PersistenceContext
	EntityManager entityManager;

	public ExtMsg alignArchivecode(String condition, String operator, String content, Tb_index_detail formConditions,
			ExtOperators formOperators, ExtDateRangeData daterangedata, String logic, String nodeid,
			boolean ifSearchLeafNode, boolean ifContainSelfNode,String datasoure) {
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

		List<Tb_entry_index> allData = findCodeSettingAlignDataSearch(datasoure, condition, operator, content, formConditions,
				formOperators, daterangedata, logic, nodeid, ifSearchLeafNode, ifContainSelfNode);// 检索全部数据（不分页）
		// 执行对齐操作
		List<Tb_entry_index> allData_copy = new ArrayList<>();
		for (Tb_entry_index tb_entry_index : allData) {
			Tb_entry_index entry_index = new Tb_entry_index();
			BeanUtils.copyProperties(tb_entry_index, entry_index);
			allData_copy.add(entry_index);
		}
		try {
			allData_copy = doAlignCodesetting(allData_copy, alignFieldList, codeSettingFields, codeSettingSplits);
		} catch (NumberFormatException e) {
			return new ExtMsg(false, "请检查需执行对齐操作的字段值中是否包含非数字字符", null);
		} catch (RuntimeException e) {
			return new ExtMsg(false, "请检查需执行对齐操作的字段值中是否有" + e.getMessage(), null);
		}
		entryIndexRepository.save(allData_copy);
		return null;
	}

	public ExtMsg alignArchivecode(String condition, String operator, String content, Tb_entry_index_capture formConditions,
								   ExtOperators formOperators, ExtDateRangeData daterangedata, String logic, String nodeid,
								   boolean ifSearchLeafNode, boolean ifContainSelfNode) {
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

		List<Tb_entry_index_capture> allData = findAlldataBySearch(null, condition, operator, content, formConditions,
				formOperators, daterangedata, logic, nodeid, ifSearchLeafNode, ifContainSelfNode);// 检索全部数据（不分页）
		// 执行对齐操作
		List<Tb_entry_index_capture> allData_copy = new ArrayList<>();
		for (Tb_entry_index_capture tb_entry_index : allData) {
			Tb_entry_index_capture entry_index = new Tb_entry_index_capture();
			BeanUtils.copyProperties(tb_entry_index, entry_index);
			allData_copy.add(entry_index);
		}
		try {
			allData_copy = doCollectAlignCodesetting(allData_copy, alignFieldList, codeSettingFields, codeSettingSplits);
		} catch (NumberFormatException e) {
			return new ExtMsg(false, "请检查需执行对齐操作的字段值中是否包含非数字字符", null);
		} catch (RuntimeException e) {
			return new ExtMsg(false, "请检查需执行对齐操作的字段值中是否有" + e.getMessage(), null);
		}
		entryIndexCaptureRepository.save(allData_copy);
		return null;
	}

	public List<Tb_entry_index> findAlldataBySearch(String type, String condition, String operator, String content,
			Tb_entry_index formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic,
			String nodeid, boolean ifSearchLeafNode, boolean ifContainSelfNode) {
		String[] nodeids;
		if (ifSearchLeafNode) {
			List<String> nodeidList = nodesettingService.getNodeidLoop(nodeid, ifContainSelfNode,
					new ArrayList<String>());
			nodeids = new String[nodeidList.size()];
			nodeidList.toArray(nodeids);
		} else {
			nodeids = new String[] { nodeid };
		}
		List<Tb_entry_index> allData = new ArrayList<>();
		if (type == null) {
			Specification<Tb_entry_index> searchNodeidCondition = ClassifySearchService.getSearchNodeidIndex(nodeids);
			Specifications specifications = Specifications.where(searchNodeidCondition);
			if (content != null) {
				specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator,
						content);
			}
			Specification<Tb_entry_index> formAdvancedSearch = ClassifySearchService
					.getFormAdvancedIndexSearch(formConditions, formOperators, logic);
			Specification<Tb_entry_index> dateRangeCondition = ClassifySearchService
					.getDateRangeIndexCondition(daterangedata);
			allData = entryIndexRepository.findAll(specifications.and(formAdvancedSearch).and(dateRangeCondition));
		} else {
			String[] entryids = entryIndexRepository.findEntryidByNodeidIn(nodeids);
			if (entryids.length > 0) {
				if(entryids.length>1000) {//大于1000的数据需要分断查询
					int quotient = entryids.length / 1000;
					for (int i = 0; i <= quotient; i++) {
						int idsLength = (i + 1) * 1000 > entryids.length ? entryids.length -i * 1000 : 1000;//判断是否够1000
						String[] ids = new String[idsLength];
						System.arraycopy(entryids, i * 1000, ids, 0, idsLength);
						allData.addAll(findDetailByIds(ids));
					}
				}else {
					allData=findDetailByIds(entryids);
				}
			}
		}
		return allData;
	}

	public List<Tb_entry_index> findCodeSettingAlignDataSearch(String datasoure, String condition, String operator, String content,
											   Tb_index_detail formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic,
													String nodeid, boolean ifSearchLeafNode, boolean ifContainSelfNode) {
		String[] nodeids= new String[] { nodeid };
		List<Tb_entry_index> allData = new ArrayList<>();
		String searchCondition = "";
		if (content != null) {
			searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
		}
		String dataStr = ClassifySearchService.getDateRangeCondition(daterangedata);
		String formStr = ClassifySearchService.getFormAdvancedSearch(formConditions, formOperators, logic,null,null,"management");
		String formDetail = "";//标记表单检索是否有副表字段
		if (!"".equals(formStr)) {
			formDetail = formStr.substring(0, 1);
			formStr = formStr.substring(1);
		}
		String table = "v_index_detail";
		if("capture".equals(datasoure)){
			table="v_index_detail_capture";
		}
		if ((condition == null || entryIndexService.checkFilecode(condition) == 0) && ("".equals(formDetail) || "0".equals(formDetail))) {//没副表字段的检索,查总数60W+用tb_entry_index会快8s+
			if("capture".equals(datasoure)){
				table="tb_entry_index_capture";
			}else {
				table = "tb_entry_index";
			}
		}
		String sql = "select sid.* from " + table + " sid where nodeid in('" + String.join("','", nodeid.split(",")) + "') "+
				searchCondition + dataStr + formStr;
		Query query = entityManager.createNativeQuery(sql, Tb_entry_index.class);
		allData.addAll(query.getResultList());
		return allData;
	}
    
    public List<Tb_entry_index> doAlignCodesetting(List<Tb_entry_index> allData,List<String> alignFieldList,List<String> codeSettingFields,List<String> codeSettingSplits){
        Map<String, Map<String, String>> mapFiled = entryIndexService.getConfigMap();//获取参数设置的MAP
        List<Tb_data_template> enumList = templateRepository.getByNodeidFtype("enum", allData.get(0).getNodeid());//获取某节点的模板中属于enum的字段
        String nodeid = allData.get(0).getNodeid();
        String type = templateRepository.findOrganFtypeByNodeid(nodeid);
        Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
        Tb_right_organ right_organ = rightOrganRepository.findByOrganid(node.getRefid());
        for(Tb_entry_index tb_entry_index : allData){
            for(String alignField:alignFieldList){//档号构成字段值补0
                String[] alignFieldStrs = alignField.split("∪");
                String alignFieldcode = alignFieldStrs[0];
                Integer alignFieldlength = Integer.parseInt(alignFieldStrs[1]);//档号设置的单位长度
                String alignFieldValue = GainField.getFieldValueByName(alignFieldcode,tb_entry_index)!=null?(String)GainField.getFieldValueByName(alignFieldcode,tb_entry_index):"";
                if (isNumeric(alignFieldValue)) {
                	int currentFieldlength = alignFieldValue.length();//字段值当前的长度
                    if(alignFieldlength!=currentFieldlength && alignFieldValue.length()>0){
                    	String alignedFieldValue = entryIndexService.alignValue(alignFieldlength, Integer.valueOf(alignFieldValue));
                        GainField.setFieldValueByName(alignFieldcode,tb_entry_index,alignedFieldValue);
                    }
                }
            }
            String archivecode = "";
            for(int i=0;i<codeSettingFields.size()-1;i++){//重新生成档号
                String codeSettingFieldValue = GainField.getFieldValueByName(codeSettingFields.get(i),tb_entry_index)!=null?(String)GainField.getFieldValueByName(codeSettingFields.get(i),tb_entry_index):"";
                String field = codeSettingFields.get(i);
                String split = codeSettingSplits.get(i);
                if("".equals(codeSettingFieldValue)){
//                    throw new RuntimeException("档号构成字段值为空");
                	archivecode += codeSettingFieldValue + split;
                }
                if (!field.equals("organ")) {
                    codeSettingFieldValue = entryIndexService.getConfigByName(codeSettingFields.get(i), codeSettingFieldValue, enumList, mapFiled);
                	archivecode += codeSettingFieldValue + split;
                }
                if (field.equals("organ")) {
                	if (type.equals("string")) {
	                	if (right_organ.getCode() != null && !right_organ.getCode().equals("")) {
	                		archivecode += right_organ.getCode() + split;
	                	} else {
	                		archivecode += node.getNodename() + split;
	                	}
                	} else {
                		archivecode += codeSettingFieldValue + split;
                	}
                }
            }
            String calFieldcode = codeSettingFields.get(codeSettingFields.size()-1);
            String calFieldvalue = GainField.getFieldValueByName(calFieldcode,tb_entry_index)!=null?(String)GainField.getFieldValueByName(calFieldcode,tb_entry_index):"";
            if("".equals(calFieldvalue)){
                throw new RuntimeException("计算项字段值为空");
            }
            archivecode+=calFieldvalue;
            tb_entry_index.setArchivecode(archivecode);
        }
        return allData;
    }

	/**
	 * 根据ids查找detail
	 * @param ids
	 * @return
	 */
	private List<Tb_entry_index> findDetailByIds(String[] ids){
		List<Tb_entry_detail> detail = entryDetailRepository.findByEntryidIn(ids);
		List<String> idStrings = new ArrayList<>();
		for (int i = 0; i < detail.size(); i++) {
			//if (condition !=null && GainField.getFieldValueByName(condition, detail.get(i)).toString().contains(content)) {
			idStrings.add(detail.get(i).getEntryid());
			//}
		}
		List<Tb_entry_index> allData = entryIndexRepository.findByEntryidIn(idStrings.toArray(new String[idStrings.size()]));
		return allData;
	}

    private boolean isNumeric(String str){ 
    	Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

	public List<Tb_entry_index_capture> doCollectAlignCodesetting(List<Tb_entry_index_capture> allData,List<String>
			alignFieldList,
																  List<String> codeSettingFields,List<String> codeSettingSplits){
		Map<String, Map<String, String>> mapFiled = entryIndexService.getConfigMap();//获取参数设置的MAP
		List<Tb_data_template> enumList = templateRepository.getByNodeidFtype("enum", allData.get(0).getNodeid());//获取某节点的模板中属于enum的字段
		String nodeid = allData.get(0).getNodeid();
		String type = templateRepository.findOrganFtypeByNodeid(nodeid);
		Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
		Tb_right_organ right_organ = rightOrganRepository.findByOrganid(node.getRefid());
		for(Tb_entry_index_capture tb_entry_index : allData){
			for(String alignField:alignFieldList){//档号构成字段值补0
				String[] alignFieldStrs = alignField.split("∪");
				String alignFieldcode = alignFieldStrs[0];
				Integer alignFieldlength = Integer.parseInt(alignFieldStrs[1]);//档号设置的单位长度
				String alignFieldValue = GainField.getFieldValueByName(alignFieldcode,tb_entry_index)!=null?(String)GainField.getFieldValueByName(alignFieldcode,tb_entry_index):"";
				if (isNumeric(alignFieldValue)) {
					int currentFieldlength = alignFieldValue.length();//字段值当前的长度
					if(alignFieldlength!=currentFieldlength && alignFieldValue.length()>0){
						String alignedFieldValue = entryIndexService.alignValue(alignFieldlength, Integer.valueOf
								(alignFieldValue));
						GainField.setFieldValueByName(alignFieldcode,tb_entry_index,alignedFieldValue);
					}
				}
			}
			String archivecode = "";
			for(int i=0;i<codeSettingFields.size()-1;i++){//重新生成档号
				String codeSettingFieldValue = GainField.getFieldValueByName(codeSettingFields.get(i),tb_entry_index)!=null?(String)GainField.getFieldValueByName(codeSettingFields.get(i),tb_entry_index):"";
				String field = codeSettingFields.get(i);
				String split = codeSettingSplits.get(i);
				if("".equals(codeSettingFieldValue)){
//                    throw new RuntimeException("档号构成字段值为空");
					archivecode += codeSettingFieldValue + split;
				}
				if (!field.equals("organ")) {
					codeSettingFieldValue = entryIndexService.getConfigByName(codeSettingFields.get(i),
							codeSettingFieldValue, enumList, mapFiled);
					archivecode += codeSettingFieldValue + split;
				}
				if (field.equals("organ")) {
					if (type.equals("string")) {
						if (right_organ.getCode() != null && !right_organ.getCode().equals("")) {
							archivecode += right_organ.getCode() + split;
						} else {
							archivecode += node.getNodename() + split;
						}
					} else {
						archivecode += codeSettingFieldValue + split;
					}
				}
			}
			String calFieldcode = codeSettingFields.get(codeSettingFields.size()-1);
			String calFieldvalue = GainField.getFieldValueByName(calFieldcode,tb_entry_index)!=null?(String)
					GainField.getFieldValueByName(calFieldcode,tb_entry_index):"";
			if("".equals(calFieldvalue)){
				throw new RuntimeException("计算项字段值为空");
			}
			archivecode+=calFieldvalue;
			tb_entry_index.setArchivecode(archivecode);
		}
		return allData;
	}

	public List<Tb_entry_index_capture> findAlldataBySearch(String type, String condition, String operator, String content,
															Tb_entry_index_capture formConditions, ExtOperators formOperators,
															ExtDateRangeData daterangedata, String logic,
															String nodeid, boolean ifSearchLeafNode, boolean ifContainSelfNode) {
		String[] nodeids;
		if (ifSearchLeafNode) {
			List<String> nodeidList = nodesettingService.getNodeidLoop(nodeid, ifContainSelfNode,
					new ArrayList<String>());
			nodeids = new String[nodeidList.size()];
			nodeidList.toArray(nodeids);
		} else {
			nodeids = new String[] { nodeid };
		}
		List<Tb_entry_index_capture> allData = new ArrayList<>();
		if (type == null) {
			Specification<Tb_entry_index_capture> searchNodeidCondition = ClassifySearchService.getcollectSearchNodeidIndex
					(nodeids);
			Specifications specifications = Specifications.where(searchNodeidCondition);
			if (content != null) {
				specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator,
						content);
			}
			Specification<Tb_entry_index_capture> formAdvancedSearch = ClassifySearchService
					.getFormAdvancedIndexCaptureSearch(formConditions, formOperators, logic);
			Specification<Tb_entry_index_capture> dateRangeCondition = ClassifySearchService
					.getDateRangeIndexCaptureCondition(daterangedata);
			allData = entryIndexCaptureRepository.findAll(specifications.and(formAdvancedSearch).and
					(dateRangeCondition));
		} else {
			String[] entryids = entryIndexCaptureRepository.findEntryidByNodeidIn(nodeids);
			List<Tb_entry_detail_capture> detail = new ArrayList<>();
			if (entryids.length > 0) {
				detail = entryDetailCaptureRepository.findByEntryidIn(entryids);
			}
			List<String> idStrings = new ArrayList<>();
			for (int i = 0; i < detail.size(); i++) {
				//if (condition !=null && GainField.getFieldValueByName(condition, detail.get(i)).toString().contains(content)) {
				idStrings.add(detail.get(i).getEntryid());
				//}
			}
			allData = entryIndexCaptureRepository.findByEntryidIn(idStrings.toArray(new String[idStrings.size()]));
		}
		return allData;
	}

	//数据审核用的档号对齐
	public List<Tb_index_detail_capture> findAlldataBySearch(String type, String condition, String operator, String content,
															 Tb_index_detail_capture formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic,
															 String nodeid, boolean ifSearchLeafNode, boolean ifContainSelfNode,String docid) {
		String[] nodeids;
		if (ifSearchLeafNode) {
			List<String> nodeidList = nodesettingService.getNodeidLoop(nodeid, ifContainSelfNode,
					new ArrayList<String>());
			nodeids = new String[nodeidList.size()];
			nodeidList.toArray(nodeids);
		} else {
			nodeids = new String[] { nodeid };
		}
		List<Tb_index_detail_capture> allData = new ArrayList<>();
		if (type == null) {
			Specification<Tb_index_detail_capture> searchNodeidCondition = ClassifySearchService.getSearchNodeidIndexcapture(nodeids);
			Specifications specifications = Specifications.where(searchNodeidCondition);
			if (content != null) {
				specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator,
						content);
			}
			Specification<Tb_index_detail_capture> formAdvancedSearch = ClassifySearchService
					.getFormAdvancedIndexDetailSearch(formConditions, formOperators, logic);
			allData = indexDetailCaptureRepository.findAll(specifications.and(formAdvancedSearch));
		} else {
			String status =Tb_transdoc_entry.STATUS_AUDIT;
			String shCondition="";//审核筛选
			if(docid!=null && !"".equals(docid)){//数据审核模块查看单据的条目详细信息
				shCondition=" and entryid in(select entryid from tb_transdoc_entry where docid ='"+ docid
						+"' and status='"+status+"')";
			}else {//数据采集模块查看单据的条目详细信息
				shCondition=" and entryid not in(select entryid from tb_transdoc_entry where status='"+status+"')";
			}
			String searchCondition = "";//检索框
			if (content != null && !"".equals(content)) {// 输入框检索
				searchCondition = classifySearchService.getSqlByConditionsto(condition, content, "sid", operator);
			}
			String sql = "select sid.* from v_index_detail sid where 1=1 "+ searchCondition +shCondition;
			Query query = entityManager.createNativeQuery(sql, Tb_index_detail_capture.class);
			allData = query.getResultList();
		}
		return allData;
	}

}