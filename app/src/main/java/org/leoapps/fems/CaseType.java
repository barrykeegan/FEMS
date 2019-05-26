package org.leoapps.fems;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.util.Log;

@Entity(tableName = "casetypes")
public class CaseType {
    public static final String TAG = "CaseType";

    @PrimaryKey
    @NonNull
    public String Type;

    CaseType (String Type)
    {
        this.Type = Type;
    }

    //Will only be called on initial creation of DB
    public static CaseType[] populateCaseTypes() {
        Log.i(TAG, "In Populate Case Types");
        return new CaseType[]{
                new CaseType("Other"),
                new CaseType("Child Exploitation"),
                new CaseType("Fraud"),
                new CaseType("Sexual Offences"),
                new CaseType("Murder"),
                new CaseType("Terrorism and Offences Against the State"),
                new CaseType("Cyber Crime"),
                new CaseType("Theft"),
                new CaseType("Public Order"),
                new CaseType("Road Traffic"),
                new CaseType("Firearms and Offensive Weapons"),
                new CaseType("Criminal Damage"),
                new CaseType("Arson"),
                new CaseType("Drugs"),
                new CaseType("Assault"),
                new CaseType("Kidnapping"),
                new CaseType("Burglary"),
                new CaseType("Affray / Riot / Violent Disorder"),
                new CaseType("Robbery"),
                new CaseType("Harassment"),
                new CaseType("Domestic"),
                new CaseType("Aggravated Burglary")
        };
    }
}
