package org.leoapps.fems;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

@Entity(tableName = "casetypes")
public class CaseType {
    public static final String TAG = "CaseType";

    @PrimaryKey(autoGenerate = true)
    public int ID;
    public String CaseType;

    CaseType (int ID, String CaseType)
    {
        this.ID = ID;
        this.CaseType = CaseType;
    }

    //Will only be called on initial creation of DB
    public static CaseType[] populateCaseTypes() {
        Log.i(TAG, "In Populate Case Types");
        return new CaseType[]{
                new CaseType(0, "Other"),
                new CaseType(0, "Public Order"),
                new CaseType(0, "Road Traffic"),
                new CaseType(0, "Theft"),
                new CaseType(0, "Firearms and Offensive Weapons"),
                new CaseType(0, "Criminal Damage"),
                new CaseType(0, "Fraud"),
                new CaseType(0, "Arson"),
                new CaseType(0, "Drugs"),
                new CaseType(0, "Assault"),
                new CaseType(0, "Kidnapping"),
                new CaseType(0, "Burglary"),
                new CaseType(0, "Cyber Crime"),
                new CaseType(0, "Affray / Riot / Violent Disorder"),
                new CaseType(0, "Robbery"),
                new CaseType(0, "Harassment"),
                new CaseType(0, "Domestic"),
                new CaseType(0, "Aggravated Burglary"),
                new CaseType(0, "Terrorism and Offences Against the State"),
                new CaseType(0, "Sexual Offences"),
                new CaseType(0, "Murder")
        };
    }
}
