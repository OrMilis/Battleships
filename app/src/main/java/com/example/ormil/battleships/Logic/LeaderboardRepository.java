package com.example.ormil.battleships.Logic;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

/**
 * Created by ormil on 11/01/2018.
 */

public class LeaderboardRepository {

    private static LeaderboardRepository instance;

    private static Context mContext;
    private UserDao mUserDao;
    private List<User> mAllUsers;
    private List<User> mTopUsers;

    private List<User> mTopUsersEasy;
    private List<User> mTopUsersNormal;
    private List<User> mTopUsersHard;

    private LeaderboardRepository(Context context){
        new openDatabaseAsyncTask().execute(context);
    }

    public static LeaderboardRepository getInstance(){
        if(instance == null){
            instance = new LeaderboardRepository(mContext);
        }
        return instance;
    }

    public static void initRepository(Context context){
        mContext = context;
        getInstance();
    }

    public void insert(User user){
        new insertAsyncTask(mUserDao).execute(user);
    }

    public void readUsers(){
        new readUsersAsyncTask(mUserDao).execute();
    }

    public void readTopUsers(){
        new readTopUsersAsyncTask(mUserDao).execute();
    }

    public void setmUserDao(UserDao dao){
        this.mUserDao = dao;
    }

    public List<User> getmAllUsers(){
        return mAllUsers;
    }

    public void setmAllUsers(List<User> users){
        this.mAllUsers = users;
    }

    public List<User> getmTopUsers(){
        return mTopUsers;
    }

    public void setmTopUsers(List<User> users){
        this.mTopUsers = users;
    }

    public List<User> getmTopUsersEasy() {
        return mTopUsersEasy;
    }

    public void setmTopUsersEasy(List<User> mTopUsersEasy) {
        this.mTopUsersEasy = mTopUsersEasy;
    }

    public List<User> getmTopUsersNormal() {
        return mTopUsersNormal;
    }

    public void setmTopUsersNormal(List<User> mTopUsersNormal) {
        this.mTopUsersNormal = mTopUsersNormal;
    }

    public List<User> getmTopUsersHard() {
        return mTopUsersHard;
    }

    public void setmTopUsersHard(List<User> mTopUsersHard) {
        this.mTopUsersHard = mTopUsersHard;
    }

    public List<User> getTopUsersByDifficulty(eDifficulty difficulty){
        switch(difficulty){
            case EASY:
                return getmTopUsersEasy();
            case NORMAL:
                return getmTopUsersNormal();
            case HARD:
                return getmTopUsersHard();
        }

        return null;
    }

    private class insertAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao mAsyncTaskDao;

        insertAsyncTask(UserDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(User... users) {
            mAsyncTaskDao.insertUser(users[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            LeaderboardRepository.getInstance().readUsers();
            LeaderboardRepository.getInstance().readTopUsers();
        }

    }

    private class readUsersAsyncTask extends AsyncTask<Void, Void, Void>{

        private UserDao mAsyncTaskDao;

        readUsersAsyncTask(UserDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            LeaderboardRepository.instance.setmAllUsers(mAsyncTaskDao.getAll());
            return null;
        }
    }

    private class readTopUsersAsyncTask extends AsyncTask<Void, Void, Void>{

        private UserDao mAsyncTaskDao;

        readTopUsersAsyncTask(UserDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            LeaderboardRepository.instance.setmTopUsers(mAsyncTaskDao.getTopScorers());
            LeaderboardRepository.instance.setmTopUsersEasy(mAsyncTaskDao.getTopScorersDifficulty(eDifficulty.EASY));
            LeaderboardRepository.instance.setmTopUsersNormal(mAsyncTaskDao.getTopScorersDifficulty(eDifficulty.NORMAL));
            LeaderboardRepository.instance.setmTopUsersHard(mAsyncTaskDao.getTopScorersDifficulty(eDifficulty.HARD));
            return null;
        }

    }

    private class openDatabaseAsyncTask extends AsyncTask<Context, Void, Void>{

        @Override
        protected Void doInBackground(Context... contexts) {
            LeaderboardDatabase.getInstance(contexts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            LeaderboardRepository.getInstance().setmUserDao(LeaderboardDatabase.getInstance().userDao());
            LeaderboardRepository.getInstance().readUsers();
            LeaderboardRepository.getInstance().readTopUsers();
        }
    }

}
