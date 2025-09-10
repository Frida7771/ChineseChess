package com.frida.chinese.jchess.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.SnackbarUtils;
import com.blankj.utilcode.util.StringUtils;
import com.frida.chinese.jchess.R;
import com.frida.chinese.jchess.databinding.ActivityMainBinding;
import com.frida.chinese.jchess.game.GameConfig;
import com.frida.chinese.jchess.game.GameLogic;
import com.frida.chinese.jchess.game.IGameCallback;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements IGameCallback {

    private ActivityMainBinding binding; // ★ 新增：ViewBinding
    private SoundPool mSoundPool;
    private LinkedList<Integer> mSoundList;
    private GameLogic mGameLogic;
    private SharedPreferences mPreference;
    private boolean mSoundEnable;
    private int mHandicapIndex;
    private boolean mComputerFlip;
    private int mPieceStyle;
    private int mAILevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ★ 使用 ViewBinding 替代 setContentView + ButterKnife
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        loadDefaultConfig();
        initSoundPool();
        initGameLogic();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.main_menu_exit) {
            finish();
        } else if (itemId == R.id.main_menu_retract) {
            mGameLogic.retract();
        } else if (itemId == R.id.main_menu_restart) {
            mGameLogic.restart(mComputerFlip, mHandicapIndex);
            showMessage(getString(R.string.new_game_started));
        } else if (itemId == R.id.main_menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (itemId == R.id.main_menu_about) {
            startActivity(new Intent(this, AboutActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDefaultConfig();
        mGameLogic.setLevel(mAILevel);
        binding.gameBoard.setPieceTheme(mPieceStyle);
        binding.gameBoard.invalidate();
    }

    @Override
    protected void onDestroy() {
        if (mSoundPool != null) {
            mSoundPool.release();
        }
        mPreference.edit().putString(GameConfig.PREF_LAST_FEN, mGameLogic.getCurrentFen()).apply();
        super.onDestroy();
    }

    private void loadDefaultConfig() {
        mSoundEnable = mPreference.getBoolean(getString(R.string.pref_sound_key), true);
        mHandicapIndex = Integer.parseInt(mPreference.getString(getString(R.string.pref_handicap_key), "0"));
        mComputerFlip = mPreference.getBoolean(getString(R.string.pref_who_first_key), false);
        mPieceStyle = Integer.parseInt(mPreference.getString(getString(R.string.pref_piece_style_key), "0"));
        mAILevel = Integer.parseInt(mPreference.getString(getString(R.string.pref_level_key), "0"));
    }

    private void initSoundPool() {
        mSoundList = new LinkedList<>();
        int poolSize = GameConfig.SOUND_RES_ARRAY.length;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSoundPool = new SoundPool.Builder().setMaxStreams(poolSize).build();
        } else {
            mSoundPool = new SoundPool(poolSize, AudioManager.STREAM_MUSIC, 0);
        }
        for (int res : GameConfig.SOUND_RES_ARRAY) {
            mSoundList.add(mSoundPool.load(this, res, 1));
        }
    }

    private void initGameLogic() {
        mGameLogic = binding.gameBoard.getGameLogic(); // ★ 从 binding 里取 GameBoardView
        mGameLogic.setCallback(this);
        mGameLogic.setLevel(mAILevel);
        binding.gameBoard.setPieceTheme(mPieceStyle);

        // load last saved game
        String lastFen = mPreference.getString(GameConfig.PREF_LAST_FEN, "");
        if (StringUtils.isEmpty(lastFen)) {
            mGameLogic.restart(mComputerFlip, mHandicapIndex);
        } else {
            showMessage(getString(R.string.load_last_game_finish));
            mGameLogic.restart(mComputerFlip, lastFen);
        }
    }

    @Override
    public void postPlaySound(final int soundIndex) {
        if (mSoundPool != null && mSoundEnable) {
            int soundId = mSoundList.get(soundIndex);
            mSoundPool.play(soundId, 1, 1, 0, 0, 1);
        }
    }

    @Override
    public void postShowMessage(final String message) {
        runOnUiThread(() -> showMessage(message));
    }

    private void showMessage(String message) {
        SnackbarUtils.with(binding.gameBoard) // ★ binding 替代 mGameBoard
                .setDuration(SnackbarUtils.LENGTH_LONG)
                .setMessage(message)
                .show();
    }

    @Override
    public void postShowMessage(int messageId) {
        postShowMessage(getString(messageId));
    }

    @Override
    public void postStartThink() {
        runOnUiThread(() -> binding.gameProgress.setVisibility(View.VISIBLE));
    }

    @Override
    public void postEndThink() {
        runOnUiThread(() -> binding.gameProgress.setVisibility(View.GONE));
    }
}
