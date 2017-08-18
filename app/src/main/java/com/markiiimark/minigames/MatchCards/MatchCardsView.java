package com.markiiimark.minigames.MatchCards;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.markiiimark.minigames.R;

import java.util.Random;

/**
 * Created by admin on 2017-08-02.
 */

public class MatchCardsView extends View
{
    private Bitmap mCardBack;
    private Bitmap mGamePrize;
    private Bitmap mPressToStart;
    private Bitmap mGameOver;
    private Bitmap mStageClear;

    private Card mCardShuffles[][];
    private Card mCardSelected0, mCardSelected1;
    private Bitmap[][] mCardBitmaps;
    private static final int mCardmages[][] = // 12 x 4
    {
            {  R.drawable.card01a, R.drawable.card01b, R.drawable.card01c, R.drawable.card01d  },
            {  R.drawable.card02a, R.drawable.card02b, R.drawable.card02c, R.drawable.card02d  },
            {  R.drawable.card03a, R.drawable.card03b, R.drawable.card03c, R.drawable.card03d  },
            {  R.drawable.card04a, R.drawable.card04b, R.drawable.card04c, R.drawable.card04d  },
            {  R.drawable.card05a, R.drawable.card05b, R.drawable.card05c, R.drawable.card05d  },
            {  R.drawable.card06a, R.drawable.card06b, R.drawable.card06c, R.drawable.card06d  },
            {  R.drawable.card07a, R.drawable.card07b, R.drawable.card07c, R.drawable.card07d  },
            {  R.drawable.card08a, R.drawable.card08b, R.drawable.card08c, R.drawable.card08d  },
            {  R.drawable.card09a, R.drawable.card09b, R.drawable.card09c, R.drawable.card09d  },
            {  R.drawable.card10a, R.drawable.card10b, R.drawable.card10c, R.drawable.card10d  },
            {  R.drawable.card11a, R.drawable.card11b, R.drawable.card11c, R.drawable.card11d  },
            {  R.drawable.card12a, R.drawable.card12b, R.drawable.card12c, R.drawable.card12d  }
    };
    private int mStageNumber = 1;
    private Bitmap[] mStageNumberBitmaps;
    private static final int mStageNumberImages[] =
    {
            R.drawable.n1, R.drawable.n2, R.drawable.n3, R.drawable.n4, R.drawable.n5,
            R.drawable.n6, R.drawable.n7, R.drawable.n8, R.drawable.n9
    };

    protected CardThread mThread;

    private final static String CARD_GAME_VIEW_TAG = "At CardGameView";
    private final static String CARD_THREAD_ERROR= "At Card Thread";

    private float  mCardOffsetX, mCardOffsetY, mPrizeOffsetX=10;

    private int  mGameState = GAME_SHOW_STAGE;
    private final static int
            GAME_SHOW_STAGE = -1,
            GAME_READY = 0,
            GAME_START = 1,
            GAME_PASS = 2,                      // Show results & stats
            GAME_SHOW_RESULT = 3,
            GAME_END = 4;                       // Game over

    private final static int NUMCOL = 3;
    private final static int NUMROW = 2;
    private int mNumCardsRemaining;

    private int[][] mPairs;
    private MediaPlayer mSoundHit0, mSoundHit1, mThemeSong;


