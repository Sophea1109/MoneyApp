package com.example.moneyapp.Dom;
import android.content.Intent;
import android.os.Bundle;

import android.os.PersistableBundle;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.Dom.setting;
import com.example.moneyapp.R;
import com.example.moneyapp.databinding.AboutBinding;

public class about extends AppCompatActivity {

    private AboutBinding binding;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = AboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageButton backBtn = findViewById(R.id.backAbt);
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(about.this, setting.class);
            startActivity(intent);
        });
    }
}
