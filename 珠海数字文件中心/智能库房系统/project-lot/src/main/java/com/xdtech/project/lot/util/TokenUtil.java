package com.xdtech.project.lot.util;

import com.alibaba.fastjson.JSONObject;

public class TokenUtil {

    /**
     *
     * @param username 用户名
     * @param password 密码
     * @param url 要获取tonken的url地址
     * @param key 返回json数据中包含token的字段
     * @return
     */
    public static String getToken(String username,String password,String url,String key){
        JSONObject loginParms = new JSONObject(true);//构造登录系统的账户名密码
        loginParms.put("username",username);
        loginParms.put("password",password);
        JSONObject response  = RemoteRequestUtil.sendPost(url,loginParms);//这里得到返回的json对象，再进行解析
        return response.getJSONObject(key).getString("access_token");
    }

}
