package com.example.moneyapp.Dom;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.AppNavigator;
import com.example.moneyapp.Database.DatabaseHelper;
import com.example.moneyapp.Lida.report_screen;
import com.example.moneyapp.Lida.transaction_screen;
import com.example.moneyapp.R;
import com.example.moneyapp.SessionManager;
import com.example.moneyapp.Sophea.SignIn;
import com.example.moneyapp.Sophea.account_icon;
import com.example.moneyapp.Sophea.after_sign_in;
import com.example.moneyapp.UserDataManager;
import com.example.moneyapp.databinding.SettingBinding;

public class setting extends AppCompatActivity {

    private SettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = SettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Bottom nav
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

        Button editProfileButton = findViewById(R.id.EditProfile);
        editProfileButton.setOnClickListener(v ->
                startActivity(new Intent(setting.this, EditProfileActivity.class)));

        Switch showTotalSwitch = findViewById(R.id.showTotalSwitch);
        boolean showTotal = UserDataManager.getPrefs(this)
                .getBoolean("show_total", true);
        showTotalSwitch.setChecked(showTotal);

        showTotalSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                UserDataManager.getPrefs(setting.this)
                        .edit()
                        .putBoolean("show_total", isChecked)
                        .apply()
        );

        // Logout
        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> {
            SessionManager.clearSession(setting.this);
            Intent intent = new Intent(setting.this, SignIn.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Delete Account
        Button deleteAccountBtn = findViewById(R.id.deleteAccountBtn);
        deleteAccountBtn.setOnClickListener(v ->
                new AlertDialog.Builder(setting.this)
                        .setTitle("Delete Account")
                        .setMessage("This will permanently delete your account and all financial data. This cannot be undone.")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", (dialog, which) -> {
                            int userId = SessionManager.getCurrentUserId(setting.this);
                            boolean deleted = new DatabaseHelper(setting.this).deleteUserAccount(userId);
                            if (deleted) {
                                UserDataManager.getPrefs(setting.this).edit().clear().apply();
                                SessionManager.clearSession(setting.this);
                                Intent intent = new Intent(setting.this, SignIn.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(setting.this, "Failed to delete account",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show()
        );
    }
}