package com.markiiimark.minigames.MatchCards;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MatchCardsActivity extends AppCompatActivity
{
    private MatchCardsView mView;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(  mView = new MatchCardsView(this)  );
    }

    @Override protected void onDestroy()
    {
        mView.stopAllThreads();
        super.onDestroy();
    }

    @Override protected void onResume()
    {
        mView.resumeAllThreads();
        super.onResume();
    }

    @Override protected void onPause()
    {
        mView.pauseAllThreads();
        super.onPause();
    }
}
