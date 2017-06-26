package earth.forestgrass.vegastarcolorpicker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.Button;

/**
 * Created by Tatyana Klimanova on 25.06.2017.
 */

public class VegaStarColorPickerUtil {

    public static void correctXY(View view, Point point) {
        if (view == null ||  point == null) {
            Log.e(VegaStarColorPickerConst.LOG_TAG,
                    "correctXY view: " + view + " point: " + point);
            return;
        }

        int width = view.getWidth();
        int height = view.getHeight();

        if (width <= 0 || height <= 0) {
            Log.e(VegaStarColorPickerConst.LOG_TAG,
                    "correctXY view width: " + width + " height: " + height);
            return;
        }

        if (point.x < 0) {
            point.x = 0;
        }

        if (point.y < 0) {
            point.y = 0;
        }

        if (point.x >= width) {
            point.x = width -1;
        }

        if (point.y >= height) {
            point.y = height - 1;
        }
    }

    public static int getPixelFromPoint(View view, Point point) {
        if (view == null ||  point == null) {
            Log.e(VegaStarColorPickerConst.LOG_TAG,
                    "getPixelFromPoint view: " + view + " point: " + point);
            return 0;
        }

        // this is the important code :)
        // Without it the view will have a dimension of 0,0 and the bitmap will be null
        /*view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));*/

        Point pointCopy = new Point(point.x, point.y);

        int width = view.getWidth();
        int height = view.getHeight();

        if (width <= 0 || height <= 0) {
            Log.e(VegaStarColorPickerConst.LOG_TAG,
                    "getPixelFromPoint view width: " + width + " height: " + height);
            return 0;
        }

        correctXY(view, pointCopy);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        view.draw(c);

        return bitmap.getPixel(pointCopy.x, pointCopy.y);
    }

    public static Point getViewSize(View view) {
        Point result = new Point(0, 0);
        if (view == null) {
            return result;
        }

        // this is the important code :)
        // Without it the view will have a dimension of 0,0 and the bitmap will be null
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        result.x = view.getWidth();
        result.y = view.getHeight();

        return result;
    }

    public static void trySetViewColor(View view, String color) {
        if (view == null || color == null || color.length() == 0) {
            return;
        }
        try {
            view.setBackgroundColor(Color.parseColor(color));
        } catch (Exception ex) {
            Log.e(VegaStarColorPickerConst.LOG_TAG, ex.getMessage());
        }
    }

    public static void trySetButtonTextColor(Button button, String color) {
        if (button == null || color == null || color.length() == 0) {
            return;
        }
        try {
            button.setTextColor(Color.parseColor(color));
        } catch (Exception ex) {
            Log.e(VegaStarColorPickerConst.LOG_TAG, ex.getMessage());
        }
    }
}
