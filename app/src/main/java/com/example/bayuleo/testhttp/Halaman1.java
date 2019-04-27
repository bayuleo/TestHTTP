package com.example.bayuleo.testhttp;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class Halaman1 extends MainActivity {

    private static final String TAG = "Test TAG";
    TextView textViewHal1, textViewHal2, textViewSaklar, textViewTime, textViewTitle, textView3;
    EditText editTextLimit;
    Switch switchPower, switchLimit;
    int field, saklar, limitpower;
    Boolean bol_alarm, bol_limit;
    Double watt, currentWatt;
    Boolean KWH = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.halaman1);

        textViewHal1 = findViewById(R.id.textViewCurHal1);
        textViewHal2 = findViewById(R.id.textViewCurHal2);
        textViewSaklar = findViewById(R.id.textViewOnOff);
        textView3 = findViewById(R.id.textView3);
        textViewTime = findViewById(R.id.textViewTime);
        textViewTitle = findViewById(R.id.textView2);
        switchPower = findViewById(R.id.switchOnOff);
        switchLimit = findViewById(R.id.switchLimit);
        field = getIntent().getIntExtra(FIELD_KEY, field);
        editTextLimit = findViewById(R.id.editTextLimit);
        saklar = field + field;
        bol_alarm = true;
        bol_limit = true;

        textViewTitle.setText("Sensor " + field);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref_value_limit = database.getReference("Sensor/" + field);
        //Load data on first time
        ref_value_limit.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String limit_status = dataSnapshot.child("limit_status").getValue(String.class);
                String limit_value = dataSnapshot.child("limit_value").getValue(String.class);
                String power_status = dataSnapshot.child("power_status").getValue(String.class);
                String switch_power_status = dataSnapshot.child("switch_power_status").getValue(String.class);
                watt = Double.parseDouble(dataSnapshot.child("watt").getValue(String.class));
                currentWatt = Double.parseDouble(dataSnapshot.child("watt_current").getValue(String.class));


//                Date date = new Date(time);
//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//                String formattedDate = formatter.format(date);
//                textViewTime.setText(formattedDate);

                Log.d("Value Limit : ", limit_value);
                Log.d("Status Limit : ", limit_status);
                Log.d("Status Power : ", power_status);
                Log.d("Nilai watt : ", "" + watt);

                textViewHal1.setText(String.format("%,.2f", watt) + " KWh");
                textViewHal2.setText(String.format("%,.2f", currentWatt) + " W");
                textViewSaklar.setText(power_status);
                editTextLimit.setText(limit_value);

                if (switch_power_status.equals("ON")) {
                    switchPower.setChecked(true);
                } else if (switch_power_status.equals("OFF")) {
                    switchPower.setChecked(false);
                }

                if (limit_status.equals("ON")) {
                    switchLimit.setChecked(true);
                    bol_limit = true;
                } else if (limit_status.equals("OFF")) {
                    switchLimit.setChecked(false);
                    bol_limit = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read status limit.", error.toException());
            }
        });

        //Realtime update on running activity
        ref_value_limit.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String limit_status = dataSnapshot.child("limit_status").getValue(String.class);
                String limit_value = dataSnapshot.child("limit_value").getValue(String.class);
                String power_status = dataSnapshot.child("power_status").getValue(String.class);
                String switch_power_status = dataSnapshot.child("switch_power_status").getValue(String.class);
//                Long time = (Long) dataSnapshot.child("time").getValue();
                watt = Double.parseDouble(dataSnapshot.child("watt").getValue(String.class));
                currentWatt = Double.parseDouble(dataSnapshot.child("watt_current").getValue(String.class));


