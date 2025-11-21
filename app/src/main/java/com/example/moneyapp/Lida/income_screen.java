package com.example.moneyapp.Lida;
import android.content.Intent;
import android.os.Bundle;

import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.R;
import com.example.moneyapp.Sophea.account_icon;
import com.example.moneyapp.databinding.IncomeScreenBinding;

public class income_screen extends AppCompatActivity{

    private IncomeScreenBinding binding;
    private EditText dateIncome;
    private EditText amtIncome;
    private EditText detailsIncome;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = IncomeScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageButton IncomeBtn = findViewById(R.id.incomeBtn);
        IncomeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(income_screen.this, account_icon.class);
            startActivity(intent);
        });

        dateIncome = findViewById(R.id.dateIncome);
        amtIncome = findViewById(R.id.amtIncome);
        detailsIncome = findViewById(R.id.detailsIncome);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String saveDate1 = getSharedPreferences("MoneyApp", MODE_PRIVATE)
                .getString("income_date", "");
        String saveIncome = getSharedPreferences("MoneyApp", MODE_PRIVATE)
                .getString("income_value", "");
        String saveDetails1 = getSharedPreferences("MoneyApp", MODE_PRIVATE)
                .getString("income_details", "");

        dateIncome.setText(saveDate1);
        amtIncome.setText(saveIncome);
        detailsIncome.setText(saveDetails1);
    }
    @Override
    protected void onPause(){
        super.onPause();

        String Date1 = dateIncome.getText().toString().trim();
        String Income = amtIncome.getText().toString().trim();
        String Details1 = detailsIncome.getText().toString().trim();

        getSharedPreferences("MoneyApp", MODE_PRIVATE)
                .edit()
                .putString("income_date", Date1)
                .putString("income_value", Income)
                .putString("income_details", Details1)
                .apply();
    }
}
