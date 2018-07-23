package com.example.ormil.battleships.Logic;

import android.util.ArrayMap;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ormil on 09/12/2017.
 */

public class SmartHunt extends State {

    private Board referencedBoard;
    private int[][] probabilityMatrix;
    private ArrayList<Integer> possibleIndexes = new ArrayList<>();
    private int maxValue;
    private Random random;

    public SmartHunt(Board humanBoard){
        referencedBoard = humanBoard;
        probabilityMatrix = new int[referencedBoard.NUM_OF_TILES][referencedBoard.NUM_OF_TILES];
        maxValue = 0;
        random = new Random();
    }

    //Overriding the State abstract method
    //Returns one of the maximum probability positions at random.
    @Override
    public int getPositionToHit() {
        calculate(referencedBoard.availableSpots); // In case you want to see the probabilities print insert method print() under this line.
        getPossibleIndexes();
        return possibleIndexes.get(random.nextInt(possibleIndexes.size()));
    }

    /* Pure magic algorithm that calculate the probabilities for each tile. Concentrates on bigger than 1 tile sized ships.
    *  Very complex algorithm but worst case scenario run time i got was around 10 ms so its acceptable for this purpose.
    */
    private void calculate(ArrayMap<Integer, Integer> availableSpots) {

        probabilityMatrix = new int[referencedBoard.NUM_OF_TILES][referencedBoard.NUM_OF_TILES];
        maxValue = 0;
        int shipSize;
        int row;
        int col;

        for (int ver = 0; ver < 2; ver++) {
            for (int shipNumber = 1; shipNumber <= referencedBoard.NUM_OF_SHIPS; shipNumber++) {
                if (referencedBoard.getShipByIndex(shipNumber) != null) {
                    shipSize = referencedBoard.getShipByIndex(shipNumber).getSize();
                    if (ver == 1 && shipSize == 1)
                        continue;
                }
                else
                    continue;
                for (int i = 0; i < probabilityMatrix.length; i++) {
                    for (int j = 0; j < probabilityMatrix.length; j++) {
                        boolean isFit = true;
                        for (int k = j; k < j + shipSize; k++) {
                            if (ver == 0) {
                                row = i;
                                col = k;
                            } else {
                                row = k;
                                col = i;
                            }
                            try {
                                int pos = referencedBoard.getPositionFromXY(row, col);
                                if (!availableSpots.containsValue(pos) || k >= 10)
                                    isFit = false;
                            } catch (Exception e) {
                                isFit = false;
                            }
                        }
                        if (isFit) {
                            for (int k = j; k < j + shipSize; k++) {
                                if (ver == 0) {
                                    row = i;
                                    col = k;
                                } else {
                                    row = k;
                                    col = i;
                                }
                                probabilityMatrix[row][col]++;
                                maxValue = maxValue < probabilityMatrix[row][col] ? probabilityMatrix[row][col] : maxValue;
                            }
                        }
                    }
                }
            }
        }
    }

    private void getPossibleIndexes(){
        possibleIndexes.clear();
        for (int i = 0; i < probabilityMatrix.length; i++)
            for (int j = 0; j < probabilityMatrix.length; j++){
                if(probabilityMatrix[i][j] == maxValue)
                    possibleIndexes.add(referencedBoard.getPositionFromXY(i, j));
            }
    }

    //region print() Method: Print all probabilities for tests. Welcome to enable it and see it.
    /*private void print(){
        for (int i = 0; i < probabilityMatrix.length; i++) {
            for (int j = 0; j < probabilityMatrix.length; j++) {
                System.out.printf("%02d ", probabilityMatrix[i][j]);
            }
            System.out.println();
        }
    }*/
    //endregion

}
