package com.example.bayuleo.testhttp;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;


public class MainActivity extends AppCompatActivity {

    final Context context = this;

    public static String FIELD_KEY = "field";

    public Timer myTimer;

    private String url_saklar = "https://api.thingspeak.com/update.json?api_key=5A8ZVNQJX3GY2OUE&field";
    private String url_get = "https://api.thingspeak.com/channels/479672/feeds.json?results=50";
    private DatabaseReference mDataBaseParent;
    OkHttpClient client = new OkHttpClient();

    TextView textViewUtama, textViewUtama2;
    Button buttonHal1, buttonHal2, buttonHal3, buttonHal4, buttonTest, buttonFB;
    public String[] think = new String[10];
    Double totalWatt, totalWattCurr;
    Double fuzzy_watt[] = new Double[3];
    Switch switchFuzzy;
    Boolean status_fuzzy, KWH;
    public Boolean start = true;
    public Boolean[] ping = new Boolean[4];
    Integer[] count = new Integer[4];

    private static String TAG = "PieChartTest";

    private double[] yData = new double[4];
    private float[] zData = new float[4];
    private String[] xData = {"Sen 1", "Sen 2", "Sen 3", "Sen 4"};
    PieChart pieChart1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }

        }, 10000, 35000);

        //Inisialisasi textView dan button
        textViewUtama = findViewById(R.id.textViewUtama);
        textViewUtama2 = findViewById(R.id.textViewUtama2);
        buttonHal1 = findViewById(R.id.buttonPindahHal1);
        buttonHal2 = findViewById(R.id.buttonPindahHal2);
        buttonHal3 = findViewById(R.id.buttonPindahHal3);
        buttonHal4 = findViewById(R.id.buttonPindahHal4);
//        buttonTest = findViewById(R.id.buttonTest);
//        buttonFB = findViewById(R.id.buttonFB);
        switchFuzzy = findViewById(R.id.switch_fuzzy);


        Log.d(TAG, "onCreate: starting to create chart");

        pieChart1 = findViewById(R.id.idPieChartUtama);

        pieChart1.setRotationEnabled(true);
        pieChart1.setHoleRadius(40f);
        pieChart1.setTransparentCircleAlpha(20);
        pieChart1.setCenterText("Current Watt");
        pieChart1.setCenterTextSize(10);

        status_fuzzy = false;
        KWH = false;

        for (int i = 0; i < 4; i++) {
            count[i] = 0;
        }


