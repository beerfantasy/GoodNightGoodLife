package com.kmutt.cs.goodnightgoodlife;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Array;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
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

    TextView textViewDate;
    public static ImageView moon_sleep;
    public static ImageView moon_happy;

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
    private long deep_sleep_per = 0;
    private long sleep_per;
    private boolean syncTemp = false;

    public static String currentDate;
    private Map<String, Object> dateSleep = new HashMap<>();
    //private Map<String, Object> deepSleep = new HashMap<>();


    float sleep[] = new float[2];
    String label[] = {"Deep Sleep" ,"Other stages"};
    public static final int[] CHART_COLORS = {
            Color.rgb(0,80,115), Color.rgb(100, 152, 182)
    };

    //drawer
    private DrawerLayout mDrawerlayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mDrawerList;

    //bottom sheet
    private RelativeLayout bottomsheetlayout;
    private BottomSheetBehavior bottomsheetbehavior;
    private Button activities;
    RecyclerView recyclerView;
    ActivityInListAdapter activityInListAdapter;
    List<ActivityInList> activityList;
    List<String> activity;
    Button add;
    private String m_Text = "";

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

        //bottomsheet

        add = (Button) findViewById(R.id.add_button);

        final Intent intent = new Intent(getApplicationContext(),MeasurementActivity.class);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this, R.style.MyDialogTheme);
                builder.setTitle("Add activity");

                // Set up the input
                final EditText input = new EditText(HomeActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setTextColor(Color.BLACK);
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

        bottomsheetlayout = (RelativeLayout) findViewById(R.id.bottomsheet);
        activities = (Button) findViewById(R.id.activities);
        bottomsheetbehavior = BottomSheetBehavior.from(bottomsheetlayout);
        bottomsheetbehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        activities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomsheetbehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        } );

        activity = new ArrayList<>();
        activity.add("Read Book");
        activity.add("Listen Music");
        activity.add("Meditation");
        activity.add("Yoga");
        activity.add("Massage");

        if(!m_Text.matches("")) Log.e(TAG, "ADDDDDD !!!"); //activity.add(m_Text);

        activityList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.activity_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        for(String s:activity) {
            activityList.add(new ActivityInList(s));
            Log.e(TAG,s);
        }

        ActivityInListAdapter adapter = new ActivityInListAdapter(this, activityList);
        recyclerView.setAdapter(adapter);


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
        chart.setNoDataText("Please press SYNC button first");
        chart.setCenterTextSize(25);
        chart.setNoDataTextColor(Color.WHITE);

        Calendar calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        textViewDate = findViewById(R.id.text_view_date);
        textViewDate.setText("" + currentDate);



        /*Button button1 = (Button)findViewById(R.id.activities);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ActivityActivity.class);
                startActivity(intent);
            }
        });*/

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
                                    long mod_DSD = long_DSD%60;
                                    long hour_DSD = long_DSD/60;
                                    DSD.setText(hour_DSD + " Hr. " + mod_DSD + " m.");
                                    current_sleep = (long) sleepObject.get("minutesAsleep");
                                    long mod_LNSD = current_sleep%60;
                                    long hour_LNSD = current_sleep/60;
                                    LNSD.setText(hour_LNSD + " Hr. " +mod_LNSD + " m.");

                                    //Calculate Graph
                                    deep_sleep_per = (long_DSD*100)/current_sleep;
                                    sleep_per = 100-deep_sleep_per;
                                    setupPieChart();
                                    chart.notifyDataSetChanged();
                                    chart.invalidate();
                                }
                            }
                            int i = 0;
                            for (String sleepDate : date_of_sleep){
                                Map<String, Object> deepSleep = new HashMap<>();
                                Map<String, Object> sleepDu = new HashMap<>();
                                if(deep_sleep[i] != null) {
                                    deepSleep.put("duration", (Integer) Integer.parseInt(deep_sleep[i]));
                                }
                                sleepDu.put("duration", sleep_time[i]);
                                db.collection(user.getEmail().toString().trim()).document("Date of Sleep")
                                        .collection(sleepDate).document("Deep Sleep").set(deepSleep)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully written! : Deep Sleep");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                            }
                                        });
                                db.collection(user.getEmail().toString().trim()).document("Date of Sleep")
                                        .collection(sleepDate).document("Sleep Duration").set(sleepDu)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully written! : Sleep Duration");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                            }
                                        });
                                i++;
                            }
                            //db.collection(user.getEmail().toString().trim()).document("Date of Sleep").set(dateSleep);
                            try {
                                if(!date_of_sleep[6].matches("")) {

                                    //set latest date
                                    DateFormat outputFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
                                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    String inputText = date_of_sleep[6];
                                    Date date = inputFormat.parse(inputText);
                                    String outputText = outputFormat.format(date);
                                    Log.e(TAG, "date : " + outputText);
                                    Log.e(TAG, deep_sleep_per + "");
                                    textViewDate.setText(outputText);
                                }

                            } catch (Exception e) {

                                e.printStackTrace();
                            }
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
