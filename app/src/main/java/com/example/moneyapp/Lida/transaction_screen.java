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
import com.example.moneyapp.databinding.TransactionScreenBinding;
import com.example.moneyapp.Database.DatabaseHelper;
import com.example.moneyapp.SessionManager;
import android.widget.Button;
import android.widget.Toast;

public class transaction_screen extends AppCompatActivity {

    private TransactionScreenBinding binding;
    private EditText dateTransaction;
    private EditText amtTransaction;
    private EditText detailsTransaction;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = TransactionScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageButton transactionBtn = findViewById(R.id.transactionBtn);
        transactionBtn.setOnClickListener(v -> {
            Intent intent = new Intent(transaction_screen.this, account_icon.class);
            startActivity(intent);
        });

        dateTransaction = findViewById(R.id.dateTransaction);
        amtTransaction = findViewById(R.id.amtTransaction);
        detailsTransaction = findViewById(R.id.detailsTransaction);

        Button createButton = findViewById(R.id.btnCreateTransaction);
        createButton.setOnClickListener(v -> createTransactionEntry());
    }

    @Override
    protected void onResume() {
        super.onResume();

        String saveDate = UserDataManager.getPrefs(this)
                .getString("transaction_date", "");
        String saveTransaction = UserDataManager.getPrefs(this)
                .getString("transaction_value", "");
        String saveDetails = UserDataManager.getPrefs(this)
                .getString("transaction_details", "");

        dateTransaction.setText(saveDate);
        amtTransaction.setText(saveTransaction);
        detailsTransaction.setText(saveDetails);
    }

     private void createTransactionEntry() {
            String date = dateTransaction.getText().toString().trim();
            String transaction = amtTransaction.getText().toString().trim();
            String details = detailsTransaction.getText().toString().trim();

            HistoryRepository.appendHistoryEntry(this, "transaction", date, transaction, details);

            new DatabaseHelper(this).insertFinancialEntry(
                    "spending",
                    SessionManager.getCurrentUser(this),
                    date,
                    transaction,
                    details
            );

            UserDataManager.getPrefs(this)
                    .edit()
                    .putString("transaction_date", date)
                    .putString("transaction_value", transaction)
                    .putString("transaction_details", details)
                    .apply();
            Toast.makeText(this, "Create successfully", Toast.LENGTH_SHORT).show();
        }
    }
