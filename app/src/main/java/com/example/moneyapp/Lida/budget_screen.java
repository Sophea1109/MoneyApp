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
import com.example.moneyapp.databinding.BudgetScreenBinding;

import java.util.Calendar;

public class budget_screen extends AppCompatActivity {

    private BudgetScreenBinding binding;
    private EditText dateBudget;
    private EditText amtBudget;
    private EditText detailsBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = BudgetScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageButton budgetBtn = findViewById(R.id.budgetBtn);
        budgetBtn.setOnClickListener(v -> {
            Intent intent = new Intent(budget_screen.this, account_icon.class);
            startActivity(intent);
        });

        dateBudget = findViewById(R.id.dateBudget);
        amtBudget = findViewById(R.id.amtBudget);
        detailsBudget = findViewById(R.id.detailsBudget);

        // Date field opens a DatePickerDialog instead of free text
        dateBudget.setFocusable(false);
        dateBudget.setClickable(true);
        dateBudget.setOnClickListener(v -> showDatePicker(dateBudget));

        // Amount field numeric only (decimal allowed)
        amtBudget.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        Button createButton = findViewById(R.id.btnCreateBudget);
        createButton.setOnClickListener(v -> createBudgetEntry());
    }

    @Override
    protected void onResume() {
        super.onResume();
        String saveDate2 = UserDataManager.getPrefs(this).getString("budget_date",    "");
        String saveBudget = UserDataManager.getPrefs(this).getString("budget_value",   "");
        String saveDetails2 = UserDataManager.getPrefs(this).getString("budget_details", "");

        dateBudget.setText(saveDate2);
        amtBudget.setText(saveBudget);
        detailsBudget.setText(saveDetails2);
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

    private void createBudgetEntry() {
        String date = dateBudget.getText().toString().trim();
        String budget = amtBudget.getText().toString().trim();
        String details = detailsBudget.getText().toString().trim();

        if (date.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (budget.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            double val = Double.parseDouble(budget);
            if (val <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Amount must be a positive number", Toast.LENGTH_SHORT).show();
            return;
        }

        HistoryRepository.appendHistoryEntry(this, "budget", date, budget, details);
        int userId = SessionManager.getCurrentUserId(this);
        new DatabaseHelper(this).insertFinancialEntry(
                "budget",
                userId,
                SessionManager.getCurrentUser(this),
                date, budget, details
        );
        dateBudget.setText("");
        amtBudget.setText("");
        detailsBudget.setText("");
        UserDataManager.getPrefs(this)
                .edit()
                .putString("budget_date", "")
                .putString("budget_value", "")
                .putString("budget_details", "")
                .apply();

        Toast.makeText(this, "Created successfully", Toast.LENGTH_SHORT).show();
    }
}