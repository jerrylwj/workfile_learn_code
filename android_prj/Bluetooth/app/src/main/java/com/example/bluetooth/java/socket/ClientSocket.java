package com.example.bluetooth.java.socket;

import android.os.Bundle;
import android.util.Log;

import com.example.bluetooth.utils.Constants;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientSocket {
    private static final String TAG = "ClientSocket";

    private Socket mClientSocket;

    public ClientSocket() {

    }

    private void doEvent(int event, Bundle params) {
        switch (event) {
            case Constants.EVENT_CONNECT_SOCKET_SERVER:
                connectSocketServer();
                break;
            case Constants.EVENT_START_SOCKET_SERVER:
                break;
            case Constants.EVENT_SEND_SOCKET_MESSAGE:
                break;
            case Constants.EVENT_RECV_SOCKET_MESSAGE:
                break;
            default:
                break;
        }
    }

    private void connectSocketServer() {
        try {
            mClientSocket = new Socket(Constants.SERVER_IP, Constants.PORT);
        } catch (UnknownHostException e) {
            Log.d(TAG, "connect server failed. error=" + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "IOException error=" + e.getMessage());
        }
    }
}