//        buttonTest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, PieChartTest.class);
//                startActivity(intent);
//            }
//        });
//
//        buttonFB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DateFormat df = new SimpleDateFormat("dd MM yyyy, HH:mm");
//                String date = df.format(Calendar.getInstance().getTime());
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                final DatabaseReference myRef = database.getReference("Sensor/1/time/" + date);
//                myRef.setValue("1");
//                dismissNotification();
//            }
//        });


        mDataBaseParent = FirebaseDatabase.getInstance().getReference("Sensor");
        mDataBaseParent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> cekPings = new ArrayList<>();
                ArrayList<String> limitStatuss = new ArrayList<>();
                ArrayList<Integer> valLimits = new ArrayList<>();
                ArrayList<String> powerStatuss = new ArrayList<>();
                ArrayList<String> switchPowerStatuss = new ArrayList<>();
                ArrayList<Double> valWatts = new ArrayList<>();
                ArrayList<Double> valWattCurrs = new ArrayList<>();


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String cekPing = snapshot.child("ping").getValue(String.class);
                    String limitStatus = snapshot.child("limit_status").getValue(String.class);
                    int valLimit = Integer.parseInt(snapshot.child("limit_value").getValue(String.class));
                    String powerStatus = snapshot.child("power_status").getValue(String.class);
                    String switchPowerSatus = snapshot.child("switch_power_status").getValue(String.class);
                    Double valWatt = Double.parseDouble(snapshot.child("watt").getValue(String.class));
                    Double valWattCurr = Double.parseDouble(snapshot.child("watt_current").getValue(String.class));

                    cekPings.add(cekPing);
                    limitStatuss.add(limitStatus);
                    valLimits.add(valLimit);
                    powerStatuss.add(powerStatus);
                    switchPowerStatuss.add(switchPowerSatus);
                    valWatts.add(valWatt);
                    valWattCurrs.add(valWattCurr);
                    Log.d("Valwatt :", limitStatus + " / " + valLimit + " / " + powerStatus + " / " + switchPowerSatus + " / " + valWatt);
                }


                for (int i = 0; i < 4; i++) {
//                    Toast.makeText(MainActivity.this, "test "+ valLimits.get(i) +" | " + valWattCurrs.get(i) + limitStatuss.get(i), Toast.LENGTH_SHORT).show();
                    if (valLimits.get(i) < valWattCurrs.get(i) && limitStatuss.get(i).equals("ON")) {
                        Toast.makeText(MainActivity.this, "Alert Sensor " + i + " over limit " + valLimits.get(i), Toast.LENGTH_LONG).show();
                        generateNotification("Sensor " + (i + 1) + " over", "Segera lakukan tindakan !");
                        alertDialogCreate("Watt High Usage on Sensor " + (i + 1));
                        saveAlertLog((i + 1), valLimits.get(i) + "_" + valWattCurrs.get(i));
                    }

                    Log.d("jarangoyang1", ""+ping[i]);
                    Drawable orange = getResources().getDrawable(R.drawable.button);
                    Drawable red = getResources().getDrawable(R.drawable.button_red);
                    if (cekPings.get(i) != null) {
                        if (cekPings.get(i).equals("pong")) {
                            ping[i] = true;
                            switch ((i + 1)) {
                                case 1:
                                    buttonHal1.setTextColor(Color.WHITE);
                                    buttonHal1.setBackground(orange);
                                    break;
                                case 2:
                                    buttonHal2.setTextColor(Color.WHITE);
                                    buttonHal2.setBackground(orange);
                                    break;
                                case 3:
                                    buttonHal3.setTextColor(Color.WHITE);
                                    buttonHal3.setBackground(orange);
                                    break;
                                case 4:
                                    buttonHal4.setTextColor(Color.WHITE);
                                    buttonHal4.setBackground(orange);
                                    break;
                            }

                        } else {
                            ping[i] = false;
                            if (start == true) {
                                switch ((i + 1)) {
                                    case 1:
                                        buttonHal1.setTextColor(Color.WHITE);
                                        buttonHal1.setBackground(red);
                                        break;
                                    case 2:
                                        buttonHal2.setTextColor(Color.WHITE);
                                        buttonHal2.setBackground(red);
                                        break;
                                    case 3:
                                        buttonHal3.setTextColor(Color.WHITE);
                                        buttonHal3.setBackground(red);
                                        break;
                                    case 4:
                                        buttonHal4.setTextColor(Color.WHITE);
                                        buttonHal4.setBackground(red);
                                        break;
                                }
                            }
                        }
                    }
                }
                start = false;


                //Set show data Total watt di textView
                totalWatt = valWatts.get(0) + valWatts.get(1) + valWatts.get(2) + valWatts.get(3);
                totalWattCurr = valWattCurrs.get(0) + valWattCurrs.get(1) + valWattCurrs.get(2) + valWattCurrs.get(3);
                if (KWH){
                    textViewUtama.setText(String.format("%,.2f", (totalWatt*1000)) + " Wh");
                }else{

                    textViewUtama.setText(String.format("%,.2f", totalWatt) + " KWh");
                }
                textViewUtama2.setText(String.format("%,.2f", totalWattCurr) + " W");

                FuzzyWatt(totalWattCurr);


                yData[0] = Double.parseDouble(dataSnapshot.child("1").child("watt_current").getValue(String.class));
                yData[1] = Double.parseDouble(dataSnapshot.child("2").child("watt_current").getValue(String.class));
                yData[2] = Double.parseDouble(dataSnapshot.child("3").child("watt_current").getValue(String.class));
                yData[3] = Double.parseDouble(dataSnapshot.child("4").child("watt_current").getValue(String.class));

                //Data harus diconvert ke float, karena enggak support double
                zData[0] = (float) yData[0];
                zData[1] = (float) yData[1];
                zData[2] = (float) yData[2];
                zData[3] = (float) yData[3];

                addDataSet();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference mDataBaseHome = FirebaseDatabase.getInstance().getReference("Home");
        mDataBaseHome.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String smartStatus = dataSnapshot.child("Smart_Status").getValue(String.class);
                if (smartStatus.equals("ON")) {
                    switchFuzzy.setChecked(true);
                } else {
                    switchFuzzy.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final Intent pergiKe = new Intent(MainActivity.this, Halaman1.class);


        buttonHal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int field = 1;
                pergiKe.putExtra(FIELD_KEY, field);
                if (myTimer != null) {
                    Log.d("Nilai Timer :", "" + myTimer);
                    myTimer.cancel();
                    myTimer.purge();
                    myTimer = null;
                }
                startActivity(pergiKe);
            }
        });

        buttonHal2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int field = 2;
                pergiKe.putExtra(FIELD_KEY, field);
                startActivity(pergiKe);
            }
        });

        buttonHal3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int field = 3;
                pergiKe.putExtra(FIELD_KEY, field);
                startActivity(pergiKe);
            }
        });

        buttonHal4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int field = 4;
                pergiKe.putExtra(FIELD_KEY, field);
                startActivity(pergiKe);
            }
        });


        switchFuzzy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    status_fuzzy = true;
                    inputFirebase("Home/Smart_Status", "ON");
                } else {
                    status_fuzzy = false;
                    inputFirebase("Home/Smart_Status", "OFF");
                }
            }
        });

        textViewUtama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (KWH){
                    KWH = false;
                    textViewUtama.setText(String.format("%,.2f", (totalWatt*1000)) + " Wh");
                }else{
                    KWH = true;
                    textViewUtama.setText(String.format("%,.2f", totalWatt) + " KWh");
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void generateNotification(String judul, String deskripsi) {
        Intent intents = new Intent(MainActivity.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{intents}, 0);
        long[] pattern = {500, 5000, 1000, 5000};
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(judul)
                .setLights(Color.RED, 500, 500)
                .setContentIntent(pendingIntent)
                .setVibrate(pattern)
                .setSound(alarmSound)
                .setContentText(deskripsi);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());

    }

    public void dismissNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }


    private void TimerMethod() {
        this.runOnUiThread(Timer_Tick);
    }

    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            Log.d("TIMER 1", "Timer RUN");
            checkPing();
            sendPing();

        }
    };

    public void inputFirebase(String firebase_url, String firebase_value) {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(firebase_url);
        myRef.setValue(firebase_value);
    }

    public void inputFirebaseDouble(String firebase_url, Double firebase_value) {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(firebase_url);
        myRef.setValue(firebase_value);
    }

    public void FuzzyWatt(Double valueSensor) {
        // untuk fuzzy rendah
        if (valueSensor <= 400) {
            fuzzy_watt[0] = 1.0;
        } else if (valueSensor > 400 && valueSensor <= 700) {
            fuzzy_watt[0] = (700 - valueSensor) / (700 - 400);
        } else {
            fuzzy_watt[0] = 0.0;
        }

        // untuk fuzzy sedang
        if (valueSensor <= 400) {
            fuzzy_watt[1] = 0.0;
        } else if (valueSensor > 400 && valueSensor <= 700) {
            fuzzy_watt[1] = (valueSensor - 400) / (700 - 400);
        } else if (valueSensor > 700 && valueSensor <= 1000) {
            fuzzy_watt[1] = (1000 - valueSensor) / (1000 - 700);
        } else {
            fuzzy_watt[1] = 0.0;
        }

        // untuk fuzzy tinggi
        if (valueSensor <= 700) {
            fuzzy_watt[2] = 0.0;
        } else if (valueSensor > 700 && valueSensor <= 1000) {
            fuzzy_watt[2] = (valueSensor - 700) / (1000 - 700);
        } else {
            fuzzy_watt[2] = 1.0;
        }

        RuleEva(valueSensor);
    }

    void RuleEva(Double nilaiCurrenTotal) {
        if (fuzzy_watt[0] > fuzzy_watt[1]) {
            if (fuzzy_watt[0] > fuzzy_watt[2]) {
                //result rendah
                //textViewUtama2.setBackgroundColor(Color.BLUE);
                textViewUtama2.setTextColor(Color.WHITE);
                textViewUtama2.clearAnimation();
            }
        } else if (fuzzy_watt[1] > fuzzy_watt[0]) {
            if (fuzzy_watt[1] > fuzzy_watt[2]) {
                //result sedang
                if (status_fuzzy) {
                    textViewUtama2.setTextColor(Color.parseColor("#FB8C00"));
                    textBlink();
                }
            }
        } else if (fuzzy_watt[2] > fuzzy_watt[0]) {
            if (fuzzy_watt[2] > fuzzy_watt[1]) {
                //result tinggi
                if (status_fuzzy) {
                    textViewUtama2.setTextColor(Color.parseColor("#F06292"));
                    textBlink();
                    Toast.makeText(this, "Perhatian, Penggunaan Listrik Tinggi !", Toast.LENGTH_SHORT).show();
                    generateNotification("Penggunaan listrik anda " + textViewUtama2.getText() + " !", "Segera lakukan pengurangan penggunaan daya listrik anda");
                    alertDialogCreate("Penggunaan listrik tinggi ! " + textViewUtama2.getText() + " !");
                    DateFormat df = new SimpleDateFormat("dd MM yyyy, HH:mm");
                    String date = df.format(Calendar.getInstance().getTime());
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference myRef = database.getReference("Home/time/" + date);
                    myRef.setValue(nilaiCurrenTotal);
                }
            }
        }
    }


    private void addDataSet() {
        Log.d(TAG, "addDataSet started");
        ArrayList<PieEntry> zEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        for (int i = 0; i < yData.length; i++) {
            zEntrys.add(new PieEntry(zData[i], i));
        }

        for (int i = 1; i < xData.length; i++) {
            xEntrys.add(xData[i]);
        }

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(zEntrys, "Energy Record");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);


        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        //add legend to chart
        Legend legend = pieChart1.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setEnabled(true);
        LegendEntry l1 = new LegendEntry("Sen 1", Legend.LegendForm.DEFAULT, 10f, 2f, null, Color.parseColor("#F06292"));
        LegendEntry l2 = new LegendEntry("Sen 2", Legend.LegendForm.DEFAULT, 10f, 2f, null, Color.parseColor("#FB8C00"));
        LegendEntry l3 = new LegendEntry("Sen 3", Legend.LegendForm.DEFAULT, 10f, 2f, null, Color.parseColor("#FFF176"));
        LegendEntry l4 = new LegendEntry("Sen 4", Legend.LegendForm.DEFAULT, 10f, 2f, null, Color.parseColor("#4DB6AC"));

        legend.setCustom(new LegendEntry[]{l1, l2, l3, l4});

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart1.setData(pieData);
        pieChart1.invalidate();
    }

    public void alertDialogCreate(String keterangan) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("WARNING !");
        alertDialogBuilder
                .setMessage(keterangan)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dismissNotification();
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void saveAlertLog(Integer nomorSensor, String konten) {
        DateFormat df = new SimpleDateFormat("dd MM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Sensor/" + nomorSensor + "/time/" + date);
        myRef.setValue(konten);
    }

    public void textBlink() {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500); //You can manage the blinking time with this parameter
        anim.setStartOffset(200);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        textViewUtama2.startAnimation(anim);
    }

    public void checkPing() {
        Log.d("Check Ping", "Check Ping RUN");
        Drawable red = getResources().getDrawable(R.drawable.button_red);
        for (int i = 0; i < 4; i++) {
            Log.d("Value Check "+i,""+ ping[i]);

            if (ping[i]== false) {
                if (count[i] > 2) {
                    switch ((i + 1)) {
                        case 1:
                            buttonHal1.setTextColor(Color.WHITE);
                            buttonHal1.setBackground(red);
                            inputFirebase("Sensor/1/watt_current", "0");
                            break;
                        case 2:
                            buttonHal2.setTextColor(Color.WHITE);
                            buttonHal2.setBackground(red);
                            inputFirebase("Sensor/2/watt_current", "0");
                            break;
                        case 3:
                            buttonHal3.setTextColor(Color.WHITE);
                            buttonHal3.setBackground(red);
                            inputFirebase("Sensor/3/watt_current", "0");
                            break;
                        case 4:
                            buttonHal4.setTextColor(Color.WHITE);
                            buttonHal4.setBackground(red);
                            inputFirebase("Sensor/4/watt_current", "0");
                            break;
                    }
                    count[i] = 0;
                }
                count[i] = count[0] + 1;
            }
        }
    }

    public void sendPing(){
        Log.d("SendPing", "send Ping RUN");
        for (int i = 0; i < 4; i++) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference("Sensor/" + (i + 1) + "/ping");
            myRef.setValue("ping");
        }
    }

}
