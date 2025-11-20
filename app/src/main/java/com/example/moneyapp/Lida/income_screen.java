package com.example.moneyapp.Lida;
import android.content.Intent;
import android.os.Bundle;

import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.R;
import com.example.moneyapp.Sophea.account_icon;
import com.example.moneyapp.databinding.IncomeScreenBinding;

public class income_screen extends AppCompatActivity{

    private IncomeScreenBinding binding;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = IncomeScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageButton IncomeBtn = findViewById(R.id.incomeBtn);
        IncomeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(income_screen.this, account_icon.class);
            startActivity(intent);
        });
    }
}
