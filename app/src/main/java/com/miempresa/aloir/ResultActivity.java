package com.miempresa.aloir;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.button.MaterialButton;
import com.miempresa.aloir.audio.Feedback;
import com.miempresa.aloir.data.ExerciseType;
import com.miempresa.aloir.data.TuningLevel;
import com.miempresa.aloir.ui.Ui;

import java.util.Locale;

/** Resultados del nivel: puntaje, aciertos, errores y opciones para seguir. */
public class ResultActivity extends BaseActivity {

    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_COLOR = "color";
    private static final String EXTRA_SCORE = "score";
    private static final String EXTRA_CORRECT = "correct";
    private static final String EXTRA_TOTAL = "total";
    private static final String EXTRA_TYPE = "type";
    private static final String EXTRA_LEVEL = "level";

    private Feedback feedback;

    public static void start(Activity from, String title, int colorRes, int score, int correct, int total,
                             ExerciseType type, int level) {
        Intent intent = new Intent(from, ResultActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_COLOR, colorRes);
        intent.putExtra(EXTRA_SCORE, score);
        intent.putExtra(EXTRA_CORRECT, correct);
        intent.putExtra(EXTRA_TOTAL, total);
        intent.putExtra(EXTRA_TYPE, type.name());
        intent.putExtra(EXTRA_LEVEL, level);
        from.startActivity(intent);
        from.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent in = getIntent();
        int score = in.getIntExtra(EXTRA_SCORE, 0);
        int correct = in.getIntExtra(EXTRA_CORRECT, 0);
        int total = in.getIntExtra(EXTRA_TOTAL, 10);
        int colorRes = in.getIntExtra(EXTRA_COLOR, R.color.eg_neon);
        ExerciseType type = ExerciseType.valueOf(in.getStringExtra(EXTRA_TYPE));
        int level = in.getIntExtra(EXTRA_LEVEL, 1);

        TextView title = findViewById(R.id.resultTitle);
        title.setText(in.getStringExtra(EXTRA_TITLE));
        title.setTextColor(Ui.color(this, colorRes));

        TextView scoreView = findViewById(R.id.resultScore);
        scoreView.setText(String.format(Locale.US, "%04d", score));
        Ui.popIn(scoreView, 150);

        stat(findViewById(R.id.resultCorrect), R.drawable.ic_check, R.color.eg_neon,
                "Respuestas correctas: " + correct);
        stat(findViewById(R.id.resultWrong), R.drawable.ic_close, R.color.eg_red,
                "Respuestas incorrectas: " + (total - correct));
        Ui.riseIn(findViewById(R.id.resultStats), 300);

        // Mismos mensajes que la versión original de Eargym
        float ratio = correct / (float) total;
        TextView message = findViewById(R.id.resultMessage);
        if (ratio <= 0.5f) message.setText("Tú puedes, sigue intentando");
        else if (ratio < 0.9f) message.setText("¡Bien hecho!");
        else message.setText("¡FELICITACIONES!");
        message.setTextColor(Ui.color(this, ratio <= 0.5f ? R.color.eg_yellow : R.color.eg_neon));
        Ui.riseIn(message, 450);

        MaterialButton next = findViewById(R.id.resultNext);
        boolean hasNext = level < type.levels && ratio >= 0.7f;
        next.setVisibility(hasNext ? View.VISIBLE : View.GONE);
        if (hasNext) {
            int nextLevel = level + 1;
            next.setText("Siguiente nivel (" + nextLevel + ")");
            next.setOnClickListener(v -> {
                launch(type, nextLevel);
                finish();
            });
        }

        MaterialButton retry = findViewById(R.id.resultRetry);
        if (hasNext) {
            retry.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Ui.color(this, R.color.white)));
        }
        retry.setOnClickListener(v -> {
            launch(type, level);
            finish();
        });
        findViewById(R.id.resultBack).setOnClickListener(v -> onBack());

        feedback = new Feedback(this);
        if (ratio >= 0.7f) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (!isFinishing()) feedback.finished();
            }, 300);
        }
    }

    private void launch(ExerciseType type, int level) {
        if (type == ExerciseType.TUNING) TuningActivity.start(this, TuningLevel.get(level));
        else QuizActivity.start(this, type, level);
    }

    private void stat(TextView view, int iconRes, int colorRes, String text) {
        Drawable icon = DrawableCompat.wrap(ContextCompat.getDrawable(this, iconRes).mutate());
        DrawableCompat.setTint(icon, Ui.color(this, colorRes));
        int size = Ui.dp(this, 26);
        icon.setBounds(0, 0, size, size);
        view.setCompoundDrawables(icon, null, null, null);
        view.setText(text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Handler(Looper.getMainLooper()).postDelayed(feedback::release, 1500);
    }
}
