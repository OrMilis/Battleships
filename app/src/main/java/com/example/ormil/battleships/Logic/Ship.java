package com.example.ormil.battleships.Logic;

/**
 * Created by ormil on 06/12/2017.
 */

public class Ship {
    private int startPosition;
    private int size;
    private boolean isVertical;
    private int numOfHits;

    public static final int MAX_SHIP_SIZE = 4;

    public Ship(int size, boolean isVertical){
        this.size = size;
        this.isVertical = isVertical;
        this.numOfHits = 0;
    }

    //Count how many times ship got hit.
    public void takeHit(){
        if(!isDead())
            numOfHits++;
    }

    //Checking if ship is dead.
    public boolean isDead(){
        if(numOfHits >= size)
            return true;
        return false;
    }

    public int getSize(){
        return size;
    }

    public boolean isVertical(){
        return isVertical;
    }

    public int getStartPos(){
        return startPosition;
    }

    public void setIsVertical(boolean isVertical){
        this.isVertical = isVertical;
    }

    public void setStartPos(int position){
        this.startPosition = position;
    }
}
