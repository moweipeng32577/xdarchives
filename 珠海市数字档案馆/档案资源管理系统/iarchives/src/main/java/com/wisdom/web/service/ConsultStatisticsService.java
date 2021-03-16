package com.wisdom.web.service;

import com.wisdom.util.DBCompatible;
import com.wisdom.util.GainField;
import com.wisdom.web.entity.*;
import com.wisdom.web.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Leo on 2020/7/3 0003.
 */
@Service
@Transactional
public class ConsultStatisticsService {

    @Autowired
    ConsultStatisticsRepository consultStatisticsRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    EntryIndexRepository entryIndexRepository;

    @Autowired
    NodesettingService nodesettingService;

    @Autowired
    BorrowDocRepository borrowDocRepository;

    @Autowired
    BorrowMsgRepository borrowMsgRepository;

    @Autowired
    FileNoneRepository fileNoneRepository;

    public Page<ConsultStandingBookVo> listConsultStatistics(int page, int limit, String sort,String startdate,String enddata){
        Sort sortobj = WebSort.getSortByJson(sort);
        PageRequest pageRequest = new PageRequest(page-1,limit,sortobj);

        String sortStr = "";//排序
        String sqlString="select * from tb_consult_statistics ";
        String str="";
        if(startdate!=null||enddata!=null){ //日期过滤
            str+="where ";
            if(!"".equals(startdate)){
                str+="datetime>='"+startdate+"'";
            }
            if(!"".equals(startdate)&&!"".equals(enddata)){
                str+=" and ";
            }
            if(!"".equals(enddata)){
                str+=" datetime<='"+enddata+"'";
            }
        }
        if (sort != null && sortobj.iterator().hasNext()) {    //排序（默认按类型和时间）
            Sort.Order order = sortobj.iterator().next();
            sortStr = " order by " + order.getProperty() + " " + order.getDirection();
        } else {
            sortStr = " order by datetime desc ";
        }
        Query countQuery = entityManager.createNativeQuery("select count(1) from Tb_consult_statistics "+str);
        int count = Integer.parseInt(countQuery.getResultList().get(0) + "");
        Query query = entityManager.createNativeQuery(DBCompatible.getInstance().sqlPages(sqlString+str+sortStr,page-1,limit*11), Tb_consult_statistics.class);
        List<Tb_consult_statistics> list = query.getResultList();
        entityManager.clear();//清空缓存
        return new PageImpl(mergeStatistics(list),pageRequest,count/11);
    }

    //合并同一天的数据
    public List<ConsultStandingBookVo> mergeStatistics(List<Tb_consult_statistics> list){
        //按key（日期）降序排序
        Map<String,ConsultStandingBookVo> voMap= new TreeMap<String, ConsultStandingBookVo>(
                new MapKeyComparator());

        list.stream().forEach(consult->{
            ConsultStandingBookVo vo=new ConsultStandingBookVo();
            if(voMap.get(consult.getDatetime())!=null){
                vo=voMap.get(consult.getDatetime());
            }
            vo.setDate(consult.getDatetime());
            if("文书档案".equals(consult.getType())){
                vo.setWsda("单位 "+consult.getCompany()+",个人 "+consult.getPersonal()
                        +",卷 "+consult.getVolume()+",件 "+consult.getPiece()+",复印 "+consult.getTocopy());
            }else if("婚姻档案".equals(consult.getType())){
                vo.setHyda("单位 "+consult.getCompany()+",个人 "+consult.getPersonal()
                        +",卷 "+consult.getVolume()+",复印 "+consult.getTocopy()+",证明 "+consult.getProve());
            }else if("退伍档案".equals(consult.getType())){
                vo.setTwda("单位 "+consult.getCompany()+",个人 "+consult.getPersonal()
                        +",卷 "+consult.getVolume()+",复印 "+consult.getTocopy());
            }else if("人员/已故人员档案".equals(consult.getType())){
                vo.setRyda("单位 "+consult.getCompany()+",个人 "+consult.getPersonal()
                        +",卷 "+consult.getVolume()+",件 "+consult.getPiece()+",复印 "+consult.getTocopy());
            }else if("土地档案".equals(consult.getType())){
                vo.setTdda("单位 "+consult.getCompany()+",个人 "+consult.getPersonal()
                        +",卷 "+consult.getVolume()+",复印 "+consult.getTocopy());
            }else if("林政档案".equals(consult.getType())){
                vo.setLzda("单位 "+consult.getCompany()+",个人 "+consult.getPersonal()
                        +",卷 "+consult.getVolume()+",复印 "+consult.getTocopy());
            }else if("合同档案".equals(consult.getType())){
                vo.setHtda("单位 "+consult.getCompany()+",个人 "+consult.getPersonal()
                        +",卷 "+consult.getVolume()+",复印 "+consult.getTocopy());
            }else if("科技/城建/基建档案".equals(consult.getType())){
                vo.setKjcjda("单位 "+consult.getCompany()+",个人 "+consult.getPersonal()
                        +",卷 "+consult.getVolume()+",复印 "+consult.getTocopy());
            }else if("业务/工龄档案".equals(consult.getType())){
                vo.setYwglda("单位 "+consult.getCompany()+",个人 "+consult.getPersonal()
                        +",卷 "+consult.getVolume()+",复印 "+consult.getTocopy());
            }else if("其他档案/资料".equals(consult.getType())){
                vo.setQtdazl("单位 "+consult.getCompany()+",个人 "+consult.getPersonal()
                        +",卷 "+consult.getVolume()+",复印 "+consult.getTocopy());
            }else if("电话/现场咨询".equals(consult.getType())){
                vo.setDhxc("单位 "+consult.getCompany()+",个人 "+consult.getPersonal());
            }
            voMap.put(consult.getDatetime(),vo);
        });
        return voMap.values().stream().collect(Collectors.toList());
    }

