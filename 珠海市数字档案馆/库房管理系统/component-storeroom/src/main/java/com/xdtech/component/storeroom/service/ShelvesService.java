package com.xdtech.component.storeroom.service;

import com.xdtech.component.storeroom.entity.*;
import com.xdtech.component.storeroom.repository.StorageRepository;
import com.xdtech.component.storeroom.repository.ZoneShelvesRepository;
import com.xdtech.component.storeroom.repository.ZonesRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * 密集架管理业务类
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/21.
 */
@Service
@Transactional
public class ShelvesService {

    @Autowired
    private ZonesRepository zonesRepository;

    @Autowired
    private ZoneShelvesRepository zoneShelvesRepository;

    @Autowired
    private MoveWareService moveWareService;

    @Autowired
    StorageRepository storageRepository;

    @PersistenceContext
    EntityManager entityManager;

    /**
     * 密集架初始化
     * 依据设置好的密集架列数、节数、层数初始化存储
     * @param zones         区信息
     * @param capacity      单元格容量
     */
    public void initZones(Zones zones, int capacity){
        //1.初始化密集级区
        //从session获取用户名
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session=request.getSession();
        String nickname=(String)session.getAttribute("nickname");
        String codeunit=(String)session.getAttribute("codeunit");

        if(nickname==null){
        }else{//韶关
            String code=nickname.substring(0,nickname.indexOf("|"));
            String name=nickname.substring(nickname.indexOf("|")+1);
            String cCode=codeunit.substring(0,codeunit.indexOf("|"));
            String cName=codeunit.substring(codeunit.indexOf("|")+1);
            //获取城区名，全宗号，单位名，单位全宗号设置到zones
            String citycode="";
            String city="";
            String unitcode="";
            String unit="";
            if(nickname.startsWith("444")){
                citycode=code;
                city=name;
                unitcode=cCode;
                unit=cName;
            }else{
                unitcode="444"+code;
                unit=name;
                citycode=cCode;
                city=cName;
            }
            zones.setCity(citycode);
            zones.setCitydisplay(city);
            zones.setUnit(unitcode);
            zones.setUnitdisplay(unit);
        }

        if(zones.getZoneid()==null||"".equals(zones.getZoneid())){  //新增
            zones = zonesRepository.save(zones);
        }else{  //修改
            zones = zonesRepository.save(zones);
            //先删除密集架单元格
            zoneShelvesRepository.delZoneShelves(zones.getZoneid());
        }
            //2.初始化密集架单元格
            ZoneShelves cells;
            for(int i=0;i<zones.getCountcol();i++){                //密集架列
                String colcode = String.format("%02d",i+1);
                for(int j=0;j<zones.getCountsection();j++){        //密集架节
                    String seccode = String.format("%02d",j+1);
                    for(int k=0;k<zones.getCountlayer();k++){      //密集架层
                        String laycode = String.format("%02d",k+1);
                        for(int m=0;m<2;m++){       //密集架面
                            String sidecode = String.format("%02d",m+1);
                            cells = new ZoneShelves();
                            cells.setZone(zones);
                            cells.setCol(colcode);
                            cells.setColdisplay(colcode);
                            cells.setSection(seccode);
                            cells.setSectiondisplay(seccode);
                            cells.setLayer(laycode);
                            cells.setLayerdisplay(laycode);
                            cells.setSide(sidecode);
                            cells.setSidedisplay(m==0?"A面":"B面");
                            cells.setCapacity(capacity);
                            cells.setUsecapacity(0);
                            zoneShelvesRepository.save(cells);
                        }
                    }
                }
            }
    }

    /**
     * 密集架修改
     * 主要涉及到别名及存储量修改
     * 暂时不考虑密集架结构变化修改，若涉及到只需通过单元格的增加删除即可
     * @param zone   密集架对象
     * @return  修改后的密集架对象
     */
    public Zones save(Zones zone){
        return zonesRepository.save(zone);
    }

    /**
     * 检索密集架区
     * @return  所有密集架区的集合
     */
    public Page<Zones> findZones(Pageable pageable){
        //从session获取用户名
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session=request.getSession();
        String nickname=(String)session.getAttribute("nickname");
        String codeunit=(String)session.getAttribute("codeunit");
        if(nickname==null){//非韶关
            return zonesRepository.findAll(pageable);
        }
        String code=nickname.substring(0,nickname.indexOf("|"));
        String cCode=codeunit.substring(0,codeunit.indexOf("|"));
        String unitcode="";
        if(nickname.startsWith("444")){
            unitcode=cCode;
        }else{
            unitcode="444"+code;
        }
        List<Zones> result = zonesRepository.findByUnit(unitcode);
        return new PageImpl(result, pageable,result.size());
    }

