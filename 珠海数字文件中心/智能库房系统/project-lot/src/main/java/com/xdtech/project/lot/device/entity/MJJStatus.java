package com.xdtech.project.lot.device.entity;

/**
 * 专门用来推送密集架状态给大屏app
 */
public class MJJStatus {

    public static final String MOVING = "moving";//正在移动中
    public static final String ENDMOVE = "endMove";//移动到位
    public static final String COLUMN_STATUS = "columnStatus";//查询列状态
    public static final String WARN = "warn";//告警

    private String deviceId;//设备id
    private Integer code; //区号
    private Integer moveCol; //移动的列
    private Integer operate;//到位情况 (0-左右到位；1—右到位；2—左到位；3—左右不靠；4—此列已锁)
    private String tag;//标识（查询列状态、告警、正在移动和移动到位）

    public MJJStatus(String deviceId, Integer code, Integer moveCol, Integer operate) {
        this.deviceId = deviceId;
        this.code = code;
        this.moveCol = moveCol;
        this.operate = operate;
    }

    public MJJStatus(String deviceId, Integer code, Integer moveCol, Integer operate, String tag) {
        this.deviceId = deviceId;
        this.code = code;
        this.moveCol = moveCol;
        this.operate = operate;
        this.tag = tag;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getMoveCol() {
        return moveCol;
    }

    public void setMoveCol(Integer moveCol) {
        this.moveCol = moveCol;
    }

    public Integer getOperate() {
        return operate;
    }

    public void setOperate(Integer operate) {
        this.operate = operate;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
