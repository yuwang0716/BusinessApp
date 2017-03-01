package com.liuhesan.app.businessapp.bean;

/**
 * Created by Tao on 2017/2/22.
 */

public class UrgeDetails {
    private String reminder_time;
    private String response_time;
    private String response_content;

    public String getReminder_time() {
        return reminder_time;
    }

    public void setReminder_time(String reminder_time) {
        this.reminder_time = reminder_time;
    }

    public String getResponse_time() {
        return response_time;
    }

    public void setResponse_time(String response_time) {
        this.response_time = response_time;
    }

    public String getResponse_content() {
        return response_content;
    }

    public void setResponse_content(String response_content) {
        this.response_content = response_content;
    }
}
