package com.preciosclaros;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.preciosclaros.adaptadores.MyPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TutorialActivity extends AppCompatActivity {

    @BindView(R.id.view_pager)
    ViewPager vp;

    @BindView(R.id.next)
    Button next;
    @OnClick({R.id.next}) public void nextClicked(Button btn){
        int i = getItem(+1);
        if(i == screens.length - 1){
            next.setText("Entendido");
            skip.setVisibility(View.GONE);
        }
        if (i < screens.length) {
            vp.setCurrentItem(i);
        } else {
            launchMain();
        }
    }

    @BindView(R.id.skip)
    Button skip;
    @OnClick({R.id.skip}) public void skipClicked(Button btn){
        launchMain();
    }

    int[] screens;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        ButterKnife.bind(this);
        MyPagerAdapter myPagerViewAdapter = new MyPagerAdapter();
        vp.setAdapter(myPagerViewAdapter);
        screens = new int[]{
                R.layout.tutorial_step_one,
                R.layout.tutorial_step_two,
                R.layout.tutorial_step_three
        };
        vp.addOnPageChangeListener(viewPagerPageChangeListener);

    }

    public void next(View v) {
        int i = getItem(+1);
        if (i < screens.length) {
            vp.setCurrentItem(i);
        } else {
            launchMain();
        }
    }

    private int getItem(int i) {
        return vp.getCurrentItem() + i;
    }

    private void launchMain() {
        if(!this.isHomeVisible()){
            Intent mainIntent = new Intent(TutorialActivity.this,HomeActivity.class);
            TutorialActivity.this.startActivity(mainIntent);
        }
        TutorialActivity.this.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

            if (position == screens.length - 1) {
                next.setText("Entendido");
                skip.setVisibility(View.GONE);
            } else {
                next.setText("Siguiente");
                skip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public boolean isHomeVisible(){
        Intent intent = getIntent();
        String actividad = intent.getStringExtra(Constants.ACTIVITY);
        if(actividad != null && actividad.equalsIgnoreCase(Constants.HOME_ACTIVITY)){
            return true;
        }
        return false;
    }
}
