package com.example.cardviewtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.util.Log;
import android.widget.TextView;
import android.app.AlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class login extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private AppCompatButton loginButton;
    private TextView signUpText;

    private static final String TAG = "LoginActivity"; // 로그 태그 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // UI 요소 연결
        usernameEditText = findViewById(R.id.id);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signUpText = findViewById(R.id.signUpText);

        // 회원가입 화면으로 이동
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, sign_up.class); // 회원가입 액티비티로 이동
                startActivity(intent);
            }
        });

        // 로그인 버튼 클릭 시 로그인 처리
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // 아이디와 비밀번호가 비어있는지 확인
                if (id.isEmpty() || password.isEmpty()) {
                    showAlertDialog("경고", "아이디와 비밀번호를 입력해주세요.");
                    return;
                }

                // LoginRequest 객체 생성
                LoginRequest loginRequest = new LoginRequest(id, password);

                // Retrofit을 통한 로그인 API 호출
                ApiService apiService = RetrofitClient.getApiService();
                Call<LoginResponse> call = apiService.login(loginRequest);  // 로그인 요청 (LoginResponse 객체 응답)

                // 비동기 방식으로 로그인 요청
                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful()) {  // 서버에서 응답이 정상적일 때
                            LoginResponse loginResponse = response.body(); // 응답 메시지 (LoginResponse 객체)

                            if (loginResponse != null) {
                                // 서버 응답을 로그에 출력
                                Log.d(TAG, "서버 응답: " + loginResponse.getMessage());

                                // 상태가 "success"이면 로그인 성공
                                if (loginResponse.getStatus().equals("success")) {
                                    showAlertDialog("성공", "로그인 성공");

                                    // 일정 시간 후에 MainActivity로 이동
                                    loginButton.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(login.this, MainActivity.class);  // 로그인 후 이동할 화면
                                            startActivity(intent);
                                            finish();  // 로그인 후 이전 화면으로 돌아가지 않도록 종료
                                        }
                                    }, 1000); // 1초 지연 후 이동
                                } else {
                                    // 로그인 실패 시
                                    showAlertDialog("실패", "로그인 실패: " + loginResponse.getMessage());
                                }
                            } else {
                                // 응답 객체가 null인 경우 로그에 출력
                                Log.e(TAG, "응답 메시지가 null입니다.");
                            }
                        } else {
                            showAlertDialog("오류", "로그인 실패. 정보가 일치하지 않습니다.");
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        // 네트워크 오류 처리
                        Log.e(TAG, "네트워크 오류 발생: " + t.getMessage(), t); // 오류 메시지 로그 출력
                        showAlertDialog("네트워크 오류", "네트워크 오류가 발생했습니다.");
                    }
                });
            }
        });
    }

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("확인", null)
                .show();
    }
}
