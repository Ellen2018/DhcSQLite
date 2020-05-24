package com.ellen.dhcsqlite;

import com.ellen.dhcsqlitelibrary.table.reflection.annotation.DhcSqlFieldName;
import com.ellen.dhcsqlitelibrary.table.reflection.annotation.Ignore;
import com.ellen.dhcsqlitelibrary.table.reflection.annotation.Primarykey;

public class Student {

    @Primarykey //主键
    private int id;
    @DhcSqlFieldName("my_name") //映射数据库中字段名字为my_name
    private String name;
    @DhcSqlFieldName("your_age")
    private int age;
    private String phoneNumber;
    private String address;
    @Ignore //不映射这个属性到数据库中
    private String ingoreString;
    private boolean isMan;
    private Father father;

    public Student(int id,String name, int age, String phoneNumber, String address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.address = address;
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
                '}';
    }
}
