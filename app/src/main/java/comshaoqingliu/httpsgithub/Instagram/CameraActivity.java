package comshaoqingliu.httpsgithub.Instagram;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
import Helper.BitmapStore;
import comshaoqingliu.httpsgithub.helloworld.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/* Camera Activity implements the camera functionality of the app. */
public class CameraActivity extends Activity implements SurfaceHolder.Callback {

    private Camera camera = null;
    private SurfaceView cameraSurfaceView = null;
    private SurfaceHolder cameraSurfaceHolder = null;
    private boolean previewing = false;
    RelativeLayout relativeLayout;

    private Button btnCapture = null;
    private ToggleButton btnFlash = null;
    private ImageButton btnGallery = null;

    private final String TAG = "CameraActivity";
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    // On create, first initialize the view elements
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /**************************************** Button to Capture *******************************/
        Log.d(TAG,"----------onCreate begin--------");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        relativeLayout=(RelativeLayout) findViewById(R.id.containerImg);
        relativeLayout.setDrawingCacheEnabled(true);

        cameraSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);

        cameraSurfaceHolder = cameraSurfaceView.getHolder();
        cameraSurfaceHolder.setKeepScreenOn(true);
        cameraSurfaceHolder.addCallback(this);

        btnCapture = (Button)findViewById(R.id.button_camera);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(cameraShutterCallback,
                        cameraPictureCallbackRaw,
                        cameraPictureCallbackJpeg);
            }
        });
        /**************************************** Button to Capture *******************************/

        /**************************************** Button to Control FlashLight *******************************/
        btnFlash = (ToggleButton) findViewById(R.id.button_flash);
        // BugFixed - when return with isChecked state, there is a error message on screen
        // saying that camera isn't working properly, but does not affect the functioning
        btnFlash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "----------Flash button click--------");

//              check if the device has Flashlight
                boolean hasFlash = getApplicationContext().getPackageManager()
                        .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

                if (!hasFlash) {
                    Toast.makeText(getApplicationContext(), "Sorry, your device doesn't support flash light!", Toast.LENGTH_LONG).show();
                } else {
                    if (isChecked) {
                        // The toggle is enabled
                        if (camera == null) {
                            camera = Camera.open();
                        }
                        Camera.Parameters parameters = camera.getParameters();
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        camera.setParameters(parameters);
                        camera.startPreview();
                    } else {
                        // The toggle is disabled
                        if (camera == null) {
                            camera = Camera.open();
                        }
                        Camera.Parameters parameters = camera.getParameters();
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        camera.setParameters(parameters);
                        camera.startPreview();
                    }
                }
            }
        });
        /**************************************** Button to Control FlashLight *******************************/


        /**************************************** Button to Enter Gallery *******************************/
        // Bug-fixed need to access all the pictures
        btnGallery = (ImageButton) findViewById(R.id.button_gallery);
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"----------Enter Gallery--------");
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                final int ACTIVITY_SELECT_IMAGE = 0;
                startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);
            }
        });
    }
        /**************************************** Button to Enter Gallery *******************************/

    Camera.ShutterCallback cameraShutterCallback = new Camera.ShutterCallback(){
        @Override
        public void onShutter(){

        }
    };

    Camera.PictureCallback cameraPictureCallbackRaw = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };

    Camera.PictureCallback cameraPictureCallbackJpeg = new Camera.PictureCallback(){
        @Override
        public void onPictureTaken(byte[] data, Camera camera){
            Log.d("onPictureTaken","---------onPictureTaken begins-------");
            Bitmap cameraBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

//          1.create a directory to save image
            File storagePath = new File(Environment.getExternalStorageDirectory()
                    + "/DCIM/100ANDRO/");
            if (! storagePath.exists()){  // if not exist, create one
                storagePath.mkdirs();
            }

//          2.create a jpg file
            File myImage = new File(storagePath, Long.toString(System.currentTimeMillis()) + ".jpg");
//          3. store the bitmap into the jpg file we created
            try {
                FileOutputStream out = new FileOutputStream(myImage);
                cameraBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                out.flush();
                out.close();
            } catch(FileNotFoundException e) {
                Log.d("In Saving File", e + "");
            } catch(IOException e) {
                Log.d("In Saving File", e + "");
            }
//          4.Send the image file to the gallery
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(myImage)));
            Log.d("onPictureTaken","---------gallery updated-------");

            // Pass the new image to the next edit view
            BitmapStore.setBitmap(cameraBitmap);
            Intent intent = new Intent();
            intent.setClass(CameraActivity.this, EditPhotoActivity.class);
            startActivity(intent);
        }
    };

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(previewing) {
            camera.stopPreview();
            previewing = false;
        }
        try {
            // set camera and preview size
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(640, 480);
            parameters.setPictureSize(640, 480);
            if(this.getResources().getConfiguration().orientation
                    != Configuration.ORIENTATION_LANDSCAPE) {
                camera.setDisplayOrientation(90);
            }
//            camera.setDisplayOrientation(90);

            camera.setParameters(parameters);
            camera.setPreviewDisplay(cameraSurfaceHolder);
            camera.startPreview();
            previewing = true;
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("surfaceCreated","---------surfaceCreated begins--------");
        try {
//            check permission before open camera
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_CAMERA_REQUEST_CODE);
            }

            camera = Camera.open();
        } catch(RuntimeException e) {
            Toast.makeText(getApplicationContext(), "Device Camera is " +
                    "not working properly, please try after sometime.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        if (camera!=null){
            camera.release();
            camera = null;
            previewing = false;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    // Bug-fixed: out of memory error when go back and select photo 2nd time
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Uri selectedImage = data.getData();

            try{
                Bitmap yourSelectedImage = BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(selectedImage));
                BitmapStore.setBitmap(yourSelectedImage);
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }

            // Pass the new image to the next edit view
            Log.d("onActivityResult","---------this is onActivityResult--------");
            Intent intent = new Intent();
            intent.setClass(CameraActivity.this, EditPhotoActivity.class);
            startActivity(intent);
        }
    }
}
