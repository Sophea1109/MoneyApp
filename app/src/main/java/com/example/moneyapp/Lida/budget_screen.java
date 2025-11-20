package com.example.moneyapp.Lida;
import android.content.Intent;
import android.os.Bundle;

import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.R;
import com.example.moneyapp.Sophea.account_icon;
import com.example.moneyapp.databinding.BudgetScreenBinding;

public class budget_screen extends AppCompatActivity{

    private BudgetScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = BudgetScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageButton BudgetBtn = findViewById(R.id.budgetBtn);
        BudgetBtn.setOnClickListener(v -> {
            Intent intent = new Intent(budget_screen.this, account_icon.class);
            startActivity(intent);
        });
    }
}
