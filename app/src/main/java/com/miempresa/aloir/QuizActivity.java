package com.miempresa.aloir;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.miempresa.aloir.audio.Feedback;
import com.miempresa.aloir.audio.ToneSynth;
import com.miempresa.aloir.data.ExerciseType;
import com.miempresa.aloir.data.Question;
import com.miempresa.aloir.data.QuestionFactory;
import com.miempresa.aloir.ui.Cards;
import com.miempresa.aloir.ui.Dialogs;
import com.miempresa.aloir.ui.Ui;

import java.util.ArrayList;
import java.util.List;

/** Ejercicios de opción múltiple con sonidos sintetizados (dirección, notas, intervalos, acordes). */
public class QuizActivity extends BaseActivity {

    private static final int EXERCISES = 10;
    private static final String EXTRA_TYPE = "type";
    private static final String EXTRA_LEVEL = "level";

    private ExerciseType type;
    private int level;
    private List<Question> questions;
    private int index;
    private int score;
    private int correct;
    private boolean answered;

    private ToneSynth synth;
    private Feedback feedback;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Dialog openDialog;

    private TextView question, counter, scoreView;
    private ProgressBar progress;
    private MaterialButton listen, slow;
    private LinearLayout optionsBox;
    private final List<MaterialButton> options = new ArrayList<>();

    public static void start(Activity from, ExerciseType type, int level) {
        Intent intent = new Intent(from, QuizActivity.class);
        intent.putExtra(EXTRA_TYPE, type.name());
        intent.putExtra(EXTRA_LEVEL, level);
        from.startActivity(intent);
        from.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        type = ExerciseType.valueOf(getIntent().getStringExtra(EXTRA_TYPE));
        level = getIntent().getIntExtra(EXTRA_LEVEL, 1);
        synth = new ToneSynth();
        feedback = new Feedback(this);

        Ui.titleBar(this, type.title);
        TextView extra = findViewById(R.id.titleExtra);
        extra.setVisibility(View.VISIBLE);
        extra.setText("Nivel " + level);
        extra.setTextColor(Ui.color(this, type.colorRes));

        question = findViewById(R.id.quizQuestion);
        counter = findViewById(R.id.quizCounter);
        scoreView = findViewById(R.id.quizScore);
        progress = findViewById(R.id.quizProgress);
        optionsBox = findViewById(R.id.quizOptions);
        listen = findViewById(R.id.quizListen);
        slow = findViewById(R.id.quizSlow);
        listen.setBackgroundTintList(ColorStateList.valueOf(Ui.color(this, type.colorRes)));
        listen.setOnClickListener(v -> play(false));
        slow.setOnClickListener(v -> play(true));

        questions = QuestionFactory.build(this, type, level, EXERCISES);
        showQuestion();
    }

    private void showQuestion() {
        Question q = questions.get(index);
        answered = false;
        question.setText(q.prompt);
        counter.setText((index + 1) + "/" + EXERCISES);
        scoreView.setText(String.valueOf(score));
        ObjectAnimator.ofInt(progress, "progress", progress.getProgress(), index * 100 / EXERCISES)
                .setDuration(300).start();

        optionsBox.removeAllViews();
        options.clear();
        for (int i = 0; i < q.options.length; i++) {
            final int choice = i;
            MaterialButton option = Cards.outlineButton(this, q.options[i], type.colorRes, v -> answer(choice));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) option.getLayoutParams();
            lp.topMargin = Ui.dp(this, i == 0 ? 0 : 10);
            option.setMinHeight(Ui.dp(this, 56));
            option.setGravity(android.view.Gravity.CENTER);
            optionsBox.addView(option);
            options.add(option);
            Ui.riseIn(option, 60L * i);
        }
        handler.postDelayed(() -> play(false), 400);
    }

    private void play(boolean slowly) {
        Question q = questions.get(index);
        int noteMs = slowly ? (int) (q.noteMs * 1.6f) : q.noteMs;
        int gapMs = slowly ? q.gapMs * 2 + 200 : q.gapMs;
        MaterialButton button = slowly ? slow : listen;
        synth.play(q.steps, noteMs, gapMs, 0.85f, new ToneSynth.Listener() {
            @Override
            public void onPlaybackStart() {
                button.animate().scaleX(1.08f).scaleY(1.08f).setDuration(150).start();
            }

            @Override
            public void onPlaybackEnd() {
                button.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
            }
        });
    }

    private void answer(int choice) {
        if (answered) return;
        answered = true;
        handler.removeCallbacksAndMessages(null);
        Question q = questions.get(index);
        MaterialButton chosen = options.get(choice);
        if (choice == q.correct) {
            score += 10;
            correct++;
            scoreView.setText(String.valueOf(score));
            Ui.pulse(scoreView);
            Ui.tint(chosen, R.color.eg_neon);
            feedback.correct();
            openDialog = new Dialogs.Builder(this)
                    .icon(R.drawable.ic_check, R.color.eg_neon)
                    .title("¡Buen trabajo!")
                    .message("+10 puntos\n" + q.explanation)
                    .positive("Siguiente", this::next)
                    .cancelable(false)
                    .show();
        } else {
            Ui.tint(chosen, R.color.eg_red);
            Ui.tint(options.get(q.correct), R.color.eg_neon);
            Ui.shake(chosen);
            feedback.wrong();
            openDialog = new Dialogs.Builder(this)
                    .icon(R.drawable.ic_close, R.color.eg_red)
                    .title("Entrena tu oído")
                    .message("La respuesta correcta es: " + q.correctText() + ".\n" + q.explanation)
                    .positive("Siguiente", this::next)
                    .cancelable(false)
                    .show();
        }
    }

    private void next() {
        synth.stop();
        index++;
        if (index >= EXERCISES) {
            ResultActivity.start(this, type.title + " · Nivel " + level, type.colorRes,
                    score, correct, EXERCISES, type, level);
            finish();
            return;
        }
        showQuestion();
    }

    @Override
    protected void onBack() {
        new Dialogs.Builder(this)
                .icon(R.drawable.ic_exit, R.color.eg_orange)
                .title("¿Seguro que quieres abandonar?")
                .message("Se perderá el puntaje de este ejercicio.")
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
        handler.removeCallbacksAndMessages(null);
        synth.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (openDialog != null && openDialog.isShowing()) openDialog.dismiss();
        synth.release();
        feedback.release();
    }
}
