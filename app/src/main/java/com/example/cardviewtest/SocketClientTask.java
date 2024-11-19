package com.example.cardviewtest;

import android.os.AsyncTask;
import java.io.OutputStream;
import java.net.Socket;

public class SocketClientTask extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... params) {
        String serverIp = params[0]; // 서버 IP
        int serverPort = Integer.parseInt(params[1]); // 서버 포트
        String message = params[2]; // 보낼 메시지

        Socket socket = null;
        OutputStream outputStream = null;

        try {
            // 서버에 연결
            socket = new Socket(serverIp, serverPort);
            outputStream = socket.getOutputStream();

            // 메시지를 서버로 전송
            outputStream.write(message.getBytes());
            outputStream.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

