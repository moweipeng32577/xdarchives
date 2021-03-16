package com.xdtech.project.lot.hj;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 *  @author wujy 2019/10/22
 */
public interface Environment extends Library {

    Environment environment = (Environment) Native.loadLibrary("SPD_DLL_Net", Environment.class);

    int Open_Port();
    /**
     * 控制空调开关或设置温湿度
     * 示例：假定环境主机IP为 192.168.1.199，空调ID 8,传感器类型0xe0,属性0
     *      控制空调开机
     *      WriteAircondition("192.168.1.199",502,1,8,0xe0,0,1,-1,-1);
     *
     *      设置空调温度为25度
     *      WriteAircondition("192.168.1.199",502,1,8,0xe0,0,-1,25,-1);
     * @param iIP 主机IP地址例如 "192.168.1.145"
     * @param pPort 端口号 例如 502
     * @param Target 一般固定为1
     * @param SensorID 传感器ID值
     * @param SensorType 值感器类型
     * @param SensorProp 传感器属性
     * @param Power 开机，0关机，-1 不控制开关即保持目前的空调开关状态
     * @param temperature 要设置的温度值，-1不设置温度即保持空调已有的设置值
     * @param hemidity 湿度设置值，-1不改变空调已有的湿度值
     * @return
     */
    int WriteAircondition(String iIP, int pPort, int Target, int SensorID,
                          int SensorType, int SensorProp,int Power,int temperature,int hemidity);


}
