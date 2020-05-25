package com.ellen.dhcsqlitelibrary.table.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 应对于数据结构类型的数据
 * 支持:
 * 数组
 * List
 * Set
 * Map
 * Stack
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataStructure {

    /**
     * 操作类型
     * 固定值类型
     */
    OperateEnum operate() default OperateEnum.JSON;
}