package com.wisdom.web.service;

import com.wisdom.secondaryDataSource.entity.Tb_entry_index_sx;
import com.wisdom.util.GainField;
import com.wisdom.util.SpecificationUtil;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.EntryIndexRepository;
import com.wisdom.web.repository.UserDataNodeRepository;
import com.wisdom.web.security.SecurityUser;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by RonJiang on 2017/11/3 0003.
 */
@Service
@Transactional
public class ClassifySearchService {
    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    UserDataNodeRepository userDataNodeRepository;

    @Autowired
    UserService userService;

    @Autowired
    NodesettingService nodesettingService;

    @Autowired
    EntryIndexService entryIndexService;

    @Autowired
    EntryService entryService;
    /**
     *  高级检索核心算法
     * @param page                    　　页码
     * @param limit　　　　　　　　　　　分页大小
     * @param condition　　　　　　　　　检索栏检索条件
     * @param operator　　　　　　　　　检索栏检索操作符
     * @param content　　　　　　　　　　检索栏检索内容
     * @param nodeid　　　　　　　　　　选择节点的节点id
     * @param formConditions           表单检索条件
     * @param formOperators　　　　　　表单检索操作符
     * @param daterangedata　　　　　　表单日期范围型控件中数据（若有）
     * @param logic　　　　　　　　　　　表单检索条件之间的逻辑关系(或/与)
     * @param ifSearchLeafNode　　　　　是否检索所选节点的子节点（若选择节点为非叶子节点且此参数为false，则无检索结果）
     * @param ifContainSelfNode　　　　 是否查询出当前非叶子节点及其包含的所有非叶子节点数据
     * @return
     */
    public Page<Tb_entry_index> findBySearch(int page,int limit,String condition,String operator,String content,String nodeid,Tb_entry_index formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic,boolean ifSearchLeafNode,boolean ifContainSelfNode, Sort sort){
        String[] nodeids;
        if(ifSearchLeafNode){
            List<String> nodeidList = nodesettingService.getNodeidLoop(nodeid,ifContainSelfNode,new ArrayList<String>());
            nodeids = new String[nodeidList.size()];
            nodeidList.toArray(nodeids);
        }else{
            nodeids = new String[]{nodeid};
        }
        Specification<Tb_entry_index> searchNodeidCondition = getSearchNodeidIndex(nodeids);
        Specifications specifications = Specifications.where(searchNodeidCondition);
        if (content != null) {
            specifications = addSearchbarCondition(specifications, condition, operator, content);
        }
        Specification<Tb_entry_index> formAdvancedSearch = getFormAdvancedIndexSearch(formConditions,formOperators,logic);
        Specification<Tb_entry_index> dateRangeCondition = getDateRangeIndexCondition(daterangedata);
        PageRequest pageRequest = new PageRequest(page-1,limit,sort == null?new Sort("archivecode"):sort);
        return entryIndexRepository.findAll(specifications.and(formAdvancedSearch).and(dateRangeCondition), pageRequest);
    }

    /**
     *  分类检索核心算法
     * @param page　　　　　　　　　　　　　　页码
     * @param limit　　　　　　　　　　　　　分页大小
     * @param condition　　　　　　　　　　　检索栏检索条件
     * @param operator　　　　　　　　　　　检索栏检索操作符
     * @param content　　　　　　　　　　　　检索栏检索内容
     * @param nodeidStr　　　　　　　　　　！！！选择分类对应的所有节点id（多个id之间用逗号分隔）
     * @param formConditions　　　　　　　　表单检索条件
     * @param formOperators　　　　　　　　　表单检索操作符
     * @param daterangedata　　　　　　　　　表单日期范围型控件中数据（若有）
     * @param logic　　　　　　　　　　　　　表单检索条件之间的逻辑关系(或/与)
     * @param ifSearchLeafNode　　　　　　　是否检索所选节点的子节点（若选择节点为非叶子节点且此参数为false，则无检索结果）
     * @param ifContainSelfNode　　　　　　是否查询出当前非叶子节点及其包含的所有非叶子节点数据
     * @return
     */
    public Page<Tb_entry_index> findByClassifySearch(int page,int limit,String condition,String operator,String content,String nodeidStr,
                                                     Tb_entry_index formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata, String logic,
                                                     boolean ifSearchLeafNode,boolean ifContainSelfNode,Sort sort){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        List<String> qxNodeidList = userService.findDataAuths(userDetails.getUserid());
        List<String> nodeids = new ArrayList<>();
        if(ifSearchLeafNode){//递归获取当前节点的子节点id
            for(String nodeid:nodeidStr.split(",")){
                List<String> nodeidList = nodesettingService.getNodeidLoop(nodeid,ifContainSelfNode,new ArrayList<String>());
                nodeids.addAll(nodeidList);
            }
        }else{
        	if (nodeidStr != null) {
        		nodeids = Arrays.asList(nodeidStr.split(","));
        	}
        }
        //根据用户权限过滤检索节点id
        nodeids = nodeids.parallelStream().filter(nodeid -> qxNodeidList.contains(nodeid)).collect(Collectors.toList());
        int array = 1;
        if(nodeids.size()!=0){
            array =  nodeids.size();
        }
        String[] hasQxNodeidArr = new String[array];
        nodeids.toArray(hasQxNodeidArr);
        Specification<Tb_entry_index> searchNodeidCondition = getSearchNodeidIndex(hasQxNodeidArr);
        Specifications specifications = Specifications.where(searchNodeidCondition);
        if (content != null) {
            specifications = addSearchbarCondition(specifications, condition, operator, content);
        }
        Specification<Tb_entry_index> formAdvancedSearch = getFormAdvancedIndexSearch(formConditions,formOperators,logic);
        Specification<Tb_entry_index> dateRangeCondition = getDateRangeIndexCondition(daterangedata);
        PageRequest pageRequest = new PageRequest(page-1,limit,sort==null?new Sort("archivecode"):sort);
        Page<Tb_entry_index> entry_indices = entryIndexRepository.findAll(specifications.and(formAdvancedSearch).and(dateRangeCondition), pageRequest);

        //return entryIndexRepository.findAll(specifications.and(formAdvancedSearch).and(dateRangeCondition), pageRequest);
        return entry_indices;
    }

