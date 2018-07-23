package com.example.ormil.battleships;

/* Battleships Or Milis 208999185 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ormil.battleships.Logic.GameManager;
import com.example.ormil.battleships.Logic.KeyManager;
import com.example.ormil.battleships.Logic.LeaderboardRepository;
import com.example.ormil.battleships.Logic.User;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class EndScreen extends AppCompatActivity {

    private final int       BASE_SCORE = 1000;
    private final double    MULTIPLY = 1.1;

    private TextView gameState;

    private RelativeLayout endScreen;
    private RelativeLayout recordView;

    private boolean isHumanWon;
    private long    time;
    private int     untouchedTiles;

    private User user;
    private LatLng userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_screen);

        gameState = findViewById(R.id.statusWL);
        endScreen = findViewById(R.id.endScreen);
        recordView = findViewById(R.id.recordScreen);

        final ImageButton   replayButton = findViewById(R.id.replayButton);
        final ImageButton   menuButton = findViewById(R.id.menuButton);
        final Button        recordConfirmButton = findViewById(R.id.recordConfirmButton);
        final TextView      score = findViewById(R.id.scoreNum);
        final EditText      nameFill = findViewById(R.id.nameValue);

        isHumanWon = getIntent().getExtras().getBoolean(KeyManager.WIN_STATE);
        time = getIntent().getExtras().getLong(KeyManager.PLAY_TIME);
        untouchedTiles = getIntent().getExtras().getInt(KeyManager.UNTOUCHED_TILES);
        userLocation = new LatLng(getIntent().getExtras().getDouble(KeyManager.LAT_VALUE),getIntent().getExtras().getDouble(KeyManager.LON_VALUE));

        user = new User();
        user.setScore(calculateScore(time, untouchedTiles));

        score.setText("" + user.getScore());
        recordConfirmButton.setEnabled(false);

        setFont(isHumanWon);

        if(isHumanWon) {
            List<User> userList = LeaderboardRepository.getInstance().getTopUsersByDifficulty(GameManager.getInstance().getDifficulty());
            if(userList.isEmpty() || userList.get(userList.size() - 1).getScore() < user.getScore()) {
                endScreen.setVisibility(View.GONE);
            }
        }

        //region ** Button Listeners **

        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), BoardSetUp.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        //close all other activities in back stack and return to the Main activity
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MainMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        nameFill.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int before) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int before) {
                if(charSequence.length() == 0){
                    recordConfirmButton.setEnabled(false);
                }
                else{
                    recordConfirmButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        recordConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordConfirmButton.setEnabled(false);
                user.setName(nameFill.getText().toString());
                user.setLat(userLocation.latitude);
                user.setLon(userLocation.longitude);
                user.setDifficulty(GameManager.getInstance().getDifficulty());
                LeaderboardRepository.getInstance().insert(user);
                endScreen.setVisibility(View.VISIBLE);
                ObjectAnimator anim = ObjectAnimator.ofFloat(recordView, "alpha", 1f, 0f);
                anim.start();
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        recordView.setVisibility(View.GONE);
                    }
                });
            }
        });
        //endregion
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getBaseContext(), MainMenu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private int calculateScore(long timePlayed, int untouchedTiles){
        long timeInSec = timePlayed / 1000;
        int temp = (int) (BASE_SCORE - MULTIPLY * timeInSec);
        return temp + (temp/10 * untouchedTiles);
    }

    private void setFont(boolean isWon){
        if(isWon) {
            gameState.setText(R.string.winStr);
            Typeface typeface = ResourcesCompat.getFont(EndScreen.this, R.font.gilroy_extrabold);
            gameState.setTypeface(typeface);
        }
        else{
            gameState.setText(R.string.loseStr);
            Typeface typeface = ResourcesCompat.getFont(EndScreen.this, R.font.pt_serif);
            gameState.setTypeface(typeface);
        }
    }
}
