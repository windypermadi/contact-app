package com.windypermadi.yestodopersonal.ui.grup;

import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_CARI;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_DATA;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_EMAIL;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_FK_USER;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_FULLNAME;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_GRUP;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_ID_FRIEND;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_ID_GRUP;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_ID_USER;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_IMAGE;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_KONTAK;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_NAME;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_PIN;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_TELP;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.windypermadi.yestodopersonal.MainActivity;
import com.windypermadi.yestodopersonal.R;
import com.windypermadi.yestodopersonal.databinding.FragmentGrupBinding;
import com.windypermadi.yestodopersonal.ui.grup.model.GrupModel;
import com.windypermadi.yestodopersonal.ui.grup.tambah.TambahGrup;
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

public class GrupFragment extends Fragment {
    CustomLoading customLoading = CustomLoading.getInstance();
    private GrupViewModel grupViewModel;
    private FragmentGrupBinding binding;

    CekKoneksi koneksi = new CekKoneksi();
    @BindView(R.id.ly00)
    protected LinearLayout ly00;
    @BindView(R.id.ly11)
    protected LinearLayout ly11;
    @BindView(R.id.ly22)
    protected LinearLayout ly22;
    @BindView(R.id.rv_grup)
    protected RecyclerView rv_grup;
    @BindView(R.id.text_more)
    protected AppCompatTextView text_more;
    @BindView(R.id.text_search)
    protected EditText text_search;
    List<GrupModel> GrupModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        grupViewModel = new ViewModelProvider(this).get(GrupViewModel.class);
        binding = FragmentGrupBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ButterKnife.bind(this, root);
        getActivity().setTitle("Grup Saya");
        root.findViewById(R.id.fab).setOnClickListener(view -> {
            startActivity(new Intent(getContext(), TambahGrup.class));
        });

        GrupModel = new ArrayList<>();
        LinearLayoutManager x = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
        rv_grup.setHasFixedSize(true);
        rv_grup.setLayoutManager(x);
        rv_grup.setNestedScrollingEnabled(true);

        ActionButton();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        ly00.setVisibility(View.VISIBLE);
        ly11.setVisibility(View.GONE);
        ly22.setVisibility(View.GONE);
        LoadGrup(text_search.getText().toString().trim());
    }

    private void ActionButton() {
        text_search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                customLoading.showProgress(getContext(), false);
                LoadGrup(text_search.getText().toString().trim());
                return true;
            }
            return false;
        });
    }

    private void LoadGrup(String cari) {
        GrupModel.clear();
        AndroidNetworking.get(Connection.url + PARAM_GRUP)
                .addQueryParameter(PARAM_ID_USER, MainActivity.id_user)
                .addQueryParameter(PARAM_CARI, cari)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.optJSONArray(PARAM_DATA);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject responses = jsonArray.getJSONObject(i);
                                GrupModel bk = new GrupModel(
                                        responses.getString(PARAM_ID_GRUP),
                                        responses.getString(PARAM_NAME),
                                        responses.getString(PARAM_IMAGE),
                                        responses.getString(PARAM_ID_GRUP));
                                GrupModel.add(bk);
                            }
                            GrupAdapter adapter = new GrupAdapter(getContext(), GrupModel);
                            rv_grup.setAdapter(adapter);

                            customLoading.hideProgress();
                            if (adapter.getItemCount() > 0) {
                                ly00.setVisibility(View.GONE);
                                ly11.setVisibility(View.VISIBLE);
                                ly22.setVisibility(View.GONE);
                                if (adapter.getItemCount() < 10) {
                                    text_more.setVisibility(View.GONE);
                                } else {
                                    text_more.setVisibility(View.VISIBLE);
                                }
                            } else {
                                ly00.setVisibility(View.GONE);
                                ly11.setVisibility(View.GONE);
                                ly22.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            ly11.setVisibility(View.GONE);
                            ly00.setVisibility(View.GONE);
                            ly22.setVisibility(View.GONE);
                            customLoading.hideProgress();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            customLoading.hideProgress();
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                ly00.setVisibility(View.GONE);
                                ly11.setVisibility(View.GONE);
                                ly22.setVisibility(View.GONE);
                                Utils.errorDialog(getContext(), "errprrrr");
                            } catch (JSONException ignored) {
                            }
                        } else {
                            customLoading.hideProgress();
                            Utils.errorDialog(getContext(), "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
//                            popupPeringatan("Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    public class GrupAdapter extends RecyclerView.Adapter<GrupAdapter.ProductViewHolder> {
        private final Context mCtx;
        private final List<GrupModel> GrupModel;

        GrupAdapter(Context mCtx, List<GrupModel> GrupModel) {
            this.mCtx = mCtx;
            this.GrupModel = GrupModel;
        }

        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.custom_layout_grup_b2c, null);
            return new ProductViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ProductViewHolder holder, int i) {
            final GrupModel kontak = GrupModel.get(i);
            holder.text_nama.setText(kontak.getName());
            holder.text_jumlah.setText(kontak.getId_group());
            Glide.with(mCtx)
                    .load(Prop.PARAM_HTTP + kontak.getImage())
                    .error(R.drawable.ic_image)
                    .into(holder.img);
            holder.cv.setOnClickListener(view -> {
                if (koneksi.isConnected(mCtx)) {
//                    popupDelete(kontak.getId_friend(), "Hapus " + kontak.getFullname() + "\nsebagai kontak anda?");
                } else {
                    Utils.noInternet(mCtx);
                }
            });
        }

        @Override
        public int getItemCount() {
            return GrupModel.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView text_nama, text_jumlah;
            CircleImageView img;
            CardView cv;

            ProductViewHolder(View itemView) {
                super(itemView);
                text_nama = itemView.findViewById(R.id.text_nama);
                text_jumlah = itemView.findViewById(R.id.text_jumlah);
                cv = itemView.findViewById(R.id.cv);
                img = itemView.findViewById(R.id.img);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}