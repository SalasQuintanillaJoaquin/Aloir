package com.miempresa.aloir.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.miempresa.aloir.R;

/** Utilidades pequeñas de interfaz y animaciones reutilizables. */
public final class Ui {

    private Ui() {}

    public static int dp(Context context, float value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }

    public static int color(Context context, int res) {
        return ContextCompat.getColor(context, res);
    }

    /** Barra superior común: botón atrás y título. */
    public static void titleBar(ComponentActivity activity, CharSequence title) {
        ((TextView) activity.findViewById(R.id.titleText)).setText(title);
        activity.findViewById(R.id.titleBack).setOnClickListener(v -> activity.getOnBackPressedDispatcher().onBackPressed());
    }

    /** Pinta un MaterialButton con un color de fondo y texto oscuro legible. */
    public static void tint(MaterialButton button, int colorRes) {
        Context c = button.getContext();
        button.setBackgroundTintList(ColorStateList.valueOf(color(c, colorRes)));
        button.setTextColor(color(c, R.color.eg_ink));
        button.setIconTint(ColorStateList.valueOf(color(c, R.color.eg_ink)));
    }

    /** Tarjeta azul con borde de color y efecto al tocar. */
    public static Drawable card(Context context, int strokeColorRes) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color(context, R.color.eg_surface));
        shape.setCornerRadius(dp(context, 22));
        shape.setStroke(dp(context, 2), color(context, strokeColorRes));
        return new RippleDrawable(ColorStateList.valueOf(0x33FFFFFF), shape, null);
    }

    public static Drawable circle(Context context, int colorRes) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(color(context, colorRes));
        return shape;
    }

    /** Aparece creciendo con un pequeño rebote. */
    public static void popIn(View view, long delay) {
        view.setScaleX(0.6f);
        view.setScaleY(0.6f);
        view.setAlpha(0f);
        view.animate().scaleX(1f).scaleY(1f).alpha(1f)
                .setStartDelay(delay).setDuration(360)
                .setInterpolator(new OvershootInterpolator(2f)).start();
    }

    /** Entra desde abajo con fundido. */
    public static void riseIn(View view, long delay) {
        view.setTranslationY(dp(view.getContext(), 20));
        view.setAlpha(0f);
        view.animate().translationY(0).alpha(1f).setStartDelay(delay).setDuration(320).start();
    }

    /** Sacudida horizontal para respuestas incorrectas. */
    public static void shake(View view) {
        float d = dp(view.getContext(), 8);
        view.animate().translationX(d).setDuration(50).withEndAction(() ->
                view.animate().translationX(-d).setDuration(80).withEndAction(() ->
                        view.animate().translationX(d / 2).setDuration(70).withEndAction(() ->
                                view.animate().translationX(0).setDuration(60).start()).start()).start()).start();
    }

    /** Latido breve para destacar un cambio. */
    public static void pulse(View view) {
        view.animate().scaleX(1.15f).scaleY(1.15f).setDuration(120).withEndAction(() ->
                view.animate().scaleX(1f).scaleY(1f).setDuration(160).start()).start();
    }
}
