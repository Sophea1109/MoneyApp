package com.example.moneyapp.Dom;
import android.content.Intent;
import android.os.Bundle;

import android.os.PersistableBundle;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.Dom.setting;
import com.example.moneyapp.Lida.report_screen;
import com.example.moneyapp.Lida.transaction_screen;
import com.example.moneyapp.R;
import com.example.moneyapp.Sophea.account_icon;
import com.example.moneyapp.Sophea.after_sign_in;
import com.example.moneyapp.databinding.SettingBinding;

public class setting extends AppCompatActivity{

    private SettingBinding binding;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = SettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageButton homeBtn = findViewById(R.id.home);
        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(setting.this, after_sign_in.class);
            startActivity(intent);
        });

        ImageButton accountBtn = findViewById(R.id.account);
        accountBtn.setOnClickListener(v -> {
            Intent intent = new Intent(setting.this, account_icon.class);
            startActivity(intent);
        });

        ImageButton reportBtn = findViewById(R.id.report);
        reportBtn.setOnClickListener(v -> {
            Intent intent = new Intent(setting.this, report_screen.class);
            startActivity(intent);
        });

        ImageButton settingBtn = findViewById(R.id.setting);
        settingBtn.setOnClickListener(v -> {
            Intent intent = new Intent(setting.this, setting.class);
            startActivity(intent);
        });

        ImageButton plusBtn = findViewById(R.id.plus);
        plusBtn.setOnClickListener(v -> {
            Intent intent = new Intent(setting.this, transaction_screen.class);
            startActivity(intent);
        });
    }
}
