package com.example.user.companycommunity;

public class Chat {
    String name, text, type  ;


    public Chat(String name, String text, String type) {
        this.text = text;
        this.type = type;
        this.name = name;
    }

    public Chat(){


    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }





}
