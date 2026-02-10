package com.example.moneyapp;

import android.app.Activity;
import android.content.Intent;
public class AppNavigator {
    private AppNavigator() {
        // Utility class
    }

    public static void navigateTo(Activity currentActivity, Class<?> targetActivity) {
        if (currentActivity.getClass().equals(targetActivity)) {
            return;
        }

        Intent intent = new Intent(currentActivity, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        currentActivity.startActivity(intent);
        currentActivity.finish();
    }
}
