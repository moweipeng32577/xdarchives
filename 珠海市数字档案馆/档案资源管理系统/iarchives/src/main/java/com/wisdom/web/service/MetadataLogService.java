package com.wisdom.web.service;

import com.wisdom.util.LogAop;
import com.wisdom.web.entity.Tb_metadata_log;
import com.wisdom.web.repository.MetadataLogRepository;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wjh
 */
@Service
@Transactional
public class MetadataLogService {

    //线程队列(与任务线程进行隔离)
    private static ExecutorService ec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);

    @Autowired
    MetadataLogRepository metadataLogRepository;

    /**
     * 插入保存单个档案元数据日志
     * @param metadataLog 日志对象
     */
    public void save(Tb_metadata_log metadataLog){
        save(Arrays.asList(metadataLog));
    }

    /**
     * 插入多个档案元数据日志
     * @param metadataLogs 日志对象集合
     */
    public void save(List<Tb_metadata_log> metadataLogs){
        SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String ip = LogAop.getIpAddress();
        ec.submit(()->{
            String operatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            for(Tb_metadata_log metadataLog:metadataLogs){
                if(metadataLog.getOperateuser()==null){//判断是否填写完整相关字段
                    metadataLog.setOperateuser(user.getLoginname());//设置操作人
                    metadataLog.setOperateusername(user.getRealname());//设置操作人名
                    metadataLog.setOperatetime(operatTime);//设置操作时间
                    metadataLog.setIp(ip);//设置操作IP
                }
            }
            metadataLogRepository.save(metadataLogs);
        });
    }

    public Page<Tb_metadata_log> findBySearch(String condition, String operator, String content, String entryid, int page,int limit) {
        Specifications sp =  ClassifySearchService.addSearchbarCondition(null, "entryid","equal",entryid);
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(new Sort.Order(Sort.Direction.DESC,"operatetime"));//按操作时间倒序
        return metadataLogRepository.findAll(sp, new PageRequest(page - 1, limit, new Sort(sorts)));
    }
}
