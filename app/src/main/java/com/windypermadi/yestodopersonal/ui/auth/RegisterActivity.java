package com.windypermadi.yestodopersonal.ui.auth;

import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_EMAIL;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_FULLNAME;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_IMAGE;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_PASSWORD;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_REGISTER;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_TELP;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_TOKEN;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.windypermadi.yestodopersonal.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {
        CustomLoading customLoading = CustomLoading.getInstance();
        CekKoneksi koneksi = new CekKoneksi();
    @BindView(R.id.nama_perusahaan_txt_edit)
    protected TextInputEditText nama_perusahaan_txt_edit;
    @BindView(R.id.et_email)
    protected TextInputEditText et_email;
    @BindView(R.id.et_telp)
    protected TextInputEditText et_telp;
    @BindView(R.id.et_katasandi)
    protected TextInputEditText et_katasandi;
    @BindView(R.id.et_katasandi2)
    protected TextInputEditText et_katasandi2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        ButtonAction();
    }

    private void ButtonAction() {
        findViewById(R.id.register_btn).setOnClickListener(view -> {
            String nama = nama_perusahaan_txt_edit.getText().toString().trim();
            String email = et_email.getText().toString().trim();
            String telp = et_telp.getText().toString().trim();
            String sandi = et_katasandi.getText().toString().trim();
            String sandi2 = et_katasandi2.getText().toString().trim();

            if (nama.length() == 0) {
                nama_perusahaan_txt_edit.setError("Nama tidak boleh kosong");
            } else if (email.length() == 0) {
                et_email.setError("Email tidak boleh kosong");
            } else if (telp.length() == 0) {
                et_telp.setError("Nomor Telepon tidak boleh kosong");
            } else if (sandi.length() == 0) {
                et_katasandi.setError("Sandi tidak boleh kosong");
            } else if (sandi2.length() == 0) {
                et_katasandi2.setError("Konfirmasi Sandi tidak boleh kosong");
            } else {
                if (koneksi.isConnected(RegisterActivity.this)) {
                    ProsesRegister(nama, email, telp, sandi);
                } else {
                    Utils.noInternet(RegisterActivity.this);
                }
            }
        });
    }

    private void ProsesRegister(String fullname, String email, String no_telp, String password) {
        customLoading.showProgress(RegisterActivity.this, false);
        AndroidNetworking.post(Connection.url + PARAM_REGISTER)
                .addHeaders(PARAM_TOKEN, Connection.token)
                .addBodyParameter(PARAM_FULLNAME, fullname)
                .addBodyParameter(PARAM_EMAIL, email)
                .addBodyParameter(PARAM_TELP, no_telp)
                .addBodyParameter(PARAM_PASSWORD, password)
                .addBodyParameter(PARAM_IMAGE, "")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        customLoading.hideProgress();
                        successDialog(RegisterActivity.this, "Yay, kamu " +
                                "berhasil mendaftar akun baru di yestodo. Selangkah lagi kamu " +
                                "akan menjadi orang sukses lho.");
                    }

                    @Override
                    public void onError(ANError error) {
                        customLoading.hideProgress();
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                Utils.errorDialog(RegisterActivity.this, body.optString("message"));
                                //  Utils.errorDialog(RegisterActivity.this, "Oh tidak, kamu gagal nih untuk mendaftar akun di yestodo. Ulangi lagi ya caranya sama kok.");
                            } catch (JSONException ignored) {

                            }

                        } else {
                            JSONObject body = null;
                            try {
                                body = new JSONObject(error.getErrorBody());
                                Utils.errorDialog(RegisterActivity.this, body.optString("message"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            Utils.errorDialog(RegisterActivity.this, "Wadawww, kamu gagal nih " +
//                                    "untuk mendaftar akun di yestodo. Jaringan kamu sedang terganggu nih ulangi lagi ya. Tetep semangat.");
                        }

                    }
                });
    }

    public void successDialog(final Context context, final String alertText){
        final View inflater = LayoutInflater.from(context).inflate(R.layout.custom_success_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(inflater);
        builder.setCancelable(false);
        final TextView ket = inflater.findViewById(R.id.keterangan);
        ket.setText(alertText);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparan);
        inflater.findViewById(R.id.ok).setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
            alertDialog.dismiss();
        });
        inflater.findViewById(R.id.back).setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
}