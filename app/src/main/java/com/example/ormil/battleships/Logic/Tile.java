package com.example.ormil.battleships.Logic;

/**
 * Created by ormil on 02/12/2017.
 */

public class Tile {

    private Board.TileState mStatus;

    public Tile() {
        mStatus = Board.TileState.EMPTY;
    }

    public Board.TileState getStatus() {
        return mStatus;
    }

    public void setStatus(Board.TileState status) {
        this.mStatus = status;
    }
    
}
