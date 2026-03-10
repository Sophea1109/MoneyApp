package com.example.moneyapp.Sophea;
//import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
//after_sign_in

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.AppNavigator;
import com.example.moneyapp.Database.DatabaseHelper;
import com.example.moneyapp.Dom.setting;
import com.example.moneyapp.HistoryRepository;
import com.example.moneyapp.Lida.report_screen;
import com.example.moneyapp.Lida.transaction_screen;
import com.example.moneyapp.R;
import com.example.moneyapp.SessionManager;
import com.example.moneyapp.UserDataManager;
import com.example.moneyapp.databinding.AfterSignInBinding;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class after_sign_in extends AppCompatActivity{

    private AfterSignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = AfterSignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SessionManager.setLoggedIn(after_sign_in.this, true);

        ImageButton homeBtn = findViewById(R.id.home);
        homeBtn.setOnClickListener(v -> AppNavigator.navigateTo(after_sign_in.this, after_sign_in.class));

        ImageButton accountBtn = findViewById(R.id.account);
        accountBtn.setOnClickListener(v -> AppNavigator.navigateTo(after_sign_in.this, account_icon.class));

        ImageButton reportBtn = findViewById(R.id.report);
        reportBtn.setOnClickListener(v -> AppNavigator.navigateTo(after_sign_in.this, report_screen.class));

        ImageButton settingBtn = findViewById(R.id.setting);
        settingBtn.setOnClickListener(v -> AppNavigator.navigateTo(after_sign_in.this, setting.class));

        ImageButton plusBtn = findViewById(R.id.plus);
        plusBtn.setOnClickListener(v -> AppNavigator.navigateTo(after_sign_in.this, transaction_screen.class));

        bindHomeSummary();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindHomeSummary();
        bindProfileImage();
    }

    private void bindProfileImage() {
        ImageButton profileImage = findViewById(R.id.homeProfileImage);
        String uriValue = new DatabaseHelper(this).getProfileImageUri(SessionManager.getCurrentUser(this));

        if (uriValue == null || uriValue.trim().isEmpty()) {
            profileImage.setImageResource(R.drawable.moon);
            return;
        }
        try {
            profileImage.setImageURI(Uri.parse(uriValue));
        } catch (Exception ignored) {
            profileImage.setImageResource(R.drawable.moon);
        }
    }

    private void bindHomeSummary() {
        boolean showTotal = UserDataManager.getPrefs(this)
                .getBoolean("show_total", true);
        TextView savingAmount = findViewById(R.id.savingAmount);
        if (showTotal) {
            savingAmount.setVisibility(View.VISIBLE);
        } else {
            savingAmount.setVisibility(View.GONE);
        }

        double spending = HistoryRepository.getTotalAmount(this, "transaction");
        double income = HistoryRepository.getTotalAmount(this, "income");
        double budget = HistoryRepository.getTotalAmount(this, "budget");
            double saving = budget - spending;

            Button spendingBtn = findViewById(R.id.btnSpending);
            spendingBtn.setText("Spending      $" + String.format("%.2f", spending));

            Button incomeBtn = findViewById(R.id.btnIncome);
            incomeBtn.setText("Income      $" + String.format("%.2f", income));

            savingAmount.setText("$" + String.format("%.2f", saving));

            int spentPercent = budget > 0 ? (int) Math.min(100, Math.round((spending / budget) * 100)) : 0;

            CircularProgressIndicator homeProgress = findViewById(R.id.homeProgress);
            homeProgress.setMax(100);
            homeProgress.setProgress(spentPercent);

            TextView percentView = findViewById(R.id.homeSpentPercent);
            percentView.setText("Spent: " + spentPercent + "%");
        }
}
