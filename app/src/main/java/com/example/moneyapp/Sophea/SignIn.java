package com.example.moneyapp.Sophea;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
//SignIn

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.Database.DatabaseHelper;
import com.example.moneyapp.R;
import com.example.moneyapp.databinding.SignInBinding;

public class SignIn extends AppCompatActivity{
    private SignInBinding binding;
    DatabaseHelper databaseHelper;

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

        databaseHelper = new DatabaseHelper(this);

        // CREATE ACCOUNT BUTTON
        binding.btnCreateAccount.setOnClickListener(v -> {
            String email = binding.signinEmail.getText().toString().trim();
            String password = binding.signinPassword.getText().toString().trim();

            // Validation
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignIn.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if email already exists
            Boolean exists = databaseHelper.checkEmail(email);
            if (exists) {
                Toast.makeText(SignIn.this, "Email already exists!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert into database
            Boolean success = databaseHelper.insertData(email, password);

            if (success) {
                Toast.makeText(SignIn.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignIn.this, after_sign_in.class));
                finish();
            } else {
                Toast.makeText(SignIn.this, "Failed to create account", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

