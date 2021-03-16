package com.wisdom.web.service;

import com.alibaba.fastjson.JSONObject;
import com.wisdom.web.entity.Szh_electronic_capture;
import com.wisdom.web.entity.Szh_entry_index_capture;
import com.wisdom.web.repository.SzhCalloutEntryRepository;
import com.wisdom.web.repository.SzhElectronicCaptureRepository;
import com.wisdom.web.repository.SzhEntryIndexCaptureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.File;
import java.util.Map;

/**
 * Created by Rong on 2018-12-06.
 */
@Service
public class SzhElectronicService {

    @Autowired
    SzhEntryIndexCaptureRepository szhEntryIndexCaptureRepository;

    @Autowired
    SzhElectronicCaptureRepository szhElectronicCaptureRepository;

    @Autowired
    ElectronicService electronicService;

    @Autowired
    SzhCalloutEntryRepository szhCalloutEntryRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Resource
    RedisTemplate<String, String> redisTemplate;

    @Value("${system.document.rootpath}")
    private String rootpath;

    private final String REDIS_KEY = "batchinsert";


    public Map<String, Object> saveElectronic(String entryid, String filename){
        String basepath = electronicService.getStorageBaseDir("capture", entryid);
        File targetFile= new File(rootpath + basepath, filename);
        Szh_electronic_capture elec = new Szh_electronic_capture();
        elec.setFilesize(String.valueOf(targetFile.length()));
        elec.setEntryid(entryid);
        elec.setFilepath(basepath);

        Szh_entry_index_capture index = szhEntryIndexCaptureRepository.findByEntryid(entryid);
        if (index.getEleid() == null) {
            index.setEleid(String.valueOf(1));
        } else {
            int num = Integer.parseInt(index.getEleid().trim());
            index.setEleid(String.valueOf(num + 1));
        }
        szhEntryIndexCaptureRepository.save(index);

        elec.setFilename(filename);
        elec.setFiletype(filename.substring(filename.lastIndexOf('.') + 1));
        elec = szhElectronicCaptureRepository.save(elec);
        return elec.getMap();
    }

    public void saveElectronic(String basepath, String entryid, String filename, long filesize){
        Szh_electronic_capture elec = new Szh_electronic_capture();
        elec.setFilesize(String.valueOf(filesize));
        elec.setEntryid(entryid);
        elec.setFilepath(basepath);
        elec.setFilename(filename);
        elec.setFiletype(filename.substring(filename.lastIndexOf('.') + 1));
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.opsForList().rightPush(REDIS_KEY, JSONObject.toJSONString(elec));
    }

    //@Scheduled(fixedRate = 30000)
    @Modifying
    @Transactional
    public synchronized void batchInsert(){
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        long sum = redisTemplate.opsForList().size(REDIS_KEY);
        String sql = "insert into szh_electronic_capture (eleid,entryid,filename,filepath,filesize,filetype) values (replace(uuid(),'-',''),?,?,?,?,?)";
        Query query = entityManager.createNativeQuery(sql);
        Szh_electronic_capture ele = null;
        for (int i = 0; i < sum; i++) {
            String redisdata = (String)redisTemplate.opsForList().leftPop(REDIS_KEY);
            ele = JSONObject.parseObject(redisdata, Szh_electronic_capture.class);
            query.setParameter(1, ele.getEntryid());
            query.setParameter(2, ele.getFilename());
            query.setParameter(3, ele.getFilepath());
            query.setParameter(4, ele.getFilesize());
            query.setParameter(5, ele.getFiletype());
            query.executeUpdate();

            Szh_entry_index_capture index = szhEntryIndexCaptureRepository.findByEntryid(ele.getEntryid());
            if (index.getEleid() == null) {
                index.setEleid(String.valueOf(1));
            } else {
                int num = Integer.parseInt(index.getEleid().trim());
                index.setEleid(String.valueOf(num + 1));
            }
            if (index.getPages() == null) {
                index.setPages(String.valueOf(1));
            } else {
                int num = Integer.parseInt(index.getPages().trim());
                index.setPages(String.valueOf(num + 1));
            }
            entityManager.persist(index);
            szhCalloutEntryRepository.pageIncrease(ele.getEntryid());
        }
        entityManager.flush();
        entityManager.clear();
    }

}
