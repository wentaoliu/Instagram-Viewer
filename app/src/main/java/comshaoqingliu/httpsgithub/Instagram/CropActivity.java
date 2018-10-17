package comshaoqingliu.httpsgithub.Instagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import Helper.BitmapStore;
import comshaoqingliu.httpsgithub.helloworld.R;

import com.theartofdev.edmodo.cropper.CropImageView;

/* This activity deals with the crop image function */
public class CropActivity extends AppCompatActivity {

    private Bitmap rawBitmap = null;
    private Bitmap cropBitmap = null;
//    private CropView cropview = null;
    private CropImageView cropview = null;


    private Button btnOK = null;
    private Button btnReturn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

//        cropview = (CropView) findViewById(R.id.crop_view);     //original
        cropview = findViewById(R.id.cropImageView);
        Intent intent = getIntent();
        if (intent != null) {
            rawBitmap = BitmapStore.getBitmap();
            cropview.setImageBitmap(rawBitmap);
        }

        btnOK = (Button) findViewById(R.id.button_ok);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                cropBitmap = cropview.getCroppedImage();    // original
                cropBitmap = cropview.getCroppedImage();
                cropview.setImageBitmap(cropBitmap);
            }
        });

        btnReturn = (Button) findViewById(R.id.button_return);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cropBitmap != null) {
                    BitmapStore.setBitmap(cropBitmap);
                    Intent intent = new Intent(CropActivity.this, EditPhotoActivity.class);
                    CropActivity.this.finish();   // kill CropActivity before back to editPhotoActivity
                    startActivity(intent);
                } else {
                    CropActivity.this.finish();
                }
            }
        });
    }


}
