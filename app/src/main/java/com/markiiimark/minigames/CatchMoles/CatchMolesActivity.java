package com.markiiimark.minigames.CatchMoles;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class CatchMolesActivity extends AppCompatActivity
{
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(  new CatchMolesView(this)  );
    }
}
