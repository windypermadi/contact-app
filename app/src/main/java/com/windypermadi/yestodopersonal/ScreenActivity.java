package com.windypermadi.yestodopersonal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.windypermadi.yestodopersonal.ui.auth.LoginActivity;

public class ScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);

        findViewById(R.id.img1).setOnClickListener(view -> {
            Intent i = new Intent(ScreenActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        });
    }
}