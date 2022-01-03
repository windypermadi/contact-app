package com.windypermadi.yestodopersonal.ui.grup.tambah.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.windypermadi.yestodopersonal.R;
import com.windypermadi.yestodopersonal.ui.grup.tambah.model.KontakGrupModel;
import com.windypermadi.yestodopersonal.utils.Prop;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
        if (checkBox.isChecked()){

        }

        return view;
    }
}
