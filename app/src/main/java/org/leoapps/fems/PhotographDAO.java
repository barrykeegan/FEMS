package org.leoapps.fems;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface PhotographDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addPhotograph(Photograph photograph);

    @Query("SELECT * FROM photographs WHERE ID = :ID")
    Photograph getPhotograph(long ID);

    @Query("SELECT * FROM photographs")
    List<Photograph> getAllPhotographs();

    @Query("SELECT * FROM photographs WHERE ExhibitID = :ExhibitID")
    List<Photograph> getPhotographsForExhibit(long ExhibitID);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateExhibit(Photograph photograph);

    @Query("DELETE FROM photographs WHERE ID = :ID")
    void deletePhotograph(long ID);

    @Query("DELETE FROM photographs")
    void removeAllPhotographs();
}
