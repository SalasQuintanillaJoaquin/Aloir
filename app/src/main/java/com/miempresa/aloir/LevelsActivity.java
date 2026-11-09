package com.miempresa.aloir;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miempresa.aloir.data.TuningLevel;
import com.miempresa.aloir.ui.Dialogs;
import com.miempresa.aloir.ui.Ui;

/** Selección de los 7 niveles de afinación. */
public class LevelsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        Ui.titleBar(this, "Niveles");

        LinearLayout list = findViewById(R.id.levelList);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (TuningLevel level : TuningLevel.values()) {
            View row = inflater.inflate(R.layout.item_level, list, false);
            row.setBackground(Ui.card(this, level.colorRes));
            TextView number = row.findViewById(R.id.levelNumber);
            number.setText(String.valueOf(level.number));
            number.setBackground(Ui.circle(this, level.colorRes));
            ((TextView) row.findViewById(R.id.levelName)).setText("Nivel " + level.number + " · " + level.name);
            ((TextView) row.findViewById(R.id.levelDetail)).setText(level.description);
            ImageView play = row.findViewById(R.id.levelPlay);
            play.setBackground(Ui.circle(this, level.colorRes));
            row.setContentDescription("Nivel " + level.number + ", " + level.name + ", " + level.description);
            row.setOnClickListener(v -> confirm(level));
            list.addView(row);
            if (savedInstanceState == null) Ui.riseIn(row, 60L * level.number);
        }
    }

    private void confirm(TuningLevel level) {
        new Dialogs.Builder(this)
                .icon(R.drawable.ic_tuning_fork, level.colorRes)
                .title("Nivel " + level.number + " · " + level.name)
                .message("¿Estás listo para empezar? Son " + TuningActivity.EXERCISES
                        + " ejercicios con una diferencia de ±" + level.cents + " cents.")
                .positive("Iniciar", () -> TuningActivity.start(this, level))
                .negative("Cancelar", null)
                .show();
    }
}
