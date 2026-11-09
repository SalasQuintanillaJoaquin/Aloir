package com.miempresa.aloir;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miempresa.aloir.ui.Cards;
import com.miempresa.aloir.ui.Ui;

/** Acerca de Eargym. */
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Ui.titleBar(this, "Acerca de Eargym");
        LinearLayout content = findViewById(R.id.listContent);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo_eargym_neon);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        logo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.logo_height)));
        logo.setPadding(0, Ui.dp(this, 12), 0, 0);
        logo.setContentDescription("Logo de Eargym");
        content.addView(logo);

        TextView body = new androidx.appcompat.widget.AppCompatTextView(this);
        androidx.core.widget.TextViewCompat.setTextAppearance(body, R.style.Eargym_Text_Body);
        body.setText("Eargym® es la herramienta perfecta para músicos de todos los niveles. Mejora tu oído musical "
                + "con funciones intuitivas y personalizables, desde reconocer notas hasta ejercicios auditivos.\n\n"
                + "Diseñada para acompañarte en tu viaje musical, ofrece una experiencia accesible y efectiva para "
                + "afinar y entrenar tu oído.\n\n"
                + "Apreciamos el apoyo de nuestra comunidad de usuarios y el trabajo conjunto con músicos y expertos "
                + "en audio para ofrecerte la mejor experiencia posible.");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = Ui.dp(this, 20);
        body.setLayoutParams(lp);
        content.addView(body);

        View features = Cards.create(this, content, R.drawable.ic_hearing, R.color.eg_neon,
                "Para músicos desde los 10 años", "Afinación en 7 niveles, dirección melódica, notas, intervalos y acordes. Sin registros ni datos guardados: solo práctica.");
        content.addView(features);
        View version = Cards.create(this, content, R.drawable.ic_info, R.color.eg_violet,
                "Versión " + BuildConfig.VERSION_NAME, "Hecho con cariño para músicos.");
        content.addView(version);
    }
}
