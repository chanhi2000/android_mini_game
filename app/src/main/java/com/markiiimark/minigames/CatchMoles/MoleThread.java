package com.markiiimark.minigames.CatchMoles;

import android.util.Log;

/**
 * Created by admin on 2017-08-02.
 */

public class MoleThread extends Thread implements Runnable
{
    protected volatile boolean isPaused = false;
    private final Object pauseLock = new Object();

    CatchMolesView mCatchMolesView;
    public int idx;
    public int[] mSeq;

    public MoleThread(CatchMolesView view)
    {
        mCatchMolesView = view;
        mSeq = new int[]
        {
                mCatchMolesView.DRAW_NO_MOLE, mCatchMolesView.DRAW_MOLE, mCatchMolesView.DRAW_NO_MOLE
        };
        idx = 0;
    }

    @Override public void run()
    {
        for (int i=0; i<50; i++)
        {
            if (mCatchMolesView.mMode == mCatchMolesView.DRAW_MOLE_HIT)
            {
                try {  Thread.sleep(1000);  } catch (InterruptedException e) {}
                setMode(mCatchMolesView.DRAW_NO_MOLE);
                idx = 0;
            }
            if ( idx % 3 == 0)
            {
                mCatchMolesView.mX = (int)(Math.random() * 5) * (mCatchMolesView.mWidth)/5;
                mCatchMolesView.mY = (int)(Math.random() * 5) * (mCatchMolesView.mWidth+100)/5 + 200;
            }
            setMode(mSeq[idx % 3]);
            idx++;

            if (mCatchMolesView.mMode == mCatchMolesView.GAME_OVER || mCatchMolesView.mMode == mCatchMolesView.HALT_GAME)
            {
                Log.d("MoleThread", "Terminating the thread.");
//                while (  !isGameOver )  {  }
                break;
            }
            if (mCatchMolesView.mMode == mCatchMolesView.PAUSE_GAME)
            {
                Log.d("MoleThread", "Pause the thread.");
                synchronized (pauseLock)
                {
                    if (isPaused)
                    {
                        try {  pauseLock.wait();  }
                        catch (InterruptedException e) {  break;  }
                    }
                }
            }
        }
    }
    public void paused()
    {
        isPaused = true;
    }

    public void resumed()
    {
        synchronized (pauseLock)
        {
            isPaused = false;
            pauseLock.notifyAll();
        }
    }

    public void setMode(int mode)
    {
        mCatchMolesView.mMode = mode;
        mCatchMolesView.postInvalidate();
        try {  Thread.sleep(mode == mCatchMolesView.DRAW_MOLE ? 500 : 100);  }
        catch (InterruptedException e) {}
    }
}
