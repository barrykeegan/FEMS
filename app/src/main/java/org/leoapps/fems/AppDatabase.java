package org.leoapps.fems;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.Executors;

@Database(entities = {CaseType.class, Case.class, Exhibit.class, Photograph.class}, version=1, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = "AppDatabase";
    private static volatile AppDatabase INSTANCE;
    public abstract CaseTypeDAO caseTypeDAO();
    public abstract CaseDAO caseDAO();
    public abstract ExhibitDAO exhibitDAO();
    public abstract PhotographDAO photographDAO();

    public static AppDatabase getDatabase( final Context context)
    {
        Log.i(TAG, "In AppDatabase, Before Instance null check");
        if (INSTANCE == null)
        {
            Log.i(TAG, "In AppDatabase, Instance was null");
            INSTANCE = buildDatabase(context);
        }
        Log.i(TAG, "In AppDatabase, After Instance null check");
        return INSTANCE;
    }

    public static void destroyInstance() { INSTANCE = null; }

    //Inspiration drawn from:
    //https://android.jlelse.eu/pre-populate-room-database-6920f9acc870
    public static AppDatabase buildDatabase(final Context context)
    {
        Log.i(TAG, "In AppDatabase.buildDatabase, Before building");
        return Room.databaseBuilder(context, AppDatabase.class, "FEMSDB")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Log.i(TAG, "In AppDatabase.buildDatabase callback, inside onCreate, before populating");
                        Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                            @Override
                            public void run() {

                                getDatabase(context).caseTypeDAO().addAllCaseTypes(CaseType.populateCaseTypes());

                            }
                        });
                        Log.i(TAG, "In AppDatabase.buildDatabase, inside onCreate, after populating");
                    }

                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db);
                        Log.i(TAG, "In AppDatabase.buildDatabase callback, inside onOpen");
                    }
                })
                .build();
    }
}
