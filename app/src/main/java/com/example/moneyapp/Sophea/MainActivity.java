package com.example.moneyapp.Sophea;

import android.content.Intent;
import android.os.Bundle;
//MainActivity

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.SessionManager;
import com.example.moneyapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SessionManager.isLoggedIn(this)) {
            startActivity(new Intent(MainActivity.this, after_sign_in.class));
            finish();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignIn.class);
            startActivity(intent);
            finish();
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        routeIfLoggedIn();
    }

    private boolean routeIfLoggedIn() {
        if (SessionManager.isLoggedIn(this)) {
            startActivity(new Intent(MainActivity.this, after_sign_in.class));
            finish();
            return true;
        }
        return false;
    }
}