package com.example.tmates;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    private ImageView logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logo = findViewById(R.id.logo);

        animateLogo(logo);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startLoginActivity();
            }
        }, 4000);
    }


    //  Splash screen - animate the logo.
    public void animateLogo(ImageView view){
        ObjectAnimator logoAnimate, logoAnimate1, logoAnimate2;
        logoAnimate = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
        logoAnimate1 = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f);
        logoAnimate2 = ObjectAnimator.ofFloat(view, "rotationY", 0f, 360f);
        logoAnimate.setDuration(2000);
        logoAnimate1.setDuration(2000);
        logoAnimate2.setDuration(2000);
        logoAnimate.start();
        logoAnimate1.start();
        logoAnimate2.start();
    }

    // Method that start the login activity.
    public void startLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
