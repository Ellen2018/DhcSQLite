package com.ellen.dhcsqlitelibrary.table.annotation.field.bound;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Default {
    DefaultValueEnum defaultValueEnum();
    byte byteValue() default 0;
    short shortValue() default 0;
    int intValue() default 0;
    long longValue() default 0;
    char charValue() default 0;
    String strValue() default "";
    float floatValue() default 0;
    double doubleValue() default 0;

}
