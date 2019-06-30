package org.leoapps.fems;

import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;

import org.w3c.dom.Text;

public class PhotoDetails extends AppCompatActivity {
    private PhotoView pvPhoto;
    private TextView tvTimestamp;
    private TextView tvFileLocation;

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

        photoID = getIntent().getIntExtra("ID", -1);
        fileLocation = getIntent().getStringExtra("Location");
        timestamp = getIntent().getStringExtra("Timestamp");
    }

    @Override
    protected void onResume() {
        super.onResume();
        pvPhoto.setImageBitmap(BitmapFactory.decodeFile(fileLocation));
        tvTimestamp.setText(timestamp);
        tvFileLocation.setText(fileLocation);
    }
}
