package com.example.ormil.battleships.Logic;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ormil on 09/12/2017.
 */

public class Target extends State {

    private final int NORTH_VALUE   = -10;
    private final int SOUTH_VALUE   = 10;
    private final int EAST_VALUE    = 1;
    private final int WEST_VALUE    = -1;

    private enum eDirections {NORTH, SOUTH, EAST, WEST, NONE}

    private ArrayList<eDirections> possibleDirections = new ArrayList<>();

    private Board referencedBoard;
    private boolean isVertical;
    private boolean isHorizontal;
    private int firstHitPos;
    private int currentPos;
    private Random random;

    public Target(Board board){
        referencedBoard = board;
        random = new Random();
    }

    //Overriding the State abstract method
    //Returns the position by direction. If no position available (StackOverflow occurred) returns random available position.
    @Override
    public int getPositionToHit() {
        eDirections direction = getAvailableDirection();
        switch(direction){
            case NORTH:
                return currentPos + NORTH_VALUE;
            case SOUTH:
                return currentPos + SOUTH_VALUE;
            case EAST:
                return currentPos + EAST_VALUE;
            case WEST:
                return currentPos + WEST_VALUE;
            case NONE:
                return referencedBoard.availableSpots.keyAt(random.nextInt(referencedBoard.availableSpots.size()));
        }

        return 0;
    }

    //Get random Direction from the possible list
    private eDirections getAvailableDirection(){
        initPossibleDirections();
        return possibleDirections.get(random.nextInt(possibleDirections.size()));
    }

    //Initiate Target mode every time entering to this state
    public void initTargetMode(int firstHitPos){
        possibleDirections.clear();
        this.firstHitPos = firstHitPos;
        isVertical = true;
        isHorizontal = true;
    }

    //Informing the Target state about the last play he did
    public void setHit(boolean isHit, int position){
        this.currentPos = position;
        if(!isHit)
            currentPos = firstHitPos;
        if(isVertical && isHorizontal) {
            if (isHit && ((position == firstHitPos + NORTH_VALUE) || (position == firstHitPos + SOUTH_VALUE)))
                this.isHorizontal = false;
            if (isHit && ((position == firstHitPos + EAST_VALUE) || (position == firstHitPos + WEST_VALUE)))
                this.isVertical = false;
        }
    }

    //Adds possible directions to a list.
    private void initPossibleDirections(){
        possibleDirections.clear();
        if(isVertical) {
            if (referencedBoard.availableSpots.containsKey(currentPos + NORTH_VALUE))
                possibleDirections.add(eDirections.NORTH);
            if (referencedBoard.availableSpots.containsKey(currentPos + SOUTH_VALUE))
                possibleDirections.add(eDirections.SOUTH);
        }
        if(isHorizontal) {
            if (referencedBoard.availableSpots.containsKey(currentPos + EAST_VALUE))
                if(isOnTheSameLine(currentPos, currentPos + EAST_VALUE))
                    possibleDirections.add(eDirections.EAST);
            if (referencedBoard.availableSpots.containsKey(currentPos + WEST_VALUE))
                if(isOnTheSameLine(currentPos, currentPos + WEST_VALUE))
                    possibleDirections.add(eDirections.WEST);
        }
        if(possibleDirections.isEmpty()) {
            currentPos = firstHitPos;
            try {
                initPossibleDirections();
            } catch (StackOverflowError e){             // To make sure 99.99% StackOverflow exception wont happen because recursive call and crash the app.
                possibleDirections.add(eDirections.NONE);
            }
        }
    }

    private boolean isOnTheSameLine(int currentPos, int searchPos){
        if(currentPos / 10 == searchPos / 10)
            return true;
        return false;
    }
}
