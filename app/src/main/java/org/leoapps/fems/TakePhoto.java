package org.leoapps.fems;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


//For this activity I have borrowed heavily from the android developer documents
//https://developer.android.com/guide/topics/media/camera.html#custom-camera

public class TakePhoto extends AppCompatActivity {
    private Camera camera;
    private CameraPreview mPreview;
    private FrameLayout flCameraPreview;
    private Button btnTakePhoto;

    private int exhibitID;
    private String exhibitRef;

    private static final float FOCUS_AREA_SIZE = 75f;
    public final static int CAMERA_PERMISSION = 1;
    public final static int STORAGE_PERMISSION = 1;
    protected static final int MEDIA_TYPE_IMAGE = 1;
    private final static String TAG = "TakePhoto - ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
        }


        exhibitID = getIntent().getIntExtra("ExhibitID", -1);
        exhibitRef = getIntent().getStringExtra("ExternalRef") + getIntent().getStringExtra("LocalRef");


        if (checkCameraHardware(this))
        {
            camera = getCameraInstance(this);

            //https://stackoverflow.com/questions/10605560/how-do-you-take-high-resolution-images-using-camera-takepicture/10605793#10605793
            Camera.Parameters params = setPreviewSize();
            params = setPictureSize(params);

            //params.setPictureSize(bestPreviewSize.width, bestPreviewSize.height);
            params.setRotation(90);


            //params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            //params.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
            //params.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);

            camera.setParameters(params);

            mPreview = new CameraPreview(this, camera);
            flCameraPreview = findViewById(R.id.fl_take_photo_camera_preview);
            flCameraPreview.addView(mPreview);

            flCameraPreview.setOnTouchListener( new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d("OnTouch: ", event.toString());
                    if (camera != null) {
                        Camera.Parameters params = camera.getParameters();
                        if (params.getMaxNumFocusAreas() > 0)
                        {
                            List<Camera.Area> focusAreas = new ArrayList<>();

                            Rect areaRect1 = new Rect(-100, -100, 100, 100);
                            focusAreas.add(new Camera.Area(areaRect1,1000));

                            params.setFocusAreas(focusAreas);
                        }
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        camera.setParameters(params);
                    }
                    return true;
                }
            });
        }
        else
        {
            Toast.makeText(this, "Camera Not Available", Toast.LENGTH_LONG).show();
        }

        btnTakePhoto =  findViewById(R.id.btn_take_photo_take_photo);
        btnTakePhoto.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        takePhoto();
                    }
                }
        );
    }

    private void takePhoto() {
        Camera.PictureCallback pictureCB = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera cam) {
                File picFile = getOutputMediaFile(MEDIA_TYPE_IMAGE, exhibitRef);
                if (picFile == null) {
                    Log.e(TAG, "Couldn't create media file; check storage permissions?");
                    return;
                }

                try {
                    FileOutputStream fos = new FileOutputStream(picFile);
                    fos.write(data);
                    fos.close();
                    String timeStamp = picFile.getAbsolutePath().split("_")[1];
                    Log.i("Timestamp", timeStamp);
                    //https://stackoverflow.com/questions/3674930/java-regex-meta-character-and-ordinary-dot
                    timeStamp = timeStamp.split("\\.")[0];
                    Log.i("Timestamp", timeStamp);

                    Photograph p = new Photograph(0, exhibitID, timeStamp,  picFile.getAbsolutePath());
                    Utils.database.photographDAO().addPhotograph(p);
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "File not found: " + e.getMessage());
                    e.getStackTrace();
                } catch (IOException e) {
                    Log.e(TAG, "I/O error writing file: " + e.getMessage());
                    e.getStackTrace();
                }
            }
        };
        camera.takePicture(null, null, pictureCB);
    }

    //https://developer.android.com/guide/topics/media/camera.html#custom-camera
    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(Context context){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Toast.makeText(context, "Error, could not open camera, exception thrown", Toast.LENGTH_LONG).show();
        }
        return c; // returns null if camera is unavailable
    }

    /** Create a file Uri for saving an image or video */
    /*private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }*/

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type, String filePrefix){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        String filename = filePrefix + "_" + timeStamp + ".jpg";
        File file = new File(Utils.appContext.getFilesDir(), filename);
        //File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
          //      Environment.DIRECTORY_PICTURES), "FEMS");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        /*if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("FEMS", "failed to create directory");
                return null;
            }
        }*/

        // Create a media file name
        /*
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                     filePrefix + timeStamp + ".jpg");
        }  else {
            return null;
        }*/

        return file;
    }

    Camera.Parameters setPreviewSize()
    {
        Camera.Parameters params = camera.getParameters();
        List<Camera.Size> previewSizeList = params.getSupportedPreviewSizes();

        Camera.Size bestPreviewSize = null;
        for(int i = 1; i < previewSizeList.size(); i++){
            if( previewSizeList.get(i).width == previewSizeList.get(i).height)
            {
                if (bestPreviewSize == null)
                {
                    bestPreviewSize = previewSizeList.get(i);
                }
                else if ((previewSizeList.get(i).width * previewSizeList.get(i).height) > (bestPreviewSize.width * bestPreviewSize.height))
                {
                    bestPreviewSize = previewSizeList.get(i);
                }
            }
        }

        if (bestPreviewSize == null)
        {
            bestPreviewSize = previewSizeList.get(0);

            for (int i = 1; i < previewSizeList.size(); i++) {
                if ((previewSizeList.get(i).width * previewSizeList.get(i).height) > (bestPreviewSize.width * bestPreviewSize.height)) {
                    bestPreviewSize = previewSizeList.get(i);
                }
            }
        }

        params.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
        return params;
    }

    Camera.Parameters setPictureSize(Camera.Parameters params)
    {
        List<Camera.Size> pictureSizeList = params.getSupportedPictureSizes();

        Camera.Size bestPictureSize = null;
        for(int i = 1; i < pictureSizeList.size(); i++){
            Log.i("PictureSize: ", "Width: " + pictureSizeList.get(i).width + ", Height: " + pictureSizeList.get(i).height);
            if( pictureSizeList.get(i).width == pictureSizeList.get(i).height)
            {
                if (bestPictureSize == null)
                {
                    bestPictureSize = pictureSizeList.get(i);
                }
                else if ((pictureSizeList.get(i).width * pictureSizeList.get(i).height) > (bestPictureSize.width * bestPictureSize.height))
                {
                    bestPictureSize = pictureSizeList.get(i);
                }
            }
        }

        if (bestPictureSize == null)
        {
            bestPictureSize = pictureSizeList.get(0);

            for (int i = 1; i < pictureSizeList.size(); i++) {
                if ((pictureSizeList.get(i).width * pictureSizeList.get(i).height) > (bestPictureSize.width * bestPictureSize.height)) {
                    bestPictureSize = pictureSizeList.get(i);
                }
            }
        }

        params.setPictureSize(bestPictureSize.width, bestPictureSize.height);
        return params;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera()
    {
        if (camera != null)
        {
            camera.release();
            camera = null;
        }
    }

}
