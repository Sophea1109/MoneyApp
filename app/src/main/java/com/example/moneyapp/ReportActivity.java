package com.example.moneyapp;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

    public class ReportActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.report_screen);

            ImageView backBtn = findViewById(R.id.btnBack);
            if (backBtn != null) {
                backBtn.setOnClickListener(v -> onBackPressed());
            }
        }
    }


