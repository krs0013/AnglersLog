package com.starwood.anglerslong;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;

/**
 * Created by kennystreit on 8/2/15.
 */
public class Popup extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_window);

        byte[] logImageByte = getIntent().getByteArrayExtra("log_image");
        Bitmap logImage = BitmapFactory.decodeByteArray(logImageByte, 0, logImageByte.length);

        /* First set the display height/width of the popup window */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        getWindow().setLayout((int) (screenWidth * 0.8), (int) (screenHeight * 0.8));


        /* Next, set the actual content */

        // Drink Image
        ImageView logImageView = (ImageView) findViewById(R.id.log_image);
        logImageView.setImageBitmap(logImage);
    }

}
