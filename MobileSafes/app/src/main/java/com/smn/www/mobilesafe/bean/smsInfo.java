package com.smn.www.mobilesafe.bean;

/**
 * Created by Administrator on 2018-03-29.
 */

public class smsInfo {
    private String address;
    private int date;
    private int type;
    private String body;

    public smsInfo(String address, int date, int type, String body) {
        this.address = address;
        this.date = date;
        this.type = type;
        this.body = body;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
}


