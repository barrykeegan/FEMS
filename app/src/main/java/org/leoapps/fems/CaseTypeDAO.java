package org.leoapps.fems;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CaseTypeDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addCaseType(CaseType caseType);

    @Insert
    void addAllCaseTypes(CaseType... caseTypes);

    @Query("SELECT * FROM casetypes WHERE ID = :ID")
    CaseType getCaseType(long ID);

    @Query("SELECT * FROM casetypes")
    List<CaseType> getAllCaseTypes();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCaseType(CaseType caseType);

    @Query("DELETE FROM casetypes WHERE ID = :ID")
    void deleteCaseType(long ID);

    @Query("DELETE FROM casetypes")
    void removeAllCaseTypes();
}
