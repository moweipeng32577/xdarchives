package com.wisdom.service.startup;

import com.wisdom.util.GuavaCache;
import com.wisdom.util.TimeScheduled;
import com.wisdom.web.entity.Tb_system_constants;
import com.wisdom.web.repository.SystemConstantsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * 静态配置加载类
 * 系统启动时从数据库读取静态配置，并添加到内存中
 *
 * Created by rong on 2020/6/12
 */
@Component
@Order(value=1)
public class ConstantsLoad implements ApplicationRunner {

    @Autowired
    SystemConstantsRepository systemConstantsRepository;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        List<Tb_system_constants> list = systemConstantsRepository.findAll();
        list.forEach(constant -> {
            GuavaCache.setKeyValue(constant.getConstantcode(), constant.getConstantvalue());
        });
    }
}
