package com.ellen.dhcsqlite.bean;

public class Father {

    private String name;
    private String id;
    private String student;

    public Father(){}

    public Father(String name, String id,String s) {
        this.name = name;
        this.id = id;
        this.student = s;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "Father{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", student='" + student + '\'' +
                '}';
    }
}
