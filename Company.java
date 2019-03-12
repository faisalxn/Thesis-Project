package com.example.user.companycommunity;

public class Company {

    String company_name;
    String about_company;
    String company_address;

    public Company(String company_name, String about_company, String company_address) {
        this.company_name = company_name;
        this.about_company = about_company;
        this.company_address = company_address;
    }

    public Company(){

    }


    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getAbout_company() {
        return about_company;
    }

    public void setAbout_company(String about_company) {
        this.about_company = about_company;
    }

    public String getCompany_address() {
        return company_address;
    }

    public void setCompany_address(String company_address) {
        this.company_address = company_address;
    }






}
