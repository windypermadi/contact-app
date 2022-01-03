package com.windypermadi.yestodopersonal.ui.grup.tambah;

import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_DATA;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_EMAIL;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_FK_USER;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_FULLNAME;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_ID_FRIEND;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_ID_USER;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_IMAGE;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_KONTAK;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_PIN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.windypermadi.yestodopersonal.MainActivity;
import com.windypermadi.yestodopersonal.R;
import com.windypermadi.yestodopersonal.ui.grup.tambah.model.KontakGrupModel;
import com.windypermadi.yestodopersonal.utils.CekKoneksi;
import com.windypermadi.yestodopersonal.utils.Connection;
import com.windypermadi.yestodopersonal.utils.CustomLoading;
import com.windypermadi.yestodopersonal.utils.Prop;
import com.windypermadi.yestodopersonal.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TambahGrup extends AppCompatActivity {
    CustomLoading customLoading = CustomLoading.getInstance();
    CekKoneksi koneksi = new CekKoneksi();
    @BindView(R.id.text_nama)
    protected TextView text_nama;
    @BindView(R.id.back)
    protected ImageView back;
    ArrayList<HashMap<String, String>> dataKontak = new ArrayList<>();
    String idkontak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_grup);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.bind(this);
        text_nama.setText("Tambah Grup");
        back.setOnClickListener(view -> finish());

        findViewById(R.id.text_pilih).setOnClickListener(view -> {
            loadKontak("");
        });
    }

    private void loadKontak(String cari) {
        dataKontak.clear();
        customLoading.showProgress(TambahGrup.this, false);
        AndroidNetworking.get(Connection.url + PARAM_KONTAK + "/all")
                .addQueryParameter(PARAM_ID_USER, MainActivity.id_user)
                .addQueryParameter("q", cari)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.optJSONArray(PARAM_DATA);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject responses = jsonArray.getJSONObject(i);
                                HashMap<String, String> map = new HashMap<>();

                                map.put(PARAM_ID_FRIEND, responses.optString(PARAM_ID_FRIEND));
                                map.put(PARAM_FULLNAME, responses.optString(PARAM_FULLNAME));
                                map.put(PARAM_IMAGE, responses.optString(PARAM_IMAGE));

                                dataKontak.add(map);
                            }
                            customLoading.hideProgress();
                            popup_kontak();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        customLoading.hideProgress();
                    }
                });
    }

    private void popup_kontak() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TambahGrup.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_list_kontak_popup, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = dialogBuilder.create();
        ListView lv_kontak = dialogView.findViewById(R.id.lv_kontak);
        TextView text_simpan = dialogView.findViewById(R.id.text_simpan);
        EditText text_search = dialogView.findViewById(R.id.text_search);
        text_search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                dataKontak.clear();
                loadKontak(text_search.getText().toString().trim());
                return true;
            }
            return false;
        });
        dialogView.findViewById(R.id.back).setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, dataKontak, R.layout.list_row_kontak_listview,
                new String[]{PARAM_ID_FRIEND, PARAM_FULLNAME, PARAM_IMAGE},
                new int[]{R.id.text_id, R.id.text_nama, R.id.ci_imageview});
        lv_kontak.setAdapter(simpleAdapter);
        lv_kontak.setOnItemClickListener((parent, view, position, id) -> {
            idkontak = ((TextView) view.findViewById(R.id.text_id)).getText().toString();
            String nama = ((TextView) view.findViewById(R.id.text_nama)).getText().toString();
            text_search.setText(nama);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    @OnClick(R.id.text_simpan)
    void submitButton(View view) {
        if (koneksi.isConnected(TambahGrup.this)) {
            prosesTambah();
        } else {
            Utils.noInternet(TambahGrup.this);
        }
    }

    private void prosesTambah() {
//        String jsonStr = {"theTeam":[{"teamId":"1","status":"pending"},{"teamId":"2","status":"member"},{"teamId":"3","status":"member"}]}";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id_user", MainActivity.id_user);
            jsonObject.put("name", "kopet");
            jsonObject.put("describe", "kopet");
            jsonObject.put("name", "oke");

//            JSONArray jsonArray = jsonObject.optJSONArray("members");
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject responses = jsonArray.getJSONObject(i);
//                String fk_user = responses.getString("fk_user");
//                jsonObject.put("members", fk_user);
//
//                Log.d("ddd", fk_user);
//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("ddd", String.valueOf(jsonObject));

        customLoading.showProgress(this, false);
        AndroidNetworking.post(Connection.url + Prop.PARAM_KONTAK + "/" + Prop.PARAM_ADD)
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        customLoading.hideProgress();
                        successDialog(TambahGrup.this, response.optString("message"));
                    }

                    @Override
                    public void onError(ANError error) {
                        customLoading.hideProgress();
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                String message = body.optString("message");
                                Utils.errorDialog(TambahGrup.this, message);
                            } catch (JSONException ignored) {
                            }
                        } else {
                            Utils.errorDialog(TambahGrup.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    public void successDialog(final Context context, final String alertText) {
        final View inflater = LayoutInflater.from(context).inflate(R.layout.custom_success_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(inflater);
        builder.setCancelable(false);
        final TextView ket = inflater.findViewById(R.id.keterangan);
        ket.setText(alertText);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparan);
        inflater.findViewById(R.id.ok).setOnClickListener(v -> {
            finish();
            alertDialog.dismiss();
        });
        inflater.findViewById(R.id.back).setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
}