package com.example.ormil.battleships.Logic;

import android.util.ArrayMap;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

/**
 * Created by ormil on 02/12/2017.
 */

public class Board {

    public final int NUM_OF_TILES = 10;
    public final int NUM_OF_SHIPS = 10;

    public enum TileState {
        EMPTY, SHIP, SHIP_AREA, HIT, SHIP_HIT
    }

    public enum HitState {
        HIT, MISS, NONE
    }

    private Tile mTiles[][] = new Tile[NUM_OF_TILES][NUM_OF_TILES];
    private int shipKeysBoard[][] = new int[NUM_OF_TILES][NUM_OF_TILES];
    public ArrayMap<Integer, Integer> availableSpots = new ArrayMap<>();
    private HashMap<Integer, Ship> shipHashMap = new HashMap<>();
    private int shipKey;

    private boolean isAlreadySetup = false;
    private Stack<Ship> setupShipList = new Stack<>();

    public Board() {

        shipKey = 1;

        for(int i = 0 ; i< mTiles.length ; i++) {
            for(int j = 0; j< mTiles[i].length; j++) {
                mTiles[i][j] = new Tile();
                availableSpots.put(getPositionFromXY(i, j), getPositionFromXY(i, j));
            }
        }

        generateShips();
    }

    public int getBoardSize() {
        return NUM_OF_TILES * NUM_OF_TILES;
    }

    public Tile getTile(int position) {

        int x = getXPosition(position);
        int y = getYPosition(position);
        return mTiles[x][y];
    }

    //Setup a ship from the start position
    private boolean setupShip(Ship ship, int startPosition){

        int startX = getXPosition(startPosition);
        int startY = getYPosition(startPosition);
        int startPos = ship.isVertical() ? startX : startY;

        if(!isAvailable(ship, startPosition))
            return false;
        else {
            ship.setStartPos(startPosition);
            for (int i = startPos; i < startPos + ship.getSize(); i++) {
                if (ship.isVertical()) {
                    mTiles[i][startY].setStatus(TileState.SHIP);
                    shipKeysBoard[i][startY] = shipKey;
                } else {
                    mTiles[startX][i].setStatus(TileState.SHIP);
                    shipKeysBoard[startX][i] = shipKey;
                }

            }
            setUpShipArea(ship, TileState.SHIP_AREA);
            shipHashMap.put(shipKey, ship);
            shipKey++;
        }
        return true;
    }

    //Checks if position is possible for the ship before setting up
    private boolean isAvailable(Ship ship, int startPosition){

        int startX = getXPosition(startPosition);
        int startY = getYPosition(startPosition);
        int startPos = ship.isVertical() ? startX : startY;
        int shipSize = ship.getSize();

        if(ship.isVertical() && startPos + shipSize >= NUM_OF_TILES)
            return false;
        else if(startPos + shipSize >= NUM_OF_TILES)
            return false;
        else {
            for (int i = startPos; i < startPos + shipSize; i++) {
                if (ship.isVertical()) {
                    if (mTiles[i][startY].getStatus() != TileState.EMPTY)
                        return false;
                } else if (mTiles[startX][i].getStatus() != TileState.EMPTY)
                    return false;
            }
        }
        return true;
    }

    //Sets/Changes the area around the ship accordingly to the type we want.
    private void setUpShipArea(Ship ship, TileState type){

        int minX = getXPosition(ship.getStartPos());
        int minY = getYPosition(ship.getStartPos());

        int maxX;
        int maxY;
        if(ship.isVertical()) {
            maxX = minX + ship.getSize();
            maxY = minY + 1;
        } else {
            maxX = minX + 1;
            maxY = minY + ship.getSize();
        }

        int xMinBound = Math.max(minX - 1, 0);
        int yMinBound = Math.max(minY - 1, 0);

        int xMaxBound = Math.min(maxX + 1, NUM_OF_TILES);
        int yMaxBound = Math.min(maxY + 1, NUM_OF_TILES);

        for(int i = xMinBound; i < xMaxBound; i++)
            for(int j = yMinBound; j < yMaxBound; j++){
                if(mTiles[i][j].getStatus() != TileState.SHIP && mTiles[i][j].getStatus() != TileState.SHIP_HIT) {
                    mTiles[i][j].setStatus(type);
                    if(type == TileState.HIT)
                        if(availableSpots.containsKey(getPositionFromXY(i, j)))
                            availableSpots.remove(getPositionFromXY(i, j));
                }
            }
    }

