package com.miempresa.aloir.ui;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.miempresa.aloir.R;

/** Diálogo centrado con el estilo de Eargym: icono de color, título, mensaje y botones. */
public final class Dialogs {

    public static class Builder {
        private final Activity activity;
        private int iconRes = R.drawable.ic_info;
        private int colorRes = R.color.eg_neon;
        private CharSequence title = "";
        private CharSequence message = "";
        private CharSequence positive;
        private CharSequence negative;
        private Runnable onPositive;
        private Runnable onNegative;
        private boolean cancelable = true;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder icon(int iconRes, int colorRes) {
            this.iconRes = iconRes;
            this.colorRes = colorRes;
            return this;
        }

        public Builder title(CharSequence text) { title = text; return this; }

        public Builder message(CharSequence text) { message = text; return this; }

        public Builder positive(CharSequence text, Runnable action) {
            positive = text;
            onPositive = action;
            return this;
        }

        public Builder negative(CharSequence text, Runnable action) {
            negative = text;
            onNegative = action;
            return this;
        }

        public Builder cancelable(boolean value) { cancelable = value; return this; }

        public Dialog show() {
            Dialog dialog = new Dialog(activity, R.style.Theme_Eargym_Dialog);
            View root = activity.getLayoutInflater().inflate(R.layout.dialog_card, null);

            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(Ui.color(activity, colorRes));
            root.findViewById(R.id.dialogIconBox).setBackground(circle);
            ((ImageView) root.findViewById(R.id.dialogIcon)).setImageResource(iconRes);
            ((TextView) root.findViewById(R.id.dialogTitle)).setText(title);
            TextView messageView = root.findViewById(R.id.dialogMessage);
            messageView.setText(message);
            messageView.setVisibility(message.length() == 0 ? View.GONE : View.VISIBLE);

            MaterialButton positiveButton = root.findViewById(R.id.dialogPositive);
            if (positive == null) {
                positiveButton.setVisibility(View.GONE);
            } else {
                positiveButton.setText(positive);
                Ui.tint(positiveButton, colorRes);
                positiveButton.setOnClickListener(v -> {
                    dialog.dismiss();
                    if (onPositive != null) onPositive.run();
                });
            }
            MaterialButton negativeButton = root.findViewById(R.id.dialogNegative);
            if (negative == null) {
                negativeButton.setVisibility(View.GONE);
            } else {
                negativeButton.setText(negative);
                negativeButton.setOnClickListener(v -> {
                    dialog.dismiss();
                    if (onNegative != null) onNegative.run();
                });
            }

            dialog.setContentView(root);
            dialog.setCancelable(cancelable);
            dialog.setCanceledOnTouchOutside(cancelable);
            Window window = dialog.getWindow();
            if (window != null) {
                // Ancho cómodo en teléfonos y sin estirarse de más en tabletas
                int screen = activity.getResources().getDisplayMetrics().widthPixels;
                int width = Math.min(screen - Ui.dp(activity, 40), Ui.dp(activity, 420));
                window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            dialog.show();
            Ui.popIn(root, 0);
            return dialog;
        }
    }

    private Dialogs() {}
}
