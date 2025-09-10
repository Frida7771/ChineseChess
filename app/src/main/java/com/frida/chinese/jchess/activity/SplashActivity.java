package com.frida.chinese.jchess.activity;


import android.app.Activity;
import android.os.Bundle;

import com.blankj.utilcode.util.ActivityUtils;
import com.frida.chinese.jchess.R;
import com.frida.chinese.jchess.game.GameConfig;
import com.frida.chinese.jchess.xqwlight.Position;

import java.io.InputStream;


public class SplashActivity extends Activity {

    private static boolean mDataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (mDataLoaded) {
            startGame();
        } else {
            loadBookAndStartGame();
        }
    }

    private void loadBookAndStartGame() {
        new Thread() {
            @Override
            public void run() {
                try {
                    // do some loading job
                    InputStream is = getAssets().open(GameConfig.DAT_ASSETS_PATH);
                    Position.loadBook(is);
                    mDataLoaded = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> startGame());
            }
        }.start();
    }

    private void startGame() {
        ActivityUtils.startActivity(MainActivity.class);
        finish();
    }
}
