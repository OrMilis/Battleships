package com.example.ormil.battleships;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class TileView extends RelativeLayout {

    public ImageView image;
    public GameView gameView;

    public TileView(Context context) {
        super(context);

        LayoutParams layoutParams = new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);

        image = new ImageView(context);
        image.setLayoutParams(layoutParams);
        image.setPadding(8,8,8,8);

        gameView = new GameView(context);
        gameView.setLayoutParams(layoutParams);

        addView(image);
        addView(gameView);

        setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackground));
    }
}