    public Page<Tb_index_detail> getEntrys(String nodeid,String condition,String operator,String content,Tb_index_detail formConditions,ExtOperators formOperators,ExtDateRangeData daterangedata,String logic,boolean ifSearchLeafNode,boolean ifContainSelfNode,int page,int limit,String sort){
        Sort sortobj = WebSort.getSortByJson(sort);
        return entryIndexService.getEntries(nodeid,condition,operator,content,formConditions,formOperators,daterangedata,logic,ifSearchLeafNode,ifContainSelfNode,page,limit,sortobj);
        /*List<Tb_entry_index> teiList=list.getContent();
        List<Entry> eList= entryService.getEntrys(teiList);
        PageRequest pageRequest = new PageRequest(page - 1, limit);
        return new PageImpl<Entry>(eList, pageRequest, list.getTotalElements());*/
    }

    /**
     *  分类检索核心算法(利用平台)
     * @param page　　　　　　　　　　　　　　页码
     * @param limit　　　　　　　　　　　　　分页大小
     * @param condition　　　　　　　　　　　检索栏检索条件
     * @param operator　　　　　　　　　　　检索栏检索操作符
     * @param content　　　　　　　　　　　　检索栏检索内容
     * @param nodeidStr　　　　　　　　　　！！！选择分类对应的所有节点id（多个id之间用逗号分隔）
     * @param formConditions　　　　　　　　表单检索条件
     * @param formOperators　　　　　　　　　表单检索操作符
     * @param daterangedata　　　　　　　　　表单日期范围型控件中数据（若有）
     * @param logic　　　　　　　　　　　　　表单检索条件之间的逻辑关系(或/与)
     * @param ifSearchLeafNode　　　　　　　是否检索所选节点的子节点（若选择节点为非叶子节点且此参数为false，则无检索结果）
     * @param ifContainSelfNode　　　　　　是否查询出当前非叶子节点及其包含的所有非叶子节点数据
     * @return
     */
    public Page<Tb_entry_index> findByClassifySearchPlatform(int page,int limit,String condition,String operator,String content,String nodeidStr,
                                                     Tb_entry_index formConditions, ExtOperators formOperators, ExtDateRangeData daterangedata,String openType, String logic,
                                                     boolean ifSearchLeafNode,boolean ifContainSelfNode,Sort sort){
        SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        List<String> qxNodeidList = userService.findDataAuths(userDetails.getUserid());
        List<String> nodeids = new ArrayList<>();
        if(ifSearchLeafNode){//递归获取当前节点的子节点id
            for(String nodeid:nodeidStr.split(",")){
                List<String> nodeidList = nodesettingService.getNodeidLoop(nodeid,ifContainSelfNode,new ArrayList<String>());
                nodeids.addAll(nodeidList);
            }
        }else{
            nodeids = Arrays.asList(nodeidStr.split(","));
        }
        //根据用户权限过滤检索节点id
//        nodeids = nodeids.parallelStream().filter(nodeid -> qxNodeidList.contains(nodeid)).collect(Collectors.toList());
        int array = 1;
        if(nodeids.size()!=0){
            array =  nodeids.size();
        }
        String[] hasQxNodeidArr = new String[array];
        nodeids.toArray(hasQxNodeidArr);
        Specification<Tb_entry_index> searchNodeidCondition = getSearchNodeidIndex(hasQxNodeidArr);
        Specifications specifications = Specifications.where(searchNodeidCondition);
        if (content != null) {
            specifications = addSearchbarCondition(specifications, condition, operator, content);
        }
        Specification<Tb_entry_index> formAdvancedSearch = getFormAdvancedIndexSearch(formConditions,formOperators,logic);
        Specification<Tb_entry_index> dateRangeCondition = getDateRangeIndexCondition(daterangedata);
        PageRequest pageRequest = new PageRequest(page-1,limit,sort==null?new Sort("archivecode"):sort);
        return entryIndexRepository.findAll(specifications.and(formAdvancedSearch).and(getSearchOpenCondition(openType)).and(dateRangeCondition), pageRequest);
    }


    public static Specification<Tb_entry_index> getSearchOpenCondition(String opentype) {
        Specification<Tb_entry_index> searchOpenCondition = new Specification<Tb_entry_index>() {
            @Override
            public Predicate toPredicate(Root<Tb_entry_index> root, CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder criteriaBuilder) {
                CriteriaBuilder.In in = criteriaBuilder.in(root.get("flagopen"));
                String[] openarr = opentype.split(",");
                for (String ele : openarr) {
                    in.value(ele);
                }
                return criteriaBuilder.or(in);
            }
        };
        return searchOpenCondition;
    }

    //获取对象的所有属性
    public static String[] getFiledName(Object o){
        Field[] fields=o.getClass().getDeclaredFields();
        String[] fieldNames=new String[fields.length];
        for(int i=0;i<fields.length;i++){
            System.out.println(fields[i].getType());
            fieldNames[i]=fields[i].getName();
        }
        return fieldNames;
    }

