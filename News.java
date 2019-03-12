package com.example.user.companycommunity;

public class News {
    String name;
    String news ;

    public News(){


    }

    public News(String name, String news) {
        this.name = name;
        this.news = news;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }


}
