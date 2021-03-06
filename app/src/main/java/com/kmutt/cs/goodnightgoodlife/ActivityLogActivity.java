package com.kmutt.cs.goodnightgoodlife;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import android.support.v7.widget.LinearLayoutManager;

public class ActivityLogActivity extends AppCompatActivity {

    //a list to store all the products
    public static List<Log> logList;

    //the recyclerview
    public static RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activityhistory);
        setTitle("Activity Log");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        logList = new ArrayList<>();

        logList.add(
                new com.kmutt.cs.goodnightgoodlife.Log(
                        "26",
                        "Sep",
                        "2018",
                        "Read Book",
                        92,
                        "Duration: 30 minutes",
                        35
                )
        );

        logList.add(
                new com.kmutt.cs.goodnightgoodlife.Log(
                        "25",
                        "Sep",
                        "2018",
                        "Listen Music",
                        39,
                        "Duration: 30 minutes",
                        98
                )
        );

        logList.add(
                new com.kmutt.cs.goodnightgoodlife.Log(
                        "24",
                        "Sep",
                        "2018",
                        "Meditation",
                        49,
                        "Duration: 30 minutes",
                        78
                )
        );

        logList.add(
                new com.kmutt.cs.goodnightgoodlife.Log(
                        "23",
                        "Sep",
                        "2018",
                        "Yoga",
                        79,
                        "Duration: 30 minutes",
                        67
                )
        );

        logList.add(
                new com.kmutt.cs.goodnightgoodlife.Log(
                        "22",
                        "Sep",
                        "2018",
                        "Read Book",
                        56,
                        "Duration: 30 minutes",
                        13
                )
        );

        logList.add(
                new com.kmutt.cs.goodnightgoodlife.Log(
                        "21",
                        "Sep",
                        "2018",
                        "Read Book",
                        50,
                        "Duration: 30 minutes",
                        15
                )
        );

        logList.add(
                new com.kmutt.cs.goodnightgoodlife.Log(
                        "20",
                        "Sep",
                        "2018",
                        "Read Book",
                        96,
                        "Duration: 30 minutes",
                        40
                )
        );

        //creating recyclerview adapter
        LogAdapter adapter = new LogAdapter(this, logList);

        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);

    }
}