    public static String getFormAdvancedSearch(Tb_index_detail formConditions, ExtOperators formOperators, String logic, Tb_index_detail_manage ManageformConditions, Tb_entry_index_sx sxformConditions, String type){
        Field[] entryIndexFields = {};
        if(type!=null&&"directory".equals(type)){  //判断是否目录高级检索
            entryIndexFields = ManageformConditions.getClass().getDeclaredFields();//检索字段
        }else if(type!=null&&"management".equals(type)){
            entryIndexFields = formConditions.getClass().getDeclaredFields();//检索字段
        }else if(type!=null&&"soundimage".equals(type)){  //声像系统
            entryIndexFields = sxformConditions.getClass().getDeclaredFields();//检索字段
        }
        /*以下代码作用为将实体Tb_entry_index中没有检索内容的属性去除*/
        int index = 0;
        int detailInt=0;//标记副表字段的存在
        Field[] entryIndexLivingFields = new Field[entryIndexFields.length];
        List<String> excludeFieldList = new ArrayList<>();//排除检索字段集合
        excludeFieldList.add("fscount");//份数
        excludeFieldList.add("kccount");//库存份数
        excludeFieldList.add("nodeid");//节点id
        excludeFieldList.add("tdn");//数据节点对象
        excludeFieldList.add("nodefullname");//数据节点全名
        excludeFieldList.add("eleid");//电子文件数
        /*String[] addField = getFiledName(new Tb_index_detail());
        for(String str:addField){
            excludeFieldList.add(str);
        }*/
        for(int i=0;i<entryIndexFields.length;i++){//遍历实体Tb_entry_index，得到有检索值的属性
            if(excludeFieldList.contains(entryIndexFields[i].getName())){//若反射得到的属性名在排除检索字段集合中，则不获取该属性的内容
                continue;
            }
            String content = "";
            if(type!=null&&"directory".equals(type)){  //判断是否目录高级检索
                content = GainField.getFieldValueByName(entryIndexFields[i].getName(),ManageformConditions)+"";
            }else if(type!=null&&"management".equals(type)){
                content = GainField.getFieldValueByName(entryIndexFields[i].getName(),formConditions)+"";
            }else if(type!=null&&"soundimage".equals(type)){
                content = GainField.getFieldValueByName(entryIndexFields[i].getName(),formConditions)+"";
            }
            if(!("null".equals(content)) && !("".equals(content))){
                entryIndexLivingFields[index] = entryIndexFields[i];//有输入值的属性存入entryIndexLivingFields数组
                index++;
            }
        }
        entryIndexFields = Arrays.copyOf(entryIndexLivingFields,index);
        if(entryIndexFields.length == 0){
            return "";
        }
        //Predicate[] predicates = new Predicate[entryIndexFields.length];//检索表达式，有几个检索字段，则数组长度为几
        String[] predicates=new String[entryIndexFields.length];
        for(int i=0;i<predicates.length;i++){//遍历存放搜索条件的表达式数组
            String condition = entryIndexFields[i].getName();//字段名
            String operator = GainField.getFieldValueByName(entryIndexFields[i].getName()+"OperatorCombo",formOperators)+"";//需检索字段的对应操作符的值
            String content = "";
            if(type!=null&&"directory".equals(type)){  //判断是否目录高级检索
                content = GainField.getFieldValueByName(entryIndexFields[i].getName(),ManageformConditions)+"";//查询条件内容
            }else if(type!=null&&"management".equals(type)){
                content = GainField.getFieldValueByName(entryIndexFields[i].getName(),formConditions)+"";//查询条件内容
            }else if(type!=null&&"soundimage".equals(type)){
                content = GainField.getFieldValueByName(entryIndexFields[i].getName(),formConditions)+"";//查询条件内容
            }
            if(!condition.contains("sparefield")&&checkFilecode(condition)==1){//排除备用字段
                detailInt=1;//存在副表检索字段
            }
            predicates[i]=getOperatorConditions(operator, condition, content);//拼接条件检索
        }
        String formStr=" and( ";
        formStr=formStr+predicates[0]+" ";
        if("and".equals(logic)){//搜索字段之间逻辑关系为：并且
            //return criteriaBuilder.and(predicates);//将数组作为变长参数传入
            for(int i=1;i<predicates.length;i++){
                formStr=formStr+" and "+predicates[i]+" ";
            }
        }else if("or".equals(logic)){//搜索字段之间逻辑关系为：或者
            //return criteriaBuilder.or(predicates);//将数组作为变长参数传入
            for(int i=1;i<predicates.length;i++){
                formStr=formStr+" or "+predicates[i]+" ";
            }
        }
        formStr=formStr+") ";
        return detailInt+formStr;
    }

    //拼接条件检索
    public static String getOperatorConditions(String operator, String condition, String content){
        String predicates="";
        if("like".equals(operator)){//类似于
            predicates = condition+" like '%"+content+"%' ";
        }else if("equal".equals(operator)){//等于
            predicates = condition+"='"+content+"' ";
        }else if("notEqual".equals(operator)) {
            predicates =condition+"<>'"+content+"'";
        }else if("notLike".equals(operator)) {
            predicates = condition +" not like '%"+content+"%'";
        }else if("isNull".equals(operator)) {
            predicates = "(" + condition + " is null or " + condition + " = '')";
        }else if("isNotNull".equals(operator)) {
            predicates = "(" + condition + " is not null or " + condition + " <> '')";
        }else if("greatAndEqual".equals(operator)) {
//            if(isNumeric(content)){
//                predicates = "(" + condition + " > " + content + " or " + condition + " = " + content + ")";
//            }else {
                predicates = "(" + condition + " > '" + content + "' or " + condition + " = '" + content + "')";
            //}
        }else if("lessAndEqual".equals(operator)) {
//            if(isNumeric(content)){
//                predicates = "(" + condition + " < " + content + " or " + condition + " = " + content + ")";
//            }else {
                predicates = "(" + condition + " < '" + content + "' or " + condition + " = '" + content + "')";
            //}
        }else if("greaterThan".equals(operator)) {
//            if(isNumeric(content)){
//                predicates = condition + " > " + content;
//            }else {
                predicates = condition + " > '" + content + "'";
          //  }
        }else if("lessThan".equals(operator)) {
//            if(isNumeric(content)){
//                predicates = condition + " < " + content;
//            }else {
                predicates = condition + " < '" + content + "'";
            //}
        }
        else{
            predicates  = condition+" is not null ";
        }
        return predicates ;
    }

    private static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static String getDateRangeCondition(ExtDateRangeData daterangedata){
        String dateStart = daterangedata.getFiledatestartday();
        String dateEnd = daterangedata.getFiledateendday();
        if(dateStart==null && dateEnd==null){//开始日期和结束日期均为空
            return "";
        }
        String dateStr="";
        boolean beginTrue = dateStart!=null && !"".equals(dateStart);//开始日期不为空
        boolean endTrue = dateEnd!=null && !"".equals(dateEnd);//结束日期不为空
        if(!endTrue && beginTrue){//开始日期不为空，结束日期为空
            //Predicate p = criteriaBuilder.greaterThanOrEqualTo(root.get("filedate"),dateStart);//大于或等于
            dateStr=" and filedate>'"+dateStart+"' ";
        }else if(!beginTrue && endTrue){//开始日期为空，结束日期不为空
            /*redicates[0] = criteriaBuilder.lessThanOrEqualTo(root.get("filedate"),dateEnd);//小于或等于
            predicates[1] = criteriaBuilder.isNotNull(root.get("filedate"));
            predicates[2] = criteriaBuilder.notEqual(root.get("filedate"),"");*/
            dateStr=" and (filedate<'"+dateEnd+"' and filedate is not null and filedate !='') ";
        }else if(beginTrue && endTrue){//开始日期和结束日期均不为空
            //Predicate p = criteriaBuilder.between(root.get("filedate"),dateStart,dateEnd);//介于
            dateStr=" and (filedate between '"+dateStart+"' and '"+dateEnd+"') ";
        }
        return dateStr;
    }

    public Page<Tb_entry_index> getAllEntry(int page, int limit){
        PageRequest pageRequest = new PageRequest(page-1, limit);
        return entryIndexRepository.findAll(pageRequest);
    }

    public Tb_entry_index getEntryIndexByEntryid(String entryid){
        return entryIndexRepository.findByEntryid(entryid);
    }

