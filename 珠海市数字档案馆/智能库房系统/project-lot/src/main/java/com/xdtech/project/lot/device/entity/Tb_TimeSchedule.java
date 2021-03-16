package com.xdtech.project.lot.device.entity;

import com.alibaba.fastjson.annotation.JSONField;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;


 @Entity
 @Table(name = "tb_Time_Schedule")
public class Tb_TimeSchedule {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid") //生成32位UUID
    @GeneratedValue(generator = "idGenerator")
    @Column(columnDefinition = "char(36) comment '主键ID'")
    private String id;

    @Column(columnDefinition = "varchar(100) comment '任务类型'")
    private String type;

    @Column(columnDefinition = "time comment '定时任务时间'")
    @JSONField(format = "HH:mm")
    private String schedule_time;                                         //采集时间
//
    @Column(columnDefinition = "varchar(100) comment '执行标识'")
    private String flag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSchedule_time() {
        return schedule_time;
    }

    public void setSchedule_time(String schedule_time) {
        this.schedule_time = schedule_time;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
