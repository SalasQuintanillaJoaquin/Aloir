package com.miempresa.aloir.data;

import android.content.Context;

import com.miempresa.aloir.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Genera las preguntas de cada tipo de ejercicio según la dificultad. */
public final class QuestionFactory {

    public static final String[] NOTE_NAMES = {"DO", "DO♯", "RE", "RE♯", "MI", "FA", "FA♯", "SOL", "SOL♯", "LA", "LA♯", "SI"};

    // ---- Afinación: notas grabadas (los 7 niveles originales) ----
    private static final String[] TUNING_LABELS = {"DO", "DO♯ / RE♭", "RE", "RE♯ / MI♭", "MI", "FA", "FA♯ / SOL♭", "SOL", "SOL♯ / LA♭", "LA", "LA♯ / SI♭", "SI"};
    private static final String[] LEVEL_KEYS = {"do", "doreb", "re", "remib", "mi", "fa", "fasolb", "sol", "sollab", "la", "lasib", "si"};
    private static final String[][] LEVEL1_KEYS = {
            {"do", "dossreb", "re", "remib", "mi", "fa", "fasolb", "sol", "sollab", "la", "lasib", "si"},
            {"do4", "doreb4", "re4", "remib4", "mi4", "fa4", "fasol4", "sol4", "solla4", "la4", "lasi4", "si4"},
            {"do5", "dore5", "re5", "remi5", "mi5", "fa5", "fasol5", "sol5", "solla5", "la5", "lasi5", "si5"},
    };
    private static final int[][] STAFF_IMAGES = {
            {R.drawable.dos, R.drawable.doreb, R.drawable.re, R.drawable.remib, R.drawable.mi, R.drawable.fa,
                    R.drawable.fasolb, R.drawable.sol, R.drawable.solab, R.drawable.la, R.drawable.lasib, R.drawable.si},
            {R.drawable.do4, R.drawable.doreb4, R.drawable.re4, R.drawable.remib4, R.drawable.mi4, R.drawable.fa4,
                    R.drawable.fasolb4, R.drawable.sol4, R.drawable.solab4, R.drawable.la4, R.drawable.lasib4, R.drawable.si4},
            {R.drawable.do5, R.drawable.doreb5, R.drawable.re5, R.drawable.remib5, R.drawable.mi5, R.drawable.fa5,
                    R.drawable.fasolb5, R.drawable.sol5, R.drawable.solab5, R.drawable.la5, R.drawable.lasib5, R.drawable.si5},
    };

    // ---- Intervalos ----
    public static final String[] INTERVAL_NAMES = {"Unísono", "2ª menor", "2ª mayor", "3ª menor", "3ª mayor",
            "4ª justa", "Tritono", "5ª justa", "6ª menor", "6ª mayor", "7ª menor", "7ª mayor", "Octava"};
    public static final String[] INTERVAL_TIPS = {
            "Las dos notas son la misma.",
            "Suena como el tema de «Tiburón».",
            "Como el «Cumpleaños feliz» al subir.",
            "Como el inicio de la «Canción de cuna» de Brahms.",
            "Como el inicio de «When the Saints Go Marching In».",
            "Como el inicio de la «Marcha nupcial».",
            "El intervalo del tema de «Los Simpson».",
            "Como el inicio del tema de «Star Wars».",
            "Un salto amplio y algo melancólico.",
            "Como el inicio de «My Bonnie».",
            "Como el inicio del tema clásico de «Star Trek».",
            "A un semitono de la octava: muy tenso.",
            "Como el inicio de «Somewhere Over the Rainbow».",
    };
    private static final int[][] INTERVAL_POOLS = {
            {},
            {0, 7, 12},
            {0, 4, 5, 7, 12},
            {1, 2, 3, 4, 5, 7, 9, 12},
            {2, 3, 4, 5, 7, 12},
    };

    // ---- Acordes ----
    private static final String[] CHORD_NAMES = {"Mayor", "Menor", "Disminuido", "Aumentado"};
    private static final int[][] CHORD_SHAPES = {{0, 4, 7}, {0, 3, 7}, {0, 3, 6}, {0, 4, 8}};
    private static final String[] CHORD_TIPS = {
            "El acorde mayor suena brillante y alegre.",
            "El acorde menor suena más oscuro o triste.",
            "El disminuido suena tenso e inestable.",
            "El aumentado suena misterioso, como de sueño.",
    };

    private QuestionFactory() {}

