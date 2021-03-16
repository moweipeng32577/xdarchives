package com.wisdom.util;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by Rong on 2018/6/14.
 */
public class OracleCharIDType implements UserType {

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.CHAR};
    }

    @Override
    public Class returnedClass() {
        return String.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y)
            return true;
        if (x == null || y == null)
            return false;

        if (x instanceof String && y instanceof String)
            return x.equals(y);

        return false;
    }

    @Override
    public int hashCode(Object o) throws HibernateException {
        return o.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] strings, SessionImplementor sessionImplementor, Object o) throws HibernateException, SQLException {
        return resultSet.getString(strings[0]);
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object o, int index, SessionImplementor sessionImplementor) throws HibernateException, SQLException {
        String drivername = preparedStatement.getConnection().getMetaData().getDriverName();
        if (o == null) {
            preparedStatement.setNull(index, Types.CHAR);
            return;
        }
        if(drivername.indexOf("Oracle") > -1){
            preparedStatement.setString(index, String.format("%1$-36s",(String) o));
        }else{
            preparedStatement.setString(index, (String)o);
        }
    }

    @Override
    public Object deepCopy(Object o) throws HibernateException {
        return o;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object o) throws HibernateException {
        return (Serializable)o;
    }

    @Override
    public Object assemble(Serializable serializable, Object o) throws HibernateException {
        return serializable;
    }

    @Override
    public Object replace(Object o, Object o1, Object o2) throws HibernateException {
        return o;
    }

}
