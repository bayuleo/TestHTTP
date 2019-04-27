package com.example.bayuleo.testhttp;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PieChartTest extends AppCompatActivity {

    private static String TAG = "PieChartTest";

    private double[] yData = new double[4];
    private float[] zData = new float[4];
    private String[] xData = {"Sen 1", "Sen 2" , "Sen 3" , "Sen 4"};
    PieChart pieChart;

    DatabaseReference testFB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart_test);

        Log.d(TAG, "onCreate: starting to create chart");

        pieChart = (PieChart) findViewById(R.id.idPieChart);

        //pieChart.setDescription("Sales by employee (In Thousands $) ");
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleAlpha(20);
        pieChart.setCenterText("Current Watt");
        pieChart.setCenterTextSize(10);

        //pieChart.animateX(1000);




        testFB = FirebaseDatabase.getInstance().getReference("Sensor");
        testFB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                yData[0] = (double) dataSnapshot.child("1").child("watt_current").getValue();
                yData[1] = (double) dataSnapshot.child("2").child("watt_current").getValue();
                yData[2] = (double) dataSnapshot.child("3").child("watt_current").getValue();
                yData[3] = (double) dataSnapshot.child("4").child("watt_current").getValue();

                //Data harus diconvert ke float, karena enggak support double
                zData[0] = (float)yData[0];
                zData[1] = (float)yData[1];
                zData[2] = (float)yData[2];
                zData[3] = (float)yData[3];

                pieChart.animateXY(2000,1000);
                addDataSet();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addDataSet() {
        Log.d(TAG, "addDataSet started");
        ArrayList<PieEntry> zEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        for(int i = 0; i < yData.length; i++){
            zEntrys.add(new PieEntry(zData[i] , i));
        }

        for(int i = 1; i < xData.length; i++){
            xEntrys.add(xData[i]);
        }

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(zEntrys, "Energy Record");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        //add colors to dataset
//        ArrayList<Integer> colors = new ArrayList<>();
//        colors.add();
//        colors.add(R.color.chart2);
//        colors.add(R.color.chart3);
//        colors.add(R.color.chart4);
//        colors.add(R.color.chart5);
//        colors.add(Color.YELLOW);
//        colors.add(Color.MAGENTA);

        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        //add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setFormSize(10f);


        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}
