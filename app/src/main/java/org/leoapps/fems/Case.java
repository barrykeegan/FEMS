package org.leoapps.fems;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.SET_NULL;

@Entity(tableName = "cases",
        indices = {@Index("CaseTypeID")},
        foreignKeys =  @ForeignKey(entity = CaseType.class,
        parentColumns = "ID",
        childColumns = "CaseTypeID",
        onDelete = SET_NULL))
public class Case {
    @PrimaryKey(autoGenerate = true)
    public int ID;
    public String ReferenceID;
    public String ExternalReferenceID;
    public int CaseTypeID;
    public String CaseDate;
    public String Location;
    public String OperationName;

    public Case (int ID, String ReferenceID, String ExternalReferenceID, int CaseTypeID,
                  String CaseDate, String Location, String OperationName)
    {
        this.ID = ID;
        this.ReferenceID = ReferenceID;
        this.ExternalReferenceID = ExternalReferenceID;
        this.CaseTypeID = CaseTypeID;
        this.CaseDate = CaseDate;
        this.Location = Location;
        this.OperationName = OperationName;
    }
}
