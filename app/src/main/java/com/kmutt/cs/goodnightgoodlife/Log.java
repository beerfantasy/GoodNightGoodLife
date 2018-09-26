package com.kmutt.cs.goodnightgoodlife;

public class List {
    String date;
    String activity;
    String relaxation;
    float deepsleep;

    public List(String d, String a, String r, float ds){
        this.date = d;
        this.activity = a;
        this.relaxation = r;
        this.deepsleep = ds;
    }

    public List(String d, String a, String r){
        this.date = d;
        this.activity = a;
        this.relaxation = r;
        this.deepsleep = 0;
    }

    public String getdate() {
        return date;
    }

    public String getActivity() {
        return activity;
    }

    public String getRelaxation() {
        return relaxation;
    }

    public float getDeepsleep() {
        return deepsleep;
    }

}