    /**
     * 实体盘点所有城区
     * @return  实体盘点所有城区
     */
    public List<String> findZones(){
        //从session获取用户名
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session=request.getSession();
        String nickname=(String)session.getAttribute("nickname");
        String codeunit=(String)session.getAttribute("codeunit");
        if(nickname==null){//非韶关
            return zonesRepository.findUnitByCityDistinct();
        }
        String code=nickname.substring(0,nickname.indexOf("|"));
        String cCode=codeunit.substring(0,codeunit.indexOf("|"));
        String unitcode="";
        if(nickname.startsWith("444")){
            unitcode=cCode;
        }else{
            unitcode="444"+code;
        }
        List<String> result = zonesRepository.findByUnitDistinct(unitcode);
        return result;
    }

    /**
     * 检索密集架区的详细设置
     * @param zoneid  密集架区信息
     * @return  区内所有的单元格集合
     */
    public Page<ZoneShelveMsg> findZoneDetails(String zoneid, int page, int limit){
        PageRequest pageRequest = new PageRequest(page-1, limit);
        List<ZoneShelveMsg> list=zoneShelvesRepository.findWithZoneid(zoneid);
        List<ZoneShelveMsg> ListPage=new ArrayList<>();
        int start=(page-1)*limit;//开始编号
        int end=start+limit>list.size()?list.size():start+limit;//截止编号
        for(int i=start;i<end;i++){//设置分页条目，针对此查询的结果集很小可用
            ListPage.add(list.get(i));
        }
        return new PageImpl<ZoneShelveMsg>(ListPage,pageRequest,list.size());
    }

    /**
     * 批量删除
     * @param shids 主键ID字符串，多ID用','分隔
     */
//    public void delete(String shids){
//        zoneShelvesRepository.deleteAllByShidIn(shids.split(","));
//    }

    /**
     * 检索所有库房
     * @return  库房集合
     */
    public List<Zones> findRooms(){
        return zonesRepository.findAllRooms();
    }

    /**
     * 检索库房内的所有密集架区
     * @param zone   库房信息
     * @return  库房的密集架区集合
     */
    public List<Zones> findZoneByRoom(Zones zone){
        return zonesRepository.findAllByCityAndUnitAndRoomOrderByZone(zone.getCity(), zone.getUnit(),zone.getRoom());
    }

    /**
     * 检索密集架区的所有列
     * @param zone   密集架区信息
     * @return  密集架区的列集合
     */
    public List<ZoneShelves> findColByZone(Zones zone){
        return zoneShelvesRepository.findColByZone(zone);
    }

    /**
     * 检索列的所有节
     * @param zoneShelves   密集架列信息
     * @return  密集架列的节集合
     */
//    public List<ZoneShelves> findSectionByCol(ZoneShelves zoneShelves){
//        return zoneShelvesRepository.findSectionByCol(zoneShelves.getCity(), zoneShelves.getUnit(), zoneShelves.getRoom(),
//                zoneShelves.getZone(), zoneShelves.getCol());
//    }

    /**
     * 检索节的所有层
     * @param zoneShelves   密集架节信息
     * @return  密集架节的层集合
     */
//    public List<ZoneShelves> findLayerBySection(ZoneShelves zoneShelves){
//        return zoneShelvesRepository.findLayerBySection(zoneShelves.getCity(), zoneShelves.getUnit(), zoneShelves.getRoom(),
//                zoneShelves.getZone(), zoneShelves.getCol(), zoneShelves.getSection());
//    }

    /**
     * 检索层的所有面
     * @param zoneShelves   密集架层信息
     * @return  密集架层的面集合
     */
//    public List<ZoneShelves> findSideByLayer(ZoneShelves zoneShelves){
//        return zoneShelvesRepository.findSideByLayer(zoneShelves.getCity(), zoneShelves.getUnit(), zoneShelves.getRoom(),
//                zoneShelves.getZone(), zoneShelves.getCol(), zoneShelves.getSection(), zoneShelves.getLayer());
//    }


