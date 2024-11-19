package com.example.cardviewtest;

import android.app.Application;
import android.util.Log;
import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Firebase 초기화 시도
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this);
                Log.d("Firebase", "Firebase initialized successfully.");
            } else {
                Log.d("Firebase", "Firebase is already initialized.");
            }
        } catch (Exception e) {
            Log.e("Firebase", "Error initializing Firebase: " + e.getMessage());
        }
    }
}