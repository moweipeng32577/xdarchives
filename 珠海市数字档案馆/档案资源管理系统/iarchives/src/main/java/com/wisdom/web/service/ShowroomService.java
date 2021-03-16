package com.wisdom.web.service;

import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.ElectronicRepository;
import com.wisdom.web.repository.ShowroomDatePersonRepository;
import com.wisdom.web.repository.ShowroomRepository;
import com.wisdom.web.repository.ShowroomRepository;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zdw on 2020/03/20
 */
@Service
@Transactional
public class ShowroomService {

    @Autowired
    ShowroomRepository showroomRepository;

    @Autowired
    ShowroomDatePersonRepository showroomDatePersonRepository;

    @Autowired
    ElectronicRepository electronicRepository;

    public Page<Tb_showroom> findBySearch(String type,String condition, String operator, String content, int page, int limit, Sort sort){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Specifications sp = null;
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        return showroomRepository.findAll(sp,pageRequest);
    }

    //预约当天的展厅信息
    public Page<Tb_showroom> findByDateSearch(String date,int page, int limit){
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        Page<Tb_showroom> showroomPage=showroomRepository.findAll(pageRequest);
        if(date!=null&&date.length()>10){
            date=date.substring(0,10);
            Specifications sp = null;
            sp = ClassifySearchService.addSearchbarCondition(sp,"visitingdate","equal",date);
            List<Tb_showroom_date_person> sdps=showroomDatePersonRepository.findAll(sp);//预约当天的展厅预约人数信息
            if(sdps.size()>0){
                List<Tb_showroom> content = showroomPage.getContent();
                long totalElements = showroomPage.getTotalElements();
                List<Tb_showroom> returnResult = new ArrayList<>();
                for(Tb_showroom showroom:content){
                    Tb_showroom newShowroom= new Tb_showroom();
                    BeanUtils.copyProperties(showroom,newShowroom);
                    int yyPerson=0;
                    for(Tb_showroom_date_person sdp:sdps){
                        if(newShowroom.getShowroomid().equals(sdp.getShowroomid())){//同一展厅id
                            yyPerson=sdp.getAudiences();
                            break;
                        }
                    }
                    newShowroom.setYyAudiences(yyPerson);//设置当天已预约人数
                    if(yyPerson==newShowroom.getAudiences()){
                        newShowroom.setFlag("1");//1 表示已满
                    }
                    returnResult.add(newShowroom);
                }
                return new PageImpl(returnResult,pageRequest,totalElements);
            }else{//直接返回展厅信息
                return showroomPage;
            }
        }else{//直接返回展厅信息
            return showroomPage;
        }
    }

    public Tb_showroom getShowroom(String showroomid){
        return showroomRepository.findByShowroomid(showroomid);
    }

    public Integer delShowroom(String[] showroomidData){
        return showroomRepository.deleteByShowroomidIn(showroomidData);
    }

    public Tb_showroom saveShowroom(Tb_showroom showroom,  String[] eleids){
        showroom.setAppendix("");
        Tb_showroom save = showroomRepository.save(showroom);
        if (eleids != null) {
            electronicRepository.updateEntryid(save.getShowroomid(), eleids);
        }
        return save;
    }

    public List<Tb_electronic> getShowroomFile(String showroomId) {
        return electronicRepository.findByEntryidOrderBySortsequence(showroomId);
    }

    /**
     * 展厅修改
     * @param eleids
     * @return
     */
    public Tb_showroom editShowroom(Tb_showroom showroom, String[] eleids) {
        if (eleids != null) {
            electronicRepository.updateEntryid(showroom.getShowroomid(), eleids);
        }
        showroom.setAppendix("");
        return showroomRepository.save(showroom);
    }
}