    public MatchCardsView(Context context)
    {
        super(context);
        setBackgroundResource(R.drawable.background);
        initializeDeck();
        initializeBitmapImages();
        initializeSounds(context);
        shuffleCards(50);
        initializeThread();
    }
    private void initializeDeck() {

        reconfigureDeck();

        mPairs = null;
        mPairs = new int[12][2];
        for (int i=0; i<12; i++)
        {
            mPairs[i] = generateTwoDifferentRandom(0, 3);
        }
        // STAGE 1
        if (mStageNumber == 1)
        {
            assignPairToDeckVertical(0,0, Card.AUG);
            assignPairToDeckVertical(0,1, Card.SEP);
            assignPairToDeckVertical(0,2, Card.MAR);
        }
        else if (mStageNumber == 2)
        {
            assignPairToDeckHorizontal(0,0, Card.JAN);
            assignPairToDeckHorizontal(0,2, Card.FEB);
            assignPairToDeckHorizontal(1,0, Card.MAY);
            assignPairToDeckHorizontal(1,2, Card.OCT);
            assignPairToDeckHorizontal(2,0, Card.JUL);
            assignPairToDeckHorizontal(2,2, Card.AUG);
        }
        else if (mStageNumber == 3)
        {
            assignPairToDeckVertical(0,0, Card.DEC);
            assignPairToDeckVertical(2,0, Card.OCT);
            assignPairToDeckVertical(0,1, Card.APR);
            assignPairToDeckVertical(2,1, Card.JAN);
            assignPairToDeckVertical(0,2, Card.FEB);
            assignPairToDeckVertical(2,2, Card.JUN);
            assignPairToDeckVertical(0,3, Card.MAR);
            assignPairToDeckVertical(2,3, Card.NOV);
            assignPairToDeckVertical(0,4, Card.MAY);
            assignPairToDeckVertical(2,4, Card.JUL);
        }
    }
    private void reconfigureDeck()
    {
        mCardShuffles = null;
        mCardShuffles = new Card[NUMROW+mStageNumber-1][NUMCOL+mStageNumber-1];
        resetNumCardsRemaining();
    }
    private void resetNumCardsRemaining()
    {
        mNumCardsRemaining = (NUMCOL+mStageNumber-1) * (NUMROW+mStageNumber-1);
    }
    private void assignPairToDeckHorizontal(int startRow, int startCol, int cardSuit)
    {
        int col = startCol;
        mCardShuffles[startRow][col] = new Card(  cardSuit,  mPairs[cardSuit][0]  );
        mCardShuffles[startRow][col+1] = new Card(  cardSuit, mPairs[cardSuit][1]  );
    }

    private void assignPairToDeckVertical(int startRow, int startCol, int cardSuit)
    {
        int row = startRow;
        mCardShuffles[row][startCol] = new Card(  cardSuit, mPairs[cardSuit][0]  );
        mCardShuffles[row+1][startCol] = new Card(  cardSuit, mPairs[cardSuit][1]  );
    }

    private void initializeBitmapImages()
    {
        mCardBack = BitmapFactory.decodeResource(getResources(), R.drawable.backs);

        mCardBitmaps = new Bitmap[12][4];
        for (int m=0; m<12; m++)
        {
            for (int n=0; n<4; n++)
            {
                mCardBitmaps[m][n] = BitmapFactory.decodeResource(getResources(), mCardmages[m][n]);
            }
        }

        mStageNumberBitmaps = new Bitmap[9];
        for (int i=0; i<9; i++)
        {
            mStageNumberBitmaps[i] = BitmapFactory.decodeResource(getResources(), mStageNumberImages[i]);
        }

        mGamePrize = BitmapFactory.decodeResource(getResources(), R.drawable.cup);
        mPressToStart = BitmapFactory.decodeResource(getResources(), R.drawable.press);
        mStageClear = BitmapFactory.decodeResource(getResources(), R.drawable.stage_clear);
        mGameOver = BitmapFactory.decodeResource(getResources(), R.drawable.game_over_matchcards);
    }
    private void initializeSounds(Context context)
    {
        mSoundHit0 = MediaPlayer.create(context, R.raw.punch);
        mSoundHit1 = MediaPlayer.create(context, R.raw.punch);
        mThemeSong = MediaPlayer.create(context, R.raw.theme);
    }
    private int[] generateTwoDifferentRandom(int min, int max)
    {
        int a = generateRandom(min, max);
        int b = generateRandom(min, max);
        if (  a != b  )
        {
            int[] set = {a, b};
            return set;
        }
        else
        {
            return generateTwoDifferentRandom(min, max);
        }
    }
    private int generateRandom(int min, int max)
    {
        return new Random().nextInt(  max-min+1  ) + min;
    }
    private void shuffleCards(int numOfSwap)
    {
        int x, y;
        for (int i=0; i<numOfSwap; i++) {
            x=(int)(Math.random()*(NUMCOL+mStageNumber-1));
            y=(int)(Math.random()*(NUMROW+mStageNumber-1));
            mCardSelected0 = mCardShuffles[y][x];

            x=(int)(Math.random()*(NUMCOL+mStageNumber-1));
            y=(int)(Math.random()*(NUMROW+mStageNumber-1));
            mCardSelected1 = mCardShuffles[y][x];
            swapCards(mCardSelected0, mCardSelected1);
        }
        mCardSelected0 = null;
        mCardSelected1 = null;
    }
    private void swapCards(Card c1, Card c2)
    {
        int tmp = c1.getCardSuit();
        c1.setCardSuit(c2.getCardSuit());
        c2.setCardSuit(tmp);
    }
    private void initializeThread()
    {
        mThread = new CardThread(this);
        mThread.start();
    }


