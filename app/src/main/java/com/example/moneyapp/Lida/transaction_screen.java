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
import com.example.moneyapp.databinding.TransactionScreenBinding;

import java.util.Calendar;

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

        dateTransaction    = findViewById(R.id.dateTransaction);
        amtTransaction     = findViewById(R.id.amtTransaction);
        detailsTransaction = findViewById(R.id.detailsTransaction);
        dateTransaction.setFocusable(false);
        dateTransaction.setClickable(true);
        dateTransaction.setOnClickListener(v -> showDatePicker(dateTransaction));
        amtTransaction.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        Button createButton = findViewById(R.id.btnCreateTransaction);
        createButton.setOnClickListener(v -> createTransactionEntry());
    }

    @Override
    protected void onResume() {
        super.onResume();
        String saveDate = UserDataManager.getPrefs(this).getString("transaction_date",    "");
        String saveTransaction = UserDataManager.getPrefs(this).getString("transaction_value",   "");
        String saveDetails = UserDataManager.getPrefs(this).getString("transaction_details", "");

        dateTransaction.setText(saveDate);
        amtTransaction.setText(saveTransaction);
        detailsTransaction.setText(saveDetails);
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

    private void createTransactionEntry() {
        String date = dateTransaction.getText().toString().trim();
        String transaction = amtTransaction.getText().toString().trim();
        String details = detailsTransaction.getText().toString().trim();

        if (date.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (transaction.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            double val = Double.parseDouble(transaction);
            if (val <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Amount must be a positive number", Toast.LENGTH_SHORT).show();
            return;
        }

        HistoryRepository.appendHistoryEntry(this, "transaction", date, transaction, details);
        int userId = SessionManager.getCurrentUserId(this);
        new DatabaseHelper(this).insertFinancialEntry(
                "spending",
                userId,
                SessionManager.getCurrentUser(this),
                date, transaction, details
        );
        dateTransaction.setText("");
        amtTransaction.setText("");
        detailsTransaction.setText("");
        UserDataManager.getPrefs(this)
                .edit()
                .putString("transaction_date", "")
                .putString("transaction_value", "")
                .putString("transaction_details", "")
                .apply();

        Toast.makeText(this, "Created successfully", Toast.LENGTH_SHORT).show();
    }
}