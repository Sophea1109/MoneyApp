package com.example.moneyapp.Lida;
import android.content.Intent;
import android.os.Bundle;

import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.R;
import com.example.moneyapp.Sophea.account_icon;
import com.example.moneyapp.databinding.TransactionScreenBinding;

public class transaction_screen extends AppCompatActivity{

    private TransactionScreenBinding binding;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = TransactionScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageButton TransactionBtn = findViewById(R.id.transactionBtn);
        TransactionBtn.setOnClickListener(v -> {
            Intent intent = new Intent(transaction_screen.this, account_icon.class);
            startActivity(intent);
        });
    }
}
