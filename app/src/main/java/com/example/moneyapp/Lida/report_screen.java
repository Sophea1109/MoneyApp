package com.example.moneyapp.Lida;
import android.content.Intent;
import android.os.Bundle;

import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.Dom.setting;
import com.example.moneyapp.Lida.report_screen;
import com.example.moneyapp.R;
import com.example.moneyapp.Sophea.account_icon;
import com.example.moneyapp.Sophea.after_sign_in;
import com.example.moneyapp.databinding.ReportScreenBinding;

public class report_screen extends AppCompatActivity{

    private ReportScreenBinding binding;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ReportScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageButton homeBtn = findViewById(R.id.home);
        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(report_screen.this, after_sign_in.class);
            startActivity(intent);
        });

        ImageButton accountBtn = findViewById(R.id.account);
        accountBtn.setOnClickListener(v -> {
            Intent intent = new Intent(report_screen.this, after_sign_in.class);
            startActivity(intent);
        });

        ImageButton reportBtn = findViewById(R.id.report);
        reportBtn.setOnClickListener(v -> {
            Intent intent = new Intent(report_screen.this, report_screen.class);
            startActivity(intent);
        });

        ImageButton settingBtn = findViewById(R.id.setting);
        settingBtn.setOnClickListener(v -> {
            Intent intent = new Intent(report_screen.this, setting.class);
            startActivity(intent);
        });

        ImageButton plusBtn = findViewById(R.id.plus);
        plusBtn.setOnClickListener(v -> {
            Intent intent = new Intent(report_screen.this, transaction_screen.class);
            startActivity(intent);
        });
    }
}
