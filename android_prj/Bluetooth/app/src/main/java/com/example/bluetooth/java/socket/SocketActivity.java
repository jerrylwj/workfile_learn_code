package com.example.bluetooth.java.socket;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.bluetooth.R;
import com.example.bluetooth.utils.Constants;

public class SocketActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "SocketActivity";
    private ScrollView mScrollView;
    private TextView mDebugInfo;
    private Button mStartServer;
    private Button mConnectServer;
    private Button mBtnSendMsg;
    private Button mReleaseSocket;

    private EditText mEdtAddr;
    private EditText mEdtSendData;


    private StringBuilder sb = new StringBuilder();
    private SocketManager mSocketManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socket_layout);

        initView();
        mSocketManager = new SocketManager(commActivityForSocket);
    }

    private void initView() {
        mBtnSendMsg = findViewById(R.id.btn_send_data);
        mStartServer = findViewById(R.id.btn_start_server);
        mConnectServer = findViewById(R.id.btn_connect_server);
        mReleaseSocket = findViewById(R.id.btn_release);
        mDebugInfo = findViewById(R.id.log_debug);
        mScrollView = findViewById(R.id.srcoll_view);

        mEdtAddr = findViewById(R.id.edt_server_ip);
        mEdtSendData = findViewById(R.id.edt_send_data);

        mBtnSendMsg.setOnClickListener(this);
        mStartServer.setOnClickListener(this);
        mConnectServer.setOnClickListener(this);
        mReleaseSocket.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_data:
                if (mEdtSendData != null && !TextUtils.isEmpty(mEdtSendData.getText().toString())) {
                    mSocketManager.sendMsg(mEdtSendData.getText().toString());
                    mEdtSendData.setText("");
                }
                break;
            case R.id.btn_start_server:
                mSocketManager.startServer(Constants.PORT);
                break;
            case R.id.btn_connect_server:
                mSocketManager.connectToServer(mEdtAddr.getText().toString(),
                        Constants.PORT);
                break;
            case R.id.btn_release:
                mSocketManager.releaseSocket();
                break;
            default:
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MSG_DISPLAY_DEBUG_INFO:
                    displayDebugInfo((String) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    private void displayDebugInfo(String data) {
        if (mDebugInfo != null) {
            if (mDebugInfo.getText() != null) {
                sb.append("\r\n");
            }
            sb.append(data);
            mDebugInfo.setText(sb.toString());
            Log.d(TAG, "debugInfo: " + data);
        }
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    CommActivityForSocket commActivityForSocket = new CommActivityForSocket();
    class CommActivityForSocket implements SocketManager.ICommActivityForSocket {
        @Override
        public void addLog(String string) {
            handler.sendMessage(handler.obtainMessage(Constants.MSG_DISPLAY_DEBUG_INFO,
                    string));
        }
    }
}