    public static Specification<Tb_entry_index> getFormAdvancedIndexSearch(Tb_entry_index formConditions, ExtOperators formOperators,String logic){
        Specification<Tb_entry_index> formAdvancedSearch = new Specification<Tb_entry_index>(){
            @Override
            public Predicate toPredicate(Root<Tb_entry_index> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Field[] entryIndexFields = formConditions.getClass().getDeclaredFields();//检索字段
                /*以下代码作用为将实体Tb_entry_index中没有检索内容的属性去除*/
                int index = 0;
                Field[] entryIndexLivingFields = new Field[entryIndexFields.length];
                List<String> excludeFieldList = new ArrayList<>();//排除检索字段集合
                excludeFieldList.add("fscount");//份数
                excludeFieldList.add("kccount");//库存份数
                excludeFieldList.add("nodeid");//节点id
                excludeFieldList.add("tdn");//数据节点对象
                excludeFieldList.add("nodefullname");//数据节点全名
                excludeFieldList.add("eleid");//电子文件数
                for(int i=0;i<entryIndexFields.length;i++){//遍历实体Tb_entry_index，得到有检索值的属性
                    if(excludeFieldList.contains(entryIndexFields[i].getName())){//若反射得到的属性名在排除检索字段集合中，则不获取该属性的内容
                        continue;
                    }
                    String content = GainField.getFieldValueByName(entryIndexFields[i].getName(),formConditions)+"";
                    if(!("null".equals(content)) && !("".equals(content))){
                        entryIndexLivingFields[index] = entryIndexFields[i];//有输入值的属性存入entryIndexLivingFields数组
                        index++;
                    }
                }
                entryIndexFields = Arrays.copyOf(entryIndexLivingFields,index);
                if(entryIndexFields.length == 0){
                    return null;
                }
                Predicate[] predicates = new Predicate[entryIndexFields.length];//检索表达式，有几个检索字段，则数组长度为几
                for(int i=0;i<predicates.length;i++){//遍历存放搜索条件的表达式数组
                    String condition = entryIndexFields[i].getName();//字段名
                    String operator = GainField.getFieldValueByName(entryIndexFields[i].getName()+"OperatorCombo",formOperators)+"";//需检索字段的对应操作符的值
                    String content = GainField.getFieldValueByName(entryIndexFields[i].getName(),formConditions)+"";//查询条件内容
                    if("like".equals(operator)){//类似于
                        predicates[i] = criteriaBuilder.like(root.get(condition),"%"+content+"%");
                    }else if("notLike".equals(operator)){//不类似于
                        predicates[i] = criteriaBuilder.notLike(root.get(condition),condition);
                    }else if("equal".equals(operator)){//等于
                        predicates[i] = criteriaBuilder.equal(root.get(condition),content);
                    }else if("notEqual".equals(operator)){//不等于
                        predicates[i] = criteriaBuilder.notEqual(root.get(condition),condition);
                    }else if("greaterThan".equals(operator)){//大于
                        predicates[i] = criteriaBuilder.greaterThan(root.get(condition),content);
                    }else if("greatAndEqual".equals(operator)){//大于等于
                        predicates[i] = criteriaBuilder.greaterThanOrEqualTo(root.get(condition),content);
                    }else if("lessThan".equals(operator)){//小于
                        predicates[i] = criteriaBuilder.lessThan(root.get(condition),content);
                    }else if("lessAndEqual".equals(operator)){//小于等于
                        predicates[i] = criteriaBuilder.lessThanOrEqualTo(root.get(condition),content);
                    }else if("isNotNull".equals(operator)){//不为空
                        predicates[i] = criteriaBuilder.isNotNull(root.get(condition));
                    }else if("isNull".equals(operator)){//为空
                        predicates[i] = criteriaBuilder.isNull(root.get(condition));
                    }
                }
                if("and".equals(logic)){//搜索字段之间逻辑关系为：并且
                    return criteriaBuilder.and(predicates);//将数组作为变长参数传入
                }else if("or".equals(logic)){//搜索字段之间逻辑关系为：或者
                    return criteriaBuilder.or(predicates);//将数组作为变长参数传入
                }
                return null;
            }
        };
        return formAdvancedSearch;
    }
    
    public static Specification<Tb_entry_index_capture> getFormAdvancedIndexCaptureSearch(Tb_entry_index_capture formConditions, ExtOperators formOperators,String logic){
        Specification<Tb_entry_index_capture> formAdvancedSearch = new Specification<Tb_entry_index_capture>(){
            @Override
            public Predicate toPredicate(Root<Tb_entry_index_capture> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Field[] entryIndexFields = formConditions.getClass().getDeclaredFields();//检索字段
                /*以下代码作用为将实体Tb_entry_index中没有检索内容的属性去除*/
                int index = 0;
                Field[] entryIndexLivingFields = new Field[entryIndexFields.length];
                List<String> excludeFieldList = new ArrayList<>();//排除检索字段集合
                excludeFieldList.add("fscount");//份数
                excludeFieldList.add("kccount");//库存份数
                excludeFieldList.add("nodeid");//节点id
                excludeFieldList.add("tdn");//数据节点对象
                excludeFieldList.add("nodefullname");//数据节点全名
                excludeFieldList.add("eleid");//电子文件数
                for(int i=0;i<entryIndexFields.length;i++){//遍历实体Tb_entry_index，得到有检索值的属性
                    if(excludeFieldList.contains(entryIndexFields[i].getName())){//若反射得到的属性名在排除检索字段集合中，则不获取该属性的内容
                        continue;
                    }
                    String content = GainField.getFieldValueByName(entryIndexFields[i].getName(),formConditions)+"";
                    if(!("null".equals(content)) && !("".equals(content))){
                        entryIndexLivingFields[index] = entryIndexFields[i];//有输入值的属性存入entryIndexLivingFields数组
                        index++;
                    }
                }
                entryIndexFields = Arrays.copyOf(entryIndexLivingFields,index);
                if(entryIndexFields.length == 0){
                    return null;
                }
                Predicate[] predicates = new Predicate[entryIndexFields.length];//检索表达式，有几个检索字段，则数组长度为几
                for(int i=0;i<predicates.length;i++){//遍历存放搜索条件的表达式数组
                    String condition = entryIndexFields[i].getName();//字段名
                    String operator = GainField.getFieldValueByName(entryIndexFields[i].getName()+"OperatorCombo",formOperators)+"";//需检索字段的对应操作符的值
                    String content = GainField.getFieldValueByName(entryIndexFields[i].getName(),formConditions)+"";//查询条件内容
                    if("like".equals(operator)){//类似于
                        predicates[i] = criteriaBuilder.like(root.get(condition),"%"+content+"%");
                    }else if("equal".equals(operator)){//等于
                        predicates[i] = criteriaBuilder.equal(root.get(condition),content);
                    }
                }
                if("and".equals(logic)){//搜索字段之间逻辑关系为：并且
                    return criteriaBuilder.and(predicates);//将数组作为变长参数传入
                }else if("or".equals(logic)){//搜索字段之间逻辑关系为：或者
                    return criteriaBuilder.or(predicates);//将数组作为变长参数传入
                }
                return null;
            }
        };
        return formAdvancedSearch;
    }

