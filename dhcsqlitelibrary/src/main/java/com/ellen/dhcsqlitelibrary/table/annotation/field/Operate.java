package com.ellen.dhcsqlitelibrary.table.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Operate {

    /**
     * 操作类型
     * 固定值类型
     */
    OperateEnum operate() default OperateEnum.JSON;

    /**
     * 存储哪个值 需要指定属性名字
     */

    String valueName() default "";

}