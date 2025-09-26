package com.example.kindvinapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kindvinapp.R;
import com.example.kindvinapp.utils.SoundManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500; // 2.5 секунды

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView appNameText = findViewById(R.id.splash_app_name);
        ImageView diceImage = findViewById(R.id.splash_dice_icon);

        Animation textAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in_scale);
        Animation diceAnim = AnimationUtils.loadAnimation(this, R.anim.dice_roll_splash);

        appNameText.startAnimation(textAnim);
        diceImage.startAnimation(diceAnim);

        SoundManager.playSplashIntro(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (getIntent().getSerializableExtra("TEAMS_LIST") == null ||
                    getIntent().getSerializableExtra("BOARD_CONFIG") == null) {
                Intent intent = new Intent(SplashActivity.this, TeamEditorActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
            mainIntent.putExtras(getIntent().getExtras());
            startActivity(mainIntent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, SPLASH_DURATION);
    }
}