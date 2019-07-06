package org.leoapps.fems;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class PhotoDetails extends AppCompatActivity {
    //private static final String TAG = "PhotoDetails";

    private PhotoView pvPhoto;
    private TextView tvTimestamp;
    private TextView tvFileLocation;
    private Button btnDeletePhoto;
    private Button btnSharePhoto;

    private int photoID;
    private String fileLocation;
    private String timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_details);

        pvPhoto = findViewById(R.id.pv_photo_details_photo);
        tvTimestamp = findViewById(R.id.tv_photo_details_timestamp);
        tvFileLocation = findViewById(R.id.tv_photo_details_file_loc);

        btnDeletePhoto = findViewById(R.id.btn_photo_details_delete);
        btnSharePhoto = findViewById(R.id.btn_photo_details_share);

        photoID = getIntent().getIntExtra("ID", -1);
        fileLocation = getIntent().getStringExtra("Location");
        timestamp = getIntent().getStringExtra("Timestamp");

        btnDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePhoto(v);
            }
        });

        btnSharePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePhoto(v);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        pvPhoto.setImageBitmap(BitmapFactory.decodeFile(fileLocation));
        tvTimestamp.setText(timestamp);
        tvFileLocation.setText(fileLocation);
    }

    private void deletePhoto(View v)
    {
        new AlertDialog.Builder(v.getContext())
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this Photo?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Utils.database.photographDAO().deletePhotograph(photoID);
                        PhotoDetails.this.finish();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void sharePhoto(View v)
    {
        new AlertDialog.Builder(v.getContext())
                .setTitle("Confirm Share")
                .setMessage("Are you sure you want to send this Photo by email?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Utils.copyPhotoToExternal(fileLocation);






                        //new File(path).mkdirs();
                        /*Uri uri = Uri.parse(fileLocation);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/jpeg");
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "RE: Exhibit " +
                                fileLocation.substring(
                                        fileLocation.lastIndexOf('/') + 1,
                                        fileLocation.indexOf('_')
                                ));
                        intent.putExtra(Intent.EXTRA_TEXT, "Please find attached image...");
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        //Log.d(TAG, "uri: " + uri.toString() + ", fileLocation: " + fileLocation);
                        startActivity(Intent.createChooser(intent, "Share Image"));*/
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
}
