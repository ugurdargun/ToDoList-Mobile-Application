package com.msu.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomSpinnerAdapter extends ArrayAdapter<Category> {
    Context context;
    ArrayList<Category> spinerItemValues;

    public CustomSpinnerAdapter(@NonNull Context context, @NonNull ArrayList<Category> values) {
        super(context, 0, values);
        this.context = context;
        this.spinerItemValues = values;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getMyCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getMyCustomView(position, convertView, parent);
    }

    public View getMyCustomView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Category stemp = spinerItemValues.get(position);

        View view = inflater.inflate(R.layout.custom_spinner1, parent, false);
        TextView tvItemName = view.findViewById(R.id.custSpin_catName);
        ImageView imgItem = view.findViewById(R.id.custSpin_catImg);

        tvItemName.setText(stemp.getName());

        int id = context.getResources().getIdentifier(stemp.getImgName(), "drawable", context.getPackageName());
        imgItem.setImageResource(id);

        return view;
    }
}