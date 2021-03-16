package com.wisdom.web.service;

import com.wisdom.web.entity.Szh_media_metadata;
import com.wisdom.web.entity.WebSort;
import com.wisdom.web.repository.SzhMediaMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * 流水线管理服务层
 */
@Service
@Transactional
public class MetadataAdminService {

    @Autowired
    SzhMediaMetadataRepository szhMediaMetadataRepository;

    public Page<Szh_media_metadata> getMetadataBySearch(int page, int limit, String sort, String condition, String operator, String content){
        Sort sortobj = WebSort.getSortByJson(sort);
        Specifications sp = null;
        if (content != null) {
            sp = ClassifySearchService.addSearchbarCondition(sp, condition, operator, content);
        }
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(new Sort.Order(Sort.Direction.DESC,"archivecode"));//发布时间降序
        return szhMediaMetadataRepository.findAll(sp, new PageRequest(page - 1, limit,(Sort) (sortobj == null ? new Sort(sorts) : sortobj)));
    }

    public Szh_media_metadata getMetadataForm(String id){
        return szhMediaMetadataRepository.findOne(id);
    }

    public boolean metadataFormSubmit(Szh_media_metadata metadata){
        boolean state = false;
        try{
            szhMediaMetadataRepository.save(metadata);
            state = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return state;
    }

    public boolean delMetadatas(String[] ids){
        boolean state = false;
        try{
            szhMediaMetadataRepository.deleteByIdIn(ids);
            state = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return state;
    }
}
