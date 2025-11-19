package com.example.moneyapp.Sophea;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
//SignIn

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.R;
import com.example.moneyapp.databinding.SignInBinding;

public class SignIn extends AppCompatActivity{
    private SignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = SignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button forgotBtn = findViewById(R.id.btnForgotPassword);
        forgotBtn.setOnClickListener(v -> {
            Intent intent = new Intent(SignIn.this, SignIn.class);
            startActivity(intent);
        });

        Button createBtn = findViewById(R.id.btnCreateAccount);
        createBtn.setOnClickListener(v -> {
            Intent intent = new Intent(SignIn.this, after_sign_in.class);
            startActivity(intent);
        });
    }
}
