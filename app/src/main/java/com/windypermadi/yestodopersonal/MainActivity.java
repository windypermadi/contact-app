package com.windypermadi.yestodopersonal;

import static com.windypermadi.yestodopersonal.utils.Prop.PARAM_USER;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.windypermadi.yestodopersonal.databinding.ActivityMainBinding;
import com.windypermadi.yestodopersonal.ui.auth.LoginActivity;
import com.windypermadi.yestodopersonal.utils.Connection;
import com.windypermadi.yestodopersonal.utils.CustomLoading;
import com.windypermadi.yestodopersonal.utils.Prop;
import com.windypermadi.yestodopersonal.utils.SessionManager;
import com.windypermadi.yestodopersonal.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    CustomLoading customLoading = CustomLoading.getInstance();
    private SessionManager SessionManager;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    public static String username, pin, id_user;
    private TextView text_nama, text_pin;
    private CircleImageView imageView;
    private String fullname, pin_home, image_home;
    private View navHeader;

    @Override
    protected void onResume() {
        super.onResume();
        getProfil();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager = new SessionManager(MainActivity.this);
        SessionManager.checkLogin();
        HashMap<String, String> user = SessionManager.getUserDetails();
        id_user = user.get(SessionManager.KEY_ID);
        username = user.get(SessionManager.KEY_USERNAME);
        pin = user.get(SessionManager.KEY_PIN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;

        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_profil, R.id.nav_grup)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        findViewById(R.id.nav_logout).setOnClickListener(view -> {
            logoutUser();
        });

        navHeader = navigationView.getHeaderView(0);
        text_nama = navHeader.findViewById(R.id.text_nama);
        text_pin = navHeader.findViewById(R.id.text_pin);
        imageView = navHeader.findViewById(R.id.imageView);

    }

    private void getProfil() {
        customLoading.showProgress(MainActivity.this, false);
        AndroidNetworking.get(Connection.url + PARAM_USER)
                .addHeaders(Prop.PARAM_TOKEN, Connection.token)
                .addQueryParameter(Prop.PARAM_ID_USER, id_user)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        customLoading.hideProgress();
                        fullname = response.optString(Prop.PARAM_FULLNAME);
                        image_home = response.optString(Prop.PARAM_IMAGE);
                        pin_home = response.optString(Prop.PARAM_PIN);

                        Glide.with(MainActivity.this)
                                .load(Prop.PARAM_HTTP + image_home)
                                .error(R.drawable.ic_image)
                                .into(imageView);
                        text_nama.setText(fullname);
                        text_pin.setText(pin_home);
                    }

                    @Override
                    public void onError(ANError error) {
                        customLoading.hideProgress();
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                Utils.errorDialog(MainActivity.this, body.optString("message"));
                            } catch (JSONException ignored) {

                            }

                        } else {
                            JSONObject body = null;
                            try {
                                body = new JSONObject(error.getErrorBody());
                                Utils.errorDialog(MainActivity.this, body.optString("message"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void logoutUser() {
        SessionManager.logoutUser();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finishAffinity();
    }
}