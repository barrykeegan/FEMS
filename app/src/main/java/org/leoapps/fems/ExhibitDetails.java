package org.leoapps.fems;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ExhibitDetails extends AppCompatActivity {
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
}
