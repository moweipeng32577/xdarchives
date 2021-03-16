package com.wisdom.web.service;

import com.wisdom.web.entity.RetentionInitProbability;
import com.wisdom.web.repository.RetentionInitProbabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by Leo on 2019/5/8 0008.
 */
@Service
@Transactional
public class RetentionInitProbabilityService {
    @Autowired
    RetentionInitProbabilityRepository retentionInitProbabilityRepository;

    //更新初始概率
    public boolean updateRetentionInitProbability(RetentionInitProbability retentionInitProbability) {
        //先找出dr为0的一项
        RetentionInitProbability oldRetentionInitProbability = retentionInitProbabilityRepository.findByDr(0);
        //更新dr为1；
        if(oldRetentionInitProbability != null) {
            oldRetentionInitProbability.setDr(1);
            retentionInitProbabilityRepository.save(oldRetentionInitProbability);
        }
        //然后插入新的数据，dr为0
        if(retentionInitProbability != null){
            retentionInitProbability.setDr(0);
            retentionInitProbability.setCreatedate(new Date());
            retentionInitProbabilityRepository.save(retentionInitProbability);
        }
        return true;
    }
}
