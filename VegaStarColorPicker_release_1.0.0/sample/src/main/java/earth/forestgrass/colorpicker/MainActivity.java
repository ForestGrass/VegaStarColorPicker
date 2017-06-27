package earth.forestgrass.colorpicker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import earth.forestgrass.vegastarcolorpicker.VegaStarColorPicker;
import earth.forestgrass.vegastarcolorpicker.VegaStarColorPickerResultConst;
import earth.forestgrass.vegastarcolorpicker.VegaStarColorPickerSettingsConst;


public class MainActivity extends AppCompatActivity {

    private static final int VEGA_STAR_COLOR_PICKER_RESULT = 1;
    private String selectedColor = "#99009900";
    private boolean alphaSupport = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.showPickerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alphaSupport = false;
                showColorPicker();
            }
        });

        findViewById(R.id.showPickerWithAlphaButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alphaSupport = true;
                showColorPicker();
            }
        });
    }

    private void showColorPicker() {
        Intent intent = new Intent(this, VegaStarColorPicker.class);

        //All of this parameters is optional
        intent.putExtra(VegaStarColorPickerSettingsConst.CURRENT_COLOR_MESSAGE, "Current color:");
        intent.putExtra(VegaStarColorPickerSettingsConst.SELECTED_COLOR_MESSAGE, "Selected color:");
        intent.putExtra(VegaStarColorPickerSettingsConst.SELECT_COLOR_MESSAGE, "Please select color");
        intent.putExtra(VegaStarColorPickerSettingsConst.SELECT_ALPHA_MESSAGE, "Please select alpha");
        intent.putExtra(VegaStarColorPickerSettingsConst.OK_BUTTON_TEXT, "Save");
        intent.putExtra(VegaStarColorPickerSettingsConst.OK_BUTTON_COLOR, "#FFAED581");
        intent.putExtra(VegaStarColorPickerSettingsConst.OK_BUTTON_TEXT_COLOR, "#FF555555");
        intent.putExtra(VegaStarColorPickerSettingsConst.COLOR_ON_START, selectedColor);

        if (alphaSupport) {
            intent.putExtra(VegaStarColorPickerSettingsConst.ALPHA_SUPPORT, VegaStarColorPickerSettingsConst.ALPHA_SUPPORT_YES);
        } else {
            intent.putExtra(VegaStarColorPickerSettingsConst.ALPHA_SUPPORT, VegaStarColorPickerSettingsConst.ALPHA_SUPPORT_NO);
        }

        startActivityForResult(intent, VEGA_STAR_COLOR_PICKER_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VEGA_STAR_COLOR_PICKER_RESULT) {
            TextView info = (TextView) findViewById(R.id.info);
            if (resultCode == RESULT_OK) {
                int selectedValueAlpha = data.getIntExtra(VegaStarColorPickerResultConst.ALPHA, -1);
                int selectedValueRed = data.getIntExtra(VegaStarColorPickerResultConst.RED, -1);
                int selectedValueGreen = data.getIntExtra(VegaStarColorPickerResultConst.GREEN, -1);
                int selectedValueBlue = data.getIntExtra(VegaStarColorPickerResultConst.BLUE, -1);
                selectedColor = data.getStringExtra(VegaStarColorPickerResultConst.HEX_HTML);

                info.setText("Success select color.\n" +
                        VegaStarColorPickerResultConst.ALPHA + ": " + String.valueOf(selectedValueAlpha) + "\n" +
                        VegaStarColorPickerResultConst.RED + ": " + String.valueOf(selectedValueRed) + "\n" +
                        VegaStarColorPickerResultConst.GREEN + ": " + String.valueOf(selectedValueGreen) + "\n" +
                        VegaStarColorPickerResultConst.BLUE + ": " + String.valueOf(selectedValueBlue) + "\n" +
                        VegaStarColorPickerResultConst.HEX_HTML + ": " + String.valueOf(data.getStringExtra(VegaStarColorPickerResultConst.HEX_HTML)) + "\n" +
                        VegaStarColorPickerResultConst.HEX_CODE + ": " + String.valueOf(data.getStringExtra(VegaStarColorPickerResultConst.HEX_CODE))
                );
                ColorDrawable colorDrawable = new ColorDrawable(Color.argb(
                        selectedValueAlpha, selectedValueRed, selectedValueGreen, selectedValueBlue));
                ((ImageView) findViewById(R.id.resultPreviewColor)).setImageDrawable(colorDrawable);
            } else {
                info.setText("fail");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
}
