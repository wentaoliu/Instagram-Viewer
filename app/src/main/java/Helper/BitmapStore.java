package Helper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

/**
 * Created by raymond on 9/25/15.
 * This class provides global static methods handling Bitmap object storage and cropping the bitmap
 * from square circle.
 */

public class BitmapStore {
    static private Bitmap bmp = null;
    static private Bitmap rbmp = null;

    public static void setBitmap (Bitmap myBmp) {
        bmp = myBmp;
    }

    public static Bitmap getBitmap() {
        return bmp;
    }

    public static void setReceivedBitmap (Bitmap receivedBitmap) {
        rbmp = receivedBitmap;
    }

    public static Bitmap getReceivedBitmap () {
        return rbmp;
    }

    // universal method of cropping bitmap to circle
    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
}