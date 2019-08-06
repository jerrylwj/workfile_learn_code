package com.example.bluetooth.utils;

public class Constants {

    public static final int DISCOVER_BLUETOOTH = 0;
    public static final int CANCEL_DISCOVER_BLUETOOTH = 1;
    public static final int SEND_DATA_BLUETOOTH = 2;
    public static final int EVENT_BOND_DEVICE = 3;
    public static final int EVENT_CANCEL_BOND_DEVICE = 4;
    public static final int EVENT_START_BLUETOOTH_SERVER = 5;
    public static final int EVENT_CONNECT_SERVER = 6;

    public static final int MSG_DISPLAY_DEBUG_INFO = 0;
    public static final int MSG_DISPLAY_DEVICE_INFO = 1;

    public static final String PARAMS_SEND_DATA = "send_data";
    public static final String PARAMS_DEVICE_INFO = "bluetooth_device";

    public static final String UUID = "00001101-0000-1000-8000-00805F9B34FB";

    public static final String MSG_FLAG_END = "end";
    public static final String SERVER_IP = "127.0.0.1";
    public static final int PORT = 5000;
    public static final int EVENT_CONNECT_SOCKET_SERVER = 0;
    public static final int EVENT_START_SOCKET_SERVER = 1;
    public static final int EVENT_SEND_SOCKET_MESSAGE = 2;
    public static final int EVENT_RECV_SOCKET_MESSAGE = 3;

    public static final int OPERATION_TYPE_CLIENT = 0;
    public static final int OPERATION_TYPE_SERVER  = 1;
}
