package com.wisdom.web.service;

import com.wisdom.secondaryDataSource.entity.Tb_data_node_sx;
import com.wisdom.secondaryDataSource.entity.Tb_report_sx;
import com.wisdom.secondaryDataSource.repository.SecondaryDataNodeRepository;
import com.wisdom.secondaryDataSource.repository.SxReportRepository;
import com.wisdom.util.DBCompatible;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.DataNodeRepository;
import com.wisdom.web.repository.ElectronicRepository;
import com.wisdom.web.repository.ReportRepository;
import com.wisdom.web.repository.UserDataNodeRepository;
import com.wisdom.web.security.SecurityUser;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.util.*;

/**
 * Created by RonJiang on 2018/2/27 0027.
 */
@Service
@Transactional
public class ReportService {

    @PersistenceContext(unitName="entityManagerFactorySecondary")
    EntityManager entityManagerSx;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    NodesettingService nodesettingService;

    @Autowired
    DataNodeRepository dataNodeRepository;

    @Autowired
    SxReportRepository sxReportRepository;
    @Autowired
    SecondaryDataNodeRepository secondaryDataNodeRepository;

    @Autowired
    UserDataNodeRepository userDataNodeRepository;

    @Autowired
    ClassifySearchService classifySearchService;

    @Autowired
    ElectronicRepository electronicRepository;

    @Value("${system.document.rootpath}")
    private String rootpath;//系统文件根目录
    @Value("${system.document.reportFullDir}")
    private String reportFullDir;//FineReport报表文件存储目录
    @Value("${system.document.UReportFullDir}")
    private String UReportFullDir;//UReport报表文件存储目录
    @Value("${system.report.server}")
    private String reportServer;//报表服务

    public Page<Tb_report> findBySearch(int page, int limit, String condition, String operator, String content, String[] nodeids,Sort sort) {
        Specification<Tb_report> searchNodeID = null;
        if(nodeids.length > 0){
            //searchNodeID = getSearchNodeidCondition(nodeids);
            if("undefined".equals(nodeids[0])){
                searchNodeID = new Specification<Tb_report>() {
                    @Override
                    public Predicate toPredicate(Root<Tb_report> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        return cb.equal(root.get("reporttype"), nodeids[1]);
                    }
                };
            }else{
                Set<String> fullnameset = new HashSet<>();
                for (String nodeid : nodeids){
                    String fullname = nodesettingService.getNodefullnameLoop(nodeid,"/","");
                    String[] splits = fullname.split("/");
                    String root = "";
                    for (int i = 0; i < splits.length; i++) {
                        root += (root.length()>0?"/":"") + splits[i];
                        fullnameset.add(root);
                    }
                }
                String[] fullnames = new String[fullnameset.size()];
                fullnameset.toArray(fullnames);
                searchNodeID = getSearchNodenameCondition(fullnames);
            }
        }
        Specifications specifications = Specifications.where(searchNodeID);
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        return reportRepository.findAll(specifications, pageRequest);
    }

    public Page<Tb_report_sx> findSxBySearch(int page, int limit, String condition, String operator, String content, String[] nodeids,Sort sort) {
        Specification<Tb_report_sx> searchNodeID = null;
        if(nodeids.length > 0){
            //searchNodeID = getSearchNodeidCondition(nodeids);
            if("undefined".equals(nodeids[0])){
                searchNodeID = new Specification<Tb_report_sx>() {
                    @Override
                    public Predicate toPredicate(Root<Tb_report_sx> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        return cb.equal(root.get("reporttype"), nodeids[1]);
                    }
                };
            }else{
                Set<String> fullnameset = new HashSet<>();
                for (String nodeid : nodeids){
                    String fullname = nodesettingService.getNodefullnameLoop(nodeid,"_","");
                    String[] splits = fullname.split("_");
                    String root = "";
                    for (int i = 0; i < splits.length; i++) {
                        root += (root.length()>0?"_":"") + splits[i];
                        fullnameset.add(root);
                    }
                }
                String[] fullnames = new String[fullnameset.size()];
                fullnameset.toArray(fullnames);
                searchNodeID = getSxSearchNodenameCondition(fullnames);
            }
        }
        Specifications specifications = Specifications.where(searchNodeID);
        if (content != null) {
            specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
        }
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        return sxReportRepository.findAll(specifications, pageRequest);
    }

