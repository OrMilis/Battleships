package com.example.ormil.battleships;

/* Battleships Or Milis 208999185 */

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;

import com.example.ormil.battleships.Logic.Board;
import com.example.ormil.battleships.Logic.ComputerPlayer;
import com.example.ormil.battleships.Logic.GameManager;
import com.example.ormil.battleships.Logic.HumanPlayer;
import com.example.ormil.battleships.Logic.KeyManager;
import com.example.ormil.battleships.Logic.LeaderboardRepository;
import com.example.ormil.battleships.Logic.User;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import android.location.LocationListener;

import java.util.List;

public class GameScreen extends AppCompatActivity implements SensorService.MovementAlertListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private HumanPlayer mHumanPlayer;
    private ComputerPlayer mAiPlayer;
    private Board mHumanPlayerBoard;
    private Board mAiPlayerBoard;
    private View mHumanFrameLayout;
    private View mAiFrameLayout;
    private GridView mHumanPlayerGrid;
    private GridView mAiPlayerGrid;
    private TileAdapter mHumanPlayerAdapter;
    private static TileAdapter mAiPlayerAdapter;

    final AnimatorSet humanToAi = new AnimatorSet();
    final AnimatorSet aiToHuman = new AnimatorSet();

    private boolean isBitmapScaled = false;
    private int layoutListenerInitCounter = 0;

    private ImageView mTurnIndicator;

    private int mNumColumns;
    private int mImageThumbSpacing;

    private SensorService.SensorBinder mMovementBinder;
    private ServiceConnection mMovementService;
    private boolean isMovementServiceBound;

    private long startTime = 0;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        List<User> userList = LeaderboardRepository.getInstance().getmTopUsersEasy();

        for (User u: userList) {
            Log.e("User", u.toString());
        }

        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        mNumColumns = getResources().getInteger(R.integer.grid_num_columns);

        mTurnIndicator = findViewById(R.id.turnIndicatorDrawable);

        //region Animations

        final ObjectAnimator humanToAiRotateAnim = ObjectAnimator.ofFloat(mTurnIndicator , "rotation", 0f, 180f);
        final ObjectAnimator aiToHumanRotateAnim = ObjectAnimator.ofFloat(mTurnIndicator , "rotation", 180f, 360f);
        final ValueAnimator colorAnimHumanToAi = ValueAnimator.ofArgb(ContextCompat.getColor(getBaseContext(), R.color.colorHumanBoard), ContextCompat.getColor(getBaseContext(), R.color.colorAiBoard));
        final ValueAnimator colorAnimAiToHuman = ValueAnimator.ofArgb(ContextCompat.getColor(getBaseContext(), R.color.colorAiBoard), ContextCompat.getColor(getBaseContext(), R.color.colorHumanBoard));
        final ValueAnimator.AnimatorUpdateListener colorTransactionListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mTurnIndicator.getDrawable().setTint((Integer) valueAnimator.getAnimatedValue());
            }
        };

        colorAnimHumanToAi.addUpdateListener(colorTransactionListener);
        colorAnimAiToHuman.addUpdateListener(colorTransactionListener);

        humanToAiRotateAnim.setInterpolator(new OvershootInterpolator(0.85f));
        aiToHumanRotateAnim.setInterpolator(new OvershootInterpolator(0.85f));

        humanToAi.play(colorAnimHumanToAi).with(humanToAiRotateAnim);
        aiToHuman.play(colorAnimAiToHuman).with(aiToHumanRotateAnim);

        humanToAi.setDuration(500);
        aiToHuman.setDuration(500);

        //endregion

        //region Initiate Player and Ai Fields
        mHumanPlayer = GameManager.getInstance().getHumanPlayer();
        mAiPlayer = GameManager.getInstance().getAiPlayer();

        mHumanPlayerBoard = GameManager.getInstance().getHumanPlayer().getBoard();
        mAiPlayerBoard = GameManager.getInstance().getAiPlayer().getBoard();

        mHumanFrameLayout = findViewById(R.id.humanPlayerFrameLayout);
        mHumanPlayerGrid = findViewById(R.id.humanPlayerGridView);

        mAiFrameLayout = findViewById(R.id.aiPlayerFrameLayout);
        mAiPlayerGrid = findViewById(R.id.aiPlayerGridView);

        mHumanPlayerAdapter = new TileAdapter(GameScreen.this, mHumanPlayerBoard, TileAdapter.ePlayerIndicator.HUMAN);
        mAiPlayerAdapter = new TileAdapter(GameScreen.this, mAiPlayerBoard, TileAdapter.ePlayerIndicator.AI);

        mHumanPlayer.setPlayerGrid(mAiPlayerGrid);
        mAiPlayer.setPlayerGrid(mHumanPlayerGrid);

        //endregion

        mHumanPlayerGrid.setAdapter(mHumanPlayerAdapter);
        mAiPlayerGrid.setAdapter((mAiPlayerAdapter));

        //region Grid Item listener and Game play Thread

        mAiPlayerGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(startTime == 0)
                    startTime = System.currentTimeMillis();

                if(GameManager.getInstance().isHumanTurn) {
                    GameManager.getInstance().isHumanTurn = mHumanPlayer.playTurn(position, mAiPlayerBoard);

                    if (mAiPlayerBoard.checkIfWon()) {
                        endGame(true);
                    }

                    if (!GameManager.getInstance().isHumanTurn) {
                        humanToAi.start();
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (!GameManager.getInstance().isHumanTurn) {
                                    GameManager.getInstance().isHumanTurn = aiPlayTurn(false, true);

                                    if (mHumanPlayerBoard.checkIfWon()) {
                                        GameManager.getInstance().isHumanTurn = true;
                                        endGame(false);
                                    }
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        aiToHuman.start();
                                    }
                                });
                            }
                        });
                        t.start();
                    }
                }

            }
        });
        //endregion

        //region ViewTreeListeners for both grids

        mHumanPlayerGrid.getViewTreeObserver().addOnGlobalLayoutListener(setGlobalListener(mHumanFrameLayout, mHumanPlayerAdapter, mHumanPlayerGrid));
        mAiPlayerGrid.getViewTreeObserver().addOnGlobalLayoutListener(setGlobalListener(mAiFrameLayout, mAiPlayerAdapter, mAiPlayerGrid));

        //endregion

        mAiPlayerGrid.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        isBitmapScaled = false;
                        layoutListenerInitCounter = 0;
                        mAiPlayerAdapter.notifyDataSetChanged();
                    }
                });

            mMovementService = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mMovementBinder = (SensorService.SensorBinder)iBinder;
                mMovementBinder.registerListener(GameScreen.this);
                isMovementServiceBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                isMovementServiceBound = false;
            }
        };

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationCallback =  new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        Intent intentMovement = new Intent(GameScreen.this, SensorService.class);
        bindService(intentMovement, mMovementService, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        if(isMovementServiceBound){
            unbindService(mMovementService);
            isMovementServiceBound = false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getBaseContext(), BoardSetUp.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void endGame(boolean humanPlayerWon){
        Intent intent = new Intent(GameScreen.this, EndScreen.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(KeyManager.WIN_STATE, humanPlayerWon);
        bundle.putLong(KeyManager.PLAY_TIME, System.currentTimeMillis() - startTime);
        bundle.putInt(KeyManager.UNTOUCHED_TILES, mHumanPlayerBoard.availableSpots.size());

        double lat = mLocation != null ? mLocation.getLatitude() : 0;
        double lon = mLocation != null ? mLocation.getLongitude() : 0;

        bundle.putDouble(KeyManager.LAT_VALUE, lat);
        bundle.putDouble(KeyManager.LON_VALUE, lon);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private ViewTreeObserver.OnGlobalLayoutListener setGlobalListener(final View frameLayout, final TileAdapter adapter, final GridView gridView){
        ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(layoutListenerInitCounter < 10) {
                    int gridWidth = frameLayout.getHeight() - frameLayout.getPaddingLeft();
                    final int numColumns = mNumColumns;
                    if (numColumns > 0) {
                        int columnWidth =
                                (gridWidth / numColumns) - mImageThumbSpacing;
                        LinearLayout.LayoutParams lparam = new LinearLayout.LayoutParams(gridWidth + 2 * mImageThumbSpacing, 0, (float) 0.45);
                        lparam.setMargins(0, 20, 0, 20);
                        frameLayout.setLayoutParams(lparam);

                        frameLayout.setPadding(mImageThumbSpacing * 2, mImageThumbSpacing * 2, 0, 0);
                        adapter.setNumColumns(numColumns);
                        gridView.setColumnWidth(columnWidth);
                        adapter.setItemHeight(columnWidth);

                        layoutListenerInitCounter++;
                        if(!isBitmapScaled){
                            scaleBitmap(columnWidth);
                            isBitmapScaled = true;
                        }
                    }
                }
            }
        };

        return layoutListener;
    }

    private void scaleBitmap(int size){
        scaleBitmapAndKeepRation(MainMenu.explosion_anim,size * 6, size * 4);
        scaleBitmapAndKeepRation(MainMenu.water_anim,size * 6, size * 4);
        GameView.reSize();
    }

    private void scaleBitmapAndKeepRation(Bitmap TargetBmp, int reqHeightInPixels, int reqWidthInPixels)
    {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, TargetBmp.getWidth(), TargetBmp.getHeight()), new RectF(0, 0, reqWidthInPixels, reqHeightInPixels), Matrix.ScaleToFit.CENTER);
        Bitmap scaledBitmap = Bitmap.createBitmap(TargetBmp, 0, 0, TargetBmp.getWidth(), TargetBmp.getHeight(), m, false);

        if(TargetBmp.equals(MainMenu.explosion_anim))
            MainMenu.explosion_anim = scaledBitmap;
        else
            MainMenu.water_anim = scaledBitmap;
    }

    @Override
    public void movementAlert(boolean toPlayTurn) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            else
                vibrator.vibrate(100);
        }
        if (GameManager.getInstance().isHumanTurn)
            aiPlayTurn(toPlayTurn, false);
    }

    private boolean aiPlayTurn(boolean isAlert, boolean aiTurn){
        if(isAlert) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    mAiPlayer.playTurn(mHumanPlayerBoard, false);

                    if (mHumanPlayerBoard.checkIfWon()) {
                        GameManager.getInstance().isHumanTurn = true;
                        endGame(false);
                    }
                }
            });
            t.start();
            return false;
        }
        else if(!aiTurn)
            return false;
        else
            return !mAiPlayer.playTurn(mHumanPlayerBoard, true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("Game", "Connected");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("Game", "WHY?!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,
                "onConnectionFailed: \n" + connectionResult.toString(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            mLocation = location;
        }
    }

}
