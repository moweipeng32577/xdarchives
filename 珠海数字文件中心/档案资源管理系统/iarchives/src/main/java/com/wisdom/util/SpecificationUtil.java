package com.wisdom.util;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created by yl on 2017/11/2.
 */
public class SpecificationUtil<T> implements Specification<T> {
    private String condition;
    private String operator;
    private String content;

    public SpecificationUtil() {
    }

    /**
     *
     * @param condition 字段
     * @param operator 操作符
     * @param content  内容
     */
    public SpecificationUtil(String condition,String operator, String content) {
        this.condition = condition;
        this.operator = operator;
        this.content = content;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = null;
        if ("like".equals(operator)) { //类似与
            predicate = criteriaBuilder.like(root.get(condition), "%" + content + "%");
        } else if ("beginAt".equals(operator)) {//开始于
            predicate = criteriaBuilder.like(root.get(condition), content + "%");
        } else if ("equal".equals(operator)) {//等于
            predicate = criteriaBuilder.equal(root.get(condition), content);
        } else if ("isNull".equals(operator)) {//为空
            Predicate isNUll = criteriaBuilder.isNull(root.get(condition));
            Predicate equal = criteriaBuilder.equal(root.get(condition), "");
            return criteriaBuilder.or(isNUll, equal);
        } else if ("isNotNull".equals(operator)) {//不为空
            Predicate isNotNull = criteriaBuilder.isNotNull(root.get(condition));
            Predicate notEqual = criteriaBuilder.notEqual(root.get(condition), "");
            return criteriaBuilder.and(isNotNull, notEqual);
        } else if ("notLike".equals(operator)) {//不类似于
            predicate = criteriaBuilder.notLike(root.get(condition), content);
        } else if ("notEqual".equals(operator)) {//不等于
            predicate = criteriaBuilder.notEqual(root.get(condition), content);
        } else if ("greaterThan".equals(operator)) {//大于
            predicate = criteriaBuilder.greaterThan(root.get(condition), content);
        } else if ("greaterThanOrEqualTo".equals(operator)) {//大于或等于
            predicate = criteriaBuilder.greaterThanOrEqualTo(root.get(condition), content);
        } else if ("lessThan".equals(operator)) {//小于
            predicate = criteriaBuilder.lessThan(root.get(condition), content);
        } else if ("lessThanOrEqualTo".equals(operator)) {//小于或等于
            predicate = criteriaBuilder.lessThanOrEqualTo(root.get(condition), content);
        }
        return criteriaBuilder.or(predicate);
    }
}
