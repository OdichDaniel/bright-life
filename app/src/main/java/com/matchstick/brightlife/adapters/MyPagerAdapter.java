package com.matchstick.brightlife.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;


public class MyPagerAdapter extends ViewPager {
    private int position =0;
    private float downX, downY,upX, upY;
    private boolean scrollPastFirstView;


    public MyPagerAdapter(@NonNull Context context) {
        super(context);
    }

    public MyPagerAdapter(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPosition(int position){
        this.position = position;
    }

    public void canScrollPastFristView(boolean scrollPastFirstView){
        this.scrollPastFirstView = scrollPastFirstView;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            downX = ev.getX();
            downY = ev.getY();
            return true;
        }else{
            upX = ev.getX();
            upY = ev.getY();

            float deltaX = downX-upX;
            float deltaY = downY-upY;

            if (Math.abs(deltaX) > Math.abs(deltaY)){
                int min_distance = 400;
                if (Math.abs(deltaX)> min_distance){
                    if (deltaX < 0){
                        return super.onTouchEvent(ev);
                    }
                    if (deltaX > 0 ){
                        if (!scrollPastFirstView){
                            return false;
                        }
                        if (getCurrentItem() == position && getCurrentItem() != 0){
                            return false;
                        }else{
                            return super.onTouchEvent(ev);
                        }
                    }
                }
            }
        }
        return false;
    }
}

