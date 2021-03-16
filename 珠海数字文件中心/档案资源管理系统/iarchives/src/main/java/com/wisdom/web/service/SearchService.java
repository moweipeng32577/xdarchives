package com.wisdom.web.service;

import net.sf.json.JSONArray;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import javax.sql.DataSource;

/**
 * Created by Zengdw on 2019/01/05.
 */
@Service
@Transactional
public class SearchService {

    @Autowired
    DataSource  dataSource;

    public String sqlData(String stl,String state){
        ConnectionPool pool = dataSource.getPool();
        try {
            //System.out.println(pool.getActive());
            Connection conn = pool.getConnection();
            //System.out.println(pool.getActive());
            //System.out.println(conn);
            PreparedStatement ps = conn.prepareStatement(stl);
            ResultSet rs ;
            System.out.println(stl);
            if("select".equals(state)){
                ArrayList list = null;
                rs = ps.executeQuery();
                list = this.transRStoVOMapListhashmap(rs);
                String json ="";
                if(list.size()>0){
                    json =listtojson(list);
                }else{
                    json="-2";
                }
                return json;
            }else if("alter".equals(state)){
                Boolean r=false;
                int i=0;
                r=ps.execute(stl);
                if(r){
                    i=0;
                }else{
                    i=10002;
                }
                return i+"A";
            }else if("update".equals(state)){
                int i=ps.executeUpdate(stl);
                return i+"";
            }
            conn.close();
            //Thread.sleep(3);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    /**
     * 将rs转换成list
     *
     * @param rs
     *            结果集
     * @return list
     * @throws Exception
     */
    private ArrayList transRStoVOMapListhashmap(ResultSet rs) throws Exception {

        ArrayList paraList = new ArrayList();

        paraList = new ArrayList();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();


        int rslength = 0;
        while (rs.next()) {
            //ArrayList vo = new ArrayList();
            HashMap  vo=new HashMap();
            for (int i = 1; i <= columnCount; i++) {
                String colName = rsmd.getColumnName(i).trim().toLowerCase();
				/*
				 * String recordValue = rs.getString(rsmd.getColumnName(i)); if
				 * (recordValue != null) { recordValue = recordValue.trim(); }
				 * else recordValue="";
				 */
                String recordValue = "";
                if (rs.getString(rsmd.getColumnName(i)) != null) {
                    recordValue = rs.getString(rsmd.getColumnName(i)).trim();
                } else
                    recordValue = "";

                //vo.add(recordValue);
                vo.put(colName,recordValue);
            }
            rslength++;
            paraList.add(vo);
        }
        //log.info("共有 " + rslength + " 行记录");

        return paraList;

    }

    /**
     * method 把list对象转换成json对象
     * @param list 要转换的list对象
     * @return 返回json对象
     */
    public String listtojson(List list) throws Exception {
        return JSONArray.fromObject(list).toString().trim();
    }
}