package com.miempresa.aloir.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.miempresa.aloir.R;

/** Teclado de una octava (DO a DO) con nombres en las teclas blancas. */
public class PianoView extends View {

    public interface OnKeyListener {
        void onKey(int midi);
    }

    private static final int[] WHITE = {0, 2, 4, 5, 7, 9, 11, 12};
    private static final String[] WHITE_NAMES = {"DO", "RE", "MI", "FA", "SOL", "LA", "SI", "DO"};
    /** Teclas negras: semitono y posición (después de qué tecla blanca). */
    private static final int[][] BLACK = {{1, 0}, {3, 1}, {6, 3}, {8, 4}, {10, 5}};

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint text = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private final float density;
    private int baseMidi = 60;
    private int pressed = -1;
    private OnKeyListener listener;

    public PianoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = getResources().getDisplayMetrics().density;
        text.setTextAlign(Paint.Align.CENTER);
        text.setTypeface(Typeface.DEFAULT_BOLD);
        text.setTextSize(13 * density);
        setContentDescription("Teclado de piano");
    }

    public void setBaseMidi(int midi) {
        baseMidi = midi;
        invalidate();
    }

    public void setOnKeyListener(OnKeyListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int semitone = hit(event.getX(), event.getY());
            if (semitone >= 0) {
                pressed = semitone;
                invalidate();
                if (listener != null) listener.onKey(baseMidi + semitone);
            }
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            pressed = -1;
            invalidate();
            performClick();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private int hit(float x, float y) {
        float whiteW = getWidth() / (float) WHITE.length;
        float blackH = getHeight() * 0.6f;
        if (y < blackH) {
            for (int[] b : BLACK) {
                float cx = whiteW * (b[1] + 1);
                if (Math.abs(x - cx) < whiteW * 0.32f) return b[0];
            }
        }
        int i = Math.max(0, Math.min(WHITE.length - 1, (int) (x / whiteW)));
        return WHITE[i];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth();
        float h = getHeight();
        float whiteW = w / WHITE.length;
        float gap = 3 * density;
        float r = 10 * density;
        float edge = 6 * density;
        int stroke = 0xFFB8C2E6;
        int green = ContextCompat.getColor(getContext(), R.color.eg_neon);

        for (int i = 0; i < WHITE.length; i++) {
            boolean down = pressed == WHITE[i];
            float left = i * whiteW + gap / 2;
            float right = (i + 1) * whiteW - gap / 2;
            paint.setColor(down ? ContextCompat.getColor(getContext(), R.color.eg_neon_dark) : stroke);
            rect.set(left, 0, right, h);
            canvas.drawRoundRect(rect, r, r, paint);
            paint.setColor(down ? green : 0xFFFFFFFF);
            rect.set(left, 0, right, h - (down ? edge / 2 : edge));
            canvas.drawRoundRect(rect, r, r, paint);
            text.setColor(down ? 0xFFFFFFFF : 0xFF6B7193);
            canvas.drawText(WHITE_NAMES[i], (left + right) / 2, h - edge - 14 * density, text);
        }
        for (int[] b : BLACK) {
            boolean down = pressed == b[0];
            float cx = whiteW * (b[1] + 1);
            float half = whiteW * 0.3f;
            float bh = h * 0.6f;
            paint.setColor(0xFF0B0D1F);
            rect.set(cx - half, -r, cx + half, bh);
            canvas.drawRoundRect(rect, r * 0.6f, r * 0.6f, paint);
            paint.setColor(down ? green : 0xFF2A2F57);
            rect.set(cx - half + 2 * density, -r, cx + half - 2 * density, bh - (down ? 2 : 6) * density);
            canvas.drawRoundRect(rect, r * 0.6f, r * 0.6f, paint);
        }
    }
}
