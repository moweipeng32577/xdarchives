package com.wisdom.web.service;

import com.wisdom.web.entity.Tb_backup_strategy;
import com.wisdom.web.repository.BackupStrategyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

/**
 * Created by RonJiang on 2018/3/23 0023.
 */
@Service
@Transactional
public class BackupStrategyService {

    @Autowired
    BackupStrategyRepository backupStrategyRepository;

    public void saveBackupStrategy(Tb_backup_strategy backupStrategy){
        backupStrategyRepository.save(backupStrategy);
    }

    public void clearOriginalBackupStrategy(String backupContent){
        Integer backupContentCount = backupStrategyRepository.findCountByBackupcontent(backupContent);
        if(backupContentCount>0){
            backupStrategyRepository.deleteByBackupcontent(backupContent);
        }
    }

    public Tb_backup_strategy getBackupStrategy(String backupContent){
        return backupStrategyRepository.findByBackupcontent(backupContent);
    }

    /**
     *  检验是否到达备份执行时间
     * @param backupContent
     * @return
     */
    public boolean checkTimeOut(String backupContent){
        String backupFrequency = backupStrategyRepository.findBackupfrequencyByBackupcontent(backupContent);
        String backupTime = backupStrategyRepository.findBackuptimeByBackupcontent(backupContent);
        if(backupFrequency==null || "".equals(backupFrequency) || backupTime==null || "".equals(backupTime)){//备份策略未设置
            return false;
        }
        Integer backupTimeIntval = Integer.parseInt(backupTime);
        Calendar c = Calendar.getInstance();
        Integer currentHourOfDay = c.get(Calendar.HOUR_OF_DAY);//获取当前小时数值
        Integer currentDayOfWeek = c.get(Calendar.DAY_OF_WEEK);//获取当前星期数据
        if("everyday".equals(backupFrequency)){//备份频率为每天
            if(currentHourOfDay==backupTimeIntval){//设置时间和当前时间时钟数相同
                return true;
            }
        }else{//备份频率为每周
            Integer backupDayOfWeekIntval = convertDayOfWeek(backupFrequency);
            if(currentDayOfWeek==backupDayOfWeekIntval && currentHourOfDay==backupTimeIntval){//星期和时钟均符合
                return true;
            }
        }
        return false;
    }

    /**
     *  将Cron表达式能识别的周代号转化为日历api返回的周代号
     * @param backupFrequency
     * @return
     */
    private static Integer convertDayOfWeek(String backupFrequency){
        if("SUN".equals(backupFrequency)){return 1;}
        if("MON".equals(backupFrequency)){return 2;}
        if("TUE".equals(backupFrequency)){return 3;}
        if("WED".equals(backupFrequency)){return 4;}
        if("THU".equals(backupFrequency)){return 5;}
        if("FRI".equals(backupFrequency)){return 6;}
        if("SAT".equals(backupFrequency)){return 7;}
        return null;
    }

}
