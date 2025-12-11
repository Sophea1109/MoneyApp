package com.example.moneyapp.Sophea;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.Database.DatabaseHelper;
import com.example.moneyapp.R;
import com.example.moneyapp.databinding.SignInBinding;

public class SignIn extends AppCompatActivity {

    private SignInBinding binding;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = SignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize database first
        databaseHelper = new DatabaseHelper(this);

        // ------------------------------
        // LOGIN BUTTON (btnForgotPassword)
        // ------------------------------
        binding.btnForgotPassword.setOnClickListener(v -> {
            String email = binding.signinEmail.getText().toString().trim();
            String password = binding.signinPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignIn.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            Boolean validLogin = databaseHelper.checkEmailPassword(email, password);

            if (validLogin) {
                Toast.makeText(SignIn.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignIn.this, after_sign_in.class));
                finish();
            } else {
                Toast.makeText(SignIn.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });

        // ------------------------------
        // CREATE ACCOUNT BUTTON
        // ------------------------------
        binding.btnCreateAccount.setOnClickListener(v -> {
            String email = binding.signinEmail.getText().toString().trim();
            String password = binding.signinPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignIn.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            Boolean exists = databaseHelper.checkEmail(email);
            if (exists) {
                Toast.makeText(SignIn.this, "Email already exists!", Toast.LENGTH_SHORT).show();
                return;
            }

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