package com.example.user.companycommunity;

import android.os.Parcel;
import android.os.Parcelable;

public class Poll implements Parcelable {
    private String count , des , name , text1 , text2 , text3 , text4 , text5 , textC1 , textC2 , textC3 , textC4 , textC5 ;

    public Poll() {

    }

    public Poll(String count, String des, String name, String text1, String text2, String text3, String text4, String text5, String textC1, String textC2, String textC3, String textC4, String textC5) {
        this.count = count;
        this.des = des;
        this.name = name;
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
        this.text4 = text4;
        this.text5 = text5;
        this.textC1 = textC1;
        this.textC2 = textC2;
        this.textC3 = textC3;
        this.textC4 = textC4;
        this.textC5 = textC5;
    }

    protected Poll(Parcel in) {
        count = in.readString();
        des = in.readString();
        name = in.readString();
        text1 = in.readString();
        text2 = in.readString();
        text3 = in.readString();
        text4 = in.readString();
        text5 = in.readString();
        textC1 = in.readString();
        textC2 = in.readString();
        textC3 = in.readString();
        textC4 = in.readString();
        textC5 = in.readString();
    }

    public static final Creator<Poll> CREATOR = new Creator<Poll>() {
        @Override
        public Poll createFromParcel(Parcel in) {
            return new Poll(in);
        }

        @Override
        public Poll[] newArray(int size) {
            return new Poll[size];
        }
    };

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public String getText3() {
        return text3;
    }

    public void setText3(String text3) {
        this.text3 = text3;
    }

    public String getText4() {
        return text4;
    }

    public void setText4(String text4) {
        this.text4 = text4;
    }

    public String getText5() {
        return text5;
    }

    public void setText5(String text5) {
        this.text5 = text5;
    }

    public String getTextC1() {
        return textC1;
    }

    public void setTextC1(String textC1) {
        this.textC1 = textC1;
    }

    public String getTextC2() {
        return textC2;
    }

    public void setTextC2(String textC2) {
        this.textC2 = textC2;
    }

    public String getTextC3() {
        return textC3;
    }

    public void setTextC3(String textC3) {
        this.textC3 = textC3;
    }

    public String getTextC4() {
        return textC4;
    }

    public void setTextC4(String textC4) {
        this.textC4 = textC4;
    }

    public String getTextC5() {
        return textC5;
    }

    public void setTextC5(String textC5) {
        this.textC5 = textC5;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(count);
        parcel.writeString(des);
        parcel.writeString(name);

        parcel.writeString(text1);
        parcel.writeString(text2);
        parcel.writeString(text3);
        parcel.writeString(text4);
        parcel.writeString(text5);

        parcel.writeString(textC1);
        parcel.writeString(textC2);
        parcel.writeString(textC3);
        parcel.writeString(textC4);
        parcel.writeString(textC5);

    }
}
