package com.example.moneyapp.Sophea;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
//after_sign_in

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.Dom.setting;
import com.example.moneyapp.Lida.report_screen;
import com.example.moneyapp.Lida.transaction_screen;
import com.example.moneyapp.R;
import com.example.moneyapp.databinding.AfterSignInBinding;

public class after_sign_in extends AppCompatActivity{

    private AfterSignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = AfterSignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageButton homeBtn = findViewById(R.id.home);
        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(after_sign_in.this, after_sign_in.class);
            startActivity(intent);
        });

        ImageButton accountBtn = findViewById(R.id.account);
        accountBtn.setOnClickListener(v -> {
            Intent intent = new Intent(after_sign_in.this, account_icon.class);
            startActivity(intent);
        });

        ImageButton reportBtn = findViewById(R.id.report);
        reportBtn.setOnClickListener(v -> {
            Intent intent = new Intent(after_sign_in.this, report_screen.class);
            startActivity(intent);
        });

        ImageButton settingBtn = findViewById(R.id.setting);
        settingBtn.setOnClickListener(v -> {
            Intent intent = new Intent(after_sign_in.this, setting.class);
            startActivity(intent);
        });

        ImageButton plusBtn = findViewById(R.id.plus);
        plusBtn.setOnClickListener(v -> {
            Intent intent = new Intent(after_sign_in.this, transaction_screen.class);
            startActivity(intent);
        });
    }
}
