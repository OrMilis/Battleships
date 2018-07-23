package com.example.ormil.battleships.Logic;

import java.util.Random;

/**
 * Created by ormil on 09/12/2017.
 */

public class Hunt extends State {

    private Board referencedBoard;
    private Random pos;

    public Hunt(Board board){
        this.referencedBoard = board;
         pos = new Random();
    }

    //Overriding the State abstract method
    //Returns available position randomly;
    @Override
    public int getPositionToHit() {
        return referencedBoard.availableSpots.keyAt(pos.nextInt(referencedBoard.availableSpots.size()));
    }
}