    /**
     * 查库房城区
     * @param
     * @return  库房的密集架区集合
     */
    public List<String> findCity(){
        //只能选自己所在局或分局的城区
        //从session获取用户名
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session=request.getSession();
        String nickname=(String)session.getAttribute("nickname");
        String codeunit=(String)session.getAttribute("codeunit");

        if(nickname==null){//非韶关
            return zonesRepository.findCitys();
        }
        String code=nickname.substring(0,nickname.indexOf("|"));
        String cCode=codeunit.substring(0,codeunit.indexOf("|"));

        String funds="";
        if(nickname.startsWith("444")){
            funds=code;
        }else{
            funds=cCode;
        }

        return zonesRepository.findCity(funds);
    }

    /**
     * 查库房单位
     * @param
     * @return  库房的密集架区集合
     */
    public List<String> findUnitByCity(String citydisplay){
        //从session获取用户名，只能查自己所在单位的库房
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session=request.getSession();
        String nickname=(String)session.getAttribute("nickname");
        String codeunit=(String)session.getAttribute("codeunit");
        if(nickname==null){//非韶关
            return zonesRepository.findUnitsByCity(citydisplay);
        }
        String code=nickname.substring(0,nickname.indexOf("|"));
        String cCode=codeunit.substring(0,codeunit.indexOf("|"));
        String unitcode="";
        if(nickname.startsWith("444")){
            unitcode=cCode;
        }else{
            unitcode="444"+code;
        }
        return zonesRepository.findUnitByCity(citydisplay,unitcode);
    }

    /**
     * 按已有地区和单位名查询所有库房
     * @param
     * @return  库房的密集架区集合
     */
    public List<String> findRoomsByUnit(String citydisplay, String unitdisplay){
        return zonesRepository.findRoomsByUnit(citydisplay, unitdisplay);
    }


    /**
     * 按已有地区和单位和库房名查询所有区名
     * @param
     * @return  库房的密集架区集合
     */
    public List<Zones> findZonesByRoom(String citydisplay, String unitdisplay, String roomdisplay){
        return zonesRepository.findZonesByRoom(citydisplay, unitdisplay,roomdisplay);
    }

    /**
     * 按已有zoneid查询所有列名
     * @param
     * @return  库房的密集架区集合
     */
    public List<String> findColsByZoneid(String zoneid){
        return zonesRepository.findColsByZoneid(zoneid);
    }

    /**
     * 按已有zoneid查询所有节名
     * @param
     * @return  库房的密集架区集合
     */
    public List<String> findSectionsByZoneid(String zoneid,String col){
        return zonesRepository.findSectionsByZoneid(zoneid,col);
    }

    /**
     * 按已有zoneid查询所有层名
     * @param
     * @return  库房的密集架区集合
     */
    public List<String> findLayersByZoneid(String zoneid,String col,String section){
        return zonesRepository.findLayersByZoneid(zoneid,col,section);
    }

    /**
     * 按已有zoneid查询所有面名
     * @param
     * @return  库房的密集架区集合
     */
    public List<String> findSidesByZoneid(String zoneid,String col,String section,String layer){
        return zonesRepository.findSidesByZoneid(zoneid,col,section,layer);
    }

    /**
     * 按已有zoneid查询shid
     * @param
     * @return  库房的密集架区集合
     */
    public List<String> findShid(String zoneid,String col,String section,String layer,String side){
        return zonesRepository.findShid(zoneid,col,section,layer,side);
    }


