package com.miempresa.aloir;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.miempresa.aloir.ui.Ui;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Ui.popIn(findViewById(R.id.splashLogo), 100);
        Ui.riseIn(findViewById(R.id.splashFor), 450);
        Ui.riseIn(findViewById(R.id.splashTagline), 600);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isFinishing()) return;
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2200);
    }
}
