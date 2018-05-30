package com.example.bayuleo.testhttp;

import android.app.DownloadManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private Timer myTimer;

    private String url_saklar = "https://api.thingspeak.com/update.json?api_key=5A8ZVNQJX3GY2OUE&field1=";
    private String url_get = "https://api.thingspeak.com/channels/479672/feeds.json?results=5";
    OkHttpClient client = new OkHttpClient();

    TextView textView;
    Button buttonON, buttonOFF, buttonJ;
    String valamat;

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
        textView = (TextView) findViewById(R.id.textView3);
        buttonON = (Button) findViewById(R.id.buttonON);
        buttonOFF = (Button) findViewById(R.id.buttonOFF);
        buttonJ = findViewById(R.id.buttonJson);


        //method pada saat button proses diklik
        buttonON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saklarSet("1");
                Toast.makeText(MainActivity.this, "Saklar ON", Toast.LENGTH_SHORT).show();

            }
        });

        buttonOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saklarSet("5");
                Toast.makeText(MainActivity.this, "Saklar OFF", Toast.LENGTH_SHORT).show();
            }
        });

        buttonJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ambiljson();
            }
        });








    }

    public void saklarSet(String val){
        //String url = "https://api.thingspeak.com/update.json?api_key=5A8ZVNQJX3GY2OUE&field1="+val+"";
        //Membuat intace baru
        //OkHttpClient client = new OkHttpClient();

        //membuat cache agar hemat bandwith
        client.cache();

        //Membuat Request
        Request request = new Request.Builder()
                .url(url_saklar+val)
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
            ambiljson();

        }
    };

    public void ambiljson(){
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
                            parsingarray(hasil);
                        }
                    });
                }

            }
        });
    }



    private void parsingarray(String datajson){
        try {
            JSONObject jsonObj = new JSONObject(datajson); //ambil object json
            Log.d("Run","run" + jsonObj);
            //JSONObject jsonpandawa = jsonObj.getJSONObject("feeds"); //ambil obj pandawa
            Log.d("Run","run2");
            //JSONArray anggota = jsonpandawa.getJSONArray("feeds"); //ambil array anggota
            JSONArray anggota = jsonObj.getJSONArray("feeds");
            Log.d("Run","run3");
            //for (int i = 0; i < anggota.length(); i++) {
                //JSONObject jsonobject = anggota.getJSONObject(i);
            JSONObject jsonobject = anggota.getJSONObject(anggota.length()-1);
                //vfild = jsonobject.getString("feeds");
                valamat = jsonobject.getString("field1");
            //}
            //tnama.setText(vnama);
            textView.setText(valamat);
        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + datajson + "\"");
        }
    }


}
