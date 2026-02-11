package com.example.moneyapp.Lida;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.HistoryRepository;
import com.example.moneyapp.R;

import java.util.List;

public class history_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_screen);

        String type = getIntent().getStringExtra("history_type");
        if (type == null) {
            type = "transaction";
        }

        TextView title = findViewById(R.id.historyTitle);
        title.setText(capitalize(type) + " History");

        ListView listView = findViewById(R.id.historyList);
        List<String> lines = HistoryRepository.getHistoryLines(this, type);
        if (lines.isEmpty()) {
            lines.add("No history yet.");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_history_line, R.id.historyLineText, lines);
        listView.setAdapter(adapter);

        ImageButton backBtn = findViewById(R.id.historyBack);
        backBtn.setOnClickListener(v -> finish());
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}