    /**
     * 按已有zoneid查询单元格
     * @param
     * @param coldisplay
     * @return  String
     */
   public String findZoneShel(String zoneid, String coldisplay){
        //根据zoneid获取行数
       List<String> layerList =zonesRepository.getLayers(zoneid);

        //如果没有数据，返回空集合
        if(!(layerList.size()>0)){
            return "";
        }
        //根据zoneid获取列数，节数，用列数X节数X面数来构造表格的列值
        String layer="01";
       List<ZoneShelveMsg> colList;
       if(StringUtils.isBlank(coldisplay)){
           colList=zoneShelvesRepository.getCols(zoneid,layer);
       }else{
           coldisplay = String.format("%02d", Integer.valueOf(coldisplay));
           colList=zoneShelvesRepository.getCols(zoneid,layer,coldisplay);
       }

        List<String> colShelList=new ArrayList<String>();
        String colStr="";
        for(int k=0;k<colList.size();k++){
            //ZoneShelves zs=(ZoneShelves)colList.get(k);
            String col=colList.get(k).getCol();
            String section=colList.get(k).getSection();
            String sidedisplay=colList.get(k).getSidedisplay();
            String shel=col+section+sidedisplay;
            String shelDisplay=col+"列"+section+"节"+sidedisplay;
            //colShelList.add(shel);
            colShelList.add(shelDisplay);
            colStr+=shelDisplay+",";
        }
        colStr=colStr.substring(0,colStr.length()-1);

        //获取每行数据后截取合成单元格显示字段
        String tableJson="[";
        //先读取每行数据，根据层数layer分
        for(int i=0;i<layerList.size();i++){
            String lay=layerList.get(i);
            List<ZoneShelveMsg> list;
            if(StringUtils.isBlank(coldisplay)){
                list=zoneShelvesRepository.getShelves(zoneid,lay);
            }else{
                list=zoneShelvesRepository.getShelves(zoneid,lay,coldisplay);
            }
            List<String> shelList=new ArrayList<String>();
            for(int j=0;j<list.size();j++){
                //String col=list.get(j).getCol();
                String layerDis=list.get(j).getLayer();
                //String section=list.get(j).getSection();
                //String sidedisplay=list.get(j).getSidedisplay();
                Integer usecapacity=list.get(j).getUsecapacity();
                Integer capacity=list.get(j).getCapacity();
                //String shid=list.get(j).getShid();
                String shel="";

                //获取库存百分比,0的话不显示
                float num= (float)usecapacity/capacity;
                if(num>0){

                    DecimalFormat nt = new DecimalFormat("0.00%");
                    //设置百分数精确度2即保留两位小数
                    String percentStr= nt.format(num);

                    //获取格式化对象
//                    NumberFormat nt = NumberFormat.getPercentInstance();
                    //设置百分数精确度2即保留两位小数
//                    nt.setMinimumFractionDigits(0);
//                    String percentStr=nt.format(num);

                    //shel=col+layerDis+section+sidedisplay+" "+percentStr;
                    shel=layerDis+"层 "+percentStr;
                    //获取出库数量  暂时屏蔽
                    /*List<Object> stList=storageRepository.findStoragesOutByShid(shid);
                    if(stList.size()>0){
                        shel=shel+" -"+stList.size();
                    }*/

                }else{
                    //shel=col+layerDis+section+sidedisplay;
                    shel=layerDis+"层";
                }

                shelList.add(shel);
            }
            //拼接表格的data数据,用列值和对应的行值
            String rowJson="{";
            for(int m=0;m<colShelList.size();m++){
                String colname=colShelList.get(m);
                String valueName=shelList.get(m);
                rowJson+="'"+colname+"':'"+valueName+"',";
            }
            rowJson=rowJson.substring(0,rowJson.length()-1);
            rowJson+="}";
            tableJson+=rowJson+",";
        }
        tableJson=tableJson.substring(0,tableJson.length()-1);
        tableJson+="]";


        return tableJson+colStr;
    }


