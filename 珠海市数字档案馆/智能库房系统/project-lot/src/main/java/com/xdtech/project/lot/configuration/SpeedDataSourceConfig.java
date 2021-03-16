package com.xdtech.project.lot.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

/**
 * Created by tanly on 2018/3/7 0007.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactorySpeed",
        transactionManagerRef = "transactionManagerSpeed",
        basePackages = {"com.xdtech.project.lot.speed"})
public class SpeedDataSourceConfig {

    @Resource
    private DataSource speedDataSource;

    @Resource
    private JpaProperties jpaProperties;

    private Map<String, String> getVendorProperties(DataSource dataSource) {
        return jpaProperties.getHibernateProperties(dataSource);
    }

    @Bean(name = "entityManagerFactorySpeed")
    public LocalContainerEntityManagerFactoryBean entityManagerFactorySpeed(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(speedDataSource)
                .properties(getVendorProperties(speedDataSource))
                .packages("com.xdtech.project.lot.speed")
                .persistenceUnit("speedPersistenceUnit")
                .build();
    }

    @Bean(name = "entityManagersSpeed")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactorySpeed(builder).getObject().createEntityManager();
    }

    @Bean(name = "transactionManagerSpeed")
    public PlatformTransactionManager transactionManagerSpeed(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactorySpeed(builder).getObject());
    }
}
