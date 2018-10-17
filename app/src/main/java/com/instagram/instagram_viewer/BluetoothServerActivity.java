package com.instagram.instagram_viewer;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class BluetoothServerActivity extends AppCompatActivity {

    private static final String TAG = "haha";

    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;

    private static final int REQUEST_ENABLE_BT = 2;


    private TextView tv;
    private BluetoothAdapter bluetoothAdapter;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B3499");

    private ServerThread serverThread;

    private volatile boolean isServerRunning = false;

    private static final int MESSAGE_RECEIVED = 0;
    private static final int CONNECTION_SUCCESSFUL = 1;

    InputStream ServerInStream = null;
    OutputStream ServerOutStream = null;
    private String token;
    private SwipeRefreshLayout swipeContainer;
    InstagramPhoto photo = new InstagramPhoto();
    private ArrayList<InstagramPhoto> comments;
    private MyPhotosAdapter aComments;


    private  android.os.Handler handler_process = new android.os.Handler(){
        public void handleMessage(Message msg){

            if (msg.what==MESSAGE_RECEIVED){
                photo.bitmap = (Bitmap)msg.obj;
                aComments.notifyDataSetChanged();
                Log.d(TAG, msg.obj.toString());
            }else if (msg.what==CONNECTION_SUCCESSFUL){
                Log.d(TAG, "CONNECTION_SUCCESSFUL");
                Toast.makeText(getApplicationContext(), "CONNECTION SUCCESSFUL", Toast.LENGTH_SHORT).show();

            }
        }
    };

    private IntentFilter filter;

    private static final String[] BLUE_PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_server);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.mySwipeContainer);
        try {
            initRefreshLayout();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startBluetoothSensor();
    }
    private void initRefreshLayout() throws IOException {
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        fetchPhotos();

    }
    private void fetchPhotos() throws IOException {
        comments = new ArrayList<InstagramPhoto>(); // initialize arraylist
        // Create adapter bind it to the data in arraylist
        aComments = new MyPhotosAdapter(this, comments);
        // Populate the data into the listview
        ListView lvComments = (ListView) findViewById(R.id.lvMyPhotos);
        // Set the adapter to the listview (population of items)
        lvComments.setAdapter(aComments);
        // https://api.instagram.com/v1/media/<id>/comments?client_id=<clientid>
        // Setup comments url endpoint
        InstagramPhoto photo = new InstagramPhoto();
        comments.add(photo);


    }
    public void startBluetoothSensor(){

        // This only targets API 23+
        // check permission using a thousand lines (Google is naive!)


        if (!hasPermissionsGranted(BLUE_PERMISSIONS)) {
            requestBluePermissions(BLUE_PERMISSIONS);
            return;
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null)
        {
            Log.d(TAG, "Device has no bluetooth");
            Toast.makeText(getApplicationContext(), "Device has no bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }

        // ask users to open bluetooth
        if (bluetoothAdapter.isEnabled()==false){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


        // make this device visible to others for 3000 seconds
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3000);
        startActivity(discoverableIntent);

        // start server
        becomeServer();
    }


    // be a server

    public void becomeServer() {

        Log.d(TAG, "152");
        serverThread = new ServerThread();
        serverThread.start();
    }

    private class ServerThread extends Thread {
        private final BluetoothServerSocket serverSocket;

        private ServerThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("myServer", MY_UUID);
                Log.d(TAG, "@ UUID "+MY_UUID);
            } catch (IOException e) {
                Log.d(TAG, "Server establishing failed");
            }
            serverSocket = tmp;
        }

        public void sendData(String data) {
            StringBuffer sb = new StringBuffer();
            sb.append(data);
            sb.append("\n");
            if (ServerOutStream != null) {
                try {
                    ServerOutStream.write(sb.toString().getBytes());
                    ServerOutStream.flush();
                    Log.d(TAG,"@ send data " + sb.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG,"@ Server sending fail");
                }
            }
        }

        public void closeServer(){
            this.interrupt();
        }


        public void run() {
            Log.d(TAG, "start server.");
            BluetoothSocket connectSocket = null;
            String line = "";

            isServerRunning = true;

            // Keep listening until exception occurs or a socket is returned.
            while (isServerRunning) {
                try {
                    Log.d(TAG, "waiting for connection");
                    connectSocket = serverSocket.accept();
                } catch (IOException e) {
                    Log.d(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (connectSocket != null) {
                    // A connection was accepted. Perform work later.
                    break;
                }

                if(Thread.currentThread().isInterrupted())
                {

                    try {
                        Log.d(TAG, "quit during waiting for connection");
                        serverSocket.close();
                    } catch (IOException e) {
                        Log.d(TAG, "error quit during waiting", e);
                        break;
                    }

                    isServerRunning = false;
                    break;
                }
            }

            while (isServerRunning) {
                try {
                    Log.d(TAG, "do work with connection");
                    serverSocket.close();

                    if (connectSocket != null) {
                        // A connection was accepted. Perform work here

                        bluetoothAdapter.cancelDiscovery();
                        Log.d(TAG, "@ Connection Successfully");
                        // send a message to the UI thread
                        Message message = new Message();
                        message.what=CONNECTION_SUCCESSFUL;
                        handler_process.sendMessage(message);
                    }
                    else {
                        isServerRunning = false;
                        break;
                    }

                } catch (IOException e) {
                    Log.d(TAG, "Connection failed");
                    break;
                }
                try {
                    Log.d(TAG, "@ line1"+line);

                    if(ServerInStream == null) {
                        InputStream tmpIn = null;
                        // Get the input and output streams; using temp objects because
                        // member streams are final.
                        try {
                            tmpIn = connectSocket.getInputStream();
                        } catch (Exception e) {
                            Log.e(TAG, "Error occurred when creating input stream", e);
                        }
                        ServerInStream = tmpIn;
                    }

                    if(ServerOutStream == null) {
                        OutputStream tmpOut = null;
                        // Get the input and output streams; using temp objects because
                        // member streams are final.
                        try {
                            tmpOut = connectSocket.getOutputStream();
                        } catch (Exception e) {
                            Log.e(TAG, "Error occurred when creating output stream", e);
                        }
                        ServerOutStream = tmpOut;
                    }


                    // send data to client
                    sendData("Server: Hello!");

                    Log.d(TAG, "254");

                    while (isServerRunning &&ServerInStream != null) {

                        Bitmap bitmap = BitmapFactory.decodeStream(ServerInStream);
                        // readLine() read and delete one line
                        // send a message to the UI thread
                        Message message = new Message();
                        message.what = MESSAGE_RECEIVED;
                        message.obj = bitmap;
                        handler_process.sendMessage(message);



                        // send data to client
                        sendData("Server: Hello!");

                        if(Thread.currentThread().isInterrupted())
                        {
                            connectSocket.close();
                            Log.d(TAG, "quit from connection");
                            isServerRunning = false;
                            break;
                        }

                    }

                    if(Thread.currentThread().isInterrupted())
                    {
                        connectSocket.close();
                        Log.d(TAG, "quit from connection");
                        isServerRunning = false;
                        break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();


                    Log.d(TAG, "quit from connection");

                    try {
                        connectSocket.close();
                    } catch (Exception e2) {
                        Log.e(TAG, "Error occurred in an exception of an exception", e2);
                    }
                    isServerRunning = false;

                    break;
                }

            }

        }

    }



    // check if app has a list of permissions, then request not-granted ones

    public void requestBluePermissions(String[] permissions) {
        Log.d(TAG, "line 376");
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_BLUETOOTH_PERMISSIONS);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_BLUETOOTH_PERMISSIONS:
                Log.d(TAG, "line 394");

                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            Log.d("haha", "one or more permission denied");
                            return;
                        }
                    }
                    Log.d("haha", "all permissions granted");

                }

        }
    }


    private boolean hasPermissionsGranted(String[] permissions) {
        Log.d(TAG, "411");
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }
}
