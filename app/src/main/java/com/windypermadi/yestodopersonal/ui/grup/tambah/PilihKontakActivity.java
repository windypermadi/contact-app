package com.windypermadi.yestodopersonal.ui.grup.tambah;

import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_DATA;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_FK_USER;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_FULLNAME;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_ID_USER;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_IMAGE;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_KONTAK;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
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
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class PilihKontakActivity extends AppCompatActivity {

    @BindView(R.id.listView)
    protected ListView listView;
    @BindView(R.id.text_nama)
    protected TextView text_nama;
    @BindView(R.id.back)
    protected ImageView back;
    @BindView(R.id.text_search)
    protected EditText text_search;
    @BindView(R.id.ly00)
    protected LinearLayout ly00;
    @BindView(R.id.ly11)
    protected LinearLayout ly11;
    @BindView(R.id.ly22)
    protected LinearLayout ly22;
    @BindView(R.id.text_simpan)
    protected TextView text_simpan;
    TextView txt_menu;
    private List<KontakGrupModel> list;
    private KontakGrupModel model;
    private KontakGrupAdapter adapter;

    String dipilih;
    CustomLoading customLoading = CustomLoading.getInstance();
    CekKoneksi koneksi = new CekKoneksi();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_kontak);
        ButterKnife.bind(this);

        text_nama.setText("Pilih Kontak");
        back.setOnClickListener(view -> finish());

        text_simpan.setOnClickListener(view -> {
//            String str = "[";
//            String str2 = "]";
//            for (int i = 0; i < items.size(); i++) {
//                if (items.get(i).isChecked()) {
//                    str += "{" + i + "}," + "\n";
//                }
//            }
//            Toast.makeText(PilihKontakActivity.this,
//                    str+str2,
//                    Toast.LENGTH_LONG).show();
        });

        list = new ArrayList<>();

        customLoading.showProgress(PilihKontakActivity.this, false);
        ly11.setVisibility(View.GONE);
        ly00.setVisibility(View.VISIBLE);
        ly22.setVisibility(View.GONE);
        LoadKontak();
    }

    private void LoadKontak() {
        list.clear();
        AndroidNetworking.get(Connection.url + PARAM_KONTAK + "/all")
                .addQueryParameter(PARAM_ID_USER, MainActivity.id_user)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.optJSONArray(PARAM_DATA);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject responses = jsonArray.getJSONObject(i);
                                KontakGrupModel kontak = new KontakGrupModel();
                                kontak.setId_friend(responses.getString(PARAM_FK_USER));
                                kontak.setFullname(responses.getString(PARAM_FULLNAME));
                                kontak.setImage(responses.getString(PARAM_IMAGE));
                                list.add(kontak);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ly11.setVisibility(View.GONE);
                            ly00.setVisibility(View.GONE);
                            ly22.setVisibility(View.GONE);
                            customLoading.hideProgress();
                        }

                        customLoading.hideProgress();
                        ly11.setVisibility(View.VISIBLE);
                        ly00.setVisibility(View.GONE);
                        ly22.setVisibility(View.GONE);

                        adapter = new KontakGrupAdapter(PilihKontakActivity.this, list);
                        listView.setAdapter(adapter);
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                ly00.setVisibility(View.GONE);
                                ly11.setVisibility(View.GONE);
                                ly22.setVisibility(View.GONE);
                                Utils.errorDialog(getApplicationContext(), "errprrrr");
                            } catch (JSONException ignored) {
                            }
                        } else {
                            Utils.errorDialog(getApplicationContext(), "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    public class KontakGrupAdapter extends BaseAdapter {
        private Context context;
        private KontakGrupModel model;
        private List<KontakGrupModel> list;
        List<KontakGrupModel> list2;

        public KontakGrupAdapter(Context context, List<KontakGrupModel> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.list_row_kontak_listview, null, false);
            }
            model = list.get(i);
            TextView text_nama = view.findViewById(R.id.text_nama);
            CircleImageView ci_imageview = view.findViewById(R.id.ci_imageview);
            CheckBox checkBox = view.findViewById(R.id.checkbox);

            text_nama.setText(model.getFullname());
            Glide.with(context)
                    .load(Prop.PARAM_HTTP + model.getImage())
                    .error(R.drawable.ic_image)
                    .into(ci_imageview);

            return view;
        }
    }

    private void formSubmit(String hasil){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.form_submit, null);
        dialog.setView(dialogView);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Menu Yang Dipilih");
        dialog.setCancelable(true);

        txt_menu = (TextView) dialogView.findViewById(R.id.txt_menu);

        txt_menu.setText(hasil);

        dialog.setNeutralButton("CLOSE", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}