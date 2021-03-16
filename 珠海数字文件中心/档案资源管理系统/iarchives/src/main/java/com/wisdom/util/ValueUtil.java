package com.wisdom.util;

import com.wisdom.secondaryDataSource.entity.Tb_codeset_sx;
import com.wisdom.secondaryDataSource.entity.Tb_data_template_sx;
import com.wisdom.web.entity.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;

import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by SunK on 2018/7/24 0024.
 */
public class ValueUtil {

	public static Object getPoFieldValue(String fieldName, Object po)
			throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		Class poClass = Class.forName(po.getClass().getName());
		//fieldName = fieldName;
		String methodName = fieldName.trim().substring(0, 1).toUpperCase() + fieldName.substring(1);
		(new StringBuilder("set")).append(methodName).toString();
		String getMethod = "get" + methodName;
		Method meth = null;
		String fieldType = "string";
		Field field = null;
		Class[] partypes = new Class[1];
		Object fieldNameValue = null;
		try {
			field = poClass.getField(fieldName);//获取类的属性
		} catch (Exception var13) {
			field = null;
		}
		if (field != null) {
			fieldType = field.getType().getName().trim();
			if (fieldType.trim().toLowerCase().lastIndexOf("string") >= 0) {
				partypes[0] = field.getType().getName().getClass();
				meth = poClass.getMethod(getMethod, (Class[]) null);
				fieldNameValue = meth.invoke(po, (Object[]) null);
			} else if (fieldType.trim().toLowerCase().equalsIgnoreCase("int")) {
				partypes[0] = Integer.TYPE;
				meth = poClass.getMethod(getMethod, (Class[]) null);
				fieldNameValue = meth.invoke(po, (Object[]) null);
				fieldNameValue = String.valueOf(fieldNameValue);
			} else if (fieldType.trim().toLowerCase().lastIndexOf("integer") >= 0) {
				partypes[0] = Integer.class;
				meth = poClass.getMethod(getMethod, (Class[]) null);
				fieldNameValue = meth.invoke(po, (Object[]) null);
				fieldNameValue = String.valueOf(fieldNameValue);
			} else if (fieldType.trim().toLowerCase().lastIndexOf("double") >= 0) {
				partypes[0] = Double.TYPE;
				meth = poClass.getMethod(getMethod, (Class[]) null);
				fieldNameValue = meth.invoke(po, (Object[]) null);
				fieldNameValue = String.valueOf(fieldNameValue);
			} else if (fieldType.trim().toLowerCase().lastIndexOf("date") >= 0) {
				partypes[0] = Date.class;
				meth = poClass.getMethod(getMethod, (Class[]) null);
				fieldNameValue = meth.invoke(po, (Object[]) null);
				fieldNameValue = String.valueOf(fieldNameValue).substring(0, 10);
			} else if (fieldType.trim().toLowerCase().lastIndexOf("long") >= 0) {
				partypes[0] = Long.TYPE;
				meth = poClass.getMethod(getMethod, (Class[]) null);
				fieldNameValue = meth.invoke(po, (Object[]) null);
				fieldNameValue = String.valueOf(fieldNameValue);
			}
		} else {
			meth = poClass.getMethod(getMethod, (Class[]) null);
			fieldNameValue = meth.invoke(po, (Object[]) null);
			fieldNameValue = String.valueOf(fieldNameValue);
		}
		if ("null".equals(fieldNameValue)) {// 判断字符串为null时设置为空格
			fieldNameValue = "";
		}
		return fieldNameValue;
	}

	// 把List集合组装成entry_index对象
	public static Tb_entry_index creatEntryIndex(String[] fieldCode, List<String> lists)
			throws NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, ParseException {

		System.out.println(Arrays.toString(fieldCode));

		Tb_entry_index bean = new Tb_entry_index();
		Class<?> cls = bean.getClass();
		// 取出entry中的所有方法
		Field[] fields = cls.getDeclaredFields();
		Method[] methods = cls.getMethods();
		for (int i = 0; i < lists.size(); i++) {// 遍历属性数
			String fieldSetName = parSetName(fieldCode[i]);// 拼接set方法名
			if (!checkSetMet(methods, fieldSetName)) {// 判断是否有次属性
				continue;
			}
			Field field = cls.getDeclaredField(fieldCode[i].toLowerCase());// 返回指定私有成员字段
			Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());// 得到方法（方法名，字段属性类型）
			String value = lists.get(i);
			if (null != value && !"".equals(value)) {
				String fieldType = field.getType().getSimpleName();
				if ("String".equals(fieldType)) {
					fieldSetMet.invoke(bean, value);
				} else if ("Date".equals(fieldType)) {
					Date temp = parseDate(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
					Integer intval = Integer.parseInt(value);
					fieldSetMet.invoke(bean, intval);
				} else if ("Long".equalsIgnoreCase(fieldType)) {
					Long temp = Long.parseLong(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Double".equalsIgnoreCase(fieldType)) {
					Double temp = Double.parseDouble(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Boolean".equalsIgnoreCase(fieldType)) {
					Boolean temp = Boolean.parseBoolean(value);
					fieldSetMet.invoke(bean, temp);
				}
			}else{
				fieldSetMet.invoke(bean, value);
			}
		}
		return bean;
	}

	// 把List集合组装成tb_entry_index_temp对象
	public static Tb_entry_index_temp creatEntryIndexTemp(String[] fieldCode, List<String> lists)
			throws NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, ParseException {

		System.out.println(Arrays.toString(fieldCode));

		Tb_entry_index_temp bean = new Tb_entry_index_temp();
		Class<?> cls = bean.getClass();
		// 取出entry中的所有方法
		Field[] fields = cls.getDeclaredFields();
		Method[] methods = cls.getMethods();
		for (int i = 0; i < lists.size(); i++) {// 遍历属性数
			String fieldSetName = parSetName(fieldCode[i]);// 拼接set方法名
			if (!checkSetMet(methods, fieldSetName)) {// 判断是否有次属性
				continue;
			}
			Field field = cls.getDeclaredField(fieldCode[i].toLowerCase());// 返回指定私有成员字段
			Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());// 得到方法（方法名，字段属性类型）
			String value = lists.get(i);
			if (null != value && !"".equals(value)) {
				String fieldType = field.getType().getSimpleName();
				if ("String".equals(fieldType)) {
					fieldSetMet.invoke(bean, value);
				} else if ("Date".equals(fieldType)) {
					Date temp = parseDate(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
					Integer intval = Integer.parseInt(value);
					fieldSetMet.invoke(bean, intval);
				} else if ("Long".equalsIgnoreCase(fieldType)) {
					Long temp = Long.parseLong(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Double".equalsIgnoreCase(fieldType)) {
					Double temp = Double.parseDouble(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Boolean".equalsIgnoreCase(fieldType)) {
					Boolean temp = Boolean.parseBoolean(value);
					fieldSetMet.invoke(bean, temp);
				}
			}else{
				fieldSetMet.invoke(bean, value);
			}
		}
		return bean;
	}

	// 把List集合组装成Tb_entry_index_capture对象
	public static Tb_entry_index_capture captureCreatEntryIndex(String[] fieldCode, List<String> lists)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException,
			SecurityException, NoSuchMethodException, ParseException {
		Tb_entry_index_capture bean = new Tb_entry_index_capture();
		Class<?> cls = bean.getClass();
		// 取出entry中的所有方法
		Field[] fields = cls.getDeclaredFields();
		Method[] methods = cls.getMethods();
		for (int i = 0; i < lists.size(); i++) {// 遍历属性数
			String fieldSetName = parSetName(fieldCode[i]);// 拼接set方法名
			if (!checkSetMet(methods, fieldSetName)) {// 判断是否有次属性
				continue;
			}
			Field field = cls.getDeclaredField(fieldCode[i].toLowerCase());// 返回指定私有成员字段
			Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());// 得到方法（方法名，字段属性类型）
			String value = lists.get(i);
			if (null != value && !"".equals(value)) {
				String fieldType = field.getType().getSimpleName();
				if ("String".equals(fieldType)) {
					fieldSetMet.invoke(bean, value);
				} else if ("Date".equals(fieldType)) {
					Date temp = parseDate(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
					Integer intval = Integer.parseInt(value);
					fieldSetMet.invoke(bean, intval);
				} else if ("Long".equalsIgnoreCase(fieldType)) {
					Long temp = Long.parseLong(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Double".equalsIgnoreCase(fieldType)) {
					Double temp = Double.parseDouble(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Boolean".equalsIgnoreCase(fieldType)) {
					Boolean temp = Boolean.parseBoolean(value);
					fieldSetMet.invoke(bean, temp);
				}
			}else {
				fieldSetMet.invoke(bean, value);
			}
		}
		return bean;
	}

	// 把List集合组装成Tb_entry_index_accept对象
	public static Tb_entry_index_accept captureCreatEntryIndexAccept(String[] fieldCode, List<String> lists)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException,
			SecurityException, NoSuchMethodException, ParseException {
		Tb_entry_index_accept bean = new Tb_entry_index_accept();
		Class<?> cls = bean.getClass();
		// 取出entry中的所有方法
		Field[] fields = cls.getDeclaredFields();
		Method[] methods = cls.getMethods();
		for (int i = 0; i < lists.size(); i++) {// 遍历属性数
			String fieldSetName = parSetName(fieldCode[i]);// 拼接set方法名
			if (!checkSetMet(methods, fieldSetName)) {// 判断是否有次属性
				continue;
			}
			Field field = cls.getDeclaredField(fieldCode[i].toLowerCase());// 返回指定私有成员字段
			Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());// 得到方法（方法名，字段属性类型）
			String value = lists.get(i);
			if (null != value && !"".equals(value)) {
				String fieldType = field.getType().getSimpleName();
				if ("String".equals(fieldType)) {
					fieldSetMet.invoke(bean, value);
				} else if ("Date".equals(fieldType)) {
					Date temp = parseDate(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
					Integer intval = Integer.parseInt(value);
					fieldSetMet.invoke(bean, intval);
				} else if ("Long".equalsIgnoreCase(fieldType)) {
					Long temp = Long.parseLong(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Double".equalsIgnoreCase(fieldType)) {
					Double temp = Double.parseDouble(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Boolean".equalsIgnoreCase(fieldType)) {
					Boolean temp = Boolean.parseBoolean(value);
					fieldSetMet.invoke(bean, temp);
				}
			}else {
				fieldSetMet.invoke(bean, value);
			}
		}
		return bean;
	}

	// 把List集合组装成entry_detail对象
	public static Tb_entry_detail creatEntryDetail(String[] fieldCode, List<String> lists)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NoSuchFieldException, ParseException {
		Tb_entry_detail bean = new Tb_entry_detail();
		Class<?> cls = bean.getClass();
		// 取出entry中的所有方法
		Field[] fields = cls.getDeclaredFields();
		Method[] methods = cls.getMethods();
		for (int i = 0; i < lists.size(); i++) {// 遍历属性数
			String fieldSetName = parSetName(fieldCode[i]);// 拼接set方法名
			if (!checkSetMet(methods, fieldSetName)) {// 判断是否有次属性
				continue;
			}
			Field field = cls.getDeclaredField(fieldCode[i].toLowerCase());// 返回指定私有成员字段
			Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());// 得到方法（方法名，字段属性类型）
			String value = lists.get(i);
			if (null != value && !"".equals(value)) {
				String fieldType = field.getType().getSimpleName();
				if ("String".equals(fieldType)) {
					fieldSetMet.invoke(bean, value);
				} else if ("Date".equals(fieldType)) {
					Date temp = parseDate(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
					Integer intval = Integer.parseInt(value);
					fieldSetMet.invoke(bean, intval);
				} else if ("Long".equalsIgnoreCase(fieldType)) {
					Long temp = Long.parseLong(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Double".equalsIgnoreCase(fieldType)) {
					Double temp = Double.parseDouble(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Boolean".equalsIgnoreCase(fieldType)) {
					Boolean temp = Boolean.parseBoolean(value);
					fieldSetMet.invoke(bean, temp);
				}
			}else{
				fieldSetMet.invoke(bean, value);
			}
		}
		return bean;
	}

	// 把List集合组装成entry_detail对象
	public static Tb_entry_detail_capture captureCreatEntryDetail(String[] fieldCode, List<String> lists)
			throws NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, ParseException {
		Tb_entry_detail_capture bean = new Tb_entry_detail_capture();
		Class<?> cls = bean.getClass();
		// 取出entry中的所有方法
		Field[] fields = cls.getDeclaredFields();
		Method[] methods = cls.getMethods();
		for (int i = 0; i < lists.size(); i++) {// 遍历属性数
			String fieldSetName = parSetName(fieldCode[i]);// 拼接set方法名
			if (!checkSetMet(methods, fieldSetName)) {// 判断是否有次属性
				continue;
			}
			Field field = cls.getDeclaredField(fieldCode[i].toLowerCase());// 返回指定私有成员字段
			Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());// 得到方法（方法名，字段属性类型）
			String value = lists.get(i);
			if (null != value && !"".equals(value)) {
				String fieldType = field.getType().getSimpleName();
				if ("String".equals(fieldType)) {
					fieldSetMet.invoke(bean, value);
				} else if ("Date".equals(fieldType)) {
					Date temp = parseDate(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
					Integer intval = Integer.parseInt(value);
					fieldSetMet.invoke(bean, intval);
				} else if ("Long".equalsIgnoreCase(fieldType)) {
					Long temp = Long.parseLong(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Double".equalsIgnoreCase(fieldType)) {
					Double temp = Double.parseDouble(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Boolean".equalsIgnoreCase(fieldType)) {
					Boolean temp = Boolean.parseBoolean(value);
					fieldSetMet.invoke(bean, temp);
				}
			}else{
				fieldSetMet.invoke(bean, value);
			}
		}
		return bean;
	}

	// 把List集合组装成entry_detail对象
	public static Tb_entry_detail_accept captureCreatEntryDetailAccept(String[] fieldCode, List<String> lists)
			throws NoSuchFieldException, SecurityException, NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, ParseException {
		Tb_entry_detail_accept bean = new Tb_entry_detail_accept();
		Class<?> cls = bean.getClass();
		// 取出entry中的所有方法
		Field[] fields = cls.getDeclaredFields();
		Method[] methods = cls.getMethods();
		for (int i = 0; i < lists.size(); i++) {// 遍历属性数
			String fieldSetName = parSetName(fieldCode[i]);// 拼接set方法名
			if (!checkSetMet(methods, fieldSetName)) {// 判断是否有次属性
				continue;
			}
			Field field = cls.getDeclaredField(fieldCode[i].toLowerCase());// 返回指定私有成员字段
			Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());// 得到方法（方法名，字段属性类型）
			String value = lists.get(i);
			if (null != value && !"".equals(value)) {
				String fieldType = field.getType().getSimpleName();
				if ("String".equals(fieldType)) {
					fieldSetMet.invoke(bean, value);
				} else if ("Date".equals(fieldType)) {
					Date temp = parseDate(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
					Integer intval = Integer.parseInt(value);
					fieldSetMet.invoke(bean, intval);
				} else if ("Long".equalsIgnoreCase(fieldType)) {
					Long temp = Long.parseLong(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Double".equalsIgnoreCase(fieldType)) {
					Double temp = Double.parseDouble(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Boolean".equalsIgnoreCase(fieldType)) {
					Boolean temp = Boolean.parseBoolean(value);
					fieldSetMet.invoke(bean, temp);
				}
			}else{
				fieldSetMet.invoke(bean, value);
			}
		}
		return bean;
	}


	// 把List集合组装成Template对象
	public static Tb_data_template creatTemp(String[] fieldCode, List<String> lists)
			throws NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, ParseException {

		System.out.println(Arrays.toString(fieldCode));

		Tb_data_template bean = new Tb_data_template();
		Class<?> cls = bean.getClass();
		// 取出entry中的所有方法
		Field[] fields = cls.getDeclaredFields();
		Method[] methods = cls.getMethods();
		for (int i = 0; i < lists.size(); i++) {// 遍历属性数
			String fieldSetName = parSetName(fieldCode[i]);// 拼接set方法名
			if (!checkSetMet(methods, fieldSetName)) {// 判断是否有次属性
				continue;
			}
			Field field = cls.getDeclaredField(fieldCode[i].toLowerCase());// 返回指定私有成员字段
			Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());// 得到方法（方法名，字段属性类型）
			String value = lists.get(i);
			if (null != value && !"".equals(value.trim())) {
				String fieldType = field.getType().getSimpleName();
				if ("String".equals(fieldType)) {
					fieldSetMet.invoke(bean, value);
				} else if ("Date".equals(fieldType)) {
					Date temp = parseDate(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
					Integer intval = Integer.parseInt(value);
					fieldSetMet.invoke(bean, intval);
				} else if ("Long".equalsIgnoreCase(fieldType)) {
					Long temp = Long.parseLong(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Double".equalsIgnoreCase(fieldType)) {
					Double temp = Double.parseDouble(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Boolean".equalsIgnoreCase(fieldType)) {
					Boolean temp = Boolean.parseBoolean(value);
					fieldSetMet.invoke(bean, temp);
				}
			}/*else{
				fieldSetMet.invoke(bean, null);
			}*/
		}
		return bean;
	}

	// 把List集合组装成Template对象
	public static Tb_data_template_sx creatSxTemp(String[] fieldCode, List<String> lists)
			throws NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, ParseException {

		System.out.println(Arrays.toString(fieldCode));

		Tb_data_template_sx bean = new Tb_data_template_sx();
		Class<?> cls = bean.getClass();
		// 取出entry中的所有方法
		Field[] fields = cls.getDeclaredFields();
		Method[] methods = cls.getMethods();
		for (int i = 0; i < lists.size(); i++) {// 遍历属性数
			String fieldSetName = parSetName(fieldCode[i]);// 拼接set方法名
			if (!checkSetMet(methods, fieldSetName)) {// 判断是否有次属性
				continue;
			}
			Field field = cls.getDeclaredField(fieldCode[i].toLowerCase());// 返回指定私有成员字段
			Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());// 得到方法（方法名，字段属性类型）
			String value = lists.get(i);
			if (null != value && !"".equals(value.trim())) {
				String fieldType = field.getType().getSimpleName();
				if ("String".equals(fieldType)) {
					fieldSetMet.invoke(bean, value);
				} else if ("Date".equals(fieldType)) {
					Date temp = parseDate(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
					Integer intval = Integer.parseInt(value);
					fieldSetMet.invoke(bean, intval);
				} else if ("Long".equalsIgnoreCase(fieldType)) {
					Long temp = Long.parseLong(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Double".equalsIgnoreCase(fieldType)) {
					Double temp = Double.parseDouble(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Boolean".equalsIgnoreCase(fieldType)) {
					Boolean temp = Boolean.parseBoolean(value);
					fieldSetMet.invoke(bean, temp);
				}
			}/*else{
				fieldSetMet.invoke(bean, null);
			}*/
		}
		return bean;
	}

	// 把List集合组装成codeset对象
	public static Tb_codeset creatcodeset(String[] fieldCode, List<String> lists)
			throws NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, ParseException {

		System.out.println(Arrays.toString(fieldCode));

		Tb_codeset bean = new Tb_codeset();
		Class<?> cls = bean.getClass();
		// 取出entry中的所有方法
		Field[] fields = cls.getDeclaredFields();
		Method[] methods = cls.getMethods();
		for (int i = 0; i < lists.size(); i++) {// 遍历属性数
			String fieldSetName = parSetName(fieldCode[i]);// 拼接set方法名
			if (!checkSetMet(methods, fieldSetName)) {// 判断是否有次属性
				continue;
			}
			Field field = cls.getDeclaredField(fieldCode[i].toLowerCase());// 返回指定私有成员字段
			Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());// 得到方法（方法名，字段属性类型）
			String value = lists.get(i);
			if (null != value && !"".equals(value)) {
				String fieldType = field.getType().getSimpleName();
				if ("String".equals(fieldType)) {
					fieldSetMet.invoke(bean, value);
				} else if ("Date".equals(fieldType)) {
					Date temp = parseDate(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
					Integer intval = Integer.parseInt(value);
					fieldSetMet.invoke(bean, intval);
				} else if ("Long".equalsIgnoreCase(fieldType)) {
					Long temp = Long.parseLong(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Double".equalsIgnoreCase(fieldType)) {
					Double temp = Double.parseDouble(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Boolean".equalsIgnoreCase(fieldType)) {
					Boolean temp = Boolean.parseBoolean(value);
					fieldSetMet.invoke(bean, temp);
				}
			}/*else{
				fieldSetMet.invoke(bean, null);
			}*/
		}
		return bean;
	}

	// 把List集合组装成声像的codeset对象
	public static Tb_codeset_sx creatSxcodeset(String[] fieldCode, List<String> lists)
			throws NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, ParseException {

		System.out.println(Arrays.toString(fieldCode));

		Tb_codeset_sx bean = new Tb_codeset_sx();
		Class<?> cls = bean.getClass();
		// 取出entry中的所有方法
		Field[] fields = cls.getDeclaredFields();
		Method[] methods = cls.getMethods();
		for (int i = 0; i < lists.size(); i++) {// 遍历属性数
			String fieldSetName = parSetName(fieldCode[i]);// 拼接set方法名
			if (!checkSetMet(methods, fieldSetName)) {// 判断是否有次属性
				continue;
			}
			Field field = cls.getDeclaredField(fieldCode[i].toLowerCase());// 返回指定私有成员字段
			Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());// 得到方法（方法名，字段属性类型）
			String value = lists.get(i);
			if (null != value && !"".equals(value)) {
				String fieldType = field.getType().getSimpleName();
				if ("String".equals(fieldType)) {
					fieldSetMet.invoke(bean, value);
				} else if ("Date".equals(fieldType)) {
					Date temp = parseDate(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
					Integer intval = Integer.parseInt(value);
					fieldSetMet.invoke(bean, intval);
				} else if ("Long".equalsIgnoreCase(fieldType)) {
					Long temp = Long.parseLong(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Double".equalsIgnoreCase(fieldType)) {
					Double temp = Double.parseDouble(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Boolean".equalsIgnoreCase(fieldType)) {
					Boolean temp = Boolean.parseBoolean(value);
					fieldSetMet.invoke(bean, temp);
				}
			}/*else{
				fieldSetMet.invoke(bean, null);
			}*/
		}
		return bean;
	}


	/**
	 * 格式化string为Date
	 *
	 * @param datestr
	 * @return date
	 * @throws ParseException
	 */
	private static Date parseDate(String datestr) throws ParseException {
		if (null == datestr || "".equals(datestr)) {
			return null;
		}
		String fmtstr = null;
		if (datestr.indexOf(':') > 0) {
			fmtstr = "yyyy-MM-dd HH:mm:ss";
		} else {
			fmtstr = "yyyy-MM-dd";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(fmtstr, Locale.UK);
		return sdf.parse(datestr);
	}

	/**
	 * 判断是否存在某属性的 set方法
	 *
	 * @param methods
	 * @param fieldSetMet
	 * @return boolean
	 */
	private static boolean checkSetMet(Method[] methods, String fieldSetMet) {
		for (Method met : methods) {
			if (fieldSetMet.equals(met.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 拼接在某属性的 set方法
	 *
	 * @param fieldName
	 * @return String
	 */
	private static String parSetName(String fieldName) {
		if (null == fieldName || "".equals(fieldName)) {
			return null;
		}
		return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}


	//方法功能描述: 判断是否是IE浏览器
	public static boolean isMSBrowser(HttpServletRequest request) {
		String[] IEBrowserSignals = {"MSIE", "Trident", "Edge"};
		String userAgent = request.getHeader("User-Agent");
		for (String signal : IEBrowserSignals) {
			if (userAgent.contains(signal)){
				return true;
			}
		}
		return false;
	}

	public static String baseConvertStr(String path) throws Exception {
		File file = new File(path);
		FileInputStream inputFile = new FileInputStream(file);
		byte[] buffer = new byte[(int)file.length()];
		inputFile.read(buffer);
		inputFile.close();
		return new BASE64Encoder().encode(buffer);
	}


	// 把List集合组装成Szh_entry_index_capture对象
	public static Szh_entry_index_capture creatCalloutEntryIndex(String[] fieldCode, List<String> lists)
			throws NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, ParseException {

		System.out.println(Arrays.toString(fieldCode));

		Szh_entry_index_capture bean = new Szh_entry_index_capture();
		Class<?> cls = bean.getClass();
		// 取出entry中的所有方法
		Field[] fields = cls.getDeclaredFields();
		Method[] methods = cls.getMethods();
		for (int i = 0; i < lists.size(); i++) {// 遍历属性数
			String fieldSetName = parSetName(fieldCode[i]);// 拼接set方法名
			if (!checkSetMet(methods, fieldSetName)) {// 判断是否有次属性
				continue;
			}
			Field field = cls.getDeclaredField(fieldCode[i].toLowerCase());// 返回指定私有成员字段
			Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());// 得到方法（方法名，字段属性类型）
			String value = lists.get(i);
			if (null != value && !"".equals(value)) {
				String fieldType = field.getType().getSimpleName();
				if ("String".equals(fieldType)) {
					fieldSetMet.invoke(bean, value);
				} else if ("Date".equals(fieldType)) {
					Date temp = parseDate(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
					Integer intval = Integer.parseInt(value);
					fieldSetMet.invoke(bean, intval);
				} else if ("Long".equalsIgnoreCase(fieldType)) {
					Long temp = Long.parseLong(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Double".equalsIgnoreCase(fieldType)) {
					Double temp = Double.parseDouble(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Boolean".equalsIgnoreCase(fieldType)) {
					Boolean temp = Boolean.parseBoolean(value);
					fieldSetMet.invoke(bean, temp);
				}
			}else{
				fieldSetMet.invoke(bean, value);
			}
		}
		return bean;
	}

	// 把List集合组装成Szh_callout_entry对象
	public static Szh_callout_entry creatCalloutEntry(String[] fieldCode, List<String> lists)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NoSuchFieldException, ParseException {
		Szh_callout_entry bean = new Szh_callout_entry();
		Class<?> cls = bean.getClass();
		// 取出entry中的所有方法
		Field[] fields = cls.getDeclaredFields();
		Method[] methods = cls.getMethods();
		for (int i = 0; i < lists.size(); i++) {// 遍历属性数
			String fieldSetName = parSetName(fieldCode[i]);// 拼接set方法名
			if (!"tracktext".equals(fieldCode[i])&&!"tracknumber".equals(fieldCode[i])&&!"archivecode".equals(fieldCode[i])) {// 判断是否有次属性
				continue;
			}
			Field field = cls.getDeclaredField(fieldCode[i].toLowerCase());// 返回指定私有成员字段
			Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());// 得到方法（方法名，字段属性类型）
			String value = lists.get(i);
			if (null != value && !"".equals(value)) {
				String fieldType = field.getType().getSimpleName();
				if ("String".equals(fieldType)) {
					fieldSetMet.invoke(bean, value);
				} else if ("Date".equals(fieldType)) {
					Date temp = parseDate(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
					Integer intval = Integer.parseInt(value);
					fieldSetMet.invoke(bean, intval);
				} else if ("Long".equalsIgnoreCase(fieldType)) {
					Long temp = Long.parseLong(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Double".equalsIgnoreCase(fieldType)) {
					Double temp = Double.parseDouble(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Boolean".equalsIgnoreCase(fieldType)) {
					Boolean temp = Boolean.parseBoolean(value);
					fieldSetMet.invoke(bean, temp);
				}
			}else{
				fieldSetMet.invoke(bean, value);
			}
		}
		return bean;
	}

	// 把List集合组装成entry_index对象
	public static Tb_right_organ creatRightOrgan(String[] fieldCode, List<String> lists)
			throws NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, ParseException {

		System.out.println(Arrays.toString(fieldCode));
		Tb_right_organ bean = new Tb_right_organ();
		Class<?> cls = bean.getClass();
		// 取出entry中的所有方法
		Field[] fields = cls.getDeclaredFields();
		Method[] methods = cls.getMethods();
		for (int i = 0; i < lists.size(); i++) {// 遍历属性数
			String fieldSetName = parSetName(fieldCode[i]);// 拼接set方法名
			if (!checkSetMet(methods, fieldSetName)) {// 判断是否有次属性
				continue;
			}
			Field field = cls.getDeclaredField(fieldCode[i].toLowerCase());// 返回指定私有成员字段
			Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());// 得到方法（方法名，字段属性类型）
			String value = lists.get(i);
			try{
				String fieldType = field.getType().getSimpleName();
				if (null != value && !"".equals(value)) {
					if ("String".equals(fieldType)) {
						fieldSetMet.invoke(bean, value);
					} else if ("Date".equals(fieldType)) {
						Date temp = parseDate(value);
						fieldSetMet.invoke(bean, temp);
					} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
						Integer intval = Integer.parseInt(value);
						fieldSetMet.invoke(bean, intval);
					} else if ("Long".equalsIgnoreCase(fieldType)) {
						Long temp = Long.parseLong(value);
						fieldSetMet.invoke(bean, temp);
					} else if ("Double".equalsIgnoreCase(fieldType)) {
						Double temp = Double.parseDouble(value);
						fieldSetMet.invoke(bean, temp);
					} else if ("Boolean".equalsIgnoreCase(fieldType)) {
						Boolean temp = Boolean.parseBoolean(value);
						fieldSetMet.invoke(bean, temp);
					}
				}else{
					if ("Integer".equals(fieldType) || "int".equals(fieldType)||"Long".equalsIgnoreCase(fieldType)||"Double".equalsIgnoreCase(fieldType)) {
						fieldSetMet.invoke(bean, 0);
					}
					fieldSetMet.invoke(bean, value);
				}
			}catch(Exception e){
				e.printStackTrace();
			}

		}
		return bean;
	}

	// 把List集合组装成codeset对象
	public static Tb_thematic_detail creatthematicDetail(String[] fieldCode, List<String> lists)
			throws NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, ParseException {

		System.out.println(Arrays.toString(fieldCode));

		Tb_thematic_detail bean = new Tb_thematic_detail();
		Class<?> cls = bean.getClass();
		// 取出entry中的所有方法
		Field[] fields = cls.getDeclaredFields();
		Method[] methods = cls.getMethods();
		for (int i = 0; i < lists.size(); i++) {// 遍历属性数
			String fieldSetName = parSetName(fieldCode[i]);// 拼接set方法名
			if (!checkSetMet(methods, fieldSetName)) {// 判断是否有次属性
				continue;
			}
			Field field = cls.getDeclaredField(fieldCode[i].toLowerCase());// 返回指定私有成员字段
			Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());// 得到方法（方法名，字段属性类型）
			String value = lists.get(i);
			if (null != value && !"".equals(value)) {
				String fieldType = field.getType().getSimpleName();
				if ("String".equals(fieldType)) {
					fieldSetMet.invoke(bean, value);
				} else if ("Date".equals(fieldType)) {
					Date temp = parseDate(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
					Integer intval = Integer.parseInt(value);
					fieldSetMet.invoke(bean, intval);
				} else if ("Long".equalsIgnoreCase(fieldType)) {
					Long temp = Long.parseLong(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Double".equalsIgnoreCase(fieldType)) {
					Double temp = Double.parseDouble(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Boolean".equalsIgnoreCase(fieldType)) {
					Boolean temp = Boolean.parseBoolean(value);
					fieldSetMet.invoke(bean, temp);
				}
			}/*else{
				fieldSetMet.invoke(bean, null);
			}*/
		}
		return bean;
	}

	// 把List集合组装成equipment对象
	public static Tb_equipment creatEquipment(String[] fieldCode, List<String> lists)
			throws NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, ParseException {

		System.out.println(Arrays.toString(fieldCode));

		Tb_equipment bean = new Tb_equipment();
		Class<?> cls = bean.getClass();
		// 取出equipment中的所有方法
		Field[] fields = cls.getDeclaredFields();
		Method[] methods = cls.getMethods();
		for (int i = 0; i < lists.size(); i++) {// 遍历属性数
			String fieldSetName = parSetName(fieldCode[i]);// 拼接set方法名
			if (!checkSetMet(methods, fieldSetName)) {// 判断是否有次属性
				continue;
			}
			Field field = cls.getDeclaredField(fieldCode[i].toLowerCase());// 返回指定私有成员字段
			Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());// 得到方法（方法名，字段属性类型）
			String value = lists.get(i);
			if (null != value && !"".equals(value)) {
				String fieldType = field.getType().getSimpleName();
				if ("String".equals(fieldType)) {
					fieldSetMet.invoke(bean, value);
				} else if ("Date".equals(fieldType)) {
					Date temp = parseDate(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
					Integer intval = Integer.parseInt(value);
					fieldSetMet.invoke(bean, intval);
				} else if ("Long".equalsIgnoreCase(fieldType)) {
					Long temp = Long.parseLong(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Double".equalsIgnoreCase(fieldType)) {
					Double temp = Double.parseDouble(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Boolean".equalsIgnoreCase(fieldType)) {
					Boolean temp = Boolean.parseBoolean(value);
					fieldSetMet.invoke(bean, temp);
				}
			}
		}
		return bean;
	}

	// 把List集合组装成creatMetadataTemp对象
	public static Tb_metadata_temp creatMetadataTemp(String[] fieldCode, List<String> lists)
			throws NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, ParseException {

		System.out.println(Arrays.toString(fieldCode));

		Tb_metadata_temp bean = new Tb_metadata_temp();
		Class<?> cls = bean.getClass();
		// 取出entry中的所有方法
		Field[] fields = cls.getDeclaredFields();
		Method[] methods = cls.getMethods();
		for (int i = 0; i < lists.size(); i++) {// 遍历属性数
			String fieldSetName = parSetName(fieldCode[i]);// 拼接set方法名
			if (!checkSetMet(methods, fieldSetName)) {// 判断是否有次属性
				continue;
			}
			Field field = cls.getDeclaredField(fieldCode[i].toLowerCase());// 返回指定私有成员字段
			Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());// 得到方法（方法名，字段属性类型）
			String value = lists.get(i);
			if (null != value && !"".equals(value)) {
				String fieldType = field.getType().getSimpleName();
				if ("String".equals(fieldType)) {
					fieldSetMet.invoke(bean, value);
				} else if ("Date".equals(fieldType)) {
					Date temp = parseDate(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
					Integer intval = Integer.parseInt(value);
					fieldSetMet.invoke(bean, intval);
				} else if ("Long".equalsIgnoreCase(fieldType)) {
					Long temp = Long.parseLong(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Double".equalsIgnoreCase(fieldType)) {
					Double temp = Double.parseDouble(value);
					fieldSetMet.invoke(bean, temp);
				} else if ("Boolean".equalsIgnoreCase(fieldType)) {
					Boolean temp = Boolean.parseBoolean(value);
					fieldSetMet.invoke(bean, temp);
				}
			}/*else{
				fieldSetMet.invoke(bean, null);
			}*/
		}
		return bean;
	}

	/**
	 * 对象装换map
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> transBean2Map(Object obj) {
		if (obj == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<>();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();
				// 过滤class属性
				if (!key.equals("class") && !key.equals("pageNo") && !key.equals("pageSize")) {
					// 得到property对应的getter方法
					Method getter = property.getReadMethod();
					Object value = getter.invoke(obj);
					map.put(key, value);
				}
			}
		} catch (Exception e) {
			System.out.println("transBean2Map Error " + e);
		}
		return map;
	}


	public static void main(String[] ages){
		List<String> list = new ArrayList<>();
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		System.out.println(list);
		String str = "";
		//System.out.println(str.toString());
	}
}