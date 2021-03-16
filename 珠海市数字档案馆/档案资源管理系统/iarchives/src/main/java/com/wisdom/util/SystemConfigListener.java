package com.wisdom.util;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.wisdom.web.entity.Tb_system_config;
import com.wisdom.web.repository.SystemConfigRepository;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/*
 * @description:
 * @author:ljr
 * @create: 2020-12-24 13-42
 *
 */
public class SystemConfigListener extends AnalysisEventListener<Tb_system_config> {


    List<Tb_system_config> list = new ArrayList<>();

    private static final int BATCH_COUNT = 300;

    private SystemConfigRepository systemConfigRepository;

    private String parentid;

    public SystemConfigListener(SystemConfigRepository systemConfigRepository,String parentid){
        this.systemConfigRepository=systemConfigRepository;
        this.parentid=parentid;
    }
    @Override
    public void invoke(Tb_system_config tb_system_config, AnalysisContext analysisContext) {
        System.out.println(JSON.toJSONString(tb_system_config));
        if(StringUtils.isNotBlank(parentid)) {
            tb_system_config.setParentconfigid(parentid);
        }
        list.add(tb_system_config);
        if(list.size() >= BATCH_COUNT){
            saveData();
            list.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
    }

    private void saveData(){
        systemConfigRepository.save(list);
    }
}
