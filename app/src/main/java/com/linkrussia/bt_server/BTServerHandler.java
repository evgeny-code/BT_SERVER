package com.linkrussia.bt_server;

import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.function.Consumer;

public class BTServerHandler implements Runnable {
    private final BluetoothSocket socket;
    private final Consumer<Void> callback;

    public BTServerHandler(BluetoothSocket socket, Consumer<Void> callback) {
        this.socket = socket;
        this.callback = callback;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        try (
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        ) {
            String data = dis.readUTF();
            dos.writeUTF("Data: " + data + " _ " + LocalDateTime.now());
        } catch (IOException e) {
            Log.e("BTServerHandler", "IOException", e);
        }

        callback.accept(null);
    }
}
