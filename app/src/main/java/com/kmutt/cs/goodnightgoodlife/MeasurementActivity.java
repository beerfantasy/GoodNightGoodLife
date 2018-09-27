package com.kmutt.cs.goodnightgoodlife;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.util.Measure;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.choosemuse.libmuse.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.Buffer;
import java.text.DecimalFormat;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class MeasurementActivity extends AppCompatActivity {

    private final String TAG = "Good Night Good Life";
    private MuseManagerAndroid manager;
    private Muse muse;
    private ConnectionListener connectionListener;
    private DataListener dataListener;

    int timer;

    double sum;
    Button start;
    Button finish;
    TextView activity;
    TextView avg_relax;
    String act;
    private LineChart chart;
    private boolean start_act;
    private static DecimalFormat df2 = new DecimalFormat(".##");

    private BluetoothAdapter mBluetoothAdapter;
    private static final int MY_PERMISSIONS_REQUEST_BLUETOOTH = 0;
    private static final int REQUEST_ENABLE_BT = 1;

    private final double[] eegBuffer = new double[6];
    private boolean eegStale;
    private final double[] thetaBuffer = new double[6];
    private boolean thetaStale;
    private final double[] accelBuffer = new double[3];
    private boolean accelStale;
    private Handler handler = new Handler();
    private ArrayAdapter<String> spinnerAdapter;
    double ran = 0;

    private String avg_relax_data = "";
    double avg = 0;
    Map<String, Object> user = new HashMap<>();

    CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long l) {
            if (start_act) {
                addEntry();
                timer++;
            }
        }

        @Override
        public void onFinish() {
            countDownTimer.start();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        setTitle("Relaxation Measurement");
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        final String formattedDate = df.format(c);



        manager = MuseManagerAndroid.getInstance();
        manager.setContext(this);

        WeakReference<MeasurementActivity> weakActivity =
                new WeakReference<>(this);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /***Android 6.0 and higher need to request permission*****/
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_BLUETOOTH);
            }
            else{
                checkConnect();
            }
        }
        else {
            checkConnect();
        }

        connectionListener = new ConnectionListener(weakActivity);
        // Register a listener to receive data from a Muse.
        dataListener = new DataListener(weakActivity);
        // Register a listener to receive notifications of what Muse headbands
        // we can connect to.
        manager.setMuseListener(new MuseL(weakActivity));

        sum = 0;

        lineChartSetup();

        countDownTimer.start();

        start_act = false;

        setupUIView();

        Intent intent = getIntent();
        act = intent.getStringExtra("activity_cur");
        activity.setText(act);

        finish.setVisibility(View.INVISIBLE);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish.setVisibility(View.VISIBLE);
                start.setVisibility(View.INVISIBLE);
                start_act = true;
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish.setVisibility(View.INVISIBLE);
                start.setVisibility(View.VISIBLE);
                start_act = false;
                user.put("avg", avg);
                db.collection("avg2").document(formattedDate).set(user);

                //save data to array
                /*
                logList.add(
                        new com.kmutt.cs.goodnightgoodlife.Log(
                                HomeActivity.currentDate,
                                act,
                                avg_relax_data,
                                timer
                        )
                );
                */
            }
        });

        // Muse Spinner for containing muse headband
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        final Spinner musesSpinner = (Spinner) findViewById(R.id.spinner);
        musesSpinner.setAdapter(spinnerAdapter);

        //refresh button for finding new headband
        Button refreshButton = (Button) findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.stopListening();
                manager.startListening();
            }
        });

        // connect to headband and start streaming data
        Button connectButton = (Button) findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.stopListening();

                List<Muse> availableMuses = manager.getMuses();
                Log.e(TAG, "list size"+availableMuses.size());
                if (availableMuses.size() < 1 || musesSpinner.getAdapter().getCount() < 1 ) {
                    Log.e(TAG, "There is nothing to connect to ");
                }else{
                    muse = availableMuses.get(musesSpinner.getSelectedItemPosition());
                    // Unregister all prior listeners and register our data listener to
                    // receive the MuseDataPacketTypes we are interested in.  If you do
                    // not register a listener for a particular data type, you will not
                    // receive data packets of that type.
                    // receive data packets of that type.
                    muse.unregisterAllListeners();
                    muse.registerConnectionListener(connectionListener);
                    //muse.registerDataListener(dataListener, MuseDataPacketType.EEG);

                    /* Choose One between these three data type*/
                    muse.registerDataListener(dataListener, MuseDataPacketType.THETA_RELATIVE);
                    //muse.registerDataListener(dataListener, MuseDataPacketType.THETA_ABSOLUTE);
                    //muse.registerDataListener(dataListener, MuseDataPacketType.THETA_SCORE);
                    //muse.registerDataListener(dataListener, MuseDataPacketType.ACCELEROMETER);
                    muse.registerDataListener(dataListener, MuseDataPacketType.BATTERY);
                    muse.registerDataListener(dataListener, MuseDataPacketType.DRL_REF);
                    muse.registerDataListener(dataListener, MuseDataPacketType.QUANTIZATION);
                    manager.stopListening();

                    // Initiate a connection to the headband and stream the data asynchronously.
                    muse.runAsynchronously();
                }
            }
        });

    }

    protected void onPause() {
        super.onPause();
        // It is important to call stopListening when the Activity is paused
        // to avoid a resource leak from the LibMuse library.
        manager.stopListening();
    }



    private void setupUIView() {
        start = (Button) findViewById(R.id.button_start);
        finish = (Button) findViewById(R.id.button_finish);
        activity = (TextView) findViewById(R.id.activity);
        avg_relax = (TextView) findViewById(R.id.avg_relax);
    }

    private void lineChartSetup() {
        chart = (LineChart) findViewById(R.id.line_chart);
        chart.getDescription().setEnabled(false);
        chart.setNoDataText("No data");
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.setBackgroundColor(Color.TRANSPARENT);

        Legend z = chart.getLegend();
        z.setForm(Legend.LegendForm.LINE);
        z.setTextColor(Color.WHITE);

        //data
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);

        XAxis x = chart.getXAxis();
        x.setTextColor(Color.WHITE);
        x.setAvoidFirstLastClipping(true);

        YAxis y = chart.getAxisLeft();
        y.setTextColor(Color.WHITE);
        y.setAxisMinValue(0);
        y.setAxisMaximum(100);
        y.setDrawGridLines(true);

        YAxis y2 = chart.getAxisRight();
        y2.setEnabled(false);

        LineData _data = chart.getData();
        _data.addEntry(new Entry(0,0),0);

    }

    private void addEntry() {

        LineData data = chart.getData();

        if (data != null) {
            LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            double theta_one = 0;
            double theta_two = 0;
            double theta_three = 0;
            double theta_four = 0;

            theta_one = (thetaBuffer[0]+1)*50;
            theta_two = (thetaBuffer[1]+1)*50;
            theta_three = (thetaBuffer[2]+1)*50;
            theta_four = (thetaBuffer[3]+1)*50;
            Log.e("one", ""+theta_one);
            Log.e("two", ""+theta_two);
            Log.e("three", ""+theta_three);
            Log.e("four", ""+theta_four);

//            Double theta_one_tmp = new Double(theta_one);
//            Log.e("one_tmp" , ""+theta_one_tmp.doubleValue());
//            Double theta_two_tmp = new Double(theta_two);
//            Log.e("two_tmp", ""+theta_two_tmp.doubleValue());
//            Double theta_three_tmp = new Double(theta_three);
//            Log.e("three_tmp", ""+theta_three_tmp.doubleValue());
//            Double theta_four_tmp = new Double(theta_four);
//            Log.e("four_tmp", ""+theta_four_tmp.doubleValue());

            if(!Double.isNaN(theta_one) || !Double.isNaN(theta_two) || !Double.isNaN(theta_three) || !Double.isNaN(theta_four)) {
                int divide = 4;
                if(Double.isNaN(theta_one)){
                    theta_one = 0;
                    divide -= 1;
                }
                if(Double.isNaN(theta_two)){
                    theta_two = 0;
                    divide -= 1;
                }
                if(Double.isNaN(theta_three)){
                    theta_three = 0;
                    divide -= 1;
                }
                if(Double.isNaN(theta_four)){
                    theta_four = 0;
                    divide -= 1;
                }
                if(divide > 0) {
                    ran = (theta_one + theta_two + theta_three + theta_four) / divide;
                }
                Log.e("ran", ""+ran);
            }
            data.addEntry(new Entry(set.getEntryCount(), (float) ran),0);
            sum += ran;
            Log.e("sum", ""+sum);
//            Double sumTmp = new Double(sum);
            if(!Double.isNaN(sum)) {
                avg = (sum / set.getEntryCount())-50;
                Log.e("avg", ""+avg);
            }
            avg_relax.setText("Average Relaxation : " + df2.format(avg) + "%");
            avg_relax_data = df2.format(avg) + "%";

            chart.notifyDataSetChanged();
            chart.setVisibleXRange(0,10);
            chart.moveViewToX(data.getEntryCount() - 11);
        }
    }


    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null,"Relaxation");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.rgb(178, 207, 255));
        set.setLineWidth(2f);
        set.setCubicIntensity(0.2f);
        set.setFillAlpha(65);
        set.setFillColor(Color.WHITE);
        set.setValueTextSize(0);
        return set;
    }

    public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) {
        //writeDataPacketToFile(p);

        // valuesSize returns the number of data values contained in the packet.
        final long n = p.valuesSize();
        switch (p.packetType()) {
//            case EEG:
//                assert(eegBuffer.length >= n);
//                getEegChannelValues(eegBuffer,p);
//                eegStale = true;
//                break;
//            case ACCELEROMETER:
//                assert(accelBuffer.length >= n);
//                getAccelValues(p);
//                accelStale = true;
//                break;
            case THETA_RELATIVE:
                assert(thetaBuffer.length >= n);
                getEegChannelValues(thetaBuffer,p);
                thetaStale = true;
                break;
            case THETA_ABSOLUTE:
                assert(thetaBuffer.length >= n);
                getEegChannelValues(thetaBuffer,p);
                thetaStale = true;
                break;
            case THETA_SCORE:
                assert(thetaBuffer.length >= n);
                getEegChannelValues(thetaBuffer,p);
                thetaStale = true;
                break;
            case BATTERY:
            case DRL_REF:
            case QUANTIZATION:
            default:
                break;
        }

    }
    public void receiveMuseConnectionPacket(final MuseConnectionPacket p, final Muse muse) {

        final ConnectionState current = p.getCurrentConnectionState();

        // Format a message to show the change of connection state in the UI.
        final String status = p.getPreviousConnectionState() + " -> " + current;
        Log.i(TAG, status);


    }

    public void museListChanged() {
        final List<Muse> list = manager.getMuses();
        spinnerAdapter.clear();
        for (Muse m : list) {
            spinnerAdapter.add(m.getName() + " - " + m.getMacAddress());
        }
    }

    private void getEegChannelValues(double[] buffer, MuseDataPacket p) {
        buffer[0] = p.getEegChannelValue(Eeg.EEG1);
        buffer[1] = p.getEegChannelValue(Eeg.EEG2);
        buffer[2] = p.getEegChannelValue(Eeg.EEG3);
        buffer[3] = p.getEegChannelValue(Eeg.EEG4);
        buffer[4] = p.getEegChannelValue(Eeg.AUX_LEFT);
        buffer[5] = p.getEegChannelValue(Eeg.AUX_RIGHT);
    }

    private void checkConnect(){
        if (!mBluetoothAdapter.isEnabled()) {
            /****Request turn on Bluetooth***************/
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else{
            Log.e("Bluetooth", "bluetooth on");
        }
    }

    /* inner class */
    class MuseL extends MuseListener {
        final WeakReference<MeasurementActivity> activityRef;

        MuseL(final WeakReference<MeasurementActivity> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void museListChanged() {
            activityRef.get().museListChanged();
        }
    }

    class ConnectionListener extends MuseConnectionListener {
        final WeakReference<MeasurementActivity> activityRef;

        ConnectionListener(final WeakReference<MeasurementActivity> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void receiveMuseConnectionPacket(final MuseConnectionPacket p, final Muse muse) {
            activityRef.get().receiveMuseConnectionPacket(p, muse);
        }
    }

    class DataListener extends MuseDataListener {
        final WeakReference<MeasurementActivity> activityRef;

        DataListener(final WeakReference<MeasurementActivity> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) {
            activityRef.get().receiveMuseDataPacket(p, muse);
        }

        @Override
        public void receiveMuseArtifactPacket(final MuseArtifactPacket p, final Muse muse) {
            //activityRef.get().receiveMuseArtifactPacket(p, muse);
        }
    }
}


