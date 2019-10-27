package org.leoapps.fems;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.io.File;
import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "exhibits",
        indices = {@Index("CaseID")},
        foreignKeys =  @ForeignKey(entity = Case.class,
                                  parentColumns = "ID",
                                  childColumns = "CaseID",
                                  onDelete = CASCADE))
public class Exhibit {
    @PrimaryKey(autoGenerate = true)
    public int ID;
    public int CaseID;
    public String LocalExhibitID;
    public String ExternalExhibitID;
    public String Description;
    public String DateReceived;
    public String TimeReceived;
    public String ReceivedFrom;
    public String LocationReceived;
    public String DateFromCustody;
    public String ExhibitTo;

    public Exhibit(int ID, int CaseID, String LocalExhibitID, String ExternalExhibitID,
                   String Description, String DateReceived, String TimeReceived, String ReceivedFrom,
                   String LocationReceived, String DateFromCustody, String ExhibitTo)
    {
        this.ID = ID;
        this.CaseID = CaseID;
        this.LocalExhibitID = LocalExhibitID;
        this.ExternalExhibitID = ExternalExhibitID;
        this.Description = Description;
        this.DateReceived = DateReceived;
        this.TimeReceived = TimeReceived;
        this.ReceivedFrom = ReceivedFrom;
        this.LocationReceived = LocationReceived;
        this.DateFromCustody = DateFromCustody;
        this.ExhibitTo = ExhibitTo;
    }

    public void deleteExhibit()
    {
        List<Photograph> photosToDelete = Utils.database.photographDAO().getPhotographsForExhibit(ID);
        for (Photograph photoToDelete: photosToDelete) {
            photoToDelete.DeletPhotographFiles();
        }

        Utils.database.exhibitDAO().deleteExhibit(ID);
    }
}
