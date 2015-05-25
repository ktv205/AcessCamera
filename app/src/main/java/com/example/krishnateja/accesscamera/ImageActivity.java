package com.example.krishnateja.accesscamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

/**
 * Created by krishnateja on 5/25/2015.
 */
public class ImageActivity extends AppCompatActivity {
    ImageViewTouch mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ImageView imageView=(ImageView)findViewById(R.id.image_view);
        Intent intent=getIntent();
        String path=intent.getStringExtra("path");
        File imgFile = new File(path);
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        imageView.setImageBitmap(myBitmap);

    }

}