    public static List<Question> build(Context context, ExerciseType type, int difficulty, int count) {
        Random random = new Random();
        List<Question> questions = new ArrayList<>();
        String previous = "";
        for (int i = 0; i < count; i++) {
            Question q = null;
            for (int attempt = 0; attempt < 5; attempt++) {
                q = create(context, type, difficulty, random);
                if (!q.signature.equals(previous)) break;
            }
            previous = q.signature;
            questions.add(q);
        }
        return questions;
    }

    public static Question create(Context context, ExerciseType type, int difficulty, Random random) {
        switch (type) {
            case TUNING:
                return tuning(context, Math.max(1, Math.min(7, difficulty)), random);
            case DIRECTION:
                return direction(Math.max(1, Math.min(3, difficulty)), random);
            case NOTE:
                return note(Math.max(1, Math.min(3, difficulty)), random);
            case INTERVAL:
                return interval(Math.max(1, Math.min(4, difficulty)), random);
            case CHORD:
            default:
                return chord(Math.max(1, Math.min(2, difficulty)), random);
        }
    }

    private static Question tuning(Context context, int level, Random random) {
        int octave = random.nextInt(3); // 3, 4 o 5
        int noteIndex = random.nextInt(12);
        String base = level == 1
                ? LEVEL1_KEYS[octave][noteIndex]
                : "n" + level + "_" + LEVEL_KEYS[noteIndex] + (octave + 3);

        int answer = random.nextInt(3); // 0 alto, 1 bajo, 2 igual
        String suffix = answer == 0 ? "_alto" : answer == 1 ? "_bajo" : "_afinado";
        int reference = raw(context, base + "_afinado");
        // En el nivel 6 el DO#3 afinado se llama "n6_doreb3_alfinado"
        if (reference == 0) reference = raw(context, base + "_alfinado");
        int target = answer == 2 ? reference : raw(context, base + suffix);
        if (reference == 0 || target == 0) {
            // Si falta algún audio, se usa DO4 del nivel 1 que siempre existe.
            return tuning(context, 1, random);
        }

        Question q = new Question();
        q.type = ExerciseType.TUNING;
        q.prompt = "¿El segundo sonido está más alto, más bajo o igual?";
        q.label = TUNING_LABELS[noteIndex] + " " + (octave + 3);
        q.imageRes = STAFF_IMAGES[octave][noteIndex];
        q.rawReference = reference;
        q.rawTarget = target;
        q.options = new String[]{"Más alto", "Más bajo", "Igual"};
        q.optionIcons = new int[]{R.drawable.ic_arrow_up, R.drawable.ic_arrow_down, R.drawable.ic_equal};
        q.correct = answer;
        q.explanation = answer == 2
                ? "Los dos sonidos eran el mismo " + q.label + " afinado."
                : "El segundo sonido estaba " + (answer == 0 ? "por encima" : "por debajo") + " del " + q.label + " afinado.";
        q.signature = base + suffix;
        return q;
    }

    private static Question direction(int difficulty, Random random) {
        int min = difficulty == 1 ? 7 : difficulty == 2 ? 3 : 1;
        int max = difficulty == 1 ? 12 : difficulty == 2 ? 6 : 2;
        int distance = min + random.nextInt(max - min + 1);
        boolean up = random.nextBoolean();
        int first = 55 + random.nextInt(14);
        int second = up ? first + distance : first - distance;

        Question q = new Question();
        q.type = ExerciseType.DIRECTION;
        q.prompt = "¿La melodía sube o baja?";
        q.steps = Arrays.asList(new int[]{first}, new int[]{second});
        q.noteMs = 650;
        q.options = new String[]{"Sube", "Baja"};
        q.optionIcons = new int[]{R.drawable.ic_arrow_up, R.drawable.ic_arrow_down};
        q.correct = up ? 0 : 1;
        q.explanation = (up ? "Subía" : "Bajaba") + " de " + noteName(first) + " a " + noteName(second)
                + " (" + distance + (distance == 1 ? " semitono)." : " semitonos).");
        q.signature = "dir" + first + "_" + second;
        return q;
    }

    private static Question note(int difficulty, Random random) {
        int[] pool;
        if (difficulty == 1) pool = new int[]{0, 4, 7};
        else if (difficulty == 2) pool = new int[]{0, 2, 4, 5, 7, 9, 11};
        else pool = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};

