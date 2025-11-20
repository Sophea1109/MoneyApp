package com.example.moneyapp.Lida.;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
//after_sign_in

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.R;
import com.example.moneyapp.databinding.AfterSignInBinding;

public class budget_screen extends AppCompatActivity{

    private AfterSignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = AfterSignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageButton homeBtn = findViewById(R.id.home);
        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(budget_screen.this, budget_screen.class);
            startActivity(intent);
        });

        ImageButton accountBtn = findViewById(R.id.account);
        accountBtn.setOnClickListener(v -> {
            Intent intent = new Intent(budget_screen.this, budget_screen.class);
            startActivity(intent);
        });
    }
}
