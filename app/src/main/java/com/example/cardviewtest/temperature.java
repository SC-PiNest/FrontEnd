package com.example.cardviewtest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class temperature extends AppCompatActivity {

    private TextView temperatureText;
    private TextView humidityText;
    private TextView cityNameText;
    private Button getWeatherButton;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private String apiKey = "";  // OpenWeatherMap API 키

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        // UI 요소 초기화
        temperatureText = findViewById(R.id.antempText);
        humidityText = findViewById(R.id.anhumText);
        cityNameText = findViewById(R.id.cityNameText);  // 도시 이름을 표시할 TextView 추가
        getWeatherButton = findViewById(R.id.getWeatherButton);

        // 위치 서비스 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 버튼 클릭 리스너 설정
        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 위치 권한 확인 후 날씨 정보 가져오기
                if (ContextCompat.checkSelfPermission(temperature.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(temperature.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    getLocationAndWeather();
                }
            }
        });
    }

    // 위치 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndWeather();
            }
        }
    }

    // 위치 정보를 가져오고, 날씨 API 호출하여 데이터를 가져오는 메소드
    private void getLocationAndWeather() {
        // 위치 권한이 있는지 확인
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없으면 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // 위치를 가져오고 날씨 데이터 요청
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // 로그로 위도, 경도 출력
                            Log.d("Location", "Latitude: " + latitude + ", Longitude: " + longitude);

                            // Geocoder를 사용하여 도시 이름을 가져오기
                            getCityName(latitude, longitude);

                            // 날씨 데이터 가져오는 메소드 호출
                            fetchWeatherData(latitude, longitude);
                        } else {
                            Toast.makeText(temperature.this, "위치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Geocoder를 사용하여 도시 이름을 가져오는 메소드
    private void getCityName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String cityName = address.getLocality();  // 도시 이름 가져오기
                cityNameText.setText(cityName);  // TextView에 도시 이름 표시
            }
        } catch (IOException e) {
            e.printStackTrace();
            cityNameText.setText("도시 정보를 가져오지 못했습니다.");
        }
    }

    // OpenWeatherMap API 호출하여 날씨 데이터를 가져오는 메소드
    private void fetchWeatherData(double latitude, double longitude) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenWeatherMapAPI api = retrofit.create(OpenWeatherMapAPI.class);
        Call<WeatherResponse> call = api.getWeatherDataByCoords(latitude, longitude, apiKey, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    if (weatherResponse != null) {
                        float temp = weatherResponse.getMain().getTemp();
                        int humidity = weatherResponse.getMain().getHumidity();

                        // UI 업데이트
                        temperatureText.setText("온도: " + temp + "°C");
                        humidityText.setText("습도: " + humidity + "%");
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                temperatureText.setText("온도 데이터를 가져오지 못했습니다.");
                humidityText.setText("습도 데이터를 가져오지 못했습니다.");
            }
        });
    }
}
