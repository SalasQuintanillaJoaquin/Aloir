package com.miempresa.aloir.audio;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import java.util.Arrays;

/** Sonidos cortos de acierto/error y una vibración breve al fallar. */
public class Feedback {

    private final Context context;
    private final ToneSynth synth = new ToneSynth();

    public Feedback(Context context) {
        this.context = context.getApplicationContext();
    }

    public void correct() {
        synth.play(Arrays.asList(new int[]{84}, new int[]{88, 91}), 140, 10, 0.45f, null);
    }

    public void wrong() {
        synth.play(Arrays.asList(new int[]{52, 58}, new int[]{51, 57}), 160, 20, 0.4f, null);
        vibrate(160);
    }

    public void finished() {
        synth.play(Arrays.asList(new int[]{72}, new int[]{76}, new int[]{79}, new int[]{72, 76, 79, 84}),
                170, 0, 0.45f, null);
    }

    @SuppressWarnings("deprecation")
    private void vibrate(long ms) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null || !vibrator.hasVibrator()) return;
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(ms);
        }
    }

    public void release() {
        synth.release();
    }
}
