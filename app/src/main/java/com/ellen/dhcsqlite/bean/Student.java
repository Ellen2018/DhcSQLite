package com.ellen.dhcsqlite.bean;

import com.ellen.dhcsqlitelibrary.table.annotation.field.bound.Check;
import com.ellen.dhcsqlitelibrary.table.annotation.field.bound.CreateEndString;
import com.ellen.dhcsqlitelibrary.table.annotation.field.DataStructure;
import com.ellen.dhcsqlitelibrary.table.annotation.field.DhcSqlFieldName;
import com.ellen.dhcsqlitelibrary.table.annotation.field.bound.Default;
import com.ellen.dhcsqlitelibrary.table.annotation.field.bound.DefaultValueEnum;
import com.ellen.dhcsqlitelibrary.table.annotation.field.bound.EndAutoString;
import com.ellen.dhcsqlitelibrary.table.annotation.field.Ignore;
import com.ellen.dhcsqlitelibrary.table.annotation.field.bound.MajorKey;
import com.ellen.dhcsqlitelibrary.table.annotation.field.Operate;
import com.ellen.dhcsqlitelibrary.table.annotation.field.OperateEnum;

import java.util.Arrays;

@CreateEndString("整个表的约束")
public class Student {

    //主键
    @MajorKey(isAutoIncrement = true)
    @DhcSqlFieldName(sqlFieldName = "sssssid")
    private int id;
    @DhcSqlFieldName(sqlFieldName = "my_name") //映射数据库中字段名字为my_name
    @EndAutoString("CHECK(my_name like 'Ellen%')")
    private String name;
    @DhcSqlFieldName(sqlFieldName = "your_age")
    @Default(defaultValueEnum = DefaultValueEnum.BYTE,byteValue = 3)
    @Check("{} > -1")
    private int age;
    private String phoneNumber;
    private String address;
    @Ignore //不映射这个属性到数据库中
    private String ingoreString;
    @DhcSqlFieldName(sqlFieldName = "真不真")
    private boolean isMan;
    @Operate(operate = OperateEnum.VALUE,valueName = "student")
    private Father father;
    private Father father2;
    @DataStructure //表示这个属性是数据类型属性，需要用注解区分，才能正确的进行json映射，否则会报错
    @DhcSqlFieldName(sqlFieldName = "爸爸们")
    private Father[] fathers;
    private double f = 3.6;

    public Student(String name, int age, String phoneNumber, String address) {
        this.name = name;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.address = address;

        fathers = new Father[5];
        for (int i = 0; i < fathers.length; i++) {
            fathers[i] = new Father("名字：" + i, "ID:" + i,"好尼玛"+i);
        }
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", ingoreString='" + ingoreString + '\'' +
                ", isMan=" + isMan +
                ", father=" + father +
                ", father2=" + father2 +
                ", fathers=" + Arrays.toString(fathers) +
                ", f=" + f +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Father getFather() {
        return father;
    }

    public void setFather(Father father) {
        this.father = father;
    }

    public boolean isMan() {
        return isMan;
    }

    public void setMan(boolean man) {
        isMan = man;
    }
}

