package com.xdtech.project.lot.speed.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Rong on 2019-11-15.
 */
@Entity
public class RecordRam {

    @Id
    private Integer sensorIndex;

    public Integer getSensorIndex() {
        return sensorIndex;
    }

    public void setSensorIndex(Integer sensorIndex) {
        this.sensorIndex = sensorIndex;
    }

}
