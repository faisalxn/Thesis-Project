package com.example.user.companycommunity;

public class PastLeave {

    private String days,name;

    public PastLeave(String days, String name ) {
        this.days = days;
        this.name = name;
    }

    public PastLeave() {
    }


    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
