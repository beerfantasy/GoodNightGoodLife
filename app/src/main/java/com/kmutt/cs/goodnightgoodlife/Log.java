package com.kmutt.cs.goodnightgoodlife;

public class Log {
    String day;
    String month;
    String year;
    String activity;
    float relaxation;
    float deepsleep;
    String duration;

    /*public Log(String d, String a, String r, float dr, float ds){
        this.date = d;
        this.activity = a;
        this.relaxation = r;
        //this.deepsleep = ds;
        this.duration = dr;
    }*/

    public Log(String day, String month, String year, String a, float r, String dr, float ds){
        this.day = day;
        this.month = month;
        this.year = year;
        this.activity = a;
        this.relaxation = r;
        this.deepsleep = ds;
        this.duration = dr;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public String getActivity() {
        return activity;
    }

    public float getRelaxation() { return relaxation; }

    public float getDeepsleep() { return deepsleep; }

    public String getDuration() {
        return duration;
    }

}