    /**
     * 按已有zoneid查询单元格
     * @param
     * @return  String
     */
    public String findZoneShel(String zoneid,String column,String side){
        //根据zoneid获取行数
        List<String> layerList=zonesRepository.getLayers(zoneid);

        //如果没有数据，返回空集合
        if(!(layerList.size()>0)){
            return "";
        }
        //根据zoneid获取列数，节数，用列数X节数X面数来构造表格的列值
        String layer="01";
        List<ZoneShelveMsg> colList;
        if((column == null || StringUtils.isBlank(column)) && (side == null || StringUtils.isNotBlank(side))){
            colList=zoneShelvesRepository.getCols(zoneid,layer);
        }else{
            column = String.format("%02d", Integer.valueOf(column));
            colList=zoneShelvesRepository.getCols(zoneid,layer,column,side);
        }
        List<String> colShelList=new ArrayList<String>();
        String colStr="";
        for(int k=0;k<colList.size();k++){
            //ZoneShelves zs=(ZoneShelves)colList.get(k);
            String col=colList.get(k).getCol();
            String section=colList.get(k).getSection();
            String sidedisplay=colList.get(k).getSidedisplay();
            String shel=col+section+sidedisplay;
            String shelDisplay=col+"列"+section+"节"+sidedisplay;
            //colShelList.add(shel);
            colShelList.add(shelDisplay);
            colStr+=shelDisplay+",";
        }
        colStr=colStr.substring(0,colStr.length()-1);

        //获取每行数据后截取合成单元格显示字段
        String tableJson="[";
        //先读取每行数据，根据层数layer分
        for(int i=0;i<layerList.size();i++){
            String lay=layerList.get(i);
            List<ZoneShelveMsg> list;
            if(StringUtils.isBlank(column) && StringUtils.isBlank(side)){
                list=zoneShelvesRepository.getShelves(zoneid,lay);
            }else{
                list=zoneShelvesRepository.getShelves(zoneid,lay,column,side);
            }
            List<String> shelList=new ArrayList<String>();
            for(int j=0;j<list.size();j++){
                String col=list.get(j).getCol();
                String layerDis=list.get(j).getLayer();
                String section=list.get(j).getSection();
                String sidedisplay=list.get(j).getSidedisplay();
                Integer usecapacity=list.get(j).getUsecapacity();
                Integer capacity=list.get(j).getCapacity();
                String shid=list.get(j).getShid();
                String shel="";

                //获取库存百分比,0的话不显示
                float num= (float)usecapacity/capacity;
                if(num>0){
                    //获取格式化对象
//                    NumberFormat nt = NumberFormat.getPercentInstance();
                    //设置百分数精确度2即保留两位小数
//                    nt.setMinimumFractionDigits(0);
//                    String percentStr=nt.format(num);

                    DecimalFormat nt = new DecimalFormat("0.00%");
                    //设置百分数精确度2即保留两位小数
                    String percentStr= nt.format(num);

                    //shel=col+layerDis+section+sidedisplay+" "+percentStr;
                    shel=layerDis+"层 "+percentStr;
                    //获取出库数量 暂时屏蔽
                    /*List<String> stList=storageRepository.findStoragesOutByShid1(shid);
                    if(stList.size()>0){
                        shel=shel+" -"+stList.size();
                    }*/

                }else{
                    //shel=col+layerDis+section+sidedisplay;
                    shel=layerDis+"层";
                }

                shelList.add(shel);
            }
            //拼接表格的data数据,用列值和对应的行值
            String rowJson="{";
            for(int m=0;m<colShelList.size();m++){
                String colname=colShelList.get(m);
                String valueName=shelList.get(m);
                rowJson+="'"+colname+"':'"+valueName+"',";
            }
            rowJson=rowJson.substring(0,rowJson.length()-1);
            rowJson+="}";
            tableJson+=rowJson+",";
        }
        tableJson=tableJson.substring(0,tableJson.length()-1);
        tableJson+="]";


        return tableJson+colStr;
    }

    /**
     * 单元格移库操作
     * @param sourceStr
     * @param targetStr
     */
    public void changeShel(String sourceStr, String targetStr,String user,String zoneid){
        //根据sourceStr, targetStr分别找到对应的shid，对它们的库存进行增减设置 01列01节A面01层
        String tarcol=targetStr.substring(0,2);
        String tarlayer=targetStr.substring(8,10);
        String tarsection=targetStr.substring(3,5);
        String tarsidedisplay=targetStr.substring(6,targetStr.indexOf("面")+1);

        String col=sourceStr.substring(0,2);
        String layer=sourceStr.substring(8,10);
        String section=sourceStr.substring(3,5);
        String sidedisplay=sourceStr.substring(6,sourceStr.indexOf("面")+1);

        ZoneShelves source = zoneShelvesRepository.findByDisplay(zoneid,col,section,layer,sidedisplay);
        ZoneShelves target = zoneShelvesRepository.findByDisplay(zoneid,tarcol,tarsection,tarlayer,tarsidedisplay);

        //重新标记档案系统的对应条目的库存位置,直接写了tb_intry_index
        String zsMsg="";
        String zoneMsg="";
        zsMsg=target.getColdisplay()+"列-"+target.getSectiondisplay()+"节-"+target.getLayerdisplay()+"层-"+target.getSidedisplay();
        Zones zone=target.getZone();
        zoneMsg=zone.getCitydisplay()+"-"+zone.getUnitdisplay()+"-"+zone.getRoomdisplay()+"-"+zone.getZonedisplay()+"-";
        String savePosition=zoneMsg+zsMsg;//详细移库位置
        //获取原单元格的所有的电子条目的entryid
        String[] entryids = storageRepository.findByShid(source.getShid());
        String[] targetEntryids = storageRepository.findByShid(target.getShid());
        if(entryids.length>0){
            storageRepository.savePosition(entryids,savePosition);
        }

        MoveWare moveWare = new MoveWare();
        moveWare.setWareuser(user);
        moveWare.setDescription("备注一些东西移库");
        List<ZoneShelves> sources = new ArrayList<ZoneShelves>();
        sources.add(source);
        List<ZoneShelves> targets = new ArrayList<ZoneShelves>();
        targets.add(target);
        moveWareService.save(moveWare, sources, targets,entryids.length,targetEntryids.length);
    }


