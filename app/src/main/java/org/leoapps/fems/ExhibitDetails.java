package org.leoapps.fems;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ExhibitDetails extends AppCompatActivity {
    private static final String TAG = "ExhibitDetails";
    private final int THUMBSIZE = 64;

    private TextView tvLocalRef;
    private TextView tvExternalRef;
    private TextView tvDescription;
    private TextView tvDateReceived;
    private TextView tvTimeReceived;
    private TextView tvLocationReceived;
    private TextView tvReceivedFrom;
    private TextView tvDateReturned;
    private TextView tvReturnedTo;

    private ImageView ivShareExhibit;
    private ImageView ivEditExhibit;
    private ImageView ivDeleteExhibit;

    private Exhibit exhibit;

    private RecyclerView rvPhotos;

    private String strExhibitID;
    private int intExhibitID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_details);

        tvLocalRef = findViewById(R.id.tv_exhibit_details_local_reference);
        tvExternalRef = findViewById(R.id.tv_exhibit_details_external_reference);
        tvDescription = findViewById(R.id.tv_exhibit_details_description);
        tvDateReceived = findViewById(R.id.tv_exhibit_details_date_received);
        tvTimeReceived = findViewById(R.id.tv_exhibit_details_time_received);
        tvLocationReceived = findViewById(R.id.tv_exhibit_details_location_received);
        tvReceivedFrom = findViewById(R.id.tv_exhibit_details_received_from);
        tvDateReturned = findViewById(R.id.tv_exhibit_details_date_returned);
        tvReturnedTo = findViewById(R.id.tv_exhibit_details_returned_to);

        ivShareExhibit = findViewById(R.id.iv_exhibit_details_share_exhibit);
        ivEditExhibit = findViewById(R.id.iv_exhibit_details_edit_exhibit);
        ivDeleteExhibit = findViewById(R.id.iv_exhibit_details_delete_exhibit);

        strExhibitID = getIntent().getStringExtra("ExhibitID");
        intExhibitID = Integer.parseInt(strExhibitID);

        rvPhotos = findViewById(R.id.rv_exhibit_details_photos);

        FloatingActionButton fab = findViewById(R.id.fab_add_photo);

        exhibit = Utils.database.exhibitDAO().getExhibit(intExhibitID);

        tvLocalRef.setText(exhibit.LocalExhibitID);
        tvExternalRef.setText(exhibit.ExternalExhibitID);
        tvDescription.setText(exhibit.Description);
        tvDateReceived.setText(exhibit.DateReceived);
        tvTimeReceived.setText(exhibit.TimeReceived);
        tvLocationReceived.setText(exhibit.LocationReceived);
        tvReceivedFrom.setText(exhibit.ReceivedFrom);
        tvDateReturned.setText(exhibit.DateFromCustody);
        tvReturnedTo.setText(exhibit.ExhibitTo);

        //set for backgroundTint backwards compatibility as per:
        //https://stackoverflow.com/questions/27735890/lollipops-backgroundtint-has-no-effect-on-a-button/29756195#29756195
        fab.setBackgroundTintList(new ColorStateList(new int[][]{new int[0]}, new int[]{R.color.colorSecondary}));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TakePhoto.class);
                intent.putExtra("ExhibitID", exhibit.ID);
                intent.putExtra("ExternalRef", exhibit.ExternalExhibitID);
                intent.putExtra("LocalRef", exhibit.LocalExhibitID);
                view.getContext().startActivity(intent);
            }
        });

        ivShareExhibit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Sharing Exhibit", Toast.LENGTH_LONG).show();

            }
        });

        ivEditExhibit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Editing Exhibit", Toast.LENGTH_LONG).show();
                Intent toUpdateExhibit = new Intent(v.getContext(), UpdateExhibit.class);
                toUpdateExhibit.putExtra("From", "ExhibitDetails");
                toUpdateExhibit.putExtra("ExhibitID", Integer.toString(exhibit.ID));
                v.getContext().startActivity(toUpdateExhibit);

            }
        });

        ivDeleteExhibit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Deleting Exhibit", Toast.LENGTH_LONG).show();
                verifyExhibitDelete(v);
            }
        });

        displayPhotoList();

    }

    @Override
    protected void onResume() {
        super.onResume();
        displayPhotoList();

    }

    private void verifyExhibitDelete(View v)
    {
        //https://stackoverflow.com/questions/5127407/how-to-implement-a-confirmation-yes-no-dialogpreference
        new AlertDialog.Builder(v.getContext())
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this exhibit? If you press yes this exhibit and all associated photos will also be removed.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        String caseID = Integer.toString(exhibit.CaseID);
                        Utils.database.exhibitDAO().deleteExhibit(exhibit.ID);
                        Intent toCaseDetails = new Intent(ExhibitDetails.this, CaseDetails.class);
                        toCaseDetails.putExtra("CaseID", caseID);
                        ExhibitDetails.this.startActivity(toCaseDetails);
                        ExhibitDetails.this.finish();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void displayPhotoList()
    {
        Log.i(TAG, "In display exhibit List");
        List<Photograph> photographs = Utils.database.photographDAO().getPhotographsForExhibit(exhibit.ID);



        if (photographs.size() > 0)
        {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
            rvPhotos.setLayoutManager(layoutManager);
            RecyclerView.Adapter myAdapter = new PhotographListAdapter(photographs);
            rvPhotos.setAdapter(myAdapter);
        }
        else
        {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            rvPhotos.setLayoutManager(layoutManager);
            List<NoContent> noContent = new ArrayList<>();
            noContent.add(new NoContent(
                    "There are no exhibits attached to this case yet",
                    "Click on + button below to begin adding exhibits."
            ));
            RecyclerView.Adapter myAdapter = new NoContentAdapter(noContent);
            rvPhotos.setAdapter(myAdapter);
        }
    }
}
