package com.wisdom.web.service;

import com.wisdom.web.controller.AcquisitionController;
import com.wisdom.web.entity.Tb_appraisal_standard;
import com.wisdom.web.entity.Tb_appraisal_type;
import com.wisdom.web.repository.AppraisalStandardRepository;
import com.wisdom.web.repository.AppraisalTypeRepository;
import com.wisdom.web.repository.EntryIndexCaptureRepository;
import com.wisdom.web.repository.EntryIndexRepository;
import com.wisdom.web.repository.SystemConfigRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * Created by RonJiang on 2018/5/9 0009.
 */
@Service
@Transactional
public class AppraisalStandardService {

    @Autowired
    AppraisalStandardRepository appraisalStandardRepository;

    @Autowired
    AppraisalTypeRepository appraisalTypeRepository;

    @Autowired
    EntryIndexCaptureRepository entryIndexCaptureRepository;
    
    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    SystemConfigRepository systemConfigRepository;

    public Page<Tb_appraisal_standard> findBySearch(String appraisaltypevalue, String condition, String operator, String content, int page, int limit, Sort sort){
        Specifications sp = null;
        Specification<Tb_appraisal_standard> searchAppraisalTypeCondition = null;
        if(appraisaltypevalue!=null && !"".equals(appraisaltypevalue)){
            searchAppraisalTypeCondition = getSearchAppraisalTypeCondition(appraisaltypevalue);
            sp = Specifications.where(searchAppraisalTypeCondition);
        }
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        return appraisalStandardRepository.findAll(sp,pageRequest);
    }

    public Tb_appraisal_standard getAppraisalStandard(String appraisalstandardid){
        return appraisalStandardRepository.findByAppraisalstandardid(appraisalstandardid);
    }

    public Tb_appraisal_standard saveAppraisalStandard(Tb_appraisal_standard appraisalStandard){
        return appraisalStandardRepository.save(appraisalStandard);
    }

    public Integer delAppraisalStandard(String[] appraisalstandardidData){
        return appraisalStandardRepository.deleteByAppraisalstandardidIn(appraisalstandardidData);
    }

    public List<Tb_appraisal_type> getAllAppraisalStandard(){
        return appraisalTypeRepository.findAll();
    }

    public List<String> getAllAppraisaltypevalue(){
        return appraisalTypeRepository.findAllAppraisaltypevalue();
    }

    public Tb_appraisal_type saveAppraisalType(Tb_appraisal_type appraisalType){
        return appraisalTypeRepository.save(appraisalType);
    }

    public Integer delAppraisalType(String appraisaltypevalue){
        return appraisalTypeRepository.deleteByAppraisaltypevalue(appraisaltypevalue);
    }

    public String findAppraisaltypeidByAppraisaltypevalue(String appraisaltypevalue){
        return appraisalTypeRepository.findAppraisaltypeidByAppraisaltypevalue(appraisaltypevalue);
    }

