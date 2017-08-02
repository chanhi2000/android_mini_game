package com.markiiimark.minigames.MatchCards;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MatchCardsActivity extends AppCompatActivity
{
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(  new MatchCardsView(this)  );
    }
}
