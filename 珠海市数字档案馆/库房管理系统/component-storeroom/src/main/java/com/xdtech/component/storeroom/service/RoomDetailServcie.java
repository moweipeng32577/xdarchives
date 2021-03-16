package com.xdtech.component.storeroom.service;

import com.xdtech.component.storeroom.entity.ZoneShelves;
import com.xdtech.component.storeroom.entity.Zones;
import com.xdtech.component.storeroom.repository.ZoneShelvesRepository;
import com.xdtech.component.storeroom.repository.ZonesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class RoomDetailServcie {
    @Autowired
    private ZonesRepository zonesRepository;
    @Autowired
    private ZoneShelvesRepository zoneShelvesRepository;

    public Page<Zones> findRooms(Pageable pageable) {
        List content = zonesRepository.findRooms();
        List<Zones> roomsList = new LinkedList<>();
        for (Object obj : content) {
            Object[] objArr = (Object[])obj;
            Zones zone = new Zones();
            zone.setUnitdisplay((String) objArr[0]);
            zone.setRoomdisplay((String) objArr[1]);
            zone.setCapacity((Integer) objArr[2]);
            zone.setUsecapacity((Integer) objArr[3]);
            String usage = (zone.getUsecapacity()*100 / zone.getCapacity() ) + "%";
            zone.setUsage(usage);
            roomsList.add(zone);
        }
        return new PageImpl<>(roomsList,pageable,content.size());
    }

    public Page<Zones> findZones(Pageable pageable, String roomDisplay) {
        List content = zonesRepository.findZonesByRoom(roomDisplay);
        List<Zones> zonesList = new LinkedList<>();
        for (Object obj : content) {
            Object[] objArr = (Object[])obj;
            Zones zone = new Zones();
            zone.setRoomdisplay((String) objArr[0]);
            zone.setZonedisplay((String) objArr[1]);
            zone.setCapacity((Integer) objArr[2]);
            zone.setUsecapacity((Integer) objArr[3]);
            String usage = (zone.getUsecapacity()*100 / zone.getCapacity() ) + "%";
            zone.setUsage(usage);
            zone.setZoneid((String) objArr[4]);
            zone.setDevice((String) objArr[5]);
            zonesList.add(zone);
        }
        return new PageImpl<>(zonesList,pageable,content.size());
    }

    public Page<ZoneShelves> findColumns(Pageable pageable, String zoneid) {
        List content = zoneShelvesRepository.findColumnsByZone(zoneid);
        List<ZoneShelves> zoneShelves = new LinkedList<>();
        for (Object obj : content) {
            Object[] objArr = (Object[])obj;
            ZoneShelves zoneShelve = new ZoneShelves();
            zoneShelve.setZone(new Zones());
            zoneShelve.getZone().setZonedisplay((String) objArr[0]);
            zoneShelve.setCol((String) objArr[1]);
            zoneShelve.setColdisplay((String) objArr[2]);
            zoneShelve.setCapacity((Integer) objArr[3]);
            zoneShelve.setUsecapacity((Integer) objArr[4]);
            String usage = ( zoneShelve.getUsecapacity()*100 /  zoneShelve.getCapacity() ) + "%";
            zoneShelve.setRate(usage);
            zoneShelve.setCapacity((Integer) objArr[3]);
            zoneShelve.setUsecapacity((Integer) objArr[4]);
            zoneShelve.setRate(usage);
            zoneShelves.add(zoneShelve);
        }
        return new PageImpl<>(zoneShelves,pageable,content.size());
    }
}
