package com.example.cardviewtest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class alarm extends AppCompatActivity {

    private TimePicker alarmTimePicker;
    private Button setAlarmButton;
    private Button cancelAlarmButton;
    private static final String TAG = "AlarmActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // 알람 권한 확인
        checkAndRequestAlarmPermission();

        // UI 요소 초기화
        alarmTimePicker = findViewById(R.id.alarmTimePicker);
        setAlarmButton = findViewById(R.id.setAlarmButton);
        cancelAlarmButton = findViewById(R.id.cancelAlarmButton);

        // 알람 설정 버튼 클릭 리스너
        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarm();
            }
        });

        // 알람 취소 버튼 클릭 리스너
        cancelAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });
    }

    // 알람 설정 메서드
    private void setAlarm() {
        // 선택된 시간 가져오기
        int hour = alarmTimePicker.getCurrentHour();
        int minute = alarmTimePicker.getCurrentMinute();
        Log.d(TAG, "알람 시간 설정됨: " + hour + ":" + minute);

        // 알람 시간 설정
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // 현재 시간보다 이전인 경우 다음 날로 설정
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Log.d(TAG, "현재 시간보다 이전이므로 다음 날로 설정됨");
        }

        // 알람 매니저 설정
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        // FLAG_IMMUTABLE 추가
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // 알람 설정
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.d(TAG, "알람 설정됨: " + calendar.getTimeInMillis());
            Toast.makeText(this, "알람이 설정되었습니다: " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "알람 매니저가 null입니다.");
        }
    }

    // 알람 취소 메서드
    private void cancelAlarm() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // 알람 취소
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(this, "알람이 취소되었습니다.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "알람이 취소되었습니다.");
        } else {
            Log.e(TAG, "알람 매니저가 null입니다.");
        }
    }

    // 알람 권한 확인 메서드
    private void checkAndRequestAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!Settings.canDrawOverlays(this)) {
                // 알람 권한이 없으면 권한 요청
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
