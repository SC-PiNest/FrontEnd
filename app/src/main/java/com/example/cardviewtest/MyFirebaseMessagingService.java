package com.example.cardviewtest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "default_channel"; // 알림 채널 ID
    private static final String CHANNEL_NAME = "Default Notifications"; // 알림 채널 이름

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "onMessageReceived called");

        // 데이터 페이로드 처리
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            // 데이터 페이로드에 따라 추가 작업 수행 가능
        }

        // 알림 페이로드 처리
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            Log.d(TAG, "Message Notification Title: " + title);
            Log.d(TAG, "Message Notification Body: " + body);

            // 알림 표시
            showNotification(title, body);
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);

        // Firebase 토큰을 파일에 저장
        saveTokenToFile(token);
    }

    /**
     * Firebase 등록 토큰을 파일에 저장
     *
     * @param token Firebase 등록 토큰
     */
    private void saveTokenToFile(String token) {
        FileOutputStream fos = null;
        try {
            // 파일을 내부 저장소에 생성
            File file = new File(getFilesDir(), "fcm_token.txt");
            fos = new FileOutputStream(file);
            fos.write(token.getBytes()); // 토큰을 파일에 씁니다.
            Log.d(TAG, "Token saved to file: " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Failed to save token to file", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 알림 표시
     *
     * @param title 알림 제목
     * @param body  알림 내용
     */
    private void showNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Android Oreo 이상에서는 알림 채널이 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("This is the default notification channel");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // 알림 생성
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // 아이콘 설정
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // 우선순위 설정
                .setAutoCancel(true); // 알림 클릭 시 제거

        // 알림 표시
        if (notificationManager != null) {
            notificationManager.notify(0, builder.build());
        }
    }
}