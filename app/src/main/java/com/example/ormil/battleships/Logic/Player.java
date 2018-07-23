package com.example.ormil.battleships.Logic;

import android.widget.GridView;

/**
 * Created by ormil on 07/12/2017.
 */

//Base class for HumanPlayer and ComputerPlayer
public abstract class Player {
    protected GridView playerGrid;
    private Board mBoard;

    public Player(){
        mBoard = new Board();
    }

    public Board getBoard(){
        return mBoard;
    }

    public void setBoard(Board board){
        this.mBoard = board;
    }

    public void setPlayerGrid(GridView gridView){
        this.playerGrid = gridView;
    }

}
