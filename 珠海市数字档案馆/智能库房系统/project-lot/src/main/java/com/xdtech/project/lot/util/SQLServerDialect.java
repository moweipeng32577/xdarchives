package com.xdtech.project.lot.util;

import org.hibernate.type.StringType;

import java.sql.Types;

/**
 * Created by Rong on 2018/11/6.
 */
public class SQLServerDialect extends org.hibernate.dialect.SQLServer2012Dialect {

    public SQLServerDialect(){
        super();
        registerHibernateType(Types.NVARCHAR, StringType.INSTANCE.getName());

        //JDBC type:-15
        registerHibernateType(Types.NCHAR, StringType.INSTANCE.getName());
    }

}
