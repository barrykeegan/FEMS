package org.leoapps.fems;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.List;

public class Utils{
    public static final String TAG = "Utils:";

    //Used to hold a static reference to app context for use in certain section of code which
    //cannot obtain their own app context
    public static Context appContext;
    //Used to hold a static reference to the Room database for use throughout entire app
    public static AppDatabase database;

    public static String[] CaseTypes = null;

    public static void clearDB()
    {
        database.photographDAO().removeAllPhotographs();
        database.exhibitDAO().removeAllExhibits();
        database.caseDAO().removeAllCases();
        database.caseTypeDAO().removeAllCaseTypes();
    }

    public static void initialiseUtilsProperties(Context applicationContext)
    {
        Log.i(TAG, "In initialiseUtilsProperties - set up references");

        appContext = applicationContext;
        database = AppDatabase.getDatabase(appContext);
        List<CaseType> caseTypes = database.caseTypeDAO().getAllCaseTypes();
        CaseTypes = new String[caseTypes.size()];

        for (int i = 0; i < caseTypes.size(); i++) {
            CaseTypes[i] = caseTypes.get(i).Type;
        }
    }

}
