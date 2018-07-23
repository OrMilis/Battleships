package com.example.ormil.battleships;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.ormil.battleships.Logic.User;

import java.util.List;

/**
 * Created by ormil on 13/01/2018.
 */

public class LeaderboardAdapter extends BaseAdapter {

    private Context mContext;
    private List<User> userList;
    private int activeView;

    public LeaderboardAdapter(Context context, List<User> userList){
        this.mContext = context;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int i) {
        return userList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        LeaderboardView leaderboardView;
        if (convertView == null) {
            leaderboardView = new LeaderboardView(mContext);
            leaderboardView.setPadding(8, 16, 8, 16);
        } else {
            leaderboardView = (LeaderboardView) convertView;
        }

        if(position == activeView){
            setViewColors(leaderboardView, leaderboardView.THEME_COLOR, leaderboardView.BACKGROUND_COLOR);
        }
        else {
            setViewColors(leaderboardView, leaderboardView.BACKGROUND_COLOR, leaderboardView.THEME_COLOR);
        }

        User user = userList.get(position);

        leaderboardView.userName.setText(user.getName());
        leaderboardView.userScore.setText(user.getScore() + "");
        leaderboardView.userLat.setText(user.getLat() + "");
        leaderboardView.userLon.setText(user.getLon() + "");

        return leaderboardView;
    }

    public void setUserList(List<User> userList){
        this.userList = userList;
    }

    public void setActiveView(int position){
        activeView = position;
    }

    private void setViewColors(LeaderboardView view, int backgroundColor, int textColor){
        view.setBackgroundColor(backgroundColor);
        view.setTextColor(textColor);
    }
}
