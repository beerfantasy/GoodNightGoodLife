package com.kmutt.cs.goodnightgoodlife;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.NavigationView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Array;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = HomeActivity.class.getSimpleName() ;
    private static final String API_PREFIX = "https://api.fitbit.com";
    public static String authCode = "";
    public static String[] date_of_sleep;
    public static String[] deep_sleep;
    public static long[] sleep_time;
    public static String current_date;
    public static String current_deep_sleep;
    public static long current_sleep;

    private TextView DSD;
    private TextView LNSD;
    private Long long_DSD;
    private long deep_sleep_per;
    private long sleep_per;
    private boolean syncTemp = false;

    public static String currentDate;
    private Map<String, Object> dateSleep = new HashMap<>();
    //private Map<String, Object> deepSleep = new HashMap<>();


    float sleep[] = new float[2];
    String label[] = {"Deep Sleep" ,"Other stages"};
    public static final int[] CHART_COLORS = {
            Color.rgb(16, 41, 109), Color.rgb(64, 108, 229)
    };

    //drawer
    private DrawerLayout mDrawerlayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mDrawerList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(),ChangeAuthCodeActivity.class);
            this.startActivity(intent);
            return true;
        }

        // Drawer toggle
        else if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //drawer menu
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.activity_drawer) {
            Intent intent1 = new Intent(getApplicationContext(),ActivityActivity.class);
            startActivity(intent1);
        }
        else if (id == R.id.sleeptrack_drawer) {
            Intent intent2 = new Intent(getApplicationContext(),SleepTrackActivity.class);
            startActivity(intent2);
        }
        else if (id == R.id.history_drawer) {
            Intent intent3 = new Intent(getApplicationContext(),ActivityLogActivity.class);
            startActivity(intent3);
        }
        else if (id == R.id.logout_drawer) {
            Intent intent4 = new Intent(getApplicationContext(),Login.class);
            startActivity(intent4);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //drawer toggle
        mDrawerlayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerList = (NavigationView) findViewById(R.id.nav_view);
        mDrawerList.setNavigationItemSelectedListener(this);
        mToggle = new ActionBarDrawerToggle(this, mDrawerlayout,R.string.open,R.string.close)
        {
            public void onDrawerClosed(View view) { /*invalidateOptionsMenu();*/ }

            public void onDrawerOpened(View drawerView) {
                mDrawerList.bringToFront();
                mDrawerlayout.requestLayout();
                //invalidateOptionsMenu();
            }
        };
        mDrawerlayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            //String name = user.getDisplayName();
            String email = user.getEmail();
            //Log.e(TAG, name);
            Log.e(TAG, email);
        }

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        final String formattedDate = df.format(c);
        setTitle("Home");

        //setupPieChart();
        final PieChart chart = (PieChart) findViewById(R.id.chart);
        //chart.animateY(1000, Easing.EasingOption.EaseInOutCubic);
        chart.setNoDataText("Please sync data first.");
        chart.setNoDataTextColor(Color.WHITE);

        Calendar calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        TextView textViewDate = findViewById(R.id.text_view_date);
        textViewDate.setText(currentDate);



        Button button1 = (Button)findViewById(R.id.activities);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ActivityActivity.class);
                startActivity(intent);
            }
        });

        Button button2 = (Button)findViewById(R.id.sleeptrack);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SleepTrackActivity.class);
                if(deep_sleep[0] != null) {
                    startActivity(intent);
                }
            }
        });

        DSD = (TextView) findViewById(R.id.text_DSD);
        LNSD = (TextView) findViewById(R.id.text_LNSD);

        //FitBit

        date_of_sleep = new String[7];
        deep_sleep = new String[7];
        sleep_time = new long[7];

        if(user != null) {
            DocumentReference docRef = db.collection(user.getEmail().toString().trim()).document("Authentication Token");
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            authCode = document.getString("authToken");
                            Log.d(TAG, "DocumentSnapshot data: " + authCode);

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "Cannot find the user instance", Toast.LENGTH_LONG).show();
        }

        Button toggle = (Button) findViewById(R.id.refresh);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread urlConnectionThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {


                            URLConnection connection = new URL(API_PREFIX.concat("/1.2/user/-/sleep/list.json?beforeDate=2018-09-13&sort=asc&offset=0&limit=7")).openConnection();
                            connection.setRequestProperty("Authorization","Bearer " + authCode);
                            InputStream response = connection.getInputStream();
                            JSONParser jsonParser = new JSONParser();
                            JSONObject responseObject = (JSONObject) jsonParser.parse(new InputStreamReader(response, "UTF-8"));
                            //Show Date
                            org.json.simple.JSONArray sleep = (org.json.simple.JSONArray) responseObject.get("sleep");
                            for(int i=0; i<sleep.size(); i++) {
                                //Show Date
                                JSONObject sleepObject = (JSONObject) sleep.get(i);
                                String dateOfSleep = (String) sleepObject.get("dateOfSleep");
                                //Show Deep Sleep Time
                                JSONObject levels = (JSONObject) sleepObject.get("levels");
                                JSONObject summary = (JSONObject) levels.get("summary");
                                JSONObject deep = (JSONObject) summary.get("deep");
                                final String minutes = (deep.get("minutes")) + "";
                                date_of_sleep[i] = dateOfSleep;
                                deep_sleep[i] = minutes;
                                sleep_time[i] = (long) sleepObject.get("minutesAsleep");
                                Log.e(TAG,"Day " + (i + 1) + ": ");
                                Log.e(TAG,"Data of sleep: " + dateOfSleep);
                                Log.e(TAG,"Deep sleep : " + minutes+" minutes");
                                Log.e(TAG,"Sleep time : " + (long) sleepObject.get("minutesAsleep") + "minutes");

                                if(i == sleep.size()-1) {
                                    current_date = dateOfSleep;
                                    current_deep_sleep = minutes;
                                    long_DSD = Long.parseLong(current_deep_sleep);
                                    DSD.setText("Deep Sleep Duration : " + current_deep_sleep + " minutes");
                                    current_sleep = (long) sleepObject.get("minutesAsleep");
                                    LNSD.setText("Last Night Sleep Duration : " + current_sleep + " minutes");

                                    //Calculate Graph
                                    deep_sleep_per = (long_DSD*100)/current_sleep;
                                    sleep_per = 100-deep_sleep_per;
                                    setupPieChart();
                                    chart.notifyDataSetChanged();
                                    chart.invalidate();
                                }
//                                String dateLastNight = "";
//                                if(!date_of_sleep[0].toString().matches("")) dateLastNight = date_of_sleep[0].toString();
//                                Calendar calendar = Calendar.getInstance();
//                                currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(dateLastNight);
//                                TextView textViewDate = findViewById(R.id.text_view_date);
//                                textViewDate.setText(currentDate);
                            }
                            int i = 0;
//                            for (String sleepDeep : deep_sleep){
//                                int deep_temp = (Integer) Integer.parseInt(sleepDeep);
//                                //deepSleep.put("duration", deep_temp);
//
//                            }
                            for (String sleepDate : date_of_sleep){
                                Map<String, Object> deepSleep = new HashMap<>();
                                deepSleep.put("duration", (Integer) Integer.parseInt(deep_sleep[i]));
                                db.collection(user.getEmail().toString().trim()).document("Date of Sleep")
                                        .collection(sleepDate).document("Deep Sleep").set(deepSleep);
                                i++;
                            }
                            //db.collection(user.getEmail().toString().trim()).document("Date of Sleep").set(dateSleep);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                urlConnectionThread.start();

            }
        });

    }


    private void setupPieChart() {
        List<PieEntry> pieEntries = new ArrayList<>();
        sleep[0] = (float) deep_sleep_per;
        sleep[1] = (float) sleep_per;
        for (int i = 0; i < sleep.length; i++) {
            pieEntries.add(new PieEntry(sleep[i],label[i]));
        }
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(CHART_COLORS);
        PieData data = new PieData(dataSet);
        data.setValueTextColor(Color.rgb(206, 225, 255));
        data.setValueTextSize(10);

        PieChart chart = (PieChart) findViewById(R.id.chart);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.argb(1,0,0,0));
        chart.setData(data);
        chart.invalidate();
    }


}
