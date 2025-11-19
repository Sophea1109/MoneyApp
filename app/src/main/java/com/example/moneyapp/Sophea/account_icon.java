package com.example.moneyapp.Sophea;
import android.content.Intent;
import android.os.Bundle;
//account_icon

import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.R;
import com.example.moneyapp.databinding.AccountIconBinding;

public class account_icon  extends AppCompatActivity{

    private AccountIconBinding binding;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = AccountIconBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageButton homeBtn = findViewById(R.id.home);
        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(account_icon.this, after_sign_in.class);
            startActivity(intent);
        });

        ImageButton accountBtn = findViewById(R.id.account);
        accountBtn.setOnClickListener(v -> {
            Intent intent = new Intent(account_icon.this, after_sign_in.class);
            startActivity(intent);
        });
    }
}
