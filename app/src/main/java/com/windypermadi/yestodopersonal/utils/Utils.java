package com.windypermadi.yestodopersonal.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.windypermadi.yestodopersonal.R;

public class Utils {
    public static void emptyDialog(final Context context, final String alertText){
        final View inflater = LayoutInflater.from(context).inflate(R.layout.custom_empty_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(inflater);
        builder.setCancelable(false);
        final TextView ket  = inflater.findViewById(R.id.keterangan);
        ket.setText(alertText);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparan);
        inflater.findViewById(R.id.ok).setOnClickListener(v -> alertDialog.dismiss());
        inflater.findViewById(R.id.back).setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
    public static void errorDialog(final Context context, final String alertText){
        final View inflater = LayoutInflater.from(context).inflate(R.layout.custom_error_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(inflater);
        builder.setCancelable(false);
        final TextView ket  = inflater.findViewById(R.id.keterangan);
        ket.setText(alertText);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparan);
        inflater.findViewById(R.id.ok).setOnClickListener(v -> alertDialog.dismiss());
        inflater.findViewById(R.id.back).setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
    public static void successDialog(final Context context, final String alertText){
        final View inflater = LayoutInflater.from(context).inflate(R.layout.custom_success_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(inflater);
        builder.setCancelable(false);
        final TextView ket = inflater.findViewById(R.id.keterangan);
        ket.setText(alertText);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparan);
        inflater.findViewById(R.id.ok).setOnClickListener(v -> alertDialog.dismiss());
        inflater.findViewById(R.id.back).setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
    public static void noInternet(final Context context){
        final View inflater = LayoutInflater.from(context).inflate(R.layout.custom_no_internet_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(inflater);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparan);
        inflater.findViewById(R.id.ok).setOnClickListener(v -> alertDialog.dismiss());
        inflater.findViewById(R.id.back).setOnClickListener(v -> alertDialog.dismiss());
        inflater.findViewById(R.id.rv_back).setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
    public static void pengembanganDialog(final Context context, final String alertText){
        final View inflater = LayoutInflater.from(context).inflate(R.layout.custom_pengembangan_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(inflater);
        builder.setCancelable(false);
        final TextView ket = inflater.findViewById(R.id.keterangan);
        ket.setText(alertText);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparan);
        inflater.findViewById(R.id.ok).setOnClickListener(v -> alertDialog.dismiss());
        inflater.findViewById(R.id.back).setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
//    public static void successDialogIntent(final Context context, final Context contextIntent, final String alertText){
//        final View inflater = LayoutInflater.from(context).inflate(R.layout.custom_success_dialog, null);
//        final AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(inflater);
//        builder.setCancelable(false);
//        final TextView ket = inflater.findViewById(R.id.keterangan);
//        ket.setText(alertText);
//        final AlertDialog alertDialog = builder.create();
//        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparan);
//        inflater.findViewById(R.id.ok).setOnClickListener(v -> {
//            Intent i = new Intent(context, contextIntent);
//
//        });
//        inflater.findViewById(R.id.back).setOnClickListener(v -> alertDialog.dismiss());
//        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        alertDialog.show();
//    }
//    public static Intent getNameIntent(Context ctx, String firstName) {
//        Intent intent = new Intent(ctx, NameActivity.class);
//        intent.putExtra(FIRST_NAME, firstName);
//        return intent;
//    }
}
