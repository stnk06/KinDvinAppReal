package com.example.kindvinapp.utils;

import android.content.Context;
import android.media.MediaPlayer;
import com.example.kindvinapp.R;

public class SoundManager {

    private static MediaPlayer winMusicPlayer;

    private static void playSoundEffect(Context context, int resId) {
        MediaPlayer player = MediaPlayer.create(context, resId);
        if (player != null) {
            player.setOnCompletionListener(MediaPlayer::release);
            player.start();
        }
    }

    public static void playSplashIntro(Context context) {
        playSoundEffect(context, R.raw.splash_intro);
    }

    public static void playDiceRoll(Context context) {
        playSoundEffect(context, R.raw.dice_roll);
    }

    public static void playPawnMove(Context context) {
        playSoundEffect(context, R.raw.pawn_move);
    }

    public static void playTeleport(Context context) {
        playSoundEffect(context, R.raw.teleport);
    }

    public static void playChallenge(Context context) {
        playSoundEffect(context, R.raw.challenge);
    }

    public static void playAttack(Context context) {
        playSoundEffect(context, R.raw.attack);
    }

    public static void playWinMusic(Context context) {
        if (winMusicPlayer != null) {
            winMusicPlayer.release();
        }
        winMusicPlayer = MediaPlayer.create(context, R.raw.win_music);
        if (winMusicPlayer != null) {
            winMusicPlayer.setLooping(true);
            winMusicPlayer.start();
        }
    }

    public static void stopWinMusic() {
        if (winMusicPlayer != null && winMusicPlayer.isPlaying()) {
            winMusicPlayer.stop();
            winMusicPlayer.release();
            winMusicPlayer = null;
        }
    }

    /**
     * Освобождает только ресурсы, которые могут быть активны в фоне (музыка).
     * Короткие эффекты освобождаются автоматически.
     */
    public static void releaseAll() {
        if (winMusicPlayer != null) {
            winMusicPlayer.release();
            winMusicPlayer = null;
        }
    }
}

