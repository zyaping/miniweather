package com.example.shirley.miniweather;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by shirley on 2017/11/29.
 */


public class ViewpagerAdapter extends PagerAdapter
{
    private List<View> views;
    private Context context;

    public ViewpagerAdapter(List<View> views, Context context)
    {
        this.views=views;
        this.context=context;
    }
    @Override
    public int getCount() {

        return views.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)  //instantiateItem用于创建position所在位置的视图
    {
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) //重写destroyItem方法，用于删除position位置所制定的视图。
    {
        container.removeView(views.get(position));
    }

    @Override
    public boolean isViewFromObject(View view, Object o) //isViewFromObject函数用于判断instantiateItem返回的对象是否与当前View代表的是同一个对象。
    {
        return (view == o);
    }
}
