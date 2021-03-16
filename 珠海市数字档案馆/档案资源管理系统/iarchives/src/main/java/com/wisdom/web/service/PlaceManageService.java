package com.wisdom.web.service;

import com.wisdom.util.GainField;
import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.Tb_place_defend;
import com.wisdom.web.entity.Tb_place_manage;
import com.wisdom.web.entity.Tb_place_order;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.repository.PlaceDefendRepository;
import com.wisdom.web.repository.PlaceManageRepository;
import com.wisdom.web.repository.PlaceOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2020/4/20.
 */
@Service
@Transactional
public class PlaceManageService {


    @Autowired
    PlaceManageRepository placeManageRepository;

    @Autowired
    PlaceDefendRepository placeDefendRepository;

    @Autowired
    PlaceOrderRepository placeOrderRepository;

    @Autowired
    LogService logService;

    public Page<Tb_place_manage> getPlaceManages(int page, int limit, String sort, String condition, String operator, String content){
        Sort sortobj = WebSort.getSortByJson(sort);
        PageRequest pageRequest = new PageRequest(page-1,limit,sortobj);
        Specifications sp = null;
        if(content!=null){
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        Page<Tb_place_manage> tb_place_managePage=placeManageRepository.findAll(sp,pageRequest);
        List<Tb_place_manage> place_manageList=tb_place_managePage.getContent();
        String ids="";//记录使用中的场地编号
        //判断预约时间是否超过当前时间，修改场地状态
        if(place_manageList!=null&&place_manageList.size()>0) {
            Map<String,Tb_place_manage> tb_place_manageMa=new HashMap<>();
            String[] placeIds=new String[place_manageList.size()];
            for (int i=0;i<place_manageList.size();i++) {
                placeIds[i]=place_manageList.get(i).getId();
                tb_place_manageMa.put(place_manageList.get(i).getId(),place_manageList.get(i));
            }
            List<Tb_place_order> place_orderList = placeOrderRepository.findByPlaceidInAndState(placeIds,"预约成功");
            if(place_orderList!=null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                try {
                    for (Tb_place_order placeOrder : place_orderList) {
                        if (!ids.contains(placeOrder.getPlaceid())) {//是否已经在使用中则不修改
                            Date startdate = sdf.parse(placeOrder.getStarttime());
                            Date enddate = sdf.parse(placeOrder.getEndtime());
                            Date nowtime = new Date();
                            //当前时间是否在预约时间范围内
                            Tb_place_manage placeManage = tb_place_manageMa.get(placeOrder.getPlaceid());
                            if (nowtime.getTime() >= startdate.getTime() && nowtime.getTime() <= enddate.getTime()) {//没过贼一直为使用中
                                if ("空闲中".equals(placeManage.getState())) {
                                    placeManage.setState("使用中");
                                    ids += placeOrder.getPlaceid() + ",";//已在使用中
                                    placeManageRepository.save(placeManage);
                                }
                            }else if (nowtime.getTime() != startdate.getTime() && nowtime.getTime() != enddate.getTime()) {//过了预约时间更新为空闲
                                if (!"空闲中".equals(placeManage.getState())) {
                                    placeManage.setState("空闲中");
                                    tb_place_manageMa.put(placeOrder.getPlaceid(),placeManage);//防止二次修改
                                    placeManageRepository.save(placeManage);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return tb_place_managePage;
    }

    public Tb_place_manage placeManageSubmit(Tb_place_manage place_manage){
        Tb_place_manage place_manage1 = placeManageRepository.save(place_manage);
        return place_manage1;
    }

    public Tb_place_manage getPlaceManageByid(String id){
        return placeManageRepository.findById(id);
    }

    public int deletePlaceManageByid(String[] ids){
        if (null!=ids){
            for(String str : ids){
                logService.recordTextLog("场地管理","场地管理;删除场地信息;条目id:"+str);
            }
        }
        return placeManageRepository.deleteByIdIn(ids);
    }



    public Page<Tb_place_defend> getPlaceDefendByCarId(String placeid, int page, int limit, String sort, String condition, String operator, String content){
        Sort sortobj = WebSort.getSortByJson(sort);
        PageRequest pageRequest = new PageRequest(page-1,limit,sortobj);
        Specifications sp = null;
        sp = sp.where(new SpecificationUtil("placeid","equal",placeid));
        if(content!=null){
            sp = sp.and(ClassifySearchService.addSearchbarCondition(sp, condition, operator, content));
        }
        return placeDefendRepository.findAll(sp,pageRequest);
    }

    public Tb_place_defend placeDefendSubmit(Tb_place_defend placeDefend,String placeid){
        if(placeid!=null&&!"".equals(placeid.trim())){
            placeDefend.setPlaceid(placeid);
        }
        return placeDefendRepository.save(placeDefend);
    }

    public Tb_place_defend getPlaceDefendByid(String id){
        return placeDefendRepository.findById(id);
    }

    public int deletePlaceDefendByid(String[] ids){
        return placeDefendRepository.deleteByIdIn(ids);
    }
}
