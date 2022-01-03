package com.windypermadi.yestodopersonal.ui.auth;

import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_EMAIL;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_LOGIN;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_PASSWORD;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_TOKEN;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.textfield.TextInputEditText;
import com.windypermadi.yestodopersonal.MainActivity;
import com.windypermadi.yestodopersonal.R;
import com.windypermadi.yestodopersonal.utils.CekKoneksi;
import com.windypermadi.yestodopersonal.utils.Connection;
import com.windypermadi.yestodopersonal.utils.CustomLoading;
import com.windypermadi.yestodopersonal.utils.SessionManager;
import com.windypermadi.yestodopersonal.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    CustomLoading customLoading = CustomLoading.getInstance();
    CekKoneksi koneksi = new CekKoneksi();
    private SessionManager SessionManager;
    @BindView(R.id.lupa_katasandi_txt)
    protected AppCompatTextView lupa_katasandi_txt;
    @BindView(R.id.username_txt_edit)
    protected TextInputEditText username_txt_edit;
    @BindView(R.id.password_txt_edit)
    protected TextInputEditText password_txt_edit;

    @Override
    protected void onResume() {
        super.onResume();
        if (SessionManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SessionManager = new SessionManager(getApplicationContext());
        ButterKnife.bind(this);
        ButtonAction();
    }

    private void ButtonAction() {
        findViewById(R.id.daftar_txt).setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
        findViewById(R.id.masuk_btn).setOnClickListener(view -> {
            String email = username_txt_edit.getText().toString().trim();
            String pass = password_txt_edit.getText().toString().trim();

            if (email.length() == 0) {
                username_txt_edit.setError("Email tidak boleh kosong");
            } else if (pass.length() == 0) {
                password_txt_edit.setError("Password tidak boleh kosong");
            } else {
                if (koneksi.isConnected(LoginActivity.this)) {
                    ProsesLogin(email, pass);
                } else {
                    Utils.noInternet(LoginActivity.this);
                }
            }
        });
    }

    private void ProsesLogin(String email, String pass) {
        customLoading.showProgress(LoginActivity.this, false);
        AndroidNetworking.get(Connection.url + PARAM_LOGIN)
                .addQueryParameter(PARAM_EMAIL, email)
                .addQueryParameter(PARAM_PASSWORD, pass)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        customLoading.hideProgress();
                        SessionManager.createLoginSession(response.optString("id_user"), response.optString("fullname"),
                                response.optString("email"), response.optString("pin"));
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(ANError error) {
                        customLoading.hideProgress();
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                Utils.errorDialog(LoginActivity.this, body.optString("message"));
                            } catch (JSONException ignored) {

                            }

                        } else {
                            JSONObject body = null;
                            try {
                                body = new JSONObject(error.getErrorBody());
                                Utils.errorDialog(LoginActivity.this, body.optString("message"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
    }
}