    /**
     * 电子档案移库操作
     * @param targetStr
     */
    public void entryChangeShel(String targetStr,String user,String zoneid,String entryids){
        //根据sourceStr, targetStr分别找到对应的shid，对它们的库存进行增减设置
        String tarcol=targetStr.substring(0,2);
        String tarlayer=targetStr.substring(8,10);
        String tarsection=targetStr.substring(3,5);
        String tarsidedisplay=targetStr.substring(6,targetStr.indexOf("面")+1);

        //ZoneShelves source = zoneShelvesRepository.findByEntryids(entryids);
        ZoneShelves target = zoneShelvesRepository.findByDisplay(zoneid,tarcol,tarsection,tarlayer,tarsidedisplay);
        String[] ids=entryids.split(",");

        //重新标记档案系统的对应条目的库存位置,直接写了tb_intry_index
        String zsMsg="";
        String zoneMsg="";
        zsMsg=target.getColdisplay()+"列-"+target.getSectiondisplay()+"节-"+target.getLayerdisplay()+"层-"+target.getSidedisplay();
        Zones zone=target.getZone();
        zoneMsg=zone.getCitydisplay()+"-"+zone.getUnitdisplay()+"-"+zone.getRoomdisplay()+"-"+zone.getZonedisplay()+"-";
        String savePosition=zoneMsg+zsMsg;//详细移库位置
        //获取原单元格的所有的电子条目的entryid
        storageRepository.savePosition(ids,savePosition);

        MoveWare moveWare = new MoveWare();
        moveWare.setWareuser(user);
        moveWare.setDescription("备注一些东西移库");
        List<ZoneShelves> sources = new ArrayList<ZoneShelves>();
//        sources.add(source);
        List<ZoneShelves> targets = new ArrayList<ZoneShelves>();
        targets.add(target);
        moveWareService.entrySave(moveWare, ids, targets);
    }


    /**
     * 查找usecapacity不为空的shids
     * @param zoneid
     * @return
     */
    public List<ZoneShelves> findShelvesHasCapa(String zoneid){
        return zoneShelvesRepository.findShelvesHasCapa(zoneid);
    }

    /**
     * 获取密集架信息
     * @param zoneid
     * @return
     */
    public Zones getZoneShel(String zoneid){
        return zonesRepository.findByZoneid(zoneid);
    }

    /**
     * 删除指定库房
     * @param zoneid
     */
    public void delZoneShelves(String zoneid){
        //先删除zoneshelves,再删除zone
        zoneShelvesRepository.delZoneShelves(zoneid);
        zonesRepository.delZones(zoneid);

    }

    public List<String> findZonesDistinct() {
        return zonesRepository.findZonesDistinct();
    }

    /**
     * 判断密集架是否进行入库出库
     * @param zoneid
     * @return
     */
    public boolean isHasStorages(String zoneid){
        List<Storage> storages = storageRepository.findByShelves_zone(zoneid);
        if(storages.size()>0){
            return true;
        }else{
            return false;
        }
    }

    /*
    *//**
     * 按已有zoneid查询层数
     * @param
     * @return  库房的密集架区集合
     *//*
    public List<String> getLayers(String zoneid){
        return zonesRepository.getLayers(zoneid);
    }

    *//**
     * 按已有zoneid和层号查询列数
     * @param
     * @return  库房的密集架区集合
     *//*
    public List<ZoneShelves> getCols(String zoneid,String layer){
        return zonesRepository.getCols(zoneid,layer);
    }

    *//**
     * 按已有zoneid和层号查询单元格
     * @param
     * @return  库房的密集架区集合
     *//*
    public List<ZoneShelves> getShelves(String zoneid,String layer){
        return zonesRepository.getShelves(zoneid,layer);
    }*/


