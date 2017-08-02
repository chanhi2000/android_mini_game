package com.markiiimark.minigames;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.markiiimark.minigames.CatchMoles.CatchMolesActivity;
import com.markiiimark.minigames.MatchCards.MatchCardsActivity;

public class MainActivity extends AppCompatActivity
{
    ImageView mCloud1, mCloud2, mCloud3, mWalkingPig1, mRisingPig1;
    Animation mCloudAnimation1, mCloudAnimation2, mCloudAnimation3, mOpening, mWalking, mRisingRightBottom, mRisingLeftBottom, mRisingBottom;

    FrameLayout mBackground;
    MediaPlayer mBackgroundMusic, mWalkSound, mRiseSound;

    Button mButtonCatchMoles, mButtonMatchCards;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBackground = (FrameLayout)findViewById(R.id.background);
        mCloud1 = (ImageView)findViewById(R.id.img_cloud);
        mCloud2 = (ImageView)findViewById(R.id.img_cloud2);
        mCloud3 = (ImageView)findViewById(R.id.img_cloud3);
        mWalkingPig1 = (ImageView)findViewById(R.id.img_pig1);
        mRisingPig1 = (ImageView)findViewById(R.id.img_pig2);
        mButtonCatchMoles = (Button)findViewById(R.id.btn_start_mole);
        mButtonMatchCards = (Button)findViewById(R.id.btn_start_match);

        mBackgroundMusic = MediaPlayer.create(getApplicationContext(), R.raw.bg);

        mOpening = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.opening) ;
        mButtonMatchCards.setVisibility(View.INVISIBLE);
        mButtonMatchCards.setVisibility(View.INVISIBLE);
        mOpening.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation)
            {

            }

            @Override public void onAnimationEnd(Animation animation)
            {
                mBackgroundMusic.start();
                mRisingPig1.setVisibility(View.INVISIBLE);
                mWalkingPig1.setVisibility(View.VISIBLE);
                mWalkingPig1.startAnimation(mWalking);
                mWalkSound.start();
            }

            @Override public void onAnimationRepeat(Animation animation)
            {

            }
        });

        mCloudAnimation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cloud);
        mCloudAnimation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cloud2);
        mCloudAnimation3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cloud3);
        mWalking = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.walking);
        mRisingRightBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rising_left_bottom);
        mRisingLeftBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rising_right_bottom);
        mRisingBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rising_bottom);

        mWalkSound = MediaPlayer.create(getApplicationContext(), R.raw.walk);
        mRiseSound = MediaPlayer.create(getApplicationContext(), R.raw.sideup);

        mBackground.startAnimation(mOpening);
        mCloud1.startAnimation(mCloudAnimation1);
        mCloud2.startAnimation(mCloudAnimation2);
        mCloud3.startAnimation(mCloudAnimation3);

        mWalking.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}

            @Override public void onAnimationEnd(Animation animation)
            {
                mWalkingPig1.setVisibility(View.INVISIBLE);
                mRisingPig1.setVisibility(View.VISIBLE);
                mRisingPig1.startAnimation(mRisingRightBottom);
                mRisingPig1.setRotation(-45);
                mRisingPig1.setVisibility(View.INVISIBLE);
                mRiseSound.start();
            }

            @Override public void onAnimationRepeat(Animation animation) {}
        });

        mRisingRightBottom.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}

            @Override public void onAnimationEnd(Animation animation)
            {
                mRisingPig1.setVisibility(View.VISIBLE);
                mRisingPig1.startAnimation(mRisingLeftBottom);
                mRisingPig1.setRotation(45);
                mRisingPig1.setVisibility(View.INVISIBLE);
                mRiseSound.start();
            }

            @Override public void onAnimationRepeat(Animation animation) {}
        });
        mRisingLeftBottom.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}

            @Override public void onAnimationEnd(Animation animation)
            {
                mRisingPig1.setVisibility(View.VISIBLE);
                mRisingPig1.startAnimation(mRisingBottom);
                mRisingPig1.setRotation(0);
                mRisingPig1.setVisibility(View.INVISIBLE);
                mRiseSound.start();
            }

            @Override public void onAnimationRepeat(Animation animation) {}
        });
        mRisingBottom.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}

            @Override public void onAnimationEnd(Animation animation)
            {
                mButtonCatchMoles.setVisibility(View.VISIBLE);
                mButtonMatchCards.setVisibility(View.VISIBLE);
            }

            @Override public void onAnimationRepeat(Animation animation) {}
        });
    }

    public void walkPig(View v)
    {
        mWalkingPig1.startAnimation(mWalking);
    }

    public void startGame(View v)
    {
        switch (  v.getTag().toString()  )
        {
            case "CatchMoles":
                Log.d("Activities", "Call CatchMoles ");
                Intent intentCatchMoles = new Intent(MainActivity.this, CatchMolesActivity.class);
                startActivity(intentCatchMoles);
                break;
            case "MatchCards":
                Log.d("Activities", "Call MatchCards ");
                Intent intentMatchCards = new Intent(MainActivity.this, MatchCardsActivity.class);
                startActivity(intentMatchCards);
                break;
            default:break;
        }
    }
}
