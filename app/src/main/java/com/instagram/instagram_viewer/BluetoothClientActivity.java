package com.instagram.instagram_viewer;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BluetoothClientActivity extends AppCompatActivity {

    private static final String TAG = "haha";

    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;

    private static final int REQUEST_ENABLE_BT = 2;
    private TextView tv;
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private TextView tv5;

    private BluetoothAdapter bluetoothAdapter;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B3499");

    private ClientThread clientThread;

    private volatile boolean isClientRunning = false;

    private static final int MESSAGE_RECEIVED = 0;
    private static final int CONNECTION_SUCCESSFUL = 1;

    InputStream ClientInStream = null;
    OutputStream ClientOutStream = null;
    private String token;
    private ArrayList<InstagramPhoto> comments;
    private MyPhotosAdapter aComments;
    private ListView lv;
    private String url;
    private ArrayList<TextView> tvList= new ArrayList<TextView>(){{
        add(tv1);
        add(tv2);
        add(tv3);
        add(tv4);
        add(tv5);
    }};
    String stringTemp;

    private static android.os.Handler handler_process = new android.os.Handler(){
        public void handleMessage(Message msg){
            if (msg.what==MESSAGE_RECEIVED){
                Log.d(TAG, msg.obj.toString());
            }else if (msg.what==CONNECTION_SUCCESSFUL){
                Log.d(TAG, "CONNECTION_SUCCESSFUL");
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
        token = getIntent().getStringExtra("token");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_client);
        lv = (ListView)findViewById(R.id.lvMyPhotos);


        try {
            initRefreshLayout();
        } catch (IOException e) {
            e.printStackTrace();
        }

        tv = (TextView)findViewById(R.id.textclient);
//        tv1 = (TextView)findViewById(R.id.bt1);
//        tv2 = (TextView)findViewById(R.id.bt2);
//        tv3 = (TextView)findViewById(R.id.bt3);
//        tv4 = (TextView)findViewById(R.id.bt4);
//        tv5 = (TextView)findViewById(R.id.bt5);

        startBluetoothSensor();
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



        //to start scanning whether there are any other Bluetooth devices
        bluetoothAdapter.startDiscovery();

        //register the BroadcastReceiver to broadcast discovered devices
        filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        //return paired devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                Log.d(TAG, "@ paired devices: "+device.getAddress());
            }
        }
    }

    //Create a BroadcastRecevier for ACTION_FOUND
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //when discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                // Get the BluetoothDevice object from the Intent
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "@ discovered devices: "+device.getAddress());
                Toast.makeText(getApplicationContext(), "@ discovered devices: "+device.getAddress(), Toast.LENGTH_SHORT).show();
                tv.setText("@ discovered devices: "+device.getAddress());
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        becomeClient(device);

                    }
                });
                // call becomeClient
                // use your server device Bluetooth address
                //
