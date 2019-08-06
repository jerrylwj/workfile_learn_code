package com.example.bluetooth;

import android.content.Context;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDeviceAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<DeviceItem> mData;

    public BluetoothDeviceAdapter(Context context, ArrayList<DeviceItem> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.device_item_layout,parent, false);
        }

        TextView name = convertView.findViewById(R.id.device_name_txt);
        TextView address = convertView.findViewById(R.id.devce_address_txt);
        TextView bondState = convertView.findViewById(R.id.device_bond_state_txt);
        name.setText(mData.get(position).getName());
        address.setText(mData.get(position).getAddress());
        bondState.setText(mData.get(position).getBondState() + "");
        return convertView;
    }
}
