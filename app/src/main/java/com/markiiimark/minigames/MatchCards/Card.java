package com.markiiimark.minigames.MatchCards;

/**
 * Created by admin on 2017-08-18.
 */

public class Card
{
    private int mState;
    private int mSuit;
    private int mIndex;

    final static int
        JAN = 0,
        FEB = 1,
        MAR = 2,
        APR = 3,
        MAY = 4,
        JUN = 5,
        JUL = 6,
        AUG = 7,
        SEP = 8,
        OCT = 9,
        NOV = 10,
        DEC = 11;

    final static int
        HIDE = 1,
        UNHIDE = 2,
        PLAYER_OPEN = 3,
        MATCH = 4;
    Card (int suit, int index)
    {
        mState = UNHIDE;
        setCardSuit(suit);
        setCardIndex(index);
    }

    public int getCardState() {  return mState;  }
    public void setCardState(int state) {  mState = state;  }

    public int getCardSuit() {  return mSuit;  }
    public void setCardSuit(int suit) {  mSuit = suit;  }

    public int getCardIndex() {  return mIndex;  }
    public void setCardIndex(int index) {  mIndex = index;  }
}
