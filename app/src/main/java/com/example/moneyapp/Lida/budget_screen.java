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

public class budget_screen extends AppCompatActivity{

    private BudgetScreenBinding binding;
    private EditText dateBudget;
    private EditText amtBudget;
    private EditText detailsBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = BudgetScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageButton BudgetBtn = findViewById(R.id.budgetBtn);
        BudgetBtn.setOnClickListener(v -> {
            Intent intent = new Intent(budget_screen.this, account_icon.class);
            startActivity(intent);
        });

        dateBudget = findViewById(R.id.dateBudget);
        amtBudget = findViewById(R.id.amtBudget);
        detailsBudget = findViewById(R.id.detailsBudget);
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

    @Override
    protected void onPause(){
        super.onPause();

        String Date2 = dateBudget.getText().toString().trim();
        String Budget = amtBudget.getText().toString().trim();
        String Details2 = detailsBudget.getText().toString().trim();

        HistoryRepository.appendHistoryEntry(this, "budget", Date2, Budget, Details2);

        UserDataManager.getPrefs(this)
                .edit()
                .putString("budget_date", Date2)
                .putString("budget_value", Budget)
                .putString("budget_details", Details2)
                .apply();
    }
}
