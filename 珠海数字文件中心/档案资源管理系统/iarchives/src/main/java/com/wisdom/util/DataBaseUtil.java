package com.wisdom.util;

/**
 * Created by yl on 2019/8/13.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DataBaseUtil {
    /**
     * 获取数据库连接
     * @return Connection 对象
     */
    public static Connection getConnection(String url,String username,String password,String driver) {

        Connection conn = null;
        try{
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void closeConn(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}