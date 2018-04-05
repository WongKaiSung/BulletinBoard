package my.edu.tarc.bulletinboard.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import my.edu.tarc.bulletinboard.R;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        new Handler().postDelayed(() -> {
            Intent MainIntent = new Intent(SplashScreen.this,MainActivity.class);
            startActivity(MainIntent);
            finish();
        },SPLASH_TIME_OUT);
    }
}
