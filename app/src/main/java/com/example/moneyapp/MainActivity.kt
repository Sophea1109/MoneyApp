package com.example.donutchart

import android.R
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pieChart: PieChart = findViewById(R.id.donutChart)

        val entries: ArrayList<PieEntry?> = ArrayList<PieEntry?>()
        entries.add(PieEntry(50f, "50%"))
        entries.add(PieEntry(30f, "30%"))
        entries.add(PieEntry(20f, "20%"))

        // 2️⃣ Create dataset
        val dataSet: PieDataSet = PieDataSet(entries, "")

        // 🎨 3️⃣ Custom blue shades
        val colors: MutableList<Int?> = ArrayList<Int?>()
        colors.add(Color.parseColor("#00008B")) // Deep blue (50%)
        colors.add(Color.parseColor("#4169E1")) // Royal blue (30%)
        colors.add(Color.parseColor("#6A8DFF")) // Light blue (20%)
        dataSet.setColors(colors)
        dataSet.setValueTextSize(16f)
        dataSet.setValueTextColor(Color.BLACK)

        val data: PieData = PieData(dataSet)
        pieChart.setData(data)

        pieChart.setUsePercentValues(false)
        pieChart.setHoleRadius(70f)
        pieChart.setTransparentCircleRadius(75f)
        pieChart.setHoleColor(Color.BLACK)

        pieChart.setCenterText("Today")
        pieChart.setCenterTextSize(26f)
        pieChart.setCenterTextColor(Color.WHITE)

        pieChart.getDescription().setEnabled(false)
        pieChart.getLegend().setEnabled(false)

        pieChart.animateY(1400)
    }
}