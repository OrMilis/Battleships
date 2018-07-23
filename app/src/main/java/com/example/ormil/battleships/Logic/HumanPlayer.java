package com.example.ormil.battleships.Logic;

import android.util.Log;

import com.example.ormil.battleships.TileAdapter;
import com.example.ormil.battleships.TileView;

/**
 * Created by ormil on 07/12/2017.
 */

public class HumanPlayer extends Player {

    public boolean playTurn(int position, Board aiBoard) {
        Board.HitState hitState = aiBoard.hitTile(position);
        boolean isHit = hitState != Board.HitState.MISS;

        if(hitState != Board.HitState.NONE)
            ((TileView)playerGrid.getChildAt(position)).gameView.playAnimation(hitState, (TileAdapter)playerGrid.getAdapter());

        return isHit;
    }
}
