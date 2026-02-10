package com.example.moneyapp.Sophea;
import android.content.Intent;
import android.os.Bundle;
//account_icon

import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.AppNavigator;
import com.example.moneyapp.Dom.setting;
import com.example.moneyapp.Lida.budget_screen;
import com.example.moneyapp.Lida.income_screen;
import com.example.moneyapp.Lida.report_screen;
import com.example.moneyapp.Lida.transaction_screen;
import com.example.moneyapp.R;
import com.example.moneyapp.databinding.AccountIconBinding;

public class account_icon  extends AppCompatActivity{

    private AccountIconBinding binding;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = AccountIconBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageButton homeBtn = findViewById(R.id.home);

        ImageButton accountBtn = findViewById(R.id.account);
        accountBtn.setOnClickListener(v -> AppNavigator.navigateTo(account_icon.this, account_icon.class));

        ImageButton reportBtn = findViewById(R.id.report);
        reportBtn.setOnClickListener(v -> AppNavigator.navigateTo(account_icon.this, report_screen.class));

        ImageButton settingBtn = findViewById(R.id.setting);
        settingBtn.setOnClickListener(v -> AppNavigator.navigateTo(account_icon.this, setting.class));

        Button incomeButton = findViewById(R.id.Income);
        incomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(account_icon.this, income_screen.class);
            startActivity(intent);
        });

        Button budgetButton = findViewById(R.id.Budget);
        budgetButton.setOnClickListener(v -> {
            Intent intent = new Intent(account_icon.this, budget_screen.class);
            startActivity(intent);
        });

        Button transactionButton = findViewById(R.id.Transaction);
        transactionButton.setOnClickListener(v -> {
            Intent intent = new Intent(account_icon.this, transaction_screen.class);
            startActivity(intent);
        });

        ImageButton plusBtn = findViewById(R.id.plus);
        plusBtn.setOnClickListener(v -> AppNavigator.navigateTo(account_icon.this, transaction_screen.class));
    }
}