    @Override protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (  mGameState == GAME_SHOW_STAGE  || mGameState == GAME_END  ) {  drawStage(canvas);  }
//        else if (  mGametate == GAME_SHOW_RESULT  ) {
        else
        {
            if (  mStageNumber > 1  ) drawPrize(canvas);
            if (  mGameState != GAME_END  ) drawDeck(canvas);
        }
    }
    private void drawStage(Canvas canvas)
    {
        if (  mStageNumber > 1  )
        {
            canvas.drawBitmap(  mStageClear, getCenterBitmapPosX(mStageClear), getCenterBitmapPosY(mStageClear)-350, null);
            canvas.drawBitmap(  mStageNumberBitmaps[mStageNumber-2], getCenterBitmapPosX(mStageNumberBitmaps[mStageNumber-2]),  getCenterBitmapPosY(mStageNumberBitmaps[mStageNumber-2])-280, null);
        }

        if (  mGameState == GAME_END  )
        {
            drawGameOver(canvas);
        }
        else
        {
            canvas.drawBitmap(  mStageNumberBitmaps[mStageNumber-1], getCenterBitmapPosX(mStageNumberBitmaps[mStageNumber-1]), getCenterBitmapPosY(mStageNumberBitmaps[mStageNumber-1])-30, null  );
            canvas.drawBitmap(  mPressToStart, getCenterBitmapPosX(mPressToStart), getCenterBitmapPosY(mPressToStart)+mStageNumberBitmaps[mStageNumber-1].getHeight()+30, null);
        }
    }
    private void drawGameOver(Canvas canvas)
    {
        canvas.drawBitmap(mGameOver, getCenterBitmapPosX(mGameOver), getCenterBitmapPosY(mGameOver)+320, null);
    }
    private void drawDeck(Canvas canvas)
    {
        for (int j=0; j<NUMROW+mStageNumber-1; j++)
        {
            for (int i=0; i<NUMCOL+mStageNumber-1; i++)
            {
                if (  cardShouldOpen(  mCardShuffles[j][i].getCardState()  )  )
                {
                    canvas.drawBitmap(
                            mCardBitmaps[mCardShuffles[j][i].getCardSuit()][mCardShuffles[j][i].getCardIndex()],
                            getCardPosX(i),
                            getCardPosY(j),
                            null
                    );
                }
                else
                {
                    canvas.drawBitmap(mCardBack, getCardPosX(i), getCardPosY(j), null);
                }
            }
        }
    }
    private void drawPrize(Canvas canvas)
    {
        for(int i=1; i<mStageNumber; i++)
        {
            canvas.drawBitmap(  mGamePrize, getPrizePosX(i), getPrizePosY(), null  );
        }
    }
    private Boolean cardShouldOpen(int state)
    {
        return (state == Card.UNHIDE  || state == Card.PLAYER_OPEN || state == Card.MATCH);
    }

    @Override public boolean onTouchEvent(MotionEvent event)
    {
        if (mGameState == GAME_SHOW_STAGE)
        {
            mGameState = GAME_READY;
            mThemeSong.start();
        }
        else if (  mGameState == GAME_READY  )
        {
            setAllCardsState(Card.HIDE);
            mGameState = GAME_START;
        }
        else if (  mGameState == GAME_START  )
        {
            // Exit TouchEvent right away if more than 2 cards are selected at a moment
            if (  mCardSelected0 != null && mCardSelected1 != null  )   return true;    //

            int pX = (int)event.getX();
            int pY = (int)event.getY();

            for (int j=0; j<NUMROW+mStageNumber-1; j++)
            {
                for (int i=0; i<NUMCOL+mStageNumber-1; i++)
                {
                    Rect mCardRect = new Rect(
                            (int)getCardPosX(i),    (int)getCardPosY(j),
                            (int)getCardPosX(i)+mCardBitmaps[0][0].getWidth(),
                            (int)getCardPosY(j)+mCardBitmaps[0][0].getHeight()
                    );

                    if (  mCardRect.contains(pX, pY)  )
                    {
                        if (  mCardShuffles[j][i].getCardState() != Card.MATCH  )
                        {
                            if (  mCardSelected0 == null  )
                            {
                                mSoundHit0.start();
                                mCardSelected0 = mCardShuffles[j][i];
                                mNumCardsRemaining -= 1;
                            }
                            else
                            {
                                if (  mCardSelected0 != mCardShuffles[j][i]  )
                                {
                                    mSoundHit1.start();
                                    mCardSelected1 = mCardShuffles[j][i];
                                    mNumCardsRemaining -= 1;
                                }
                            }
                            mCardShuffles[j][i].setCardState(  Card.PLAYER_OPEN  );
                        }
                    }
                }
            }
            if (mNumCardsRemaining == 1) {  mGameState = GAME_PASS;  }
        }
        else if (  mGameState == GAME_PASS  )
        {
            mStageNumber++;
            setGameState(GAME_SHOW_STAGE);

            if (mStageNumber <= 3)
            {
                initializeDeck();
                shuffleCards(50*mStageNumber*10);
            }
            else
            {
                setGameState(GAME_END);
            }

        }
        else if (  getGameState() == GAME_SHOW_RESULT  )
        {

        }

        invalidate();
        return super.onTouchEvent(event);
    }
    private float getCardPosX(int indexCol)
    {
        mCardOffsetX = (  this.getWidth()-mCardBitmaps[0][0].getWidth()*(NUMCOL+mStageNumber-1)  ) / (NUMCOL+mStageNumber);
        return mCardOffsetX*(indexCol+1) + mCardBitmaps[0][0].getWidth()*indexCol;
    }
    private float getCardPosY(int indexRow)
    {
        mCardOffsetY = (  (  this.getHeight()*2/3  )-mCardBitmaps[0][0].getHeight()*(NUMROW+mStageNumber-1)  ) / (NUMROW+mStageNumber);
        return this.getHeight()/3 + mCardOffsetY*(indexRow+1) + mCardBitmaps[0][0].getHeight()*indexRow;
    }
    private float getCenterBitmapPosX(Bitmap bitmap)
    {
        return (  this.getWidth() - bitmap.getWidth()  )/2;
    }
    private float getCenterBitmapPosY(Bitmap bitmap)
    {
        return (  this.getHeight() - bitmap.getHeight()  )/2;
    }
    private float getPrizePosX(int index)
    {
        return this.getWidth() - (mGamePrize.getWidth()+mPrizeOffsetX)*index ;
    }
    private float getPrizePosY()
    {
        return mPrizeOffsetX;
    }
    protected void setAllCardsState(int state)
    {
        for (int j=0; j<NUMROW+mStageNumber-1; j++)
        {
            for (int i=0; i<NUMCOL+mStageNumber-1; i++)
            {
                mCardShuffles[j][i].setCardState(state);
            }
        }
    }
    public void checkMatch()
    {
        if (  (mCardSelected0 != null && mCardSelected1 != null) &&  mGameState == GAME_START  )
        {
            if (  mCardSelected0.getCardSuit()== mCardSelected1.getCardSuit()  )
            {
                mCardSelected0.setCardState(  Card.MATCH  );
                mCardSelected1.setCardState(  Card.MATCH  );
                mCardSelected0 = null;
                mCardSelected1 = null;
            }
            else
            {
                mNumCardsRemaining += 2;
                try {
                    Thread.sleep(500);
                    mCardSelected0.setCardState(  Card.HIDE  );
                    mCardSelected1.setCardState(  Card.HIDE  );
                    mCardSelected0 = null;
                    mCardSelected1 = null;
                } catch (InterruptedException e) {
                    Log.e(CARD_THREAD_ERROR, e.toString());
                }

            }
            postInvalidate();
        }
    }

    private int getGameState()
    {
        return mGameState;
    }
    private void setGameState(int state)
    {
        mGameState = state;
    }

    //region CardThread
    protected void stopAllThreads()
    {
        mThemeSong.stop();
        mThread.running = false;
    }
    protected void pauseAllThreads()
    {
        mThemeSong.pause();
        mThread.setPaused(true);
    }
    protected void resumeAllThreads()
    {
        mThemeSong.start();
        mThread.setPaused(false);
    }
    //endregion


}
