package com.xdtech.project.lot.configuration;

import com.bstek.ureport.definition.datasource.BuildinDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Created by Rong on 2018/11/19.
 */
@Component
public class ReportDataSource implements BuildinDatasource {

    @Autowired
    private DataSource dataSource;

    @Override
    public String name() {
        return "NeoUnit";
    }

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        }catch(Exception e){
            throw new RuntimeException();
        }
    }
}
