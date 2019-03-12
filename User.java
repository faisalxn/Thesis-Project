package com.example.user.companycommunity;

public class User {
    String name , email, mobile , join_status ;

    public User(){


    }

    public User(String name, String email, String mobile, String join_status) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.join_status = join_status;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getJoin_status() {
        return join_status;
    }

    public void setJoin_status(String join_status) {
        this.join_status = join_status;
    }




}
