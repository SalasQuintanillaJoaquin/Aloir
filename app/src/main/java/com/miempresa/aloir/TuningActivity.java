package com.miempresa.aloir;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.miempresa.aloir.audio.Feedback;
import com.miempresa.aloir.audio.RawPlayer;
import com.miempresa.aloir.data.ExerciseType;
import com.miempresa.aloir.data.Question;
import com.miempresa.aloir.data.QuestionFactory;
import com.miempresa.aloir.data.TuningLevel;
import com.miempresa.aloir.ui.Dialogs;
import com.miempresa.aloir.ui.MeterView;
import com.miempresa.aloir.ui.Ui;

import java.util.List;

/**
 * Ejercicio de afinación: dos sonidos grabados (referencia y comparación) y la respuesta
 * alto / igual / bajo. 10 ejercicios por nivel, 10 puntos por acierto. No se guarda nada.
 */
public class TuningActivity extends BaseActivity {

    public static final int EXERCISES = 10;
    private static final String EXTRA_LEVEL = "level";
    /** Orden de las respuestas en Question: 0 alto, 1 bajo, 2 igual. */
    private static final String[] ANSWER_NAMES = {"Más alto", "Más bajo", "Igual"};

    private TuningLevel level;
    private List<Question> questions;
    private int index;
    private int score;
    private int correct;
    private boolean answered;

    private RawPlayer player;
    private Feedback feedback;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Dialog openDialog;

    private TextView counter, scoreView, noteView;
    private ImageView staff;
    private ProgressBar progress;
    private MeterView meter1, meter2;
    private MaterialButton play1, play2;
    private MaterialButton[] answerButtons;
    private int playingSlot;

    public static void start(Activity from, TuningLevel level) {
        Intent intent = new Intent(from, TuningActivity.class);
        intent.putExtra(EXTRA_LEVEL, level.number);
        from.startActivity(intent);
        from.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuning);

        level = TuningLevel.get(getIntent().getIntExtra(EXTRA_LEVEL, 1));
        player = new RawPlayer(this);
        feedback = new Feedback(this);

        Ui.titleBar(this, "Nivel " + level.number + " · " + level.name);
        TextView extra = findViewById(R.id.titleExtra);
        extra.setVisibility(View.VISIBLE);
        extra.setText("±" + level.cents + " cents");
        extra.setTextColor(Ui.color(this, level.colorRes));

        counter = findViewById(R.id.tuningCounter);
        scoreView = findViewById(R.id.tuningScore);
        noteView = findViewById(R.id.tuningNote);
        staff = findViewById(R.id.tuningStaff);
        progress = findViewById(R.id.tuningProgress);
        meter1 = findViewById(R.id.meter1);
        meter2 = findViewById(R.id.meter2);
        meter1.setColor(Ui.color(this, R.color.eg_blue));
        meter2.setColor(Ui.color(this, R.color.eg_violet));
        play1 = findViewById(R.id.play1);
        play2 = findViewById(R.id.play2);
        play1.setOnClickListener(v -> toggle(1));
        play2.setOnClickListener(v -> toggle(2));

        // índices iguales a Question.correct: 0 alto, 1 bajo, 2 igual
        answerButtons = new MaterialButton[]{
                findViewById(R.id.answerHigh), findViewById(R.id.answerLow), findViewById(R.id.answerEqual)};
        for (int i = 0; i < answerButtons.length; i++) {
            final int answer = i;
            answerButtons[i].setOnClickListener(v -> answer(answer));
        }

        questions = QuestionFactory.build(this, ExerciseType.TUNING, level.number, EXERCISES);
        showQuestion();
    }

    private void showQuestion() {
        Question q = questions.get(index);
        answered = false;
        stopAudio();
        staff.setImageResource(q.imageRes);
        noteView.setText(q.label);
        counter.setText((index + 1) + "/" + EXERCISES);
        scoreView.setText(String.valueOf(score));
        ObjectAnimator.ofInt(progress, "progress", progress.getProgress(), index * 100 / EXERCISES)
                .setDuration(300).start();
        Ui.popIn(staff, 0);
    }

    // ---------------- Audio ----------------

    private void toggle(int slot) {
        if (playingSlot == slot) {
            stopAudio();
            return;
        }
        Question q = questions.get(index);
        MeterView meter = slot == 1 ? meter1 : meter2;
        MaterialButton button = slot == 1 ? play1 : play2;
        stopAudio();
        playingSlot = slot;
        player.play(slot == 1 ? q.rawReference : q.rawTarget, new RawPlayer.Listener() {
            @Override
            public void onStart(int durationMs) {
                meter.play(durationMs);
                button.setIconResource(R.drawable.ic_stop);
            }

            @Override
            public void onEnd() {
                meter.stop();
                button.setIconResource(R.drawable.ic_play);
                if (playingSlot == slot) playingSlot = 0;
            }
        });
    }

    private void stopAudio() {
        player.stop();
        playingSlot = 0;
        meter1.stop();
        meter2.stop();
        play1.setIconResource(R.drawable.ic_play);
        play2.setIconResource(R.drawable.ic_play);
    }

    // ---------------- Respuestas ----------------

    private void answer(int choice) {
        if (answered) return;
        answered = true;
        stopAudio();
        Question q = questions.get(index);
        if (choice == q.correct) {
            score += 10;
            correct++;
            scoreView.setText(String.valueOf(score));
            Ui.pulse(scoreView);
            Ui.pulse(answerButtons[choice]);
            feedback.correct();
            openDialog = new Dialogs.Builder(this)
                    .icon(R.drawable.ic_check, R.color.eg_neon)
                    .title("¡Buen trabajo!")
                    .message("+10 puntos")
                    .cancelable(false)
                    .show();
            handler.postDelayed(() -> {
                dismissDialog();
                next();
            }, 1100);
        } else {
            Ui.shake(answerButtons[choice]);
            feedback.wrong();
            openDialog = new Dialogs.Builder(this)
                    .icon(R.drawable.ic_close, R.color.eg_red)
                    .title("Entrena tu oído")
                    .message("La respuesta correcta es: " + ANSWER_NAMES[q.correct] + ".\n" + q.explanation)
                    .positive("Siguiente", this::next)
                    .cancelable(false)
                    .show();
        }
    }

    private void next() {
        index++;
        if (index >= EXERCISES) {
            ResultActivity.start(this, "Nivel " + level.number + " · " + level.name, level.colorRes,
                    score, correct, EXERCISES, ExerciseType.TUNING, level.number);
            finish();
            return;
        }
        showQuestion();
    }

    private void dismissDialog() {
        if (openDialog != null && openDialog.isShowing()) openDialog.dismiss();
        openDialog = null;
    }

    @Override
    protected void onBack() {
        new Dialogs.Builder(this)
                .icon(R.drawable.ic_exit, R.color.eg_orange)
                .title("¿Seguro que quieres abandonar?")
                .message("Se perderá el puntaje de este nivel.")
                .positive("Sí, salir", () -> {
                    finish();
                    overridePendingTransition(R.anim.slide_in_activity, R.anim.slide_out_activity);
                })
                .negative("Seguir entrenando", null)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAudio();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        dismissDialog();
        player.stop();
        feedback.release();
    }
}
