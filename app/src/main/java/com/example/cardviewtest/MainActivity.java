package com.example.cardviewtest;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.view.View;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String SERVER_IP = "192.168.0.19"; // 라즈베리 파이 IP 주소
    private static final int SERVER_PORT = 44444; // 라즈베리 파이 포트 (온습도)
    private TextView temperatureTextView;
    private TextView humidityTextView;
    private TextView recommendationTextView; // TextView 선언
    private Switch lightSwitch;
    private Switch surveillanceSwitch;
    private CardView cctvCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 온습도 확인 버튼
        Button temperatureCheckButton = findViewById(R.id.temperatureCheckButton);  // Button으로 변경
        temperatureCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, temperature.class);  // Temperature 클래스 확인
                startActivity(intent);
            }
        });

        // 로그인 버튼
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            // 세션 초기화
            SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear(); // 세션 데이터 초기화
            editor.apply();

            // 로그인 화면으로 이동
            Intent intent = new Intent(MainActivity.this, login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 백 스택을 지움
            startActivity(intent);
            finish(); // 현재 화면 종료
        });

        // CCTV 카드 클릭 시 CCTVActivity로 이동
        cctvCard = findViewById(R.id.cctvCard);
        cctvCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CCTVActivity.class);
            startActivity(intent);
        });

        TextView noticeButton = findViewById(R.id.noticeButton);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
        String currentDate = sdf.format(new Date());
        noticeButton.setText(currentDate);

        // 온습도 정보 관련 TextView 설정
        temperatureTextView = findViewById(R.id.temperatureTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        recommendationTextView = findViewById(R.id.recommendationTextView);

        // 서버 연결 및 소켓 통신 테스트
        new SocketClientTask().execute(SERVER_IP, String.valueOf(SERVER_PORT), "3");

        ImageButton refreshButton = findViewById(R.id.refreshButton); // 새로고침 버튼을 찾아줍니다.
        refreshButton.setOnClickListener(v -> {
            // 새로고침 버튼을 눌렀을 때 전송할 메시지 설정
            String messageToSend = "3";  // 포트 "44444"로 "3"을 보냄

            // 서버로 메시지 전송 (SocketClientTask 사용)
            new SocketClientTask().execute(SERVER_IP, "44445", messageToSend);

            // AlertDialog를 사용하여 알림 메시지 표시
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("알림")
                    .setMessage("새로고침이 완료되었습니다.")
                    .setPositiveButton("확인", (dialog, which) -> dialog.dismiss())  // "확인" 버튼 클릭 시 다이얼로그 닫기
                    .setCancelable(false)  // 사용자가 다이얼로그 외부를 클릭해도 닫히지 않게 설정
                    .show();
        });

        // 전등 토글 기능
        lightSwitch = findViewById(R.id.lightSwitch);
        lightSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 전등 ON/OFF 상태를 서버로 전송 (포트 "44445" 사용)
            String messageToSend = isChecked ? "1" : "2"; // 전등 ON (1) / OFF (2)

            // "44445" 포트로 메시지 전송
            new SocketClientTask().execute(SERVER_IP, "44445", messageToSend);

            // AlertDialog 생성
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("알림")
                    .setMessage(isChecked ? "전등이 켜졌습니다." : "전등이 꺼졌습니다.")
                    .setPositiveButton("확인", (dialog, which) -> {
                        // 확인 버튼을 누르면 실행되는 동작 (필요 시 추가)
                        dialog.dismiss();
                    })
                    .setCancelable(true); // 외부 터치 시 닫힐 수 있도록 설정

            AlertDialog dialog = builder.create();
            dialog.show();

            // 전등이 켜졌을 때만 메인 화면을 유지하고, 전환 없이 상태만 업데이트
            if (isChecked) {
                // 전등이 켜지면 해당 상태만 반영하고, 화면은 그대로 유지
                lightSwitch.setChecked(true); // 전등 상태가 켜짐
            }
        });


        // 감시모드 토글 기능
        surveillanceSwitch = findViewById(R.id.securitySwitch);
        surveillanceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String messageToSend;
            String serverPort;

            if (isChecked) {
                // 감시모드 ON (4) -> 서버에 44444 포트로 메시지 전송
                messageToSend = "4";
                serverPort = "44445";
            } else {
                // 감시모드 OFF (0) -> 서버에 44443 포트로 메시지 전송
                messageToSend = "0";
                serverPort = "44443";
            }

            // 서버로 메시지 전송
            new SocketClientTask().execute(SERVER_IP, serverPort, messageToSend);

            // AlertDialog 생성
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("감시모드 알림")
                    .setMessage(isChecked ? "감시모드가 활성화되었습니다." : "감시모드가 비활성화되었습니다.")
                    .setPositiveButton("확인", (dialog, which) -> {
                        // 확인 버튼 클릭 시 동작 (필요한 경우 추가)
                        dialog.dismiss();
                    })
                    .setCancelable(true); // 외부 터치로 닫기 가능

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    public void onAlarmCardClick(View view) {
        // 알람 예약 화면으로 이동
        Intent intent = new Intent(MainActivity.this, alarm.class);
        startActivity(intent);
    }

    private class SocketClientTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String serverAddress = params[0]; // 서버 주소
            int serverPort = Integer.parseInt(params[1]); // 서버 포트
            String messageToSend = params[2]; // 서버에 보낼 메시지

            try (Socket socket = new Socket(serverAddress, serverPort);
                 PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // 서버에 메시지 전송
                out.println(messageToSend);

                // 서버로부터 메시지 지속적으로 수신
                StringBuilder responseBuilder = new StringBuilder();
                String responseLine;
                while ((responseLine = in.readLine()) != null) {
                    responseBuilder.append(responseLine).append("\n"); // 수신된 메시지 저장
                }

                return responseBuilder.toString().trim(); // 전체 메시지를 반환

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // 서버로부터 받은 결과를 TextView에 표시
            if (result != null) {
                String[] dataParts = result.split(" "); // 공백 기준으로 나누기
                if (dataParts.length >= 3) {
                    try {
                        // 온도와 습도 값 추출
                        String temperatureStr = dataParts[dataParts.length - 2].replace("도", "").trim();
                        String humidityStr = dataParts[dataParts.length - 1].replace("%", "").trim();

                        // 문자열에서 숫자만 추출
                        temperatureStr = temperatureStr.replaceAll("[^0-9.]", "").trim();

                        // 온도 및 습도 TextView에 값 설정
                        temperatureTextView.setText("온도: " + temperatureStr + "°");
                        humidityTextView.setText("습도: " + humidityStr + "%");

                        // 온도 값이 제대로 입력되었을 경우
                        if (!temperatureStr.isEmpty() && !humidityStr.isEmpty()) {
                            double temperature = Double.parseDouble(temperatureStr);
                            double humidity = Double.parseDouble(humidityStr);

                            // 온도에 따른 메시지 설정
                            if (temperature <= 18) {
                                recommendationTextView.setText("난방을 가동하세요!");
                            } else if (temperature >= 19 && temperature <= 25) {
                                recommendationTextView.setText("적정 실내 온도입니다!");
                            } else if (temperature >= 26) {
                                recommendationTextView.setText("에어컨을 가동하세요!");
                            }

                            // 습도에 따른 메시지 설정
                            if (humidity <= 30) {
                                recommendationTextView.append("\n가습기를 가동하세요!");
                            } else if (humidity > 30 && humidity <= 60) {
                                recommendationTextView.append("\n적정 습도입니다!");
                            } else if (humidity > 60) {
                                recommendationTextView.append("\n제습기를 가동하세요!");
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "온도 또는 습도 값이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (NumberFormatException e) {
                        // 예외 처리: 숫자 포맷 오류 시
                        Toast.makeText(MainActivity.this, "데이터 형식을 확인하세요.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace(); // 예외 출력
                    }
                } else {
                    Toast.makeText(MainActivity.this, "요청이 정상적으로 처리되었습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "서버와 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
