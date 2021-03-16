package com.xdtech.component.storeroom.controller;

import com.xdtech.component.storeroom.entity.ExtMsg;
import com.xdtech.component.storeroom.entity.ZoneShelveMsg;
import com.xdtech.component.storeroom.entity.ZoneShelves;
import com.xdtech.component.storeroom.entity.Zones;
import com.xdtech.component.storeroom.repository.ZonesRepository;
import com.xdtech.component.storeroom.service.ShelvesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 密集架控制器
 *
 *
 * V0.1
 * 初始版本
 * Created by Rong on 2018/4/26.
 */
@Controller
@RequestMapping(value = "/shelves")
public class ShelvesController {

    @Autowired
    private ShelvesService shelvesService;

    @Autowired
    private ZonesRepository zonesRepository;

    /**
     *
     * @return
     */
    @RequestMapping("/main")
    public String shelves(){
        return "/inlet/storeroom/shelves";
    }

    /**
     *
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping(value = "/zones", method = RequestMethod.GET)
    @ResponseBody
    public Page<Zones> findzones(int page, int limit){
        PageRequest pageRequest = new PageRequest(page-1, limit,new Sort("floordisplay","roomdisplay","zonedisplay"));
        Page<Zones> result = shelvesService.findZones(pageRequest);
        return result;
    }

    /**
     * 获取实体盘点所有城区（不重复）
     * @return
     */
    @RequestMapping(value = "/zones/distinct", method = RequestMethod.GET)
    @ResponseBody
    public List<String> findzonesDistinct(){
        List<String> result = shelvesService.findZones();
        return result;
    }

    @RequestMapping(value = "/findZones",method = RequestMethod.GET)
    @ResponseBody
    public List<Zones> findZones(){
        List<String> zones = shelvesService.findZonesDistinct();
        List<Zones> zonesList = new LinkedList<Zones>();
        for (String zone : zones) {
            Zones z = new Zones();
            z.setCitydisplay(zone);
            zonesList.add(z);

        }
        return  zonesList;
    }

    @RequestMapping(value = "/zone/{zoneid}", method = RequestMethod.GET)
    @ResponseBody
    public Page<ZoneShelveMsg> findshelves(@PathVariable String zoneid, int page, int limit){
        //Zones zone = new Zones(zoneid);

        return shelvesService.findZoneDetails(zoneid, page, limit);
    }

    @RequestMapping(value = "/zone", method = RequestMethod.POST)
    @ResponseBody
    public ExtMsg save(Zones zone, int capacity){
        //格式化地区、单位、库房、库区编码：两位数字
        /*zone.setCity(String.format("%02d",Integer.parseInt(zone.getCity())));
        zone.setUnit(String.format("%02d",Integer.parseInt(zone.getUnit())));*/
//        zone.setRoom(String.format("%02d",Integer.parseInt(zone.getRoom())));
        zone.setZone(String.format("%02d",Integer.parseInt(zone.getZone())));
        shelvesService.initZones(zone, capacity);
        return new ExtMsg(true,"增加库房成功",null);
    }

    @RequestMapping("/citys")
    @ResponseBody
    public List<Zones> findCity(){
        List<String> list=new ArrayList<String>();
        list= shelvesService.findCity();
        List<Zones> listZ=new ArrayList<Zones>();
        if(list.size()>0){
            for(int i=0;i<list.size();i++){
                Zones z=new Zones();
                z.setCitydisplay(list.get(i));
                listZ.add(z);
            }
        }
        return listZ;
    }

    @RequestMapping("/units")
    @ResponseBody
    public List<Zones> findUnits(String citydisplay){
        List<String> list=new ArrayList<String>();
        list= shelvesService.findUnitByCity(citydisplay);
        List<Zones> listZ=new ArrayList<Zones>();
        if(list.size()>0){
            for(int i=0;i<list.size();i++){
                Zones z=new Zones();
                z.setUnitdisplay(list.get(i));
                listZ.add(z);
            }
        }
        return listZ;
    }

    @RequestMapping("/rooms")
    @ResponseBody
    public List<Zones> findRoomsByUnit(String citydisplay, String unitdisplay){
        List<String> list=new ArrayList<String>();
        list= shelvesService.findRoomsByUnit(citydisplay, unitdisplay);
        List<Zones> listZ=new ArrayList<Zones>();
        if(list.size()>0){
            for(int i=0;i<list.size();i++){
                Zones z=new Zones();
                z.setRoomdisplay(list.get(i));
                listZ.add(z);
            }
        }
        return listZ;
    }