    public static String getFormAdvancedIndexDetailSearch(Tb_index_detail formConditions, ExtOperators formOperators,String logic){
        Field[] entryIndexFields = formConditions.getClass().getDeclaredFields();//检索字段
        /*以下代码作用为将实体Tb_entry_index中没有检索内容的属性去除*/
        int index = 0;
        int detailInt=0;//标记副表字段的存在
        Field[] entryIndexLivingFields = new Field[entryIndexFields.length];
        List<String> excludeFieldList = new ArrayList<>();//排除检索字段集合
        excludeFieldList.add("fscount");//份数
        excludeFieldList.add("kccount");//库存份数
        excludeFieldList.add("nodeid");//节点id
        excludeFieldList.add("tdn");//数据节点对象
        excludeFieldList.add("nodefullname");//数据节点全名
        excludeFieldList.add("eleid");//电子文件数
        for(int i=0;i<entryIndexFields.length;i++){//遍历实体Tb_index_detail，得到有检索值的属性
            if(excludeFieldList.contains(entryIndexFields[i].getName())){//若反射得到的属性名在排除检索字段集合中，则不获取该属性的内容
                continue;
            }
            String content = GainField.getFieldValueByName(entryIndexFields[i].getName(),formConditions)+"";
            if(!("null".equals(content)) && !("".equals(content))){
                entryIndexLivingFields[index] = entryIndexFields[i];//有输入值的属性存入entryIndexLivingFields数组
                index++;
            }
        }
        entryIndexFields = Arrays.copyOf(entryIndexLivingFields,index);
        if(entryIndexFields.length == 0){
            return "";
        }
        String[] predicates = new String[entryIndexFields.length];//检索表达式，有几个检索字段，则数组长度为几
        for(int i=0;i<predicates.length;i++){//遍历存放搜索条件的表达式数组
            String condition = entryIndexFields[i].getName();//字段名
            String operator = GainField.getFieldValueByName(entryIndexFields[i].getName()+"OperatorCombo",formOperators)+"";//需检索字段的对应操作符的值
            String content = GainField.getFieldValueByName(entryIndexFields[i].getName(),formConditions)+"";//查询条件内容
            if(checkFilecode(condition)==1){
                detailInt=1;//存在副表检索字段
            }
            predicates[i]=getOperatorConditions(operator, condition, content);
        }
        String searchConditions="";
        if("and".equals(logic)){//搜索字段之间逻辑关系为：并且
            for(String predicate:predicates){//将数组作为变长参数传入
                searchConditions+=" and "+predicate;
            }
            if(!"".equals(searchConditions)){//去掉第一个and
                searchConditions=searchConditions.substring(searchConditions.indexOf("and")+3);
            }
        }else if("or".equals(logic)){//搜索字段之间逻辑关系为：或者
            for(String predicate:predicates){//将数组作为变长参数传入
                searchConditions+=" or "+predicate;
            }
            if(!"".equals(searchConditions)){//去掉第一个or
                searchConditions=searchConditions.substring(searchConditions.indexOf("or")+2);
            }
        }
        if(!"".equals(searchConditions)){
            searchConditions=" and ("+searchConditions+") ";
        }
        return detailInt+searchConditions;
    }

    public static Specification<Tb_entry_index> getSearchNodeidIndex(String[] nodeids){
        Specification<Tb_entry_index> searchNodeID = null;
        if(nodeids!=null && nodeids.length > 0){
            searchNodeID = new Specification<Tb_entry_index>() {
                @Override
                public Predicate toPredicate(Root<Tb_entry_index> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    CriteriaBuilder.In<String> inValue = criteriaBuilder.in(root.get("tdn").get("nodeid"));
                    for(String nodeid : nodeids){
                        inValue.value(nodeid);
                    }
                    return inValue;
                }
            };
        }
        return searchNodeID;
    }

    /**
     * 根据condition、content拼接成where条件sql(仅限LIKE,=,>,<)
     *
     * @param condition 字段
     * @param content   内容
     * @param alias     表的别名 如：te
     * @param operator  条件符号字符串（equal，like，greaterThan，lessThan）
     * @return
     */
    public String getSqlByConditionsto(String condition, String content, String alias, String operator) {
        String str = "";
        if (condition != null && content != null) {
            String[] conditions = condition.split(",");
            String[] contents = content.split(",");
            String[] operators = operator.split(",");
            for (int i = 0; i < contents.length; i++) {
                String[] contentsDatas = new String[]{};//存放两次查询的每一次查询内容中，以空格分隔开的内容
                if(contents[i] != null){
                    contentsDatas = contents[i].split(" ");//切割以空格隔开的多个关键词
                }
                for(int j=0;j<contentsDatas.length;j++){
                    if (i == 0&&j==0) {
                    }else{
                        str += " and ";
                    }
                    if (alias != null && !alias.equals("")) {
                        str += (alias + ".");
                    }
                    if ("equal".equals(operators[i])) {
                        str += conditions[i] + " = '" + contentsDatas[j] + "' ";
                    } else if ("like".equals(operators[i])) {
                        str += conditions[i] + " like '%" + contentsDatas[j] + "%' ";
                    } else if ("greaterThan".equals(operators[i])) {
                        str += conditions[i] + " > '" + contentsDatas[j] + "' ";
                    } else if ("lessThan".equals(operators[i])) {
                        str += conditions[i] + " < '" + contentsDatas[j] + "' ";
                    }
                }

            }
        }
        if(!"".equals(str)){
            str=" and ("+str+")";
        }
        return str;
    }