    public String getEntryretentionByEntryidAndAppraisaltype(String entryid,String appraisaltypevalue, String type){
    	String title = "";
    	if (type.equals("数据采集")) {
    		title = entryIndexCaptureRepository.findTitleByEntryid(entryid);
    	} else {
    		title = entryIndexRepository.findTitleByEntryid(entryid);
    	}
    	if (title != null) {
    		List<String> appraisalStandardValueList = appraisalStandardRepository.findAppraisalstandardvalueByAppraisaltypevalue(appraisaltypevalue);
            boolean ifContainStandardvalueFlag = false;
            Set<String> containAppraisalStandardValueSet = new HashSet<>();
            for(String appraisalStandardValue:appraisalStandardValueList){
                if(title.indexOf(appraisalStandardValue)!=-1){//若题名中包含鉴定标准值，则通过鉴定标准值在相应的鉴定类型中查找对应保管期限值
                    ifContainStandardvalueFlag = true;
                    containAppraisalStandardValueSet.add(appraisalStandardValue);
                }
            }
            if(ifContainStandardvalueFlag){//题名中包含鉴定标准值
                List<String> retentionConfigvalueList = new ArrayList<>();//存储通过鉴定标准值匹配到的所有保管期限的配置值
                for(String containAppraisalStandardValue:containAppraisalStandardValueSet){
                    String appraisalretention = appraisalStandardRepository.findAppraisalretentionByAppraisalstandardvalueAndAppraisaltypevalue(containAppraisalStandardValue,appraisaltypevalue);
                    List<String> retentionConfigvalues = systemConfigRepository.findConfigvalueByConfigcode(appraisalretention);//通过保管期限找到其对应的配置值
                    String retentionConfigvalue;
                    if(retentionConfigvalues.size()>1){//保管期限值在参数表中不止一个时，找到父级配置值为"Retention"的保管期限值对应的配置值
                        retentionConfigvalue = systemConfigRepository.findConfigvalueByConfigcodeAndParentconfigvalue(appraisalretention,"Retention");
                    }else{
                        retentionConfigvalue = retentionConfigvalues.get(0);
                    }
                    retentionConfigvalueList.add(retentionConfigvalue);
                }
                //永久>长期>30年>短期>10年>5年
                //永久(如果题名中包含'永久',那么直接获取)
            	if (retentionConfigvalueList.contains("Y")) {
            		return systemConfigRepository.findConfigcodeByConfigvalueAndParentconfigvalue("Y","Retention");
            	}
            	//长期(不包含'永久')
            	if (retentionConfigvalueList.contains("CQ") && !retentionConfigvalueList.contains("Y")) {
            		return systemConfigRepository.findConfigcodeByConfigvalueAndParentconfigvalue("CQ","Retention");
            	}
            	//30年(不包含'永久','长期')
            	if (retentionConfigvalueList.contains("D30") && !retentionConfigvalueList.contains("Y") && !retentionConfigvalueList.contains("CQ")) {
            		return systemConfigRepository.findConfigcodeByConfigvalueAndParentconfigvalue("D30","Retention");
            	}
            	//短期(不包含'永久','长期','30年')
            	if (retentionConfigvalueList.contains("DQ") && !retentionConfigvalueList.contains("Y") && 
            			!retentionConfigvalueList.contains("CQ") && !retentionConfigvalueList.contains("D30")) {
             		return systemConfigRepository.findConfigcodeByConfigvalueAndParentconfigvalue("DQ","Retention");
            	}
            	//10年(不包含'永久','长期','30年','短期')
            	if (retentionConfigvalueList.contains("D10") && !retentionConfigvalueList.contains("Y") && !retentionConfigvalueList.contains("CQ") && 
            			!retentionConfigvalueList.contains("D30") && !retentionConfigvalueList.contains("DQ")) {
            		return systemConfigRepository.findConfigcodeByConfigvalueAndParentconfigvalue("D10","Retention");
            	}
            	//5年(不包含'永久','长期','30年','短期','10年')
            	if (retentionConfigvalueList.contains("D5") && !retentionConfigvalueList.contains("Y") && !retentionConfigvalueList.contains("CQ") && 
            			!retentionConfigvalueList.contains("D30") && !retentionConfigvalueList.contains("DQ") && !retentionConfigvalueList.contains("D10")){
            		return systemConfigRepository.findConfigcodeByConfigvalueAndParentconfigvalue("D5","Retention");
            	}
            }
    	}
        return AcquisitionController.DEFAULT_ENTRYRETENTION;
    }

    public static Specification<Tb_appraisal_standard> getSearchAppraisalTypeCondition(String appraisaltypevalue){
        Specification<Tb_appraisal_standard> SearchAppraisalTypeCondition = new Specification<Tb_appraisal_standard>() {
            @Override
            public Predicate toPredicate(Root<Tb_appraisal_standard> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.equal(root.get("appraisaltypevalue"), appraisaltypevalue);
                return criteriaBuilder.and(p);
            }
        };
        return SearchAppraisalTypeCondition;
    }
}