//                Date date = new Date(time);
//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//                String formattedDate = formatter.format(date);
//                textViewTime.setText(formattedDate);

                if (KWH){
                    textViewHal1.setText(String.format("%,.2f", (watt*1000)) + " Wh");
                }else{
                    textViewHal1.setText(String.format("%,.2f", watt) + " KWh");
                }
                textViewHal2.setText(String.format("%,.2f", currentWatt) + " W");
                textViewSaklar.setText(power_status);
                editTextLimit.setText(limit_value);

                if (switch_power_status.equals("ON")) {
                    switchPower.setChecked(true);
                } else if (switch_power_status.equals("OFF")) {
                    switchPower.setChecked(false);
                }

                if (limit_status.equals("ON")) {
                    switchLimit.setChecked(true);
                    bol_limit = true;
                } else if (limit_status.equals("OFF")) {
                    switchLimit.setChecked(false);
                    bol_limit = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        editTextLimit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String tempLimit = editTextLimit.getText().toString();
                if (tempLimit.equals("")) {
                    tempLimit = "0";
                }
                inputFirebase("Sensor/" + field + "/limit_value", tempLimit);
            }
        });


        switchPower.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    inputFirebase("Sensor/" + field + "/switch_power_status", "ON");
                } else {
                    inputFirebase("Sensor/" + field + "/switch_power_status", "OFF");
                }
            }
        });

        switchLimit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean c) {
                if (c) {
                    String tempLimit = editTextLimit.getText().toString();
                    Log.d("Nilai limit = ", tempLimit);
                    bol_limit = true;
                    if (!tempLimit.equals("")) {
                        Toast.makeText(Halaman1.this, "Turn ON Limit " + tempLimit + " Watt", Toast.LENGTH_SHORT).show();
                        editTextLimit.setEnabled(false);
                        limitpower = Integer.parseInt(tempLimit);
                        inputFirebase("Sensor/" + field + "/limit_status", "ON");
                    } else {
                        Toast.makeText(Halaman1.this, "Limit harus di set", Toast.LENGTH_SHORT).show();
                        switchLimit.setChecked(false);
                        inputFirebase("Sensor/" + field + "/limit_status", "OFF");
                    }
                } else {
                    editTextLimit.setEnabled(true);
                    Toast.makeText(Halaman1.this, "Turn OFF Limit ", Toast.LENGTH_SHORT).show();
                    bol_limit = false;
                    inputFirebase("Sensor/" + field + "/limit_status", "OFF");
                }
            }
        });


        textViewTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogReset("Apakah anda yakin ingin mereset data ?");
            }
        });

        textViewHal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (KWH){
                    KWH = false;
                    textViewHal1.setText(String.format("%,.2f", (watt*1000)) + " Wh");
                }else{
                    KWH = true;
                    textViewHal1.setText(String.format("%,.2f", watt) + " KWh");
                }
            }
        });
    }

    private String getDate(String timeStamp) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }

    private void CheckAlarm() {
        try {
//            if (bol_limit) {
            Integer c = Integer.parseInt(editTextLimit.getText().toString());
            Integer d = Integer.parseInt(textViewHal2.getText().toString());
            if (c.equals("Value")) {
                c = 0;
            } else if (d.equals("Value"))
                d = 0;
            if (c < limitpower) {
                if (bol_alarm) {
                    Log.d("Limit Alarm : ", "" + limitpower);
                    Toast.makeText(this, "Warning Over Usage !", Toast.LENGTH_SHORT).show();
                    generateNotification("Sensor 1 Over than " + limitpower + " Watt !", "Segera lakukan pengurangan penggunaan daya listrik anda");
                    bol_alarm = false;
                }

            } else {
                bol_alarm = true;
//                }
            }
        } catch (Exception e) {
            Log.d("error", "alarm");
            Log.d("Boolean alarm", "" + bol_alarm);
            Log.d("Boolean limit", "" + bol_limit);

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Respond to the action bar's Up/Home button
                finish();
                return false;
        }
        return super.onOptionsItemSelected(item);
    }


    public void alertDialogReset(String keterangan) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("PERHATIAN !");
        alertDialogBuilder
                .setMessage(keterangan)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        Toast.makeText(Halaman1.this, "Reset data", Toast.LENGTH_SHORT).show();
                        inputFirebase("Sensor/" + field + "/limit_value", "0");
                        inputFirebase("Sensor/" + field + "/watt", "0.0");
                        inputFirebase("Sensor/" + field + "/watt_current", "0.0");
                        inputFirebase("Sensor/" + field + "/limit_status", "OFF");

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
}
