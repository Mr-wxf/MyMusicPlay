package com.musicplay.administrator.mymusicplay.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.musicplay.administrator.mymusicplay.R;
import com.musicplay.administrator.mymusicplay.Utils.SpUtil;
import com.musicplay.administrator.mymusicplay.Value.Value;
import com.musicplay.administrator.mymusicplay.adapter.ViewPagerAdapter;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    private ViewPager vp;
    private Button bt;
    private ArrayList<ImageView> imagerList;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        boolean result = SpUtil.getBoolean(this, Value.SPLASHVALUE);
        if(result){
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        vp = (ViewPager) findViewById(R.id.vp);
        bt = (Button) findViewById(R.id.bt);
        imagerList = new ArrayList<ImageView>();
        int[] ints = {R.drawable.splash1, R.drawable.splash2, R.drawable.splash3};
        for (int i = 0; i < ints.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(ints[i]);
            imagerList.add(imageView);
        }

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, imagerList);
        vp.setAdapter(viewPagerAdapter);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                int currentItem = vp.getCurrentItem();
                Log.d("tag", currentItem + "");
                if (currentItem == vp.getAdapter().getCount() - 1) {
                    bt.setVisibility(View.VISIBLE);
                } else {
                    bt.setVisibility(View.GONE);
                }
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                SpUtil.putBoolean(SplashActivity.this, Value.SPLASHVALUE,true);
                finish();

            }
        });

    }
}
