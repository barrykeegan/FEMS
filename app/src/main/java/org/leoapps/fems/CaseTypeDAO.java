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

    @Query("SELECT * FROM casetypes WHERE Type = :Type")
    CaseType getCaseType(String Type);

    @Query("SELECT * FROM casetypes")
    List<CaseType> getAllCaseTypes();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCaseType(CaseType caseType);

    @Query("DELETE FROM casetypes WHERE Type = :Type")
    void deleteCaseType(String Type);

    @Query("DELETE FROM casetypes")
    void removeAllCaseTypes();
}
