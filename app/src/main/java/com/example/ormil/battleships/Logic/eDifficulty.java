package com.example.ormil.battleships.Logic;

import android.arch.persistence.room.TypeConverter;

/**
 * Created by ormil on 12/01/2018.
 */
public enum eDifficulty {
    EASY(0), NORMAL(1), HARD(2);

    private int value;

    eDifficulty(int value){
        this.value = value;
    }

    @TypeConverter
    public int getValue(){
        return value;
    }

    @TypeConverter
    public static eDifficulty fromInt(int i) {
        for (eDifficulty difficulty : eDifficulty.values()) {
            if (difficulty.getValue() == i) { return difficulty; }
        }
        return null;
    }
}