    //Randomly setup ships by picking a ship from the available ones and a position
    public void autoSetup(){

        Random position = new Random();
        Random vertical = new Random();

        if(isAlreadySetup){
            clearBoard();
            generateShips();
        }

        while(!setupShipList.isEmpty()){

            int randomPos = position.nextInt(100);
            Ship tempShip = setupShipList.pop();

            tempShip.setIsVertical(vertical.nextBoolean());

            if(setupShip(tempShip, randomPos))
                continue;
            else
                setupShipList.push(tempShip);

        }

        isAlreadySetup = true;
    }

    //Generates all ships at start and adds them to the stack.
    private void generateShips(){
        for(int i = 0; i < Ship.MAX_SHIP_SIZE; i++)
            for(int j = 0; j < Ship.MAX_SHIP_SIZE - i; j++)
                setupShipList.push(new Ship(i+1, false));
    }

    //Clear all board and generating ships again
    public void clearBoard(){

        shipKey = 1;
        shipHashMap.clear();

        for(int i = 0 ; i< mTiles.length ; i++) {
            for(int j = 0; j< mTiles[i].length; j++) {
                mTiles[i][j].setStatus(TileState.EMPTY);
                /*if(availableSpots.containsKey(i*10 + j))
                    availableSpots.remove(i*10 + j);
                availableSpots.put(i*10 + j, i*10 + j);*/
            }
        }
    }

    //Returns the ship key (id) from a specific position
    private int getShipKey(int position){
        return shipKeysBoard[getXPosition(position)][getYPosition(position)];
    }

    //Set the data accordingly to the current data and the position. Data includes Arrays Maps and Lists that needs to be changed.
    public HitState hitTile(int position){

        Tile thisTile = getTile(position);
        TileState status = thisTile.getStatus();
        HitState hitState = HitState.NONE;
        switch(status){
            case EMPTY:
            case SHIP_AREA:
                thisTile.setStatus(TileState.HIT);
                hitState = HitState.MISS;
                break;
            case SHIP:
                thisTile.setStatus(TileState.SHIP_HIT);
                shipHashMap.get(getShipKey(position)).takeHit();
                checkIfDead(getShipKey(position));
                hitState = HitState.HIT;
                break;
            case HIT:
            case SHIP_HIT:
                hitState = HitState.NONE;
        }
        removeAvailableSpot(position);
        return hitState;
    }

    //Checking if a ships got blew up by key (id) of the ship.
    private void checkIfDead(int shipKey){
        Ship ship = shipHashMap.get(shipKey);
        if(ship.isDead()) {
            setUpShipArea(ship, TileState.HIT);
            shipHashMap.remove(shipKey);
        }
    }

    //Returns ships by specific position
    public Ship getShip(int position){
        return shipHashMap.get(getShipKey(position));
    }

    //Returns ship by the index of it int the data structure
    public Ship getShipByIndex(int index){
        return shipHashMap.get(index);
    }

    //Remove available position if the data structure
    private void removeAvailableSpot(int position){
        if(availableSpots.containsKey(position))
            availableSpots.remove(position);
    }

    //Returns if board has no more ships alive.
    public boolean checkIfWon(){
        return shipHashMap.isEmpty();
    }

    //Returns the first digit of a position. e.g. Position: 56 -> Returns: 5
    private int getXPosition(int position){
        return position / 10;
    }

    //Returns the last digit of a position. e.g. Position: 56 -> Returns: 6
    private int getYPosition(int position){
        return position % 10;
    }

    //Returns the original position from 2 indexes. e.g. Index #1: 5, Index #2: 6 -> Returns: 56
    public int getPositionFromXY(int x, int y){
        return x * 10 + y;
    }

}