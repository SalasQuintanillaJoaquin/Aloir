package com.miempresa.aloir;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Base de las pantallas de Eargym. Desde Android 16 (API 36) el sistema ya no llama a
 * onBackPressed(); el botón/gesto atrás pasa por este callback y llega a {@link #onBack()}.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBack();
            }
        });
    }

    /**
     * Desde Android 15 la app se dibuja de borde a borde: el fondo azul llega detrás de las
     * barras del sistema y el contenido se aparta de ellas.
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        View content = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(content, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        ViewCompat.requestApplyInsets(content);
    }

    /** Atrás por defecto: cerrar la pantalla con la animación de regreso. */
    protected void onBack() {
        finish();
        overridePendingTransition(R.anim.slide_in_activity, R.anim.slide_out_activity);
    }
}
