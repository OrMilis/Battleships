package com.example.ormil.battleships.Logic;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

/**
 * Created by ormil on 11/01/2018.
 */
@Database(entities = User.class, version = 1, exportSchema = false)
public abstract class LeaderboardDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    private static LeaderboardDatabase instance;

    public static LeaderboardDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), LeaderboardDatabase.class, KeyManager.DATABASE_NAME).fallbackToDestructiveMigration().build();
        }
        return instance;
    }

    public static LeaderboardDatabase getInstance(){
        return instance;
    }

}
