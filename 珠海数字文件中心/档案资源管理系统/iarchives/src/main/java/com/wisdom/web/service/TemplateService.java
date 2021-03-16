package com.wisdom.web.service;

import com.wisdom.secondaryDataSource.entity.*;
import com.wisdom.secondaryDataSource.entity.Tb_codeset_sx;
import com.wisdom.secondaryDataSource.entity.Tb_data_node_sx;
import com.wisdom.secondaryDataSource.entity.Tb_data_template_sx;
import com.wisdom.secondaryDataSource.repository.SecondaryDataNodeRepository;
import com.wisdom.secondaryDataSource.repository.SxCodesetRepository;
import com.wisdom.secondaryDataSource.repository.SxTemplateDescRepository;
import com.wisdom.secondaryDataSource.repository.SxTemplateRepository;
import com.wisdom.util.*;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.*;
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
import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import static com.wisdom.web.service.ThematicService.delFolder;

/**
 * Created by Rong on 2017/10/30.
 */
@Service
@Transactional
public class TemplateService {
	@Value("${system.document.rootpath}")
	private String rootpath;// 系统文件根目录

	@PersistenceContext
	EntityManager entityManager;
	@PersistenceContext(unitName="entityManagerFactorySecondary")
	EntityManager entityManagerSx;

	@Autowired
	TemplateRepository templateRepository;

	@Autowired
	private DataNodeRepository dataNodeRepository;

	@Autowired
	CodesetRepository codesetRepository;

	@Autowired
	CodesettingService codesettingService;

	@Autowired
	NodesettingService nodesettingService;

	@Autowired
	SxTemplateRepository sxTemplateRepository;
	@Autowired
	SxCodesetRepository sxCodesetRepository;
	@Autowired
	SecondaryDataNodeRepository secondaryDataNodeRepository;

	@Autowired
	MetadataTempRepository metadataTempRepository;

	@Autowired
	TemplateDescRepository templateDescRepository;

	@Autowired
	SxTemplateDescRepository sxTemplateDescRepository;


	/**
	 * 获取档号组成字段中文名称
	 *
	 * @param dataNodeid
	 * @return
	 */
	public String getFieldName(String dataNodeid) {
		String value = "";
		List<String> codesetFieldCodes = codesetRepository.findFieldcodeByDatanodeid(dataNodeid);
		for (int i = 0; i < codesetFieldCodes.size(); i++) {
			String name = templateRepository.findFieldNameByFieldcodeAndNodeid(codesetFieldCodes.get(i), dataNodeid);
			if (i > 0) {
				value += "、";
			}
			value += name;
		}
		return value;
	}

	/**
	 * 根据节点id和qfield查找检索字段
	 *
	 * @param nodeid
	 * @return
	 */
	public List<ExtSearchData> queryConditionTemplate(String nodeid) {
		List<Tb_data_template> queryList = templateRepository.findQueryByNode(nodeid);// 相应节点下的查询字段的模板
		List<ExtSearchData> queryConditionList = new ArrayList<ExtSearchData>();// 存放指定节点所有查询字段
		for (Tb_data_template template : queryList) {
			ExtSearchData extSearchData = new ExtSearchData();
			extSearchData.setItem(template.getFieldcode());
			extSearchData.setName(template.getFieldname());
			queryConditionList.add(extSearchData);
		}
		return queryConditionList;
	}

