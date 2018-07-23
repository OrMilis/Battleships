package com.example.ormil.battleships;

/* Battleships Or Milis 208999185 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

import com.example.ormil.battleships.Logic.Board;
import com.example.ormil.battleships.Logic.GameManager;

public class BoardSetUp extends AppCompatActivity {

    private GridView mGrid;
    private TileAdapter mAdapter;
    private ImageButton mRandomButton;
    private Button mConfirmButton;
    private Board mBoard = new Board();
    private View mFrameView;
    private int mNumColumns;
    private int mImageThumbSpacing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_set_up);

        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        mFrameView = findViewById(R.id.setupFrameLayout);
        mNumColumns = getResources().getInteger(R.integer.grid_num_columns);

        mAdapter = new TileAdapter(BoardSetUp.this, mBoard, TileAdapter.ePlayerIndicator.HUMAN);
        mGrid = findViewById(R.id.gridView);
        mRandomButton = findViewById(R.id.randomButton);
        mConfirmButton = findViewById(R.id.setupConfirmButton);

        mConfirmButton.setEnabled(false);
        mGrid.setAdapter(mAdapter);

        //region ViewTreeObserver listener
        // This listener is used to get the final Height of the GridView.
        // The height used to set the column width in order to get each view as a nice square.

        mGrid.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override public void onGlobalLayout() {
                        int gridWidth = mFrameView.getHeight() - mFrameView.getPaddingLeft();
                            final int numColumns = mNumColumns;
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (gridWidth / numColumns) - mImageThumbSpacing;
                                mFrameView.setPadding(mImageThumbSpacing * 2, mImageThumbSpacing * 2, 0, 0);
                                mAdapter.setNumColumns(numColumns);
                                mGrid.setColumnWidth(columnWidth);
                                mAdapter.setItemHeight(columnWidth);
                            }
                        }

                });

        //endregion

        //region Buttons Listeners
        //Random setup button listener
        mRandomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBoard.autoSetup();
                mAdapter.notifyDataSetChanged();
                mConfirmButton.setEnabled(true);
            }
        });

        //Confirm board button listener
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameManager.getInstance().initGameManager();
                GameManager.getInstance().startGame(mBoard);
                Intent intent = new Intent(BoardSetUp.this, GameScreen.class);
                startActivity(intent);
            }
        });
        //endregion
    }


    //ClearTop callback. When going back to board setup in middle of game or starting again in EndScreen
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mBoard.clearBoard();
        mAdapter.notifyDataSetChanged();
        mConfirmButton.setEnabled(false);
    }
}

