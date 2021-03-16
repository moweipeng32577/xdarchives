package com.wisdom.web.service;

import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.Tb_car_defend;
import com.wisdom.web.entity.Tb_equipment_defend;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.repository.EquipmentDefendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Leo on 2020/7/23 0023.
 */
@Service
@Transactional
public class EquipmentDefendService {

    @Autowired
    EquipmentDefendRepository equipmentDefendRepository;

    public Page<Tb_equipment_defend> getEquipmentDefendByequipmentId(String equipmentid, int page, int limit, String sort, String condition, String operator, String content){
        Sort sortobj = WebSort.getSortByJson(sort);
        PageRequest pageRequest = new PageRequest(page-1,limit,sortobj);
        Specifications sp = null;
        sp = sp.where(new SpecificationUtil("equipmentid","equal",equipmentid));
        if(content!=null){
            sp = sp.and(ClassifySearchService.addSearchbarCondition(sp, condition, operator, content));
        }
        return equipmentDefendRepository.findAll(sp,pageRequest);
    }

    public Tb_equipment_defend getEquipmentDefendById(String id){
        return equipmentDefendRepository.findAllById(id);
    }

    public Tb_equipment_defend equipmentDefendSubmit(Tb_equipment_defend equipmentDefend, String equipmentId){
        if(equipmentId!=null&&!"".equals(equipmentId.trim())){
            equipmentDefend.setEquipmentid(equipmentId);
        }
        return equipmentDefendRepository.save(equipmentDefend);
    }

    public int deleteEquipmentDefendByid(String[] ids){
        return equipmentDefendRepository.deleteByIdIn(ids);
    }
}