    /**
     * 根据condition、content拼接成where条件sql(仅限LIKE)  sqlserver全文检索用
     *
     * @param condition 字段
     * @param content   内容
     * @param alias     表的别名 如：te
     * @param operator  条件符号字符串（like）
     * @return  and CONTAINS(sid.title, '测试 AND 采集')
     */
    public String getFullSearchSqlConditions(String condition, String content, String alias, String operator) {
        String str = "";
        if (condition != null && content != null) {
            String[] conditions = condition.split(",");
            String[] contents = content.split(",");
            String[] operators = operator.split(",");
            for (int i = 0; i < contents.length; i++) {
                String[] contentsDatas = new String[]{};//存放两次查询的每一次查询内容中，以空格分隔开的内容
                if(contents[i] != null){
                    contentsDatas = contents[i].split(" ");//切割以空格隔开的多个关键词
                }else{
                    continue;
                }
                str += " and CONTAINS(";
                if (alias != null && !alias.equals("")) {
                    str += (alias + ".");
                }
                str += conditions[i]+", '";
                for(int j=0;j<contentsDatas.length;j++){
                    if(j==0){
                        str +=contentsDatas[j];
                    }else{
                        str +=" AND "+contentsDatas[j];
                    }
                }
                str += "') ";
            }
        }
        return str;
    }

    public static Specification<Tb_entry_index_capture> getSearchNodeidIndexCapture(String[] nodeids){
        Specification<Tb_entry_index_capture> searchNodeID = null;
        if(nodeids!=null && nodeids.length > 0){
            searchNodeID = new Specification<Tb_entry_index_capture>() {
                @Override
                public Predicate toPredicate(Root<Tb_entry_index_capture> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    CriteriaBuilder.In<String> inValue = criteriaBuilder.in(root.get("nodeid"));
                    for(String nodeid : nodeids){
                        inValue.value(nodeid);
                    }
                    return inValue;
                }
            };
        }
        return searchNodeID;
    }
    
    public static Specification<Tb_entry_index> getDateRangeIndexCondition(ExtDateRangeData daterangedata){
        Specification<Tb_entry_index> dateRangeCondition = null;
        String dateStart = daterangedata.getFiledatestartday();
        String dateEnd = daterangedata.getFiledateendday();
        if(dateStart==null && dateEnd==null){//开始日期和结束日期均为空
            return null;
        }
        boolean beginTrue = dateStart!=null && !"".equals(dateStart);//开始日期不为空
        boolean endTrue = dateEnd!=null && !"".equals(dateEnd);//结束日期不为空
        if(!endTrue && beginTrue){//开始日期不为空，结束日期为空
            dateRangeCondition = new Specification<Tb_entry_index>() {
                @Override
                public Predicate toPredicate(Root<Tb_entry_index> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    Predicate p = criteriaBuilder.greaterThanOrEqualTo(root.get("filedate"),dateStart);//大于或等于
                    return criteriaBuilder.and(p);
                }
            };
        }else if(!beginTrue && endTrue){//开始日期为空，结束日期不为空
            dateRangeCondition = new Specification<Tb_entry_index>() {
                @Override
                public Predicate toPredicate(Root<Tb_entry_index> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    Predicate[] predicates = new Predicate[3];
                    predicates[0] = criteriaBuilder.lessThanOrEqualTo(root.get("filedate"),dateEnd);//小于或等于
                    predicates[1] = criteriaBuilder.isNotNull(root.get("filedate"));
                    predicates[2] = criteriaBuilder.notEqual(root.get("filedate"),"");
                    return criteriaBuilder.and(predicates);
                }
            };
        }else if(beginTrue && endTrue){//开始日期和结束日期均不为空
            dateRangeCondition = new Specification<Tb_entry_index>() {
                @Override
                public Predicate toPredicate(Root<Tb_entry_index> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    Predicate p = criteriaBuilder.between(root.get("filedate"),dateStart,dateEnd);//介于
                    return criteriaBuilder.and(p);
                }
            };
        }
        return dateRangeCondition;
    }
    
    public static Specification<Tb_entry_index_capture> getDateRangeIndexCaptureCondition(ExtDateRangeData daterangedata){
        Specification<Tb_entry_index_capture> dateRangeCondition = null;
        String dateStart = daterangedata.getFiledatestartday();
        String dateEnd = daterangedata.getFiledateendday();
        if(dateStart==null && dateEnd==null){//开始日期和结束日期均为空
            return null;
        }
        boolean beginTrue = dateStart!=null && !"".equals(dateStart);//开始日期不为空
        boolean endTrue = dateEnd!=null && !"".equals(dateEnd);//结束日期不为空
        if(!endTrue && beginTrue){//开始日期不为空，结束日期为空
            dateRangeCondition = new Specification<Tb_entry_index_capture>() {
                @Override
                public Predicate toPredicate(Root<Tb_entry_index_capture> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    Predicate p = criteriaBuilder.greaterThanOrEqualTo(root.get("filedate"),dateStart);//大于或等于
                    return criteriaBuilder.and(p);
                }
            };
        }else if(!beginTrue && endTrue){//开始日期为空，结束日期不为空
            dateRangeCondition = new Specification<Tb_entry_index_capture>() {
                @Override
                public Predicate toPredicate(Root<Tb_entry_index_capture> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    Predicate[] predicates = new Predicate[3];
                    predicates[0] = criteriaBuilder.lessThanOrEqualTo(root.get("filedate"),dateEnd);//小于或等于
                    predicates[1] = criteriaBuilder.isNotNull(root.get("filedate"));
                    predicates[2] = criteriaBuilder.notEqual(root.get("filedate"),"");
                    return criteriaBuilder.and(predicates);
                }
            };
        }else if(beginTrue && endTrue){//开始日期和结束日期均不为空
            dateRangeCondition = new Specification<Tb_entry_index_capture>() {
                @Override
                public Predicate toPredicate(Root<Tb_entry_index_capture> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    Predicate p = criteriaBuilder.between(root.get("filedate"),dateStart,dateEnd);//介于
                    return criteriaBuilder.and(p);
                }
            };
        }
        return dateRangeCondition;
    }

    public static Specifications addSearchbarCondition(Specifications specifications, String condition, String operator, String content) {
        if (condition == null || "undefined".equals(condition) || "".equals(condition)) {
            return specifications;
        }
        String[] conditions = condition.split(",");
        String[] operators = operator.split(",");
        String[] contents = content.split(",");
        for (int i = 0; i < contents.length; i++) {
            if("organ".equals(conditions[i])){
                specifications = specifications==null?Specifications.where(getUserByOrganName(conditions[i],operators[i],contents[i])):specifications.and(getUserByOrganName(conditions[i],operators[i],contents[i]));
            }else{
                specifications = specifications==null?Specifications.where(new SpecificationUtil(conditions[i],operators[i],contents[i])):specifications.and(new SpecificationUtil(conditions[i],operators[i],contents[i]));
            }
        }
        return specifications;
    }

