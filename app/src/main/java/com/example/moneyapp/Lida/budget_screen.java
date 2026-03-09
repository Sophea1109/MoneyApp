package com.example.moneyapp.Lida;
import android.content.Intent;
import android.os.Bundle;

import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.HistoryRepository;
import com.example.moneyapp.R;
import com.example.moneyapp.Sophea.account_icon;
import com.example.moneyapp.UserDataManager;
import com.example.moneyapp.databinding.BudgetScreenBinding;
import com.example.moneyapp.Database.DatabaseHelper;
import com.example.moneyapp.SessionManager;
import android.widget.Button;
import android.widget.Toast;

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

        Button createButton = findViewById(R.id.btnCreateBudget);
        createButton.setOnClickListener(v -> createBudgetEntry());
    }

    @Override
    protected void onResume() {
        super.onResume();

        String saveDate2 = UserDataManager.getPrefs(this)
                .getString("budget_date", "");
        String saveBudget = UserDataManager.getPrefs(this)
                .getString("budget_value", "");
        String saveDetails2 = UserDataManager.getPrefs(this)
                .getString("budget_details", "");

        dateBudget.setText(saveDate2);
        amtBudget.setText(saveBudget);
        detailsBudget.setText(saveDetails2);
    }
    private void createBudgetEntry() {
        String date = dateBudget.getText().toString().trim();
        String budget = amtBudget.getText().toString().trim();
        String details = detailsBudget.getText().toString().trim();

        HistoryRepository.appendHistoryEntry(this, "budget", date, budget, details);

        new DatabaseHelper(this).insertFinancialEntry(
                "budget",
                SessionManager.getCurrentUser(this),
                date,
                budget,
                details
        );

        UserDataManager.getPrefs(this)
                .edit()
                .putString("budget_date", date)
                .putString("budget_value", budget)
                .putString("budget_details", details)
                .apply();
        Toast.makeText(this, "Create successfully", Toast.LENGTH_SHORT).show();
    }
}
