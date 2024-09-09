package android.myapplication.View;

import android.content.Intent;
import android.myapplication.R;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        TextView tvSuccess = findViewById(R.id.tvSuccess);
        TextView tvHome = findViewById(R.id.tvHome);

        Intent intent = getIntent();
        String message = intent.getStringExtra("message");

        tvSuccess.setText(message);
        tvHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, MainActivity.class);
            startActivity(homeIntent);
            finish();
        });
    }
}