package com.ellen.dhcsqlite.bean;

import com.ellen.dhcsqlitelibrary.table.annotation.field.bound.Default;
import com.ellen.dhcsqlitelibrary.table.annotation.field.bound.DefaultValueEnum;

public class TestDefault {

    @Default(defaultValueEnum = DefaultValueEnum.STRING,strValue = "字符串默认值")
    private byte a1;
    @Default(defaultValueEnum = DefaultValueEnum.SHORT,shortValue = 2)
    private short a2;
    @Default(defaultValueEnum = DefaultValueEnum.INT,intValue = 3)
    private int a3;
    @Default(defaultValueEnum = DefaultValueEnum.LONG,intValue = 4)
    private long a4;




}
