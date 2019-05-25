package org.leoapps.fems;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "photographs",
        indices = {@Index("ExhibitID")},
        foreignKeys =  @ForeignKey(entity = Exhibit.class,
        parentColumns = "ID",
        childColumns = "ExhibitID",
        onDelete = CASCADE))
public class Photograph {
    @PrimaryKey(autoGenerate = true)
    public int ID;
    public int ExhibitID;
    public String DateTimeTaken;
    public String FileLocation;

    public Photograph(int ID, int ExhibitID, String DateTimeTaken, String FileLocation)
    {
        this.ID = ID;
        this.ExhibitID = ExhibitID;
        this.DateTimeTaken = DateTimeTaken;
        this.FileLocation = FileLocation;
    }
}
