package com.wisdom.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**获取字段值
 * Created by Administrator on 2017/8/10.wjh
 */
public class GainField {

    /**
     * 将对象o中指定属性封装成数组
     * @param o
     * @return
     */
    public static String[] getFieldName(Object o){
        Field[] fields=o.getClass().getDeclaredFields();
        String[] fieldNames=new String[fields.length];
        for(int i=0;i<fields.length;i++){
            System.out.println(fields[i].getName()+"="+getFieldValueByName(fields[i].getName(),o));
            fieldNames[i]=fields[i].getName();
        }
        return fieldNames;
    }

    public static String getFieldValue(List list,String field){
        String fieldNames = "";
        for(int i=0;i<list.size();i++){
            fieldNames += ",'"+getFieldValueByName(field,list.get(i))+"'";
        }
        if(!"".equals(fieldNames)){
            fieldNames = fieldNames.substring(1,fieldNames.length());
        }
        return fieldNames;
    }

    public static String[] getFieldValues(List list,String field){
        String[] fieldNames = new String[list.size()];
        for(int i=0;i<list.size();i++){
            Object name=getFieldValueByName(field,list.get(i));
            fieldNames[i] = (name!=null?name:"")+"";
        }
        return fieldNames;
    }

    public static String[] getFieldValues(List list,String field,String fieldTwo){
        String[] fieldNames = new String[list.size()];
        String filedcode,table;
        for(int i=0;i<list.size();i++){
            Object code=getFieldValueByName(field,list.get(i));
            Object two=getFieldValueByName(fieldTwo,list.get(i));
            filedcode=(code!=null?code:"")+"";
            table=(two!=null?two:"")+"";
            fieldNames[i] = filedcode+"_"+table;
        }
        return fieldNames;
    }

    //根据变量名获得变量的值
    public static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter);
            Object value = method.invoke(o);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setFieldValues(String fieldName, List list, String value) {
        for(int i=0;i<list.size();i++){
            setFieldValueByName(fieldName,list.get(i),value);
        }
    }

    //根据变量名设置变量的值
    public static void setFieldValueByName(String fieldName, Object o, String value) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String setter = "set" + firstLetter + fieldName.substring(1);
            String getter = "get"+ firstLetter + fieldName.substring(1);
            String type= o.getClass().getMethod(getter).getReturnType().toString();
        	Method m;
            if(type.equals("int")){
                m= o.getClass().getMethod(setter,int.class);
                m.invoke(o,Integer.valueOf(value));
            }else{
                m= o.getClass().getMethod(setter,String.class);
                m.invoke(o,value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断对象中是否所有属性均为null
     * @param obj      需要检验的对象
     * @param exclude 排除属性个数(最后exclude个)
     * @return        所检验对象的所有属性为空则返回true
     */
    public static boolean objectIsNull(Object obj,int exclude){
        String[] fieldNames = getFieldName(obj);
        List<Object> values = new ArrayList<Object>();
        for(String fieldName:fieldNames){
            Object value = getFieldValueByName(fieldName,obj);
            values.add(value);
        }

        for(int i=0;i<values.size()-exclude;i++){
            if(!"null".equals(values.get(i)) && !"".equals(values.get(i)) && values.get(i)!=null){
                return false;
            }
        }
        return true;
    }

    public static void main (String args[]){


//        List<Tb_entry_index> list = new ArrayList<Tb_entry_index>();
//        Tb_entry_index entry = new Tb_entry_index();
//        entry.setArchivecode("档号001");
//        entry.setCatalog("目录号001");
//        Tb_entry_index entry2 = new Tb_entry_index();
//        entry.setArchivecode("档号002");
//        entry.setCatalog("目录号002");
//        list.add(entry);
//        list.add(entry2);
//        System.out.println(GainField.getFieldValueByName("catalog",entry));

//        List<Tb_user> users = new ArrayList<Tb_user>();
//        Tb_user model = new Tb_user();
//        model.setLoginname("xw");
//        users.add(model);
//
//        model = new Tb_user();
//        model.setLoginname("xw1");
//        users.add(model);
//
//        model = new Tb_user();
//        model.setLoginname("xw2");
//        users.add(model);
//
//       // for(int i=0;i<users.size();i++){
//           System.out.println(getFieldValue(users,"loginname"));
//       // }

    }

}
