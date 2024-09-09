package android.myapplication.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.myapplication.R;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private Button buttonGetStarted;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra trạng thái đăng nhập
        SharedPreferences sharedPreferences = getSharedPreferences("login_preferences", MODE_PRIVATE);
        boolean rememberMe = sharedPreferences.getBoolean("remember_me", false);

        if (rememberMe) {
            String email = sharedPreferences.getString("email", "");
            String password = sharedPreferences.getString("password", "");
            // Nếu đã lưu thông tin đăng nhập, chuyển đến MainActivity
            autoLogin(email, password);
        } else {
            // Nếu không, hiển thị màn hình splash với nút "Get Started"
            setContentView(R.layout.activity_splash);
            buttonGetStarted = findViewById(R.id.buttonGetStarted);
            buttonGetStarted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Thực hiện hành động khi người dùng nhấn vào nút
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    // Kết thúc SplashActivity để ngăn không cho quay lại
                    finish();
                }
            });
        }
    }

    private void autoLogin(String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // Đăng nhập thành công, chuyển đến MainActivity
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Đóng SplashActivity để ngăn không cho quay lại
                })
                .addOnFailureListener(e -> {
                    // Nếu đăng nhập thất bại, hiển thị màn hình splash với nút "Get Started"
                    setContentView(R.layout.activity_splash);
                    buttonGetStarted = findViewById(R.id.buttonGetStarted);
                    buttonGetStarted.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                });
    }
}
