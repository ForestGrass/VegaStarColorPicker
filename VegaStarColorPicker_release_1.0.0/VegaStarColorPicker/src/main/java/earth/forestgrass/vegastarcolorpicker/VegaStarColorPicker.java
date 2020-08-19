package earth.forestgrass.vegastarcolorpicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Tatyana Klimanova on 24.06.2017.
 *
 * Example
 * https://stfalcon.com/ru/blog/post/create-and-publish-your-Android-library
 */


public class VegaStarColorPicker extends Activity {

    private final int MAX_ALPHA_PERCENT_VALUE = 100;
    private final int GAP = 10;

    private int selectedValueAlpha = 255;
    private int selectedValueRed = 255;
    private int selectedValueGreen = 255;
    private int selectedValueBlue = 255;
    private Point currentPaletteCursorCoordinates = new Point(0, 0);
    private int result = RESULT_CANCELED;

    private boolean withAlphaSupport = false;
    private SeekBar alphaBar = null;
    private View paletteSingleColor;
    private ImageView paletteBaseColor;
    private ImageView paletteMultiColor;
    private ImageView resultPreviewColor;
    private View colorPickerCursor;
    private View paletteMultiColorPointer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vega_star_color_picker);

        init();
        draw();
        startListen();
    }

    private void init() {
        colorPickerCursor = findViewById(R.id.colorPickerCursor);
        paletteSingleColor = findViewById(R.id.paletteSingleColor);
        paletteMultiColor = (ImageView) findViewById(R.id.paletteMultiColor);
        paletteMultiColorPointer = (ImageView) findViewById(R.id.paletteMultiColorPointer);
        resultPreviewColor = (ImageView) findViewById(R.id.resultPreviewColor);

        paletteSingleColor.setDrawingCacheEnabled(true);
        paletteMultiColor.setDrawingCacheEnabled(true);

        Intent data = getIntent();
        if (data != null) {
            String textForControl = data.getStringExtra(VegaStarColorPickerSettingsConst.CURRENT_COLOR_MESSAGE);
            if (textForControl != null && textForControl.length() > 0) {
                ((TextView) findViewById(R.id.currentColorMsg)).setText(textForControl);
            }

            textForControl = data.getStringExtra(VegaStarColorPickerSettingsConst.SELECTED_COLOR_MESSAGE);
            if (textForControl != null && textForControl.length() > 0) {
                ((TextView) findViewById(R.id.selectedColorMsg)).setText(textForControl);
            }

            textForControl = data.getStringExtra(VegaStarColorPickerSettingsConst.SELECT_COLOR_MESSAGE);
            if (textForControl != null && textForControl.length() > 0) {
                ((TextView) findViewById(R.id.selectColorMsg)).setText(textForControl);
            }

            textForControl = data.getStringExtra(VegaStarColorPickerSettingsConst.SELECT_ALPHA_MESSAGE);
            if (textForControl != null && textForControl.length() > 0) {
                ((TextView) findViewById(R.id.selectAlphaMsg)).setText(textForControl);
            }

            textForControl = data.getStringExtra(VegaStarColorPickerSettingsConst.OK_BUTTON_TEXT);
            if (textForControl != null && textForControl.length() > 0) {
                ((TextView) findViewById(R.id.saveButton)).setText(textForControl);
            }

            VegaStarColorPickerUtil.trySetViewColor(findViewById(R.id.saveButton),
                    data.getStringExtra(VegaStarColorPickerSettingsConst.OK_BUTTON_COLOR));

            VegaStarColorPickerUtil.trySetButtonTextColor((Button) findViewById(R.id.saveButton),
                    data.getStringExtra(VegaStarColorPickerSettingsConst.OK_BUTTON_TEXT_COLOR));

            VegaStarColorPickerUtil.trySetViewColor(findViewById(R.id.currentColor),
                    data.getStringExtra(VegaStarColorPickerSettingsConst.COLOR_ON_START));

            withAlphaSupport = data.getIntExtra(VegaStarColorPickerSettingsConst.ALPHA_SUPPORT, 0) == 1;
        }

        initAlphaSelector();
    }

    private void initAlphaSelector() {
        if (withAlphaSupport) {
            alphaBar = (SeekBar) findViewById(R.id.alphaBar);
            alphaBar.setMax(MAX_ALPHA_PERCENT_VALUE);
            alphaBar.setProgress(MAX_ALPHA_PERCENT_VALUE);
        }
    }

    private void draw() {
        drawPalettes();
        findViewById(R.id.alphaBox).setVisibility(withAlphaSupport ? View.VISIBLE : View.GONE);
        showResultOnPreview(false);
    }

    private void drawPalettes() {
        paletteBaseColor = (ImageView) findViewById(R.id.paletteSingleColorLayer1);
        ImageView paletteSingleColorLayer2 = (ImageView) findViewById(R.id.paletteSingleColorLayer2);
        ImageView paletteSingleColorLayer3 = (ImageView) findViewById(R.id.paletteSingleColorLayer3);

        ColorDrawable drawable = new ColorDrawable(Color.RED);
        paletteBaseColor.setImageDrawable(drawable);

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] { Color.WHITE, Color.alpha(0) });
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        paletteSingleColorLayer2.setImageDrawable(gradientDrawable);

        gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                new int[] { Color.BLACK, Color.alpha(0) });
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        paletteSingleColorLayer3.setImageDrawable(gradientDrawable);

        gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] {
                        Color.RED,
                        Color.MAGENTA,
                        Color.BLUE,
                        Color.GREEN,
                        Color.YELLOW,
                        Color.RED
                });
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        paletteMultiColor.setImageDrawable(gradientDrawable);
    }

    private void startListen() {
        paletteSingleColor.setOnTouchListener(onSingleColorPaletteTouch);
        paletteMultiColor.setOnTouchListener(onMultiColorPaletteTouch);
        if (withAlphaSupport) {
            alphaBar.setOnSeekBarChangeListener(onAlphaValueChange);
        }
        findViewById(R.id.saveButton).setOnClickListener(onClickHandle);
    }

    private SeekBar.OnSeekBarChangeListener onAlphaValueChange = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                selectedValueAlpha = (int)(255 * (progress / 100.0));

                updateSelectedColors();
                showResultOnPreview(true);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    View.OnTouchListener onSingleColorPaletteTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            currentPaletteCursorCoordinates.set((int)event.getX(), (int)event.getY());

            VegaStarColorPickerUtil.correctXY(paletteSingleColor, currentPaletteCursorCoordinates);
            colorPickerCursor.setX(currentPaletteCursorCoordinates.x - 5);
            colorPickerCursor.setY(currentPaletteCursorCoordinates.y - 5);
            showResultOnPreview(true);

            return true;
        }
    };

    View.OnTouchListener onMultiColorPaletteTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            Point coordinates = new Point((int)event.getX(), (int)event.getY());
            VegaStarColorPickerUtil.correctXY(paletteMultiColor, coordinates);
            int pixel = VegaStarColorPickerUtil.getPixelFromPoint(paletteMultiColor, coordinates);

            int red = Color.red(pixel);
            int green = Color.green(pixel);
            int blue = Color.blue(pixel);
            ColorDrawable drawable = new ColorDrawable(Color.rgb(red, green, blue));
            paletteBaseColor.setImageDrawable(drawable);

            paletteMultiColorPointer.setX(coordinates.x - 12);

            showResultOnPreview(true);

            return true;
        }
    };

    View.OnClickListener onClickHandle = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            closeAndReturnResult();
        }
    };

    private void showResultOnPreview(boolean onUserActions) {
        if (onUserActions) {
            updateSelectedColors();
        }
        resultPreviewColor.setBackgroundColor(Color.argb(selectedValueAlpha, selectedValueRed, selectedValueGreen, selectedValueBlue));
    }

    private void updateSelectedColors() {
        int pixel = VegaStarColorPickerUtil.getPixelFromPoint(paletteSingleColor, currentPaletteCursorCoordinates);
        selectedValueRed = Color.red(pixel);
        selectedValueGreen = Color.green(pixel);
        selectedValueBlue = Color.blue(pixel);
    }

    private void closeAndReturnResult() {
        result = RESULT_OK;

        Intent data = new Intent();
        data.putExtra(VegaStarColorPickerResultConst.ALPHA, selectedValueAlpha);
        data.putExtra(VegaStarColorPickerResultConst.RED, selectedValueRed);
        data.putExtra(VegaStarColorPickerResultConst.GREEN, selectedValueGreen);
        data.putExtra(VegaStarColorPickerResultConst.BLUE, selectedValueBlue);
        data.putExtra(VegaStarColorPickerResultConst.HEX_HTML, String.format("#%02x%02x%02x%02x",
                selectedValueAlpha, selectedValueRed, selectedValueGreen, selectedValueBlue));
        data.putExtra(VegaStarColorPickerResultConst.HEX_CODE, String.format("0x%02x%02x%02x%02x",
                selectedValueAlpha, selectedValueRed, selectedValueGreen, selectedValueBlue));
        setResult(result, data);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
