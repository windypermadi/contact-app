package com.windypermadi.yestodopersonal.ui.profil;

import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_FULLNAME;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_ID_USER;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_IMAGE;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_MESSAGE;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_TELP;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_TOKEN;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_USER;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.windypermadi.yestodopersonal.MainActivity;
import com.windypermadi.yestodopersonal.R;
import com.windypermadi.yestodopersonal.ui.auth.LoginActivity;
import com.windypermadi.yestodopersonal.ui.auth.RegisterActivity;
import com.windypermadi.yestodopersonal.utils.CekKoneksi;
import com.windypermadi.yestodopersonal.utils.Connection;
import com.windypermadi.yestodopersonal.utils.CustomLoading;
import com.windypermadi.yestodopersonal.utils.Prop;
import com.windypermadi.yestodopersonal.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UbahProfilActivity extends AppCompatActivity {
    CustomLoading customLoading = CustomLoading.getInstance();
    CekKoneksi koneksi = new CekKoneksi();

    @BindView(R.id.ly_edit)
    protected LinearLayout ly_edit;
    @BindView(R.id.register_btn)
    protected AppCompatButton btnKirim;

    @BindView(R.id.et_nama)
    protected EditText et_nama;
    @BindView(R.id.et_telp)
    protected EditText et_telp;
    @BindView(R.id.et_pin)
    protected EditText et_pin;
    @BindView(R.id.et_email)
    protected EditText et_email;
    @BindView(R.id.img_upload)
    protected CircleImageView img_upload;
    @BindView(R.id.text_edit_foto_edit)
    protected AppCompatTextView text_edit_foto_edit;
    @BindView(R.id.text_nama)
    protected TextView text_nama;

    private String fullname, email, image, notelp, pin;

    //upload
    Uri FilePath;
    String gambar = "kosong";
    Intent intent;
    Bitmap bitmap;
    public final int REQUEST_CAMERA = 0;
    public final int SELECT_FILE = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onResume() {
        super.onResume();
        customLoading.showProgress(UbahProfilActivity.this, false);
        getProfil();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_profil);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ActionButton();
        text_nama.setText("Ubah Profil");
        findViewById(R.id.back).setOnClickListener(view -> finish());
    }

    private void ActionButton() {
        btnKirim.setOnClickListener(view -> {
            if (koneksi.isConnected( UbahProfilActivity.this)) {
                String nama = et_nama.getText().toString().trim();
                String notelp = et_telp.getText().toString().trim();
                putProfil(nama, notelp);
            } else {
                Utils.noInternet(UbahProfilActivity.this);
            }
        });
        text_edit_foto_edit.setOnClickListener(view -> {
            if (checkPermission()) {
                selectImage();
            } else {
                requestPermission();
            }
        });
    }

    private void getProfil() {
        AndroidNetworking.get(Connection.url + PARAM_USER)
                .addHeaders(PARAM_TOKEN, Connection.token)
                .addQueryParameter(PARAM_ID_USER, MainActivity.id_user)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        customLoading.hideProgress();
                        fullname = response.optString(Prop.PARAM_FULLNAME);
                        email = response.optString(Prop.PARAM_EMAIL);
                        image = response.optString(Prop.PARAM_IMAGE);
                        notelp = response.optString(Prop.PARAM_TELP);
                        pin = response.optString(Prop.PARAM_PIN);

                        getTextEdit();
                    }

                    @Override
                    public void onError(ANError error) {
                        customLoading.hideProgress();
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                Utils.errorDialog(UbahProfilActivity.this, body.optString("message"));
                            } catch (JSONException ignored) {

                            }

                        } else {
                            JSONObject body = null;
                            try {
                                body = new JSONObject(error.getErrorBody());
                                Utils.errorDialog(UbahProfilActivity.this, body.optString("message"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
    }

    private void getTextEdit() {
        et_nama.setText(fullname);
        et_email.setText(email);
        et_pin.setText(pin);
        et_telp.setText(notelp);
        Glide.with(UbahProfilActivity.this)
                .load(Prop.PARAM_HTTP + image)
                .error(R.drawable.ic_image)
                .into(img_upload);
    }

    private void putProfil(String nama, String notelp) {
        customLoading.showProgress(UbahProfilActivity.this, false);
        AndroidNetworking.put(Connection.url + PARAM_USER)
                .addHeaders(PARAM_TOKEN, Connection.token)
                .addQueryParameter(PARAM_ID_USER, MainActivity.id_user)
                .addQueryParameter(PARAM_FULLNAME, nama)
                .addQueryParameter(PARAM_TELP, notelp)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        customLoading.hideProgress();
                        Utils.successDialog(UbahProfilActivity.this, response.optString("message"));
                    }

                    @Override
                    public void onError(ANError error) {
                        customLoading.hideProgress();
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                Utils.errorDialog(UbahProfilActivity.this, body.optString("message"));
                            } catch (JSONException ignored) {

                            }

                        } else {
                            JSONObject body = null;
                            try {
                                body = new JSONObject(error.getErrorBody());
                                Utils.errorDialog(UbahProfilActivity.this, body.optString("message"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
    }

    //upload foto
    private void selectImage() {
        img_upload.setImageResource(0);
        final CharSequence[] items = {"Ambil foto", "Pilih dari galeri",
                "Batal"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UbahProfilActivity.this);
        builder.setTitle("Upload bukti transaksi");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Ambil foto")) {
                //intent khusus untuk menangkap foto lewat kamera
                gambar = "isi";
                if (ContextCompat.checkSelfPermission(UbahProfilActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(UbahProfilActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }
            } else if (items[item].equals("Pilih dari galeri")) {
                gambar = "isi";
                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Pilih gambar"), SELECT_FILE);
            } else if (items[item].equals("Batal")) {
                gambar = "kosong";
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                try {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    img_upload.setImageBitmap(bitmap);
                    FilePath = getImageUri(UbahProfilActivity.this, bitmap);

                    if (gambar.equals("isi")) {
                        File file = new File(getRealPathFromURI(FilePath));
                        uploadImage(file);
                    } else {
                        uploadImage(new File(""));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_FILE && data != null && data.getData() != null) {
                try {
                    // mengambil gambar dari Gallery
                    bitmap = MediaStore.Images.Media.getBitmap(UbahProfilActivity.this.getContentResolver(), data.getData());
                    img_upload.setImageBitmap(bitmap);
                    FilePath = getImageUri(UbahProfilActivity.this, bitmap);

                    if (gambar.equals("isi")) {
                        File file = new File(getRealPathFromURI(FilePath));
                        uploadImage(file);
                    } else {
                        uploadImage(new File(""));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTime(), null);

        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }


    public void uploadImage(File file) {
        if (file.length() == 0) {
            customLoading.showProgress(this, false);
            AndroidNetworking.upload(Connection.url + PARAM_USER)
                    .addHeaders(Prop.PARAM_TOKEN, Connection.token)
                    .addMultipartParameter(PARAM_ID_USER, MainActivity.id_user)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            customLoading.hideProgress();
                            successDialog(UbahProfilActivity.this, response.optString(PARAM_MESSAGE));
                        }

                        @Override
                        public void onError(ANError error) {
                            customLoading.hideProgress();
                            if (error.getErrorCode() == 400) {
                                try {
                                    JSONObject body = new JSONObject(error.getErrorBody());
                                    Utils.errorDialog(UbahProfilActivity.this, body.optString(PARAM_MESSAGE));
                                } catch (JSONException ignored) {
                                }
                            } else {
                                Utils.errorDialog(UbahProfilActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                            }
                        }
                    });
        } else {
            customLoading.showProgress(this, false);
            AndroidNetworking.upload(Connection.url + PARAM_USER)
                    .addHeaders(Prop.PARAM_TOKEN, Connection.token)
                    .addMultipartParameter(PARAM_ID_USER, MainActivity.id_user)
                    .addMultipartFile(PARAM_IMAGE, file)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            customLoading.hideProgress();
                            successDialog(UbahProfilActivity.this, response.optString(PARAM_MESSAGE));
                        }

                        @Override
                        public void onError(ANError error) {
                            customLoading.hideProgress();
                            if (error.getErrorCode() == 400) {
                                try {
                                    JSONObject body = new JSONObject(error.getErrorBody());
                                    Utils.errorDialog(UbahProfilActivity.this, body.optString(PARAM_MESSAGE));
                                } catch (JSONException ignored) {
                                }
                            } else {
                                Utils.errorDialog(UbahProfilActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                            }
                        }
                    });
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(UbahProfilActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(UbahProfilActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(UbahProfilActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(UbahProfilActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
        }
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
            finish();
            alertDialog.dismiss();
        });
        inflater.findViewById(R.id.back).setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
}