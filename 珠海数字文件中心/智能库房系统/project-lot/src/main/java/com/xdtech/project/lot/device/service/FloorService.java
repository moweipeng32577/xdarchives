package com.xdtech.project.lot.device.service;

import com.xdtech.component.storeroom.repository.ZonesRepository;
import com.xdtech.project.lot.device.entity.Floor;
import com.xdtech.project.lot.device.repository.FloorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * 楼层管理业务类
 * Created by wujy on 2019-09-04
 */
@Service
@Transactional
public class FloorService {

    @Autowired
    ZonesRepository zonesRepository;
   @Autowired
    FloorRepository floorRepository;

    public List<Floor> getFloors() {
        return floorRepository.findAll(new Sort("floorName"));
    }
}
