package com.example.ormil.battleships.Logic;

import android.util.Log;

import com.example.ormil.battleships.TileAdapter;
import com.example.ormil.battleships.TileView;

import java.util.Random;

/**
 * Created by ormil on 07/12/2017.
 */

//State Machine AI
public class ComputerPlayer extends Player {

    private final int MAX_THINK_TIME = 1500;
    private final int MIN_THINK_TIME = 700;

    private eDifficulty difficulty;

    public enum eAiState { HUNT, TARGET, SMART_HUNT }

    private State[] states = new State[3];

    private eAiState currentState = eAiState.HUNT;

    Random randomThinkTime = new Random();

    //Initiate AI and states every new game.
    public void initAi(eDifficulty difficulty, Board humanBoard){
        this.difficulty = difficulty;
        switch(difficulty){
            case EASY:
            case NORMAL:
                currentState = eAiState.HUNT;
                break;
            case HARD:
                currentState = eAiState.SMART_HUNT;
                break;
        }
        initStates(humanBoard);
    }

    //Initiate States for the new human player board
    private void initStates(Board humanBoard){
        states[eAiState.HUNT.ordinal()] = new Hunt(humanBoard);
        states[eAiState.TARGET.ordinal()] = new Target(humanBoard);
        states[eAiState.SMART_HUNT.ordinal()] = new SmartHunt(humanBoard);
    }

    public boolean playTurn(Board humanBoard, boolean toThink){
        if(toThink)
            think();
        int position = states[currentState.ordinal()].getPositionToHit();
        Board.HitState hitState = humanBoard.hitTile(position);
        boolean isHit = hitState == Board.HitState.HIT;

        //Changing states of AI.
        switch(difficulty){
            case NORMAL:
                if(isHit && currentState != eAiState.TARGET){
                    currentState = eAiState.TARGET;
                    ((Target)states[currentState.ordinal()]).initTargetMode(position);
                    ((Target)states[currentState.ordinal()]).setHit(isHit, position);
                }
                else if(currentState == eAiState.TARGET){
                    ((Target)states[currentState.ordinal()]).setHit(isHit, position);
                }

                if(isHit){
                    if(humanBoard.getShip(position) == null) {
                        currentState = eAiState.HUNT;
                    }
                }
                break;
            case HARD:
                if(isHit && currentState != eAiState.TARGET){
                    currentState = eAiState.TARGET;
                    ((Target)states[currentState.ordinal()]).initTargetMode(position);
                    ((Target)states[currentState.ordinal()]).setHit(isHit, position);
                }
                else if(currentState == eAiState.TARGET){
                    ((Target)states[currentState.ordinal()]).setHit(isHit, position);
                }

                if(isHit){
                    if(humanBoard.getShip(position) == null) {
                        currentState = eAiState.SMART_HUNT;
                    }
                }
                break;
        }

        ((TileView)playerGrid.getChildAt(position)).gameView.playAnimation(hitState, (TileAdapter)playerGrid.getAdapter());

        return isHit;
    }

    //Waiting between 700ms to 1500ms before play.
    private void think() {
        try{
            Thread.sleep(randomThinkTime.nextInt(MAX_THINK_TIME-MIN_THINK_TIME) + MIN_THINK_TIME);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}
