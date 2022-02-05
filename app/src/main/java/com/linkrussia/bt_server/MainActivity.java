package com.linkrussia.bt_server;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {
    private static final UUID SERVER_UUID = UUID.fromString("00000002-0000-2000-0000-000000000000");

    private static final ExecutorService executorService = new ForkJoinPool();

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            createServer();
        } catch (IOException e) {
            Log.e("Bluetooth", "createServer IOException", e);
        }
    }

    private void createServer() throws IOException {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        @SuppressLint("MissingPermission")
        BluetoothServerSocket bluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("ANDR_SRV", SERVER_UUID);

        executorService.submit(() -> {
            while (true) {
                synchronized (bluetoothServerSocket) {
                    BluetoothSocket socket = bluetoothServerSocket.accept();
                    Consumer<Void> closeSocketCallback = (Void) -> {
                        try {
                            if (null != socket)
                                socket.close();
                        } catch (IOException e) {
                            Log.e("Bluetooth", "close socket IOException", e);
                        }
                    };
                    executorService.submit(new BTServerHandler(socket, closeSocketCallback));
                }
            }
        });
    }
}