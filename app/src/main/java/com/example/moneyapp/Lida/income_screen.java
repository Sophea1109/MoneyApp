package com.example.moneyapp.Lida;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.Database.DatabaseHelper;
import com.example.moneyapp.HistoryRepository;
import com.example.moneyapp.R;
import com.example.moneyapp.SessionManager;
import com.example.moneyapp.Sophea.account_icon;
import com.example.moneyapp.UserDataManager;
import com.example.moneyapp.databinding.IncomeScreenBinding;

import java.util.Calendar;

public class income_screen extends AppCompatActivity {

    private IncomeScreenBinding binding;
    private EditText dateIncome;
    private EditText amtIncome;
    private EditText detailsIncome;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = IncomeScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageButton incomeBtn = findViewById(R.id.incomeBtn);
        incomeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(income_screen.this, account_icon.class);
            startActivity(intent);
        });

        dateIncome = findViewById(R.id.dateIncome);
        amtIncome = findViewById(R.id.amtIncome);
        detailsIncome = findViewById(R.id.detailsIncome);
        dateIncome.setFocusable(false);
        dateIncome.setClickable(true);
        dateIncome.setOnClickListener(v -> showDatePicker(dateIncome));
        amtIncome.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        Button createButton = findViewById(R.id.btnCreateIncome);
        createButton.setOnClickListener(v -> createIncomeEntry());
    }

    @Override
    protected void onResume() {
        super.onResume();
        String saveDate1 = UserDataManager.getPrefs(this).getString("income_date",    "");
        String saveIncome = UserDataManager.getPrefs(this).getString("income_value",   "");
        String saveDetails1 = UserDataManager.getPrefs(this).getString("income_details", "");

        dateIncome.setText(saveDate1);
        amtIncome.setText(saveIncome);
        detailsIncome.setText(saveDetails1);
    }

    private void showDatePicker(EditText target) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    target.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void createIncomeEntry() {
        String date = dateIncome.getText().toString().trim();
        String income = amtIncome.getText().toString().trim();
        String details = detailsIncome.getText().toString().trim();

        if (date.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (income.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            double val = Double.parseDouble(income);
            if (val <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Amount must be a positive number", Toast.LENGTH_SHORT).show();
            return;
        }

        HistoryRepository.appendHistoryEntry(this, "income", date, income, details);
        int userId = SessionManager.getCurrentUserId(this);
        new DatabaseHelper(this).insertFinancialEntry(
                "income",
                userId,
                SessionManager.getCurrentUser(this),
                date, income, details
        );
        dateIncome.setText("");
        amtIncome.setText("");
        detailsIncome.setText("");
        UserDataManager.getPrefs(this)
                .edit()
                .putString("income_date", "")
                .putString("income_value", "")
                .putString("income_details", "")
                .apply();

        Toast.makeText(this, "Created successfully", Toast.LENGTH_SHORT).show();
    }
}