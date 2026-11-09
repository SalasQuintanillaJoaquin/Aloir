package com.miempresa.aloir.data;

import java.util.List;

/** Una pregunta de lección: cómo suena, qué se pregunta y cuál es la respuesta. */
public class Question {
    public ExerciseType type;
    public String prompt;
    /** Etiqueta grande opcional, p. ej. la nota de referencia en afinación. */
    public String label;
    /** Imagen opcional del pentagrama (afinación). */
    public int imageRes;

    public String[] options;
    public int[] optionIcons;
    public int correct;
    public String explanation;

    /** Afinación: audio de referencia y audio a comparar (res/raw). */
    public int rawReference;
    public int rawTarget;

    /** Ejercicios sintetizados: pasos (acordes o notas sueltas, en MIDI). */
    public List<int[]> steps;
    public int noteMs = 700;
    public int gapMs = 150;

    /** Para no repetir la misma pregunta dos veces seguidas. */
    public String signature;

    public boolean usesRecordings() {
        return rawReference != 0;
    }

    public String correctText() {
        return options[correct];
    }
}
