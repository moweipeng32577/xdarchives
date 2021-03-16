package com.wisdom.web.service;

import com.wisdom.secondaryDataSource.entity.Tb_system_config_sx;
import com.wisdom.secondaryDataSource.repository.SxSystemConfigRepository;
import com.wisdom.util.GainField;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.FunctionRepository;
import com.wisdom.web.repository.SystemConfigRepository;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RonJiang on 2017/11/30 0030.
 */
@Service
@Transactional
public class SystemConfigService {

	@Autowired
	SystemConfigRepository systemConfigRepository;

    @Autowired
    FunctionRepository functionRepository;

	@Autowired
	SxSystemConfigRepository sxSystemConfigRepository;

	public List<Tb_system_config> findbyparentvalue(String value) {
		if (value != null && !("".equals(value))) {
			return systemConfigRepository.findByParentValue(value);
		}
		return null;
	}

	public List<Tb_system_config_sx> findByParentSxValue(String value) {
		if (value != null && !("".equals(value))) {
			return sxSystemConfigRepository.findByParentValue(value);
		}
		return null;
	}

	public List<ExtSearchData> findnamebyparentvalue(String value) {
		List<Tb_system_config> enumsList = new ArrayList<Tb_system_config>();// 存放value值对应的Tb_system_config对象
		List<ExtSearchData> enumsNameList = new ArrayList<ExtSearchData>();// 存放value值对应的枚举项的值
		if (value != null && !("".equals(value))) {
			enumsList = systemConfigRepository.findByParentValue(value);
		}
		if (enumsList.size() > 0) {
			for (Tb_system_config enums : enumsList) {
				String enumsItem = enums.getValue();
				String enumsName = enums.getCode();
				ExtSearchData extSearchData = new ExtSearchData();
				extSearchData.setItem(enumsItem);
				extSearchData.setName(enumsName);
				enumsNameList.add(extSearchData);
			}
			return enumsNameList;
		}
		return null;
	}

	public List<ExtNcTree> findByConfigcodes(String configcode,String type) {
		String configid = systemConfigRepository.findByConfigcode(configcode);
		if(configid==null){
		    return null;
        }
		List<Tb_system_config> systemConfigs = systemConfigRepository.findByParentconfigidOrderBySortsequence(configid);
		List<ExtNcTree> extNcTrees = new ArrayList<>();
		SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        List<Tb_role> roles = userDetails.getRoles();
        String[] roleids = GainField.getFieldValues(roles, "roleid").length == 0 ? new String[] { "" }
                : GainField.getFieldValues(roles, "roleid");
        List<Tb_right_function> functions  = functionRepository.findByfunctionsdesktop(roleids, "desktop",
                userDetails.getUserid());
		for (int i = 0; i < systemConfigs.size(); i++) {
			ExtNcTree tree = new ExtNcTree();
			tree.setFnid(systemConfigs.get(i).getConfigid());
			tree.setCls("file");
			tree.setLeaf(true);
			tree.setText(systemConfigs.get(i).getCode());
			if("预约类型".equals(configcode)){//根据权限判断是否需要该节点
				if(type!=null&&"Gl".equals(type)){
					for(Tb_right_function function:functions){
						if("k69".equals(function.getIsp())&&function.getName().equals(systemConfigs.get(i).getCode())){
							extNcTrees.add(tree);
						}
					}
				}else{
					if("查档预约".equals(systemConfigs.get(i).getCode())){
						extNcTrees.add(tree);
					}
				}
			}else {
                extNcTrees.add(tree);
            }
		}
		return extNcTrees;
	}

	public List<ExtNcTree> findByParentconfigid(String parentconfigid) {
		List<Tb_system_config> systemConfigs;
		if ("".equals(parentconfigid)) {
			systemConfigs = systemConfigRepository.findByParentconfigidIsNullOrderBySortsequence();
		} else {
			systemConfigs = systemConfigRepository.findByParentconfigidOrderBySortsequence(parentconfigid);
		}
		List<ExtNcTree> extNcTrees = new ArrayList<>();

		for (int i = 0; i < systemConfigs.size(); i++) {
			ExtNcTree tree = new ExtNcTree();
			tree.setFnid(systemConfigs.get(i).getConfigid());
			List<Tb_system_config> lists = systemConfigRepository
					.findByParentconfigidOrderBySortsequence(systemConfigs.get(i).getConfigid());
			// if (!lists.isEmpty()) {//有子节点
			// tree.setCls("folder");
			// tree.setLeaf(false);
			// } else {
			tree.setCls("file");
			tree.setLeaf(true);
			// }
			tree.setText(systemConfigs.get(i).getCode());
			extNcTrees.add(tree);
		}
		return extNcTrees;
	}

