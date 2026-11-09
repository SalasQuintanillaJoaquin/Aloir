package com.miempresa.aloir;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import com.miempresa.aloir.ui.Dialogs;
import com.miempresa.aloir.ui.Ui;

/** Menú principal: Jugar, Más ejercicios, Piano, Acerca de y Salir. */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            Ui.popIn(findViewById(R.id.menuLogo), 0);
            Ui.riseIn(findViewById(R.id.menuTitle), 150);
            Ui.riseIn(findViewById(R.id.menuTagline), 200);
            ViewGroup buttons = findViewById(R.id.menuButtons);
            for (int i = 0; i < buttons.getChildCount(); i++) {
                Ui.riseIn(buttons.getChildAt(i), 250 + 70L * i);
            }
        }

        findViewById(R.id.btnJugar).setOnClickListener(v -> open(LevelsActivity.class));
        findViewById(R.id.btnExtras).setOnClickListener(v -> open(ExtrasActivity.class));
        findViewById(R.id.btnPiano).setOnClickListener(v -> open(PianoActivity.class));
        findViewById(R.id.btnAcercade).setOnClickListener(v -> open(AboutActivity.class));
        findViewById(R.id.btnSalir).setOnClickListener(v -> confirmExit());
    }

    private void open(Class<?> screen) {
        startActivity(new Intent(this, screen));
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    private void confirmExit() {
        new Dialogs.Builder(this)
                .icon(R.drawable.ic_exit, R.color.eg_red)
                .title("¿Deseas salir de Eargym?")
                .message("Tu oído te espera para seguir entrenando.")
                .positive("Sí, salir", this::finish)
                .negative("No, quedarme", null)
                .show();
    }

    @Override
    protected void onBack() {
        confirmExit();
    }
}
