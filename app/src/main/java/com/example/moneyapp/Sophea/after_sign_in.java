package com.example.moneyapp.Sophea;
//import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
//after_sign_in

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.AppNavigator;
import com.example.moneyapp.Dom.setting;
import com.example.moneyapp.HistoryRepository;
import com.example.moneyapp.Lida.report_screen;
import com.example.moneyapp.Lida.transaction_screen;
import com.example.moneyapp.R;
import com.example.moneyapp.SessionManager;
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
    }

        private void bindHomeSummary() {
            boolean showTotal = getSharedPreferences("MoneyApp", MODE_PRIVATE)
                    .getBoolean("show_total", true);

            TextView savingAmount = findViewById(R.id.savingAmount);

            if (showTotal) {
                savingAmount.setVisibility(View.VISIBLE);
            } else {
                savingAmount.setVisibility(View.GONE);
            }

            SharedPreferences prefs = getSharedPreferences("MoneyApp", MODE_PRIVATE);

            String spendingText = prefs.getString("transaction_value", "0.00");
            String incomeText = prefs.getString("income_value", "0.00");
            String budgetText = prefs.getString("budget_value", "0.00");

            double spending = HistoryRepository.toAmount(spendingText);
            double budget = HistoryRepository.toAmount(budgetText);
            double saving = budget - spending;

            Button spendingBtn = findViewById(R.id.btnSpending);
            spendingBtn.setText("Spending      $" + spendingText);

            Button incomeBtn = findViewById(R.id.btnIncome);
            incomeBtn.setText("Income      $" + incomeText);

            savingAmount.setText("$" + String.format("%.2f", saving));

            int spentPercent = budget > 0 ? (int) Math.min(100, Math.round((spending / budget) * 100)) : 0;

            CircularProgressIndicator homeProgress = findViewById(R.id.homeProgress);
            homeProgress.setMax(100);
            homeProgress.setProgress(spentPercent);

            TextView percentView = findViewById(R.id.homeSpentPercent);
            percentView.setText("Spent: " + spentPercent + "%");
        }
}
