package com.wsy.crm.workbench.domain;


//用于显示漏斗图而封装的java类
public class FunnelVO {
    private String name;
    private int value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
