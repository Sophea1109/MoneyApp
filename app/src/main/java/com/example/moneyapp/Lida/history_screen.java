package com.example.moneyapp.Lida;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.Database.DatabaseHelper;
import com.example.moneyapp.HistoryRepository;
import com.example.moneyapp.R;
import com.example.moneyapp.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class history_screen extends AppCompatActivity {

    private final List<HistoryRepository.HistoryEntry> entries = new ArrayList<>();
    private HistoryAdapter adapter;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_screen);

        type = getIntent().getStringExtra("history_type");
        if (type == null) {
            type = "transaction";
        }

        TextView title = findViewById(R.id.historyTitle);
        title.setText(capitalize(type) + " History");

        ListView listView = findViewById(R.id.historyList);
        adapter = new HistoryAdapter();
        listView.setAdapter(adapter);

        ImageButton backBtn = findViewById(R.id.historyBack);
        backBtn.setOnClickListener(v -> finish());

        reloadHistory();
    }

    private void reloadHistory() {
        entries.clear();
        entries.addAll(HistoryRepository.getHistoryEntries(this, type));

        TextView empty = findViewById(R.id.emptyHistoryText);
        ListView listView = findViewById(R.id.historyList);

        if (entries.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    private String toFinancialTableName() {
        if ("transaction".equals(type)) return "spending";
        return type;
    }

    private class HistoryAdapter extends BaseAdapter {

        @Override
        public int getCount() { return entries.size(); }

        @Override
        public Object getItem(int position) { return entries.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(history_screen.this)
                        .inflate(R.layout.item_history_line, parent, false);
            }

            HistoryRepository.HistoryEntry entry = entries.get(position);

            TextView text = view.findViewById(R.id.historyLineText);
            text.setText("Date: " + entry.date + "\nAmount: $" + entry.amount +
                    "\nDetails: " + entry.details);
            text.setOnClickListener(v -> openEditDialog(position, entry));
            view.setOnClickListener(v -> openEditDialog(position, entry));

            ImageButton deleteButton = view.findViewById(R.id.btnDeleteHistory);
            deleteButton.setOnClickListener(v -> {
                boolean removed = HistoryRepository.deleteHistoryEntry(
                        history_screen.this, type, position);

                if (removed) {
                    // Use userId (FK) for all DB delete operations
                    int userId = SessionManager.getCurrentUserId(history_screen.this);
                    DatabaseHelper helper = new DatabaseHelper(history_screen.this);

                    helper.deleteFinancialEntryByDisplayIndex(
                            toFinancialTableName(),
                            userId,
                            position
                    );

                    // If history is now empty, clear the DB table for this user too
                    if (HistoryRepository.getHistoryEntries(history_screen.this, type).isEmpty()) {
                        helper.clearFinancialEntriesForUser(toFinancialTableName(), userId);
                    }

                    Toast.makeText(history_screen.this, "Deleted successfully",
                            Toast.LENGTH_SHORT).show();
                }
                reloadHistory();
            });

            return view;
        }
    }

    private void openEditDialog(int position, HistoryRepository.HistoryEntry entry) {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_edit_history, null);
        EditText dateInput = dialogView.findViewById(R.id.editHistoryDate);
        EditText amountInput = dialogView.findViewById(R.id.editHistoryAmount);
        EditText detailsInput = dialogView.findViewById(R.id.editHistoryDetails);

        dateInput.setText(entry.date);
        amountInput.setText(entry.amount);
        detailsInput.setText(entry.details);

        new AlertDialog.Builder(this)
                .setTitle("Edit History")
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newDate = dateInput.getText().toString().trim();
                    String newAmount = amountInput.getText().toString().trim();
                    String newDetails = detailsInput.getText().toString().trim();

                    boolean updated = HistoryRepository.updateHistoryEntry(
                            history_screen.this, type, position,
                            newDate, newAmount, newDetails);

                    if (updated) {
                        // Use userId (FK) for the DB update
                        int userId = SessionManager.getCurrentUserId(history_screen.this);
                        new DatabaseHelper(history_screen.this)
                                .updateFinancialEntryByDisplayIndex(
                                        toFinancialTableName(),
                                        userId,
                                        position,
                                        newDate, newAmount, newDetails
                                );
                        Toast.makeText(history_screen.this, "Updated successfully",
                                Toast.LENGTH_SHORT).show();
                    }

                    reloadHistory();
                })
                .show();
    }
}