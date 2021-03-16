package com.wisdom.web.security;

import com.alibaba.fastjson.JSONObject;
import com.senseyun.openapi.SSRuntimeEasyJava.*;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Date;

/**
 * 深思加密锁读取类，读取加密锁中只读区储存的数据
 * Created by Rong on 2018/8/28.
 *
 * 1.添加获取当前版本名称的方法
 * 2.添加获取当前版本是否有利用平台的方法
 * Modify by Rong on 2018/10/25
 */
@Component
public class SlmRuntimeEasy {

    private static final String VERSION_TRAIL="0";//试用版
    private static final String VERSION_ALONE="1";//单机版
    private static final String VERSION_SINGLE="1.1";//简化版
    private static final String VERSION_NET="2";//网络版
    private static final String VERSION_NET_LIMIT="2.1";//限并发网络版
    private static final String VERSION_HIGH="3";//高级版
    private static final String VERSION_PLUS="4";//增强版
    public static String VERSION_CURRENT="3";

    public static Integer NET_LIMIT = 0;    //限并发网络版，并发数
    public static Integer ONLINE = 0;   //在线用户数

    private long ret = 0;       //调用接口的返回值
    private long Handle = 0;

    /**
     * 初始化接口
     */
    private void init(){
        //开发商API密码，深思发布绑定开发商
        String APIPsd = "DBE8394595F70F26B254C3618B2A0FA7";
        // 初始化函数，在使用深思RuntimeAPI其他接口之前，必须调用初始化函数
        ret = SSRuntimeEasy.SlmInitEasy(APIPsd);
        if(ret == ErrorCode.SS_OK){
            System.out.println("SlmInitEasy success !");
        }else{
            System.out.printf("SlmInitEasy failure : 0x%08X!  %s\n", ret,
                    SSRuntimeEasy.SlmErrorFormatEasy(ret, 2));
        }
    }

    /**
     * 登陆深思接口许可
     */
    private void login(){
        ST_LOGIN_PARAM stLogin = new ST_LOGIN_PARAM();
        stLogin.size = stLogin.getSize();
        stLogin.license_id = 2656;//许可版本2656，对应欣档电子文档资源综合管理系统
        Handle = SSRuntimeEasy.SlmLoginEasy(stLogin, INFO_FORMAT_TYPE.STRUCT.get());   //login by struct
        ret = SSRuntimeEasy.SlmGetLastError();
        if(ret == ErrorCode.SS_OK && Handle != 0){
            System.out.println("SlmLoginEasy success!");
            System.out.printf("[SLM Handle]: %d\n", Handle);
        }else{
            System.out.printf("SlmLoginEasy failure : ErrorCode = 0x%08X! SLM Handle = %d\n", ret, Handle);
        }
    }

    /**
     * 做完处理清理资源
     */
    private void clean(){
        ret = SSRuntimeEasy.SlmCleanupEasy();
        if(ret == ErrorCode.SS_OK){
            System.out.println("SlmCleanupEasy success!");
        }else{
            System.out.printf("SlmCleanupEasy failure : 0x%08X!  %s\n", ret,
                    SSRuntimeEasy.SlmErrorFormatEasy(ret, 2));
        }
    }