    public Page<Tb_report> getNodeReport(String[] nodeids, int page, int limit, Sort sort,String condition, String operator, String content) {
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        Set<String> allnodeidsSet = new HashSet<>();
        List<Tb_report> allList = new ArrayList<Tb_report>();
        if ("undefined".equals(nodeids[0])) {
            SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (content != null&&!"".equals(content)) {//无节点时的查询
                String sortSql="";
                if (sort != null && sort.iterator().hasNext()) {    //排序（默认按类型和时间）
                    Sort.Order order = sort.iterator().next();
                    sortSql = " order by " + order.getProperty() + " " + order.getDirection();
                } else {
                    sortSql = " order by reportname desc ";
                }
                String findSql=classifySearchService.getSqlByConditionsto(condition,content,"",operator);//封装查询条件
                List<String> ids = userDataNodeRepository.findByUserid(userDetails.getUserid());//获取用户全部节点
                String sql = "select * from tb_report  where nodeid in('" + String.join("','", ids) + "') " +findSql;
                String countSql="select count(1) from tb_report  where nodeid in('" + String.join("','", ids) + "') " +findSql;
                Query countQuery=entityManager.createNativeQuery(countSql);
                int count = Integer.parseInt(countQuery.getResultList().get(0) + "");
                Query query=entityManager.createNativeQuery(DBCompatible.getInstance().sqlPages(sql+sortSql,page-1,limit), Tb_report.class);
                List<Tb_report> tb_reportList=query.getResultList();
                return new PageImpl(tb_reportList,pageRequest,count);
            }
            return reportRepository.getByNodeidIn(pageRequest, userDetails.getUserid());
        }else if ("publicreportfnid".equals(nodeids[0])) {
            return reportRepository.findByNodeid(pageRequest, nodeids[0]);
        }else {
            boolean state = true;
            String nodeid = nodeids[0];
            allnodeidsSet.add(nodeids[0]); //当前节点
            while (state) {
                Tb_data_node dataNode = dataNodeRepository.findByNodeid(nodeid);
                nodeid = dataNode.getParentnodeid();
                if ("".equals(nodeid)) {
                    state = false;
                } else {
                    allnodeidsSet.add(nodeid); //所有父节点
                }
            }

            String[] allNodeids = new String[allnodeidsSet.size()];
            allnodeidsSet.toArray(allNodeids);
            Specifications specifications =Specifications.where(getSearchNodeidCondition(allNodeids));//当前节点+父节点全部报表
            if (content != null) {
                specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
            }
            Page<Tb_report> reportAll = reportRepository.findAll(specifications,pageRequest);

            List<Tb_report> reportList = reportAll.getContent();
            List<Tb_report> reportRuturn = new ArrayList<>();

            for(Tb_report report: reportList){
                if( report.getNodeid().toString().trim().equals(nodeids[0].toString())  || (  ! report.getNodeid().toString().trim().equals(nodeids[0].toString())  && report.getReporttype().equals("公有报表"))){
                    String fullname = nodesettingService.getNodefullnameLoop_new(report.getNodeid()); //节点的全名
                    report.setNodename(fullname);
                    report.setModul(fullname);

                    reportRuturn.add(report);
                }
            }

            return new PageImpl(reportRuturn, pageRequest, reportAll.getTotalElements());
        }
    }

