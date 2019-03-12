package com.example.user.companycommunity;

public class History {
    String name , text , id ;


    public History() {
    }

    public History(String name, String text, String id) {
        this.name = name;
        this.text = text;
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
