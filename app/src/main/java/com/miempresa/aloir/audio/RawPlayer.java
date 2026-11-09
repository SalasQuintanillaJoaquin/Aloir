package com.miempresa.aloir.audio;

import android.content.Context;
import android.media.MediaPlayer;

/** Reproduce los audios grabados de afinación (res/raw), uno a la vez. */
public class RawPlayer {

    public interface Listener {
        void onStart(int durationMs);

        void onEnd();
    }

    private final Context context;
    private MediaPlayer player;
    private Listener currentListener;

    public RawPlayer(Context context) {
        this.context = context.getApplicationContext();
    }

    public void play(int rawRes, Listener listener) {
        stop();
        player = MediaPlayer.create(context, rawRes);
        if (player == null) return;
        currentListener = listener;
        player.setOnCompletionListener(mp -> finish());
        player.start();
        if (listener != null) listener.onStart(player.getDuration());
    }

    private void finish() {
        Listener listener = currentListener;
        currentListener = null;
        if (player != null) {
            player.release();
            player = null;
        }
        if (listener != null) listener.onEnd();
    }

    public void stop() {
        if (player != null) {
            try {
                player.stop();
            } catch (IllegalStateException ignored) {
            }
        }
        finish();
    }
}
