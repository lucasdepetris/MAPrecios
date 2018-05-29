package com.preciosclaros;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.mikepenz.materialize.color.Material;

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();



        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("Busca Productos", "Busca un producto mediante su nombre o escaneando su codigo de barra.",R.drawable.buscar_product, getResources().getColor(R.color.grayBackground)));
        addSlide(AppIntroFragment.newInstance("Ahorra Dinero", "Elegpur++i el mejor precio segun tus prioridades!",R.drawable.save_money_intro, getResources().getColor(R.color.grayBackground)));
        addSlide(AppIntroFragment.newInstance("Ubicación", "Busca los productos cercanos al punto que elijas.",R.drawable.gps_intro_circle, getResources().getColor(R.color.grayBackground)));

        // OPTIONAL METHODS
        // Override bar/separator color.
        //setBarColor(Color.parseColor("#B0C4DE"));
        //setSeparatorColor(Color.parseColor("#2196F3"));


        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(false);
        //setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        Intent mainIntent = new Intent(IntroActivity.this,HomeActivity.class);
        IntroActivity.this.startActivity(mainIntent);
        IntroActivity.this.finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        Intent mainIntent = new Intent(IntroActivity.this,HomeActivity.class);
        IntroActivity.this.startActivity(mainIntent);
        IntroActivity.this.finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
