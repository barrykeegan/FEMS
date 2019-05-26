package org.leoapps.fems;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.service.autofill.RegexValidator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class AddCase extends AppCompatActivity  implements  DatePickerDialog.OnDateSetListener{
    private EditText etLocalReference;
    private EditText etExternalReference;
    private Spinner spnrCaseType;
    private EditText etCaseDate;
    private EditText etLocation;
    private EditText etOperation;
    private Button btnAddCase;
    private Button btnDiscard;
    private Button btnDatePicker;
    private TextView tvValidationMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_case);

        etLocalReference = findViewById(R.id.et_add_case_internal_ref);
        etExternalReference = findViewById(R.id.et_add_case_external_ref);
        spnrCaseType = findViewById(R.id.spnr_add_case_type);
        etCaseDate = findViewById(R.id.et_add_case_date);
        etLocation = findViewById(R.id.et_add_case_location);
        etOperation = findViewById(R.id.et_add_case_operation);
        btnAddCase = findViewById(R.id.btn_add_case_add);
        btnDiscard = findViewById(R.id.btn_add_case_discard);
        btnDatePicker = findViewById(R.id.btn_add_case_date_picker);
        tvValidationMessage = findViewById(R.id.tv_validation_message);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Utils.CaseTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrCaseType.setAdapter(adapter);

        String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        etCaseDate.setText(currentDate);

        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToCaseList();
            }
        });

        btnAddCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Case newCase = new Case(
                        0,
                        etLocalReference.getText().toString(),
                        etExternalReference.getText().toString(),
                        spnrCaseType.getSelectedItem().toString(),
                        etCaseDate.getText().toString(),
                        etLocation.getText().toString(),
                        etOperation.getText().toString()
                );
                Utils.database.caseDAO().addCase(newCase);
                backToCaseList();
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

    private void backToCaseList()
    {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        this.finish();
    }
}
