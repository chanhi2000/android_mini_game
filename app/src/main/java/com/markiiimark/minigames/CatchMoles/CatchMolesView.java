package com.markiiimark.minigames.CatchMoles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.markiiimark.minigames.R;

/**
 *
 * Created by admin on 2017-08-02.
 *
 * ISSUES:
 *  - mole hitbox not accurate
 *  - menu options
 */

public class CatchMolesView extends View
{
    protected MoleThread mMoleThread;
    private Bitmap mMole, mMoleEmpty, mMoleHit, mBomb, mBombExplode, mReplayButton, mReplayButtonLight,
            mGoToMenuButton, mGoToMenuButtonLight, mPauseButton, mPauseButtonLight,
            mHealthUp, mHealthDown, mGameOver;
    private Bitmap[] mNum;
    private final static int[] mNumImgs =
    {
          R.drawable.num_0, R.drawable.num_1, R.drawable.num_2, R.drawable.num_3, R.drawable.num_4,
          R.drawable.num_5, R.drawable.num_6, R.drawable.num_7, R.drawable.num_8, R.drawable.num_9
    };
    private Paint mScoreBoard, mLevel;
    protected int mMode;

    private final String MOLE_VIEW_TAG = "MoleView";
    protected final int DRAW_GAME_BEGIN = 0;
    protected final int DRAW_MOLE = 1;
    protected final int DRAW_NO_MOLE = 2;
    protected final int DRAW_MOLE_HIT = 3;
    protected final int DRAW_BOMB = 4;
    protected final int DRAW_BOMB_EXPLODE = 5;
    protected final int PAUSE_GAME = 6;
    protected final int GAME_OVER = 8;
    protected final int HALT_GAME = 9;

    protected int mWidth, mHeight;
    protected int mReplayStartX;
    protected int mReplayStartY;
    protected int mGoToMenuStartX;
    protected int mGoToMenuStartY;
    protected int mPauseStartX;
    protected int mPauseStartY;
    protected final int mButtonDimen = 72;
    protected int mX, mY;
    int mScore=0, mLevelIndex=1, mHealthNum=5;
    MediaPlayer mSndMole, mSndCatch, mSndExplode, mSndPunch, mSndBump;
    protected int mTouchX, mTouchY;

    //region class-instantiation-domain
    public CatchMolesView(Context context)
    {
        super(context);
        setBackgroundResource(R.drawable.mole_intro);
        getBackground().setColorFilter(0xbbccbbaa, PorterDuff.Mode.DARKEN);

        retrieveDeviceDisplayMetrics(context);
        initializeBitmapImages();
        initializeSoundFile(context);
        initializeScoreAndLevel();
        setupMoleThread();

        Log.d("MoleCatch", "Display width in px is " + mWidth);
        Log.d("MoleCatch", "Display height in px is " + mHeight);
    }

