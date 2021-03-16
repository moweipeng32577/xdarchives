package com.wisdom.web.service;

import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.Tb_car_defend;
import com.wisdom.web.entity.Tb_car_manage;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.repository.CarDefendRepository;
import com.wisdom.web.repository.CarManageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2020/4/17.
 */
@Service
@Transactional
public class CarManageService {


    @Autowired
    CarManageRepository carManageRepository;

    @Autowired
    CarDefendRepository carDefendRepository;

    @Autowired
    LogService logService;

    public Page<Tb_car_manage> getCarManages(int page, int limit, String sort, String condition, String operator, String content){
        Sort sortobj = WebSort.getSortByJson(sort);
        PageRequest pageRequest = new PageRequest(page-1,limit,sortobj);
        Specifications sp = null;
        if(content!=null){
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        return carManageRepository.findAll(sp,pageRequest);
    }

    public Tb_car_manage carManageSubmit(Tb_car_manage car_manage){
        Tb_car_manage car_manage1 = null;
        try{
           car_manage1 =  carManageRepository.save(car_manage);
        }catch (Exception e){
            e.printStackTrace();
        }
        return car_manage1;
    }

    public Tb_car_manage getCarManageByCarnumber(String carnumber){return carManageRepository.findByCarnumber(carnumber);}

    public Tb_car_manage getCarManageByid(String id){
        return carManageRepository.findById(id);
    }

    public int deleteCarManageByid(String[] ids){
        if(ids!=null){
            for(String str : ids){
                logService.recordTextLog("公车管理","车辆管理;删除车辆信息;条目id:"+str);
            }
        }
        return carManageRepository.deleteByIdIn(ids);
    }

    public Page<Tb_car_defend> getCarDefendByCarId(String carid,int page, int limit, String sort, String condition, String operator, String content){
        Sort sortobj = WebSort.getSortByJson(sort);
        PageRequest pageRequest = new PageRequest(page-1,limit,sortobj);
        Specifications sp = null;
        sp = sp.where(new SpecificationUtil("carid","equal",carid));
        if(content!=null){
            sp = sp.and(ClassifySearchService.addSearchbarCondition(sp, condition, operator, content));
        }
        return carDefendRepository.findAll(sp,pageRequest);
    }

    public Tb_car_defend carDefendSubmit(Tb_car_defend carDefend,String carid){
        if(carid!=null&&!"".equals(carid.trim())){
            carDefend.setCarid(carid);
        }
        return carDefendRepository.save(carDefend);
    }

    public Tb_car_defend getCarDefendByid(String id){
        return carDefendRepository.findById(id);
    }

    public int deleteCarDefendByid(String[] ids){
        return carDefendRepository.deleteByIdIn(ids);
    }
}
