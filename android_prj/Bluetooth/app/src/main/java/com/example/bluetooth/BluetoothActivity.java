package com.example.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.bluetooth.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BluetoothActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener{
    private static final String TAG = "BluetoothActivity";
    private Button mBtnFinddevice;
    private Button mBtnCancelFinddevice;
    private Button mBtnStartServer;
    private Button mBtnConnectServer;

    private Button mBtnSendData;

    private EditText mEdtSendData;
    private ScrollView mScrollView;
    private ListView mListView;

    private BluetoothDeviceAdapter mAdapter;
    private ArrayList<DeviceItem> mDevices;
    private Map<String, BluetoothDevice> mDeviceList;

    private TextView mTvDebugInfo;
    private BluetoothManager mBluetoothManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluebooth_layout);

        mBluetoothManager = new BluetoothManager(this, commActivity);
        initView();
        initListView();
    }

    private void initView() {
        mBtnFinddevice = findViewById(R.id.btn_discover_device);
        mBtnCancelFinddevice = findViewById(R.id.btn_cancel_discover_device);
        mBtnStartServer = findViewById(R.id.btn_server_start);
        mBtnConnectServer = findViewById(R.id.btn_connect_server);
        mBtnSendData = findViewById(R.id.btn_send_data);
        mEdtSendData = findViewById(R.id.edt_send_data);
        mScrollView = findViewById(R.id.srcoll_view);

        mTvDebugInfo = findViewById(R.id.log_debug);

        mBtnFinddevice.setOnClickListener(this);
        mBtnCancelFinddevice.setOnClickListener(this);
        mBtnSendData.setOnClickListener(this);
        mBtnStartServer.setOnClickListener(this);
        mBtnConnectServer.setOnClickListener(this);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MSG_DISPLAY_DEBUG_INFO:
                    displayDebugInfo((String) msg.obj);
                    break;
                case Constants.MSG_DISPLAY_DEVICE_INFO:
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void initListView() {
        mListView = findViewById(R.id.device_list_view);
        mDevices = new ArrayList<>();
        mDeviceList = new HashMap<>();
        mAdapter = new BluetoothDeviceAdapter(BluetoothActivity.this, mDevices);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        commActivity.addLog("click item address = " + mDevices.get(position));
        String deviceAddr = mDevices.get(position).getAddress();
        BluetoothDevice device = mDeviceList.get(deviceAddr);
        mBluetoothManager.createBond(device);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_discover_device:
                mBluetoothManager.doEvent(Constants.DISCOVER_BLUETOOTH, null);
                break;
            case R.id.btn_cancel_discover_device:
                mBluetoothManager.doEvent(Constants.CANCEL_DISCOVER_BLUETOOTH, null);
                break;
            case R.id.btn_send_data:
                Bundle param = new Bundle();
                param.putString(Constants.PARAMS_SEND_DATA, mEdtSendData.getText().toString());
                mBluetoothManager.doEvent(Constants.SEND_DATA_BLUETOOTH, param);
                break;
            case R.id.btn_server_start:
                mBluetoothManager.doEvent(Constants.EVENT_START_BLUETOOTH_SERVER, null);
                break;
            case R.id.btn_connect_server:
                mBluetoothManager.doEvent(Constants.EVENT_CONNECT_SERVER, null);
                break;
            default:
                break;
        }
    }

    private StringBuilder sb = new StringBuilder();
    private void displayDebugInfo(String data) {
        if (mTvDebugInfo != null) {
            if (mTvDebugInfo.getText() != null) {
                sb.append("\r\n");
            }
            sb.append(data);
            mTvDebugInfo.setText(sb.toString());
            Log.d(TAG, "debugInfo: " + data);
        }
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void addDeviceList(BluetoothDevice device) {
        for (DeviceItem temp : mDevices) {
            if (temp.getAddress().equals(device.getAddress())) {
                commActivity.addLog(device + " already exits");
                return;
            }
        }
        DeviceItem item = new DeviceItem();
        item.setAddress(device.getAddress());
        item.setName(device.getName() == null ? "null" : device.getName());
        item.setBondState(device.getBondState());
        mDevices.add(item);
        mDeviceList.put(device.getAddress(), device);
    }

    CommActivity commActivity = new CommActivity();
    class CommActivity implements BluetoothManager.ICommActivity {
        @Override
        public void addLog(String string) {
            mHandler.sendMessage(mHandler.obtainMessage(Constants.MSG_DISPLAY_DEBUG_INFO,
                    string));
        }

        @Override
        public void showBluetoothDevice(BluetoothDevice device) {
            addDeviceList(device);
            if (mHandler.hasMessages(Constants.MSG_DISPLAY_DEVICE_INFO)) {
                mHandler.removeMessages(Constants.MSG_DISPLAY_DEVICE_INFO);
            }
            mHandler.sendMessageDelayed(mHandler.obtainMessage(Constants.MSG_DISPLAY_DEVICE_INFO),
                    200);
        }

        @Override
        public void setDeviceBondState(BluetoothDevice device, int bondState) {
            for (DeviceItem item : mDevices) {
                if (item.getAddress().equals(device.getAddress())) {
                    commActivity.addLog("change state, addr:" + item.getAddress()
                            + ", bondstate:" + bondState);
                    item.setBondState(bondState);
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(Constants.MSG_DISPLAY_DEVICE_INFO),
                            200);
                    return;
                }
            }
            return;
        }
    }
}
