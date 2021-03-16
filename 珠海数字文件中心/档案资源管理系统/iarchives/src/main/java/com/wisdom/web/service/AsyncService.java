package com.wisdom.web.service;

import com.wisdom.web.entity.Tb_service_config;
import com.wisdom.web.entity.Tb_service_metadata;
import com.wisdom.web.repository.ServiceConfigRepository;
import com.wisdom.web.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * @description: 专门用于异步操作的service
 * @author:ljr
 * @create: 2020-05-25 14-24
 *
 */
@Service
public class AsyncService {

    @Autowired
    ServiceConfigRepository serviceConfigRepository;

    @PersistenceContext
    EntityManager entityManager;

    /**
    * 插入源数据记录
    *
    * @param entryids
    * @param module
    * @param operation
    * @param userid
    * @return
    * @throws
    **/
    @Async
    @Transactional
    public void saveserviceConfig(String[] entryids,String module, String operation,String userid){
        Tb_service_config service_config = serviceConfigRepository.findByOperationAndParentidIsNull(operation);
        String msg="";
        String status="";
        String accredId = "";
        if(service_config!=null){
            status = service_config.getMstatus();
            msg = service_config.getOperationmsg();
            accredId = service_config.getAid();
        }
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Tb_service_metadata> list = new ArrayList<>();
        for(String entryid: entryids){
            Tb_service_metadata service_metadata = new Tb_service_metadata(operation,status,sdf.format(new Date()),
                    msg,accredId,userid, entryid);
            list.add(service_metadata);
        }
        for(int i=0;i<list.size();i++){
            entityManager.persist(list.get(i));
            if(i%1000==0&&i!=0){
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
//        for (String entryid : entryids) {
//            count = count + saveServiceMetadata(entryid, module, operation);
//        }
    }
}
