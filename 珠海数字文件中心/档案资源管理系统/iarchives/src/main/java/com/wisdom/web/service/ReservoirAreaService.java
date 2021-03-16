package com.wisdom.web.service;

import com.xdtech.component.storeroom.entity.ZoneShelves;
import com.xdtech.component.storeroom.entity.Zones;
import com.xdtech.component.storeroom.repository.ZoneShelvesRepository;
import com.xdtech.component.storeroom.repository.ZonesRepository;
import com.xdtech.project.lot.device.repository.DeviceAreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class ReservoirAreaService {
    @Autowired
    private ZonesRepository zonesRepository;
    @Autowired
    private ZoneShelvesRepository zoneShelvesRepository;
    @Autowired
    private DeviceAreaRepository deviceAreaRepository;

    public List<Zones> findFloorAndRoom(){
        List<Object> objs = zonesRepository.findFloorAndRoom();
        List<Zones> zones = new LinkedList<>();
        for (Object obj : objs) {
            Object[] objArr= (Object[])obj;
            Zones zone = new Zones();
            zone.setFloordisplay((String) objArr[0]);
            zone.setRoomdisplay((String)objArr[1]);
            zones.add(zone);
        }
        return zones;
    }

    public List<ZoneShelves> getZoneShelves(String floordisplay, String roomdisplay, String zonedisplay) {
        Zones zones = zonesRepository.findByFloordisplayAndRoomdisplayAndZonedisplay(floordisplay,roomdisplay,zonedisplay);
        List<ZoneShelves> zoneShelves = new LinkedList<>();
        if(zones != null){
            List<Object> zsList = zoneShelvesRepository.findByZoneidGroupBy(zones.getZoneid());
            for (Object zs : zsList) {
                ZoneShelves z = new ZoneShelves();
                Object[] obj = (Object[]) zs;
                z.setColdisplay((String) obj[0]);
                z.setUsecapacity((int)((long)obj[1]));
                z.setCapacity((int)obj[2]);
                String rate = (float)z.getUsecapacity()/z.getCapacity()+"";
                z.setRate(rate);
                z.setZone(zones);
                zoneShelves.add(z);
            }
        }
        return zoneShelves;
    }
}
