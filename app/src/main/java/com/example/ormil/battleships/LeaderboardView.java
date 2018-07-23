package com.example.ormil.battleships;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ormil on 13/01/2018.
 */

public class LeaderboardView extends LinearLayout {

    public final int BACKGROUND_COLOR = ContextCompat.getColor(getContext(), R.color.colorBackground);
    public final int THEME_COLOR = ContextCompat.getColor(getContext(), R.color.colorListItemText);

    TextView userName;
    TextView userScore;
    TextView userLat;
    TextView userLon;

    public LeaderboardView(Context context) {
        super(context);

        userName = new TextView(context);
        userScore = new TextView(context);
        userLat = new TextView(context);
        userLon = new TextView(context);

        TextView[] textViews = {userName, userScore, userLat, userLon};

        Typeface typeface = ResourcesCompat.getFont(context, R.font.gilroy_light);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.25f);
        layoutParams.gravity = Gravity.CENTER;

        for (TextView textView: textViews) {
            textView.setLayoutParams(layoutParams);
            textView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setTypeface(typeface);
            textView.setTextSize(16);
            textView.setTextColor(THEME_COLOR);
            addView(textView);
        }
    }

    public void setTextColor(int color){
        userName.setTextColor(color);
        userScore.setTextColor(color);
        userLat.setTextColor(color);
        userLon.setTextColor(color);
    }
}
