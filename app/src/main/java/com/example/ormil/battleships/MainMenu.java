package com.example.ormil.battleships;

/* Battleships Or Milis 208999185 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ormil.battleships.Logic.GameManager;
import com.example.ormil.battleships.Logic.LeaderboardDatabase;
import com.example.ormil.battleships.Logic.LeaderboardRepository;
import com.example.ormil.battleships.Logic.eDifficulty;

public class MainMenu extends AppCompatActivity {

    private eDifficulty gameDifficulty = eDifficulty.NORMAL;

    public static Bitmap explosion_anim;
    public static Bitmap water_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        LeaderboardRepository.initRepository(this);

        explosion_anim = BitmapFactory.decodeResource(getResources(), R.drawable.spritesheet_explosion);
        water_anim = BitmapFactory.decodeResource(getResources(), R.drawable.spritesheet_water);

        final Button difficultyButton    = findViewById(R.id.difficulty_button);
        final ImageButton startButton        = findViewById(R.id.startButton);
        final ImageButton leaderboardButton = findViewById(R.id.leaderboard_button);

        //region ** Buttons Listeners **

        //Difficulty button listener
        difficultyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Button)view).setText(nextDifficulty());
            }
        });

        //Start button listener
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameManager.getInstance().setGameDifficulty(gameDifficulty);
                Intent intent = new Intent(getBaseContext(), BoardSetUp.class);
                //Intent intent = new Intent(getBaseContext(), SpriteSheetAnimation.class);
                startActivity(intent);
            }
        });

        //Leaderboard button listener
        leaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), LeaderboardScreen.class);
                startActivity(intent);
            }
        });
        //endregion
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LeaderboardDatabase.getInstance().close();
    }

    private String nextDifficulty(){
        int index = gameDifficulty.getValue();
        int nextIndex = ++index;
        eDifficulty difficulties[] = eDifficulty.values();
        nextIndex %= difficulties.length;

        gameDifficulty = difficulties[nextIndex];

        return gameDifficulty.toString();
    }

}
