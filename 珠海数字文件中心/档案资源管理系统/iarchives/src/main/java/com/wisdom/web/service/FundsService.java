package com.wisdom.web.service;

import com.wisdom.util.GainField;
import com.wisdom.util.LogAop;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.DataNodeRepository;
import com.wisdom.web.repository.FundsRepository;
import com.wisdom.web.repository.RightOrganRepository;
import com.wisdom.web.repository.TemplateRepository;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RonJiang on 2018/4/8 0008.
 */
@Service
@Transactional
public class FundsService {

    @Autowired
    FundsRepository fundsRepository;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Autowired
    RightOrganRepository rightOrganRepository;

    @Autowired
    TemplateRepository templateRepository;

    public Page<Tb_funds> findBySearch(String condition,String operator,String content,int page,int limit,Sort sort){
        Specifications sp = null;
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        List<Sort.Order> sorts = new ArrayList<>();
    	sorts.add(new Sort.Order(Sort.Direction.DESC,"fundsstarttime"));//全总起始时间降序
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort == null ? new Sort(sorts) : sort);
        return fundsRepository.findAll(sp,pageRequest);
    }
    
    public List<String> findByFunds() {
    	return fundsRepository.findByFunds();
    }

    public Tb_funds getFunds(String fundsid){
        return fundsRepository.findByFundsid(fundsid);
    }

    public Tb_funds saveFunds(Tb_funds funds){
        return fundsRepository.save(funds);
    }

    /**
     * 更新对应的模板全宗字段的全宗号默认值
     * @param organid  机构id
     * @param fundsValue 全宗值
     */
    public void updateTemplateFunds(String organid, String fundsValue) {
        Tb_right_organ organ=rightOrganRepository.findOne(organid);
        //String[] organidArr = rightOrganRepository.findWithOrganlevel(organ.getOrganlevel());
        List<Tb_right_organ> organList= rightOrganRepository.getWithOrganlevel(organ.getOrganlevel().trim());//根据机构序号获取所有的子机构id，包括自己的机构id
        List<String> fundOrganidList=fundsRepository.getOrganids();//获取已设置全宗号的机构id
        //判断子机构是否是机构和已设置全宗号,只更新没有全宗信息的子机构和部门
        List<String> noFundsList=new ArrayList<>();//没设置全宗号的机构集合
        noFundsList.add(organid);//添加本机构
        List<String> subOrganlevels=new ArrayList<>();//有设置全宗号的organlevel集合
        for(Tb_right_organ subOrgan:organList){
            if(organid.trim().equals(subOrgan.getOrganid())){
                continue;
            }
            if(fundOrganidList.contains(subOrgan.getOrganid())||fundOrganidList.contains(subOrgan.getOrganid()+"    ")){
                subOrganlevels.add(subOrgan.getOrganlevel().trim());
            }
        }
        for(Tb_right_organ subOrgan:organList){
            if(organid.trim().equals(subOrgan.getOrganid())){
                continue;
            }
            boolean falg=true;
            if(subOrganlevels.size()>0){//有子机构设置全宗号
                for(String subOrganlevel:subOrganlevels){
                    if(subOrgan.getOrganlevel().trim().startsWith(subOrganlevel)){//设置了全宗号的子机构的所有子机构不做全宗号更新处理
                        falg=false;
                        break;
                    }
                }
            }
            if(falg){
                noFundsList.add(subOrgan.getOrganid());
            }
        }
        String[] organidArr=noFundsList.toArray(new String[noFundsList.size()]);
        //更新对应模板全宗字段的默认值
        templateRepository.updateFunds(fundsValue,organidArr);
    }

    public Integer delFunds(String[] fundsidData){
        return fundsRepository.deleteByFundsidIn(fundsidData);
    }
    
    public String getNodeFunds(String nodeid){
        String organid = dataNodeRepository.findRefidByNodeid(nodeid);
        List<String> fundsList = fundsRepository.findFundsByOrganid(organid);
        if(fundsList.size()>0){
            return fundsList.get(0);
        }
        return "";
    }
    
    public String getOrganFunds(String organid){
    	List<String> funds = fundsRepository.findFundsByOrganid(organid);
    	if (funds.size() > 0) {
    		return funds.get(0);
    	}
        return null;
    }
}