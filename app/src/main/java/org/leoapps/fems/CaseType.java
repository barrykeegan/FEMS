package org.leoapps.fems;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
//import android.util.Log;

@Entity(tableName = "casetypes")
public class CaseType {
    //public static final String TAG = "CaseType";

    @PrimaryKey
    @NonNull
    public String Type;

    CaseType (String Type)
    {
        this.Type = Type;
    }

    //Will only be called on initial creation of DB
    public static CaseType[] populateCaseTypes() {
        //Log.i(TAG, "In Populate Case Types");
        CaseType[] caseTypes = new CaseType[Utils.strCaseTypes.length];
        for (int i =0; i < Utils.strCaseTypes.length; i++)
        {
            caseTypes[i] = new CaseType(Utils.strCaseTypes[i]);
        }
        return caseTypes;
    }
}