	public Page<Tb_template_desc> findDescBySearch(int page, int limit, String condition, String operator, String content,
												   String nodeID, Sort sort) {
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort("fieldcode") : sort);
		Specifications specifications = null;
		if (content != null) {
			specifications = Specifications.where(new SpecificationUtil(condition, operator, content));
			return templateDescRepository.findAll(specifications, pageRequest);
		}
		return templateDescRepository.findAll(pageRequest);
	}

	public Page<Tb_template_desc_sx> findSxDescBySearch(int page, int limit, String condition, String operator,
													String content,
												   String nodeID, Sort sort) {
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort("fieldcode") : sort);
		Specifications specifications = null;
		if (content != null) {
			specifications = Specifications.where(new SpecificationUtil(condition, operator, content));
			return sxTemplateDescRepository.findAll(specifications, pageRequest);
		}
		return sxTemplateDescRepository.findAll(pageRequest);
	}

	//更新指定模板字段描述表
	public void updateTemplateDesc(String fieldcode){
		String[] fieldNameArray = templateRepository.getOrderFileName(fieldcode);
		String fullFieldName = "";
		if(fieldNameArray.length >0 ) {
			for(String fieldname:fieldNameArray){
				if(fieldname!=null&&!fieldname.startsWith("字段描述")){
					fullFieldName+=fieldname+",";
				}
			}
		}
		if(!"".equals(fullFieldName)){
			fullFieldName=fullFieldName.substring(0,fullFieldName.lastIndexOf(","));
		}
		List<Tb_template_desc> templateDescs= templateDescRepository.findByFieldcode(fieldcode);
		if(templateDescs.size()>0){//有字段存在时就修改
			Tb_template_desc templateDesc=templateDescs.get(0);
			templateDesc.setDescs(fullFieldName);
			templateDescRepository.save(templateDesc);
		}else{//还没有就增加
			Tb_template_desc templateDesc=new Tb_template_desc();
			templateDesc.setFieldcode(fieldcode);
			templateDesc.setDescs(fullFieldName);
			templateDescRepository.save(templateDesc);
		}
	}

	//更新所有模板副表字段描述表
	public void updateTemplateDesc(){
		//副表字段份f01~f50
		String[] appendFieldNameArray = new String[50];
		for (int i = 1; i <= appendFieldNameArray.length; i++) {
			if (i < 10) {
				appendFieldNameArray[i - 1] = "f0" + i;
			} else {
				appendFieldNameArray[i - 1] = "f" + i;
			}
		}
		for(String fieldcode:appendFieldNameArray){
			updateTemplateDesc(fieldcode);//更新指定模板字段描述表
		}
	}

	//更新指定模板字段描述表
	public void updateTemplateSxDesc(String fieldcode){
		String[] fieldNameArray = sxTemplateRepository.getOrderFileName(fieldcode);
		String fullFieldName = "";
		if(fieldNameArray.length >0 ) {
			for(String fieldname:fieldNameArray){
				if(fieldname!=null&&!fieldname.startsWith("字段描述")){
					fullFieldName+=fieldname+",";
				}
			}
		}
		if(!"".equals(fullFieldName)){
			fullFieldName=fullFieldName.substring(0,fullFieldName.lastIndexOf(","));
		}
		List<Tb_template_desc_sx> templateDescs= sxTemplateDescRepository.findByFieldcode(fieldcode);
		if(templateDescs.size()>0){//有字段存在时就修改
			Tb_template_desc_sx templateDesc=templateDescs.get(0);
			templateDesc.setDescs(fullFieldName);
			sxTemplateDescRepository.save(templateDesc);
		}else{//还没有就增加
			Tb_template_desc_sx templateDesc=new Tb_template_desc_sx();
			templateDesc.setFieldcode(fieldcode);
			templateDesc.setDescs(fullFieldName);
			sxTemplateDescRepository.save(templateDesc);
		}
	}

	//更新所有模板副表字段描述表
	public void updateTemplateSxDesc(){
		//副表字段份f01~f50
		String[] appendFieldNameArray = new String[50];
		for (int i = 1; i <= appendFieldNameArray.length; i++) {
			if (i < 10) {
				appendFieldNameArray[i - 1] = "f0" + i;
			} else {
				appendFieldNameArray[i - 1] = "f" + i;
			}
		}
		for(String fieldcode:appendFieldNameArray){
			updateTemplateSxDesc(fieldcode);//更新指定模板字段描述表
		}
	}

	/**
	 * 根据节点id和qfield查找检索字段  声像
	 *
	 * @param nodeid
	 * @return
	 */
	public List<ExtSearchData> queryConditionSxTemplate(String nodeid) {
		List<Tb_data_template_sx> queryList = sxTemplateRepository.findQueryByNode(nodeid);// 相应节点下的查询字段的模板
		List<ExtSearchData> queryConditionList = new ArrayList<ExtSearchData>();// 存放指定节点所有查询字段
		for (Tb_data_template_sx template : queryList) {
			ExtSearchData extSearchData = new ExtSearchData();
			extSearchData.setItem(template.getFieldcode());
			extSearchData.setName(template.getFieldname());
			queryConditionList.add(extSearchData);
		}
		return queryConditionList;
	}

	public boolean updateQueue(String nodeid,String[] leftfieldnamelist,String[] rightfieldnamelist) {
		List<Tb_data_template> rightlist=new ArrayList<Tb_data_template>();
		List<Tb_data_template>	leftlist=new ArrayList<Tb_data_template>();
		if(leftfieldnamelist!=null) {
            for (int i = 0; i < leftfieldnamelist.length; i++) {
                leftlist.add(templateRepository.findByNodeidAndFieldcode(nodeid, leftfieldnamelist[i]));
            }
            Long[] left=leftlist.stream().map(p->p.getFsequence()).toArray(Long[]::new);
            for(int i=0;i<left.length;i++){leftlist.get(i).setFsequence(left[i]);leftlist.get(i).setFfield(false);}
            templateRepository.save(leftlist);
        }
        if(rightfieldnamelist!=null) {
            for (int i = 0; i < rightfieldnamelist.length; i++) {
                rightlist.add(templateRepository.findByNodeidAndFieldcode(nodeid, rightfieldnamelist[i]));
            }
            Long[] right = rightlist.stream().map(p -> p.getFsequence()).sorted().toArray(Long[]::new);
            for (int i = 0; i < right.length; i++) {
                rightlist.get(i).setFsequence(right[i]);
                rightlist.get(i).setFfield(true);
            }
            templateRepository.save(rightlist);
        }
        return true;
	}

	public boolean updateSxQueue(String nodeid,String[] leftfieldnamelist,String[] rightfieldnamelist) {
		List<Tb_data_template_sx> rightlist=new ArrayList<Tb_data_template_sx>();
		List<Tb_data_template_sx>	leftlist=new ArrayList<Tb_data_template_sx>();
		String filedcode,table;
		int index=0;
		if(leftfieldnamelist!=null) {
			for (int i = 0; i < leftfieldnamelist.length; i++) {
				index=leftfieldnamelist[i].lastIndexOf("_tb_");
				filedcode=leftfieldnamelist[i].substring(0,index);
				table=leftfieldnamelist[i].substring(index+1);
				leftlist.add(sxTemplateRepository.findByNodeidAndFieldcodeAndFieldtable(nodeid, filedcode, table));
			}
			Long[] left=leftlist.stream().map(p->p.getFsequence()).toArray(Long[]::new);
			for(int i=0;i<left.length;i++){leftlist.get(i).setFsequence(left[i]);leftlist.get(i).setFfield(false);}
			sxTemplateRepository.save(leftlist);
		}
		if(rightfieldnamelist!=null) {
			for (int i = 0; i < rightfieldnamelist.length; i++) {
				index= rightfieldnamelist[i].lastIndexOf("_tb_");
				filedcode= rightfieldnamelist[i].substring(0,index);
				table= rightfieldnamelist[i].substring(index+1);
				rightlist.add(sxTemplateRepository.findByNodeidAndFieldcodeAndFieldtable(nodeid, filedcode, table));
			}
			Long[] right = rightlist.stream().map(p -> p.getFsequence()).sorted().toArray(Long[]::new);
			for (int i = 0; i < right.length; i++) {
				rightlist.get(i).setFsequence(right[i]);
				rightlist.get(i).setFfield(true);
			}
			sxTemplateRepository.save(rightlist);
		}
		return true;
	}

	public Page<Tb_data_template> findByNodeidAndffieldequels(int page, int limit, String condition, String operator,
														String content, String nodeid, Sort sort){
		if(nodeid==null||"".equals(nodeid)){
			return null;
		}
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort("fsequence") : sort);
		Specification<Tb_data_template> searchNodeidCondition = new Specification<Tb_data_template>() {
			@Override
			public Predicate toPredicate(Root<Tb_data_template> root, CriteriaQuery<?> criteriaQuery,
										 CriteriaBuilder criteriaBuilder) {
				Predicate p = criteriaBuilder.equal(root.get("nodeid"), nodeid);
				return criteriaBuilder.or(p);
			}
		};
		Specification<Tb_data_template> searchffieldCondition = new Specification<Tb_data_template>() {
			@Override
			public Predicate toPredicate(Root<Tb_data_template> root, CriteriaQuery<?> criteriaQuery,
										 CriteriaBuilder criteriaBuilder) {
				Predicate p = criteriaBuilder.equal(root.get("ffield"), 1);
				return criteriaBuilder.and(p);
			}
		};
		Specifications specifications = Specifications.where(searchNodeidCondition).and(searchffieldCondition);

		return templateRepository.findAll(specifications,pageRequest);
	}

	public Page<Tb_data_template_sx> findByNodeidAndffieldequelsSx(int page, int limit, String condition, String operator,
															  String content, String nodeid, Sort sort){
		if(nodeid==null||"".equals(nodeid)){
			return null;
		}
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort("fsequence") : sort);
		Specification<Tb_data_template> searchNodeidCondition = new Specification<Tb_data_template>() {
			@Override
			public Predicate toPredicate(Root<Tb_data_template> root, CriteriaQuery<?> criteriaQuery,
										 CriteriaBuilder criteriaBuilder) {
				Predicate p = criteriaBuilder.equal(root.get("nodeid"), nodeid);
				return criteriaBuilder.or(p);
			}
		};
		Specification<Tb_data_template> searchffieldCondition = new Specification<Tb_data_template>() {
			@Override
			public Predicate toPredicate(Root<Tb_data_template> root, CriteriaQuery<?> criteriaQuery,
										 CriteriaBuilder criteriaBuilder) {
				Predicate p = criteriaBuilder.equal(root.get("ffield"), 1);
				return criteriaBuilder.and(p);
			}
		};
		Specifications specifications = Specifications.where(searchNodeidCondition).and(searchffieldCondition);

		return sxTemplateRepository.findAll(specifications,pageRequest);
	}
	/**
	 * 从检索字段中排除指定字段后返回
	 *
	 * @param nodeid
	 *            节点id
	 * @param excludeValues
	 *            需排除的检索字段
	 * @return
	 */
	public List<ExtSearchData> excludedQueryConditionTemplate(String nodeid, String[] excludeValues) {
		List<Tb_data_template> list = templateRepository.findByNodeid(nodeid);
		List<Tb_data_template> tempList = new ArrayList<>();
		for (Tb_data_template tb_data_template : list) {
			Tb_data_template tb_data_template_temp = new Tb_data_template();
			BeanUtils.copyProperties(tb_data_template, tb_data_template_temp);
			tempList.add(tb_data_template_temp);
		}
		for (String fieldcodeStr : excludeValues) {
			for (Tb_data_template data_template : tempList) {
				if (fieldcodeStr.equals(data_template.getFieldcode())) {
					data_template.setQfield(false);// 设为非检索字段
					break;
				}
			}
		}
		List<ExtSearchData> returnList = new ArrayList<>();
		for (Tb_data_template data_template : tempList) {
			ExtSearchData extSearchData = new ExtSearchData();
			if (data_template.getQfield()) {
				extSearchData.setItem(data_template.getFieldcode());
				extSearchData.setName(data_template.getFieldname());
				returnList.add(extSearchData);// 过滤数据
			}
		}
		return returnList;
	}

	public List<Tb_data_template> formTemplate(String nodeid) {
		return templateRepository.findFormByNode(nodeid);
	}

	public List<Tb_data_template_sx> sxformTemplate(String nodeid,String tableType) {
		return sxTemplateRepository.findByNodeidAndFieldtableIn(nodeid,getTableNameByTableType(tableType));
	}

	public List<Tb_data_template> fromTemplateOrderbyFs(String noide){
		return templateRepository.findByNodeOrderByFs(noide);
	}

	public List<Tb_data_template> formTemplateEdit(String nodeid) {//获取预归档编辑字段
		List<Tb_data_template> templates=new ArrayList<>();
		templates=templateRepository.findEditFormByNode(nodeid);//预归档编辑字段
		if(templates.size()<1){//没有设置编辑字段的话就获取全部的表单模板字段
			templates=templateRepository.findFormByNode(nodeid);
		}else{//获取预归档编辑字段
			List<Tb_codeset> codesets=codesetRepository.findEditCodeset(nodeid);//档编辑字段含档号字段
			if(codesets.size()>0){//预归档编辑字段含档号字段，获取档号设置字段和预归档编辑字段和archivecode字段的并集
				templates=templateRepository.findEditFormAndCodesetByNode(nodeid);
			}
		}
		return templates;
	}

	public String firstFormField(String nodeid) {
		List<Tb_data_template> list = templateRepository.findFormByNode(nodeid);
		if (list.size() > 0) {
			return list.get(0).getFieldcode();
		}
		return null;
	}

	public List<Tb_field> getFieldInfo(String nodeid, String targetNodeid, List<String> code, List<String> targetCode) {
		List<Tb_field> fields = new ArrayList<>();
		List<String> codeInfo = new ArrayList<>();
		codeInfo.addAll(code);

		List<String> targetCodeInfo = new ArrayList<>();
		targetCodeInfo.addAll(targetCode);

		codeInfo.retainAll(targetCode);
		// 先保存交集的字段
		for (int i = 0; i < codeInfo.size(); i++) {
			Tb_field field = new Tb_field();
			field.setFieldName(templateRepository.findFieldNameByFieldcodeAndNodeid(codeInfo.get(i), nodeid));
			field.setTargetFieldName(
					templateRepository.findFieldNameByFieldcodeAndNodeid(codeInfo.get(i), targetNodeid));
			field.setFieldCode(codeInfo.get(i));
			field.setTargetFieldCode(codeInfo.get(i));
			fields.add(field);
		}
		code.removeAll(codeInfo);
		for (int i = 0; i < code.size(); i++) {
			Tb_field field = new Tb_field();
			field.setFieldName(templateRepository.findFieldNameByFieldcodeAndNodeid(code.get(i), nodeid));
			field.setFieldCode(code.get(i));
			fields.add(field);
		}
		targetCode.removeAll(codeInfo);
		for (int j = 0; j < targetCode.size(); j++) {
			Tb_field field = new Tb_field();
			field.setTargetFieldName(
					templateRepository.findFieldNameByFieldcodeAndNodeid(targetCode.get(j), targetNodeid));
			field.setTargetFieldCode(targetCode.get(j));
			fields.add(field);
		}
		return fields;
	}

	public List<Tb_data_template> gridTemplate(String nodeid, String type, String info) {
		List<Tb_data_template> templates = new ArrayList<Tb_data_template>();
		List<Tb_data_template> templateInfo = templateRepository.findGridByNode(nodeid);
		// 数据开放中,所有表单信息都要有'开放状态'属性
		if (type != null && type.equals("数据开放")) {
			// 如果原表单中有'开放状态'属性,那么直接返回
			for (int i = 0; i < templateInfo.size(); i++) {
				Tb_data_template template = templateInfo.get(i);
				if (template.getFieldcode().equals("flagopen")) {
					return templateInfo;
				}
			}
			// 没有'开放状态'则需要手动添加信息,且在表格中显示在序号后
			Tb_data_template template = new Tb_data_template();
			template.setFdefault("");
			template.setFfield(false);
			template.setFieldcode("flagopen");
			template.setFieldname("开放状态");
			template.setFieldtable("tb_entry_index");
			template.setFreadonly(false);
			template.setFrequired(false);
			template.setFsequence(new Long((long) 0));
			template.setFtip("");
			template.setFtype("String");
			template.setFvalidate("");
			template.setGfield(true);
			template.setGhidden(false);
			template.setGsequence(new Long((long) 0));
			template.setGwidth(new Long((long) 150));
			template.setInactiveformfield(false);
			template.setNodeid(nodeid);
			template.setQfield(false);
			template.setQsequence(new Long((long) 0));
			template.setTemplateid("");
			templates.add(template);
			if (info != null && info.equals("详细内容")) {
				Tb_data_template nodefullname = new Tb_data_template();
				nodefullname.setFdefault("");
				nodefullname.setFfield(false);
				nodefullname.setFieldcode("nodefullname");
				nodefullname.setFieldname("数据节点全名");
				nodefullname.setFieldtable("tb_entry_index");
				nodefullname.setFreadonly(false);
				nodefullname.setFrequired(false);
				nodefullname.setFsequence(new Long((long) 0));
				nodefullname.setFtip("");
				nodefullname.setFtype("String");
				nodefullname.setFvalidate("");
				nodefullname.setGfield(true);
				nodefullname.setGhidden(false);
				nodefullname.setGsequence(new Long((long) 0));
				nodefullname.setGwidth(new Long((long) 150));
				nodefullname.setInactiveformfield(false);
				nodefullname.setNodeid(nodeid);
				nodefullname.setQfield(false);
				nodefullname.setQsequence(new Long((long) 0));
				nodefullname.setTemplateid("");
				templates.add(nodefullname);
			}
		}
		templates.addAll(templateInfo);
		return templates;
	}

	//声像模板
	public List<Tb_data_template_sx> gridSxTemplate(String nodeid, String type, String info,String table) {
		List<Tb_data_template_sx> templates = new ArrayList<Tb_data_template_sx>();
		List<Tb_data_template_sx> templateInfo = sxTemplateRepository.findGridByNode(nodeid,getTableNameByTableType(table));
		// 数据开放中,所有表单信息都要有'开放状态'属性
		if (type != null && type.equals("数据开放")) {
			// 如果原表单中有'开放状态'属性,那么直接返回
			for (int i = 0; i < templateInfo.size(); i++) {
				Tb_data_template_sx template = templateInfo.get(i);
				if (template.getFieldcode().equals("flagopen")) {
					return templateInfo;
				}
			}
			// 没有'开放状态'则需要手动添加信息,且在表格中显示在序号后
			Tb_data_template_sx template = new Tb_data_template_sx();
			template.setFdefault("");
			template.setFfield(false);
			template.setFieldcode("flagopen");
			template.setFieldname("开放状态");
			template.setFieldtable("tb_entry_index");
			template.setFreadonly(false);
			template.setFrequired(false);
			template.setFsequence(new Long((long) 0));
			template.setFtip("");
			template.setFtype("String");
			template.setFvalidate("");
			template.setGfield(true);
			template.setGhidden(false);
			template.setGsequence(new Long((long) 0));
			template.setGwidth(new Long((long) 150));
			template.setInactiveformfield(false);
			template.setNodeid(nodeid);
			template.setQfield(false);
			template.setQsequence(new Long((long) 0));
			template.setTemplateid("");
			templates.add(template);
			if (info != null && info.equals("详细内容")) {
				Tb_data_template_sx nodefullname = new Tb_data_template_sx();
				nodefullname.setFdefault("");
				nodefullname.setFfield(false);
				nodefullname.setFieldcode("nodefullname");
				nodefullname.setFieldname("数据节点全名");
				nodefullname.setFieldtable("tb_entry_index");
				nodefullname.setFreadonly(false);
				nodefullname.setFrequired(false);
				nodefullname.setFsequence(new Long((long) 0));
				nodefullname.setFtip("");
				nodefullname.setFtype("String");
				nodefullname.setFvalidate("");
				nodefullname.setGfield(true);
				nodefullname.setGhidden(false);
				nodefullname.setGsequence(new Long((long) 0));
				nodefullname.setGwidth(new Long((long) 150));
				nodefullname.setInactiveformfield(false);
				nodefullname.setNodeid(nodeid);
				nodefullname.setQfield(false);
				nodefullname.setQsequence(new Long((long) 0));
				nodefullname.setTemplateid("");
				templates.add(nodefullname);
			}
		}
		templates.addAll(templateInfo);
		return templates;
	}

	public List<Tb_data_template> findByNodeidOrderByGsequence(String nodeid) {
		return templateRepository.findByNodeidOrderByGsequence(nodeid);
	}

	public ExtMsg getCodesettingTemplate(String nodeid) {
		List<Tb_codeset> codesetList = codesetRepository.findByDatanodeidOrderByOrdernum(nodeid);// 获取相应节点的档号设置字段对象
		List<Tb_data_template> templateList = templateRepository.findByNodeidAndFfieldOrderByFsequence(nodeid, true);// 获取相应节点的所有模板对象
		List<Tb_data_template> resultTemplateList = new ArrayList<Tb_data_template>();// 存放筛选得到的模板对象（档号设置字段）
		List<String> codesetStrList = new ArrayList<>();
		// 遍历档号构成集合
		for (int i = 0; i < codesetList.size(); i++) {
			if (i == codesetList.size() - 1) {
				String lastFieldcode = codesetList.get(i).getFieldcode();
				String lastFieldcodeType = templateRepository.findFtypeByFieldcodeAndNodeid(lastFieldcode, nodeid);
				if (!"calculation".equals(lastFieldcodeType)) {
					return new ExtMsg(false, "档号设置最后一个字段必须为统计型,请检查该字段模板设置是否正确", null);
				}
			}
			codesetStrList.add(codesetList.get(i).getFieldcode());// 获取档号设置的fieldcode，存入list
		}
		// 遍历模板构成集合，判断档号构成map中是否含有模板字段
		for (int i = 0; i < templateList.size(); i++) {
			if (codesetStrList.contains(templateList.get(i).getFieldcode())) {
				resultTemplateList.add(templateList.get(i));// 若档号list中有相应字段，则添加至resultTemplateList,作为最终结果返回
			}
		}
		return new ExtMsg(true, "", resultTemplateList);// 返回只包含档号设置字段的模板对象
	}

    public ExtMsg copyTemplate(String sourceid, String withCode,String[] targetids) {
        try {
            List<Tb_data_template> saveTemplateList = new ArrayList<>();
            // 删除子节点的数据
            templateRepository.delete(templateRepository.findByNodeidIn(targetids));
            List<Tb_data_template> data_templateList_return = findByNodeid(sourceid);
            for(int i=0;i<targetids.length;i++){
                //复制模板
                for (Tb_data_template data_template_return : data_templateList_return) {
                    Tb_data_template data_template = new Tb_data_template();
                    BeanUtils.copyProperties(data_template_return, data_template);
                    data_template.setTemplateid(null);
                    data_template.setNodeid(targetids[i]);
                    saveTemplateList.add(data_template);
                }
            }
            templateRepository.save(saveTemplateList);
            if ("true".equals(withCode)) {
                List<Tb_codeset> codesetList = new ArrayList<>();
                //删除子节点数据
                codesetRepository.delete(codesetRepository.findByDatanodeidIn(targetids));
                List<Tb_codeset> codeset_return_list = codesettingService.findCodesetByDatanodeid(sourceid);
                for(int i=0;i<targetids.length;i++){
                    for (Tb_codeset codeset_return : codeset_return_list) {
                        Tb_codeset codeset = new Tb_codeset();
                        BeanUtils.copyProperties(codeset_return, codeset);
                        codeset.setCodeid(null);
                        codeset.setDatanodeid(targetids[i]);
                        codesetList.add(codeset);
                    }
                }
                codesettingService.SaveCodeset(codesetList);
            }
            return new ExtMsg(true, "模板复制成功", null);
        }catch (Exception e) {
            e.printStackTrace();
            return new ExtMsg(false,"模板复制失败", null);
        }
    }
    public boolean updateTemplistByType(Tb_data_template source,Tb_data_template target,String xtype){
        if(StringUtils.isEmpty(xtype)||source==null||target==null){
            return false;
        }else{
            source.setGwidth(target.getGwidth());
            source.setGsequence(target.getGsequence());
            source.setGhidden(target.getGhidden());
            if("档案系统".equals(xtype)){
                templateRepository.save(source);
            }else{
                Tb_data_template_sx tb_data_template_sx=new Tb_data_template_sx();
                BeanUtils.copyProperties(source,tb_data_template_sx);
                sxTemplateRepository.save(tb_data_template_sx);
            }
        }
        return true;
    }
	public boolean updateTemplateByType(Tb_data_template source,Tb_data_template target,String xtype){
		if(StringUtils.isEmpty(xtype)||source==null||target==null){
			return false;
		}else{
			source.setFdefault(target.getFdefault());
			source.setFieldlength(target.getFieldlength());
			source.setFvalidate(target.getFvalidate());
			source.setFtip(target.getFtip());
			source.setFsequence(target.getFsequence());
			source.setFrequired(target.getFrequired());
			source.setFenumsedit(target.getFenumsedit());
			source.setFreadonly(target.getFreadonly());
			source.setInactiveformfield(target.getInactiveformfield());
			source.setArchivecodeedit(target.getArchivecodeedit());
			source.setFrows(target.getFrows());
			if("enum".equals(target.getFenums())){
				source.setFtype(target.getFtype());
				source.setFenums(target.getFenums());
			}else{
				source.setFtype(target.getFtype());
			}
			if("档案系统".equals(xtype)){
				templateRepository.save(source);
			}else{
				Tb_data_template_sx tb_data_template_sx=new Tb_data_template_sx();
				BeanUtils.copyProperties(source,tb_data_template_sx);
				sxTemplateRepository.save(tb_data_template_sx);
			}
		}
		return true;
	}
    public ExtMsg copySxTemplate(String sourceid, String withCode,String[] targetids) {
        try {
            List<Tb_data_template_sx> saveTemplateList = new ArrayList<>();
            // 删除子节点的数据
            sxTemplateRepository.delete(sxTemplateRepository.findByNodeidIn(targetids));
            List<Tb_data_template_sx> data_templateList_return = findSxByNodeid(sourceid);
            for(int i=0;i<targetids.length;i++){
                //复制模板
                for (Tb_data_template_sx data_template_return : data_templateList_return) {
                    Tb_data_template_sx data_template = new Tb_data_template_sx();
                    BeanUtils.copyProperties(data_template_return, data_template);
                    data_template.setTemplateid(null);
                    data_template.setNodeid(targetids[i]);
                    saveTemplateList.add(data_template);
                }
            }
            sxTemplateRepository.save(saveTemplateList);
            if ("true".equals(withCode)) {
                List<Tb_codeset_sx> codesetList = new ArrayList<>();
                //删除子节点数据
                sxCodesetRepository.delete(sxCodesetRepository.findByDatanodeidIn(targetids));
                List<Tb_codeset_sx> codeset_return_list = codesettingService.findSxCodesetByDatanodeid(sourceid);
                for(int i=0;i<targetids.length;i++){
                    for (Tb_codeset_sx codeset_return : codeset_return_list) {
                        Tb_codeset_sx codeset = new Tb_codeset_sx();
                        BeanUtils.copyProperties(codeset_return, codeset);
                        codeset.setCodeid(null);
                        codeset.setDatanodeid(targetids[i]);
                        codesetList.add(codeset);
                    }
                }
                codesettingService.SaveSxCodeset(codesetList);
            }
            return new ExtMsg(true, "模板复制成功", null);
        }catch (Exception e) {
            e.printStackTrace();
            return new ExtMsg(false,"模板复制失败", null);
        }
    }

	public List<Tb_data_template> findByNodeid(String nodeid) {
		return templateRepository.findByNodeidOrderByFsequence(nodeid);
	}

	public List<Tb_data_template_sx> findSxByNodeid(String nodeid) {
		return sxTemplateRepository.findByNodeidOrderByFsequence(nodeid);
	}

	public List<Tb_data_template> findPartialTemplateByNodeidAndExclude(String datanodeid, String[] excludes) {
		return templateRepository.findByNodeidAndFieldtableAndFieldcodeNotIn(datanodeid, "tb_entry_index", excludes);
	}

	public List<Tb_data_template_sx> findPartialSxTemplateByNodeidAndExclude(String datanodeid, String[] excludes) {
		return sxTemplateRepository.findByNodeidAndFieldtableAndFieldcodeNotIn(datanodeid, "tb_entry_index", excludes);
	}

	public void deleteTemplateByNodeid(String nodeid) {
		templateRepository.deleteByNodeid(nodeid);
	}

	public void deleteSxTemplateByNodeid(String nodeid,String table) {
		sxTemplateRepository.deleteByNodeidAndFieldtableIn(nodeid,getTableNameByTableType(table));
	}

	public Tb_data_template UpdateTemplate(Tb_data_template data_template, String xtType) {
		Tb_data_template data_template_return = templateRepository.findByTemplateid(data_template.getTemplateid());
		if(!data_template_return.getFieldname().equals(data_template.getFieldname())){//修改了字段名
			//判断重复
			int count = templateRepository.findCount(data_template.getNodeid(), data_template.getFieldname(), data_template.getTemplateid());
			if (count > 0) {//存在重复字段
				return null;
			}
		}
		//判断设置的元数据字段是否有重复
		if(null!=data_template.getMetadataid()&&!"".equals(data_template.getMetadataid())){
			int count = templateRepository.findCountByMetadataidAndNodeid(data_template.getMetadataid(),data_template.getNodeid());
			if (count > 1) {//存在重复设置的元数据字段
				return null;
			}
		}
		BeanUtils.copyProperties(data_template, data_template_return);
		if("12345678910".equals(data_template.getNodeid())){//库房模板直接更新返回
			return data_template_return;
		}
//		String nodeid = data_template_return.getNodeid();
//		Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
//		if (node.getLuckstate() != null && "1".equals(node.getLuckstate())) {
//			synctemplate(nodeid, "allChild", "true", xtType,"");
//		}


		//更新模板描述关联表tb_template_desc
		String fieldcode=data_template_return.getFieldcode();
		String fieldname=data_template_return.getFieldname();
		String fc_pattern = "^[f][0-5][0-9]";//副表字段
		if(fieldcode.matches(fc_pattern)){//是副表字段
			List<Tb_template_desc> templateDescs= templateDescRepository.findByFieldcode(fieldcode);
			if(templateDescs.size()>0){//有字段存在时就修改
				Tb_template_desc templateDesc=templateDescs.get(0);
				String descs=templateDesc.getDescs();
				if(!descs.contains(fieldname)&&!"".equals(fieldname)){//还不存在新的字段名描述就增加
					descs=descs+","+fieldname;
					templateDesc.setDescs(descs);
					templateDescRepository.save(templateDesc);
				}
				updateTemplateDesc(fieldcode);//更新指定字段
			}else{//还没有就增加
				updateTemplateDesc();//刷新模板描述关联表的副表字段
			}
		}else{//主表字段
			updateTemplateDesc(fieldcode);//更新指定字段
		}

		return data_template_return;
	}

	public Tb_data_template_sx UpdateSxTemplate(Tb_data_template data_template, String xtType,String table) {
		Tb_data_template_sx data_template_return = sxTemplateRepository.findByTemplateid(data_template.getTemplateid());
		if(!data_template_return.getFieldname().equals(data_template.getFieldname())){//修改了字段名
			//判断重复
			int count = sxTemplateRepository.findCount(data_template.getNodeid(), data_template.getFieldname(), data_template.getTemplateid());
			if (count > 0) {//存在重复字段
				return null;
			}
		}
		//判断设置的元数据字段是否有重复
		if(null!=data_template.getMetadataid()&&!"".equals(data_template.getMetadataid())){
			int count = sxTemplateRepository.findCountByMetadataidAndNodeid(data_template.getMetadataid(),data_template.getNodeid());
			if (count > 0) {//存在重复设置的元数据字段
				return null;
			}
		}
		BeanUtils.copyProperties(data_template, data_template_return);
		sxTemplateRepository.save(data_template_return);
		if("12345678910".equals(data_template.getNodeid())){//库房模板直接更新返回
			return data_template_return;
		}
//		String nodeid = data_template_return.getNodeid();
//		Tb_data_node_sx node = secondaryDataNodeRepository.findByNodeid(nodeid);
//		if (node.getLuckstate() != null && "1".equals(node.getLuckstate())) {
//			syncSxtemplate(nodeid, "allChild", "true", xtType,table);
//		}

		//更新模板描述关联表tb_template_desc
		String fieldcode=data_template_return.getFieldcode();
		String fieldname=data_template_return.getFieldname();
		String fc_pattern = "^[f][0-5][0-9]";//副表字段
		if(fieldcode.matches(fc_pattern)){//是副表字段
			List<Tb_template_desc_sx> templateDescs= sxTemplateDescRepository.findByFieldcode(fieldcode);
			if(templateDescs.size()>0){//有字段存在时就修改
				Tb_template_desc_sx templateDesc=templateDescs.get(0);
				String descs=templateDesc.getDescs();
				if(!descs.contains(fieldname)&&!"".equals(fieldname)){//还不存在新的字段名描述就增加
					descs=descs+","+fieldname;
					templateDesc.setDescs(descs);
					sxTemplateDescRepository.save(templateDesc);
				}
				updateTemplateSxDesc(fieldcode);//更新指定字段
			}else{//还没有就增加
				updateTemplateSxDesc();//刷新模板描述关联表的副表字段
			}
		}else{//主表字段
			updateTemplateSxDesc(fieldcode);//更新指定字段
		}

		return data_template_return;
	}

	public Page<Object> findBySearch(int page, int limit, String condition, String operator, String content,
			String nodeID, Sort sort, String xtType,String tableType) {
		PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort("fsequence") : sort);
		if("声像系统".equals(xtType)){
			Specification<Tb_data_template_sx> searchid = getSxSearchNodeidCondition(nodeID);
			Specification<Tb_data_template_sx> s2 = getFieldtableIn(getTableNameByNodeTypeAll(tableType));
			Specifications specifications = Specifications.where(searchid);
			if (content != null) {
				specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
			}
//			Page<Tb_data_template_sx> dataTemplates =sxTemplateRepository.findAll(specifications.and(s2), pageRequest);
//			List<Tb_data_template> list = new ArrayList<>();
//			for (Tb_data_template_sx dataTemplate:dataTemplates){
//				Tb_data_template copyDataTemplate = new Tb_data_template();
//				String fdlength = getFieldlengthByTable(dataTemplate.getFieldtable(), dataTemplate.getFieldcode(), xtType);
//				dataTemplate.setFieldlength(fdlength);
//				BeanUtils.copyProperties(dataTemplate, copyDataTemplate);
//				list.add(copyDataTemplate);
//			}
//			return new PageImpl<>(list, pageRequest, dataTemplates.getTotalElements());
            return sxTemplateRepository.findAll(specifications.and(s2), pageRequest);

		}else{
			Specification<Tb_data_template> searchid = getSearchNodeidCondition(nodeID);
			Specifications specifications = Specifications.where(searchid);
			if (content != null) {
				specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
			}
//			Page<Tb_data_template> dataTemplates = templateRepository.findAll(specifications, pageRequest);
//			for (Tb_data_template dataTemplate:dataTemplates){
//				String fdlength = getFieldlengthByTable(dataTemplate.getFieldtable(), dataTemplate.getFieldcode(), xtType);
//				dataTemplate.setFieldlength(fdlength);
//			}
            return templateRepository.findAll(specifications, pageRequest);
		}
	}

	public List<Tb_data_template_sx> findByNodeidOrderByFsequence(String nodeid, String nodeType) {
		if (null == nodeType || "".equals(nodeType)) {
			return sxTemplateRepository.findAllByNodeidAndFieldtableInOrderByFsequence(nodeid, new String[]{"tb_entry_index", "tb_entry_detail"});
		} else {
			String tablename = "";
			if (nodeType.equals("group") || nodeType.equals("")) {
				tablename = "tb_docgroupself";
			} else {
				tablename = "tb_dossierself";
			}
			return sxTemplateRepository.findAllByNodeidAndFieldtableInOrderByFsequence(nodeid, new String[]{tablename});
		}
	}

	public static Specification<Tb_data_template_sx> getFieldtableIn(String[] fieldtable) {
		Specification<Tb_data_template_sx> specification = new Specification<Tb_data_template_sx>() {
			@Override
			public Predicate toPredicate(Root<Tb_data_template_sx> root, CriteriaQuery<?> criteriaQuery,
										 CriteriaBuilder criteriaBuilder) {
				CriteriaBuilder.In in = criteriaBuilder.in(root.get("fieldtable"));
				for (String str : fieldtable) {
					in.value(str);
				}
				return criteriaBuilder.or(in);
			}
		};
		return specification;
	}

	public String[] getTableNameByNodeTypeAll(String tableType) {
		if ("".equals(tableType) || null == tableType) {
			return new String[]{"tb_entry_index", "tb_entry_detail"};
		} else {
			return new String[]{tableType};
		}
	}

	public void updateQuence(String nodeid, String fieldCode, String rightFieldCode, String type) {
		if (type.equals("list")) {
			// 设置列表字段
			if (fieldCode != null && !fieldCode.equals("")) {
				String[] fieldInfo = fieldCode.split(",");
				for (int i = 1; i < fieldInfo.length + 1; i++) {
					templateRepository.updateFsquenceAndGfield(true, false, nodeid, fieldInfo[i-1]);
				}
			}
			if (rightFieldCode != null && !rightFieldCode.equals("")) {
				String[] fieldInfo = rightFieldCode.split(",");
				for (int i = 1; i < fieldInfo.length + 1; i++) {
					templateRepository.updateGequence(true, i, nodeid, fieldInfo[i-1]);
				}
			}
		} else {
			// 设置检索字段
			if (fieldCode != null && !fieldCode.equals("")) {
				String[] fieldInfo = fieldCode.split(",");
				for (int i = 1; i < fieldInfo.length + 1; i++) {
					templateRepository.updateFsquenceAndQfield(true, false, nodeid, fieldInfo[i-1]);
				}
			}
			if (rightFieldCode != null && !rightFieldCode.equals("")) {
				String[] fieldInfo = rightFieldCode.split(",");
				for (int i = 1; i < fieldInfo.length + 1; i++) {
					// 修改该字段为检索字段&更新检索字段排序
					templateRepository.updateQequence(true, i, nodeid, fieldInfo[i-1]);
				}
			}
		}
	}

	public void updateSxQuence(String nodeid, String fieldCode, String rightFieldCode, String type) {
		String filedcode,table;
		int index=0;
		if (type.equals("list")) {
			// 设置列表字段
			if (fieldCode != null && !fieldCode.equals("")) {
				String[] fieldInfo = fieldCode.split(",");
				for (int i = 1; i < fieldInfo.length + 1; i++) {
					index=fieldInfo[i-1].lastIndexOf("_tb_");
					filedcode=fieldInfo[i-1].substring(0,index);
					table=fieldInfo[i-1].substring(index+1);
					sxTemplateRepository.updateFsquenceAndGfield(true, false, nodeid,  filedcode, table);
				}
			}
			if (rightFieldCode != null && !rightFieldCode.equals("")) {
				String[] fieldInfo = rightFieldCode.split(",");
				for (int i = 1; i < fieldInfo.length + 1; i++) {
					index=fieldInfo[i-1].lastIndexOf("_tb_");
					filedcode=fieldInfo[i-1].substring(0,index);
					table=fieldInfo[i-1].substring(index+1);
					sxTemplateRepository.updateGequence(true, i, nodeid, filedcode, table);
				}
			}
		} else {
			// 设置检索字段
			if (fieldCode != null && !fieldCode.equals("")) {
				String[] fieldInfo = fieldCode.split(",");
				for (int i = 1; i < fieldInfo.length + 1; i++) {
					index=fieldInfo[i-1].lastIndexOf("_tb_");
					filedcode=fieldInfo[i-1].substring(0,index);
					table=fieldInfo[i-1].substring(index+1);
					sxTemplateRepository.updateFsquenceAndQfield(true, false, nodeid, filedcode, table);
				}
			}
			if (rightFieldCode != null && !rightFieldCode.equals("")) {
				String[] fieldInfo = rightFieldCode.split(",");
				for (int i = 1; i < fieldInfo.length + 1; i++) {
					index=fieldInfo[i-1].lastIndexOf("_tb_");
					filedcode=fieldInfo[i-1].substring(0,index);
					table=fieldInfo[i-1].substring(index+1);
					// 修改该字段为检索字段&更新检索字段排序
					sxTemplateRepository.updateQequence(true, i, nodeid, filedcode, table);
				}
			}
		}
	}

	public List getIndexField(List<Tb_data_template> templates) {
		List<HashMap> returnList = new LinkedList<>();
		for (int i = 0; i < templates.size(); i++) {
			Tb_data_template template = templates.get(i);
			HashMap map = new HashMap();
			map.put("fieldcode", template.getFieldcode());
			map.put("fieldname", template.getFieldcode()+"_"+template.getFieldname());
			returnList.add(map);
		}
		return returnList;
	}

	public List getSxIndexField(List<Tb_data_template_sx> templates) {
		List<HashMap> returnList = new LinkedList<>();
		for (int i = 0; i < templates.size(); i++) {
			Tb_data_template_sx template = templates.get(i);
			HashMap map = new HashMap();
			map.put("fieldcode", template.getFieldcode()+"_"+template.getFieldtable());
			map.put("fieldname", template.getFieldcode()+"_"+template.getFieldname()+"-"+template.getFieldtable());
			returnList.add(map);
		}
		return returnList;
	}

	public List getAllByField(String nodeid, String xtType) {
		// 分成两块：一块常用字段，一块附加字段；
		String[] appendFieldNameArray = new String[50];
		for (int i = 1; i <= appendFieldNameArray.length; i++) {
			if (i < 10) {
				appendFieldNameArray[i - 1] = "f0" + i + "_字段描述";
			} else {
				appendFieldNameArray[i - 1] = "f" + i + "_字段描述";
			}
		}

		String constantFieldNameStr = "title_题名,filenumber_文件编号,archivecode_档号,funds_全宗号,catalog_目录号,filecode_案卷号,innerfile_卷内顺序号,"
				+ "filingyear_归档年度,entryretention_保管期限,organ_机构/问题,recordcode_件号,entrysecurity_密级,pages_页数,pageno_页号,filedate_文件日期,"
				+ "responsible_责任者,serial_文件流水号,flagopen_开放状态,entrystorage_存储位置,descriptiondate_著录时间,descriptionuser_著录人,fscount_份数,"
				+ "kccount_库存份数,keyword_主题词,"
				+ "sparefield1_备用字段1,sparefield2_备用字段2,sparefield3_备用字段3,sparefield4_备用字段4,sparefield5_备用字段5";

		if("声像系统".equals(xtType)){
			constantFieldNameStr = "title_题名,filenumber_文件编号,archivecode_档号,funds_全宗号,catalog_目录号,filecode_案卷号,innerfile_卷内顺序号,"
					+ "filingyear_归档年度,entryretention_保管期限,organ_机构/问题,recordcode_件号,entrysecurity_划控级别,pages_页数,pageno_页号,filedate_拍录日期,"
					+ "responsible_责任者,serial_文件流水号,flagopen_开放状态,entrystorage_存储位置,descriptiondate_著录时间,descriptionuser_著录人,fscount_份数,"
					+ "kccount_库存份数,keyword_主题词,author_拍录作者,address_拍录地点,theme_内容主题,source_来源,isprint_是否冲印";
			String[] constantFieldNameArray = constantFieldNameStr.split(",");
			List<Tb_data_template_sx> selectedFieldList = sxTemplateRepository.findAllByNodeidAndFieldtableInOrderByFsequence(nodeid,new String []{"tb_entry_index","tb_entry_detail"});
			List<HashMap> list = new ArrayList<HashMap>();
			List<HashMap> constantlist = filterSxSelected(constantFieldNameArray, selectedFieldList, list);
			List<HashMap> appendlist = filterSxSelected(appendFieldNameArray, selectedFieldList, constantlist);
			return appendlist;
		}else{

			String[] constantFieldNameArray = constantFieldNameStr.split(",");
			List<Tb_data_template> selectedFieldList = templateRepository.findByNodeid(nodeid);
			List<HashMap> list = new ArrayList<HashMap>();
			List<HashMap> constantlist = filterSelected(constantFieldNameArray, selectedFieldList, list);
			List<HashMap> appendlist = filterSelected(appendFieldNameArray, selectedFieldList, constantlist);
			return appendlist;
		}
	}

	//------------组字段设置----------------//
	public List getAllField(String nodeid,String nodeType) {
		String constantFieldNameStr = "title_事项描述,startdate_拍摄日期,enddate_结束日期,docnum_编号,atype_档案类型,startcode_文件起始号,endcode_文件终止号," +
				"lastmodifyauthor_最后修改人,lastmodifydate_最后修改时间,docstatus_状态,createdate_著录日期,docgddate_归档时间," +
				"docyjdate_移交时间,docjgdate_进馆时间,doccreateauthor_著录者,sortname_分类名,remark_备注,unitname_会议名称,filenum_文件数量," +
				"filecreater_拍摄者,filelocation_拍摄地点,qzcode_全宗号,groupinfo_主要任务极其说明," +
				"contenttop_内容主题,doclevel_划控级别,docsource_来源,isrinsepic_是否冲印,cdseq_载体编号,bgqx_保管期限," +
				"docgdauthor_参会领导（人员）,docscop_照片组范围号,sortcode_分类号,docgroupkey_组档号," +
				"rinsefilenum_纸质数量,groupattachmentcount_组附件数量,groupgdyear_归档年度,groupcode_组号,keyword_关键字";

		//案卷
		String constantFieldNameStr2 = "title_案卷题名,docgroupnum_案卷数量,dossierkey_档号(内部),qzcode_全宗号,unitname_单位," +
				"sortname_分类名称,bgqx_保管期限,atype_类型,dossiernum_案卷顺序号,filenum_文件数量,year_年度,dossierstatus_状态," +
				"remark_备注,dossiercreatedate_创建时间,dossiercreateauthor_创建者,dossiergddate_归档时间,dossieryjdate_移交时间," +
				"dossierjgdate_进馆时间,contentsnum_目录号,dosmodifyauthor_修改者,dosmodifydate_修改日期,dosgdauthor_归档者," +
				"docrinsefilenum_纸质文件数量,factdossierkey_案卷档号,directorynum_目录顺序序号,sortcode_分类号,docyears_案卷年度," +
				"olddossierkey_旧档号,groupcode_组号";
		String sql = "";
		String tablename = "";
		String[] constantFieldNameArray = null;
		if ("group".equals(nodeType)) {
			tablename = "tb_docgroupself";
			sql = new DBCompatible().getTableAllField(tablename);
			constantFieldNameArray = constantFieldNameStr.split(",");
		} else {
			tablename = "tb_dossierself";
			sql = new DBCompatible().getTableAllField(tablename);
			constantFieldNameArray = constantFieldNameStr2.split(",");
		}
		List<HashMap> list = new ArrayList<HashMap>();
		List<Tb_data_template_sx> data_template_sxList=sxTemplateRepository.findAllByNodeidAndFieldtableInOrderByFsequence(nodeid,new String[]{tablename});
		List<HashMap> constantlist = filterSxSelected(constantFieldNameArray,data_template_sxList , list);
		return constantlist;
	}

	public List<HashMap> filterSelected(String[] nameArray, List<Tb_data_template> fieldlist, List<HashMap> list) {
		String fieldcode = "";
		for (String fieldName : nameArray) {
			HashMap map = new HashMap();
			fieldcode = fieldName.substring(0, fieldName.indexOf("_"));
			map.put("fieldcode", fieldcode);
			boolean flag = false;
			for (Tb_data_template data_template : fieldlist) {// 遍历查找，判断是否为已选字段
				if (data_template.getFieldcode().equals(fieldcode)) {
					map.put("fieldname", fieldcode + "_" + data_template.getFieldname());// 是则使用已选字段名
					flag = true;// 找到
					break;
				}
			}
			if (!flag) {// 没找到
				map.put("fieldname", fieldName);// 否则使用初始字段名
			}
			list.add(map);
		}
		return list;
	}

	//拼接声像模板字段
	public List<HashMap> filterSxSelected(String[] nameArray, List<Tb_data_template_sx> fieldlist, List<HashMap> list) {
		String fieldcode = "";
		for (String fieldName : nameArray) {
			HashMap map = new HashMap();
			fieldcode = fieldName.substring(0, fieldName.indexOf("_"));
			map.put("fieldcode", fieldcode);
			boolean flag = false;
			for (Tb_data_template_sx data_template : fieldlist) {// 遍历查找，判断是否为已选字段
				if (data_template.getFieldcode().equals(fieldcode)) {
					map.put("fieldname", fieldcode + "_" + data_template.getFieldname());// 是则使用已选字段名
					flag = true;// 找到
					break;
				}
			}
			if (!flag) {// 没找到
				map.put("fieldname", fieldName);// 否则使用初始字段名
			}
			list.add(map);
		}
		return list;
	}

	public void submitfields(String nodeid, String[] fieldnames, String xtType,String table) {
		List<Tb_data_template> data_templateList = templateRepository.findByNodeid(nodeid);
		boolean flag = false;
		for (Tb_data_template data_template : data_templateList) {
			for (String fieldname : fieldnames) {
				String fieldcode = fieldname.substring(0, fieldname.indexOf("_"));
				if (data_template.getFieldcode().equals(fieldcode)) {
					flag = true;// 找到
					break;
				}
			}
			if (!flag) {// 没找到则删除
				codesetRepository.deleteByDatanodeidAndFieldcode(nodeid, data_template.getFieldcode());// 删除关联的档号设置
				templateRepository.delete(data_template);
			} else {
				flag = false;// 重置标志
			}
		}
		for (String fieldname : fieldnames) {
			String fieldcode = fieldname.substring(0, fieldname.indexOf("_"));
			for (Tb_data_template data_template : data_templateList) {
				if (data_template.getFieldcode().equals(fieldcode)) {
					flag = true;// 找到
					break;
				}
			}
			if (!flag) {// 没找到则添加
				Tb_data_template data_template = new Tb_data_template();
				data_template.setFieldcode(fieldcode);
				data_template.setNodeid(nodeid);
				data_template.setFieldname(fieldname.substring(fieldname.indexOf("_") + 1));
				Pattern pattern = Pattern.compile("^f\\d{2}");
				if (pattern.matcher(fieldcode).matches()) {
					data_template.setFieldtable("tb_entry_detail");
				} else {
					data_template.setFieldtable("tb_entry_index");
				}
				templateRepository.save(data_template);
			} else {
				flag = false;// 重置标志
			}
		}
		if("12345678910".equals(nodeid)){//库房模板
		}else{
//			Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
//			if (node.getLuckstate() != null && "1".equals(node.getLuckstate())) {
//				synctemplate(nodeid, "allChild", "true", xtType,table);
//			}
		}
		// 重新排序表单字段
		reorderFsequence(nodeid, fieldnames);
	}

	public void submitSxfields(String nodeid, String[] fieldnames, String xtType,String table) {
		List<Tb_data_template_sx> data_templateList = sxTemplateRepository.findAllByNodeidAndFieldtableInOrderByFsequence(nodeid,new String []{"tb_entry_index","tb_entry_detail"});
		boolean flag = false;
		for (Tb_data_template_sx data_template : data_templateList) {
			for (String fieldname : fieldnames) {
				String fieldcode = fieldname.substring(0, fieldname.indexOf("_"));
				if (data_template.getFieldcode().equals(fieldcode)) {
					flag = true;// 找到
					break;
				}
			}
			if (!flag) {// 没找到则删除
				sxCodesetRepository.deleteByDatanodeidAndFieldcode(nodeid, data_template.getFieldcode());// 删除关联的档号设置
				sxTemplateRepository.delete(data_template);
			} else {
				flag = false;// 重置标志
			}
		}
		for (String fieldname : fieldnames) {
			String fieldcode = fieldname.substring(0, fieldname.indexOf("_"));
			for (Tb_data_template_sx data_template : data_templateList) {
				if (data_template.getFieldcode().equals(fieldcode)) {
					flag = true;// 找到
					break;
				}
			}
			if (!flag) {// 没找到则添加
				Tb_data_template_sx data_template = new Tb_data_template_sx();
				data_template.setFieldcode(fieldcode);
				data_template.setNodeid(nodeid);
				data_template.setFieldname(fieldname.substring(fieldname.indexOf("_") + 1));
				Pattern pattern = Pattern.compile("^f\\d{2}");
				if (pattern.matcher(fieldcode).matches()) {
					data_template.setFieldtable("tb_entry_detail");
				} else {
					data_template.setFieldtable("tb_entry_index");
				}
				sxTemplateRepository.save(data_template);
			} else {
				flag = false;// 重置标志
			}
		}
		if("12345678910".equals(nodeid)){//库房模板
		}else{
//			Tb_data_node_sx node = secondaryDataNodeRepository.findByNodeid(nodeid);
//			if (node.getLuckstate() != null && "1".equals(node.getLuckstate())) {
//				syncSxtemplate(nodeid, "allChild", "true", xtType,table);
//			}
		}
		// 重新排序表单字段
		reorderSxFsequence(nodeid, fieldnames);
	}

	public void submitGroupfields(String nodeid, String[] fieldnames, String nodeType) {
		/**
		 * 1.拿到选中的字段
		 * 2.根据nodetype 选中的字段是否已存在对应的类型模板中
		 * 3 不存在则保持
		 *
		 */
		String tablename = "";
		if ("group".equals(nodeType)) {
			tablename = "tb_docgroupself";
		} else {
			tablename = "tb_dossierself";
		}
		List<Tb_data_template_sx> data_templateList = sxTemplateRepository.findAllByNodeidAndFieldtableInOrderByFsequence(nodeid, new String[]{tablename});
		boolean flag = false;
		for (Tb_data_template_sx data_template : data_templateList) {
			for (String fieldname : fieldnames) {
				String fieldcode = fieldname.substring(0, fieldname.indexOf("_"));
				if (data_template.getFieldcode().equals(fieldcode)) {
					flag = true;// 找到
					break;
				}
			}
			if (!flag) {// 没找到则删除
				sxCodesetRepository.deleteByDatanodeidAndFieldcode(nodeid, data_template.getFieldcode());// 删除关联的档号设置
				sxTemplateRepository.delete(data_template);
			} else {
				flag = false;// 重置标志
			}
		}
		for (String fieldname : fieldnames) {
			String fieldcode = fieldname.substring(0, fieldname.indexOf("_"));
			for (Tb_data_template_sx data_template : data_templateList) {
				if (data_template.getFieldcode().equals(fieldcode)) {
					flag = true;// 找到
					break;
				}
			}
			if (!flag) {// 没找到则添加
				Tb_data_template_sx data_template = new Tb_data_template_sx();
				data_template.setFieldcode(fieldcode);
				data_template.setNodeid(nodeid);
				data_template.setFieldname(fieldname.substring(fieldname.indexOf("_") + 1));
				Pattern pattern = Pattern.compile("^f\\d{2}");
				data_template.setFieldtable(tablename);
				sxTemplateRepository.save(data_template);
			} else {
				flag = false;// 重置标志
			}
		}
		if("12345678910".equals(nodeid)){//库房模板
		}else{
//			Tb_data_node_sx node = secondaryDataNodeRepository.findByNodeid(nodeid);
//			if (node.getLuckstate() != null && "1".equals(node.getLuckstate())) {
//				syncSxtemplate(nodeid, "allChild", "true", "声像系统",nodeType);
//			}
		}
		// 重新排序表单字段
		reorderSxFsequence(nodeid, fieldnames);
	}

	public boolean synctemplate(String nodeid, String copyType, String syncCodeset, String xtType,String tableType) {
		if("声像系统".equals(xtType)){
			return syncSxtemplate(nodeid, copyType, syncCodeset, xtType,tableType);
		}else{
			try {
				List<Tb_data_node> data_nodeList;
				if ("firstChild".equals(copyType)) {
					data_nodeList = nodesettingService.getFirstLevelChildNode(nodeid, xtType);// 获取首层节点
				} else {
					List<Tb_user_node_parents> childAllNodes = nodesettingService.getChildNodeOfPcid(nodeid,"");// 获取pcid下的所有权限
					String[] nodeids = new String[childAllNodes.size()];
					for (int i = 0; i < childAllNodes.size(); i++) {
						nodeids[i] = childAllNodes.get(i).getNodeid();
					}
					data_nodeList = dataNodeRepository.findByNodeidIn(nodeids);
				}
				String[] nodeIdArray = GainField.getFieldValues(data_nodeList, "nodeid").length == 0 ? new String[] { "" }
						: GainField.getFieldValues(data_nodeList, "nodeid");

				// 删除子节点的数据
				templateRepository.delete(templateRepository.findByNodeidIn(nodeIdArray));
				// 同步模板
				List<Tb_data_template> saveTemplateList = new ArrayList<>();
				List<Tb_data_template> data_templateParentList = templateRepository.findByNodeid(nodeid);
				for (Tb_data_node data_node : data_nodeList) {
					// 同步模板
					// 复制父类节点数据
					for (Tb_data_template data_template_parent : data_templateParentList) {// 所有父节点数据
						Tb_data_template data_template = new Tb_data_template();
						BeanUtils.copyProperties(data_template_parent, data_template);
						// 本质：data_template.set(data_template_return)
						data_template.setTemplateid(null);
						data_template.setNodeid(data_node.getNodeid());
						saveTemplateList.add(data_template);
					}
				}
				templateRepository.save(saveTemplateList);

				if ("true".equals(syncCodeset)) {
					// 删除子节点的数据
					codesetRepository.delete(codesetRepository.findByDatanodeidIn(nodeIdArray));
					// 同步档号设置
					List<Tb_codeset> codesetParentList = codesetRepository.findByDatanodeidIn(new String[] { nodeid });
					List<Tb_codeset> saveCodeSetList = new ArrayList<>();
					for (Tb_data_node data_node : data_nodeList) {
						// 同步档号设置
						for (Tb_codeset codeset_parent : codesetParentList) {// 所有父节点数据
							Tb_codeset codeset = new Tb_codeset();
							BeanUtils.copyProperties(codeset_parent, codeset);
							codeset.setCodeid(null);
							codeset.setDatanodeid(data_node.getNodeid());
							saveCodeSetList.add(codeset);
						}
					}
					codesetRepository.save(saveCodeSetList);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}

	public boolean syncSxtemplate(String nodeid, String copyType, String syncCodeset, String xtType,String tableType) {
		String[] table=getTableNameByTableType(tableType);
		try {
			List<Tb_data_node_sx> data_nodeList;
			if ("firstChild".equals(copyType)) {
				data_nodeList = nodesettingService.getFirstLevelChildNode(nodeid,xtType);// 获取首层节点
			} else {
				List<Tb_user_node_parents> childAllNodes = nodesettingService.getChildNodeOfPcid(nodeid,xtType);// 获取pcid下的所有权限
				String[] nodeids = new String[childAllNodes.size()];
				for (int i = 0; i < childAllNodes.size(); i++) {
					nodeids[i] = childAllNodes.get(i).getNodeid();
				}
				data_nodeList = secondaryDataNodeRepository.findByNodeidIn(nodeids);
			}
			String[] nodeIdArray = GainField.getFieldValues(data_nodeList, "nodeid").length == 0 ? new String[] { "" }
					: GainField.getFieldValues(data_nodeList, "nodeid");

			// 删除子节点的数据
			sxTemplateRepository.deleteByNodeidInAndFieldtableIn(nodeIdArray,table);
			// 同步模板
			List<Tb_data_template_sx> saveTemplateList = new ArrayList<>();
			List<Tb_data_template_sx> data_templateParentList = sxTemplateRepository.findByNodeidAndFieldtableIn(nodeid,table);
			for (Tb_data_node_sx data_node : data_nodeList) {
				// 同步模板
				// 复制父类节点数据
				for (Tb_data_template_sx data_template_parent : data_templateParentList) {// 所有父节点数据
					Tb_data_template_sx data_template = new Tb_data_template_sx();
					BeanUtils.copyProperties(data_template_parent, data_template);
					// 本质：data_template.set(data_template_return)
					data_template.setTemplateid(null);
					data_template.setNodeid(data_node.getNodeid());
					saveTemplateList.add(data_template);
				}
			}
			sxTemplateRepository.save(saveTemplateList);

			if ("true".equals(syncCodeset)) {
				// 删除子节点的数据
				sxCodesetRepository.deleteByDatanodeidInAndFiledtableIn(nodeIdArray,table);
				// 同步档号设置
				List<Tb_codeset_sx> codesetParentList = sxCodesetRepository.findByDatanodeidAndFiledtableInOrderByOrdernum(nodeid,table);
				List<Tb_codeset_sx> saveCodeSetList = new ArrayList<>();
				for (Tb_data_node_sx data_node : data_nodeList) {
					// 同步档号设置
					for (Tb_codeset_sx codeset_parent : codesetParentList) {// 所有父节点数据
						Tb_codeset_sx codeset = new Tb_codeset_sx();
						BeanUtils.copyProperties(codeset_parent, codeset);
						codeset.setCodeid(null);
						codeset.setDatanodeid(data_node.getNodeid());
						saveCodeSetList.add(codeset);
					}
				}
				sxCodesetRepository.save(saveCodeSetList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// 按提交过来的字段管理已选字段顺序进行排序
	public void reorderFsequence(String nodeid, String[] fieldnames) {
		List<Tb_data_template> data_templateList = templateRepository.findByNodeidOrderByFsequence(nodeid);
		for (Tb_data_template tb_data_template : data_templateList) {
			int index = ArrayUtils.indexOf(fieldnames,
					tb_data_template.getFieldcode() + "_" + tb_data_template.getFieldname());
			if (index != -1) {
				tb_data_template.setFsequence((long) (index + 1));
			}
		}
	}

	// 按提交过来的字段管理已选字段顺序进行排序
	public void reorderSxFsequence(String nodeid, String[] fieldnames) {
		List<Tb_data_template_sx> data_templateList = sxTemplateRepository.findByNodeidOrderByFsequence(nodeid);
		for (Tb_data_template_sx tb_data_template : data_templateList) {
			int index = ArrayUtils.indexOf(fieldnames,
					tb_data_template.getFieldcode() + "_" + tb_data_template.getFieldname());
			if (index != -1) {
				tb_data_template.setFsequence((long) (index + 1));
			}
		}
	}

	public String findFEnumsByNodeid(String nodeid) {
		return templateRepository.findFEnumsByNodeid(nodeid);
	}

	public boolean isNumberType(String nodeId, String fieldCode) {
		boolean isNumber = false;
		List<Tb_data_template> data_templateList = templateRepository.findByNodeid(nodeId);
		for (Tb_data_template data_template : data_templateList) {
			if (data_template.getFieldcode().equals(fieldCode)) {
				if ("calculation".equals(data_template.getFtype()) || "date".equals(data_template.getFtype())) {
					isNumber = true;
					break;
				}
			}
		}
		return isNumber;
	}

	public List<Tb_data_template> findByFenums(String fenums) {
		return templateRepository.findByFenums(fenums);
	}

	public static Specification<Tb_data_template> getSearchNodeidCondition(String nodeid) {
		Specification<Tb_data_template> searchNodeidCondition = new Specification<Tb_data_template>() {
			@Override
			public Predicate toPredicate(Root<Tb_data_template> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				Predicate p = criteriaBuilder.equal(root.get("nodeid"), nodeid);
				return criteriaBuilder.or(p);
			}
		};
		return searchNodeidCondition;
	}

	public static Specification<Tb_data_template_sx> getSxSearchNodeidCondition(String nodeid) {
		Specification<Tb_data_template_sx> searchNodeidCondition = new Specification<Tb_data_template_sx>() {
			@Override
			public Predicate toPredicate(Root<Tb_data_template_sx> root, CriteriaQuery<?> criteriaQuery,
										 CriteriaBuilder criteriaBuilder) {
				Predicate p = criteriaBuilder.equal(root.get("nodeid"), nodeid);
				return criteriaBuilder.or(p);
			}
		};
		return searchNodeidCondition;
	}

	public Integer updateNodeLuckState(String nodeid, String state, String xtType,String table) {
		if (state.equals("1")) {
			synctemplate(nodeid, "allChild", "true", xtType,table);
		}
		if("声像系统".equals(xtType)){
			secondaryDataNodeRepository.updateNodeLuckState(nodeid, state);
		}else{
			dataNodeRepository.updateNodeLuckState(nodeid, state);
			if(GuavaCache.getValueByKey(GuavaUsedKeys.NODE_ALL_LIST) == null){
				CopyOnWriteArrayList<Tb_data_node> list = new CopyOnWriteArrayList<>();
				list.addAll(dataNodeRepository.findAll());
				GuavaCache.setKeyValue(GuavaUsedKeys.NODE_ALL_LIST, list);
			}
			List<Tb_data_node> dataNodeList=(List<Tb_data_node>) GuavaCache.getValueByKey(GuavaUsedKeys.NODE_ALL_LIST);
			dataNodeList.parallelStream().forEach(dataNode -> {
				if(nodeid.equals(dataNode.getNodeid())){
					dataNode.setLuckstate(state);
				}
			});
			GuavaCache.setKeyValue(GuavaUsedKeys.NODE_ALL_LIST, dataNodeList);
		}
		return 1;
	}

	public ExtMsg findArchivecode(String nodeid) {
		List<Tb_data_template> dataTemplates = templateRepository.findByNodeid(nodeid);
		if (dataTemplates.size() > 0) {
			boolean hasArchivecode = false;
			for (Tb_data_template tb_data_template : dataTemplates) {
				if ("archivecode".equals(tb_data_template.getFieldcode())) {
					hasArchivecode = true;
					break;
				}
			}
			if (hasArchivecode) {
				return new ExtMsg(true, "获取档号成功", dataTemplates.size());
			} else {
				return new ExtMsg(false, "获取档号失败", dataTemplates.size());
			}
		} else {
			return new ExtMsg(false, "获取档号失败", 0);
		}
	}

	public String export(String nodeid, String xtType,String table) {
		if("声像系统".equals(xtType)){
			return exportSx(nodeid, xtType,table);
		}else{
			List<Tb_data_template> templateList = templateRepository.findByNodeid(nodeid);
			if (templateList.size() == 0) {
				return "";
			}
			Tb_data_node node = dataNodeRepository.findByNodeid(nodeid);
			List<Tb_codeset> codesetList = codesetRepository.findByDatanodeidIn(new String[] { nodeid });

			Field[] fields = templateList.get(0).getClass().getDeclaredFields();
			String[] keys = new String[fields.length];
			String[] names = new String[fields.length];
			for (int i = 0; i < fields.length; i++) {
				Field f = fields[i];
				f.setAccessible(true);
				keys[i] = f.getName();
				names[i] = f.getName();
			}

			List<Map<String, Object>> listmap = createExcelRecord(templateList, keys);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
			String fullName = "";
			if("publicNode".equalsIgnoreCase(nodeid) || "12345678910".equals(nodeid)){
				fullName = "库房模板";
			}else {
				nodesettingService.getNodefullnameLoop(node.getNodeid(), "_", "");
			}
			String fileName = "删除【" + fullName + "】节点模板数据" + "前备份" + sdf.format(new Date());

			String excelPath = rootpath + "/backupRestore/excelTemp/" + fileName;// 拷贝后根路径
			File eleDir = new File(excelPath);
			if (!eleDir.exists()) {
				eleDir.mkdirs();
			}
			// 判断有没有存在之前压缩的zip，有的就删除
			File zipFile = new File(excelPath + ".zip");
			if (zipFile.exists()) {
				zipFile.delete();
			}

			try {
				String outName = excelPath + "/" + fileName + ".xls";
				ExportUtil eu = new ExportUtil();
				Workbook wb = new HSSFWorkbook();
				wb = eu.createWorkBook(wb, "模板数据.xls", listmap, keys, names);// 获取工作簿
				OutputStream outXlsx = new FileOutputStream(outName);
				if (codesetList.size() > 0) {
					Field[] fieldCode = codesetList.get(0).getClass().getDeclaredFields();
					String[] keyCode = new String[fieldCode.length];
					String[] nameCode = new String[fieldCode.length];
					for (int i = 0; i < fieldCode.length; i++) {
						Field f = fieldCode[i];
						f.setAccessible(true);
						keyCode[i] = f.getName();
						nameCode[i] = f.getName();
					}

					List<Map<String, Object>> listmapCode = createExcelCode(codesetList, keyCode);
					wb = eu.createWorkBook(wb, "档案设置数据.xls", listmapCode, keyCode, nameCode);// 获取工作簿
				}
				wb.write(outXlsx);
				outXlsx.flush();
				outXlsx.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			ZipUtil.zip(excelPath.replaceAll("/", "\\\\"), excelPath.replaceAll("/", "\\\\") + ".zip", "");// 压缩
			delFolder(excelPath);// 打包完，删除文件夹及文件夹里面的数据
			return excelPath;
		}

	}

	private String exportSx(String nodeid, String xtType,String table){
		List<Tb_data_template_sx> templateList = sxTemplateRepository.findAllByNodeidAndFieldtableInOrderByFsequence(nodeid,getTableNameByTableType(table));
		if (templateList.size() == 0) {
			return "";
		}
		Tb_data_node_sx node = secondaryDataNodeRepository.findByNodeid(nodeid);
		List<Tb_codeset_sx> codesetList = sxCodesetRepository.findByDatanodeidAndFiledtableInOrderByOrdernum(nodeid,getTableNameByTableType(table));

		Field[] fields = templateList.get(0).getClass().getDeclaredFields();
		String[] keys = new String[fields.length];
		String[] names = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			f.setAccessible(true);
			keys[i] = f.getName();
			names[i] = f.getName();
		}

		List<Map<String, Object>> listmap = createSxExcelRecord(templateList, keys);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
		String fullName = "";
		if("publicNode".equalsIgnoreCase(nodeid) || "12345678910".equals(nodeid)){
			fullName = "库房模板";
		}else {
			nodesettingService.getSxNodefullnameLoop(node.getNodeid(), "_", "");
		}
		String fileName = "删除【" + fullName + "】节点模板数据" + "前备份" + sdf.format(new Date());

		String excelPath = rootpath + "/backupRestore/excelTemp/" + fileName;// 拷贝后根路径
		File eleDir = new File(excelPath);
		if (!eleDir.exists()) {
			eleDir.mkdirs();
		}
		// 判断有没有存在之前压缩的zip，有的就删除
		File zipFile = new File(excelPath + ".zip");
		if (zipFile.exists()) {
			zipFile.delete();
		}

		try {
			String outName = excelPath + "/" + fileName + ".xls";
			ExportUtil eu = new ExportUtil();
			Workbook wb = new HSSFWorkbook();
			wb = eu.createWorkBook(wb, "模板数据.xls", listmap, keys, names);// 获取工作簿
			OutputStream outXlsx = new FileOutputStream(outName);
			if (codesetList.size() > 0) {
				Field[] fieldCode = codesetList.get(0).getClass().getDeclaredFields();
				String[] keyCode = new String[fieldCode.length];
				String[] nameCode = new String[fieldCode.length];
				for (int i = 0; i < fieldCode.length; i++) {
					Field f = fieldCode[i];
					f.setAccessible(true);
					keyCode[i] = f.getName();
					nameCode[i] = f.getName();
				}

				List<Map<String, Object>> listmapCode = createSxExcelCode(codesetList, keyCode);
				wb = eu.createWorkBook(wb, "档案设置数据.xls", listmapCode, keyCode, nameCode);// 获取工作簿
			}
			wb.write(outXlsx);
			outXlsx.flush();
			outXlsx.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ZipUtil.zip(excelPath.replaceAll("/", "\\\\"), excelPath.replaceAll("/", "\\\\") + ".zip", "");// 压缩
		delFolder(excelPath);// 打包完，删除文件夹及文件夹里面的数据
		return excelPath;
	}

	private List<Map<String, Object>> createExcelRecord(List<Tb_data_template> templateList, String[] keys) {
		List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
		Tb_data_template template;
		for (int j = 0; j < templateList.size(); j++) {
			template = templateList.get(j);
			Map<String, Object> mapValue = new HashMap<>();
			for (String key : keys) {
				mapValue.put(key, GainField.getFieldValueByName(key, template));
			}
			listmap.add(mapValue);
		}
		return listmap;
	}

	private List<Map<String, Object>> createSxExcelRecord(List<Tb_data_template_sx> templateList, String[] keys) {
		List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
		Tb_data_template_sx template;
		for (int j = 0; j < templateList.size(); j++) {
			template = templateList.get(j);
			Map<String, Object> mapValue = new HashMap<>();
			for (String key : keys) {
				mapValue.put(key, GainField.getFieldValueByName(key, template));
			}
			listmap.add(mapValue);
		}
		return listmap;
	}

	private List<Map<String, Object>> createExcelCode(List<Tb_codeset>  codesetList, String[] keys) {
		List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
		Tb_codeset codeset;
		for (int j = 0; j < codesetList.size(); j++) {
			codeset = codesetList.get(j);
			Map<String, Object> mapValue = new HashMap<>();
			for (String key : keys) {
				mapValue.put(key, GainField.getFieldValueByName(key, codeset));
			}
			listmap.add(mapValue);
		}
		return listmap;
	}

	private List<Map<String, Object>> createSxExcelCode(List<Tb_codeset_sx> codesetList, String[] keys) {
		List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
		Tb_codeset_sx codeset;
		for (int j = 0; j < codesetList.size(); j++) {
			codeset = codesetList.get(j);
			Map<String, Object> mapValue = new HashMap<>();
			for (String key : keys) {
				mapValue.put(key, GainField.getFieldValueByName(key, codeset));
			}
			listmap.add(mapValue);
		}
		return listmap;
	}

	// 富滇初始化完善节点的classid字段
	public void updateAllNodeNext(String nodecode) {
		String classid = dataNodeRepository.findClassid(nodecode);
		dataNodeRepository.updateAllNodeNext(nodecode, classid);
	}

	// 产品更正nodecode
	public void updateAjFirstNode() {
		String nodecode = dataNodeRepository.findAjByNodeid();
		List<Tb_data_node> list = dataNodeRepository.findAjs(nodecode);
		for (Tb_data_node tdn : list) {
			List<Tb_data_node> subTdns = dataNodeRepository.findByParentnodeid(tdn.getNodeid());
			if (subTdns.size() == 1) {
				String subNodeid = subTdns.get(0).getNodeid();
				String subNodecode = tdn.getNodecode() + "001";
				dataNodeRepository.updateNodecode(subNodecode, subNodeid);
			}
		}

	}

	// 卷内文件总数更新字段设置
	public void updateSumInnerFiles() {
		// 先查找fieldname为卷内文件数，fieldcode不是f02的 nodeid fieldcode templateid
		List<Tb_data_template> templates = templateRepository.findNodeids();
		if (templates.isEmpty()) {
			return;
		}
		// 根据nodeid查找其是否存在 f02.存在就f02跟现在放卷内文件总数的字段的fieldname互换，不存在就直接更新这个字段为f02
		for (Tb_data_template t : templates) {
			// 看f02有没有设置
			List<Tb_data_template> list = templateRepository.findByNodeidAndFieldcode(t.getNodeid());
			if (!list.isEmpty()) {// 互换
				// 同时更新list原f02那条记录的filedtable和fieldcode
				String templateid = list.get(0).getTemplateid();
				String fieldcode = list.get(0).getFieldcode();
				if ("tb_entry_index".equals(t.getFieldtable())) {// 卷内文件总数的字段设在tb_entry_index
					templateRepository.updateFieldZbFjn(templateid, fieldcode);
				} else {// 卷内文件总数的字段设在tb_entry_detail
					templateRepository.updateFieldCbFjn(templateid, fieldcode);
				}
			}
			// 更新t那条卷内文件总数记录的fieldtable和fildcode
			templateRepository.updateFieldF02(t.getTemplateid());
		}
	}

	public String getFieldlengthByTable(String tablename, String fieldcode, String xtType) {
		String sql = DBCompatible.getInstance().findQueryFieldLengthSQL(tablename, fieldcode);
		if("声像系统".equals(xtType)){
			Query query = entityManagerSx.createNativeQuery(sql);
			return  query.getSingleResult().toString();
		}else{
			Query query = entityManager.createNativeQuery(sql);
			return  query.getSingleResult().toString();
		}
	}



	//获取对象的所有属性
	public String[] getFiledName(Object o){
		Field[] fields=o.getClass().getDeclaredFields();
		String[] fieldNames=new String[fields.length];
		for(int i=0;i<fields.length;i++){
			fieldNames[i]=fields[i].getName();
		}
		return fieldNames;
	}

	//导入字段模板
	public Map<Integer,Integer> importFieldModel(String filepath,String nodeid){
	    Map map = new HashMap();
		int impCount = 0;
		int erro=0;
		List<Tb_data_template> templates = new ArrayList<>();
		List<Tb_codeset> codesets = new ArrayList<>();
		if(new File(filepath).isFile()&&nodeid!=null&&!"".equals(nodeid)){
			//解析文件
            try {
                //拿到列头
                List<String> headValue = ReadExcel.getHeadField(new File(filepath));
                String[] fieldcode = new String[headValue.size()];
                String[] codeserFields = getFiledName(new Tb_codeset());
                headValue.toArray(fieldcode);
                //拿到字段数据
                List<List<List<String>>> excelValue = ReadExcel.readFieldModel(filepath);
                //拼接对象
                for(int i=0;i<excelValue.get(0).size();i++){
                    Tb_data_template template=ValueUtil.creatTemp(fieldcode,excelValue.get(0).get(i));
                    template.setTemplateid(null);
                    template.setNodeid(nodeid);
                    templates.add(template);
                }
                for(int i=0;i<excelValue.get(excelValue.size()-1).size();i++){
                    Tb_codeset tb_codeset=ValueUtil.creatcodeset(codeserFields,excelValue.get(excelValue.size()-1).get(i));
                    tb_codeset.setDatanodeid(nodeid);
                    tb_codeset.setCodeid(null);
                    codesets.add(tb_codeset);
                }
                List<Tb_data_template> count=templateRepository.save(templates);
                List<Tb_codeset> tb_codesets=codesetRepository.save(codesets);
                impCount =count.size()+tb_codesets.size();
                erro = excelValue.get(0).size()+excelValue.get(excelValue.size()-1).size()-impCount;
                map.put("impCount",impCount);
                map.put("erro",erro);
            }catch (IOException IO){
                IO.printStackTrace();
            }catch (Exception E){
                E.printStackTrace();
            }
		}
		return map;
	}

	//导入字段模板
	public Map<Integer,Integer> importSxFieldModel(String filepath,String nodeid){
		Map map = new HashMap();
		int impCount = 0;
		int erro=0;
		List<Tb_data_template_sx> templates = new ArrayList<>();
		List<Tb_codeset_sx> codesets = new ArrayList<>();
		if(new File(filepath).isFile()&&nodeid!=null&&!"".equals(nodeid)){
			//解析文件
			try {
				//拿到列头
				List<String> headValue = ReadExcel.getHeadField(new File(filepath));
				String[] fieldcode = new String[headValue.size()];
				String[] codeserFields = getFiledName(new Tb_codeset_sx());
				headValue.toArray(fieldcode);
				//拿到字段数据
				List<List<List<String>>> excelValue = ReadExcel.readFieldModel(filepath);
				//拼接对象
				for(int i=0;i<excelValue.get(0).size();i++){
					Tb_data_template_sx template=ValueUtil.creatSxTemp(fieldcode,excelValue.get(0).get(i));
					template.setTemplateid(null);
					template.setNodeid(nodeid);
					templates.add(template);
				}
				for(int i=0;i<excelValue.get(excelValue.size()-1).size();i++){
					Tb_codeset_sx tb_codeset=ValueUtil.creatSxcodeset(codeserFields,excelValue.get(excelValue.size()-1).get(i));
					tb_codeset.setDatanodeid(nodeid);
					tb_codeset.setCodeid(null);
					codesets.add(tb_codeset);
				}
				List<Tb_data_template_sx> count=sxTemplateRepository.save(templates);
				List<Tb_codeset_sx> tb_codesets=sxCodesetRepository.save(codesets);
				impCount =count.size()+tb_codesets.size();
				erro = excelValue.get(0).size()+excelValue.get(excelValue.size()-1).size()-impCount;
				map.put("impCount",impCount);
				map.put("erro",erro);
			}catch (IOException IO){
				IO.printStackTrace();
			}catch (Exception E){
				E.printStackTrace();
			}
		}
		return map;
	}


	public String getFileRemark(String fieldcode,String fieldname) {
        String fileremark = "";
        String[] fieldnames = templateRepository.getOrderFileName(fieldcode);
        if(fieldnames!=null&&fieldnames.length>0){
            fileremark = getFileremark(fieldnames,fieldname);
        }else{
            fileremark = "无";
        }
        return fileremark;
    }

    //截取描述字段
    public String getFileremark(String[] fieldnameArr,String fieldname){
        String fileremark = "";
        if(fieldnameArr.length==1&&fieldnameArr[0].equals(fieldname)){//只获取到一个字段描述并且与当前字段描述一致
            fileremark = "无";
        }else{
            for(int j=0;j<fieldnameArr.length;j++){
                if(fieldnameArr[j].equals(fieldname)){//含有当前字段描述则跳过
                    if(j==fieldnameArr.length-1){//跳过字段刚好为最后字段，去除多余[，]
                        int idx=fileremark.lastIndexOf("，");
                        fileremark=fileremark.substring(0,idx);
                    }
                    continue;
                }
                if(j>5){
                    break;
                }
                if(j==5){//5个或五个以上加...
                    fileremark += fieldnameArr[j]+"，...";
                }else if(j==fieldnameArr.length-1){//5个以内的最大数
                    fileremark += fieldnameArr[j];
                }else{
                    fileremark += fieldnameArr[j]+"，";
                }
            }
        }
        return fileremark;
    }

	public List<Tb_data_template> setMetadata(String[] selectMetadata,String[] selectMetadataIds) {
    	if(selectMetadataIds!=null){
			List<String> selectMetadataIdList = new ArrayList<>();
			for(String selectMetadataId : selectMetadataIds){
				if(selectMetadataIdList.contains(selectMetadataId)){
					return null;
				}else{
					selectMetadataIdList.add(selectMetadataId);
				}
			}
		}
    	List<Tb_data_template> templates = new ArrayList<>();
    	for(String selectMetadataStr : selectMetadata){
    		String[] selectStr = selectMetadataStr.split("∪");
			Tb_data_template template = templateRepository.findByTemplateid(selectStr[0]);
    		if(selectStr.length<3){
				template.setMetadataid(null);
				template.setMetadatafieldname(null);
				template.setMetadatatype(null);
			}else{
    			template.setMetadatatype(selectStr[1]);
    			Tb_metadata_temp metadataTemp = metadataTempRepository.findByTemplateid(selectStr[2]);
    			template.setMetadataid(selectStr[2]);
    			template.setMetadatafieldname(metadataTemp.getFieldname());
			}
			templates.add(template);
		}
		return templateRepository.save(templates);
	}

	/**
	 *  根据表类型返回表名称
	 */
	public String[] getTableNameByTableType(String tableType) {
		String[] table=new String[]{"tb_entry_index", "tb_entry_detail"};
		if ("group".equals(tableType)) {
			table=new String []{"tb_docgroupself"};
		} else if("dossierself".equals(tableType)){
			table= new String[]{"tb_dossierself"};
		}
		return table;
	}

	public String getTabTypeByTableName(String tableType) {
		String table="tb_entry_index";
		if ("group".equals(tableType)) {
			table="tb_docgroupself";
		} else if("dossierself".equals(tableType)){
			table= "tb_dossierself";
		}
		return table;
	}
}