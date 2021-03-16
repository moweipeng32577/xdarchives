package com.wisdom.web.service;


import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.ExtMsg;
import com.wisdom.web.entity.Tb_watermark;
import com.wisdom.web.repository.WatermarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.util.List;

@Service
@Transactional
public class WatermarkService {

    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录

    @Autowired
    WatermarkRepository watermarkRepository;

    public Page<Tb_watermark> findBySearch(int page, int limit, String condition, String operator, String content, String id) {
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        Specification<Tb_watermark> searchid = new Specification<Tb_watermark>() {
            @Override
            public Predicate toPredicate(Root<Tb_watermark> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate p = criteriaBuilder.equal(root.get("organid"), id);
                return criteriaBuilder.or(p);
            }
        };
        Specifications specifications = Specifications.where(searchid);
        if(content != null){
            String[] conditions = condition.split(",");
            String[] operators = operator.split(",");
            String[] contents = content.split(",");
            for (int i = 0; i < contents.length; i++) {
                specifications = specifications.and(new SpecificationUtil(conditions[i],operators[i],contents[i]));
            }
        }

        return watermarkRepository.findAll(specifications, pageRequest);
    }

    public ExtMsg getWatermark(String id) {
        return new ExtMsg(true,"",watermarkRepository.findOne(id));
    }

    public ExtMsg saveWatermark(Tb_watermark watermark) {
        if(watermark!=null&&"1".equals(watermark.getIsdefault())){
            List<Tb_watermark> watermarks = watermarkRepository.findByOrganid(watermark.getOrganid());
            for(Tb_watermark watermark1:watermarks){watermark1.setIsdefault("0");}
        }
        Tb_watermark watermark1 = watermarkRepository.save(watermark);
        if (watermark1!=null){
            return new ExtMsg(true,"操作成功",null);
        }
        return new ExtMsg(false,"操作失败",null);
    }

    public ExtMsg delWatermarks(String[] ids, String[] paths) {
        for(String path:paths){
            File f = new File(rootpath+path);
            if(f.exists()){f.delete();}
        }
        int i = watermarkRepository.deleteByIdIn(ids);
        return new ExtMsg(true,"成功删除"+i+"条数据",null);
    }

    public Tb_watermark getWatermarkByOrgan(String organId){
        return watermarkRepository.findByOrganidAndIsdefault(organId,"1");
    }
}
