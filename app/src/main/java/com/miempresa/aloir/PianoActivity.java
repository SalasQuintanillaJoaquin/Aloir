package com.miempresa.aloir;

import android.os.Bundle;
import android.widget.TextView;

import com.miempresa.aloir.audio.ToneSynth;
import com.miempresa.aloir.data.QuestionFactory;
import com.miempresa.aloir.ui.PianoView;
import com.miempresa.aloir.ui.Ui;

import java.util.Locale;

/** Piano libre para explorar las notas por su nombre y frecuencia. */
public class PianoActivity extends BaseActivity {

    private ToneSynth synth;
    private int octave = 4;
    private PianoView piano;
    private TextView octaveText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piano);
        synth = new ToneSynth();

        Ui.titleBar(this, "Piano");

        TextView note = findViewById(R.id.pianoNote);
        TextView freq = findViewById(R.id.pianoFreq);
        octaveText = findViewById(R.id.pianoOctave);
        piano = findViewById(R.id.pianoKeys);
        piano.setOnKeyListener(midi -> {
            synth.playNote(midi, 1100, null);
            note.setText(QuestionFactory.noteName(midi));
            freq.setText(String.format(Locale.US, "%s · %.1f Hz", QuestionFactory.noteNameWithOctave(midi), ToneSynth.frequency(midi)));
            Ui.pulse(note);
        });

        findViewById(R.id.pianoDown).setOnClickListener(v -> setOctave(octave - 1));
        findViewById(R.id.pianoUp).setOnClickListener(v -> setOctave(octave + 1));
        setOctave(4);
    }

    private void setOctave(int value) {
        octave = Math.max(2, Math.min(6, value));
        piano.setBaseMidi((octave + 1) * 12);
        octaveText.setText("Octava " + octave);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        synth.release();
    }
}
