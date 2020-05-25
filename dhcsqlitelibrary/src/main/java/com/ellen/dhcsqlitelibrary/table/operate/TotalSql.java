package com.ellen.dhcsqlitelibrary.table.operate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此类能完成所有sql语句
 * 多复杂的都可以
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TotalSql {
    String value();
}
