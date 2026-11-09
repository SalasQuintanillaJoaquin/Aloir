package com.miempresa.aloir;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miempresa.aloir.audio.ToneSynth;
import com.miempresa.aloir.data.QuestionFactory;
import com.miempresa.aloir.ui.Cards;
import com.miempresa.aloir.ui.Ui;

import java.util.Arrays;

/** Guía de intervalos: cada uno con su canción de referencia y un botón para oírlo. */
public class GuideActivity extends BaseActivity {

    private ToneSynth synth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        synth = new ToneSynth();

        Ui.titleBar(this, "Guía de intervalos");

        LinearLayout content = findViewById(R.id.listContent);
        TextView intro = new androidx.appcompat.widget.AppCompatTextView(this);
        androidx.core.widget.TextViewCompat.setTextAppearance(intro, R.style.Eargym_Text_Muted);
        intro.setText("Un intervalo es la distancia entre dos notas. Asociarlo con una canción conocida es el truco favorito de los músicos. Toca cada tarjeta para escucharlo desde DO.");
        content.addView(intro);

        for (int semitones = 1; semitones < QuestionFactory.INTERVAL_NAMES.length; semitones++) {
            final int distance = semitones;
            View row = Cards.create(this, content, R.drawable.ic_play, R.color.eg_violet,
                    QuestionFactory.INTERVAL_NAMES[semitones] + "  ·  " + semitones + (semitones == 1 ? " semitono" : " semitonos"),
                    QuestionFactory.INTERVAL_TIPS[semitones]);
            Cards.clickable(row, v -> synth.play(Arrays.asList(new int[]{60}, new int[]{60 + distance}, new int[]{60, 60 + distance}),
                    700, 120, 0.85f, null));
            content.addView(row);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        synth.release();
    }
}
