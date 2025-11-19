package com.example.moneyapp;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);

        PieChart pieChart = findViewById(R.id.pieChart);

        // Pie entries
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(40f, "Food"));
        entries.add(new PieEntry(25f, "Bills"));
        entries.add(new PieEntry(20f, "Shopping"));
        entries.add(new PieEntry(15f, "Other"));

        // Pie dataset
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{
                Color.parseColor("#4CAF50"),  // green
                Color.parseColor("#2196F3"),  // blue
                Color.parseColor("#FFC107"),  // yellow
                Color.parseColor("#F44336")   // red
        });
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        // Pie data
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter()); // show percentages

        // Configure PieChart
        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setHoleRadius(55f);
        pieChart.setTransparentCircleRadius(60f);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setDrawEntryLabels(true);
        pieChart.getLegend().setEnabled(true); // show legend
        pieChart.getLegend().setTextColor(Color.WHITE);
        pieChart.animateXY(1000, 1000);
    }
}
