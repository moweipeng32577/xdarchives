package com.wisdom.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


/**
 * Created by Leo on 2019/5/27 0027.
 */
public class GuavaCache {
    private static Logger logger = LoggerFactory.getLogger(GuavaCache.class);
    /**
     * 静态内存块
     */
    private static LoadingCache<String, Object> localCache = CacheBuilder.newBuilder()
            .initialCapacity(1000)//缓存的初始化容量
            .maximumSize(10000)//缓存的最大容量，超过最大值时，Guava会调用LRU最少使用算法移除缓存项
            .expireAfterAccess(3, TimeUnit.HOURS)//有效期为3小时
            .build(new CacheLoader<String, Object>(){
                //默认的数据加载实现，当key没有对应值时，调用下面这个方法进行加载
                @Override
                public String load(String key) throws Exception {
                    // TODO Auto-generated method stub
                    //以防空指针错误影响项目正常进行，我将空值换成字符串的“null”
                    return "null";
                }});

    //以下开放cache的get,set
    /**
     * 以key-value的形式设置缓存的值
     * @param key 键名
     * @param value	对用的缓存值
     */
    public static void setKeyValue(String key, Object value) {
        localCache.put(key, value);
    }
    /**
     * 通过key获取缓存的值
     * @param key 缓存对应的键名
     * @return
     */
    public static Object getValueByKey(String key) {
        Object value = null;
        //以防缓存出现别的错误影响项目正常运行
        try {
            value = localCache.get(key);
            //当出现假null时
            if("null".equals(value)) {
                return null;
            }
        }catch(Exception e) {
            logger.error("localCache出现异常", e);
        }
        return value;
    }

    public static void removeValueByKey(String key) {
        localCache.invalidate(key);
    }
}
