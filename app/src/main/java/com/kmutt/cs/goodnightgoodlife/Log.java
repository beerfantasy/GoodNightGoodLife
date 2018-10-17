package com.kmutt.cs.goodnightgoodlife;

public class Log {
    String date;
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

    public Log(String d, String a, float r, String dr, float ds){
        this.date = d;
        this.activity = a;
        this.relaxation = r;
        this.deepsleep = ds;
        this.duration = dr;
    }

    public String getDate() {
        return date;
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
