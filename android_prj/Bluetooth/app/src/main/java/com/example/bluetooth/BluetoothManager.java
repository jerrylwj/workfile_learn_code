package com.example.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import com.example.bluetooth.utils.ClsUtils;
import com.example.bluetooth.utils.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import static android.os.Build.VERSION_CODES.KITKAT;


public class BluetoothManager {

    private Context mContext;
    private ICommActivity mCommActivity;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mPairSucDevice = null;

    private BluetoothSocket mBluetoothSocket;

    private BluetoothServerSocket mBluetoothServerSocket;
    private BluetoothSocket mBluetoothSocketForServer;

    private BluetoothSocket mBluetoothSocketForClient;

    public BluetoothManager(Context context, ICommActivity commActivity) {
        this.mCommActivity = commActivity;
        mContext = context;
        initBluetooth();
    }

    public int doEvent(int event, Bundle params) {
        int ret = 0;
        switch (event) {
            case Constants.DISCOVER_BLUETOOTH:
                discoverDevice();
                break;
            case Constants.CANCEL_DISCOVER_BLUETOOTH:
                cancelDiscoverDevice();
                break;
            case Constants.SEND_DATA_BLUETOOTH:
                sendData(mBluetoothSocketForClient, params.getString(Constants.PARAMS_SEND_DATA));
                break;
            case Constants.EVENT_BOND_DEVICE:
                break;
            case Constants.EVENT_CANCEL_BOND_DEVICE:
                break;
            case Constants.EVENT_START_BLUETOOTH_SERVER:
                startServerSocket();
                break;
            case Constants.EVENT_CONNECT_SERVER:
                connectToServer(mPairSucDevice, UUID.fromString(Constants.UUID));
                break;
            default:
                break;
        }
        return ret;
    }

    private void initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            mCommActivity.addLog("手机不支持蓝牙");
            return;
        }
        mCommActivity.addLog("initBluetooth.");
        registerBlueAction();
    }

    private void registerBlueAction() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        if (Build.VERSION.SDK_INT >= KITKAT) {
            filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        }
        mContext.registerReceiver(mReceiver, filter);
    }

    private void unRegisterBlueAction() {
        mContext.unregisterReceiver(mReceiver);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            mCommActivity.addLog("收到广播：" + action);
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    foundDevice(intent);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    break;
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    bindDeviceState(intent);
                default:
                    break;
            }
        }
    };

    private void foundDevice(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(
                BluetoothDevice.EXTRA_DEVICE);
        mCommActivity.addLog("name=" + device.getName()
            + ", address=" + device.getAddress()
            + ", bondState=" + device.getBondState());
        if (device.getAddress() != null) {
            mCommActivity.showBluetoothDevice(device);
        }
    }

    private void discoverDevice() {
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
            mCommActivity.addLog("开启蓝牙");
        }
        mBluetoothAdapter.startDiscovery();
        mCommActivity.addLog("正在搜索蓝牙");
    }

    private void cancelDiscoverDevice() {
        mCommActivity.addLog("取消搜索蓝牙");
        mBluetoothAdapter.cancelDiscovery();
    }

    public void createBond(BluetoothDevice device) {
        mCommActivity.addLog("createBond addr = " + device.getAddress() +
                ", bondState = " + device.getBondState());
        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
            return;
        }
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        ClsUtils.createBond(device);
    }

    public void removeBond(BluetoothDevice device) {
        mCommActivity.addLog("removeBond addr = " + device.getAddress());
        ClsUtils.removeBond(device);
    }

    private void bindDeviceState(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        int state = device.getBondState();
        switch (state) {
            case BluetoothDevice.BOND_BONDING:
                mCommActivity.addLog("正在配对");
                break;
            case BluetoothDevice.BOND_BONDED:
                mCommActivity.addLog("配对成功, addr:" + device.getAddress());
                mCommActivity.setDeviceBondState(device, device.getBondState());
                mPairSucDevice = device;
                break;
            case BluetoothDevice.BOND_NONE:
                mCommActivity.addLog("未配对");
                break;
            default:
                break;
        }
    }

    /*
    * 服务端调用，开启服务，等待客户端连接
    * */
    private UUID startServerSocket() {
        UUID uuid = UUID.fromString(Constants.UUID);
        String name = "bluetooth";
        try {
            final BluetoothServerSocket serverSocket =
                    mBluetoothAdapter.listenUsingRfcommWithServiceRecord(name, uuid);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mCommActivity.addLog("startServerSocket.");
                        BluetoothSocket socket = serverSocket.accept();
                        listenMessage(socket);
                        mBluetoothSocketForServer = socket;
                        mCommActivity.addLog("startServerSocket. accept one device request.");
                    } catch (IOException e) {
                        mCommActivity.addLog("IOException e=" + e.getMessage());
                    }
                }
            }).start();
        } catch (IOException e) {
            mCommActivity.addLog("IOException e " + e.getMessage());
        }

        return uuid;
    }

    /**
     * 客户端调用，连接服务端
     *
     * */
    private void connectToServer(final BluetoothDevice device, final UUID uuid) {
        if (device == null) {
            return;
        }
        mCommActivity.addLog("connectToServer.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                BluetoothSocket socket = null;
                try {
                    socket = device.createRfcommSocketToServiceRecord(uuid);
                    mCommActivity.addLog("connectToServer. addr " + device.getAddress());
                    socket.connect();
                    listenMessage(socket);
                    mBluetoothSocketForClient = socket;
                    mCommActivity.addLog("connectToServer success.");
                } catch (IOException e) {
                    mCommActivity.addLog("connectToServcer exception " + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 通信接口，相互发送数据，服务端或客户端调用
     * */
    private void sendData(BluetoothSocket socket, String data) {
        mCommActivity.addLog("发送数据:" + data);
        OutputStream outputStream;
        try {
            outputStream = socket.getOutputStream();
            byte[] byteArray = (data + " ").getBytes();
            byteArray[byteArray.length - 1] = 0;
            outputStream.write(byteArray);
        } catch (IOException e) {
            mCommActivity.addLog("发送失败 error:" + e.getMessage());
        } catch (Exception e) {
            mCommActivity.addLog("send data error : " + e.getMessage());
        }
    }

    private boolean listening = false;
    /**
     * 通信接口，解析收到的数据，服务端或客户端调用
     * */
    private String listenMessage(BluetoothSocket socket) {
        StringBuilder sb = new StringBuilder();

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        listening = true;

        try {
            InputStream inputStream = socket.getInputStream();
            int bytesRead = -1;
            while (listening) {
                bytesRead = inputStream.read(buffer);
                if (bytesRead != -1) {
                    String result = "";
                    while ((bytesRead == bufferSize) &&
                            (buffer[bufferSize - 1] != 0)) {
                        result += new String(buffer, 0, bytesRead - 1);
                        bytesRead = inputStream.read(buffer);
                    }
                    result += new String(buffer, 0, bytesRead - 1);
                    sb.append(result);
                }
                socket.close();
            }
            mCommActivity.addLog("recv data : " + sb.toString());
        } catch (IOException e) {
            mCommActivity.addLog("收到消息失败");
        } finally {
        }
        return sb.toString();
    }


    interface ICommActivity {
        void addLog(String string);
        void showBluetoothDevice(BluetoothDevice device);
        void setDeviceBondState(BluetoothDevice device, int bondState);
    }
}
