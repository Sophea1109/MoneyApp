package com.example.moneyapp.Lida;
//import android.content.Intent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.AppNavigator;
import com.example.moneyapp.HistoryRepository;
import com.example.moneyapp.Dom.setting;
import com.example.moneyapp.R;
import com.example.moneyapp.Sophea.account_icon;
import com.example.moneyapp.Sophea.after_sign_in;
import com.example.moneyapp.UserDataManager;
import com.example.moneyapp.databinding.ReportScreenBinding;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class report_screen extends AppCompatActivity {
    private ReportScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ReportScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageButton homeBtn = findViewById(R.id.home);
        homeBtn.setOnClickListener(v -> AppNavigator.navigateTo(report_screen.this, after_sign_in.class));

        ImageButton accountBtn = findViewById(R.id.account);
        accountBtn.setOnClickListener(v -> AppNavigator.navigateTo(report_screen.this, account_icon.class));

        ImageButton reportBtn = findViewById(R.id.report);
        reportBtn.setOnClickListener(v -> AppNavigator.navigateTo(report_screen.this, report_screen.class));

        ImageButton settingBtn = findViewById(R.id.setting);
        settingBtn.setOnClickListener(v -> AppNavigator.navigateTo(report_screen.this, setting.class));

        ImageButton plusBtn = findViewById(R.id.plus);
        plusBtn.setOnClickListener(v -> AppNavigator.navigateTo(report_screen.this, transaction_screen.class));

        Button incomeHistory = findViewById(R.id.btnIncomeHistory);
        incomeHistory.setOnClickListener(v -> openHistory("income"));

        Button spendingHistory = findViewById(R.id.btnTransactionHistory);
        spendingHistory.setOnClickListener(v -> openHistory("transaction"));

        Button budgetHistory = findViewById(R.id.btnBudgetHistory);
        budgetHistory.setOnClickListener(v -> openHistory("budget"));

        bindReportData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindReportData();
    }

    private void openHistory(String type) {
        Intent intent = new Intent(this, history_screen.class);
        intent.putExtra("history_type", type);
        startActivity(intent);
    }

    private void bindReportData() {
        SharedPreferences prefs = UserDataManager.getPrefs(this);

        String spendingText = prefs.getString("transaction_value", "0.00");
        String incomeText = prefs.getString("income_value", "0.00");
        String budgetText = prefs.getString("budget_value", "0.00");

        double spending = HistoryRepository.toAmount(spendingText);
        double budget = HistoryRepository.toAmount(budgetText);

        TextView incomeView = findViewById(R.id.tvIncome);
        incomeView.setText("$" + incomeText);

        TextView spendingView = findViewById(R.id.tvSpending);
        spendingView.setText("$" + spendingText);

        TextView budgetView = findViewById(R.id.tvBudget);
        budgetView.setText("$" + budgetText);

        int spentPercent = budget > 0 ? (int) Math.min(100, Math.round((spending / budget) * 100)) : 0;
        CircularProgressIndicator reportProgress = findViewById(R.id.reportProgress);
        reportProgress.setMax(100);
        reportProgress.setProgress(spentPercent);
        TextView percentText = findViewById(R.id.tvSpentPercent);
        percentText.setText(spentPercent + "%");
    }
}
