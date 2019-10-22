package org.leoapps.fems;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

import static android.arch.persistence.room.ForeignKey.SET_NULL;

@Entity(tableName = "cases",
        indices = {@Index("CaseType")},
        foreignKeys =  @ForeignKey(entity = CaseType.class,
        parentColumns = "Type",
        childColumns = "CaseType",
        onDelete = SET_NULL))
public class Case {
    @PrimaryKey(autoGenerate = true)
    public int ID;
    public String ReferenceID;
    public String ExternalReferenceID;
    public String CaseType;
    public String CaseDate;
    public String Location;
    public String OperationName;

    public Case (int ID, String ReferenceID, String ExternalReferenceID, String CaseType,
                  String CaseDate, String Location, String OperationName)
    {
        this.ID = ID;
        this.ReferenceID = ReferenceID;
        this.ExternalReferenceID = ExternalReferenceID;
        this.CaseType = CaseType;
        this.CaseDate = CaseDate;
        this.Location = Location;
        this.OperationName = OperationName;
    }

    public void deleteCase()
    {
        List<Exhibit> exhibitsToDelete = Utils.database.exhibitDAO().getExhibitsForCase(ID);
        for (Exhibit exhibitToDelete: exhibitsToDelete)
        {
            exhibitToDelete.deleteExhibit();
        }

        Utils.database.caseDAO().deleteCase(ID);
    }

}
