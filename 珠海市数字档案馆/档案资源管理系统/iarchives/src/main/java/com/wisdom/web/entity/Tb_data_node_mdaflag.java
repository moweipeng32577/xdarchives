package com.wisdom.web.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Leo on 2019/12/13 0013.
 */
@Entity
public class Tb_data_node_mdaflag {
    @Id
    private String nodeid;
    private int is_media;


    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public int getIs_media() {
        return is_media;
    }

    public void setIs_media(int is_media) {
        this.is_media = is_media;
    }
}
