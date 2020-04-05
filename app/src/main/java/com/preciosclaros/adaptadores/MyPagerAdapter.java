package com.preciosclaros.adaptadores;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.preciosclaros.R;

public class MyPagerAdapter extends PagerAdapter {

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(getImageAt(position), null);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return o==view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private int getImageAt(int position) {
        switch (position) {
            case 0:
                return R.layout.tutorial_step_one;
            case 1:
                return R.layout.tutorial_step_two;
            default:
                return R.layout.tutorial_step_three;
        }
    }

}
