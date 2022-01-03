package com.windypermadi.yestodopersonal.ui.grup.detailgrup;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.windypermadi.yestodopersonal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailGrupActivity extends AppCompatActivity {
    @BindView(R.id.text_nama)
    protected TextView text_nama;
    @BindView(R.id.back)
    protected ImageView back;
    @BindView(R.id.img_setting)
    protected ImageView img_setting;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_grup);
        ButterKnife.bind(this);
        text_nama.setText("Nama Grup");
        back.setOnClickListener(view -> finish());
        img_setting.setVisibility(View.VISIBLE);


    }
}