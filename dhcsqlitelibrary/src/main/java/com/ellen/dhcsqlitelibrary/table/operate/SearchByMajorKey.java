package com.ellen.dhcsqlitelibrary.table.operate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * 用于查找的
 * 使用者可以自定义查找方法封装
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchByMajorKey {
    String whereSql();
    String orderSql() default "";
}