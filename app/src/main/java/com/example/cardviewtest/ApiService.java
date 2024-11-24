package com.example.cardviewtest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    // POST 요청을 통해 서버로 회원가입 요청
    @POST("/user/register")
    Call<SignUpResponse> signUp(@Body SignUpRequest request);

    // POST 요청을 통해 서버로 로그인 요청
    @POST("/user/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // 로그인버튼 중복요청확인
    @GET("/user/check-id/{id}")
    Call<ResponseBody> checkDuplicate(@Path("id") String id);
}