    @RequestMapping("/xlZones")
    @ResponseBody
    public List<Zones> findZonesByRoom(String citydisplay, String unitdisplay,String roomdisplay){
        return shelvesService.findZonesByRoom(citydisplay, unitdisplay,roomdisplay);
    }

    @RequestMapping("/cols")
    @ResponseBody
    public List<ZoneShelves> findColsByZoneid(String zoneid){
        List<String> list=new ArrayList<String>();

        list= shelvesService.findColsByZoneid(zoneid);
        List<ZoneShelves> listZs=new ArrayList<ZoneShelves>();
        if(list.size()>0){
            for(int i=0;i<list.size();i++){
                ZoneShelves zs=new ZoneShelves();
                zs.setColdisplay(list.get(i));
                listZs.add(zs);
            }
        }
        return listZs;
    }

    @RequestMapping("/sections")
    @ResponseBody
    public List<ZoneShelves> findSectionsByZoneid(String zoneid,String col){
        List<String> list=new ArrayList<String>();

        list= shelvesService.findSectionsByZoneid(zoneid,col);
        List<ZoneShelves> listZs=new ArrayList<ZoneShelves>();
        if(list.size()>0){
            for(int i=0;i<list.size();i++){
                ZoneShelves zs=new ZoneShelves();
                zs.setSectiondisplay(list.get(i));
                listZs.add(zs);
            }
        }
        return listZs;
    }

    @RequestMapping("/layers")
    @ResponseBody
    public List<ZoneShelves> findLayersByZoneid(String zoneid,String col,String section){
        List<String> list=new ArrayList<String>();

        list= shelvesService.findLayersByZoneid(zoneid,col,section);
        List<ZoneShelves> listZs=new ArrayList<ZoneShelves>();
        if(list.size()>0){
            for(int i=0;i<list.size();i++){
                ZoneShelves zs=new ZoneShelves();
                zs.setLayerdisplay(list.get(i));
                listZs.add(zs);
            }
        }
        return listZs;
    }

    @RequestMapping("/sides")
    @ResponseBody
    public List<ZoneShelves> findSidesByZoneid(String zoneid,String col,String section,String layer){
        List<String> list=new ArrayList<String>();

        list= shelvesService.findSidesByZoneid(zoneid,col,section,layer);
        List<ZoneShelves> listZs=new ArrayList<ZoneShelves>();
        if(list.size()>0){
            for(int i=0;i<list.size();i++){
                ZoneShelves zs=new ZoneShelves();
                zs.setSidedisplay(list.get(i));
                listZs.add(zs);
            }
        }
        return listZs;
    }

    @RequestMapping("/shid")
    @ResponseBody
    public List<String> findShid(String zoneid,String col,String section,String layer,String side){
        return shelvesService.findShid(zoneid,col,section,layer,side);

    }

    /**
     * 移库表格格数据构造
     * @param zoneid
     * @return
     */
    @RequestMapping("/zoneshel")
    @ResponseBody
    public String findZoneShel(String zoneid,String coldisplay){
        return shelvesService.findZoneShel(zoneid,coldisplay);
        //return tableJson;
    }


    @RequestMapping("/del")
    @ResponseBody
    public ExtMsg delZoneShel(String zoneid){
        List<ZoneShelves> list=shelvesService.findShelvesHasCapa(zoneid);
        if(list.size()>0){
            return new ExtMsg(true,"库房有存档，不能删除",null);
        }
        shelvesService.delZoneShelves(zoneid);
        return new ExtMsg(true,"库房删除成功",null);
    }

    //根据id获取密集架信息
    @RequestMapping("/getZoneShel")
    @ResponseBody
    public ExtMsg getZoneShel(String zoneid){
        Zones zone=shelvesService.getZoneShel(zoneid);
        if(zone !=null){
            return new ExtMsg(true,"",zone);
        }else{
            return new ExtMsg(false,"",null);
        }
    }

    //根据id判断密集架是否进行了入库出库
    @RequestMapping("/isHasStorages")
    @ResponseBody
    public boolean isHasStorages(String zoneid){
        return shelvesService.isHasStorages(zoneid);
    }

    /*
    *@ 智能库房获取行、列、层
     */
    @RequestMapping(value = "/getCell",method = RequestMethod.GET)
    @ResponseBody
    public String getCell(String zoneid,String col,String side){
        return shelvesService.findZoneShel(zoneid,col,side);
    }
}
