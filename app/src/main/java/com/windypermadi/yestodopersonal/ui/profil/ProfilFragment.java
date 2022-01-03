package com.windypermadi.yestodopersonal.ui.profil;

import static com.windypermadi.yestodopersonal.utils.CustomLoading.customProgress;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_FULLNAME;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_ID_USER;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_IMAGE;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_TELP;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_TOKEN;
import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_USER;
import static com.windypermadi.yestodopersonal.utils.Prop.PERMISSION_REQUEST_CODE;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.windypermadi.yestodopersonal.MainActivity;
import com.windypermadi.yestodopersonal.R;
import com.windypermadi.yestodopersonal.databinding.FragmentProfilBinding;
import com.windypermadi.yestodopersonal.utils.CekKoneksi;
import com.windypermadi.yestodopersonal.utils.Connection;
import com.windypermadi.yestodopersonal.utils.CustomLoading;
import com.windypermadi.yestodopersonal.utils.Prop;
import com.windypermadi.yestodopersonal.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilFragment extends Fragment {
    CustomLoading customLoading = CustomLoading.getInstance();
    CekKoneksi koneksi = new CekKoneksi();

    @BindView(R.id.ly_noedit) protected LinearLayout ly_noedit;
    @BindView(R.id.ly_edit) protected LinearLayout ly_edit;
    @BindView(R.id.register_btn) protected AppCompatButton btnKirim;

    @BindView(R.id.et_nama_noedit) protected EditText etNamaNoEdit;
    @BindView(R.id.et_telp_noedit) protected EditText etTelpNoEdit;
    @BindView(R.id.et_pin_noedit) protected EditText etPinNoEdit;
    @BindView(R.id.et_email_noedit) protected EditText etEmailNoEdit;
    @BindView(R.id.img_upload) protected CircleImageView img_upload;

    @BindView(R.id.et_nama) protected EditText et_nama;
    @BindView(R.id.et_telp) protected EditText et_telp;
    @BindView(R.id.et_pin) protected EditText et_pin;
    @BindView(R.id.et_email) protected EditText et_email;
    @BindView(R.id.img_upload_edit) protected CircleImageView img_upload_edit;
    @BindView(R.id.text_edit_foto_edit) protected AppCompatTextView text_edit_foto_edit;

    private String fullname, email, image, notelp, pin;
    private ProfilViewModel profilViewModel;
    private FragmentProfilBinding binding;

    Intent intent;
    Uri fileUri;
    Bitmap bitmap, decoded;
    public final int REQUEST_CAMERA = 0;
    public final int SELECT_FILE = 1;

    int bitmap_size = 40; // image quality 1 - 100;
    int max_resolution_image = 800;


    @Override
    public void onResume() {
        super.onResume();
        getProfil();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profilViewModel = new ViewModelProvider(this).get(ProfilViewModel.class);
        binding = FragmentProfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ButterKnife.bind(this, root);
        ActionButton();

        return root;
    }

    private void ActionButton() {
        btnKirim.setOnClickListener(view -> {
            if (koneksi.isConnected(getContext())) {
                String nama = et_nama.getText().toString().trim();
                String notelp = et_telp.getText().toString().trim();
                putProfil(nama, notelp);
            } else {
                Utils.noInternet(getContext());
            }
        });
        text_edit_foto_edit.setOnClickListener(view -> {
            selectImage();
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profil, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        boolean showMessage = false;
        String message = "You click activity ";
        if (itemId == R.id.action_edit) {
            startActivity(new Intent(getContext(), UbahProfilActivity.class));
        }
        if (showMessage) {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setMessage(message);
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
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

                        getText();
                    }

                    @Override
                    public void onError(ANError error) {
                        customLoading.hideProgress();
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                Utils.errorDialog(getContext(), body.optString("message"));
                            } catch (JSONException ignored) {

                            }

                        } else {
                            JSONObject body = null;
                            try {
                                body = new JSONObject(error.getErrorBody());
                                Utils.errorDialog(getContext(), body.optString("message"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
    }

    private void getText() {
        etNamaNoEdit.setText(fullname);
        etEmailNoEdit.setText(email);
        etTelpNoEdit.setText(notelp);
        etPinNoEdit.setText(pin);
        Glide.with(getContext())
                .load(Prop.PARAM_HTTP + image)
                .error(R.drawable.ic_image)
                .into(img_upload);
    }

    private void getTextEdit() {
        et_nama.setText(fullname);
        et_email.setText(email);
        et_pin.setText(pin);
        et_telp.setText(notelp);
        Glide.with(getContext())
                .load(image)
                .error(R.drawable.ic_image)
                .into(img_upload_edit);
    }


    private void putProfil(String nama, String notelp) {
        customLoading.showProgress(getContext(), false);
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
                        Utils.successDialog(getContext(), response.optString("message"));
                        onResume();
                    }

                    @Override
                    public void onError(ANError error) {
                        customLoading.hideProgress();
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                Utils.errorDialog(getContext(), body.optString("message"));
                            } catch (JSONException ignored) {

                            }

                        } else {
                            JSONObject body = null;
                            try {
                                body = new JSONObject(error.getErrorBody());
                                Utils.errorDialog(getContext(), body.optString("message"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
    }

    private void selectImage() {
        img_upload_edit.setImageResource(0);
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, REQUEST_CAMERA);
            } else if (items[item].equals("Choose from Library")) {
                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_FILE);
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                try {
                    Log.e("CAMERA", fileUri.getPath());

                    bitmap = BitmapFactory.decodeFile(fileUri.getPath());
                    setToImageView(getResizedBitmap(bitmap, max_resolution_image));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_FILE && data != null && data.getData() != null) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                    setToImageView(getResizedBitmap(bitmap, max_resolution_image));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Untuk menampilkan bitmap pada ImageView
    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
        img_upload_edit.setImageBitmap(decoded);
    }

    // Untuk resize bitmap
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private static File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DeKa");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("Monitoring", "Oops! Failed create Monitoring directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    public void UploadGambar(File file) {
        customProgress.showProgress(getContext(), false);
        if (file.length() == 0) {
            AndroidNetworking.upload(Connection.url + PARAM_USER)
                    .addMultipartParameter(PARAM_ID_USER, MainActivity.id_user)
                    .setTag("uploadTest")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Utils.successDialog(getContext(), response.optString("message"));
                        }

                        @Override
                        public void onError(ANError error) {
                            customProgress.hideProgress();
                            if (error.getErrorCode() == 400) {
                                try {
                                    JSONObject body = new JSONObject(error.getErrorBody());
                                    Utils.errorDialog(getContext(), body.optString("message"));
                                } catch (JSONException ignored) {
                                }
                            } else {
                                Utils.errorDialog(getContext(), "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                            }
                        }
                    });
        } else {
            AndroidNetworking.upload(Connection.url + PARAM_USER)
                    .addMultipartFile(PARAM_IMAGE, file)
                    .addMultipartParameter(PARAM_ID_USER, MainActivity.id_user)
                    .setTag("uploadTest")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Utils.successDialog(getContext(), response.optString("message"));
                        }

                        @Override
                        public void onError(ANError error) {
                            customProgress.hideProgress();
                            if (error.getErrorCode() == 400) {
                                try {
                                    JSONObject body = new JSONObject(error.getErrorBody());
                                    Utils.errorDialog(getContext(), body.optString("message"));
                                } catch (JSONException ignored) {
                                }
                            } else {
                                Utils.errorDialog(getContext(), "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                            }
                        }
                    });
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getContext(), "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
        }
    }
}