    public List<Tb_consult_statistics> findConsultStatisticsByDateTime(String dateTime){
        entityManager.clear();//清空缓存
        return consultStatisticsRepository.findAllByDatetime(dateTime);
    }

    //添加或修改
    public Integer addConsultStatistics(ConsultStatistics consultStatistics){
        List<Tb_consult_statistics> list=new ArrayList<>();
        Integer result=0;
        for(int i=0;i<consultStatistics.getCompany().length;i++){
            //根据类型和日期判断统计过则修改
            if(consultStatisticsRepository.findAllByDatetimeAndType(consultStatistics.getConsultDate(),consultStatistics.getType()[i])!=null){
                 result= consultStatisticsRepository.updateConsultStatistics(
                        consultStatistics.getConsultDate(), consultStatistics.getType()[i],
                        consultStatistics.getCompany()[i],consultStatistics.getPersonal()[i],
                        consultStatistics.getVolume()[i],consultStatistics.getPiece()[i],
                        consultStatistics.getTocopy()[i],consultStatistics.getProve()[i]);
            }else{
                Tb_consult_statistics consult_statistics=new Tb_consult_statistics();
                consult_statistics.setDatetime(consultStatistics.getConsultDate());
                consult_statistics.setType(consultStatistics.getType()[i]);
                consult_statistics.setCompany(consultStatistics.getCompany()[i]);
                consult_statistics.setPersonal(consultStatistics.getPersonal()[i]);
                consult_statistics.setPiece(consultStatistics.getPiece()[i]);
                consult_statistics.setProve(consultStatistics.getProve()[i]);
                consult_statistics.setTocopy(consultStatistics.getTocopy()[i]);
                consult_statistics.setVolume(consultStatistics.getVolume()[i]);
                consult_statistics.setOrderby(i);//添加排序（打印需要）
                list.add(consult_statistics);
            }
        }
        entityManager.clear();//清空缓存
        //没有统计的则添加
        if(consultStatisticsRepository.save(list)!=null){
            result=1;
        }
        return  result;
    }

    public Integer deleteConsultStatistics(String date){
        entityManager.clear();//清空缓存
        return consultStatisticsRepository.deleteAllByDatetime(date);
    }

    //台账统计
    public List<Tb_consult_statistics> consultStatistics(String date){
        //去除日期格式
        String regEx="[-—]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(date);
        String newDate = m.replaceAll("").trim();

        List<Tb_borrowdoc> borrowdocList=borrowDocRepository.findAllByBorrowdate(newDate);//申请单据
        List<Tb_borrowmsg> borrowmsgList=borrowMsgRepository.findAllByBorrowdate(newDate);//单据的条目
        //条目详细
        String[] entryIds = GainField.getFieldValues(borrowmsgList, "entryid");
        List<Tb_entry_index> tbEntryIndexList= entryIndexRepository.findByEntryidIn(entryIds.length == 0 ? new String[] { "" } :entryIds);
        //根据查档类型创建统计map
        Map<String,Tb_consult_statistics> tbConsultStatisticsMap=new HashMap<>();
        String[] consultType=new String[]{"文书档案","婚姻档案","退伍档案","人员/已故人员档案",
                "土地档案","林政档案","合同档案","科技/城建/基建档案","业务/工龄档案","其他档案/资料"};
        for (int i=0;i<consultType.length;i++){
            tbConsultStatisticsMap.put(consultType[i],new Tb_consult_statistics());
        }
        try {
            //根据单据查找条目对应的节点名判断档案的类型
            borrowdocList.stream().forEach(borrowdoc->{
                String borrowType="";
                if(borrowdoc.getType().contains("查档")){//判断申请单据类型
                    borrowType="查档";
                }else if(borrowdoc.getType().contains("打印")){
                    borrowType="打印";
                }
                Map<String,String> nodeMap=new HashMap<>();//条目节点全名称
                for (Tb_borrowmsg tb_borrowmsg : borrowmsgList) {
                    if(borrowdoc.getBorrowcode().equals(tb_borrowmsg.getBorrowcode())){
                        for (Tb_entry_index entry_index : tbEntryIndexList) {
                            if(tb_borrowmsg.getEntryid().trim().equals(entry_index.getEntryid().trim())){
                                String value="";
                                if(nodeMap.get(entry_index.getNodeid())==null){//不存在则查询节点的全称
                                    value=nodesettingService.getNodefullnameLoop(entry_index.getNodeid(), "_", "");
                                }else {//存在则调用
                                    value=nodeMap.get(entry_index.getNodeid());
                                }
                                nodeMap.put(entry_index.getNodeid(),value);
                                judgeConsultStatistics(tbConsultStatisticsMap,value,borrowdoc.getRelationship(),borrowType);
                            }
                        }
                    }
                }
            });
            //处理婚姻档案的证明人次
            Tb_consult_statistics tbConsultStatistics = tbConsultStatisticsMap.get("婚姻档案");
            tbConsultStatistics.setType("婚姻档案");
            int num=fileNoneRepository.findAllByRecodetypeAndTime("婚姻登记档案",newDate).size();
            tbConsultStatistics.setProve(String.valueOf(num));
            tbConsultStatisticsMap.put("婚姻档案",tbConsultStatistics);
        }catch (RuntimeException e){
           e.printStackTrace();
        }
//        Map<String,String> nodeNameMap=new HashMap<>();//存放nodeid-nodeid数据节点的全名
//        tbEntryIndexList.parallelStream().forEach(entry->{
//            String nodeid = entry.getTdn().getNodeid();
//            String nodefullname = nodesettingService.getNodefullnameLoop(nodeid, "_", "");
//            nodeNameMap.put(nodeid,nodefullname);//将新获取的节点全名加入map
//        });
//        for (String s : borrowdocMap.keySet()) {
//            Map<String,Object> map= JSONObject.parseObject(JSON.toJSONString(borrowdocMap.get(s)[1]));
//            for (String nodeId : map.keySet()) {
//
//            }
//        }
        return tbConsultStatisticsMap.values().stream().collect(Collectors.toList());
    }

