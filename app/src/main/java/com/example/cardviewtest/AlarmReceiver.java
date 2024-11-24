package com.example.cardviewtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String SERVER_IP = "192.168.0.19"; // 라즈베리파이 IP 주소
    private static final int SERVER_PORT = 44445; // 전등 제어 포트
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // 전등 상태를 ON으로 설정
        SharedPreferences sharedPreferences = context.getSharedPreferences("LightControl", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("lightOn", true); // 전등 상태를 ON으로 설정
        editor.apply();

        // 전등 제어 메시지 전송 (비동기 작업)
        new SendLightOnMessageTask().execute();

        Log.d(TAG, "전등 ON 상태로 설정됨");
    }

    // 비동기 작업을 통해 라즈베리파이에 메시지 전송
    private static class SendLightOnMessageTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                 PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {
                out.println("1"); // 전등 ON 명령 전송
                Log.d(TAG, "라즈베리파이에 전등 ON 명령 전송됨");
            } catch (Exception e) {
                Log.e(TAG, "전송 오류 발생", e);
            }
            return null;
        }
    }
}
