package org.leoapps.fems;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CaseDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addCase(Case newCase);

    @Query("SELECT * FROM cases WHERE ID = :ID")
    Case getCase(long ID);

    @Query("SELECT * FROM cases")
    List<Case> getAllCases();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCase(Case updatedCase);

    @Query("DELETE FROM cases WHERE ID = :ID")
    void deleteCase(long ID);

    @Query("DELETE FROM cases")
    void removeAllCases();
}