    /**
     * 读取加密锁只读区数据
     * @return 加密锁中只读区存储的数据
     */
    private String readUserData(){
        String userdata = "";
        // 读取加密锁只读区数据
        byte[] ReadBuff;
        long DataSize = 0;
        //先获取加密锁只读区数据长度，超过0时才去读里面的数据
        DataSize = SSRuntimeEasy.SlmUserDataGetsizeEasy(Handle, LIC_USER_DATA_TYPE.ROM.get());
        ret = SSRuntimeEasy.SlmGetLastError();
        if(ret ==ErrorCode.SS_OK){
            System.out.println("SlmUserDataGetsizeEasy success!");
            if(DataSize != 0){
                ReadBuff = new byte[(int)DataSize];
                //加载加密锁只读区数据
                ret = SSRuntimeEasy.SlmUserDataReadEasy(Handle, LIC_USER_DATA_TYPE.ROM.get(),
                        ReadBuff, 0, DataSize);
                if(ret ==ErrorCode.SS_OK){
                    System.out.println("SlmUserDataReadEasy success!");
                    try {
                        //加密锁内的数据是通过AES进行加密过的，这里先进行解密，获取数据明文
                        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
                        random.setSeed("neounit".getBytes("UTF-8"));
                        keyGenerator.init(128, random);
                        SecretKey secretKey = keyGenerator.generateKey();
                        SecretKeySpec key = new SecretKeySpec(secretKey.getEncoded(), "AES");
                        Cipher cipher = Cipher.getInstance("AES");// 创建密码器
                        cipher.init(Cipher.DECRYPT_MODE, key);
                        byte[] byteContent = Base64.decodeBase64(new String(ReadBuff));
                        byte[] byteEncode = cipher.doFinal(byteContent);
                        //返回加密锁内明文数据
                        userdata = new String(byteEncode, "utf-8");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    System.out.printf("SlmUserDataReadEasy failure : 0x%08X!  %s\n",
                            ret, SSRuntimeEasy.SlmErrorFormatEasy(ret, 2));
                }
            }else{
                System.out.println("No data!");
            }
        }else{
            System.out.printf("SlmUserDataGetsizeEasy failure : 0x%08X!  %s\n",
                    ret, SSRuntimeEasy.SlmErrorFormatEasy(ret, 2));
        }
        return userdata;
    }

    /**
     * 读取加密锁非永久授权的授权截止时间
     * @return 截止时间
     */
    private Date readDeadTime(){
        Date deadtime = new Date();
        //读取加密锁内许可信息
        Object lic_info = SSRuntimeEasy.SlmGetInfoEasy(Handle, INFO_TYPE.LICENSE_INFO.get(), INFO_FORMAT_TYPE.JSON.get());
        ret = SSRuntimeEasy.SlmGetLastError();
        if(ret == ErrorCode.SS_OK)
        {
            System.out.println("SlmGetInfoEasy [LICENSE_INFO] success!");
            System.out.println("[LICENSE INFO]:" + lic_info);
            SSRuntimeEasy.SlmFreeEasy(lic_info);
            JSONObject jsonObject = JSONObject.parseObject((String)lic_info);
            if(jsonObject.get("span_time") == null){
                return null;
            }
            //使用许可时间
            Long span = ((Integer) jsonObject.get("span_time")).longValue();
            //加密锁初次使用时间
            Long first = ((Integer) jsonObject.get("first_use_time")).longValue();
            //运算获得许可截止时间
            Long dead = (span + first) * 1000L;
            deadtime.setTime(dead);
        }
        else
        {
            System.out.printf("SlmGetInfoEasy [LICENSE_INFO]  failure : ErrorCode = 0x%08X!  %s\n",
                    ret, SSRuntimeEasy.SlmErrorFormatEasy(ret, 2));
        }
        return deadtime;
    }

    /**
     * 提供外部调用方法，用于获取加密锁只读区数据
     * @return 加密锁内只读区的数据
     */
    public String getUserData(){
        //1.初始化
        this.init();
        //2.登陆
        this.login();
        //3.获取数据
        String userdata = this.readUserData();
        //4.清理资源
        this.clean();
        return userdata;
    }

    /**
     * 提供外部调用方法，用于获取加密锁设置的许可截止时间
     * 针对试用版有效
     * @return 截止时间
     */
    public Date getDeadTime(){
        //1.初始化
        this.init();
        //2.登陆
        this.login();
        //3.获取有效截止事件
        Date deadtime = this.readDeadTime();
        //4.清理资源
        this.clean();
        return deadtime;
    }

    /**
     * 获取当前系统版本是否有过期时间
     * 试用版有过期时间
     * @return
     */
    public boolean hasOvertime(){
        if(VERSION_CURRENT.equals(VERSION_TRAIL)){
            return true;
        }
        return false;
    }

    /**
     * 获取当前系统版本是否支持远程IP访问
     * 简化版及单机版不支持远程IP访问
     * @return
     */
    public boolean hasRemoteAccess(){
        if(VERSION_CURRENT.equals(VERSION_ALONE) || VERSION_CURRENT.equals(VERSION_SINGLE)){
            return false;
        }
        return true;
    }

    /**
     * 获取当前系统版本是否有数据采集和数据审核
     * 简化版及单机版不包含数据采集和数据审核
     * @return
     */
    public boolean hasCapture(){
        if(VERSION_CURRENT.equals(VERSION_ALONE) || VERSION_CURRENT.equals(VERSION_SINGLE)){
            return false;
        }
        return true;
    }

    /**
     * 获取当前系统版本是否包含编演管理
     * 简化版不包含
     * @return
     */
    public boolean hasCompilation(){
        if(VERSION_CURRENT.equals(VERSION_SINGLE)){
            return false;
        }
        return true;
    }

    public boolean hasExchange(){
        if(VERSION_CURRENT.equals(VERSION_SINGLE)){
            return false;
        }
        return true;
    }

    /**
     * 获取当前系统版本是否包含全宗卷管理
     * 简化版不包含
     * @return
     */
    public boolean hasFondsArchive(){
        if(VERSION_CURRENT.equals(VERSION_SINGLE)){
            return false;
        }
        return true;
    }

    /**
     * 获取当前系统版本是否有利用平台
     * 高级版及增强版拥有利用平台
     * @return
     */
    public boolean hasPlatform(){
        if(VERSION_CURRENT.equals(VERSION_HIGH) || VERSION_CURRENT.equals(VERSION_PLUS)){
            return true;
        }
        return false;
    }

    /**
     * 获取当前系统版本是否有全文检索
     * 仅增强版有全文检索
     * @return
     */
    public boolean hasFulltext(){
        if(VERSION_CURRENT.equals(VERSION_PLUS)){
            return true;
        }
        return false;
    }

    /**
     * 获取当前系统版本是否为简化版
     * @return
     */
    public boolean isSingle(){
        return VERSION_CURRENT.equals(VERSION_SINGLE);
    }

    /**
     * 获取当前系统版本名称
     * @return
     */
    public String getVersionName(){
        String version = "";
        switch (VERSION_CURRENT){
            case VERSION_TRAIL:
                version = "试用版";
                break;
            case VERSION_ALONE:
                version = "单机版";
                break;
            case VERSION_SINGLE:
                version = "简化版";
                break;
            case VERSION_NET:case VERSION_NET_LIMIT:
                version = "网络版";
                break;
            case VERSION_HIGH:
                version = "高级版";
                break;
            case VERSION_PLUS:
                version = "增强版";
                break;
            default:
                break;
        }
        return version;
    }

}
