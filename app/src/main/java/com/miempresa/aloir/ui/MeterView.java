package com.miempresa.aloir.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.core.content.ContextCompat;

import com.miempresa.aloir.R;

/** Barra vertical que se llena mientras suena el audio (las "barras" de Sonido 01 y 02). */
public class MeterView extends View {

    private final Paint track = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glow = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private float progress;
    private ValueAnimator animator;

    public MeterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        track.setColor(ContextCompat.getColor(context, R.color.eg_surface_high));
        setColor(ContextCompat.getColor(context, R.color.eg_neon));
    }

    public void setColor(int color) {
        fill.setColor(color);
        glow.setColor(color);
        glow.setAlpha(70);
        invalidate();
    }

    /** Llena la barra en el tiempo que dura el audio. */
    public void play(long durationMs) {
        stop();
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(Math.max(1, durationMs));
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(a -> {
            progress = (float) a.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    public void stop() {
        if (animator != null) animator.cancel();
        animator = null;
        progress = 0f;
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) animator.cancel();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth();
        float h = getHeight();
        float r = w / 2f;
        rect.set(0, 0, w, h);
        canvas.drawRoundRect(rect, r, r, track);
        if (progress > 0f) {
            float top = h - Math.max(w, h * progress);
            rect.set(0, top, w, h);
            canvas.drawRoundRect(rect, r, r, fill);
            // brillo en la punta de la barra
            canvas.drawCircle(w / 2f, top + r, r * 1.35f, glow);
        }
    }
}