    /**
     * 查询密集架总存量和已使用量
     * @return
     * @param ip 密集架ip
     * @param port 密集架端口
     * @param code 密集架区号
     */
    public Map<String, Object> classifyAndUserCapacity(String ip, Integer port, Integer code) {
        Zones zones = null;
        if(StringUtils.isNotBlank(ip) && port != null && code != null){//根据ip，端口和区号查找出指定区的密集架
            String deviceId = this.getMJJDeviceId(ip, port, code);
            zones = zonesRepository.findByDevice(deviceId);
        }
        String  sql = "select storageproperty,sum(usecapacity) as totle from st_zones sz left join st_zone_shelves szs on sz.zoneid = szs.zoneid group by sz.storageproperty having sz.storageproperty is not null";

        if(zones != null){
            sql += " having sz.zoneid = '" + zones.getZoneid() +"'";
        }
        Query query = entityManager.createNativeQuery(sql);
        List list = query.getResultList();

        Map<String, Object> capacityMap = new HashMap<String, Object>();
        for(int i = 0; i<list.size();i++) {
            Object[] o = (Object[]) list.get(i);
            capacityMap.put(o[0].toString(), o[1].toString());
        }

        return capacityMap;
    }

    /**
     * 根据密集架ip、端口和区号查询出密集架设备id
     * @param ip
     * @param port
     * @param code
     * @return
     */
    public String getMJJDeviceId(String ip, Integer port, Integer code){
        String sql = "select de.id from lot_device de where prop like '%"+ip+"%' and " +
                "prop like '%"+port+"%' and prop like '%"+code+"%'";
        Query query = entityManager.createNativeQuery(sql);
        return  (String)query.getSingleResult();
    }

    /**
     * 查询密集架总存量和已使用量
     * @return
     * @param ip 密集架ip
     * @param port 密集架端口
     * @param code 密集架区号
     */
    public Map<String, Object> capacityAndUserCapacity(String ip, Integer port, Integer code) {
        Zones zones = null;
        if(StringUtils.isNotBlank(ip) && port != null && code != null){//根据ip，端口和区号查找出指定区的密集架
            String deviceId = this.getMJJDeviceId(ip, port, code);
            zones = zonesRepository.findByDevice(deviceId);
        }
        String  jql = "select sum(zs.capacity) as total,sum(zs.usecapacity) as use from ZoneShelves zs  where 1=1 ";
        if(zones != null){
            jql += "and zs.zone.zoneid = '" + zones.getZoneid() +"'";
        }
        Query query = entityManager.createQuery(jql);
        Object obj = query.getSingleResult();
        Object[] objArr  = (Object[])obj;
        Map<String, Object> capacityMap = new HashMap<String, Object>();
        long one = Long.valueOf(String.valueOf(objArr[0]));
        capacityMap.put("total", (long)one);//总存量
        long two = Long.valueOf(String.valueOf(objArr[1]));
        capacityMap.put("use", (long)two);//已使用
        capacityMap.put("residue", (long)one-(long)two);//未使用
        return capacityMap;
    }

    public List<ZoneShelves> getZoneShelves(String zoneid) {

        Zones zones =null;
            zones =   zonesRepository.findByZoneid(zoneid);
        List<ZoneShelves> zoneShelves = new LinkedList<ZoneShelves>();
        if(zones != null){
            List<Object> zsList = zoneShelvesRepository.findByZoneidGroupBy(zones.getZoneid());
            for (Object zs : zsList) {
                ZoneShelves z = new ZoneShelves();
                Object[] obj = (Object[]) zs;
                z.setColdisplay((String) obj[0]);
                long one = Long.valueOf(String.valueOf(obj[1]));
                z.setUsecapacity((int)one);
                long two = Long.valueOf(String.valueOf(obj[2]));
                z.setCapacity((int)two);
                DecimalFormat nt = new DecimalFormat("0.00");
                //设置百分数精确度2即保留两位小数
                Float rate= (float) Integer.valueOf(z.getUsecapacity())/Integer.valueOf(z.getCapacity());

                String rateS = rate+"";
                z.setRate(rateS);
//                z.setRate(rate);
                z.setZone(zones);
                zoneShelves.add(z);
            }
        }
        return zoneShelves;
    }
}
