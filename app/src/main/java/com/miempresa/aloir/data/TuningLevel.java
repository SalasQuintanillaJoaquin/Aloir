package com.miempresa.aloir.data;

import com.miempresa.aloir.R;

/**
 * Los 7 niveles de afinación. Los audios de cada nivel desafinan la nota cada vez menos:
 * medido sobre los archivos de res/raw, de ±50 cents (nivel 1) a ±5 cents (nivel 7).
 */
public enum TuningLevel {
    L1(1, "Fácil", 50, R.color.level_1),
    L2(2, "Medio", 40, R.color.level_2),
    L3(3, "Difícil", 30, R.color.level_3),
    L4(4, "Avanzado", 20, R.color.level_4),
    L5(5, "Experto", 15, R.color.level_5),
    L6(6, "Maestro", 10, R.color.level_6),
    L7(7, "Oído absoluto", 5, R.color.level_7);

    public final int number;
    public final String name;
    public final int cents;
    public final int colorRes;
    public final String description;

    TuningLevel(int number, String name, int cents, int colorRes) {
        this.number = number;
        this.name = name;
        this.cents = cents;
        this.colorRes = colorRes;
        this.description = "Diferencia de ±" + cents + " cents";
    }

    public static TuningLevel get(int number) {
        return values()[Math.max(1, Math.min(7, number)) - 1];
    }
}
