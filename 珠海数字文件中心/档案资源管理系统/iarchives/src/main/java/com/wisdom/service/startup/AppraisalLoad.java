package com.wisdom.service.startup;

import com.wisdom.util.TimeScheduled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 *  系统启动时获取到期鉴定节点数量
 *
 * Created by Leo on 2020/8/10 0010.
 */
@Component
@Order(value=2)
public class AppraisalLoad implements ApplicationRunner {

    @Autowired
    TimeScheduled timeScheduled;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        timeScheduled.updateAppraisal();
    }
}
