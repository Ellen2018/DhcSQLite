package com.ellen.dhcsqlitelibrary.table.annotation;

import com.ellen.dhcsqlitelibrary.table.operate.AutoDesignOperate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoOperate {

    Class<? extends AutoDesignOperate> value() default AutoDesignOperate.class;

}
