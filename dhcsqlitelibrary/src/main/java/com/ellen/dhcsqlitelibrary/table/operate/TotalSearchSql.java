package com.ellen.dhcsqlitelibrary.table.operate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * 用于完成所有有返回数据的sql
 * 使用此注解时您需要写完整的Sql语句
 *
 * 此种方式有别于Search注解的是它可以用来完成各种复杂的查询，例如:多表查询，级联查询等
 *
 * 使用者可以自定义查找方法封装
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TotalSearchSql {
    String value();
}
