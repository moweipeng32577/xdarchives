package com.wisdom.util;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.wisdom.secondaryDataSource.entity.Tb_system_config_sx;
import com.wisdom.secondaryDataSource.repository.SxSystemConfigRepository;
import com.wisdom.web.entity.Tb_system_config;
import com.wisdom.web.repository.SystemConfigRepository;

import java.util.ArrayList;
import java.util.List;

/*
 * @description:
 * @author:ljr
 * @create: 2020-12-24 17-12
 *
 */
public class SxSystemConfigListener extends AnalysisEventListener<Tb_system_config_sx> {

    List<Tb_system_config_sx> list=new ArrayList<>();

    private static final int BATCH_COUNT = 300;

    private SxSystemConfigRepository sxSystemConfigRepository;

    private String parentid;

    public SxSystemConfigListener(SxSystemConfigRepository sxSystemConfigRepository,String parentid){
        this.sxSystemConfigRepository=sxSystemConfigRepository;
        this.parentid=parentid;
    }
    @Override
    public void invoke(Tb_system_config_sx tb_system_config_sx, AnalysisContext analysisContext) {
        System.out.println(JSON.toJSONString(tb_system_config_sx));
        tb_system_config_sx.setParentconfigid(parentid);
        list.add(tb_system_config_sx);
        if(list.size()>=BATCH_COUNT){
            saveData();
            list.clear();
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
    }

    private void saveData(){
        sxSystemConfigRepository.save(list);
    }
}