    public Page<Tb_report_sx> getSxNodeReport(String[] nodeids, int page, int limit, Sort sort) {
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
        Set<String> allnodeidsSet = new HashSet<>();
        List<Tb_report_sx> allList = new ArrayList<Tb_report_sx>();
        if ("undefined".equals(nodeids[0])) {
            SecurityUser userDetails = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            //return sxReportRepository.getByNodeidIn(pageRequest, userDetails.getUserid());

            String sortStr = "";//排序
            int sortInt = 0;//判断是否副表表排序
            if (sort != null && sort.iterator().hasNext()) {
                Sort.Order order = sort.iterator().next();
                sortStr = " order by " + order.getProperty() + " " + order.getDirection();
            }
            String countSql="select count(*) from tb_report r where r.nodeid in (select nodeid from tb_user_data_node where userid = ?1)";
            Query couuntQuery = entityManagerSx.createNativeQuery(countSql);
            int count = Integer.parseInt(couuntQuery.getResultList().get(0) + "");
            List<Tb_index_detail> resultList;
            String sql = "select * from tb_report r where r.nodeid in (select nodeid from tb_user_data_node where userid = ?1) " + sortStr;
            Query query = entityManagerSx.createNativeQuery(sql, Tb_index_detail.class);
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);
            resultList = query.getResultList();
            return new PageImpl(resultList, pageRequest, count);
        }
        else if ("publicreportfnid".equals(nodeids[0])) {
            return sxReportRepository.findByNodeid(pageRequest, nodeids[0]);
        }
        else {
            boolean state = true;
            String nodeid = nodeids[0];
            allnodeidsSet.add(nodeids[0]); //当前节点
            while (state) {
                Tb_data_node_sx dataNode = secondaryDataNodeRepository.findByNodeid(nodeid);
                nodeid = dataNode.getParentnodeid();
                if ("".equals(nodeid)) {
                    state = false;
                } else {
                    allnodeidsSet.add(nodeid); //所有父节点
                }
            }

            String[] allNodeids = new String[allnodeidsSet.size()];
            allnodeidsSet.toArray(allNodeids);

            Page<Tb_report_sx> reportAll = sxReportRepository.findByNodeidIn(pageRequest,allNodeids); //当前节点+父节点全部报表

            List<Tb_report_sx> reportList = reportAll.getContent();
            List<Tb_report_sx> reportRuturn = new ArrayList<>();

            for(Tb_report_sx report: reportList){
                if( report.getNodeid().toString().equals(nodeids[0].toString())  || (  ! report.getNodeid().toString().equals(nodeids[0].toString())  && report.getReporttype().equals("公有报表"))){
                    //String fullname = nodesettingService.getNodefullnameLoop(report.getNodeid(), "_", ""); //节点的全名
                    String fullname = nodesettingService.getNodefullnameLoop_new(report.getNodeid()); //节点的全名
                    report.setNodename(fullname);
                    report.setModul(fullname);

                    reportRuturn.add(report);
                }
            }

            return new PageImpl(reportRuturn, pageRequest, reportAll.getTotalElements());
        }
    }

    public Page<Tb_report> getAllReport(int page, int limit, Sort sort,String[] nodeids,String condition, String operator, String content) {
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
//        Set<String> fullnameset = new HashSet<>();
        if ("undefined".equals(nodeids[0])) {
            return reportRepository.getReportBytype(pageRequest);
        }
        else {
//            for (String nodeid : nodeids){
//                String fullname = nodesettingService.getNodefullnameLoop(nodeid,"_","");
//                String[] splits = fullname.split("_");
//                String root = "";
//                for (int i = 0; i < splits.length; i++) {
//                    root += (root.length()>0?"_":"") + splits[i];
//                    fullnameset.add(root);
//                }
            Specifications specifications =Specifications.where(getSearchNodeidnotEqualCondition("publicreportfnid"));// 除了publicreportfnid的全部报表
            if (content != null) {
                specifications = ClassifySearchService.addSearchbarCondition(specifications, condition, operator, content);
            }
            Page<Tb_report> allReport = reportRepository.findAll(specifications,pageRequest);
            List<Tb_report> allList = allReport.getContent();
            List<Tb_report> reportRuturn = new ArrayList<>();

            for(Tb_report report:allList){
                if( report.getNodeid().toString().equals(nodeids[0].toString())  || (  ! report.getNodeid().toString().equals(nodeids[0].toString())  && report.getReporttype().equals("公有报表"))) {
                    //String fullname = nodesettingService.getNodefullnameLoop(report.getNodeid(), "_", ""); //节点的全名
                    String fullname = nodesettingService.getNodefullnameLoop_new(report.getNodeid()); //节点的全名
                    report.setNodename(fullname);
                    report.setModul(fullname);

                    reportRuturn.add(report);
                }
            }
            return new PageImpl(reportRuturn,pageRequest,allReport.getTotalElements());
        }
    }

    public Page<Tb_report_sx> getSxAllReport(int page, int limit, Sort sort,String[] nodeids) {
        PageRequest pageRequest = new PageRequest(page - 1, limit, sort);
//        Set<String> fullnameset = new HashSet<>();


        if ("undefined".equals(nodeids[0])) {
            return sxReportRepository.getReportBytype(pageRequest);
        }
        else {
//            for (String nodeid : nodeids){
//                String fullname = nodesettingService.getNodefullnameLoop(nodeid,"_","");
//                String[] splits = fullname.split("_");
//                String root = "";
//                for (int i = 0; i < splits.length; i++) {
//                    root += (root.length()>0?"_":"") + splits[i];
//                    fullnameset.add(root);
//                }
            Page<Tb_report_sx> allReport = sxReportRepository.findReportOutElse(pageRequest);// 除了publicreportfnid的全部报表
            List<Tb_report_sx> allList = allReport.getContent();
            List<Tb_report_sx> reportRuturn = new ArrayList<>();

            for(Tb_report_sx report:allList){
                if( report.getNodeid().toString().equals(nodeids[0].toString())  || (  ! report.getNodeid().toString().equals(nodeids[0].toString())  && report.getReporttype().equals("公有报表"))) {
                    //String fullname = nodesettingService.getNodefullnameLoop(report.getNodeid(), "_", ""); //节点的全名
                    String fullname = nodesettingService.getNodefullnameLoop_new(report.getNodeid()); //节点的全名
                    report.setNodename(fullname);
                    report.setModul(fullname);

                    reportRuturn.add(report);
                }
            }
            return new PageImpl(reportRuturn,pageRequest,allReport.getTotalElements());
        }
    }

    public Tb_report getReport(String reportid){
        return reportRepository.findByReportid(reportid);
    }

    public Tb_report_sx getSxReport(String reportid){
        return sxReportRepository.findByReportid(reportid);
    }

    public Tb_report saveReport(Tb_report report){
        return reportRepository.save(report);
    }

    public Tb_report_sx saveSxReport(Tb_report report){
        Tb_report_sx report_sx=new Tb_report_sx();
        BeanUtils.copyProperties(report,report_sx);
        return sxReportRepository.save(report_sx);
    }

    public Map<String, Object> uploadReport(String reportid,String filename){
//        Tb_report report = new Tb_report();
//        if(reportid!=null){
//            Tb_report tb_report = reportRepository.findByReportid(reportid);
//            BeanUtils.copyProperties(tb_report,report);
//            report.setFilename(filename);
//            reportRepository.save(report);
//        }
//        return report.getMap();
        File targetFile = new File(UReportFullDir, filename+".ureport.xml");
        Map<String, Object> map = new HashMap<>();
        Tb_electronic ele = new Tb_electronic();
        ele.setEntryid(reportid == null ? "" : reportid);
        ele.setFilename(filename);
        ele.setFilepath(UReportFullDir);
        ele.setFilesize(String.valueOf(targetFile.length()));
        ele.setFiletype("xml");
        ele = electronicRepository.save(ele);// 保存电子文件
        map = ele.getMap();
        return map;
    }

    /**
     * 删除报表数据，同时删除其对应的报表样式文件
     * @param reportidData
     * @return
     */
    public Integer delReport(String[] reportidData){
        for(String reportid:reportidData){
            deleteRepElectronic(reportid);
        }
        return reportRepository.deleteByReportidIn(reportidData);
    }

    /**
     * 删除报表数据，同时删除其对应的报表样式文件  声像
     * @param reportidData
     * @return
     */
    public Integer delSxReport(String[] reportidData){
        for(String reportid:reportidData){
            deleteSxRepElectronic(reportid);
        }
        return sxReportRepository.deleteByReportidIn(reportidData);
    }

    public Integer deleteReport(String eleid){
        Tb_electronic electronic =  electronicRepository.findByEleid(eleid);
        String filename = electronic.getFilename();
        String dir = getReportStorageDir(filename);
        File file = new File(dir);
        if(file.exists()){//报表记录和报表文件一一对应，执行删除前不判断是否有其它报表记录关联此报表文件
            file.delete();//删除电子文件
        }
        return electronicRepository.deleteByEleidIn(eleid.split(","));
    }

    /**
     * 删除报表样式文件
     * @param reportid
     * @return
     */
    public Integer deleteRepElectronic(String reportid){
        Tb_report tb_report = reportRepository.findByReportid(reportid);
        String filename;
        if(tb_report==null){
            return deleteReport(reportid);
        }else {
            filename = tb_report.getFilename();
        }
        if(filename!=null && !"".equals(filename)){
            String dir = getReportStorageDir(filename);
            File file = new File(dir);
//            Integer filenameCount = reportRepository.findCountByFilename(filename);//查询关联此报表文件的报表记录数
//            if(file.exists() && filenameCount==1){//若此报表文件存在，且只有一条记录关联此报表文件，则执行删除
//                file.delete();//删除电子文件
//            }
            if(file.exists()){//报表记录和报表文件一一对应，执行删除前不判断是否有其它报表记录关联此报表文件
                file.delete();//删除电子文件
            }
            Tb_report report = new Tb_report();
            BeanUtils.copyProperties(tb_report,report);
            report.setFilename("");
            reportRepository.save(report);
            reportid = String.format("%1$-36s",(String) reportid);
            electronicRepository.deleteByEntryid(reportid);//删除关联电子文件记录
            return reportRepository.findCountByReportid(reportid);
        }else{
            return 0;
        }
    }

    /**
     * 删除报表样式文件  声像
     * @param reportid
     * @return
     */
    public Integer deleteSxRepElectronic(String reportid){
        Tb_report_sx tb_report = sxReportRepository.findByReportid(reportid);
        String filename = tb_report.getFilename();
        if(filename!=null && !"".equals(filename)){
            String dir = getReportStorageDir(filename);
            File file = new File(dir);
//            Integer filenameCount = reportRepository.findCountByFilename(filename);//查询关联此报表文件的报表记录数
//            if(file.exists() && filenameCount==1){//若此报表文件存在，且只有一条记录关联此报表文件，则执行删除
//                file.delete();//删除电子文件
//            }
            if(file.exists()){//报表记录和报表文件一一对应，执行删除前不判断是否有其它报表记录关联此报表文件
                file.delete();//删除电子文件
            }
            Tb_report_sx report = new Tb_report_sx();
            BeanUtils.copyProperties(tb_report,report);
            report.setFilename("");
            sxReportRepository.save(report);
            reportid = String.format("%1$-36s",(String) reportid);
            return sxReportRepository.findCountByReportid(reportid);
        }else{
            return 0;
        }
    }

    public Map<String, Object> findReport(String reportid) {
        Tb_report report = reportRepository.findByReportid(reportid);
        if(report!=null){
            return report.getMap();
        }else{
           String entryid = electronicRepository.findEntryidByEleid(reportid);
           report = reportRepository.findByReportid(entryid);
           return report.getMap();
        }

    }

    public String getReportStorageDir(String filename){ //下载，预览报表样式文件路径
        String dir = "";
        if(reportServer.equals("FReport")) {
            dir = reportFullDir + filename;
            if (!dir.endsWith(".cpt")) {
                dir += ".cpt";
            }
        }
        else {
            dir = UReportFullDir + filename;
            if (!dir.endsWith(".ureport.xml")) {
                dir += ".ureport.xml";
            }
        }
        return dir;
    }

    public static Specification<Tb_report> getSearchNodenameCondition(String[] nodenames){
        Specification<Tb_report> searchNodeidCondition = new Specification<Tb_report>() {
            @Override
            public Predicate toPredicate(Root<Tb_report> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate[] predicates = new Predicate[nodenames.length];
                for(int i=0;i<nodenames.length;i++){
                    predicates[i] = criteriaBuilder.equal(root.get("nodename"),nodenames[i]);
                }
                return criteriaBuilder.or(predicates);
            }
        };
        return searchNodeidCondition;
    }

    public static Specification<Tb_report_sx> getSxSearchNodenameCondition(String[] nodenames){
        Specification<Tb_report_sx> searchNodeidCondition = new Specification<Tb_report_sx>() {
            @Override
            public Predicate toPredicate(Root<Tb_report_sx> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate[] predicates = new Predicate[nodenames.length];
                for(int i=0;i<nodenames.length;i++){
                    predicates[i] = criteriaBuilder.equal(root.get("nodename"),nodenames[i]);
                }
                return criteriaBuilder.or(predicates);
            }
        };
        return searchNodeidCondition;
    }

    public static Specification<Tb_report> getSearchNodeidCondition(String[] nodeids){
        Specification<Tb_report> searchNodeidCondition = new Specification<Tb_report>() {
            @Override
            public Predicate toPredicate(Root<Tb_report> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate[] predicates = new Predicate[nodeids.length];
                for(int i=0;i<nodeids.length;i++){
                    predicates[i] = criteriaBuilder.equal(root.get("nodeid"),nodeids[i]);
                }
                return criteriaBuilder.or(predicates);
            }
        };
        return searchNodeidCondition;
    }

    public static Specification<Tb_report> getSearchNodeidnotEqualCondition(String nodeid){
        Specification<Tb_report> searchNodeidnotEqCondition = new Specification<Tb_report>() {
            @Override
            public Predicate toPredicate(Root<Tb_report> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicates = criteriaBuilder.notEqual(root.get("nodeid"),nodeid);
                return criteriaBuilder.or(predicates);
            }
        };
        return searchNodeidnotEqCondition;
    }

}
