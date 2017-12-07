package com.example.shirley.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shirley  on 2016/11/29.
 */
public class Guide extends Activity implements ViewPager.OnPageChangeListener{
    private ViewpagerAdapter vpAdapter;
    private ViewPager vp;
    private List<View> views;

    private ImageView[] dots;
    private int[] ids = {R.id.iv1,R.id.iv2,R.id.iv3};  //声明一个int型数组，存放3个小圆点控件的id

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) //在Activity中，通过setContView绑定布局
    {
        super.onCreate(savedInstanceState);
        final SharedPreferences first = ( SharedPreferences) getSharedPreferences( "first", MODE_PRIVATE);
        Boolean isFirst = first.getBoolean( "isFirst", true) ;
        if( isFirst) {
            setContentView( R. layout. guide) ;
            initViews( );
            initDots( );
            btn =( Button) views.get(2).findViewById( R.id.guide_btn) ;
            btn.setOnClickListener( new View. OnClickListener( ) {
                @Override
                public void onClick( View v) {
                    SharedPreferences.Editor editor = first.edit();
                    editor.putBoolean("isFirst", false);
                    editor.commit( ) ;
                    Intent intent = new Intent( Guide. this, MainActivity. class) ;
                    startActivity( intent) ;
                    finish( ) ;
                }
            });
        }
        else{
        Intent intent = new Intent(Guide.this,MainActivity.class);
        startActivity( intent);
        finish( );
        }
    }

    void initDots() //定义一个方法，将3个小圆点控件对象，存放dots数组中
    {
        dots = new ImageView[views.size()];
        for(int i=0;i<views.size();i++)
        {
            dots[i]=(ImageView) findViewById(ids[i]);
        }
    }

    private void initViews()  //initViews主要用于动态的加载要在ViewPager中显示的视图
    {
        LayoutInflater inflater =LayoutInflater.from(this);   //首先，获取LayoutInflater对象
        views =new ArrayList<View>();                        //构造View类型的数组。
        views.add(inflater.inflate(R.layout.page1,null));
        views.add(inflater.inflate(R.layout.page2,null));
        views.add(inflater.inflate(R.layout.page3,null));
        vpAdapter = new ViewpagerAdapter(views,this);
        vp = (ViewPager)findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);   //设置Viewpager适配器
        vp.setOnPageChangeListener(this);                    //为ViewPager控件设置页面变化的监听事件
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int i) {
        for(int a = 0;a<ids.length;a++){
            if(a==i){
                dots[a].setImageResource(R.drawable.page_indicator_focused);
            }else{
                dots[a].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
