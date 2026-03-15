package com.example.moneyapp.Dom;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moneyapp.Database.DatabaseHelper;
import com.example.moneyapp.R;
import com.example.moneyapp.SessionManager;
import com.example.moneyapp.UserDataManager;

public class EditProfileActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    private ImageView profileImagePreview;
    private String currentImageUri = "";

    private final ActivityResultLauncher<String[]> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    try {
                        getContentResolver().takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );
                    } catch (Exception ignored) {
                        // no-op
                    }
                    currentImageUri = uri.toString();
                    profileImagePreview.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        emailInput = findViewById(R.id.editProfileEmail);
        passwordInput = findViewById(R.id.editProfilePassword);
        profileImagePreview = findViewById(R.id.profileImagePreview);
        CheckBox showPasswordCheck = findViewById(R.id.checkShowPassword);
        Button chooseImageButton = findViewById(R.id.btnChooseProfileImage);
        Button saveChangesButton = findViewById(R.id.btnSaveProfileChanges);

        String currentEmail = SessionManager.getCurrentUser(this);
        emailInput.setText(currentEmail);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        currentImageUri = databaseHelper.getProfileImageUri(currentEmail);
        applyImagePreview(currentImageUri);

        showPasswordCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            passwordInput.setSelection(passwordInput.getText().length());
        });

        chooseImageButton.setOnClickListener(v -> pickImageLauncher.launch(new String[]{"image/*"}));

        saveChangesButton.setOnClickListener(v -> showConfirmationDialog());
    }

    private void showConfirmationDialog() {
        String oldEmail = SessionManager.getCurrentUser(this);
        String newEmail = emailInput.getText().toString().trim();
        String newPassword = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(newEmail) || TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm changes")
                .setMessage("Are you sure about the changes?")
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Yes", (dialog, which) -> saveProfile(oldEmail, newEmail, newPassword))
                .show();
    }

    private void saveProfile(String oldEmail, String newEmail, String newPassword) {
        DatabaseHelper helper = new DatabaseHelper(this);
        boolean success = helper.updateUserProfile(oldEmail, newEmail, newPassword, currentImageUri);

        if (!success) {
            Toast.makeText(this, "Unable to update profile (email may already exist)", Toast.LENGTH_LONG).show();
            return;
        }

        if (!oldEmail.equalsIgnoreCase(newEmail)) {
            UserDataManager.migrateUserPrefs(this, oldEmail, newEmail);
            SessionManager.setCurrentUser(this, newEmail);
            SessionManager.setCurrentUserId(this, helper.getUserIdByEmail(newEmail));
        }

        SessionManager.setCurrentUserId(this, helper.getUserIdByEmail(newEmail));
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void applyImagePreview(String uriValue) {
        if (TextUtils.isEmpty(uriValue)) {
            profileImagePreview.setImageResource(R.drawable.moon);
            return;
        }

        try {
            profileImagePreview.setImageURI(Uri.parse(uriValue));
        } catch (Exception ignored) {
            profileImagePreview.setImageResource(R.drawable.moon);
        }
    }
}