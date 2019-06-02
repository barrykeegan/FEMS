package org.leoapps.fems;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateCase extends AppCompatActivity  implements  DatePickerDialog.OnDateSetListener{
    private EditText etLocalReference;
    private EditText etExternalReference;
    private Spinner spnrCaseType;
    private EditText etCaseDate;
    private EditText etLocation;
    private EditText etOperation;
    private Button btnUpdateCase;
    private Button btnDiscard;
    private Button btnDatePicker;
    private String currentDate;
    private Case caseToUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_case);

        if(Utils.database == null)
        {
            Utils.initialiseUtilsProperties(getApplicationContext());
        }

        etLocalReference = findViewById(R.id.et_update_case_internal_ref);
        etExternalReference = findViewById(R.id.et_update_case_external_ref);
        spnrCaseType = findViewById(R.id.spnr_update_case_type);
        etCaseDate = findViewById(R.id.et_update_case_date);
        etLocation = findViewById(R.id.et_update_case_location);
        etOperation = findViewById(R.id.et_update_case_operation);
        btnUpdateCase = findViewById(R.id.btn_update_case_update);
        btnDiscard = findViewById(R.id.btn_update_case_discard);
        btnDatePicker = findViewById(R.id.btn_update_case_date_picker);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Utils.CaseTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrCaseType.setAdapter(adapter);

        currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        etCaseDate.setText(currentDate);

        caseToUpdate = Utils.database.caseDAO().getCase(
                Integer.parseInt(getIntent().getStringExtra("CaseID"))
        );

        etLocalReference.setText(caseToUpdate.ReferenceID);
        etExternalReference.setText(caseToUpdate.ExternalReferenceID);
        //https://stackoverflow.com/questions/11072576/set-selected-item-of-spinner-programmatically
        spnrCaseType.setSelection(
                ((ArrayAdapter)spnrCaseType.getAdapter()).getPosition(caseToUpdate.CaseType));
        etCaseDate.setText(caseToUpdate.CaseDate);
        etLocation.setText(caseToUpdate.Location);
        etOperation.setText(caseToUpdate.OperationName);

        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyDiscard();
            }
        });

        btnUpdateCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCase();
            }
        });

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    private void showDatePickerDialog()
    {
        String datePresent = etCaseDate.getText().toString();
        String[] splitDate = datePresent.split("/");
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, this, Integer.parseInt(splitDate[2]),
                Integer.parseInt(splitDate[1]) - 1,
                Integer.parseInt(splitDate[0])
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String strDay, strMonth, strYear;
        strYear = Integer.toString(year);
        month += 1;
        if(month < 10)
        {
            strMonth = "0" + month;
        }
        else
        {
            strMonth = Integer.toString(month);
        }

        if(dayOfMonth < 10)
        {
            strDay = "0" + dayOfMonth;
        }
        else
        {
            strDay = Integer.toString(dayOfMonth);
        }
        etCaseDate.setText(strDay + "/" + strMonth + "/" + strYear);
    }

    private void updateCase()
    {
        caseToUpdate.ReferenceID = etLocalReference.getText().toString();
        caseToUpdate.ExternalReferenceID = etExternalReference.getText().toString();
        caseToUpdate.CaseType = spnrCaseType.getSelectedItem().toString();
        caseToUpdate.CaseDate = etCaseDate.getText().toString();
        caseToUpdate.Location = etLocation.getText().toString();
        caseToUpdate.OperationName = etOperation.getText().toString();

        Utils.database.caseDAO().updateCase(caseToUpdate);
        backToPrevActivity();
    }

    private void verifyDiscard()
    {
        //if no changes have been made to case details, don't bother user with a prompt as to
        //whether they wish to discard, presume they entered by this activity by accident.
        if(
                etLocalReference.getText().toString().equals(caseToUpdate.ReferenceID) &&
                etExternalReference.getText().toString().equals(caseToUpdate.ExternalReferenceID) &&
                spnrCaseType.getSelectedItem().toString().equals(caseToUpdate.CaseType) &&
                etCaseDate.getText().toString().equals(caseToUpdate.CaseDate) &&
                etLocation.getText().toString().equals(caseToUpdate.Location) &&
                etOperation.getText().toString().equals(caseToUpdate.OperationName) )
        {
            backToPrevActivity();
        }
        else
        {
            //https://stackoverflow.com/questions/5127407/how-to-implement-a-confirmation-yes-no-dialogpreference
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Discard")
                    .setMessage("Do you really want to discard your changes?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            backToPrevActivity();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }

    }

    private void backToPrevActivity()
    {
        Intent intent;
        if(getIntent().getStringExtra("From").equals("CaseList"))
        {
            intent = new Intent(this, MainActivity.class);
        }
        else
        {
            intent = new Intent(this, CaseDetails.class);
            intent.putExtra("CaseID", Integer.toString(caseToUpdate.ID));
        }

        this.startActivity(intent);
        this.finish();
    }
}