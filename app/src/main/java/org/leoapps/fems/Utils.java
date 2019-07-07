package org.leoapps.fems;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Environment;
//import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

public class Utils{
    //public static final String TAG = "Utils:";

    //Used to hold a static reference to app context for use in certain section of code which
    //cannot obtain their own app context
    public static Context appContext;
    //Used to hold a static reference to the Room database for use throughout entire app
    public static AppDatabase database;

    public static String[] strCaseTypes = null;

    public static void clearDB()
    {
        database.photographDAO().removeAllPhotographs();
        database.exhibitDAO().removeAllExhibits();
        database.caseDAO().removeAllCases();
        database.caseTypeDAO().removeAllCaseTypes();
    }

    public static void initialiseUtilsProperties(Context applicationContext)
    {
        //Log.i(TAG, "In initialiseUtilsProperties - set up references");

        appContext = applicationContext;
        database = AppDatabase.getDatabase(appContext);
        List<CaseType> caseTypes = database.caseTypeDAO().getAllCaseTypes();
        if (caseTypes.size() < 1)
        {
            database.caseTypeDAO().addAllCaseTypes(CaseType.populateCaseTypes());
            caseTypes = database.caseTypeDAO().getAllCaseTypes();
        }
        strCaseTypes = new String[caseTypes.size()];

        for (int i = 0; i < caseTypes.size(); i++) {
            strCaseTypes[i] = caseTypes.get(i).Type;
        }
    }

    public static void copyPhotoToExternal(String fileLocation)
    {
        File source = new File(fileLocation);

        String destPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/FEMS/" + fileLocation.substring(fileLocation.lastIndexOf('/')+1);
        File destination = new File(destPath);

        //Log.d(TAG, "source: " + fileLocation + ", destination: " + destPath);

        try
        {
            FileUtils.copyFile(source, destination);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
