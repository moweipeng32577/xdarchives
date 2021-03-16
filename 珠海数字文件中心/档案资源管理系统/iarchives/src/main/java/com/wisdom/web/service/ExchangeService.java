package com.wisdom.web.service;

import com.wisdom.web.entity.Tb_exchange_reception;
import com.wisdom.web.repository.ExchangeReceptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by yl on 2017/11/4.
 * 数据交换平台模块service
 */
@Service
@Transactional
public class ExchangeService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ExchangeReceptionRepository exchangeReceptionRepository;

    @PersistenceContext
    EntityManager entityManager;

    public Tb_exchange_reception saveExchange(Tb_exchange_reception tb_exchange_reception) {
        return exchangeReceptionRepository.save(tb_exchange_reception);
    }

    //使用不了jpa，因为jpa做不了hql(不查filedata字段的hql)分页+检索条件查询
    public List getExchange(int start, int limit, String condition, String operator, String
            content) {
        String countSql = "SELECT count(*) from tb_exchange_reception ex";
        String hql = "SELECT ex.exchangeid,ex.filename,ex.filemd5,round(ex.filesize/1024,2),ex.fileTime FROM " +
                "tb_exchange_reception ex";
        String contentSql = "";
        if (content != null) {
            String[] conditions = condition.split(",");
            String[] operators = operator.split(",");
            String[] contents = content.split(",");
            for (int i = 0; i < contents.length; i++) {
                if (i == 0) {
                    contentSql += " where " + conditions[i] + operatorContent(operators[i], contents[i]);
                } else {
                    contentSql += " and " + conditions[i] + operatorContent(operators[i], contents[i]);
                }
            }
        }
        Query qCount = entityManager.createNativeQuery(countSql + contentSql);
        int count = Integer.valueOf(qCount.getSingleResult().toString());
//        hql += (contentSql + " order by ex.fileTime desc limit " + start + "," + limit);
        hql += (contentSql + " order by ex.fileTime desc");
        Query query = entityManager.createNativeQuery(hql);
        query.setFirstResult(start);
        query.setMaxResults(limit);
        List list = new ArrayList();
        list.add(count);
        list.add(query.getResultList());
        return list;
    }


    public String operatorContent(String operator, String content) {
        String operatorContent = "";
        if ("equal".equals(operator)) {//等于
            operatorContent = " ='" + content + "'";
        } else if ("greaterThan".equals(operator)) {//大于
            operatorContent = " > " + content;
        } else if ("lessThan".equals(operator)) {//小于
            operatorContent = " < " + content;
        } else {//默认类似与
            operatorContent = " like '%" + content + "%'";
        }
        return operatorContent;
    }

    public Tb_exchange_reception findByExchangeid(String exchangeid) {
        return exchangeReceptionRepository.findByExchangeid(exchangeid);
    }

    public Integer deleteExchange(String[] exchangeids) {
        return exchangeReceptionRepository.deleteByExchangeidIn(exchangeids);
    }
}
