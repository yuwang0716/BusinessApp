package com.liuhesan.app.myapplication.bean;

/**
 * Created by Tao on 2016/11/14.
 */

public class TransactionData {
    private  String date;
    private  String ordernum;
    private  String year;
    private  boolean state;

    public TransactionData(String date, String ordernum, String year,boolean state) {
        this.date = date;
        this.ordernum = ordernum;
        this.year = year;
        this.state = state;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(String ordernum) {
        this.ordernum = ordernum;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
