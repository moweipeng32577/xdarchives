package com.xdtech.project.lot.hj;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * 抽湿机
 */
public interface Dehumidifier extends Library {

    Dehumidifier dehumidifier = (Dehumidifier) Native.loadLibrary("SPD_MODBUS_RTU", Dehumidifier.class);

    /**
     * 打开串口  该函数只需要执行一次
     * @param com 串口号
     * @param data 数据格式（数据格式为 9600波特率，8数据位，1停止位）
     * @return 成功返回0
     */
    int OpenPortEx(int com,String data);

    /**
     *控制抽湿机开关
     * @param com 串口号
     * @param id 串口id
     * @param cmd
     * @param prop 属性
     * @param opt 开关
     * @return
     */
    int WriteSensor(int com,int id,int cmd,int prop,int opt);
}