//                if (device.getAddress().equals("A8:0C:63:4D:6F:D6"))
//                {
//                    becomeClient(device);
//                }

            }
        }
    };

    // be a client

    public void becomeClient(BluetoothDevice device) {
        clientThread = new ClientThread(device);
        clientThread.start();
    }

    private class ClientThread extends Thread {

        private final BluetoothSocket clientSocket;
        private final BluetoothDevice clientDevice;

        private ClientThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            clientDevice = device;
            clientSocket = tmp;
        }

        public void sendData(String data) {
            StringBuffer sb = new StringBuffer();
            sb.append(data);
            sb.append("\n");
            if (ClientOutStream != null) {
                try {
                    ClientOutStream.write(sb.toString().getBytes());
                    ClientOutStream.flush();
                    Log.d(TAG,"@ send data " + sb.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG,"@ Client sending fail");
                }
            }
        }

        public void closeClient(){
            this.interrupt();
        }


        public void run() {
            Log.d(TAG, "start client.");

            isClientRunning = true;

            bluetoothAdapter.cancelDiscovery();

            String line = "";

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                clientSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    clientSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // do work with connection

            while (isClientRunning) {

                Log.d(TAG, "do work with connection");

                try {
                    Log.d(TAG, "@ line1"+line);

                    if(ClientInStream == null) {
                        InputStream tmpIn = null;
                        // Get the input and output streams; using temp objects because
                        // member streams are final.
                        try {
                            tmpIn = clientSocket.getInputStream();
                        } catch (Exception e) {
                            Log.e(TAG, "Error occurred when creating input stream", e);
                        }
                        ClientInStream = tmpIn;
                    }

                    if(ClientOutStream == null) {
                        OutputStream tmpOut = null;
                        // Get the input and output streams; using temp objects because
                        // member streams are final.
                        try {
                            tmpOut = clientSocket.getOutputStream();
                        } catch (Exception e) {
                            Log.e(TAG, "Error occurred when creating output stream", e);
                        }
                        ClientOutStream = tmpOut;
                    }

                    // send data to server
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Adapter adpter=parent.getAdapter();
                            for (int i=0;i<adpter.getCount();i++){
                                InstagramPhoto item=(InstagramPhoto) adpter.getItem(i);
                                url =item.imageUrl;

                            }
                            sendData(url);

                        }
                    });


                    Log.d(TAG, "254");

                    // listen to server
                    while (isClientRunning &&ClientInStream != null) {

                        Log.d(TAG, "257");

                        BufferedReader br = new BufferedReader(new InputStreamReader(ClientInStream));
                        // readLine() read and delete one line
                        while ((line = br.readLine()) != null) {
                            Log.d(TAG, "@message " + line);
                            // send a message to the UI thread
                            Message message = new Message();
                            message.what = MESSAGE_RECEIVED;
                            message.obj = line;
                            handler_process.sendMessage(message);

                        }


                        if(Thread.currentThread().isInterrupted())
                        {
                            clientSocket.close();
                            Log.d(TAG, "quit from connection");
                            isClientRunning = false;
                            break;
                        }

                    }

                    if(Thread.currentThread().isInterrupted())
                    {
                        clientSocket.close();
                        Log.d(TAG, "quit from connection");
                        isClientRunning = false;
                        break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "@ exception");

                    Log.d(TAG, "quit from connection");

                    try {
                        clientSocket.close();
                    } catch (Exception e2) {
                        Log.e(TAG, "Error occurred in an exception of an exception", e2);
                    }
                    isClientRunning = false;

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
    private void initRefreshLayout() throws IOException {
        // Configure the refreshing colors


        fetchPhotos();

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        String commentsUrl = "http://imitagram.wnt.io/users/self/media/recent";

        // Create the network client
        get(commentsUrl, token);

    }

    OkHttpClient client = new OkHttpClient();

    void get(String url, String token) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", token)
                .build();
        JSONArray photosJSON = null;
//        Comment comment = new Comment();
//        comment.profileUrl = " http://img.tupianzj.com/uploads/allimg/141014/1-1410141AH02K.jpg";
//        comment.username = "huo";
//        comment.text = "helloWorld!helloWorld!helloWorld!helloWorld!helloWorld!helloWorld!helloWorld!";
//        comment.createdTime = "1279340983";
//        comments.add(comment);
//        aComments.notifyDataSetChanged();

        try (Response response = client.newCall(request).execute()) {
            stringTemp = response.body().string();
            comments.clear();
            photosJSON = new JSONArray(stringTemp);
            // put newest at the top
            for (int i = photosJSON.length() - 1; i >= 0; i--) {
                JSONObject photoJSON = photosJSON.getJSONObject(i);
                InstagramPhoto photo = new InstagramPhoto();
                photo.imageUrl = photoJSON.getJSONObject("image").getString("standard_resolution");
                comments.add(photo);
            }
            // Notified the adapter that it should populate new changes into the listview
            aComments.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}