        int target = pool[random.nextInt(pool.length)];
        List<Integer> choices = pickChoices(pool, target, 4, random);

        Question q = new Question();
        q.type = ExerciseType.NOTE;
        q.prompt = "Primero suena DO. ¿Qué nota viene después?";
        q.steps = Arrays.asList(new int[]{60}, new int[]{60 + target});
        q.noteMs = 750;
        q.gapMs = 250;
        q.options = new String[choices.size()];
        q.optionIcons = new int[choices.size()];
        for (int i = 0; i < choices.size(); i++) {
            q.options[i] = NOTE_NAMES[choices.get(i)];
            q.optionIcons[i] = R.drawable.ic_music_note;
            if (choices.get(i) == target) q.correct = i;
        }
        q.explanation = target == 0
                ? "Era DO otra vez: la misma nota de referencia."
                : "Era " + NOTE_NAMES[target] + ": " + target + (target == 1 ? " semitono" : " semitonos") + " por encima de DO.";
        q.signature = "note" + target;
        return q;
    }

    private static Question interval(int difficulty, Random random) {
        int[] pool = INTERVAL_POOLS[difficulty];
        boolean descending = difficulty == 4;
        int semitones = pool[random.nextInt(pool.length)];
        List<Integer> choices = pickChoices(pool, semitones, 4, random);
        Collections.sort(choices);

        int first = descending ? 67 + random.nextInt(8) : 55 + random.nextInt(10);
        int second = descending ? first - semitones : first + semitones;

        Question q = new Question();
        q.type = ExerciseType.INTERVAL;
        q.prompt = descending ? "¿Qué intervalo descendente escuchas?" : "¿Qué intervalo escuchas?";
        q.steps = Arrays.asList(new int[]{first}, new int[]{second});
        q.noteMs = 750;
        q.options = new String[choices.size()];
        q.optionIcons = new int[choices.size()];
        for (int i = 0; i < choices.size(); i++) {
            q.options[i] = INTERVAL_NAMES[choices.get(i)];
            q.optionIcons[i] = R.drawable.ic_interval;
            if (choices.get(i) == semitones) q.correct = i;
        }
        q.explanation = INTERVAL_NAMES[semitones] + " (" + noteName(first) + " → " + noteName(second) + "). "
                + (descending ? "" : INTERVAL_TIPS[semitones]);
        q.signature = "int" + semitones + "_" + first;
        return q;
    }

    private static Question chord(int difficulty, Random random) {
        int kinds = difficulty == 1 ? 2 : 4;
        int kind = random.nextInt(kinds);
        int root = 55 + random.nextInt(10);
        int[] shape = CHORD_SHAPES[kind];

        int[] block = new int[shape.length];
        List<int[]> steps = new ArrayList<>();
        for (int i = 0; i < shape.length; i++) {
            block[i] = root + shape[i];
            steps.add(new int[]{block[i]});
        }
        steps.add(block);

        Question q = new Question();
        q.type = ExerciseType.CHORD;
        q.prompt = "¿Qué tipo de acorde suena?";
        q.steps = steps;
        q.noteMs = 520;
        q.gapMs = 0;
        q.options = Arrays.copyOf(CHORD_NAMES, kinds);
        q.optionIcons = new int[kinds];
        Arrays.fill(q.optionIcons, R.drawable.ic_chord);
        q.correct = kind;
        q.explanation = "Era " + noteName(root) + " " + CHORD_NAMES[kind].toLowerCase() + ". " + CHORD_TIPS[kind];
        q.signature = "chord" + kind + "_" + root;
        return q;
    }

    /** La respuesta correcta más otras opciones distintas del grupo, mezcladas. */
    private static List<Integer> pickChoices(int[] pool, int correct, int max, Random random) {
        List<Integer> others = new ArrayList<>();
        for (int value : pool) if (value != correct) others.add(value);
        Collections.shuffle(others, random);
        List<Integer> choices = new ArrayList<>();
        choices.add(correct);
        for (int i = 0; i < others.size() && choices.size() < max; i++) choices.add(others.get(i));
        Collections.shuffle(choices, random);
        return choices;
    }

    public static String noteName(int midi) {
        return NOTE_NAMES[((midi % 12) + 12) % 12];
    }

    public static String noteNameWithOctave(int midi) {
        return noteName(midi) + (midi / 12 - 1);
    }

    private static int raw(Context context, String name) {
        return context.getResources().getIdentifier(name, "raw", context.getPackageName());
    }
}
