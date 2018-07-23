package com.example.ormil.battleships.Logic;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import java.util.List;

/**
 * Created by ormil on 11/01/2018.
 */
@Dao
public interface UserDao {

    @Query("SELECT * FROM users")
    List<User> getAll();

    @Query("SELECT * FROM users ORDER BY Score DESC LIMIT 10")
    List<User> getTopScorers();

    @TypeConverters(DifficultyConverter.class)
    @Query("SELECT * FROM users WHERE Difficulty LIKE :difficulty ORDER BY Score DESC LIMIT 10")
    List<User> getTopScorersDifficulty(eDifficulty difficulty);

    @Insert
    void insertUser(User user);

    @Delete
    void delete(User user);

}
