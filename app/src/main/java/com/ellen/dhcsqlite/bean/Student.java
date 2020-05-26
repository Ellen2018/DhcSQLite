package com.ellen.dhcsqlite.bean;

import com.ellen.dhcsqlite.bean.Father;
import com.ellen.dhcsqlitelibrary.table.annotation.DataStructure;
import com.ellen.dhcsqlitelibrary.table.annotation.DhcSqlFieldName;
import com.ellen.dhcsqlitelibrary.table.annotation.EndAutoString;
import com.ellen.dhcsqlitelibrary.table.annotation.Ignore;
import com.ellen.dhcsqlitelibrary.table.annotation.MajorKey;
import com.ellen.dhcsqlitelibrary.table.annotation.NotNull;
import com.ellen.dhcsqlitelibrary.table.annotation.SqlType;
import com.ellen.dhcsqlitelibrary.table.annotation.Operate;
import com.ellen.dhcsqlitelibrary.table.annotation.OperateEnum;
import com.ellen.dhcsqlitelibrary.table.annotation.Unique;
import com.ellen.sqlitecreate.createsql.helper.SQLFieldTypeEnum;

import java.util.Arrays;

public class Student {

    //主键
    @MajorKey(isAutoIncrement = true)
    private int id;
    @DhcSqlFieldName(sqlFieldName = "my_name") //映射数据库中字段名字为my_name
    @EndAutoString("CHECK(name like 'Ellen%')")
    private String name;
    @DhcSqlFieldName(sqlFieldName = "your_age")
    private int age;
    private String phoneNumber;
    private String address;
    @Ignore //不映射这个属性到数据库中
    private String ingoreString;
    private boolean isMan;
    @SqlType(sqlFiledType = SQLFieldTypeEnum.TEXT, length = 100)
    @Operate(operate = OperateEnum.JSON)
    @DhcSqlFieldName(sqlFieldName = "your_father")
    @Unique
    @NotNull
    private Father father;
    @Operate(operate = OperateEnum.VALUE,valueName = "id")
    @SqlType(sqlFiledType = SQLFieldTypeEnum.TEXT, length = 50)
    private Father father2;
    @DataStructure //表示这个属性是数据类型属性，需要用注解区分，才能正确的进行json映射，否则会报错
    @DhcSqlFieldName(sqlFieldName = "爸爸们")
    private Father[] fathers;

    public Student(String name, int age, String phoneNumber, String address) {
        this.name = name;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.address = address;

        fathers = new Father[5];
        for (int i = 0; i < fathers.length; i++) {
            fathers[i] = new Father("名字：" + i, "ID:" + i);
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
                ", fathers=" + Arrays.toString(fathers) +
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

