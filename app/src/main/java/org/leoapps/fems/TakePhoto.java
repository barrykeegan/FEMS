package org.leoapps.fems;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
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

public class TakePhoto extends AppCompatActivity implements View.OnClickListener {
    public final static int CAMERA_PERMISSION = 1;
    public final static int STORAGE_PERMISSION = 1;
    protected static final int MEDIA_TYPE_IMAGE = 1;

    private static final int TOP_LEFT = 0;
    private static final int TOP = 1;
    private static final int TOP_RIGHT = 2;
    private static final int LEFT = 3;
    private static final int CENTRE = 4;
    private static final int RIGHT = 5;
    private static final int BOTTOM_LEFT = 6;
    private static final int BOTTOM = 7;
    private static final int BOTTOM_RIGHT = 8;

    private Button ibTopLeft;
    private Button ibTop;
    private Button ibTopRight;
    private Button ibLeft;
    private Button ibCentre;
    private Button ibRight;
    private Button ibBottomLeft;
    private Button ibBottom;
    private Button ibBottomRight;

    private CheckBox cbTS;
    private CheckBox cbEID;

    private TextView tvOverlay;

    private int currPosition = TOP_LEFT;
    private boolean displayOverlay = true;
    private boolean displayTimestamp = true;
    private boolean displayExhibitID = true;

    private Camera camera;
    private CameraPreview mPreview;
    private FrameLayout flCameraPreview;
    private Button btnTakePhoto;

    private ConstraintLayout csPreview;

    private int exhibitID;
    private String filePrefix;
    private String exhibitRef;


    //private final static String TAG = "TakePhoto - ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Utils.database == null)
        {
            Utils.initialiseUtilsProperties(getApplicationContext());
        }

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
        String extRef = getIntent().getStringExtra("ExternalRef");
        String localRef = getIntent().getStringExtra("LocalRef");

        filePrefix = extRef  + localRef;
        if(!extRef.isEmpty() && !localRef.isEmpty())
        {
            exhibitRef = extRef + "_" + localRef;
        }
        else
        {
            exhibitRef = extRef + localRef;
        }


        ibTopLeft = findViewById(R.id.ib_top_left);
        ibTop = findViewById(R.id.ib_top);
        ibTopRight = findViewById(R.id.ib_top_right);
        ibLeft = findViewById(R.id.ib_left);
        ibCentre = findViewById(R.id.ib_centre);
        ibRight = findViewById(R.id.ib_right);
        ibBottomLeft = findViewById(R.id.ib_bottom_left);
        ibBottom = findViewById(R.id.ib_bottom);
        ibBottomRight = findViewById(R.id.ib_bottom_right);

        ibTopLeft.setOnClickListener(this);
        ibTop.setOnClickListener(this);
        ibTopRight.setOnClickListener(this);
        ibLeft.setOnClickListener(this);
        ibCentre.setOnClickListener(this);
        ibRight.setOnClickListener(this);
        ibBottomLeft.setOnClickListener(this);
        ibBottom.setOnClickListener(this);
        ibBottomRight.setOnClickListener(this);

        cbTS = findViewById(R.id.cb_timestamp);
        cbEID = findViewById(R.id.cb_exhibit_id);
        cbTS.setChecked(true);
        cbEID.setChecked(true);

        tvOverlay = findViewById(R.id.tv_overlay);

        csPreview = findViewById(R.id.cs_photo_preview);



