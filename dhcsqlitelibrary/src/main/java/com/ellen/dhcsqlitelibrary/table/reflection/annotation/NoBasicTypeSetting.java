package com.ellen.dhcsqlitelibrary.table.reflection.annotation;

import com.ellen.sqlitecreate.createsql.helper.SQLFieldTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoBasicTypeSetting {

    /**
     * 数据类型
     * @return
     */
    SQLFieldTypeEnum sqlFiledType();

    /**
     * 数据长度
     * <= 0 无固定长度
     * > 0  设置固定长度
     * @return
     */
    int length();

    /**
     * 操作类型
     * 固定值类型
     */
    Operate operate() default Operate.JSON;

    /**
     * 存储哪个值 需要指定属性名字
     */

    String valueName() default "";

}