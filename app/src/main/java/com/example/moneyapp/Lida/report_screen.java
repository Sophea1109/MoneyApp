package com.example.moneyapp.Lida;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.AppNavigator;
import com.example.moneyapp.Database.DatabaseHelper;
import com.example.moneyapp.Dom.setting;
import com.example.moneyapp.R;
import com.example.moneyapp.SessionManager;
import com.example.moneyapp.Sophea.account_icon;
import com.example.moneyapp.Sophea.after_sign_in;
import com.example.moneyapp.databinding.ReportScreenBinding;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class report_screen extends AppCompatActivity {

    private ReportScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ReportScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Bottom nav
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
        // Use userId (FK) to query totals — always scoped to the correct user
        int userId = SessionManager.getCurrentUserId(this);
        DatabaseHelper db = new DatabaseHelper(this);

        double spending = db.getTotalAmount("spending", userId);
        double income = db.getTotalAmount("income", userId);
        double budget = db.getTotalAmount("budget", userId);

        TextView incomeView = findViewById(R.id.tvIncome);
        incomeView.setText("$" + String.format("%.2f", income));

        TextView spendingView = findViewById(R.id.tvSpending);
        spendingView.setText("$" + String.format("%.2f", spending));

        TextView budgetView = findViewById(R.id.tvBudget);
        budgetView.setText("$" + String.format("%.2f", budget));

        double net = income - spending;
        TextView netView = findViewById(R.id.tvNetBalance);
        netView.setText("Net: " + (net >= 0 ? "+" : "") + "$" + String.format("%.2f", net));
        netView.setTextColor(net >= 0
                ? android.graphics.Color.parseColor("#4CAF50")
                : android.graphics.Color.parseColor("#FF4444"));

        TextView warningView = findViewById(R.id.tvBudgetWarning);
        if (budget > 0 && spending > budget) {
            warningView.setVisibility(View.VISIBLE);
            warningView.setText("Warning: spending exceeds budget by $"
                    + String.format("%.2f", spending - budget));
        } else {
            warningView.setVisibility(View.GONE);
        }

        int spentPercent = budget > 0
                ? (int) Math.min(100, Math.round((spending / budget) * 100))
                : 0;

        CircularProgressIndicator reportProgress = findViewById(R.id.reportProgress);
        reportProgress.setMax(100);
        reportProgress.setProgress(spentPercent);

        TextView percentText = findViewById(R.id.tvSpentPercent);
        percentText.setText(spentPercent + "%");
    }
}