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

public class transaction_screen extends AppCompatActivity{

    private TransactionScreenBinding binding;
    private EditText dateTransaction;
    private EditText amtTransaction;
    private EditText detailsTransaction;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = TransactionScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageButton TransactionBtn = findViewById(R.id.transactionBtn);
        TransactionBtn.setOnClickListener(v -> {
            Intent intent = new Intent(transaction_screen.this, account_icon.class);
            startActivity(intent);
        });

        dateTransaction = findViewById(R.id.dateTransaction);
        amtTransaction = findViewById(R.id.amtTransaction);
        detailsTransaction = findViewById(R.id.detailsTransaction);
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
    @Override
    protected void onPause(){
        super.onPause();

        String Date = dateTransaction.getText().toString().trim();
        String Transaction = amtTransaction.getText().toString().trim();
        String Details = detailsTransaction.getText().toString().trim();

        HistoryRepository.appendHistoryEntry(this, "transaction", Date, Transaction, Details);

        UserDataManager.getPrefs(this)
                .edit()
                .putString("transaction_date", Date)
                .putString("transaction_value", Transaction)
                .putString("transaction_details", Details)
                .apply();
    }
}
