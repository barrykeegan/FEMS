package org.leoapps.fems;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CaseDetails extends AppCompatActivity {
    private static final String TAG = "CaseDetails";
    private int intCaseID;
    private String strCaseID;
    private TextView tvCaseID;
    private TextView tvLocalRef;
    private TextView tvOperation;
    private TextView tvExternalRef;
    private TextView tvCaseType;
    private TextView tvLocation;
    private TextView tvDate;

    private ImageView ivShareCase;
    private ImageView ivEditCase;
    private ImageView ivDeleteCase;

    private RecyclerView rvExhibits;

    private Case aCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        strCaseID = getIntent().getStringExtra("CaseID");

        FloatingActionButton fab = findViewById(R.id.fab_case_details);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CaseDetails.this, AddExhibit.class);
                intent.putExtra("CaseID", strCaseID);
                CaseDetails.this.startActivity(intent);
            }
        });

        intCaseID = Integer.parseInt(strCaseID);

        tvCaseID = findViewById(R.id.tv_case_details_case_id);
        tvLocalRef = findViewById(R.id.tv_case_details_reference);
        tvOperation = findViewById(R.id.tv_case_details_operation);
        tvExternalRef = findViewById(R.id.tv_case_details_external_reference);
        tvCaseType = findViewById(R.id.tv_case_details_type);
        tvLocation = findViewById(R.id.tv_case_details_location);
        tvDate = findViewById(R.id.tv_case_details_date);

        ivShareCase = findViewById(R.id.iv_case_details_share_case);
        ivEditCase = findViewById(R.id.iv_case_details_edit_case);
        ivDeleteCase = findViewById(R.id.iv_case_details_delete_case);

        rvExhibits = findViewById(R.id.rv_case_details_exhibits);

        aCase = Utils.database.caseDAO().getCase(intCaseID);

        tvCaseID.setText(strCaseID);
        tvLocalRef.setText(aCase.ReferenceID);
        tvOperation.setText(aCase.OperationName);
        tvExternalRef.setText(aCase.ExternalReferenceID);
        tvCaseType.setText(aCase.CaseType);
        tvLocation.setText(aCase.Location);
        tvDate.setText(aCase.CaseDate);

        ivShareCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CaseDetails.this, "Sharing case", Toast.LENGTH_LONG).show();
            }
        });

        ivDeleteCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCaseDelete(v);
            }
        });

        ivEditCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toUpdateCase = new Intent(v.getContext(), UpdateCase.class);
                toUpdateCase.putExtra("CaseID", strCaseID);
                toUpdateCase.putExtra("From", "CaseDetails");
                v.getContext().startActivity(toUpdateCase);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayExhibitList();
    }

    private void displayExhibitList()
    {
        Log.i(TAG, "In display exhibit List");
        List<Exhibit> exhibits = Utils.database.exhibitDAO().getExhibitsForCase(intCaseID);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvExhibits.setLayoutManager(layoutManager);

        if (exhibits.size() > 0)
        {
            RecyclerView.Adapter myAdapter = new ExhibitListAdapter(exhibits);
            rvExhibits.setAdapter(myAdapter);
        }
        else
        {
            List<NoContent> noContent = new ArrayList<>();
            noContent.add(new NoContent(
                    "There are no exhibits attached to this case yet",
                    "Click on + button below to begin adding exhibits."
            ));
            RecyclerView.Adapter myAdapter = new NoContentAdapter(noContent);
            rvExhibits.setAdapter(myAdapter);
        }
    }

    private void verifyCaseDelete(View v)
    {
        //https://stackoverflow.com/questions/5127407/how-to-implement-a-confirmation-yes-no-dialogpreference
        new AlertDialog.Builder(v.getContext())
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this case? If you press yes all exhibits and photos will also be removed.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Utils.database.caseDAO().deleteCase(intCaseID);
                        Intent toCaseList = new Intent(CaseDetails.this, MainActivity.class);
                        CaseDetails.this.startActivity(toCaseList);
                        CaseDetails.this.finish();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

}
