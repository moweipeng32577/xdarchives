package com.wisdom.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 数据库兼容性处理类 对于不兼容的数据库操作，通过此类方法进行转化，保持业务操作的统一性
 * 兼容性目前处理Mysql与Oracle，其他数据库默认为Mysql处理方式 Created by Rong on 2018/7/13.
 */
@Component
public class DBCompatible {

	private static String DB_CURRENT; // 当前系统所使用的数据库
	private final static String DB_MYSQL = "mysql"; // Mysql数据库
	private final static String DB_ORACLE = "oracle"; // Oracle数据库
	private final static String DB_SQLSERVER = "sqlserver";//sqlserver数据库

	private final static DBCompatible INSTANCE = new DBCompatible();
	private final Logger logger = LoggerFactory.getLogger(DBCompatible.class);

	private String database;
	/**
	 * 重写构造方法 加载此类时，根据数据库驱动程序，识别当前的数据库类型 默认为Mysql类型
	 * 识别依据为驱动程序名称中是否包含mysql、oracle关键字
	 */
	public DBCompatible() {
		DB_CURRENT = DB_MYSQL;
		InputStream inputStream = this.getClass().getResourceAsStream("/application.properties");
		try {
			// 读取配置文件设置的数据库驱动程序
			Properties properties = new Properties();
			properties.load(inputStream);
			String driverclass = properties.getProperty("spring.datasource.driverClassName");
			// 设置当前系统数据库
			if (driverclass.toLowerCase().contains(DB_ORACLE)) {
				DB_CURRENT = DB_ORACLE;
			}else if(driverclass.toLowerCase().contains(DB_SQLSERVER)) {
				DB_CURRENT = DB_SQLSERVER;
			}
			if(DB_CURRENT.equals(DB_MYSQL)){
				String url = properties.getProperty("spring.datasource.url");
				database = url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"));
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
	}

	public static DBCompatible getInstance() {
		return INSTANCE;
	}

	/**
	 * 获取字符串转化为整型的数据库表达式 Mysql为convert(xxx, SIGNED) Oracle为to_number(xxx)
	 *
	 * @param field
	 *            转换字段
	 * @return 数据库表达式
	 */
	public String findExpressionOfToNumber(String field) {
		String expresssion = "";
		switch (DB_CURRENT) {
			case DB_MYSQL:
				expresssion = "CONVERT(" + field + ",SIGNED)";
				break;
			case DB_ORACLE:
				expresssion = "TO_NUMBER(" + field + ")";
				break;
			case DB_SQLSERVER:
				expresssion = "CAST(" + field +" AS INT)";
				break;
			default:
				expresssion = "CONVERT(" + field + ",SIGNED)";
				break;
		}
		return expresssion;
	}

	/**
	 * 获取计算项的正则表达式
	 *
	 * @param field
	 * @return
	 */
	public String findExpressionOfRegExp(String field) {
		String expresssion = "";
		switch (DB_CURRENT) {
			case DB_MYSQL:
				expresssion = field + " regexp '^[0-9]+$'";
				break;
			case DB_ORACLE:
				expresssion = "regexp_like(" + field + ",'^[1-9]\\d*$')";
				break;
			case DB_SQLSERVER:
				expresssion = "ISNUMERIC(" + field + ") = 1" ;
				break;
			default:
				expresssion = field + " regexp '^[0-9]+$'";
				break;
		}
		return expresssion;
	}

	public String findIntType() {
		String expresssion = "";
		switch (DB_CURRENT) {
			case DB_MYSQL:
				expresssion = "signed";
				break;
			case DB_ORACLE:
				expresssion = "int";
				break;
			case DB_SQLSERVER:
				expresssion = "int" ;
				break;
			default:
				expresssion = "signed";
				break;
		}
		return expresssion;
	}

	/**
	 * 获取日期增量天数后小于当前日期的数据库表达式
	 *
	 * @param basefield
	 * @param addfield
	 * @return
	 */
	public String findExpressionOfDateAddLeNow(String basefield, String addfield) {
		String expresssion = "";
		switch (DB_CURRENT) {
			case DB_MYSQL:
				expresssion = "now()";
				break;
			case DB_ORACLE:
				expresssion = "sysdate";
				break;
			case DB_SQLSERVER:
				expresssion = "getdate()";
				break;
			default:
				expresssion = "now()";
				break;
		}
		return findExpressionOfDateAddLeValue(basefield, addfield, expresssion);
	}

	/**
	 * 获取日期增量天数后小于指定日期的数据库表达式
	 *
	 * @param basefield
	 * @param addfield
	 * @param value
	 * @return
	 */
	public String findExpressionOfDateAddLeValue(String basefield, String addfield, String value) {
		String expresssion = "";
		switch (DB_CURRENT) {
			case DB_MYSQL:
				expresssion = "DATE_ADD(" + basefield + ", INTERVAL " + addfield + " DAY) <= date(" + value + ")";
				break;
			case DB_ORACLE:
				expresssion = basefield + "+" + addfield + " <= to_char(" + value + ",'yyyyMMdd')";
				break;
			case DB_SQLSERVER:
				expresssion = "DATEADD(day," + addfield + "," + basefield + ") <= " + value;
				break;
			default:
				expresssion = "DATE_ADD(" + basefield + ", INTERVAL " + addfield + " DAY) <= date(" + value + ")";
				break;
		}
		return expresssion;
	}

	/**
	 * 用于鉴定销毁模块查找鉴定过期记录
	 * 鉴定标准：1、文件日期 filedate + 保管期限 entryretention （到期时间duetime为空）
	 * @return
	 */
	public String findAppraisalOverdueData(String value,String nodeid,int limit,String type,String contentSql) {
		String expresssion = "";
		switch (DB_CURRENT) {
			case DB_MYSQL:
				expresssion = "DATE_ADD(date_format(substring_index(replace("+value+",'-',''),' ',1),'%Y%m%d'), INTERVAL substring_index(CASE EntryRetention WHEN '长期' THEN '30年' WHEN '三十年' THEN '30年' WHEN '短期' "
						+ "THEN '10年' ELSE EntryRetention END,'年',1) YEAR) <= replace(date(now()),'-','')  and FileDate !='' and EntryRetention !='' and  "
						+ "EntryRetention is not null and (duetime = '' or duetime is null)  and EntryRetention != '永久'  and FileDate >='1753' ";
					if(limit!=0)
						expresssion+= " and NodeID = '"+ nodeid + "' ";
				break;
//			case DB_ORACLE:
//				expresssion = "add_months(to_date(substr(replace(replace(replace("+value+",'-',''),'/',''),'.',''),1,8),'yyyy.mm.dd'), to_number(substr(CASE EntryRetention WHEN "
//						+ "'长期' THEN '30年' WHEN '短期' THEN '10年' ELSE EntryRetention END,0,instr(CASE EntryRetention WHEN "
//						+ "'长期' THEN '30年' WHEN '短期' THEN '10年' ELSE EntryRetention END,'年',1, 1)-1))*12)" +
//						"  < sysdate and EntryRetention is not null";
//				break;
			case DB_SQLSERVER:
				expresssion = "DATEADD(year, cast(substring(CASE EntryRetention WHEN '长期' THEN '30年' WHEN '短期' THEN '10年' WHEN '三十年' THEN '30年' " +
						"ELSE EntryRetention END,0,charindex('年', CASE EntryRetention WHEN '长期' THEN '30年' WHEN '短期' THEN '10年' " +
						"ELSE EntryRetention END)) as int), substring(replace(replace(replace("+value+",'-',''),'/',''),'.',''),1,4)) <= getdate() and EntryRetention !='' and EntryRetention is not null " +
						"and (duetime = '' or duetime is null)  and EntryRetention != '永久' and FileDate!='' and FileDate is not null  and FileDate >='1753' ";
					if(limit!=0)
						expresssion+=" and NodeID = '"+ nodeid + "' ";
				break;
			default:
				expresssion = "DATE_ADD( DATE_ADD(date_format(substring_index(replace("+value+",'-',''),' ',1),'%Y%m%d'), INTERVAL substring_index(CASE EntryRetention WHEN '长期' THEN '30年' WHEN '短期' "
						+ "THEN '10年' ELSE EntryRetention END,'年',1) YEAR), INTERVAL substring_index(CASE DuentryRetention WHEN '长期' THEN '30年' WHEN '短期' WHEN '三十年' THEN '30年' "
						+ "THEN '10年' WHEN '30年' THEN '30年' WHEN '10年' THEN '10年' ELSE '' END,'年',1) YEAR) <= date(now()) and FileDate!='' and FileDate is not null and EntryRetention !='' and  "
						+ "EntryRetention is not null  and FileDate >='1753' ";
				break;
		}
		expresssion+=contentSql;
		return findNoSpecificationDate(expresssion, value,nodeid,limit,type);
	}

	/**
	 * 过滤不规范日期(匹配：2018.07.16、20180716、2018-07-16、2018/07/16格式)
	 * (20180715-20180716(主要)、2018-07-15 15:58:00)
	 */
	private String findNoSpecificationDate(String sql, String value,String nodeid,int limit,String type) {
		String expresssion = "";
		switch (DB_CURRENT) {
			case DB_MYSQL:
				expresssion = value+" regexp '(^[0-9]{4}.((0[1-9])|(1[0-2])).((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1]))$)"
						+ "|(^[0-9]{4}((0[1-9])|(1[0-2]))((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1]))$)"
						+ "|(^[0-9]{4}-((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1]))$)"
						+ "|(^[0-9]{4}/((0[1-9])|(1[0-2]))/((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1]))$)"
						+ "|(^[0-9]{8}-[0-9]{8}$)"
						+ "|(^[0-9]{4}-((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1])) ((0[0-9])|(1[0-9])|(2[0-3])):"
						+ "((0[0-9])|(1[0-9])|(2[0-9])|(3[0-9])|(4[0-9])|(5[0-9])):"
						+ "((0[0-9])|(1[0-9])|(2[0-9])|(3[0-9])|(4[0-9])|(5[0-9]))$)'";
				break;
//			case DB_ORACLE:
//				expresssion = "regexp_like("+value+",'(^[0-9]{4}.((0[1-9])|(1[0-2])).((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1]))$)"//2018.07.16
//						+ "|(^[0-9]{4}((0[1-9])|(1[0-2]))((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1]))$)"//20180716
//						+ "|(^[0-9]{4}-((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1]))$)"//2018-07-16
//						+ "|(^[0-9]{4}/((0[1-9])|(1[0-2]))/((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1]))$)"//2018/07/16
//						+ "|(^[0-9]{8}-[0-9]{8}$)"//20180715-20180716
//						+ "|(^[0-9]{4}-((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1])) ((0[0-9])|(1[0-9])|(2[0-3])):"
//						+ "((0[0-9])|(1[0-9])|(2[0-9])|(3[0-9])|(4[0-9])|(5[0-9])):"
//						+ "((0[0-9])|(1[0-9])|(2[0-9])|(3[0-9])|(4[0-9])|(5[0-9]))$)'";
//				break;
			case DB_SQLSERVER:
                expresssion = "("+value+" not like '[0-9][0-9][0-9][0-9][0][0][0][0]' " +
                        " and "+value+" not like '[0-9][0-9][0-9][0-9][0-9][0-9][0][0]' " +
                        " and "+value+" not like '[0][0][0][0][0-9][0-9][0-9][0-9]' " +
                        " and "+value+" not like '[0-9][0-9][0-9][0-9][0][0][0][0][-][0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]' " +
                        " and "+value+" not like '[0][0][0][0][0-1][0-9][0-3][0-9][-][0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]' " +
                        " and "+value+" not like '[0][0][0][0][0][2][3][1-9]' " +
                        " and "+value+" not like '[0-9][0-9][0-9][0-9][0][2][3][1-9]') ";
				break;
			default:
				expresssion = value+" regexp '(^[0-9]{4}.((0[1-9])|(1[0-2])).((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1]))$)"
						+ "|(^[0-9]{4}((0[1-9])|(1[0-2]))((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1]))$)"
						+ "|(^[0-9]{4}-((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1]))$)"
						+ "|(^[0-9]{4}/((0[1-9])|(1[0-2]))/((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1]))$)"
						+ "|(^[0-9]{8}-[0-9]{8}$)"
						+ "|(^[0-9]{4}-((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1])) ((0[0-9])|(1[0-9])|(2[0-3])):"
						+ "((0[0-9])|(1[0-9])|(2[0-9])|(3[0-9])|(4[0-9])|(5[0-9])):"
						+ "((0[0-9])|(1[0-9])|(2[0-9])|(3[0-9])|(4[0-9])|(5[0-9]))$)'";
				break;
		}
		expresssion = sql + " and " + expresssion;
		return findduetimeOverdueData(expresssion,nodeid,limit,type);
	}

	/**
	 * 用于鉴定销毁模块查找鉴定过期记录2
	 * 鉴定标准：2、到期时间duetime（到期时间不为空）
	 * @return
	 */
	public String findduetimeOverdueData(String sql,String nodeid,int limit,String type) {
		String expresssion = "";
		if("count".equals(type)){
			switch (DB_CURRENT) {
				case DB_MYSQL:
					expresssion = "union select count(entryid) from v_index_detail where  duetime <= replace(date(now()),'-','') and FileDate >='1753' and (duetime is not null or duetime != '') and EntryRetention != '永久' and NodeID = '"+ nodeid + "'";
					break;
				case DB_ORACLE:
					break;
				case DB_SQLSERVER:
					expresssion = "union select count(entryid) from v_index_detail where duetime <= getdate() and FileDate >='1753' and (duetime is not null or duetime != '') and EntryRetention != '永久' and NodeID = '"+ nodeid + "'";
					break;
				default:
					break;
			}
		}else{
			switch (DB_CURRENT) {
				case DB_MYSQL:
					expresssion = "union select * from v_index_detail where  duetime <= replace(date(now()),'-','') and (duetime is not null or duetime != '') and FileDate!='' and FileDate is not null and EntryRetention != '永久' and FileDate >='1753' ";
					if(limit!=0){
						expresssion+=" and NodeID = '"+ nodeid + "' ";
					}
					break;
				case DB_ORACLE:
					break;
				case DB_SQLSERVER:
					if(limit==0){
						expresssion = "union select * from v_index_detail where duetime <= getdate() and (duetime is not null or duetime != '') and EntryRetention != '永久' and FileDate!='' and FileDate is not null and FileDate >='1753' ";
					}else {
						expresssion = "union select Top(" + limit + ")* from v_index_detail where duetime <= getdate() and (duetime is not null or duetime != '') and EntryRetention != '永久' and FileDate!='' and FileDate is not null and FileDate >='1753' and NodeID = '"+ nodeid + "'";
					}
					break;
				default:
					break;
			}
		}
		return sql + expresssion;
	}


	/**
	 * 处理文件名后缀截取
	 */
	public String subAddPdf() {
		String expresssion;
		switch (DB_CURRENT) {
			case DB_MYSQL:
				expresssion = "(es.filename=concat(LEFT(e.filename, CHAR_LENGTH(e.filename) - LOCATE('.', REVERSE(e.filename))),'.pdf') "
						+ "or es.filename=concat(concat(concat(LEFT(e.filename, CHAR_LENGTH(e.filename) - LOCATE('.', REVERSE(e.filename))),'_'),right(e.filename, LOCATE('.', REVERSE(e.filename))-1)),'.pdf'))";
				break;
			case DB_ORACLE:
				expresssion = "(es.filename=concat(substr(e.filename,0,instr(e.filename, '.', -1, 1) - 1),'.pdf') "
						+ "or es.filename=concat(concat(concat(substr(e.filename,0,instr(e.filename, '.', -1, 1) - 1),'_'),substr(e.filename,instr(e.filename, '.', -1, 1)+1,length(e.filename))),'.pdf'))";
				break;
			case DB_SQLSERVER:
				expresssion = "(es.filename=concat(left(e.filename, len(e.filename) - charindex('.', REVERSE(e.filename))),'.pdf') " +
						"or es.filename=concat(left(e.filename, len(e.filename) - charindex('.', REVERSE(e.filename))), '_', right(e.filename, charindex('.', REVERSE(e.filename)) - 1),'.pdf'))";
				break;
			default:
				expresssion = "(es.filename=concat(LEFT(e.filename, CHAR_LENGTH(e.filename) - LOCATE('.', REVERSE(e.filename))),'.pdf') "
						+ "or es.filename=concat(concat(concat(LEFT(e.filename, CHAR_LENGTH(e.filename) - LOCATE('.', REVERSE(e.filename))),'_'),right(e.filename, LOCATE('.', REVERSE(e.filename))-1)),'.pdf'))";
				break;
		}
		return expresssion;
	}

	/**
	 * sql分页
	 */
	public String sqlPages(String sql,int page,int limit) {

		switch (DB_CURRENT) {
			case DB_MYSQL:
				sql = sql+" limit "+page*limit+","+limit;
				break;
			case DB_ORACLE:
				sql = "select top "+limit+" o.* from (select row_number() over(order by entryid) as rownumber,* from("+sql+") as o where rownumber>"+page*limit;
				break;
			case DB_SQLSERVER:
				//sql = "select * from(select a.*,ROWNUM rn from("+sql+") a where ROWNUM<="+(page*limit+limit)+") where rn>"+page*limit;
				sql =sql+ " offset "+page*limit+" rows fetch next "+limit+" rows only";
				break;
			default:
				sql = sql;
				break;
		}
		return sql;
	}

	/**
	 * sql首条记录
	 */
	public String sqlFirstRecord(String sql,String zsql) {
		//String sql="select * from tb_data_node_mdaflag a where a.is_media = 1 and a.nodeid = (select nodeid from tb_data_node where classid = ?1 limit 1)";
		//String sql="select * from tb_data_node_mdaflag a where a.is_media = 1 and a.nodeid = (select "+" top 1 "+" nodeid from tb_data_node where classid = ?1 "+")";
		switch (DB_CURRENT) {
			case DB_MYSQL:
				sql = sql+zsql+" limit 1)";
				break;
			case DB_ORACLE:
				sql =sql+ " top 1 "+zsql+")";
				break;
			case DB_SQLSERVER:
				sql =sql+ " top 1 "+zsql+")";
				break;
			default:
				sql = sql+zsql+" limit 1)";
				break;
		}
		return sql;
	}

	/**
	 * 处理节点编码
	 */
	public StringBuffer updateNodeCode(String[] nodeids) {
		StringBuffer expresssion = new StringBuffer();
		switch (DB_CURRENT){
			case DB_MYSQL:
				expresssion.append("concat(left(nodecode,length(nodecode)-3),'001',right(nodecode,3)) where nodeid in (");
				break;
			case DB_ORACLE:
				expresssion.append("substr(nodecode,0,length(nodecode)-3)||'001'||substr(nodecode,length(nodecode)-2) where nodeid in (");
				break;
			case DB_SQLSERVER:
				expresssion.append("concat(left(nodecode,len(nodecode)-3),'001',right(nodecode,3)) where nodeid in (");
				break;
			default:
				break;
		}
		for (int i = 0; i < nodeids.length; i++) {
			expresssion.append("'").append(nodeids[i]).append("'");
			if (i < nodeids.length - 1) {
				expresssion.append(",");
			} else {
				expresssion.append(")");
			}
		}
		return expresssion;
	}

	public String findExpressionOf() {
		String expresssion = "";
		switch (DB_CURRENT) {
			case DB_MYSQL:
				expresssion = "escape '/'";
				break;
			case DB_ORACLE:
				expresssion = "";
				break;
			default:
				expresssion = "escape '/'";
				break;
		}
		return expresssion;
	}

	/**
	 * 获取查询表字段长度的sql语句
	 * @param tablename	表名
	 * @param field	字段名
	 * @return
	 */
	public String findQueryFieldLengthSQL(String tablename, String field) {
		String sql = "";
		switch (DB_CURRENT) {
			case DB_SQLSERVER:
				sql = "select prec from syscolumns where name = '" + field
						+ "' and id = (select object_id from sys.tables where name = '" + tablename + "')";
				break;
			case DB_ORACLE:
				sql = "select data_length from user_tab_columns where table_name = '" + tablename.toUpperCase()
						+ "' and column_name = '"+ field.toUpperCase() +"'";
				break;
			case DB_MYSQL:
				sql = "select DISTINCT CHARACTER_MAXIMUM_LENGTH from information_schema.columns "
						+ "WHERE table_schema = '" + database + "' and table_name = '" + tablename
						+ "' AND column_name = '" + field + "'";
				break;
			default:
				sql = "select DISTINCT CHARACTER_MAXIMUM_LENGTH from information_schema.columns "
						+ "WHERE table_schema = '" + database + "' and table_name = '" + tablename
						+ "' AND column_name = '" + field + "'";
				break;
		}
		return sql;
	}

    /**
     * 获取当前使用的是什么数据库
     * @return
     */
	public static String getDBVersion(){
	    return DB_CURRENT;
    }

    /** 字符串类型字段排序 以及有null值排序
     * @param field 字段
     */
    public String getNullSort(String field){
        String str = "";
        String s = field+"c1";
        switch (DB_CURRENT){
            case(DB_MYSQL) :
                str = s.replace("c","*");
                break;
            case(DB_SQLSERVER):
                str = s.replace("c","*");
                break;
            case (DB_ORACLE):
                str = field+" nulls last";
                break;
        }
        return str;
    }

    //递归with as 方式引用
	public String findWithAs() {
		String expresssion = "";
		switch (DB_CURRENT) {
			case DB_MYSQL:
				expresssion = "RECURSIVE";
				break;
			case DB_ORACLE:
				expresssion = "";
				break;
			case DB_SQLSERVER:
				expresssion = "";
				break;
			default:
				expresssion = "";
				break;
		}
		return expresssion;
	}

	/**
	 * 获取表的所有字段
	 */
	public String getTableAllField(String tablename){
		String querysql = "select COLUMN_NAME from " +
				"information_schema.COLUMNS " +
				"where table_name = '"+tablename+"' and " +
				"table_schema = '"+database+ "'";
		return  querysql;
	}
}