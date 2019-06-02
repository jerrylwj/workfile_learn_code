package com.example.a67024.serviceapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FruitAdapter extends ArrayAdapter<Fruit> {

    private int resourceId;

    public FruitAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Fruit fruitItem = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            TextView fruitName =  view.findViewById(R.id.fruit_name);
            ImageView imageView =  view.findViewById(R.id.fruit_image);
            viewHolder.fruitImage = imageView;
            viewHolder.txtFruitName = fruitName;
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.txtFruitName.setText(fruitItem.getmFruitName());
        viewHolder.fruitImage.setImageResource(fruitItem.getmImageId());
        return view;
    }

    class ViewHolder {
        public TextView txtFruitName;
        public ImageView fruitImage;
    }
}
