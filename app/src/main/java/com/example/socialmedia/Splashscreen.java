package com.example.socialmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splashscreen extends AppCompatActivity {

    ImageView imageView;
    TextView text1, text2;
    long animTime = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);

        imageView = findViewById(R.id.logo_splash);
        text1 = findViewById(R.id.text_splash);
        text2 = findViewById(R.id.text_splash_2);

        ObjectAnimator animatorY = ObjectAnimator.ofFloat(imageView, "y", 400f);
        ObjectAnimator name = ObjectAnimator.ofFloat(text1, "x", 200f);
        animatorY.setDuration(animTime);
        name.setDuration(animTime);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorY, name);
        animatorSet.start();


    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(user != null){

                    Intent intent = new Intent(Splashscreen.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                }else{

                    Intent intent = new Intent(Splashscreen.this,LoginActivity.class);
                    startActivity(intent);

                }

            }
        },4000);
    }
}