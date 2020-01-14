package org.leoapps.fems;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateExhibit extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private EditText etLocalExhibitID;
    private EditText etExternalExhibitID;
    private EditText etDescription;
    private EditText etDateReceived;
    private EditText etTimeReceived;
    private EditText etLocationReceived;
    private EditText etReceivedFrom;
    private EditText etDateReturned;
    private EditText etReturnedTo;

    private Button btnDateReceivedPicker;
    private Button btnTimeReceivedPicker;
    private Button btnDateReturnedPicker;
    private Button btnUpdateExhibit;
    private Button btnDiscard;

    private String currentDate;
    private String strCaseID;
    private int intCaseID;

    private String strExhibitID;
    private int intExhibitID;

    private Exhibit exhibit;

    //variable to store which date is being picked. 1 is for dateReceived, 2 is for dateReturned
    public int dateInQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_exhibit);

        if(Utils.database == null)
        {
            Utils.initialiseUtilsProperties(getApplicationContext());
        }

        strExhibitID = getIntent().getStringExtra("ExhibitID");
        intExhibitID = Integer.parseInt(strExhibitID);

        etLocalExhibitID = findViewById(R.id.et_update_exhibit_local_id);
        etExternalExhibitID = findViewById(R.id.et_update_exhibit_external_id);
        etDescription = findViewById(R.id.et_update_exhibit_description);
        etDateReceived = findViewById(R.id.et_update_exhibit_date_received);
        etTimeReceived = findViewById(R.id.et_update_exhibit_time_received);
        etLocationReceived = findViewById(R.id.et_update_exhibit_location_received);
        etReceivedFrom = findViewById(R.id.et_update_exhibit_received_from);
        etDateReturned = findViewById(R.id.et_update_exhibit_date_returned);
        etReturnedTo = findViewById(R.id.et_update_exhibit_returned_to);

        btnDateReceivedPicker = findViewById(R.id.btn_update_exhibit_date_received_picker);
        btnTimeReceivedPicker = findViewById(R.id.btn_update_exhibit_time_received_picker);
        btnDateReturnedPicker = findViewById(R.id.btn_update_exhibit_date_returned_picker);
        btnUpdateExhibit = findViewById(R.id.btn_update_exhibit_update);
        btnDiscard = findViewById(R.id.btn_update_exhibit_discard);

        exhibit = Utils.database.exhibitDAO().getExhibit(intExhibitID);

        intCaseID = exhibit.CaseID;
        strCaseID = Integer.toString(intCaseID);

        etLocalExhibitID.setText(exhibit.LocalExhibitID);
        etExternalExhibitID.setText(exhibit.ExternalExhibitID);
        etDescription.setText(exhibit.Description);
        etDateReceived.setText(exhibit.DateReceived);
        etTimeReceived.setText(exhibit.TimeReceived);
        etLocationReceived.setText(exhibit.LocationReceived);
        etReceivedFrom.setText(exhibit.ReceivedFrom);
        etDateReturned.setText(exhibit.DateFromCustody);
        etReturnedTo.setText(exhibit.ExhibitTo);

        currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        btnDateReceivedPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateInQuestion = 1;
                showDatePickerDialog(etDateReceived);
            }
        });

        btnTimeReceivedPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        btnDateReturnedPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateInQuestion =2;
                showDatePickerDialog(etDateReturned);
            }
        });

        btnUpdateExhibit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateExhibit();
            }
        });

        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyDiscard();
            }
        });
    }

    private void showDatePickerDialog(EditText et)
    {
        String datePresent = et.getText().toString();
        String[] splitDate;
        if( !datePresent.isEmpty()) {
            splitDate = datePresent.split("/");
        }
        else
        {
            splitDate = currentDate.split("/");
        }
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

        if(dateInQuestion ==1)
        {
            etDateReceived.setText(strDay + "/" + strMonth + "/" + strYear);
        }
        else if(dateInQuestion ==2)
        {
            etDateReturned.setText(strDay + "/" + strMonth + "/" + strYear);
        }
    }

    private void showTimePickerDialog()
    {
        String timePresent = etTimeReceived.getText().toString();
        String[] splitTime = null;

        if(!timePresent.isEmpty())
        {
            splitTime = timePresent.split(":");
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this, this,
                splitTime!=null? Integer.parseInt(splitTime[0]) : 0,
                splitTime!=null? Integer.parseInt(splitTime[1]) : 0,
                true
        );
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String strHour = hourOfDay < 10 ? "0" + hourOfDay : Integer.toString(hourOfDay);
        String strMinute = minute < 10 ? "0" + minute : Integer.toString(minute);
        etTimeReceived.setText(strHour + ":" + strMinute);
    }

    private void verifyDiscard()
    {
        //if no changes have been made to default case details, don't bother user with a prompt as to
        //whether they wish to discard, presume they entered by this activity by accident.
        /*if(etLocalReference.getText().toString().isEmpty() &&
                etExternalReference.getText().toString().isEmpty() &&
                spnrCaseType.getSelectedItem().toString().equals("Other") &&
                etCaseDate.getText().toString().equals(currentDate) &&
                etLocation.getText().toString().isEmpty() &&
                etOperation.getText().toString().isEmpty() )
        {
            backToCaseList();
        }
        else
        {*/
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
        //}

    }

    private void updateExhibit()
    {
        exhibit.LocalExhibitID = etLocalExhibitID.getText().toString();
        exhibit.ExternalExhibitID = etExternalExhibitID.getText().toString();
        exhibit.Description = etDescription.getText().toString();
        exhibit.DateReceived = etDateReceived.getText().toString();
        exhibit.TimeReceived = etTimeReceived.getText().toString();
        exhibit.LocationReceived = etLocationReceived.getText().toString();
        exhibit.ReceivedFrom = etReceivedFrom.getText().toString();
        exhibit.DateFromCustody = etDateReturned.getText().toString();
        exhibit.ExhibitTo = etReturnedTo.getText().toString();
        Utils.database.exhibitDAO().updateExhibit(exhibit);
        backToPrevActivity();
    }

    private void backToPrevActivity()
    {
        this.finish();
    }
}
