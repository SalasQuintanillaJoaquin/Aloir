package com.miempresa.aloir;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.widget.TextViewCompat;

import com.miempresa.aloir.audio.ToneSynth;
import com.miempresa.aloir.data.ExerciseType;
import com.miempresa.aloir.ui.Cards;
import com.miempresa.aloir.ui.Ui;

import java.util.Collections;

/** Más ejercicios: dirección, notas, intervalos y acordes, más herramientas de apoyo. */
public class ExtrasActivity extends BaseActivity {

    private ToneSynth synth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Ui.titleBar(this, getString(R.string.mas_ejercicios));
        synth = new ToneSynth();

        LinearLayout content = findViewById(R.id.listContent);
        content.addView(heading("Elige un ejercicio y su nivel. Todos los sonidos se generan en el momento."));

        long delay = 0;
        for (ExerciseType type : ExerciseType.values()) {
            if (type == ExerciseType.TUNING) continue;
            View card = Cards.create(this, content, type.iconRes, type.colorRes, type.title, type.instructions);
            for (int level = 1; level <= type.levels; level++) {
                final int chosen = level;
                Cards.addAction(card, "Nivel " + level + "  ·  " + type.levelDescription(level), type.colorRes,
                        v -> QuizActivity.start(this, type, chosen));
            }
            content.addView(card);
            if (savedInstanceState == null) Ui.riseIn(card, delay += 70);
        }

        content.addView(heading("Herramientas"));
        View fork = Cards.create(this, content, R.drawable.ic_tuning_fork, R.color.eg_neon,
                "Diapasón · LA 440 Hz", "La nota de referencia para afinar instrumentos.");
        Cards.clickable(fork, v -> synth.play(Collections.singletonList(new int[]{69}), 2500, 0, 0.9f, null));
        content.addView(fork);

        View guide = Cards.create(this, content, R.drawable.ic_info, R.color.eg_violet,
                "Guía de intervalos", "Canciones para reconocer cada intervalo.");
        Cards.clickable(guide, v -> {
            startActivity(new Intent(this, GuideActivity.class));
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        });
        content.addView(guide);
    }

    private TextView heading(String text) {
        TextView view = new androidx.appcompat.widget.AppCompatTextView(this);
        TextViewCompat.setTextAppearance(view, R.style.Eargym_Text_Muted);
        view.setText(text);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = Ui.dp(this, 18);
        view.setLayoutParams(lp);
        return view;
    }

    @Override
    protected void onPause() {
        super.onPause();
        synth.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        synth.release();
    }
}