    private void retrieveDeviceDisplayMetrics(Context context)
    {
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        mWidth = displayMetrics.widthPixels;
        mHeight = displayMetrics.heightPixels;
//        mWidth = this.getWidth();
//        mHeight = this.getHeight();

        mReplayStartX = mWidth/2-180;
        mReplayStartY = mHeight/2+100;
        mGoToMenuStartX = mWidth/2+80;
        mGoToMenuStartY = mReplayStartY;
        mPauseStartX = mWidth-110;
        mPauseStartY = mHeight-150;

        Log.d(  MOLE_VIEW_TAG, String.format("the width: %d,\t\t the height: %d", mWidth, mHeight)  );
        Log.d(  MOLE_VIEW_TAG, String.format("the width from getwidth: %d,\t\t the height from getheight: %d", this.getWidth(), this.getHeight())  );
    }
    protected void initializeBitmapImages()
    {
        mMole = BitmapFactory.decodeResource(getResources(), R.drawable.mole);
        mMoleEmpty = BitmapFactory.decodeResource(getResources(), R.drawable.mole_empty);
        mMoleHit = BitmapFactory.decodeResource(getResources(), R.drawable.mole_hurt);
        mBomb = BitmapFactory.decodeResource(getResources(), R.drawable.bomb);
        mBombExplode = BitmapFactory.decodeResource(getResources(), R.drawable.bomb_explode);

        mNum = new Bitmap[mNumImgs.length];
        for (int i=0; i<mNumImgs.length; i++)
        {
            mNum[i] = BitmapFactory.decodeResource(getResources(), mNumImgs[i]);
        }

        mHealthUp = BitmapFactory.decodeResource(getResources(), R.drawable.heart_up);
        mHealthDown = BitmapFactory.decodeResource(getResources(), R.drawable.heart_down);
        mPauseButton = BitmapFactory.decodeResource(getResources(), R.drawable.pause);
        mPauseButtonLight = BitmapFactory.decodeResource(getResources(), R.drawable.pause_light);
        mReplayButton = BitmapFactory.decodeResource(getResources(), R.drawable.replay);
        mReplayButtonLight = BitmapFactory.decodeResource(getResources(), R.drawable.replay_light);
        mGoToMenuButton = BitmapFactory.decodeResource(getResources(), R.drawable.go_to_menu);
        mGoToMenuButtonLight = BitmapFactory.decodeResource(getResources(), R.drawable.go_to_menu_light);
        mGameOver = BitmapFactory.decodeResource(getResources(), R.drawable.game_over_molecatch);
    }
    private void initializeSoundFile(Context context)
    {
        mSndMole = MediaPlayer.create(context, R.raw.mole);
        mSndCatch = MediaPlayer.create(context, R.raw.catch_mole);
        mSndPunch = MediaPlayer.create(context, R.raw.punch);
        mSndExplode = MediaPlayer.create(context, R.raw.explode);
        mSndBump = MediaPlayer.create(context, R.raw.bump);
    }
    protected  void initializeScoreAndLevel()
    {
        mScoreBoard = new Paint();
        mScoreBoard.setTextSize(35);
        mScoreBoard.setColor(Color.WHITE);
        mScore = 0;
        mLevel = new Paint();
        mLevel.setTextSize(35);
        mLevel.setColor(Color.WHITE);
    }
    protected void setupMoleThread()
    {
        mMoleThread = new MoleThread(this);
        mMoleThread.start();
    }
    //endregion

    //region onDraw-domain
    @Override protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (mMode == DRAW_GAME_BEGIN)
        {
            drawStartMenu();
        }
        else
        {
            setBackgroundResource(R.drawable.mole_background);
            drawHealthStatus(canvas);
            drawScoreboard(canvas);
            drawLevel(canvas);
            drawMoleAtRandomCoordinate(canvas);
        }
        drawPauseButton(canvas);