	public List<ExtNcTree> findSxByParentconfigid(String parentconfigid) {
		List<Tb_system_config_sx> systemConfigs;
		if ("".equals(parentconfigid)) {
			systemConfigs = sxSystemConfigRepository.findByParentconfigidIsNullOrderBySortsequence();
		} else {
			systemConfigs = sxSystemConfigRepository.findByParentconfigidOrderBySortsequence(parentconfigid);
		}
		List<ExtNcTree> extNcTrees = new ArrayList<>();

		for (int i = 0; i < systemConfigs.size(); i++) {
			ExtNcTree tree = new ExtNcTree();
			tree.setFnid(systemConfigs.get(i).getConfigid());
			List<Tb_system_config_sx> lists = sxSystemConfigRepository
					.findByParentconfigidOrderBySortsequence(systemConfigs.get(i).getConfigid());
			// if (!lists.isEmpty()) {//有子节点
			// tree.setCls("folder");
			// tree.setLeaf(false);
			// } else {
			tree.setCls("file");
			tree.setLeaf(true);
			// }
			tree.setText(systemConfigs.get(i).getCode());
			extNcTrees.add(tree);
		}
		return extNcTrees;
	}

	public Page<Tb_system_config> findBySearch(int page, int limit, String condition, String operator, String content,
			String configid, Sort sort) {
		PageRequest pageRequest = new PageRequest(page - 1, limit,
				sort == null ? new Sort(Sort.Direction.ASC, "sortsequence") : sort);
		Specification<Tb_system_config> searchid;
		if ("".equals(configid)) {
			searchid = getSearchParentconfigidIsnullCondition();
		} else {
			searchid = getSearchParentconfigidEqualCondition(configid);
		}
		Specifications specifications = Specifications.where(searchid);
		if (content != null) {
			specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
		}
		return systemConfigRepository.findAll(specifications, pageRequest);
	}

	public Page<Tb_system_config> findSxBySearch(int page, int limit, String condition, String operator, String content,
											   String configid, Sort sort) {
		PageRequest pageRequest = new PageRequest(page - 1, limit,
				sort == null ? new Sort(Sort.Direction.ASC, "sortsequence") : sort);
		Specification<Tb_system_config_sx> searchid;
		if ("".equals(configid)) {
			searchid = getSxSearchParentconfigidIsnullCondition();
		} else {
			searchid = getSxSearchParentconfigidEqualCondition(configid);
		}
		Specifications specifications = Specifications.where(searchid);
		if (content != null) {
			specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
		}
		return sxSystemConfigRepository.findAll(specifications, pageRequest);
	}

	public Tb_system_config saveSystemConfig(Tb_system_config system_config) {
		return systemConfigRepository.save(system_config);
	}

	public Tb_system_config_sx saveSxSystemConfig(Tb_system_config system_config) {
		Tb_system_config_sx systemConfigSx=new Tb_system_config_sx();
		BeanUtils.copyProperties(system_config,systemConfigSx);
		return sxSystemConfigRepository.save(systemConfigSx);
	}

	public Tb_system_config_sx saveSxSystemConfigTwo(Tb_system_config_sx system_config) {
		return sxSystemConfigRepository.save(system_config);
	}

	public List<Tb_system_config> findByConfigidAndParentconfigidIsNotNull(String parentconfigid) {
		return systemConfigRepository.findByConfigidAndParentconfigidIsNotNull(parentconfigid);
	}

	public Tb_system_config findByConfigid(String configid) {
		return systemConfigRepository.findByConfigid(configid);
	}

	public Tb_system_config_sx findSxByConfigid(String configid) {
		return sxSystemConfigRepository.findByConfigid(configid);
	}

	public Tb_system_config findByConfigcode(String code) {
		return systemConfigRepository.findByConfigcodeAndParentconfigidIsNull(code);
	}

	public Tb_system_config_sx findSxByConfigcode(String code) {
		return sxSystemConfigRepository.findByConfigcodeAndParentconfigidIsNull(code);
	}

	public Tb_system_config findByConfigvalue(String value) {
		return systemConfigRepository.findByConfigvalueAndParentconfigidIsNull(value);
	}

	public Tb_system_config_sx findSxByConfigvalue(String value) {
		return sxSystemConfigRepository.findByConfigvalueAndParentconfigidIsNull(value);
	}

	public Tb_system_config findByParentconfigidAndConfigcode(String parentconfigid, String code) {
		return systemConfigRepository.findByParentconfigidAndConfigcode(parentconfigid, code);
	}

