package org.leoapps.fems;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ExhibitDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addExhibit(Exhibit exhibit);

    @Query("SELECT * FROM exhibits WHERE ID = :ID")
    Exhibit getExhibit(long ID);

    @Query("SELECT * FROM exhibits")
    List<Exhibit> getAllExhibits();

    @Query("SELECT * FROM exhibits WHERE CaseID = :CaseID")
    List<Exhibit> getExhibitsForCase(long CaseID);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateExhibit(Exhibit exhibit);

    @Query("DELETE FROM exhibits WHERE ID = :ID")
    void deleteExhibit(long ID);

    @Query("DELETE FROM exhibits")
    void removeAllExhibits();
}
