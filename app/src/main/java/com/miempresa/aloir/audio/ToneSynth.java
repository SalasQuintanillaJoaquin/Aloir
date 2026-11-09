package com.miempresa.aloir.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Sintetizador sencillo con timbre parecido a un piano eléctrico. Genera las notas en
 * memoria, así los ejercicios nuevos no necesitan archivos de audio.
 */
public class ToneSynth {

    public interface Listener {
        void onPlaybackStart();

        void onPlaybackEnd();
    }

    private static final int SAMPLE_RATE = 44100;
    private static final float[] HARMONICS = {1f, 0.45f, 0.22f, 0.12f, 0.06f};

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());
    private AudioTrack track;
    private Runnable pendingEnd;
    private int generation;

    public static double frequency(int midi) {
        return 440.0 * Math.pow(2, (midi - 69) / 12.0);
    }

    /** Toca una secuencia de pasos; cada paso es un acorde (o una sola nota). */
    public void play(List<int[]> steps, int noteMs, int gapMs, float volume, Listener listener) {
        final int myGeneration;
        synchronized (this) {
            myGeneration = ++generation;
        }
        stopTrack();
        executor.execute(() -> {
            short[] pcm = render(steps, noteMs, gapMs, volume);
            synchronized (this) {
                if (myGeneration != generation) return;
                startTrack(pcm, listener);
            }
        });
    }

    public void playNote(int midi, int ms, Listener listener) {
        play(java.util.Collections.singletonList(new int[]{midi}), ms, 0, 0.8f, listener);
    }

    private short[] render(List<int[]> steps, int noteMs, int gapMs, float volume) {
        int noteSamples = SAMPLE_RATE * noteMs / 1000;
        int stepSamples = SAMPLE_RATE * (noteMs + gapMs) / 1000;
        int total = stepSamples * (steps.size() - 1) + noteSamples;
        float[] mix = new float[total];

        for (int s = 0; s < steps.size(); s++) {
            int[] notes = steps.get(s);
            int offset = s * stepSamples;
            float perNote = volume / (float) Math.sqrt(Math.max(1, notes.length)) * 0.55f;
            for (int midi : notes) {
                double f = frequency(midi);
                for (int i = 0; i < noteSamples; i++) {
                    double t = (double) i / SAMPLE_RATE;
                    double env = envelope(i, noteSamples, t);
                    double sample = 0;
                    for (int h = 0; h < HARMONICS.length; h++) {
                        double fh = f * (h + 1);
                        if (fh > 12000) break;
                        // los armónicos agudos se apagan antes, como en un instrumento real
                        sample += HARMONICS[h] * Math.exp(-t * h * 1.6) * Math.sin(2 * Math.PI * fh * t);
                    }
                    mix[offset + i] += (float) (sample * env * perNote);
                }
            }
        }

        short[] pcm = new short[total];
        for (int i = 0; i < total; i++) {
            float v = Math.max(-1f, Math.min(1f, mix[i]));
            pcm[i] = (short) (v * Short.MAX_VALUE);
        }
        return pcm;
    }

    private static double envelope(int i, int length, double t) {
        int attack = SAMPLE_RATE / 120;
        int release = SAMPLE_RATE / 25;
        double env = Math.exp(-t * 2.2);
        if (i < attack) env *= (double) i / attack;
        if (i > length - release) env *= (double) (length - i) / release;
        return env;
    }

    private void startTrack(short[] pcm, Listener listener) {
        AudioTrack newTrack;
        try {
            newTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    pcm.length * 2, AudioTrack.MODE_STATIC);
            newTrack.write(pcm, 0, pcm.length);
            newTrack.play();
        } catch (IllegalStateException | IllegalArgumentException e) {
            return;
        }
        track = newTrack;
        long durationMs = pcm.length * 1000L / SAMPLE_RATE;
        main.post(() -> {
            if (listener != null) listener.onPlaybackStart();
        });
        pendingEnd = () -> {
            if (listener != null) listener.onPlaybackEnd();
        };
        main.postDelayed(pendingEnd, durationMs);
    }

    private synchronized void stopTrack() {
        if (pendingEnd != null) {
            main.removeCallbacks(pendingEnd);
            pendingEnd.run();
            pendingEnd = null;
        }
        if (track != null) {
            try {
                track.stop();
            } catch (IllegalStateException ignored) {
            }
            track.release();
            track = null;
        }
    }

    public void stop() {
        synchronized (this) {
            generation++;
        }
        stopTrack();
    }

    public void release() {
        stop();
        executor.shutdownNow();
    }
}
