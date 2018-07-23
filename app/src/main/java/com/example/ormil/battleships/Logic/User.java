package com.example.ormil.battleships.Logic;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

/**
 * Created by ormil on 11/01/2018.
 */
@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    private int user_id;

    @NonNull
    @ColumnInfo(name = "Name")
    private String name;

    @ColumnInfo(name = "Score")
    private int score;

    @ColumnInfo(name = "Lat")
    private double lat;

    @ColumnInfo(name = "Lon")
    private double lon;

    @ColumnInfo(name = "Difficulty")
    @NonNull
    @TypeConverters(DifficultyConverter.class)
    private eDifficulty difficulty;

    public User(){}

    @Override
    public String toString() {
        return "User{" +
                "user_id=" + user_id +
                ", name='" + name + '\'' +
                ", score=" + score +
                ", lat=" + lat +
                ", lon=" + lon +
                ", difficulty=" + difficulty +
                '}';
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @NonNull
    public eDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(@NonNull eDifficulty difficulty) {
        this.difficulty = difficulty;
    }
}
