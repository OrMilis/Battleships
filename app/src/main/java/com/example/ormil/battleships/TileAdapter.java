package com.example.ormil.battleships;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.example.ormil.battleships.Logic.Board;

public class TileAdapter extends BaseAdapter {

    public enum ePlayerIndicator{
        HUMAN, AI
    }

    private Context mContext;
    private Board mBoard;

    private GridView.LayoutParams mImageViewLayoutParams;
    private int mItemHeight = 0;
    private int mNumColumns;

    private int shipBackgroundColor;
    private int defaultBackgroundColor;
    private int hitBackgroundColor;
    private int shipHitBackgroundColor;
    private int hitIcon; //Mirror bug
    private int shipHitIcon;

    public TileAdapter(Context context, Board board, ePlayerIndicator indicator) {
        mBoard = board;
        mContext = context;
        mImageViewLayoutParams = new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT);

        defaultBackgroundColor = ContextCompat.getColor(mContext, R.color.colorBackground);
        hitIcon = R.drawable.ic_hit; //Got mirror bug cant use that
        shipHitIcon = R.drawable.ic_hit_red;
        hitBackgroundColor = ContextCompat.getColor(mContext, R.color.colorHitBackground);
        shipHitBackgroundColor = ContextCompat.getColor(mContext, R.color.colorShipHitBackground);

        switch (indicator){
            case AI:
                shipBackgroundColor = ContextCompat.getColor(context, R.color.colorBackground);
                break;
            case HUMAN:
                if(mContext.getClass() == BoardSetUp.class)
                    shipBackgroundColor = ContextCompat.getColor(context, R.color.colorSetupShip);
                else
                    shipBackgroundColor = ContextCompat.getColor(context, R.color.colorGameHumanShip);
                break;
        }
    }

    @Override
    public int getCount() {
        return mBoard.getBoardSize();
    }

    @Override
    public Object getItem(int position) {
        return mBoard.getTile(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TileView tileView;
        //if (convertView == null) {
            tileView = new TileView(mContext);
            tileView.setLayoutParams(mImageViewLayoutParams);
            //tileView.setPadding(8, 8, 8, 8);
        /*} else {
            tileView = (TileView) convertView;
        }*/

        if ((tileView.getLayoutParams().height != mItemHeight)
                || (tileView.getLayoutParams().width != mItemHeight)) {
            tileView.setLayoutParams(mImageViewLayoutParams);
        }

        Board.TileState status = mBoard.getTile(position).getStatus();

        switch(status){
            case SHIP:
                tileView.setBackgroundColor(shipBackgroundColor);
                break;
            case HIT:
                tileView.image.setBackgroundColor(hitBackgroundColor);
                /* Mirroring bug */
                tileView.image.setImageResource(hitIcon);
                break;
            case SHIP_HIT:
                tileView.image.setBackgroundColor(shipHitBackgroundColor);
                /* Mirroring bug */
                tileView.image.setImageResource(shipHitIcon);
                break;
            default:
                //tileView.setBackgroundColor(defaultBackgroundColor);
                break;
        }
        return tileView;
    }

    public void setItemHeight(int height) {
        if (height == mItemHeight) {
            return;
        }
        mItemHeight = height;
        mImageViewLayoutParams = new GridView.LayoutParams(mItemHeight, mItemHeight);
        notifyDataSetChanged();
    }

    public int getHeight(){
        return mItemHeight;
    }

    public void setNumColumns(int numColumns) {
        mNumColumns = numColumns;
    }

}
