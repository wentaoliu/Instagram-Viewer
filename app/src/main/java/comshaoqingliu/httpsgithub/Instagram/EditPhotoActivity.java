package comshaoqingliu.httpsgithub.Instagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import Helper.BitmapStore;
import Helper.ImageProcessing;
import comshaoqingliu.httpsgithub.helloworld.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/*
 * This class implements all the photo editing related functions, including the contrast and
 * brightness, filters, and crop
 */
public class EditPhotoActivity extends AppCompatActivity {
    private ImageView imageView;
    private Bitmap rawBitmap;
    private Bitmap newBitmap;
    private Bitmap scaledBitmap;

    private Button btnColorFilter = null;
    private Button btnSaturation = null;
    private Button btnEngrave = null;
    private Button btnCrop = null;
    private SeekBar seekBarContrast = null;
    private SeekBar seekBarBrightness = null;
    private TextView textview_contrast = null;
    private TextView textview_brightness = null;

    static int progress_contrast = 0;
    static int progress_brightness = 0;
    static int FILTER_STATIC = 0; // 0 represents no filters on, 1 one filter applied.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);

        imageView = (ImageView)findViewById(R.id.imageview_edit);
        Intent intent = getIntent();
        if (intent != null) {
            rawBitmap = BitmapStore.getBitmap();
            rawBitmap = Bitmap.createScaledBitmap(rawBitmap, 640, 640, false);
            imageView.setImageBitmap(rawBitmap);
            newBitmap = rawBitmap;
        }

        // Filters
        btnColorFilter = (Button) findViewById(R.id.button_color);
        btnSaturation = (Button) findViewById(R.id.button_saturation);
        btnEngrave = (Button) findViewById(R.id.button_engrave);
        btnCrop = (Button) findViewById(R.id.button_crop);


        btnColorFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FILTER_STATIC == 1) {
                    newBitmap = ImageProcessing.doColorFilter(rawBitmap, 0.5, 0.5, 0.5);
                    imageView.setImageBitmap(newBitmap);
                } else {
                    newBitmap = ImageProcessing.doColorFilter(newBitmap, 0.5, 0.5, 0.5);
                    imageView.setImageBitmap(newBitmap);
                    FILTER_STATIC = 1;
                }
            }
        });

        btnSaturation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FILTER_STATIC == 1) {
                    newBitmap = ImageProcessing.applySaturationFilter(rawBitmap, 1);
                    imageView.setImageBitmap(newBitmap);
                } else {
                    newBitmap = ImageProcessing.applySaturationFilter(newBitmap, 1);
                    imageView.setImageBitmap(newBitmap);
                    FILTER_STATIC = 1;
                }
            }
        });

        btnEngrave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FILTER_STATIC == 1) {
                    newBitmap = ImageProcessing.engrave(rawBitmap);
                    imageView.setImageBitmap(newBitmap);
                } else {
                    newBitmap = ImageProcessing.engrave(newBitmap);
                    imageView.setImageBitmap(newBitmap);
                    FILTER_STATIC = 1;
                }
            }
        });

        // Contrast & Brightness
        seekBarContrast = (SeekBar) findViewById(R.id.seekbar_contrast);
        seekBarBrightness = (SeekBar) findViewById(R.id.seekbar_brightness);
        textview_contrast = (TextView) findViewById(R.id.text_contrast);
        textview_brightness = (TextView) findViewById(R.id.textview_brightness);


        // Listener for seekbar object
        //Bug-fixed need to figure out how to change contrast of the picture on the fly
        //Bug-fixed 2nd time return to editing, it will throw an error
        seekBarContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textview_contrast.setText("Contrast: " + String.valueOf(progress));
                newBitmap = ImageProcessing.changeBitmapContrastBrightness(rawBitmap,
                        (float) progress/10f, (float) 5.12*(progress_brightness-50f));
                imageView.setImageBitmap(newBitmap);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                progress_contrast = seekBarContrast.getProgress();
            }
        });

        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textview_brightness.setText("Brightness: " + String.valueOf(progress));

                newBitmap = ImageProcessing.changeBitmapContrastBrightness(rawBitmap,
                        (float) progress_contrast/10f, (float) 5.12*(progress -50f));
                imageView.setImageBitmap(newBitmap);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                progress_brightness = (seekBarBrightness.getProgress());
            }
        });


        //listen for crop butten, send image to crop activity
        btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapStore.setBitmap(newBitmap);
                Intent intent = new Intent(EditPhotoActivity.this, CropActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_photo, menu);
        return true;
    }


    // handle action bar "next", when click, run startNext()
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_next) {
            startNext();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startNext() {
        scaledBitmap = Bitmap.createScaledBitmap(newBitmap, 640, 640, false);

        // save the modified picture
        File storagePath = new File(Environment.getExternalStorageDirectory()
                + "/DCIM/100ANDRO/");
        storagePath.mkdirs();

        File myImage = new File(storagePath, Long.toString(System.currentTimeMillis())
                + "_mod.jpg");
        try {
            FileOutputStream out = new FileOutputStream(myImage);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            out.flush();
            out.close();
        } catch(FileNotFoundException e) {
            Log.d("In Saving File", e + "");
        } catch(IOException e) {
            Log.d("In Saving File", e + "");
        }

        // Send the new image to the next post activity
        Intent intent = new Intent();
        intent.putExtra("post_img", myImage.toString());
        intent.setClass(EditPhotoActivity.this, PostActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
