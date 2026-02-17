package com.example.moneyapp.Dom;
import static androidx.core.content.ContextCompat.startActivity;

//import android.content.Intent;
import android.os.Bundle;

import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.AppNavigator;
import com.example.moneyapp.Lida.report_screen;
import com.example.moneyapp.Lida.transaction_screen;
import com.example.moneyapp.R;
import com.example.moneyapp.SessionManager;
import com.example.moneyapp.Sophea.SignIn;
import com.example.moneyapp.Sophea.account_icon;
import com.example.moneyapp.Sophea.after_sign_in;
import com.example.moneyapp.databinding.SettingBinding;

public class setting extends AppCompatActivity{

    private SettingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = SettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageButton homeBtn = findViewById(R.id.home);
        homeBtn.setOnClickListener(v -> AppNavigator.navigateTo(setting.this, after_sign_in.class));

        ImageButton accountBtn = findViewById(R.id.account);
        accountBtn.setOnClickListener(v -> AppNavigator.navigateTo(setting.this, account_icon.class));

        ImageButton reportBtn = findViewById(R.id.report);
        reportBtn.setOnClickListener(v -> AppNavigator.navigateTo(setting.this, report_screen.class));

        ImageButton settingBtn = findViewById(R.id.setting);
        settingBtn.setOnClickListener(v -> AppNavigator.navigateTo(setting.this, setting.class));

        ImageButton plusBtn = findViewById(R.id.plus);
        plusBtn.setOnClickListener(v -> AppNavigator.navigateTo(setting.this, transaction_screen.class));

        Button aboutBtn = findViewById(R.id.aboutBtn);
        aboutBtn.setOnClickListener(v -> AppNavigator.navigateTo(setting.this, about.class));

        Switch showTotalSwitch = findViewById(R.id.showTotalSwitch);
        boolean showTotal = getSharedPreferences("MoneyApp", MODE_PRIVATE)
                .getBoolean("show_total", true);

        showTotalSwitch.setChecked(showTotal);
        showTotalSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getSharedPreferences("MoneyApp", MODE_PRIVATE)
                    .edit()
                    .putBoolean("show_total", isChecked)
                    .apply();
        });

        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> {
            SessionManager.setLoggedIn(setting.this, false);
            android.content.Intent intent = new android.content.Intent(setting.this, SignIn.class);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
