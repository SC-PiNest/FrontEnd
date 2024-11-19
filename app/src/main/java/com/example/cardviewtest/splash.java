package com.example.cardviewtest;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Handler를 사용하여 3초 후에 login클래스로 전환
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(splash.this, login.class));
                finish(); // 현재 액티비티를 종료하여 스플래시 화면이 뒤로 가도록 함
            }
        }, 3000); // 3000ms 후에 실행
    }
}