        // for debugging purpose
        drawButtonBoxes(canvas);
    }
    private void drawStartMenu()
    {
        setBackgroundResource(R.drawable.mole_intro);
    }
    private void drawMoleAtRandomCoordinate(Canvas canvas)
    {
        switch (mMode)
        {
            case DRAW_MOLE:
                canvas.drawBitmap(mMole, mX, mY, null);
                mSndMole.start();
                break;
            case DRAW_NO_MOLE:
                canvas.drawBitmap(mMoleEmpty, mX, mY, null);
                break;
            case DRAW_MOLE_HIT:
                canvas.drawBitmap(mMoleHit, mX, mY, null);
                break;
            case DRAW_BOMB:
                canvas.drawBitmap(mBomb, mX, mY, null);
                break;
            case DRAW_BOMB_EXPLODE:
                canvas.drawBitmap(mBombExplode, mX, mY, null);
                mSndExplode.start();
                break;
            case HALT_GAME:
            case GAME_OVER:
                Log.d("GAME_OVER", "at game over");
                setBackgroundResource(R.drawable.mole_game_over);
                canvas.drawBitmap(mGameOver, mWidth/2-50, mHeight/2, null);
                canvas.drawBitmap(mReplayButton, mReplayStartX, mReplayStartY, null);
                canvas.drawBitmap(mGoToMenuButton, mGoToMenuStartX, mGoToMenuStartY, null);
                break;
            case PAUSE_GAME:
                Log.d("PAUSE_GAME", "at pause");
                setBackgroundResource(R.drawable.mole_pause);
                canvas.drawBitmap(mReplayButtonLight, mReplayStartX, mReplayStartY, null);
//                canvas.drawText("replay", mReplayStartX, mReplayStartY+20, null);
                canvas.drawBitmap(mGoToMenuButtonLight, mGoToMenuStartX, mGoToMenuStartY, null);
//                canvas.drawText("go to menu", mGoToMenuStartX, mGoToMenuStartY+20, null);
            default:
                break;
        }
    }
    private void drawScoreboard(Canvas canvas)
    {
        canvas.drawText("Score:\t", mWidth-350, mHeight-90, mScoreBoard);
        canvas.drawBitmap(mNum[mScore%10], mWidth-200, mHeight-100-25, null);
        if (mScore>=10) canvas.drawBitmap(mNum[mScore/10%10], mWidth-250, mHeight-100-25, null);
    }
    private void drawLevel(Canvas canvas)
    {
        canvas.drawText("Level:\t", 90, mHeight-90, mLevel);
        canvas.drawBitmap(mNum[mLevelIndex%10], 190, mHeight-100-25, null);
    }
    private void drawHealthStatus(Canvas canvas)
    {
        for (int i=0; i<5; i++)
        {
            canvas.drawBitmap(i < mHealthNum ? mHealthUp : mHealthDown, mWidth-110, 85*(i+1) + 9*i, null);
        }
    }
    private void drawPauseButton(Canvas canvas)
    {
        canvas.drawBitmap(mMode != PAUSE_GAME ? mPauseButton : mPauseButtonLight, mPauseStartX, mPauseStartY, null);
    }
    private void drawButtonBoxes(Canvas canvas)
    {
        Paint box1= new Paint();
        box1.setStyle(Paint.Style.STROKE);
        box1.setStrokeWidth(15);
        canvas.drawRect(mReplayStartX, mReplayStartY, mReplayStartX+mButtonDimen+12, mReplayStartY+mButtonDimen+12, box1);

        Paint box2= new Paint();
        box2.setStyle(Paint.Style.STROKE);
        box2.setStrokeWidth(15);
        canvas.drawRect(mGoToMenuStartX, mGoToMenuStartY, mGoToMenuStartX+mButtonDimen+12, mGoToMenuStartY+mButtonDimen+12, box2);

        Paint box3 = new Paint();
        box3.setStyle(Paint.Style.STROKE);
        box3.setStrokeWidth(15);
        canvas.drawRect(mPauseStartX, mPauseStartY, mPauseStartX+mButtonDimen+12, mPauseStartY+mButtonDimen+12, box3);
    }
    //endregion

    @Override public boolean onTouchEvent(MotionEvent event)
    {
        mTouchX = (int)event.getX();
        mTouchY = (int)event.getY();

        Rect hitbox = new Rect(mX-30, mY-30, mX+180, mY+180);
        Rect replayBox = new Rect(mReplayStartX, mReplayStartY, mReplayStartX+mButtonDimen+12, mReplayStartY+mButtonDimen+12);
        Rect gotomenuBox = new Rect(mGoToMenuStartX, mGoToMenuStartY, mGoToMenuStartX+mButtonDimen+12, mGoToMenuStartY+mButtonDimen+12);
        Rect pauseBox = new Rect(mPauseStartX, mPauseStartY, mPauseStartX+mButtonDimen+12, mPauseStartY+mButtonDimen+12);

        if (  mHealthNum > 0  )
        {
            // game is still on.
            if (  event.getAction() == MotionEvent.ACTION_DOWN  )
            {
                if (hitbox.contains(mTouchX, mTouchY) && mMode == DRAW_MOLE)
                {
                    mMode = DRAW_MOLE_HIT;
                    mScore++;
                    mSndCatch.start();
                    mSndPunch.start();
                }
                else if (  pauseBox.contains(mTouchX, mTouchY)  )
                {
                    mMode = PAUSE_GAME;
                    if (mMoleThread.isPaused) {  mMoleThread.resumed();  }
                    else                      {  mMoleThread.paused();  }

                    // Toast.makeText(this.getContext(), "pause Button touched", Toast.LENGTH_SHORT).show();
                }
                else if (  !(mMode == PAUSE_GAME)  )
                {
                    mSndBump.start();
                    mHealthNum--;
                }
            }
        }
        else if (  mHealthNum == 0  )
        {
            // Game stops and shows gameover-scene once.
            mMode = GAME_OVER;
            mSndExplode.start();
            mHealthNum--;
        }
        else
        {
            // Wait for users to press restart or go to menu button.
            mMode = HALT_GAME;
            if (  event.getAction() == MotionEvent.ACTION_DOWN  )
            {
                if (  replayBox.contains(mTouchX, mTouchY)  )
                {
                    mMode = DRAW_MOLE;
                    mHealthNum = 5;
                    setupMoleThread();
                    Toast.makeText(this.getContext(), "replay Button touched", Toast.LENGTH_SHORT).show();
                }
                else if (  gotomenuBox.contains(mTouchX, mTouchY)  )
                {
                    Toast.makeText(this.getContext(), "gotomenu Button touched", Toast.LENGTH_SHORT).show();
                }
            }
        }
        invalidate();
        return true;
    }


}
