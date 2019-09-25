package com.matchstick.brightlife.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.matchstick.brightlife.R;
import com.matchstick.brightlife.adapters.TourScreenPageAdapter;

public class WelcomeActivity extends AppCompatActivity {

    private LinearLayout layoutDot;
    private ViewPager viewPager;
    private TextView[] dotstv;
    private  int[] layouts;
    private Button btnSkip;
    private Button btnNext;
    private TourScreenPageAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!isFirstTimeStartApp()){
            startMainActivity();
            finish();
        }

        setStatusBarTransparent();
        setContentView(R.layout.activity_welcome);
        //fonts();
        viewPager = findViewById(R.id.view_pager);
        btnNext = findViewById(R.id.btn_next);
        btnSkip = findViewById(R.id.btn_skip);
        layoutDot = findViewById(R.id.dotlayout);

        //handing the button clicks
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start main activity
                startMainActivity();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPage = viewPager.getCurrentItem() + 1;
                if (currentPage < layouts.length){
                    //move to the next slider
                    viewPager.setCurrentItem(currentPage);
                }
                else{
                    startMainActivity();
                }
            }
        });
        layouts = new int[]{R.layout.slider_1, R.layout.slider_2,R.layout.slider_3, R.layout.slider_4};
        pagerAdapter = new TourScreenPageAdapter(layouts, getApplicationContext());
        viewPager.setAdapter(pagerAdapter);

        //handle on pagechangeListner and update dot layout
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == layouts.length-1){
                    //last pate
                    btnNext.setText("START");
                    btnSkip.setVisibility(View.GONE);
                }else{
                    btnNext.setText("NEXT");
                    btnSkip.setVisibility(View.VISIBLE);
                }
                dotStatus(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        dotStatus(0);
    }

    public void startMainActivity(){
        setFirstTimeStartStatus(false);
        startActivity(new Intent(WelcomeActivity.this, SplashActivity.class));
        finish();
    }

    //only show the slider at the first launch of the application
    private boolean isFirstTimeStartApp(){
        SharedPreferences ref = getApplication().getSharedPreferences("introSlider", Context.MODE_PRIVATE);
        return ref.getBoolean("firstTimeFlag", true);
    }
    private void setFirstTimeStartStatus(boolean stt){
        SharedPreferences ref = getApplication().getSharedPreferences("introSlider", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ref.edit();
        editor.putBoolean("firstTimeFlag", stt);
        editor.commit();
    }

    //setting the dotstatus in the dot layout
    private  void dotStatus(int page){
        layoutDot.removeAllViews();
        dotstv = new TextView[layouts.length];
        for(int i = 0; i<dotstv.length; i++){
            dotstv[i] = new TextView(this);
            dotstv[i].setText(Html.fromHtml("&#8226;"));
            dotstv[i].setTextSize(30);
            dotstv[i].setTextColor(Color.parseColor("#a9b4bb"));
            layoutDot.addView(dotstv[i]);
        }

        //set the current dot active
        if(dotstv.length > 0){
            dotstv[page].setTextColor(Color.parseColor("#ffffff"));
        }
    }

    //making statusbar tranparent
    private void setStatusBarTransparent(){
        //check the min SDK
        if(Build.VERSION.SDK_INT >= 21){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

}
