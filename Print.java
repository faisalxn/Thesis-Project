package com.example.user.companycommunity;

public class Print {
    String name , pages, payment ;

    public Print(String name, String pages, String payment) {
        this.name = name;
        this.pages = pages;
        this.payment = payment;
    }

    public Print() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

}
