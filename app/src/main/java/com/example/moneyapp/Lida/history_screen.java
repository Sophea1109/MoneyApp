package com.example.moneyapp.Lida;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.HistoryRepository;
import com.example.moneyapp.R;

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
        if (text == null || text.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    private class HistoryAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return entries.size();
        }

        @Override
        public Object getItem(int position) {
            return entries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(history_screen.this).inflate(R.layout.item_history_line, parent, false);
            }

            HistoryRepository.HistoryEntry entry = entries.get(position);

            TextView text = view.findViewById(R.id.historyLineText);
            text.setText("Date: " + entry.date + "\nAmount: $" + entry.amount + "\nDetails: " + entry.details);

            ImageButton deleteButton = view.findViewById(R.id.btnDeleteHistory);
            deleteButton.setOnClickListener(v -> {
                HistoryRepository.deleteHistoryEntry(history_screen.this, type, position);
                reloadHistory();
            });

            return view;
        }
    }
}
