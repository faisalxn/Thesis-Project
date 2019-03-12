package com.example.user.companycommunity;

public class Employee {
    String email,join_status,mobile,name;

    public Employee() {
    }

    public Employee(String email, String join_status, String mobile, String name) {
        this.email = email;
        this.join_status = join_status;
        this.mobile = mobile;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJoin_status() {
        return join_status;
    }

    public void setJoin_status(String join_status) {
        this.join_status = join_status;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
