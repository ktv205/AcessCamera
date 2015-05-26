package com.example.krishnateja.accesscamera;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ImageView mImageView;
    static final int REQUEST_TAKE_PHOTO = 2;
    static final int REQUEST_PIC_IMAGE = 3;
    static final int REQUEST_VIDEO_CAPTURE = 4;
    String mCurrentPhotoPath;
    private VideoView mVideoView;
    private final static int REQEUST_PICK_VIDEO=5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button thumbnailButton = (Button) findViewById(R.id.pic_thumbnail_button);
        Button saveButton = (Button) findViewById(R.id.pic_save_button);
        Button picButton = (Button) findViewById(R.id.pic_gallery_button);
        Button videoPickButton=(Button)findViewById(R.id.video_pick_button);
        mVideoView = (VideoView) findViewById(R.id.videoView2);

        videoPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickVideoIntent();
            }
        });

        mImageView = (ImageView) findViewById(R.id.image_view);
        thumbnailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImageIntent();
            }
        });

        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picImageIntent();
            }
        });
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                intent.putExtra("path", (String) mImageView.getTag());
                MainActivity.this.startActivity(intent);
            }
        });
        Button recordVideo = (Button) findViewById(R.id.record_video_button);
        recordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakeVideoIntent();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    public void saveImageIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            Uri photoUri = null;
            try {
                photoUri = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoUri != null) {
                takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                        photoUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }


    }

    public void picImageIntent() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_PIC_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.d(TAG, "resultCode->" + resultCode);
            Log.d(TAG, "requestCode->" + requestCode);
            Log.d(TAG, "ResultOk->" + RESULT_OK);
            if (data == null) {
                Log.d(TAG, "Intent data is null");
            } else {
                Log.d(TAG, "intent data is not null");
                Bundle extras = data.getExtras();
                Uri uri = data.getData();
                if (uri != null) {
                    Log.d(TAG, uri.toString());
                } else {
                    Log.d(TAG, "uri ins null");
                }

                Bitmap imageBitmap = (Bitmap) extras.get("data");
                mImageView.setImageBitmap(imageBitmap);
            }
        }
        if (requestCode == REQUEST_PIC_IMAGE && resultCode == RESULT_OK) {
            Log.d(TAG, "resultCode->" + resultCode);
            Log.d(TAG, "requestCode->" + requestCode);
            Log.d(TAG, "ResultOk->" + RESULT_OK);
            if (data == null) {
                Log.d(TAG, "Intent data is null");
            } else {
                Uri selectedImageURI = data.getData();
                Log.d(TAG, selectedImageURI.toString());
                String tempPath = getPath(selectedImageURI);
                Log.d(TAG, "tempPath->" + tempPath);
                File imgFile = new File(tempPath);

                if (imgFile.exists()) {

                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    mImageView.setImageBitmap(myBitmap);
                    mImageView.setTag(tempPath);

                }
            }
        }

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.d(TAG, "resultCode->" + resultCode);
            Log.d(TAG, "requestCode->" + requestCode);
            Log.d(TAG, "ResultOk->" + RESULT_OK);
            Log.d(TAG, "image path->" + mCurrentPhotoPath);

            if (mCurrentPhotoPath != null) {
                File imgFile = new File(mCurrentPhotoPath);
                galleryAddPic();
                if (imgFile != null) {
                    Log.d(TAG, "iamge file is not null");
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    mImageView.setImageBitmap(myBitmap);
                    mImageView.setTag(mCurrentPhotoPath);
                } else {
                    Log.d(TAG, "image fileis null");
                }

            }
        }
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            Log.d(TAG, "here in video->" + videoUri.toString());


            mVideoView.setVideoURI(videoUri);

            MediaController mediaController = new
                    MediaController(this);
            mediaController.setAnchorView(mVideoView);
            mVideoView.setMediaController(mediaController);
            mVideoView.start();

        }

        if (requestCode == REQEUST_PICK_VIDEO && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            mVideoView.setVideoURI(videoUri);
            Log.d(TAG, "here in pick video->" + videoUri.toString());
            MediaController mediaController = new
                    MediaController(this);
            mediaController.setAnchorView(mVideoView);
            mVideoView.setMediaController(mediaController);
            mVideoView.start();

        }

    }

    public String getPath(Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    private Uri createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        Uri imageUri = Uri.fromFile(image);
        Log.d(TAG, imageUri.toString());

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d(TAG, mCurrentPhotoPath);
        return imageUri;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        Log.d(TAG, "contentUri->" + contentUri);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void pickVideoIntent(){
      Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent,REQEUST_PICK_VIDEO);

    }


}



