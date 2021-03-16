package com.xdtech.project.lot.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 *
 */
@Configuration
public class LotMultiDataSourceConfig {

    @Bean(name = "lotPrimaryDataSource")
    @Qualifier("lotPrimaryDataSource")
    //@Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource lotPrimaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "speedDataSource")
    @Qualifier("speedDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.speed")
    public DataSource speedDataSource() {
        return DataSourceBuilder.create().build();
    }

}
