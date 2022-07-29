package com.wsy.crm.commons.domain;

//用于响应至前端的java对象
public class ReturnObject {
    private String code; //代表处理成功或者失败，0代表失败，1代表成功
    private String message; //代表一些提示信息
    private Object retData; //代表其他可能携带的数据

    public ReturnObject() {
    }

    public ReturnObject(String code, String message, Object retData) {
        this.code = code;
        this.message = message;
        this.retData = retData;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getRetData() {
        return retData;
    }

    public void setRetData(Object retData) {
        this.retData = retData;
    }

    @Override
    public String toString() {
        return "ReturnObject{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", retData=" + retData +
                '}';
    }
}

