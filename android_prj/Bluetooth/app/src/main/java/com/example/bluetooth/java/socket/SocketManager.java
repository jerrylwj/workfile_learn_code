package com.example.bluetooth.java.socket;

import android.text.TextUtils;

import com.example.bluetooth.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SocketManager {
    private static final String TAG = "SocketManager";
    private static final int EVENT_TYPE_SEND_MSG = 0;
    private static final int EVENT_TYPE_RECV_MSG = 1;

    private ICommActivityForSocket commActivityForSocket;
    private Socket mSocketClient = null;
    private ServerSocket mServerSocket = null;
    private List<Socket> mSocketServerClientList = new ArrayList<>();

    private int mOperation = 0;

    public SocketManager(ICommActivityForSocket commActivityForSocket) {
        this.commActivityForSocket = commActivityForSocket;
    }

    /*
    *
    * 服务端调用，开启服务
    *
    * */
    public void startServer(final int port) {
        mOperation = Constants.OPERATION_TYPE_SERVER;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mServerSocket = new ServerSocket(port);
                    while (true) {
                        commActivityForSocket.addLog("wait for client accept.");
                        Socket socket = mServerSocket.accept();
                        mSocketServerClientList.add(socket);
                        commActivityForSocket.addLog("accept one socket. addr:" + socket.getRemoteSocketAddress());
                        new SocketThread(socket, EVENT_TYPE_SEND_MSG,
                                "from server: hello client , addr:" + socket.getRemoteSocketAddress())
                                .start();
                        new SocketThread(socket, EVENT_TYPE_RECV_MSG, null)
                                .start();
                    }
                } catch (IOException e) {
                    commActivityForSocket.addLog("create server socket error:" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     *
     * 客服端调用，连接到服务器
     *
     */
    public void connectToServer(final String ipAddr, final int port) {
        if (TextUtils.isEmpty(ipAddr)) {
            commActivityForSocket.addLog("ipAddr == null");
            return;
        }
        mOperation = Constants.OPERATION_TYPE_CLIENT;
        commActivityForSocket.addLog("connect to server");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocketClient = new Socket(ipAddr, port);
                    mSocketClient.setReuseAddress(true);
                    new SocketThread(mSocketClient, EVENT_TYPE_RECV_MSG, null)
                        .start();
                } catch (IOException e) {
                    commActivityForSocket.addLog("connect to server error:" + e.getMessage());
                }
            }
        }).start();
    }

    /*
    *
    * 关闭socket连接
    * */
    public void releaseSocket() {
        sendMsg(Constants.MSG_FLAG_END);
    }

    /*
    * 删除服务端socket
    *
    * */
    private void deleteSocket(Socket socket) throws IOException {
        if (mOperation != Constants.OPERATION_TYPE_SERVER) {
            return;
        }
        for (Socket tmp : mSocketServerClientList) {
            commActivityForSocket.addLog("tmp_addr:" + tmp.getRemoteSocketAddress()
                    + " sock_arr:" + socket.getRemoteSocketAddress());
            if (tmp.getRemoteSocketAddress().equals(socket.getRemoteSocketAddress())) {
                mSocketServerClientList.remove(tmp);
                if (mSocketServerClientList.isEmpty() && (mServerSocket != null)) {
                    mServerSocket.close();
                }
                break;
            }
        }
    }

    /*
    *
    * 发送数据
    *
    * */
    public void sendMsg(String data) {
        switch (mOperation) {
            case Constants.OPERATION_TYPE_CLIENT:
                if (mSocketClient != null) {
                    new SocketThread(mSocketClient, EVENT_TYPE_SEND_MSG, data)
                            .start();
                }
                break;
            case Constants.OPERATION_TYPE_SERVER:
                if (mSocketServerClientList != null && !mSocketServerClientList.isEmpty()) {
                    new SocketThread(mSocketServerClientList.get(0), EVENT_TYPE_SEND_MSG, data)
                            .start();
                }
                break;
            default:
                break;
        }
    }

    private void sendMsg(Socket socket, String data) {
        if (socket == null || TextUtils.isEmpty(data)) {
            commActivityForSocket.addLog("sendMsg mClientSocket == null || data == null");
            return;
        }
        PrintWriter writer = null;
        try {
            commActivityForSocket.addLog("send msg. data:" + data);
            writer = new PrintWriter(socket.getOutputStream());
            writer.write(data + "\n");
            writer.flush();
        } catch (IOException e) {
            commActivityForSocket.addLog("sendMsg error = " + e.getMessage());
        }
    }

    private void recvMsg(Socket socket) throws IOException {
        if (socket == null) {
            commActivityForSocket.addLog("recvMsg failed. mClientSocket == null");
            return;
        }
        commActivityForSocket.addLog("recv msg.");
        InputStream inputStream = socket.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String recvMsg;
        while((recvMsg = bufferedReader.readLine()) != null && !Constants.MSG_FLAG_END.equalsIgnoreCase(recvMsg)) {
            commActivityForSocket.addLog(recvMsg);
        }
        commActivityForSocket.addLog("recvMsg : " + recvMsg);
        bufferedReader.close();
        if (!socket.isClosed()) {
            socket.close();
        }
        commActivityForSocket.addLog("socket closed? " + socket.isClosed());
        deleteSocket(socket);
    }

    class SocketThread extends Thread {
        private Socket socket;
        private int eventType;
        private String sendData;

        public SocketThread(Socket socket, int eventType, String param) {
            this.socket = socket;
            this.eventType = eventType;
            sendData = param;
        }

        @Override
        public void run() {
            try {
                if (eventType == EVENT_TYPE_RECV_MSG) {
                    recvMsg(socket);
                } else {
                    sendMsg(socket, sendData);
                }
            } catch (IOException e) {
                commActivityForSocket.addLog("socketThread recvMsg error:"
                        + e.getMessage() + ", eventType=" + eventType);
            }
        }
    }

    interface ICommActivityForSocket {
        void addLog(String string);
    }
}
