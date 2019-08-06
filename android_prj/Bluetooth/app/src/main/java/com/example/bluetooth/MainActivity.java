package com.example.bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.example.bluetooth.java.socket.SocketActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        Button bluetooth = findViewById(R.id.start_bluetooth_activity);
        Button socket = findViewById(R.id.start_socket_activity);

        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(BluetoothActivity.class);
            }
        });

        socket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SocketActivity.class);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startActivity(Class<?> packageName) {
        Intent intent = new Intent(MainActivity.this, packageName);
        startActivity(intent);
    }
}
