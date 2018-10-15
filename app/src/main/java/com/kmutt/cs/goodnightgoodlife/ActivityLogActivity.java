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
                        "Wednesday, 26 september 2018",
                        "Activity: Read Book",
                        "Relax: 99%",
                        "Duration: 30 minutes"
                )
        );

        logList.add(
                new com.kmutt.cs.goodnightgoodlife.Log(
                        "Tuesday, 25 september 2018",
                        "Activity: Listen Music",
                        "Relax: 99%",
                        "Duration: 30 minutes"
                )
        );

        logList.add(
                new com.kmutt.cs.goodnightgoodlife.Log(
                        "Monday, 24 september 2018",
                        "Activity: Meditation",
                        "Relax: 99%",
                        "Duration: 30 minutes"
                )
        );

        logList.add(
                new com.kmutt.cs.goodnightgoodlife.Log(
                        "Sunday, 23 september 2018",
                        "Activity: Yoga",
                        "Relax: 99%",
                        "Duration: 30 minutes"
                )
        );

        logList.add(
                new com.kmutt.cs.goodnightgoodlife.Log(
                        "Saturday, 22 september 2018",
                        "Activity: Read Book",
                        "Relax: 99%",
                        "Duration: 30 minutes"
                )
        );

        logList.add(
                new com.kmutt.cs.goodnightgoodlife.Log(
                        "Friday, 21 september 2018",
                        "Activity: Read Book",
                        "Relax: 99%",
                        "Duration: 30 minutes"
                )
        );

        logList.add(
                new com.kmutt.cs.goodnightgoodlife.Log(
                        "Thursday, 20 september 2018",
                        "Activity: Read Book",
                        "Relax: 99%",
                        "Duration: 30 minutes"
                )
        );

        //creating recyclerview adapter
        LogAdapter adapter = new LogAdapter(this, logList);

        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);
    }
}
