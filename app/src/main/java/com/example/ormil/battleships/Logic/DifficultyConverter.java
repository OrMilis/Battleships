package com.example.ormil.battleships.Logic;

import android.arch.persistence.room.TypeConverter;

/**
 * Created by ormil on 12/01/2018.
 */

public class DifficultyConverter {
    @TypeConverter
    public static int toInt(eDifficulty difficulty){
        return difficulty.ordinal();
    }

    @TypeConverter
    public static eDifficulty toDifficulty(int difficulty){
        return eDifficulty.values()[difficulty];
    }
}
