package com.miempresa.aloir.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.miempresa.aloir.R;

/** Tarjetas (item_card) para las listas de ejercicios, herramientas y la guía. */
public final class Cards {

    private Cards() {}

    public static View create(Context context, ViewGroup parent, int iconRes, int colorRes,
                              CharSequence title, CharSequence text) {
        View card = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        card.setBackground(Ui.card(context, R.color.eg_stroke));
        card.findViewById(R.id.cardIconBox).setBackground(Ui.circle(context, colorRes));
        ((ImageView) card.findViewById(R.id.cardIcon)).setImageResource(iconRes);
        ((TextView) card.findViewById(R.id.cardTitle)).setText(title);
        TextView body = card.findViewById(R.id.cardText);
        if (text == null || text.length() == 0) body.setVisibility(View.GONE);
        else body.setText(text);
        return card;
    }

    /** Toda la tarjeta es un botón (muestra la flecha a la derecha). */
    public static void clickable(View card, View.OnClickListener listener) {
        card.findViewById(R.id.cardChevron).setVisibility(View.VISIBLE);
        card.setClickable(true);
        card.setFocusable(true);
        card.setOnClickListener(listener);
    }

    /** Agrega un botón de nivel dentro de la tarjeta. */
    public static MaterialButton addAction(View card, CharSequence text, int colorRes, View.OnClickListener listener) {
        LinearLayout actions = card.findViewById(R.id.cardActions);
        MaterialButton button = outlineButton(card.getContext(), text, colorRes, listener);
        ((LinearLayout.LayoutParams) button.getLayoutParams()).topMargin =
                Ui.dp(card.getContext(), actions.getChildCount() == 0 ? 14 : 8);
        actions.addView(button);
        return button;
    }

    /** Botón con borde de color (niveles extra y opciones de respuesta). */
    public static MaterialButton outlineButton(Context context, CharSequence text, int colorRes, View.OnClickListener listener) {
        MaterialButton button = new MaterialButton(context, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        button.setText(text);
        button.setAllCaps(false);
        button.setLetterSpacing(0.01f);
        button.setTextColor(Ui.color(context, R.color.eg_text));
        button.setStrokeColor(ColorStateList.valueOf(Ui.color(context, colorRes)));
        button.setStrokeWidth(Ui.dp(context, 2));
        button.setCornerRadius(Ui.dp(context, 30));
        button.setRippleColor(ColorStateList.valueOf(0x22FFFFFF));
        button.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,
                context.getResources().getDimension(R.dimen.text_body));
        button.setGravity(android.view.Gravity.CENTER_VERTICAL | android.view.Gravity.START);
        button.setPadding(Ui.dp(context, 20), 0, Ui.dp(context, 16), 0);
        button.setMinHeight(Ui.dp(context, 50));
        button.setInsetTop(0);
        button.setInsetBottom(0);
        try {
            button.setTypeface(androidx.core.content.res.ResourcesCompat.getFont(context, R.font.fredoka_semibold));
        } catch (RuntimeException ignored) {
            // sin Google Play Services se usa la fuente del sistema
        }
        button.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setOnClickListener(listener);
        return button;
    }
}
