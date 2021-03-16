package com.wisdom.util;

import com.wisdom.web.entity.Entry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tanly on 2017/11/23 0023.
 */
public class ConfigValue {

    private final static Logger logger = LoggerFactory.getLogger(ConfigValue.class);

    static private ConfigValue configValue;

    public String getConfigInfomation(String key) {
        InputStream is = null;
        try {
            Properties properties = new Properties();
            is = getClass().getResourceAsStream("/const.properties");
            properties.load(is);
            return properties.get(key).toString();
        } catch (IOException e) {
            logger.error("无法读取配置文件:const.properties");
            return "";
        } finally {
            if(is != null){
                try{
                    is.close();
                }catch (IOException e){
                    logger.error(e.getMessage());
                }
            }
        }
    }

    public static String getValue(String key) {
        if (null == configValue) {
            configValue = new ConfigValue();
        }
        return configValue.getConfigInfomation(key);
    }

    public String getConfigPath(String key) {
        InputStream is = null;
        try {
            Properties properties = new Properties();
            is = getClass().getResourceAsStream("/application.properties");
            properties.load(is);
            return properties.get(key).toString();
        } catch (IOException e) {
            logger.error("无法读取配置文件:application.properties");
            return "";
        } finally {
            if(is != null){
                try{
                    is.close();
                }catch (IOException e){
                    logger.error(e.getMessage());
                }
            }
        }
    }

    public static String getPath(String key) {
        if(null==configValue){
            configValue=new ConfigValue();
        }
        return configValue.getConfigPath(key);
    }

    public String getField(String key) {
        InputStream is = null;
        try {
            Properties properties = new Properties();
            is = getClass().getResourceAsStream("/exportTemp.properties");
            properties.load(is);
            return properties.get(key).toString();
        } catch (IOException e) {
            logger.error("无法读取配置文件:exportTemp.properties");
            return "";
        } finally {
            if(is != null){
                try{
                    is.close();
                }catch (IOException e){
                    logger.error(e.getMessage());
                }
            }
        }
    }

    public static String getFieldProperty(String key) {
        if(null==configValue){
            configValue=new ConfigValue();
        }
        return configValue.getField(key);
    }

    public static void StringCopy(String[] str1,String[] str2){
        int strLen1 = str1.length;// 保存第一个数组长度
        int strLen2 = str2.length;// 保存第二个数组长度
        str1 = Arrays.copyOf(str1, strLen1 + strLen2);// 扩容
        System.arraycopy(str2, 0, str1, strLen1, strLen2);// 将第二个数组与第一个数组合并
        logger.info(Arrays.toString(str1));
    }

    public static String[] getEntryid(List<Entry> entryList){
        String entryid = "";
        for(Entry entry :entryList){
            entryid +=entry.getEntryid()+",";
        }
        return entryid.split(",");
    }


    public static String getSGOATemp(String key) {
        if(null==configValue){
            configValue=new ConfigValue();
        }
        return configValue.getOATemp(key);
    }

    public String getOATemp(String key) {
        InputStream is = null;
        try {
            Properties properties = new Properties();
            is = getClass().getResourceAsStream("/OATemp.properties");
            properties.load(is);
            if(null!=properties.getProperty(key)) {
                return properties.get(key).toString();
            }else {
                return null;
            }
        } catch (IOException e) {
            logger.error("无法读取配置文件:oaTemp.properties");
            return "";
        } finally {
            if(is != null){
                try{
                    is.close();
                }catch (IOException e){
                    logger.error(e.getMessage());
                }
            }
        }
    }
//    public static void main(String[] age){
//        String[] str1 = {"1","2"};
//        String[] str2 = {"a","b"};
//        ConfigValue.StringCopy(str1,str2);
//        logger.info(Arrays.toString(str1));
//    }
}