    /**
     * 根据节点名称统计次数
     * @param consultStatisticsMap 统计的map
     * @param value 节点全称
     * @param docRelationship   查档人员类型
     */
    public void judgeConsultStatistics(Map<String,Tb_consult_statistics> consultStatisticsMap,String value,String docRelationship,String borrowType){
        Tb_consult_statistics consult_statistics=null;
        String node=value;//节点全称
        String docType="";//档案类型
        if(node.contains("文书")){
            consult_statistics=consultStatisticsMap.get("文书档案");
            docType="文书档案";
        }else if(node.contains("合同")){
            consult_statistics=consultStatisticsMap.get("合同档案");
            docType="合同档案";
        }else if(node.contains("婚姻")){
            consult_statistics=consultStatisticsMap.get("婚姻档案");
            docType="婚姻档案";
        }else if(node.contains("退伍")){
            consult_statistics=consultStatisticsMap.get("退伍档案");
            docType="退伍档案";
        }else if(node.contains("人员")){
            consult_statistics=consultStatisticsMap.get("人员档案");
            docType="人员档案";
        }else if(node.contains("土地")){
            consult_statistics=consultStatisticsMap.get("土地档案");
            docType="土地档案";
        }else if(node.contains("林政")){
            consult_statistics=consultStatisticsMap.get("林政档案");
            docType="林政档案";
        }else if(node.contains("科技")||node.contains("城建")||node.contains("基建")){
            consult_statistics=consultStatisticsMap.get("科技/城建/基建档案");
            docType="科技/城建/基建档案";
        }else if(node.contains("科技")||node.contains("城建")||node.contains("基建")){
            consult_statistics=consultStatisticsMap.get("科技/城建/基建档案");
            docType="科技/城建/基建档案";
        }else if(node.contains("业务")||node.contains("工龄")){
            consult_statistics=consultStatisticsMap.get("业务/工龄档案");
            docType="业务/工龄档案";
        }else {
            consult_statistics=consultStatisticsMap.get("其他档案/资料");
            docType="其他档案/资料";
        }
        if(consult_statistics!=null) {
            if(!"".equals(borrowType)) {
                if("查档".equals(borrowType)){
                    if ("夫妻".equals(docRelationship) || "委托人".equals(docRelationship)) {
                        consult_statistics.setPersonal((Integer.parseInt(consult_statistics.getPersonal()) + 1) + "");
                    } else {
                        consult_statistics.setCompany((Integer.parseInt(consult_statistics.getCompany()) + 1) + "");
                    }
                    if (node.contains("案卷")) {
                        consult_statistics.setVolume((Integer.parseInt(consult_statistics.getVolume()) + 1) + "");
                    } else {
                        consult_statistics.setPiece((Integer.parseInt(consult_statistics.getPiece()) + 1) + "");
                    }
                }else if("打印".equals(borrowType)){
                    consult_statistics.setTocopy((Integer.parseInt(consult_statistics.getTocopy()) + 1) + "");
                }
                consult_statistics.setType(docType);
                consultStatisticsMap.put(docType, consult_statistics);
            }
        }
    }


    //排序比较器
    class MapKeyComparator implements Comparator<String>{
        @Override
        public int compare(String str1, String str2) {
            return str2.compareTo(str1);
        }
    }
}
