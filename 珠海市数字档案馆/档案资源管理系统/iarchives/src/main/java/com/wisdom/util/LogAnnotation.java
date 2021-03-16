package com.wisdom.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志配置注解接口
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD})
public @interface LogAnnotation {
    String module() default "其他";//模块
    String startDesc() default "";//描述开始文字
    String endDesc() default "";//描述结束文字
    String connect() default "";//连接字符串(多个以逗号分隔)
    String sites() default "";//参数位置(需要读取的信息的参数适用于原始类型,对象,字符串,list未实现)
    String fields() default "";//需要获取参数字段(多字段以逗号分隔，多对象以&风格如:(name,sex&title))
}
