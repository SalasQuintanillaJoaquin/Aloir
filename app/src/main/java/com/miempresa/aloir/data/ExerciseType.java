package com.miempresa.aloir.data;

import com.miempresa.aloir.R;

/** Ejercicios auditivos de Eargym. La afinación usa los audios grabados; el resto se sintetiza. */
public enum ExerciseType {
    TUNING("Afinación", "¿El segundo sonido está más alto, más bajo o igual?", R.drawable.ic_tuning_fork, R.color.eg_neon, 7),
    DIRECTION("Dirección melódica", "Dos notas seguidas: ¿la melodía sube o baja?", R.drawable.ic_trend, R.color.eg_blue, 3),
    NOTE("Reconocer notas", "Suena un DO de referencia. ¿Qué nota viene después?", R.drawable.ic_music_note, R.color.eg_pink, 3),
    INTERVAL("Intervalos", "¿Qué distancia hay entre las dos notas?", R.drawable.ic_interval, R.color.eg_violet, 4),
    CHORD("Acordes", "Escucha el acorde: ¿mayor, menor...?", R.drawable.ic_chord, R.color.eg_orange, 2);

    public final String title;
    public final String instructions;
    public final int iconRes;
    public final int colorRes;
    public final int levels;

    ExerciseType(String title, String instructions, int iconRes, int colorRes, int levels) {
        this.title = title;
        this.instructions = instructions;
        this.iconRes = iconRes;
        this.colorRes = colorRes;
        this.levels = levels;
    }

    /** Qué entrena cada nivel de los ejercicios extra. */
    public String levelDescription(int level) {
        switch (this) {
            case DIRECTION:
                return level == 1 ? "Saltos grandes" : level == 2 ? "Saltos medianos" : "Tonos y semitonos";
            case NOTE:
                return level == 1 ? "DO, MI y SOL" : level == 2 ? "Notas naturales" : "Con sostenidos";
            case INTERVAL:
                return level == 1 ? "Unísono, 5ª y 8ª" : level == 2 ? "Suma 3ª y 4ª"
                        : level == 3 ? "Todos" : "Descendentes";
            case CHORD:
                return level == 1 ? "Mayor y menor" : "4 tipos";
            default:
                return TuningLevel.get(level).description;
        }
    }
}
