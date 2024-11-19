package com.example.cardviewtest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class sign_up extends AppCompatActivity {

    private EditText idField, passwordField, passwordConfirmField, serialNumberField;
    private Button signUpButton, checkDuplicateButton;
    private ApiService apiService;  // Retrofit 인터페이스
    private boolean isIdAvailable = false;  // 아이디 중복 확인 여부

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // UI 요소 찾기
        idField = findViewById(R.id.id);
        passwordField = findViewById(R.id.password);
        passwordConfirmField = findViewById(R.id.passwordConfirm);
        serialNumberField = findViewById(R.id.serialNumber);
        signUpButton = findViewById(R.id.signUpButton);
        checkDuplicateButton = findViewById(R.id.checkDuplicateButton);

        // Retrofit 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://3.37.225.63:8080")  // AWS EC2 서버의 기본 URL
                .addConverterFactory(GsonConverterFactory.create())  // GsonConverterFactory 추가
                .build();

        apiService = retrofit.create(ApiService.class);  // ApiService 객체 생성

        // 아이디 중복 확인 버튼 클릭 리스너
        checkDuplicateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = idField.getText().toString().trim();

                if (id.isEmpty()) {
                    showAlert("경고", "아이디를 입력해주세요.");
                    return;
                }

                // 서버로 아이디 중복 확인 요청
                Call<ResponseBody> call = apiService.checkDuplicate(id);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            // 아이디가 사용 가능
                            isIdAvailable = true;
                            showAlert("성공", "아이디 사용 가능");
                        } else {
                            // 아이디가 이미 존재
                            isIdAvailable = false;
                            showAlert("알림", "아이디가 이미 존재합니다.");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        showAlert("에러", "서버 연결 실패");
                    }
                });
            }
        });

        // 회원가입 버튼 클릭 리스너
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 중복 확인이 안 되었으면 경고 메시지 출력
                if (!isIdAvailable) {
                    showAlert("경고", "아이디 중복 확인을 먼저 해주세요.");
                    return;
                }

                String id = idField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();
                String passwordConfirm = passwordConfirmField.getText().toString().trim();
                String serialNumber = serialNumberField.getText().toString().trim();

                // 입력 값 오류 검사
                if (id.isEmpty() || password.isEmpty() || serialNumber.isEmpty()) {
                    showAlert("경고", "모든 칸을 입력해주세요.");
                    return;
                }

                if (!password.equals(passwordConfirm)) {
                    showAlert("경고", "비밀번호가 일치하지 않습니다.");
                    return;
                }

                // 서버로 전송할 회원가입 요청 객체 생성
                SignUpRequest request = new SignUpRequest(id, password, serialNumber);

                // 서버에 회원가입 요청
                Call<SignUpResponse> call = apiService.signUp(request);
                call.enqueue(new Callback<SignUpResponse>() {
                    @Override
                    public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            showAlert("성공", "회원가입 성공!");
                            // 로그인 화면으로 이동
                            Intent intent = new Intent(sign_up.this, login.class);
                            startActivity(intent);
                            finish();
                        } else {
                            showAlert("실패", "회원가입 실패");
                        }
                    }

                    @Override
                    public void onFailure(Call<SignUpResponse> call, Throwable t) {
                        showAlert("에러", "서버 연결 실패");
                    }
                });
            }
        });
    }

    // AlertDialog를 띄우는 메서드
    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(sign_up.this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("확인", (dialog, which) -> dialog.dismiss())
                .setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
