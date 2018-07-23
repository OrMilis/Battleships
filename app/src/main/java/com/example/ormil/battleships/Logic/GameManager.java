package com.example.ormil.battleships.Logic;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.ormil.battleships.R;

/**
 * Created by ormil on 07/12/2017.
 */

public class GameManager {
    private eDifficulty difficulty = eDifficulty.NORMAL;
    private static GameManager insGameManager;
    private HumanPlayer humanPlayer;
    private ComputerPlayer aiPlayer;
    public boolean isHumanTurn;

    //Singleton
    private GameManager(){
        initGameManager();
    }

    public void startGame(Board humanBoard){
        isHumanTurn = true; // human turn
        humanPlayer.setBoard(humanBoard);
        aiPlayer.getBoard().autoSetup();
        aiPlayer.initAi(difficulty, humanBoard);
    }

    //Initiate Game Manager when new game is setup.
    public void initGameManager(){
        humanPlayer = new HumanPlayer();
        aiPlayer = new ComputerPlayer();
    }

    public HumanPlayer getHumanPlayer(){
        return humanPlayer;
    }

    public ComputerPlayer getAiPlayer(){
        return aiPlayer;
    }

    public void setGameDifficulty(eDifficulty difficulty){
        this.difficulty = difficulty;
    }

    public eDifficulty getDifficulty(){
        return difficulty;
    }

    //Singleton
    public static GameManager getInstance() {
        if(insGameManager == null)
            insGameManager = new GameManager();
        return insGameManager;
    }

}
