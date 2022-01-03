package com.windypermadi.yestodopersonal.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.windypermadi.yestodopersonal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LupaSandiActivity extends AppCompatActivity {
    @BindView(R.id.text_nama)
    protected TextView text_nama;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_sandi);
        ButterKnife.bind(this);
        ButtonAction();
        text_nama.setText("Ubah Kata Sandi");
    }

    private void ButtonAction() {
        findViewById(R.id.back).setOnClickListener(view -> finish());
        findViewById(R.id.register_btn).setOnClickListener(view -> finish());
    }
}