    public static Specifications addSearchCondition(Specifications specifications, String condition, String operator, String content) {
        if (condition == null || "undefined".equals(condition) || "".equals(condition)) {
            return specifications;
        }
        String[] conditions = condition.split(",");
        String[] operators = operator.split(",");
        String[] contents = content.split(",");
        for (int i = 0; i < contents.length; i++) {
            if(operators[i].equals("notLike"))
                contents[i] = "%" + contents[i] + "%";
            specifications = specifications==null?Specifications.where(new SpecificationUtil(conditions[i],operators[i],contents[i])):specifications.and(new SpecificationUtil(conditions[i],operators[i],contents[i]));
        }
        return specifications;
    }

    public static Specification getUserByOrganName(String condition,String operator, String content){
        Specification userByOrganName = new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder
                    criteriaBuilder) {
                Predicate predicate = null;
                if("like".equals(operator)){
                    predicate = criteriaBuilder.like(root.get(condition).get("organname"), "%" + content + "%");
                }else if("equal".equals(operator)){
                    predicate = criteriaBuilder.equal(root.get(condition).get("organname"), content);
                }else if("greaterThan".equals(operator)){
                    predicate = criteriaBuilder.greaterThan(root.get(condition).get("organname"), content);
                }else{
                    predicate = criteriaBuilder.lessThan(root.get(condition).get("organname"), content);
                }
                return criteriaBuilder.and(predicate);
            }
        };
        return userByOrganName;
    }

    public static int checkFilecode(String condition){
        String fc_pattern = "^[f][0-5][0-9]";//副表字段
        String[] conditions = condition.split(",");
        for (int i = 0; i < conditions.length; i++) {
            if(conditions[i].matches(fc_pattern)){
                return 1;
            }
        }
        return 0;
    }

    public static Specification<Tb_entry_index_manage> getSearchNodeidManage(String[] nodeids){
        Specification<Tb_entry_index_manage> searchNodeID = null;
        if(nodeids!=null && nodeids.length > 0){
            searchNodeID = new Specification<Tb_entry_index_manage>() {
                @Override
                public Predicate toPredicate(Root<Tb_entry_index_manage> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    CriteriaBuilder.In<String> inValue = criteriaBuilder.in(root.get("tdn").get("nodeid"));
                    for(String nodeid : nodeids){
                        inValue.value(nodeid);
                    }
                    return inValue;
                }
            };
        }
        return searchNodeID;
    }

    public static Specification<Tb_entry_index_manage> getFormAdvancedManageSearch(Tb_entry_index_manage formConditions, ExtOperators formOperators,String logic){
        Specification<Tb_entry_index_manage> formAdvancedSearch = new Specification<Tb_entry_index_manage>(){
            @Override
            public Predicate toPredicate(Root<Tb_entry_index_manage> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Field[] entryIndexFields = formConditions.getClass().getDeclaredFields();//检索字段
                /*以下代码作用为将实体Tb_entry_index中没有检索内容的属性去除*/
                int index = 0;
                Field[] entryIndexLivingFields = new Field[entryIndexFields.length];
                List<String> excludeFieldList = new ArrayList<>();//排除检索字段集合
                excludeFieldList.add("fscount");//份数
                excludeFieldList.add("kccount");//库存份数
                excludeFieldList.add("nodeid");//节点id
                excludeFieldList.add("nodefullname");//数据节点全名
                excludeFieldList.add("eleid");//电子文件数
                for(int i=0;i<entryIndexFields.length;i++){//遍历实体Tb_entry_index，得到有检索值的属性
                    if(excludeFieldList.contains(entryIndexFields[i].getName())){//若反射得到的属性名在排除检索字段集合中，则不获取该属性的内容
                        continue;
                    }
                    String content = GainField.getFieldValueByName(entryIndexFields[i].getName(),formConditions)+"";
                    if(!("null".equals(content)) && !("".equals(content))){
                        entryIndexLivingFields[index] = entryIndexFields[i];//有输入值的属性存入entryIndexLivingFields数组
                        index++;
                    }
                }
                entryIndexFields = Arrays.copyOf(entryIndexLivingFields,index);
                if(entryIndexFields.length == 0){
                    return null;
                }
                Predicate[] predicates = new Predicate[entryIndexFields.length];//检索表达式，有几个检索字段，则数组长度为几
                for(int i=0;i<predicates.length;i++){//遍历存放搜索条件的表达式数组
                    String condition = entryIndexFields[i].getName();//字段名
                    String operator = GainField.getFieldValueByName(entryIndexFields[i].getName()+"OperatorCombo",formOperators)+"";//需检索字段的对应操作符的值
                    String content = GainField.getFieldValueByName(entryIndexFields[i].getName(),formConditions)+"";//查询条件内容
                    if("like".equals(operator)){//类似于
                        predicates[i] = criteriaBuilder.like(root.get(condition),"%"+content+"%");
                    }else if("equal".equals(operator)){//等于
                        predicates[i] = criteriaBuilder.equal(root.get(condition),content);
                    }
                }
                if("and".equals(logic)){//搜索字段之间逻辑关系为：并且
                    return criteriaBuilder.and(predicates);//将数组作为变长参数传入
                }else if("or".equals(logic)){//搜索字段之间逻辑关系为：或者
                    return criteriaBuilder.or(predicates);//将数组作为变长参数传入
                }
                return null;
            }
        };
        return formAdvancedSearch;
    }

    public static Specification<Tb_entry_index_manage> getDateRangeManageCondition(ExtDateRangeData daterangedata){
        Specification<Tb_entry_index_manage> dateRangeCondition = null;
        String dateStart = daterangedata.getFiledatestartday();
        String dateEnd = daterangedata.getFiledateendday();
        if(dateStart==null && dateEnd==null){//开始日期和结束日期均为空
            return null;
        }
        boolean beginTrue = dateStart!=null && !"".equals(dateStart);//开始日期不为空
        boolean endTrue = dateEnd!=null && !"".equals(dateEnd);//结束日期不为空
        if(!endTrue && beginTrue){//开始日期不为空，结束日期为空
            dateRangeCondition = new Specification<Tb_entry_index_manage>() {
                @Override
                public Predicate toPredicate(Root<Tb_entry_index_manage> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    Predicate p = criteriaBuilder.greaterThanOrEqualTo(root.get("filedate"),dateStart);//大于或等于
                    return criteriaBuilder.and(p);
                }
            };
        }else if(!beginTrue && endTrue){//开始日期为空，结束日期不为空
            dateRangeCondition = new Specification<Tb_entry_index_manage>() {
                @Override
                public Predicate toPredicate(Root<Tb_entry_index_manage> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    Predicate[] predicates = new Predicate[3];
                    predicates[0] = criteriaBuilder.lessThanOrEqualTo(root.get("filedate"),dateEnd);//小于或等于
                    predicates[1] = criteriaBuilder.isNotNull(root.get("filedate"));
                    predicates[2] = criteriaBuilder.notEqual(root.get("filedate"),"");
                    return criteriaBuilder.and(predicates);
                }
            };
        }else if(beginTrue && endTrue){//开始日期和结束日期均不为空
            dateRangeCondition = new Specification<Tb_entry_index_manage>() {
                @Override
                public Predicate toPredicate(Root<Tb_entry_index_manage> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    Predicate p = criteriaBuilder.between(root.get("filedate"),dateStart,dateEnd);//介于
                    return criteriaBuilder.and(p);
                }
            };
        }
        return dateRangeCondition;
    }

    public static Specification<Tb_transdoc> getSearchNodeidTransdoc(List<String> nodeids){
        Specification<Tb_transdoc> searchNodeID = null;
        if(nodeids!=null && nodeids.size() > 0){
            searchNodeID = new Specification<Tb_transdoc>() {
                @Override
                public Predicate toPredicate(Root<Tb_transdoc> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    CriteriaBuilder.In<String> inValue = criteriaBuilder.in(root.get("nodeid"));
                    for(String nodeid : nodeids){
                        inValue.value(nodeid);
                    }
                    return inValue;
                }
            };
        }
        return searchNodeID;
    }

    public static Specification<Tb_entry_index_capture> getcollectSearchNodeidIndex(String[] nodeids){
        Specification<Tb_entry_index_capture> searchNodeID = null;
        if(nodeids!=null && nodeids.length > 0){
            searchNodeID = new Specification<Tb_entry_index_capture>() {
                @Override
                public Predicate toPredicate(Root<Tb_entry_index_capture> root, CriteriaQuery<?> criteriaQuery,
                                             CriteriaBuilder criteriaBuilder) {
                    CriteriaBuilder.In<String> inValue = criteriaBuilder.in(root.get("nodeid"));
                    for(String nodeid : nodeids){
                        inValue.value(nodeid);
                    }
                    return inValue;
                }
            };
        }
        return searchNodeID;
    }

    public static Specification<Tb_index_detail_capture> getSearchNodeidIndexcapture(String[] nodeids){
        Specification<Tb_index_detail_capture> searchNodeID = null;
        if(nodeids!=null && nodeids.length > 0){
            searchNodeID = new Specification<Tb_index_detail_capture>() {
                @Override
                public Predicate toPredicate(Root<Tb_index_detail_capture> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    CriteriaBuilder.In<String> inValue = criteriaBuilder.in(root.get("nodeid"));
                    for(String nodeid : nodeids){
                        inValue.value(nodeid);
                    }
                    return inValue;
                }
            };
        }
        return searchNodeID;
    }

    public static Specification<Tb_index_detail_capture> getFormAdvancedIndexDetailSearch(Tb_index_detail_capture formConditions,
                                                                                          ExtOperators formOperators,String logic){
        Specification<Tb_index_detail_capture> formAdvancedSearch = new Specification<Tb_index_detail_capture>(){
            @Override
            public Predicate toPredicate(Root<Tb_index_detail_capture> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Field[] entryIndexFields = formConditions.getClass().getDeclaredFields();//检索字段
                /*以下代码作用为将实体Tb_entry_index中没有检索内容的属性去除*/
                int index = 0;
                Field[] entryIndexLivingFields = new Field[entryIndexFields.length];
                List<String> excludeFieldList = new ArrayList<>();//排除检索字段集合
                excludeFieldList.add("fscount");//份数
                excludeFieldList.add("kccount");//库存份数
                excludeFieldList.add("nodeid");//节点id
                excludeFieldList.add("tdn");//数据节点对象
                excludeFieldList.add("nodefullname");//数据节点全名
                excludeFieldList.add("eleid");//电子文件数
                for(int i=0;i<entryIndexFields.length;i++){//遍历实体Tb_entry_index，得到有检索值的属性
                    if(excludeFieldList.contains(entryIndexFields[i].getName())){//若反射得到的属性名在排除检索字段集合中，则不获取该属性的内容
                        continue;
                    }
                    String content = GainField.getFieldValueByName(entryIndexFields[i].getName(),formConditions)+"";
                    if(!("null".equals(content)) && !("".equals(content))){
                        entryIndexLivingFields[index] = entryIndexFields[i];//有输入值的属性存入entryIndexLivingFields数组
                        index++;
                    }
                }
                entryIndexFields = Arrays.copyOf(entryIndexLivingFields,index);
                if(entryIndexFields.length == 0){
                    return null;
                }
                Predicate[] predicates = new Predicate[entryIndexFields.length];//检索表达式，有几个检索字段，则数组长度为几
                for(int i=0;i<predicates.length;i++){//遍历存放搜索条件的表达式数组
                    String condition = entryIndexFields[i].getName();//字段名
                    String operator = GainField.getFieldValueByName(entryIndexFields[i].getName()+"OperatorCombo",formOperators)+"";//需检索字段的对应操作符的值
                    String content = GainField.getFieldValueByName(entryIndexFields[i].getName(),formConditions)+"";//查询条件内容
                    if("like".equals(operator)){//类似于
                        predicates[i] = criteriaBuilder.like(root.get(condition),"%"+content+"%");
                    }else if("equal".equals(operator)){//等于
                        predicates[i] = criteriaBuilder.equal(root.get(condition),content);
                    }else if("notEqual".equals(operator)) {
                        predicates[i] =criteriaBuilder.notEqual(root.get(condition),content);
                    }else if("notLike".equals(operator)) {
                        predicates[i]= criteriaBuilder.notLike(root.get(condition),"%"+content+"%");
                    }else if("isNull".equals(operator)) {
                        predicates[i]= criteriaBuilder.isEmpty((root.get(condition)));
                    }else if("isNotNull".equals(operator)) {
                        predicates[i]= criteriaBuilder.isNotEmpty((root.get(condition)));
                    }else if("greatAndEqual".equals(operator)) {
                        predicates[i]= criteriaBuilder.greaterThanOrEqualTo(root.get(condition),content);
                    }else if("lessAndEqual".equals(operator)) {
                        predicates[i]= predicates[i]= criteriaBuilder.lessThanOrEqualTo(root.get(condition),content);
                    }
                }
                if("and".equals(logic)){//搜索字段之间逻辑关系为：并且
                    return criteriaBuilder.and(predicates);//将数组作为变长参数传入
                }else if("or".equals(logic)){//搜索字段之间逻辑关系为：或者
                    return criteriaBuilder.or(predicates);//将数组作为变长参数传入
                }
                return null;
            }
        };
        return formAdvancedSearch;
    }
}