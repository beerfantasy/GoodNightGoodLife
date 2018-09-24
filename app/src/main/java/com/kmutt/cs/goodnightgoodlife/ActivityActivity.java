package com.kmutt.cs.goodnightgoodlife;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ActivityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activitieslist);
        setTitle("Activity");

        final Intent intent = new Intent(getApplicationContext(),MeasurementActivity.class);

        Button read_book = (Button) findViewById(R.id.read_book);
        Button listen_music = (Button) findViewById(R.id.listen_music);
        Button meditation = (Button) findViewById(R.id.meditation);
        Button yoga = (Button) findViewById(R.id.yoga);
        Button massage = (Button) findViewById(R.id.massage);

        read_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("activity_cur", "Read Book");
                startActivity(intent);
            }
        });

        listen_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("activity_cur", "Listen Music");
                startActivity(intent);
            }
        });

        meditation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("activity_cur", "Meditation");
                startActivity(intent);
            }
        });

        yoga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("activity_cur", "Yoga");
                startActivity(intent);
            }
        });

        massage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("activity_cur", "Massage");
                startActivity(intent);
            }
        });
    }
}
