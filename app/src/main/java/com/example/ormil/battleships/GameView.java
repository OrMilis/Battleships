package com.example.ormil.battleships;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.example.ormil.battleships.Logic.Board;

/**
 * Created by ormil on 29/12/2017.
 */

public class GameView extends View {

    private Context mContext;

    private final int ROWS = 6;
    private final int COLS = 4;
    private static int frameWidth = 120;
    private static int frameHeight = 120;
    private final int FRAME_COUNT = 24;
    private final int FRAME_LENGTH_IN_MS = 25;

    private boolean isAnimating = false;
    private Board.HitState hitState = Board.HitState.NONE;

    private int currentFrame = 0;
    private long lastFrameChangeTime = 0;

    private Rect frameToDraw = new Rect(0, 0, frameWidth, frameHeight);
    RectF whereToDraw = new RectF(0,0, frameWidth, frameHeight);

    public GameView(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isAnimating)
            switch (hitState) {
                case HIT:
                    canvas.drawBitmap(MainMenu.explosion_anim, frameToDraw, whereToDraw, null);
                    break;
                case MISS:
                    canvas.drawBitmap(MainMenu.water_anim, frameToDraw, whereToDraw, null);
                    break;
            }
    }

    private void update(){
        long time  = System.currentTimeMillis();
        if ( time > lastFrameChangeTime + FRAME_LENGTH_IN_MS) {
            lastFrameChangeTime = time;
            currentFrame++;
            postInvalidate();
            if (currentFrame >= FRAME_COUNT) {
                currentFrame = 0;
                isAnimating = false;
            }
        }

        //update the left and right values of the source of
        //the next frame on the spritesheet
        frameToDraw.left = (currentFrame % COLS) * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;
        frameToDraw.top = (currentFrame / COLS) * frameHeight;
        frameToDraw.bottom = frameToDraw.top + frameHeight;
    }

    public void playAnimation(Board.HitState hit, final TileAdapter adapter){
        this.hitState = hit;
        this.isAnimating = true;
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(isAnimating) {
                    update();
                }
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
        thread.start();
    }

    public static void reSize(){
        frameHeight = frameWidth = MainMenu.explosion_anim.getWidth() / 4;
    }
}
