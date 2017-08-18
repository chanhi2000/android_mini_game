package com.markiiimark.minigames.MatchCards;

import android.util.Log;

import java.util.Objects;

/**
 * Created by admin on 2017-08-18.
 */

public class CardThread extends Thread
{
    private MatchCardsView mView;
    volatile boolean running = true;
    volatile private boolean paused = false;
    private Object lock = new Object();

    private final static int
        GAME_SHOW_STAGE = -1,
        GAME_READY = 0,
        GAME_START = 1,
        GAME_PASS = 2,
        GAME_SHOW_RESULT = 3,
        GAME_END = 4;

    //region Instantiation
    CardThread(MatchCardsView view)
    {
        super();
        mView = view;
    }
    ///endregion

    //region Thread
    @Override public void run()
    {
//        super.run();
        while(true)
        {
            if (!running) return;               // shut down thread
            else
            {
                try
                {
                    mView.checkMatch();
                    synchronized (lock)
                    {
                        while(paused)
                        {
                            lock.wait();
                        }
                    }
                }
                catch (InterruptedException e)
                {
                    Log.e("ThreadError", e.getMessage());
                }
            }
        }

    }
    //endregion

    public void setPaused(boolean pauseState)
    {
        synchronized (lock)
        {
            paused = pauseState;
            lock.notify();
        }
    }

}
