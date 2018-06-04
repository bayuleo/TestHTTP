package com.example.bayuleo.testhttp;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.CacheControl;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



public class MainActivity extends AppCompatActivity {


    public static String FIELD_KEY = "field";

    public Timer myTimer;

    private String url_saklar = "https://api.thingspeak.com/update.json?api_key=5A8ZVNQJX3GY2OUE&field";
    private String url_get = "https://api.thingspeak.com/channels/479672/feeds.json?results=100";
    OkHttpClient client = new OkHttpClient();
    String html = "<iframe width=\"450\" height=\"260\" style=\"border: 1px solid #cccccc;\" src=\"https://thingspeak.com/channels/479672/widgets/2759\"></iframe>";

    TextView textView0;
    Button buttonON, buttonOFF, buttonHal1, buttonHal2, buttonHal3, buttonHal4;
    public String[] think = new String[10];
    Snackbar snackbar;

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

        }, 0, 1000);

        //Inisialisasi textView dan button
        textView0 = (TextView) findViewById(R.id.textView0);
        buttonON = (Button) findViewById(R.id.buttonON);
        buttonOFF = (Button) findViewById(R.id.buttonOFF);
        buttonHal1 = findViewById(R.id.buttonPindahHal1);
        buttonHal2 = findViewById(R.id.buttonPindahHal2);
        buttonHal3 = findViewById(R.id.buttonPindahHal3);
        buttonHal4 = findViewById(R.id.buttonPindahHal4);




        final Intent pergiKe = new Intent(MainActivity.this, Halaman1.class);



        //method pada saat button proses diklik
        buttonON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saklarSet(2,"0");
                Toast.makeText(MainActivity.this, "Saklar ON", Toast.LENGTH_SHORT).show();


            }
        });

        buttonOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saklarSet(2,"1");
                Toast.makeText(MainActivity.this, "Saklar OFF", Toast.LENGTH_SHORT).show();
                snackbar = Snackbar.make(view, "Uji Coba Snackbar", Snackbar.LENGTH_LONG);
                snackbar.setAction("Klik me", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                    }
                });
                snackbar.show();
            }
        });

        buttonHal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int field = 1;
                pergiKe.putExtra(FIELD_KEY,field);
                startActivity(pergiKe);
            }
        });

        buttonHal2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int field = 2;
                pergiKe.putExtra(FIELD_KEY,field);
                startActivity(pergiKe);
            }
        });

        buttonHal3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int field = 3;
                pergiKe.putExtra(FIELD_KEY,field);
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









    }

    public void generateNotification(String judul, String deskripsi){
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(judul)
                .setContentText(deskripsi);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
    }

    public void saklarSet(int noField, String val){
        //String url = "https://api.thingspeak.com/update.json?api_key=5A8ZVNQJX3GY2OUE&field2="+val+"";
        //Membuat intace baru
        //OkHttpClient client = new OkHttpClient();

        //membuat cache agar hemat bandwith
        client.cache();

        //Membuat Request
        Request request = new Request.Builder()
                .url(url_saklar+noField+"="+val)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    //final String myResponse = response.body().toString();

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //textView.setText(myResponse);
                        }
                    });
                }

            }
        });
    }


    private void TimerMethod(){
        this.runOnUiThread(Timer_Tick);
    }

    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            ambiljson(textView0,1);

        }
    };

    public void ambiljson(final TextView td, final int thingspeak){
        Request request = new Request.Builder()
                .url(url_get)
                .cacheControl(new CacheControl.Builder().noCache().build())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d("ON FAILURE", e.getStackTrace().toString());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.d("ON RESPON ERROR", String.valueOf(response));
                    throw new IOException("Unexpected code " +  response);
                } else {
                    final String hasil =response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parsingarray(hasil, td, thingspeak);
                        }
                    });
                }

            }
        });
    }



    private void parsingarray(String datajson, TextView tv, int var){
        try {
            JSONObject jsonObj = new JSONObject(datajson); //ambil object json
//            Log.d("","Run Json" + jsonObj);
            //JSONObject jsonpandawa = jsonObj.getJSONObject("feeds"); //ambil obj pandawa
//            Log.d("Run","run2");
            //JSONArray anggota = jsonpandawa.getJSONArray("feeds"); //ambil array anggota
            JSONArray anggota = jsonObj.getJSONArray("feeds");
//            Log.d("Run","run3");
            for (int i = 0; i < anggota.length(); i++) {
                JSONObject jsonobject = anggota.getJSONObject(i);
            //JSONObject jsonobject = anggota.getJSONObject(anggota.length()-1);
                //vfild = jsonobject.getString("feeds");
                for (int x = 1;x<9;x++){
                    String posisi = Integer.toString(x);
                    //Log.d(posisi,"ini nilai posisi");
                    String temp = jsonobject.getString("field"+posisi);
                    if (!temp.equals("null")){
                        think[x] = temp;
                    }
                }




//            think[1] = jsonobject.getString("field1");
//            think[2] = jsonobject.getString("field2");
//            think[3] = jsonobject.getString("field3");
//            think[4] = jsonobject.getString("field4");
//            think[5] = jsonobject.getString("field5");
//            think[6] = jsonobject.getString("field6");
//            think[7] = jsonobject.getString("field7");
//            think[8] = jsonobject.getString("field8");
            }
            //tnama.setText(vnama);
            if (!think[var].equals("null")) {
                Double a = Double.parseDouble(think[var]);
                Integer b = a.intValue();
                tv.setText(b.toString());
            }
        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + datajson + "\"");
        }
    }

    public void inputFirebase(String firebase_url, String firebase_value){
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(firebase_url);
        myRef.setValue(firebase_value);
    }




}
