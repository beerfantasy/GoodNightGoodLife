package com.kmutt.cs.goodnightgoodlife;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ActivityActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ActivityInListAdapter activityInListAdapter;

    List<ActivityInList> activityList;

    List<String> activity;

    Button add;
    private String m_Text = "";

    private static final String TAG = ActivityActivity.class.getSimpleName() ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activitieslist);
        setTitle("Activity");

        add = (Button) findViewById(R.id.add_button);

        final Intent intent = new Intent(getApplicationContext(),MeasurementActivity.class);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityActivity.this);
                builder.setTitle("Title");

                // Set up the input
                final EditText input = new EditText(ActivityActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        activity.add(m_Text);

                        finish();
                        startActivity(getIntent());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        activity = new ArrayList<>();
        activity.add("Read Book");
        activity.add("Listen Music");
        activity.add("Meditation");
        activity.add("Yoga");
        activity.add("Massage");

        if(!m_Text.matches("")) Log.e(TAG, "ADDDDDD !!!"); //activity.add(m_Text);

        activityList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.activity_list_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        for(String s:activity) {
            activityList.add(new ActivityInList(s));
            Log.e(TAG,s);
        }

        ActivityInListAdapter adapter = new ActivityInListAdapter(this, activityList);
        recyclerView.setAdapter(adapter);

    }
}