	public Tb_system_config_sx findSxByParentconfigidAndConfigcode(String parentconfigid, String code) {
		return sxSystemConfigRepository.findByParentconfigidAndConfigcode(parentconfigid, code);
	}

	public Tb_system_config findByParentconfigidAndConfigvalue(String parentconfigid, String value) {
		return systemConfigRepository.findByParentconfigidAndConfigvalue(parentconfigid, value);
	}

	public Tb_system_config_sx findSxByParentconfigidAndConfigvalue(String parentconfigid, String value) {
		return sxSystemConfigRepository.findByParentconfigidAndConfigvalue(parentconfigid, value);
	}

	public Integer deleteByConfigidIn(String[] configids) {
		return systemConfigRepository.deleteByConfigidIn(configids);
	}

	public Integer deleteSxByConfigidIn(String[] configids) {
		return sxSystemConfigRepository.deleteByConfigidIn(configids);
	}

	public Integer deleteByConfigid(String configid) {
		if (systemConfigRepository.findByParentconfigid(configid).size() > 0) {
			systemConfigRepository.deleteByParentconfigid(configid);
		}
		return systemConfigRepository.deleteByConfigid(configid);
	}

	public Integer deleteSxByConfigid(String configid) {
		if (sxSystemConfigRepository.findByParentconfigid(configid).size() > 0) {
			sxSystemConfigRepository.deleteByParentconfigid(configid);
		}
		return sxSystemConfigRepository.deleteByConfigid(configid);
	}

	public Boolean importExcelConfig(File file, String parentid){


		return false;
	}
	public static Specification<Tb_system_config> getSearchParentconfigidIsnullCondition() {
		Specification<Tb_system_config> searchParentconfigidIsnullCondition = new Specification<Tb_system_config>() {
			@Override
			public Predicate toPredicate(Root<Tb_system_config> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				Predicate p = criteriaBuilder.isNull(root.get("parentconfigid"));
				return criteriaBuilder.or(p);
			}
		};
		return searchParentconfigidIsnullCondition;
	}

	public static Specification<Tb_system_config_sx> getSxSearchParentconfigidIsnullCondition() {
		Specification<Tb_system_config_sx> searchParentconfigidIsnullCondition = new Specification<Tb_system_config_sx>() {
			@Override
			public Predicate toPredicate(Root<Tb_system_config_sx> root, CriteriaQuery<?> criteriaQuery,
										 CriteriaBuilder criteriaBuilder) {
				Predicate p = criteriaBuilder.isNull(root.get("parentconfigid"));
				return criteriaBuilder.or(p);
			}
		};
		return searchParentconfigidIsnullCondition;
	}

	public static Specification<Tb_system_config> getSearchParentconfigidEqualCondition(String configid) {
		Specification<Tb_system_config> searchParentconfigidEqualCondition = new Specification<Tb_system_config>() {
			@Override
			public Predicate toPredicate(Root<Tb_system_config> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				Predicate p = criteriaBuilder.equal(root.get("parentconfigid"), configid);
				return criteriaBuilder.or(p);
			}
		};
		return searchParentconfigidEqualCondition;
	}

	public static Specification<Tb_system_config_sx> getSxSearchParentconfigidEqualCondition(String configid) {
		Specification<Tb_system_config_sx> searchParentconfigidEqualCondition = new Specification<Tb_system_config_sx>() {
			@Override
			public Predicate toPredicate(Root<Tb_system_config_sx> root, CriteriaQuery<?> criteriaQuery,
										 CriteriaBuilder criteriaBuilder) {
				Predicate p = criteriaBuilder.equal(root.get("parentconfigid"), configid);
				return criteriaBuilder.or(p);
			}
		};
		return searchParentconfigidEqualCondition;
	}

	/**
	 * 通过父配置id获取配置名称
	 * 
	 * @param parentconfigid
	 * @return
	 */
	public List<String> findConfigcode(String parentconfigid) {
		return systemConfigRepository.findConfigcodeByParentconfigidInfo(parentconfigid);
	}

	public List<Tb_system_config> findConfigByConfigcode(String configcode) {
		String configid = systemConfigRepository.findByConfigcode(configcode);
		return systemConfigRepository.findByParentconfigidOrderBySortsequence(configid);
	}

	/**
	 * 参数拖拉排序
	 * @param order
	 * @return
	 */
	public Integer orderConfig(String parentconfig, int order) {
		Integer index = systemConfigRepository.orderConfig(parentconfig,order);
		return index;
	}

	/**
	 * 参数拖拉排序  声像
	 * @param order
	 * @return
	 */
	public Integer orderSxConfig(String parentconfig, int order) {
		Integer index = sxSystemConfigRepository.orderConfig(parentconfig,order);
		return index;
	}
}