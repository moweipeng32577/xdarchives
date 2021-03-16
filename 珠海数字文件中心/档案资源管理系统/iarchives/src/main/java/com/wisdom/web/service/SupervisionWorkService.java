package com.wisdom.web.service;

import com.wisdom.web.entity.Tb_supervision_electronic;
import com.wisdom.web.entity.Tb_supervision_work;
import com.wisdom.web.repository.SupervisionElectronicRepository;
import com.wisdom.web.repository.SupervisionWorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2020/10/12.
 */
@Service
@Transactional
public class SupervisionWorkService {



    @Autowired
    SupervisionWorkRepository supervisionWorkRepository;

    @Autowired
    SupervisionElectronicRepository supervisionElectronicRepository;


    public List getSelectYear(){
        List<Tb_supervision_work> returnList = new ArrayList<>();
        List<String> workList = supervisionWorkRepository.getSelectYear();
        for(String selectyear : workList){
            Tb_supervision_work supervisionWork = new Tb_supervision_work();
            supervisionWork.setSelectyear(selectyear);
            returnList.add(supervisionWork);
        }
        return returnList;
    }

    public void setSupervisionWork(Tb_supervision_work supervisionWork, String organid, String selectyear){
        Tb_supervision_work tbSupervisionWork = supervisionWorkRepository.findByOrganidAndSelectyear(organid,selectyear);
        if(tbSupervisionWork!=null&&(supervisionWork.getId()==null||"".equals(supervisionWork.getId()))){
            supervisionWork.setId(tbSupervisionWork.getId());
        }
        supervisionWorkRepository.save(supervisionWork);  //保存保管条件
    }

    public Map<String,String> getElectronicCount(String organid, String selectyear){
        List<Tb_supervision_electronic> supervisionElectronics = supervisionElectronicRepository.findByOrganidAndSelectyear(organid,selectyear);
        int fillingnameNum = 0;
        int classplannameNum = 0;
        int fundsfilesNum = 0;
        int setindexNum = 0;
        int normativefilenameNum = 0;
        for(Tb_supervision_electronic supervisionElectronic : supervisionElectronics){
            if("fillingname".equals(supervisionElectronic.getSavetype())){
                fillingnameNum++;
            }else if("classplanname".equals(supervisionElectronic.getSavetype())){
                classplannameNum++;
            }else if("fundsfiles".equals(supervisionElectronic.getSavetype())){
                fundsfilesNum++;
            }else if("setindex".equals(supervisionElectronic.getSavetype())){
                setindexNum++;
            }else if("normativefilename".equals(supervisionElectronic.getSavetype())){
                normativefilenameNum++;
            }
        }
        Map<String,String> map = new HashMap<>();
        map.put("fillingnameNum",fillingnameNum+"");
        map.put("classplannameNum",classplannameNum+"");
        map.put("fundsfilesNum",fundsfilesNum+"");
        map.put("setindexNum",setindexNum+"");
        map.put("normativefilenameNum",normativefilenameNum+"");
        return map;
    }
}
