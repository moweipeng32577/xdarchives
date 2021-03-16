package com.wisdom.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;
/**
 * Created by tanly on 2018/3/7 0007.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.wisdom.web","com.xdtech"})
public class PrimaryDataSourceConfig {

    @Autowired()
    @Qualifier("primaryDataSource")
    private DataSource primaryDataSource;

    @Autowired
    private JpaProperties jpaProperties;

    /**
     * 通过调用JPA配置实体中的解析方法，解析datasource中各属性的值
     * @param dataSource    数据源
     * @return     本数据源中各参数
     *这些和不同类型数据库密切相关的属性设置，不能设置在application.properties中，所以需要再不同的数据源中具体设置，赋值给JpaProperties
     */
//    private Map<String, String> getVendorProperties(DataSource dataSource) {
//        jpaProperties.setDatabase(Database.MYSQL);
//        Map<String,String> map = new HashMap<>();
//        map.put("hibernate.dialect","org.hibernate.dialect.MySQL5Dialect");
////        map.put("spring.jpa.properties.hibernate.hbm2ddl.auto","none");
////        map.put("hibernate.physical_naming_strategy","org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
//        jpaProperties.setProperties(map);
//        return jpaProperties.getHibernateProperties(dataSource);
//    }
    private Map<String, String> getVendorProperties(DataSource dataSource) {
        return jpaProperties.getHibernateProperties(dataSource);
    }
    /**
     * 配置EntityManagerFactory实体
     *
     * @param builder
     * @return
     * packages     扫描@Entity注释的软件包名称
     * persistenceUnit  持久性单元的名称。 如果只建立一个EntityManagerFactory，你可以省略这个，但是如果在同一个应用程序中有多个，你应该给它们不同的名字
     * properties       标准JPA或供应商特定配置的通用属性。 这些属性覆盖构造函数中提供的任何值。
     */
    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(primaryDataSource)
                .properties(getVendorProperties(primaryDataSource))
                .packages("com.wisdom.web","com.xdtech")
                .persistenceUnit("primaryPersistenceUnit")
                .build();
    }

    @Primary
    @Bean(name = "entityManager")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryPrimary(builder).getObject().createEntityManager();
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManagerPrimary(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryPrimary(builder).getObject());
    }
}
