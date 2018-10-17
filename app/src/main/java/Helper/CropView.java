package Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.support.v7.widget.AppCompatImageView;

/**
 * Crop image class
 */
public class CropView extends AppCompatImageView {
    //TODO: need to fix the bug when cropping the image, the actual image size is smaller than it shows
    Paint paint = new Paint();
    private static Point leftTop, rightBottom, center, previous;

    private static final int DRAG= 0;
    private static final int LEFT= 1;
    private static final int TOP= 2;
    private static final int RIGHT= 3;
    private static final int BOTTOM= 4;

    // Adding parent class constructors
    public CropView(Context context) {
        super(context);
        initCropView();
    }

    public CropView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initCropView();
    }

    public CropView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initCropView();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas)
    {
        super.onDraw(canvas);
        if(leftTop.equals(0, 0))
            resetPoints();
        canvas.drawRect(leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, paint);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        int eventaction = event.getAction();
        switch (eventaction) {
            case MotionEvent.ACTION_DOWN:
                previous.set((int)event.getX(), (int)event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                if(isActionInsideRectangle(event.getX(), event.getY())) {
                    adjustRectangle((int)event.getX(), (int)event.getY());
                    invalidate(); // redraw rectangle
                    previous.set((int)event.getX(), (int)event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
                previous = new Point();
                break;
        }
        return true;
    }

    // set initial crop view
    private void initCropView() {
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        leftTop = new Point();
        rightBottom = new Point();
        center = new Point();
        previous = new Point();
    }

    public void resetPoints() {
        center.set(getWidth()/2, getHeight()/2);
        int initial_size = 300;
        leftTop.set((getWidth()- initial_size)/2,(getHeight()- initial_size)/2);
        rightBottom.set(leftTop.x+ initial_size, leftTop.y+ initial_size);
    }

    private static boolean isActionInsideRectangle(float x, float y) {
        int buffer = 10;
        return ((x >= (leftTop.x - buffer)) && (x <= (rightBottom.x + buffer)) && (y >= (leftTop.y - buffer)) &&
                (y <= (rightBottom.y + buffer)));
    }

    private boolean isInImageRange(PointF point) {
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        getImageMatrix().getValues(f);

        // Calculate the scaled dimensions
        int imageScaledWidth = Math.round(getDrawable().getIntrinsicWidth() * f[Matrix.MSCALE_X]);
        int imageScaledHeight = Math.round(getDrawable().getIntrinsicHeight() * f[Matrix.MSCALE_Y]);

        return ((point.x >= (center.x - (imageScaledWidth / 2))) && (point.x <= (center.x + (imageScaledWidth / 2))) &&
                (point.y >= (center.y - (imageScaledHeight / 2))) && (point.y <= (center.y + (imageScaledHeight / 2))));
    }

    private void adjustRectangle(int x, int y) {
        int movement;
        switch(getAffectedSide(x,y)) {
            case LEFT:
                movement = x-leftTop.x;
                if(isInImageRange(new PointF(leftTop.x+movement,leftTop.y+movement)))
                    leftTop.set(leftTop.x+movement,leftTop.y+movement);
                break;
            case TOP:
                movement = y-leftTop.y;
                if(isInImageRange(new PointF(leftTop.x+movement,leftTop.y+movement)))
                    leftTop.set(leftTop.x+movement,leftTop.y+movement);
                break;
            case RIGHT:
                movement = x-rightBottom.x;
                if(isInImageRange(new PointF(rightBottom.x+movement,rightBottom.y+movement)))
                    rightBottom.set(rightBottom.x+movement,rightBottom.y+movement);
                break;
            case BOTTOM:
                movement = y-rightBottom.y;
                if(isInImageRange(new PointF(rightBottom.x+movement,rightBottom.y+movement)))
                    rightBottom.set(rightBottom.x+movement,rightBottom.y+movement);
                break;
            case DRAG:
                movement = x-previous.x;
                int movementY = y-previous.y;
                if(isInImageRange(new PointF(leftTop.x+movement,leftTop.y+movementY)) &&
                        isInImageRange(new PointF(rightBottom.x+movement,
                                rightBottom.y+movementY))) {
                    leftTop.set(leftTop.x+movement,leftTop.y+movementY);
                    rightBottom.set(rightBottom.x+movement,rightBottom.y+movementY);
                }
                break;
        }
    }

    private static int getAffectedSide(float x, float y) {
        int buffer = 10;
        if(x>=(leftTop.x-buffer)&&x<=(leftTop.x+buffer))
            return LEFT;
        else if(y>=(leftTop.y-buffer)&&y<=(leftTop.y+buffer))
            return TOP;
        else if(x>=(rightBottom.x-buffer)&&x<=(rightBottom.x+buffer))
            return RIGHT;
        else if(y>=(rightBottom.y-buffer)&&y<=(rightBottom.y+buffer))
            return BOTTOM;
        else
            return DRAG;
    }

    public Bitmap getCroppedImage() {
        BitmapDrawable drawable = (BitmapDrawable)getDrawable();
        float x = leftTop.x-center.x+(drawable.getBitmap().getWidth()/2);
        float y = leftTop.y-center.y+(drawable.getBitmap().getHeight()/2);
        Log.d("getCroppedImage-----", String.valueOf(x));
        Bitmap cropped = Bitmap.createBitmap(drawable.getBitmap(), (int) x, (int) y, (int) rightBottom.x -
                leftTop.x, (int) rightBottom.y - (int) leftTop.y);
        return cropped;
    }
}