        btnTakePhoto =  findViewById(R.id.btn_take_photo_take_photo);
        btnTakePhoto.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        takePhoto();
                    }
                }
        );
        setPositionChoosersOff();
        if(displayOverlay)
        {
            setOverlayText();
            setOverlayConstraints();
            setPositionChooserOn();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
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
                    //Log.d("OnTouch: ", event.toString());
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
    }

    public byte[] addOverlayText(byte[] data, String textToAdd)
    {
        //http://www.blog.nathanhaze.com/android-text-picture-camera-app/
        Bitmap dest = BitmapFactory.decodeByteArray(data, 0, data.length).copy(Bitmap.Config.ARGB_8888, true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Canvas canvas = new Canvas(dest);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setShadowLayer(2.0f, 1.0f, 1.0f, Color.BLACK);
        int picHeight = dest.getHeight();
        paint.setTextSize(picHeight * .02f);

        //set x co-ord
        int x;
        switch (currPosition)
        {
            case TOP_LEFT: case LEFT: case BOTTOM_LEFT:
                x = 100;
                break;
            case TOP: case CENTRE: case BOTTOM:
                x = (dest.getWidth() / 2) - 500;
                break;
            case TOP_RIGHT: case RIGHT: case BOTTOM_RIGHT:
                x = dest.getWidth()  - 1200;
                break;
            default:
                x = 100;
                break;
        }

        //set x co-ord
        int y;
        switch (currPosition)
        {
            case TOP_LEFT: case TOP: case TOP_RIGHT:
                y = 150;
                break;
            case LEFT: case CENTRE: case RIGHT:
                y = (dest.getHeight() / 2) + 75;
                break;
            case BOTTOM_LEFT: case BOTTOM: case BOTTOM_RIGHT:
                y = dest.getHeight()  - 100;
                break;
            default:
                y = 150;
                break;
        }

        canvas.drawText(textToAdd, x, y, paint);

        dest.compress(Bitmap.CompressFormat.JPEG,100, stream);
        return stream.toByteArray();
    }

    private void takePhoto() {
        Camera.PictureCallback pictureCB = new Camera.PictureCallback() {
            public void onPictureTaken(final byte[] data, Camera cam) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Handler handler = new Handler(Looper.getMainLooper());

                        File[] picFiles = getOutputMediaFile(MEDIA_TYPE_IMAGE, filePrefix);
                        if (picFiles[0] == null || picFiles[1] == null) {
                            //Log.e(TAG, "Couldn't create media files; check storage permissions?");
                            return;
                        }

                        try {
                            FileOutputStream fosLarge = new FileOutputStream(picFiles[0]);
                            FileOutputStream fosThumb = new FileOutputStream(picFiles[1]);
                            String timeStamp;
                            if(picFiles[0].getAbsolutePath().indexOf('_') != -1) {
                                timeStamp = picFiles[0].getAbsolutePath().split("_")[1];
                            }
                            else
                            {
                                timeStamp = picFiles[0].getAbsolutePath().substring(picFiles[0].getAbsolutePath().lastIndexOf('/') + 1);
                            }
                            //Log.i("Timestamp", timeStamp);
                            //https://stackoverflow.com/questions/3674930/java-regex-meta-character-and-ordinary-dot
                            timeStamp = timeStamp.split("\\.")[0];
                            //Log.i("Timestamp", timeStamp);

                            byte[] toSave = null;
                            if(displayOverlay)
                            {
                                String textToAdd = "";
                                if(displayExhibitID)
                                {
                                    textToAdd += exhibitRef;
                                }
                                if(displayTimestamp)
                                {
                                    if(!textToAdd.isEmpty())
                                    {
                                        textToAdd += "-";
                                    }



                                    textToAdd += timeStamp.substring(0,4) + "/" + timeStamp.substring(4,6) +"/" +timeStamp.substring(6, 8) + "-";
                                    textToAdd += timeStamp.substring(9,11) + ":" + timeStamp.substring(11,13) +":" +timeStamp.substring(13);
                                }
                                toSave = addOverlayText(data, textToAdd);
                            }
                            if(toSave == null)
                            {
                                fosLarge.write(data);
                                Bitmap thumb = Bitmap.createScaledBitmap(
                                        BitmapFactory.decodeByteArray(data, 0 , data.length), 90, 90, false);
                                ByteArrayOutputStream thumbBaos = new ByteArrayOutputStream();
                                thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbBaos);
                                fosThumb.write(thumbBaos.toByteArray());
                            }
                            else
                            {
                                fosLarge.write(toSave);
                                Bitmap thumb = Bitmap.createScaledBitmap(
                                        BitmapFactory.decodeByteArray(toSave, 0 , toSave.length), 90, 90, false);
                                ByteArrayOutputStream thumbBaos = new ByteArrayOutputStream();
                                thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbBaos);
                                fosThumb.write(thumbBaos.toByteArray());
                            }
                            fosLarge.close();
                            fosThumb.close();


                            Photograph p = new Photograph(0, exhibitID, timeStamp,  picFiles[0].getAbsolutePath());
                            Utils.database.photographDAO().addPhotograph(p);
                        } catch (FileNotFoundException e) {
                            //Log.e(TAG, "File not found: " + e.getMessage());
                            e.getStackTrace();
                        } catch (IOException e) {
                            //Log.e(TAG, "I/O error writing file: " + e.getMessage());
                            e.getStackTrace();
                        }
                    }
                }).start();


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
    private static File[] getOutputMediaFile(int type, String filePrefix){
        String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        String largefilename = filePrefix.isEmpty() ? timeStamp + ".jpg" : filePrefix + "_" + timeStamp + ".jpg";
        String thumbfilename = "thumb" + largefilename;
        File[] files = new File[2];
        files[0] = new File(Utils.appContext.getFilesDir(), largefilename);
        files[1] = new File(Utils.appContext.getFilesDir(), thumbfilename);

        return files;
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
            //Log.i("PictureSize: ", "Width: " + pictureSizeList.get(i).width + ", Height: " + pictureSizeList.get(i).height);
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

    private void setPositionChoosersOff()
    {
        ibTopLeft.getBackground().setTint(getResources().getColor(R.color.buttonDefault));
        ibTop.getBackground().setTint(getResources().getColor(R.color.buttonDefault));
        ibTopRight.getBackground().setTint(getResources().getColor(R.color.buttonDefault));
        ibLeft.getBackground().setTint(getResources().getColor(R.color.buttonDefault));
        ibCentre.getBackground().setTint(getResources().getColor(R.color.buttonDefault));
        ibRight.getBackground().setTint(getResources().getColor(R.color.buttonDefault));
        ibBottomLeft.getBackground().setTint(getResources().getColor(R.color.buttonDefault));
        ibBottom.getBackground().setTint(getResources().getColor(R.color.buttonDefault));
        ibBottomRight.getBackground().setTint(getResources().getColor(R.color.buttonDefault));

    }

    private void setPositionChooserOn()
    {
        switch (currPosition)
        {
            case TOP_LEFT:
                ibTopLeft.getBackground().setTint(getResources().getColor(R.color.colorSecondaryAccent));
                break;
            case TOP:
                ibTop.getBackground().setTint(getResources().getColor(R.color.colorSecondaryAccent));
                break;
            case TOP_RIGHT:
                ibTopRight.getBackground().setTint(getResources().getColor(R.color.colorSecondaryAccent));
                break;
            case LEFT:
                ibLeft.getBackground().setTint(getResources().getColor(R.color.colorSecondaryAccent));
                break;
            case CENTRE:
                ibCentre.getBackground().setTint(getResources().getColor(R.color.colorSecondaryAccent));
                break;
            case RIGHT:
                ibRight.getBackground().setTint(getResources().getColor(R.color.colorSecondaryAccent));
                break;
            case BOTTOM_LEFT:
                ibBottomLeft.getBackground().setTint(getResources().getColor(R.color.colorSecondaryAccent));
                break;
            case BOTTOM:
                ibBottom.getBackground().setTint(getResources().getColor(R.color.colorSecondaryAccent));
                break;
            case BOTTOM_RIGHT:
                ibBottomRight.getBackground().setTint(getResources().getColor(R.color.colorSecondaryAccent));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        //Log.i(TAG, "Pre Click Processing:");
        //reportFlags();

        if( v == ibTopLeft && displayOverlay)
        {
            setPositionChoosersOff();
            currPosition = TOP_LEFT;
            setPositionChooserOn();
        }
        if( v == ibTop  && displayOverlay)
        {
            setPositionChoosersOff();
            currPosition = TOP;
            setPositionChooserOn();
        }
        if( v == ibTopRight  && displayOverlay)
        {
            setPositionChoosersOff();
            currPosition = TOP_RIGHT;
            setPositionChooserOn();
        }
        if( v == ibLeft  && displayOverlay)
        {
            setPositionChoosersOff();
            currPosition = LEFT;
            setPositionChooserOn();
        }
        if( v == ibCentre  && displayOverlay)
        {
            setPositionChoosersOff();
            currPosition = CENTRE;
            setPositionChooserOn();
        }
        if( v == ibRight  && displayOverlay)
        {
            setPositionChoosersOff();
            currPosition = RIGHT;
            setPositionChooserOn();
        }
        if( v == ibBottomLeft && displayOverlay)
        {
            setPositionChoosersOff();
            currPosition = BOTTOM_LEFT;
            setPositionChooserOn();
        }
        if( v == ibBottom && displayOverlay)
        {
            setPositionChoosersOff();
            currPosition = BOTTOM;
            setPositionChooserOn();
        }
        if( v == ibBottomRight && displayOverlay)
        {
            setPositionChoosersOff();
            currPosition = BOTTOM_RIGHT;
            setPositionChooserOn();
        }
        setOverlayConstraints();

        //Log.i(TAG, "Post Click Processing:");
        //reportFlags();
    }

    /*private void reportFlags()
    {
        Log.i(TAG,
                "displayOverlay: " + displayOverlay +
                        ", displayTimestamp: " + displayTimestamp +
                        ", displayExhibitID: " + displayExhibitID +
                        ", currPosition: " + currPosition);
    }*/

    public void onCheckBoxClicked(View v)
    {
        CheckBox cb = (CheckBox)v;


        if (cb == cbTS)
        {
            if(cb.isChecked())
            {
                displayTimestamp = true;
                if(!displayOverlay)
                {
                    displayOverlay = true;
                    setPositionChooserOn();
                    setOverlayConstraints();
                    tvOverlay.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                displayTimestamp = false;
                if (!displayExhibitID)
                {
                    displayOverlay = false;
                    tvOverlay.setVisibility(View.INVISIBLE);
                    setPositionChoosersOff();
                }
            }
        }

        if (cb == cbEID)
        {
            if(cb.isChecked())
            {
                displayExhibitID = true;
                if(!displayOverlay)
                {
                    displayOverlay = true;
                    setPositionChooserOn();
                    setOverlayConstraints();
                    tvOverlay.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                displayExhibitID = false;
                if (!displayTimestamp)
                {
                    displayOverlay = false;
                    tvOverlay.setVisibility(View.INVISIBLE);
                    setPositionChoosersOff();
                }
            }
        }
        setOverlayText();
    }

    public void setOverlayText()
    {
        String overlayText = "";
        //Log.d(TAG, "setOverlayText: '" +  exhibitRef + "'");
        if(displayExhibitID)
        {
            overlayText += exhibitRef;
        }
        if(displayTimestamp)
        {
            if(!overlayText.isEmpty())
            {
                overlayText += "-";
            }
            overlayText += "Timestamp";
        }
        tvOverlay.setText(overlayText);
    }

    public void setOverlayConstraints()
    {
        ConstraintSet cs = new ConstraintSet();
        cs.clone(csPreview);
        switch (currPosition)
        {
            case TOP_LEFT:
                //Top
                cs.connect(R.id.tv_overlay,ConstraintSet.TOP,R.id.fl_take_photo_camera_preview,ConstraintSet.TOP,8);
                //Left
                cs.connect(R.id.tv_overlay,ConstraintSet.START,R.id.fl_take_photo_camera_preview,ConstraintSet.START,8);
                //Right
                cs.clear(R.id.tv_overlay, ConstraintSet.END);
                //Bottom
                cs.clear(R.id.tv_overlay, ConstraintSet.BOTTOM);
                break;
            case TOP:
                //Top
                cs.connect(R.id.tv_overlay,ConstraintSet.TOP,R.id.fl_take_photo_camera_preview,ConstraintSet.TOP,8);
                //Left
                cs.connect(R.id.tv_overlay,ConstraintSet.START,R.id.fl_take_photo_camera_preview,ConstraintSet.START,8);
                //Right
                cs.connect(R.id.tv_overlay,ConstraintSet.END,R.id.fl_take_photo_camera_preview,ConstraintSet.END,8);
                //Bottom
                cs.clear(R.id.tv_overlay, ConstraintSet.BOTTOM);
                break;
            case TOP_RIGHT:
                //Top
                cs.connect(R.id.tv_overlay,ConstraintSet.TOP,R.id.fl_take_photo_camera_preview,ConstraintSet.TOP,8);
                //Left
                cs.clear(R.id.tv_overlay, ConstraintSet.START);
                //Right
                cs.connect(R.id.tv_overlay,ConstraintSet.END,R.id.fl_take_photo_camera_preview,ConstraintSet.END,8);
                //Bottom
                cs.clear(R.id.tv_overlay, ConstraintSet.BOTTOM);
                break;
            case LEFT:
                //Top
                cs.connect(R.id.tv_overlay,ConstraintSet.TOP,R.id.fl_take_photo_camera_preview,ConstraintSet.TOP,8);
                //Left
                cs.connect(R.id.tv_overlay,ConstraintSet.START,R.id.fl_take_photo_camera_preview,ConstraintSet.START,8);
                //Right
                cs.clear(R.id.tv_overlay, ConstraintSet.END);
                //Bottom
                cs.connect(R.id.tv_overlay,ConstraintSet.BOTTOM,R.id.fl_take_photo_camera_preview,ConstraintSet.BOTTOM,8);
                break;
            case CENTRE:
                //Top
                cs.connect(R.id.tv_overlay,ConstraintSet.TOP,R.id.fl_take_photo_camera_preview,ConstraintSet.TOP,8);
                //Left
                cs.connect(R.id.tv_overlay,ConstraintSet.START,R.id.fl_take_photo_camera_preview,ConstraintSet.START,8);
                //Right
                cs.connect(R.id.tv_overlay,ConstraintSet.END,R.id.fl_take_photo_camera_preview,ConstraintSet.END,8);
                //Bottom
                cs.connect(R.id.tv_overlay,ConstraintSet.BOTTOM,R.id.fl_take_photo_camera_preview,ConstraintSet.BOTTOM,8);
                break;
            case RIGHT:
                //Top
                cs.connect(R.id.tv_overlay,ConstraintSet.TOP,R.id.fl_take_photo_camera_preview,ConstraintSet.TOP,8);
                //Left
                cs.clear(R.id.tv_overlay, ConstraintSet.START);
                //Right
                cs.connect(R.id.tv_overlay,ConstraintSet.END,R.id.fl_take_photo_camera_preview,ConstraintSet.END,8);
                //Bottom
                cs.connect(R.id.tv_overlay,ConstraintSet.BOTTOM,R.id.fl_take_photo_camera_preview,ConstraintSet.BOTTOM,8);
                break;
            case BOTTOM_LEFT:
                //Top
                cs.clear(R.id.tv_overlay, ConstraintSet.TOP);
                //Left
                cs.connect(R.id.tv_overlay,ConstraintSet.START,R.id.fl_take_photo_camera_preview,ConstraintSet.START,8);
                //Right
                cs.clear(R.id.tv_overlay, ConstraintSet.END);
                //Bottom
                cs.connect(R.id.tv_overlay,ConstraintSet.BOTTOM,R.id.fl_take_photo_camera_preview,ConstraintSet.BOTTOM,8);
                break;
            case BOTTOM:
                //Top
                cs.clear(R.id.tv_overlay, ConstraintSet.TOP);
                //Left
                cs.connect(R.id.tv_overlay,ConstraintSet.START,R.id.fl_take_photo_camera_preview,ConstraintSet.START,8);
                //Right
                cs.connect(R.id.tv_overlay,ConstraintSet.END,R.id.fl_take_photo_camera_preview,ConstraintSet.END,8);
                //Bottom
                cs.connect(R.id.tv_overlay,ConstraintSet.BOTTOM,R.id.fl_take_photo_camera_preview,ConstraintSet.BOTTOM,8);
                break;
            case BOTTOM_RIGHT:
                //Top
                cs.clear(R.id.tv_overlay, ConstraintSet.TOP);
                //Left
                cs.clear(R.id.tv_overlay, ConstraintSet.START);
                //Right
                cs.connect(R.id.tv_overlay,ConstraintSet.END,R.id.fl_take_photo_camera_preview,ConstraintSet.END,8);
                //Bottom
                cs.connect(R.id.tv_overlay,ConstraintSet.BOTTOM,R.id.fl_take_photo_camera_preview,ConstraintSet.BOTTOM,8);
                break;
        }
        cs.applyTo(csPreview);


    }
}
