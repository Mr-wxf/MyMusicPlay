package com.musicplay.administrator.mymusicplay.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Administrator on 2016/11/29.
 */
public class ViewPagerAdapter extends PagerAdapter {
    public Context context;
    private List<ImageView> imagerList;

    public ViewPagerAdapter(Context context, List<ImageView> imagerList){
        this.context=context;
        this.imagerList=imagerList;
    }

    @Override
    public int getCount() {
        return imagerList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(imagerList.get(position));
        return imagerList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(imagerList.get(position));
    }
}
