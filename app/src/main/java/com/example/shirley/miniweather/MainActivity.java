package com.example.shirley.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shirley.bean.FutureWeather;
import com.example.shirley.bean.TodayWeather;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shirley on 2017/9/28.
 */

public class MainActivity extends Activity implements View.OnClickListener,ViewPager.OnPageChangeListener
    {
        private static final int UPDATE_TODAY_WEATHER = 1;
        private static final int UPDATE_FUTURE_WEATHER = 2;

        private ImageView mUpdateBtn;       //在UI线程中,为更新按钮(ImageView)增加单击事件.
        private ImageView mCitySelect;      //为选择城市ImageView添加OnClick事件

        private TextView cityTv,timeTv,humidityTv,weekTv,pmDataTv,pmQualityTv,
                temperatureTv,climateTv,windTv,city_name_Tv;
        private ProgressBar pbar;
        private ImageView weatherImg,pmImg;

        //更新图标旋转
        Animation operatingAnim = null;
        //LinearInterpolator为匀速效果
        LinearInterpolator lin = null;

        TodayWeather todayWeather;
        FutureWeather futureWeather;
        //-----------------------------
        private ImageView firstdayImg,nextdayImg, thirddayImg, fourthdayImg, fifthdayImg, sisthdayImg;
        private TextView firstdayWeek,nextdayWeek, thirddayWeek, fourthdayWeek,wendu, fifthdayWeek, sisthdayWeek;
        private TextView firstdayTemp ,nextdayTemp , thirddayTemp, fourthdayTemp, fifthdayTemp, sisthdayTemp;
        private TextView firstdayWind ,nextdayWind , thirddayWind, fourthdayWind, fifthdayWind,sisthdayWind;
        private TextView firstdayClimate ,nextdayClimate , thirddayClimate, fourthdayClimate,fifthdayClimate,sisthdayClimate;
        //-----------------------------

        //ViewPager ！！！
        private ViewpagerAdapter vpAdapter;
        private ViewPager vp;
        private List<View> views;  //需要滑动的界面
        private ImageView[] dots;
        private int[] ids = {R.id.iv1, R.id.iv2};
        //=========================================

        private Handler mHandler = new Handler( ) {
            public void handleMessage(  Message msg) {
                switch ( msg. what) {
                    case UPDATE_TODAY_WEATHER:
                        updateTodayWeather( (TodayWeather) msg. obj ); //通过消息机制,将解析的天气对象， 通过消息发送给主线程，主线程接收到消息数据后 ，调用 updateTodayWeather函数，更新UI界面上的数据。
                        updateImage((TodayWeather)msg.obj);
                        break;
                    case UPDATE_FUTURE_WEATHER:
                        updateFutureWeather((FutureWeather) msg.obj);
                        updateImage_future((FutureWeather)msg.obj);
                        break;
                    default:
                        break;
                }
            }
        };

        @Override
        protected void onCreate( Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState) ;
            setContentView(R.layout.weather_info);

            if ( NetUtil. getNetworkState( this) != NetUtil.NETWORN_NONE) {
            Log.d( "myWeather", "网络OK") ;
            Toast.makeText( MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show( ) ;
        }
            else
            {
                Log.d( "myWeather", " 网络挂了");
                Toast.makeText( MainActivity.this, " 网络挂了！", Toast.LENGTH_LONG).show( );
            }
            //点击更新按钮，实现动画效果,具体的动画操作在函数queryWeatherCode（）处.
            mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        	mUpdateBtn.setOnClickListener(this);

            mCitySelect = (ImageView)findViewById(R.id.title_city_manager);
            mCitySelect.setOnClickListener(this);

           //开启后台服务
            startService(new Intent(getBaseContext(), UpdateService.class));
           //初始化控件
            initViews(); //初始化未来六天天气
            initDots();

            initView();
        }
    public void setType(String weatherType, ImageView weatherImg) {
        if (weatherType.equals("暴雪"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
        if (weatherType.equals("暴雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
        if (weatherType.equals("大暴雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
        if (weatherType.equals("大雪"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
        if (weatherType.equals("大雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
        if (weatherType.equals("多雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
        if (weatherType.equals("雷阵雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
        if (weatherType.equals("雷阵冰雹"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
        if (weatherType.equals("晴"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
        if (weatherType.equals("沙尘暴"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
        if (weatherType.equals("特大暴雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
        if (weatherType.equals("雾"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
        if (weatherType.equals("小雪"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
        if (weatherType.equals("小雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
        if (weatherType.equals("阴"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
        if (weatherType.equals("阵雪"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
        if (weatherType.equals("阵雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
        if (weatherType.equals("雨夹雪"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
        if (weatherType.equals("中雪"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
        if (weatherType.equals("中雨"))
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
    }
    @Override
    public void onClick(View view)
    {
        if(view.getId()== R.id.title_city_manager)
        {
            Intent i = new Intent(this,SelectCity.class);
            startActivityForResult(i,1);  //修改更新按钮的单击事件处理程序
        }
        if (view.getId() == R.id.title_update_btn)
        {
            //点击更新按钮，实现动画效果
            //mUpdateBtn.startAnimation(AnimationUtils.loadAnimation(this,R.anim.title_update_anim));

            //从SharedPreferences中 读取城市的id, 其中101010100为北京城市ID.
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            Log.d("myWeather",cityCode);

            //if-else 调用检测网络连接状态方法,以检测网络是否连接
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE)
            {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);       //调用方法，获取网络数据.
            }
            else
            {
                Log.d("myWeather", " 网络挂了 ");
                Toast.makeText(MainActivity.this, " 网络挂了 ！", Toast.LENGTH_LONG).show();
            }
        }
    }

    /*
    *编写onActivityResult函数, 用于接收返回的数据。
     */
    protected void onActivityResult( int requestCode, int resultCode, Intent data) {
        if ( requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode= data.getStringExtra( "cityCode") ;

            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit(); //SharedPreference中写入数据需要使用Editor
            editor.putString("main_city_code", newCityCode); //键值对newCityCode——>main_city_code（值）
            editor.commit();            //点击返回后，使用SharedPreferences保存当前数据，参考 http://www.cnblogs.com/ywtk/p/3795184.html

            Log. d( "myWeather", " 选择的城市代码为 "+newCityCode) ;
            if ( NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log. d( "myWeather", " 网络OK") ;
                queryWeatherCode(newCityCode) ;   //此处可以进一步优化，选择在city_select页面选择后刷新还是在返回后刷新,都可以.
            } else {
            Log. d( "myWeather", " 网络挂了 ") ;
            Toast. makeText( MainActivity. this, " 网络挂了 ！ ", Toast. LENGTH_LONG) . show( );
        }
        }
    }
    /*
   *初始化控件TextView内容
    */
    void initView( ) {
        city_name_Tv = (TextView) findViewById( R.id.title_city_name) ;
        cityTv = ( TextView) findViewById( R.id.city) ;
        timeTv = ( TextView) findViewById( R.id.time) ;
        humidityTv = (TextView) findViewById( R.id.humidity) ;
        weekTv = (TextView) findViewById( R.id.week_today) ;
        pmDataTv = (TextView) findViewById( R.id.pm_data) ;
        pmQualityTv = (TextView) findViewById( R.id.pm2_5_quality) ;
        pmImg = ( ImageView) findViewById( R.id.pm2_5_img) ;
        temperatureTv = (TextView) findViewById( R.id.temperature) ;
        climateTv = (TextView) findViewById( R.id.climate) ;
        windTv = (TextView) findViewById( R.id.wind) ;
        weatherImg = (ImageView) findViewById( R.id.weather_img);
        city_name_Tv.setText( "N/A") ;
        cityTv.setText( "N/A") ;
        timeTv.setText( "N/A") ;
        humidityTv.setText("N/A") ;
        pmDataTv.setText( "N/A") ;
        pmQualityTv.setText("N/A") ;
        weekTv. setText( "N/A") ;
        temperatureTv.setText("N/A") ;
        climateTv.setText("N/A") ;
        windTv.setText("N/A") ;

    }

        //*************************************
        void initDots(){
            dots = new ImageView[views.size()];
            for(int i=0; i<views.size(); i++){
                dots[i] = (ImageView)findViewById(ids[i]);
            }
        }
        private  void initViews(){

            //********************************* PageView  加载要显示的页卡(页面)
            LayoutInflater inflater = LayoutInflater.from(this);  //
            views = new ArrayList<View>();  // 将要分页显示的View装入List中
            views.add(inflater.inflate(R.layout.weatherpage1,null));
            views.add(inflater.inflate(R.layout.weatherpage2,null));
            vpAdapter = new ViewpagerAdapter(views,this); //ViewPageAdapter
            vp = (ViewPager)findViewById(R.id.viewpager2);
            vp.setAdapter(vpAdapter);

            //设置滑动监听！！！
            //vp.setOnPageChangeListener(this);

            //*********************************  未来天气
            //第x日的天气
            firstdayImg = (ImageView)views.get(0).findViewById(R.id.firstday_img);
            nextdayImg = (ImageView)views.get(0).findViewById(R.id.nextday_img);
            thirddayImg = (ImageView)views.get(0).findViewById(R.id.thirdday_img);
            fourthdayImg = (ImageView)views.get(1).findViewById(R.id.fourthday_img);
            fifthdayImg = (ImageView)views.get(1).findViewById(R.id.fifthday_img);
            sisthdayImg = (ImageView)views.get(1).findViewById(R.id.sisthday_img);

            firstdayWeek = (TextView)views.get(0).findViewById(R.id.firstday_week);
            nextdayWeek = (TextView)views.get(0).findViewById(R.id.nextday_week);
            thirddayWeek = (TextView)views.get(0).findViewById(R.id.thirdday_week);
            fourthdayWeek = (TextView)views.get(1).findViewById(R.id.fourthday_week);
            fifthdayWeek = (TextView)views.get(1).findViewById(R.id.fifthday_week);
            sisthdayWeek = (TextView)views.get(1).findViewById(R.id.sisthday_week);

            firstdayTemp = (TextView)views.get(0).findViewById(R.id.firstday_temp);
            nextdayTemp = (TextView)views.get(0).findViewById(R.id.nextday_temp);
            thirddayTemp = (TextView)views.get(0).findViewById(R.id.thirdday_temp);
            fourthdayTemp = (TextView)views.get(1).findViewById(R.id.fourthday_temp);
            fifthdayTemp = (TextView)views.get(1).findViewById(R.id.fifthday_temp);
            sisthdayTemp = (TextView)views.get(1).findViewById(R.id.sisthday_temp);

            firstdayWind = (TextView)views.get(0).findViewById(R.id.firstday_wind);
            nextdayWind = (TextView)views.get(0).findViewById(R.id.nextday_wind);
            thirddayWind = (TextView)views.get(0).findViewById(R.id.thirdday_wind);
            fourthdayWind = (TextView)views.get(1).findViewById(R.id.fourthday_wind);
            fifthdayWind = (TextView)views.get(1).findViewById(R.id.fifthday_wind);
            sisthdayWind = (TextView)views.get(1).findViewById(R.id.sisthday_wind);

            firstdayClimate = (TextView)views.get(0).findViewById(R.id.firstday_climate);
            nextdayClimate = (TextView)views.get(0).findViewById(R.id.nextday_climate);
            thirddayClimate = (TextView)views.get(0).findViewById(R.id.thirdday_climate);
            fourthdayClimate = (TextView)views.get(1).findViewById(R.id.fourthday_climate);
            fifthdayClimate = (TextView)views.get(1).findViewById(R.id.fifthday_climate);
            sisthdayClimate = (TextView)views.get(1).findViewById(R.id.sisthday_climate);
        }

        //*********************  ViewPager的一些监听处理
        @Override
        public void onPageScrolled(int i, float v, int i2) {
        }
        //
        @Override
        public void onPageSelected(int i) {
            for(int a=0; a<ids.length; a++){    //改变显示的小圆圈图标
                if(a == i){
                    dots[a].setImageResource(R.drawable.page_indicator_focused);
                }else{
                    dots[a].setImageResource(R.drawable.page_indicator_unfocused);
                }
            }
        }
        @Override
        public void onPageScrollStateChanged(int i) {
        }

    /*
    *编写解析函数， 解析出城市名称已经更新时间信息
     */
    private TodayWeather parseXML(String xmldata)
    {
        todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try{
                XmlPullParserFactory fac = XmlPullParserFactory.newInstance( ) ;
                XmlPullParser xmlPullParser = fac.newPullParser( );
                xmlPullParser.setInput( new StringReader( xmldata));
                int eventType = xmlPullParser.getEventType( ) ;
                Log.d( "myWeather", "parseXML") ;
                while ( eventType != XmlPullParser.END_DOCUMENT)
                {
                    switch ( eventType)
                    {
                        // 判 断当 前事件是否为 文档 开始事件
                        case XmlPullParser.START_DOCUMENT:
                        break;
                        // 判 断当 前事件是否为 标签元素开始事件
                        case XmlPullParser.START_TAG:
                            if(xmlPullParser. getName( ).equals( "resp")) {
                                todayWeather= new TodayWeather( );
                            }
                            if ( todayWeather != null) {
                                if (xmlPullParser.getName().equals("city")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather. setCity( xmlPullParser.getText( ));
                                    //Log.d("myWeather", "city: " + xmlPullParser.getText());  //将城市信息以及更新时间解析出来
                                } else if (xmlPullParser.getName().equals("updatetime")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setUpdatetime( xmlPullParser.getText( ) ) ;
                                    //Log.d("myWeather", "updatetime:" + xmlPullParser.getText());  //将城市信息以及更新时间解析出来
                                } else if (xmlPullParser.getName().equals("shidu")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather. setShidu( xmlPullParser.getText( ) ) ;
                                    //Log.d("myWeather", "shidu: " + xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("wendu")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather. setWendu( xmlPullParser.getText( ) ) ;
                                    //Log.d("myWeather", "wendu: " + xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("pm25")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setPm25( xmlPullParser.getText( ) ); ;
                                    //Log.d("myWeather", "pm25: " + xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("quality")) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setQuality( xmlPullParser.getText( ) ) ;
                                    //Log.d("myWeather", "quality: " + xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setFengxiang( xmlPullParser.getText( ) );
                                    //Log.d("myWeather", "fengxiang: " + xmlPullParser.getText());
                                    fengxiangCount++;
                                } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setFengli( xmlPullParser.getText( ) );
                                    //Log.d("myWeather", "fengli: " + xmlPullParser.getText());
                                    fengliCount++;
                                } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setDate( xmlPullParser.getText( ) );
                                    //Log.d("myWeather", "date: " + xmlPullParser.getText());
                                    dateCount++;
                                } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setHigh( xmlPullParser.getText( ).substring(2).trim( ));
                                    //Log.d("myWeather", "high: " + xmlPullParser.getText());
                                    highCount++;
                                } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setLow( xmlPullParser.getText( ).substring(2).trim( ));
                                    //Log.d("myWeather", "low: " + xmlPullParser.getText());
                                    lowCount++;
                                } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                    eventType = xmlPullParser.next();
                                    todayWeather.setType( xmlPullParser.getText( ) );
                                    //Log.d("myWeather", "type: " + xmlPullParser.getText());
                                    typeCount++;
                                }
                            }
                    break;
             // 判断当前事件是否为标签元素结束事件
                 case XmlPullParser.END_TAG:
                    break;
            }// 进入下一个元素并触发相应事件
                    eventType = xmlPullParser.next();
        }
        }
        catch ( XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }
    //编写解析函数
    public static FutureWeather parseXML_future_weather(String xmldata) {
            FutureWeather futureWeather = null;
            try {
                int dateCount = 0;
                int highCount = 0;
                int lowCount = 0;
                int typeCount = 0;
                int windCount = 0;

                XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
                XmlPullParser xmlPullParser = fac.newPullParser();
                xmlPullParser.setInput(new StringReader(xmldata));
                int eventType = xmlPullParser.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG:
                            if (xmlPullParser.getName().equals("resp")) {
                                futureWeather = new FutureWeather();
                            }
                            if (futureWeather != null) {
                                if (xmlPullParser.getName().equals("date_1") && dateCount == 0) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setFirstday_week(xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("high_1") && highCount == 0) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setFirstday_high(xmlPullParser.getText().substring(2).trim());
                                } else if (xmlPullParser.getName().equals("low_1") && lowCount == 0) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setFirstday_low(xmlPullParser.getText().substring(2).trim());
                                } else if (xmlPullParser.getName().equals("type_1") && typeCount == 0) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setFirstday_climate_day(xmlPullParser.getText());
                                    typeCount++;
                                }else if (xmlPullParser.getName().equals("type_1") && typeCount == 1) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setFirstday_climate_night(xmlPullParser.getText());
                                    typeCount++;
                                }else if (xmlPullParser.getName().equals("fx_1") && windCount == 0) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setFirstday_wind(xmlPullParser.getText());
                                    windCount++;
                                }else if (xmlPullParser.getName().equals("fx_1") && windCount == 1) {
                                    eventType = xmlPullParser.next();
                                    windCount++;
                                }

                                else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setNextday_week(xmlPullParser.getText());
                                    dateCount++;
                                } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setNextday_high(xmlPullParser.getText().substring(2).trim());
                                    highCount++;
                                } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setNextday_low(xmlPullParser.getText().substring(2).trim());
                                    lowCount++;
                                } else if (xmlPullParser.getName().equals("type") && typeCount == 2) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setNextday_climate_day(xmlPullParser.getText());
                                    typeCount++;
                                } else if (xmlPullParser.getName().equals("type") && typeCount == 3) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setNextday_climate_night(xmlPullParser.getText());
                                    typeCount++;
                                }else if (xmlPullParser.getName().equals("fengxiang") && windCount == 2) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setNextday_wind(xmlPullParser.getText());
                                    windCount++;
                                } else if (xmlPullParser.getName().equals("fengxiang") && windCount == 3) {
                                    eventType = xmlPullParser.next();
                                    windCount++;
                                }

                                else if (xmlPullParser.getName().equals("date") && dateCount == 1) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setThirdday_week(xmlPullParser.getText());
                                    dateCount++;
                                } else if (xmlPullParser.getName().equals("high") && highCount == 1) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setThirdday_high(xmlPullParser.getText().substring(2).trim());
                                    highCount++;
                                } else if (xmlPullParser.getName().equals("low") && lowCount == 1) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setThirdday_low(xmlPullParser.getText().substring(2).trim());
                                    lowCount++;
                                } else if (xmlPullParser.getName().equals("type") && typeCount == 4) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setThirdday_climate_day(xmlPullParser.getText());
                                    typeCount++;
                                } else if (xmlPullParser.getName().equals("type") && typeCount == 5) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setThirdday_climate_night(xmlPullParser.getText());
                                    typeCount++;
                                } else if (xmlPullParser.getName().equals("fengxiang") && windCount == 4) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setThirdday_wind(xmlPullParser.getText());
                                    windCount++;
                                } else if (xmlPullParser.getName().equals("fengxiang") && windCount == 5) {
                                    eventType = xmlPullParser.next();
                                    windCount++;
                                }

                                else if (xmlPullParser.getName().equals("date") && dateCount == 2) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setFourthday_week(xmlPullParser.getText());
                                    dateCount++;
                                } else if (xmlPullParser.getName().equals("high") && highCount == 2) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setFourthday_high(xmlPullParser.getText().substring(2).trim());
                                    highCount++;
                                } else if (xmlPullParser.getName().equals("low") && lowCount == 2) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setFourthday_low(xmlPullParser.getText().substring(2).trim());
                                    lowCount++;
                                } else if (xmlPullParser.getName().equals("type") && typeCount == 6) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setFourthday_climate_day(xmlPullParser.getText());
                                    typeCount++;
                                } else if (xmlPullParser.getName().equals("type") && typeCount == 7) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setFourthday_climate_night(xmlPullParser.getText());
                                    typeCount++;
                                } else if (xmlPullParser.getName().equals("fengxiang") && windCount == 6) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setFourthday_wind(xmlPullParser.getText());
                                    windCount++;
                                } else if (xmlPullParser.getName().equals("fengxiang") && windCount == 7) {
                                    eventType = xmlPullParser.next();
                                    windCount++;
                                }

                                else if (xmlPullParser.getName().equals("date") && dateCount == 3) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setFifthday_week(xmlPullParser.getText());
                                    dateCount++;
                                } else if (xmlPullParser.getName().equals("high") && highCount == 3) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setFifthday_high(xmlPullParser.getText().substring(2).trim());
                                    highCount++;
                                } else if (xmlPullParser.getName().equals("low") && lowCount == 3) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setFifthday_low(xmlPullParser.getText().substring(2).trim());
                                    lowCount++;
                                } else if (xmlPullParser.getName().equals("type") && typeCount == 8) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setFifthday_climate_day(xmlPullParser.getText());
                                    typeCount++;
                                }else if (xmlPullParser.getName().equals("type") && typeCount == 9) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setFifthday_climate_night(xmlPullParser.getText());
                                    typeCount++;
                                }else if (xmlPullParser.getName().equals("fengxiang") && windCount == 8) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setFifthday_wind(xmlPullParser.getText());
                                    windCount++;
                                }else if (xmlPullParser.getName().equals("fengxiang") && windCount == 9) {
                                    eventType = xmlPullParser.next();
                                    windCount++;
                                }

                                else if (xmlPullParser.getName().equals("date") && dateCount == 4) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setSisthday_week(xmlPullParser.getText());
                                    dateCount++;
                                } else if (xmlPullParser.getName().equals("high") && highCount == 4) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setSisthday_high(xmlPullParser.getText().substring(2).trim());
                                    highCount++;
                                } else if (xmlPullParser.getName().equals("low") && lowCount == 4) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setSisthday_low(xmlPullParser.getText().substring(2).trim());
                                    lowCount++;
                                } else if (xmlPullParser.getName().equals("type") && typeCount == 10) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setSisthday_climate_day(xmlPullParser.getText());
                                    typeCount++;
                                }else if (xmlPullParser.getName().equals("type") && typeCount == 11) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setSisthday_climate_night(xmlPullParser.getText());
                                    typeCount++;
                                }else if (xmlPullParser.getName().equals("fengxiang") && windCount == 10) {
                                    eventType = xmlPullParser.next();
                                    futureWeather.setSisthday_wind(xmlPullParser.getText());
                                    windCount++;
                                }else if (xmlPullParser.getName().equals("fengxiang") && windCount == 11) {
                                    eventType = xmlPullParser.next();
                                    windCount++;
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                    }
                    eventType = xmlPullParser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return futureWeather;
        }

        /*
        *使用**获取网络数据
        *@param cityCode
        */
     private void queryWeatherCode( String cityCode)
     {
         final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey="+cityCode;
         Log.d( "myWeather", address) ;

         //更新按钮旋转
         operatingAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.title_update_anim);
         lin = new LinearInterpolator(); //LinearInterpolator为匀速效果
         operatingAnim.setInterpolator(lin);//设置旋转效果
         //开始旋转
         if (operatingAnim != null) {
             mUpdateBtn.startAnimation(operatingAnim);
             Log.d("start anim","旋转啦");
         }

           //使用HttpClient获取网络数据
          //创建线程
            new Thread( new Runnable( ) {
                @Override
                public void run( ) {
                    HttpURLConnection con=null;
                    try{
                        URL url = new URL(address);
                        con = (HttpURLConnection) url.openConnection() ;
                        con.setRequestMethod( "GET") ;
                        con. setConnectTimeout( 8000) ;
                        con. setReadTimeout( 8000) ;
                        InputStream in = con.getInputStream( ) ;
                        BufferedReader reader = new BufferedReader( new InputStreamReader( in) ) ;
                        StringBuilder response = new StringBuilder( );
                        String str;
                        while(( str=reader.readLine( )) != null) {
                            response. append( str) ;
                            Log.d( "myWeather", str) ;
                        }
                        String responseStr=response.toString( ) ;
                        Log.d( "myWeather", responseStr) ;

                        todayWeather = null;
                        futureWeather = null;
                        //解析XML
                        todayWeather = parseXML(responseStr);        //获取网络数据后，调用解析函数
                        futureWeather = parseXML_future_weather(responseStr);

                        if(todayWeather != null){
                            Log.d("myWeather",todayWeather.toString());  //调用 parseXML，并返回TodayWeather对象。

                            Message msg =new Message( );        //通过消息机制，将解析的天气对象，通过消息发送给主线程，主线程接收到消息数据后 ，调用updateTodayWeather函数,更新UI界面上的数据。
                            msg.what = UPDATE_TODAY_WEATHER;
                            msg.obj = todayWeather;
                            mHandler.sendMessage( msg) ;
                        }
                        if(futureWeather != null){
                            Message msg = new Message();
                            msg.what = UPDATE_FUTURE_WEATHER;
                            msg.obj = futureWeather;
                            mHandler.sendMessage(msg); //给主线程发送消息！！！
                        }else{
                            Log.d("future weather","null");
                        }
                    }catch ( Exception e) {
                        e. printStackTrace( ) ;
                    }finally {
                        if( con != null) {
                            con.disconnect( ) ;
                        }
                    }
                }
            }) .start( );
        }
    /*
    *编写 updateTodayWeather 函数,利 用 TodayWeather对象更新UI中 的控件
     */
    void updateTodayWeather( TodayWeather todayWeather) {
        city_name_Tv. setText( todayWeather. getCity( ) +" 天气") ;
        cityTv. setText( todayWeather. getCity( ) ) ;
        timeTv. setText( todayWeather. getUpdatetime( ) + " 发布") ;
        humidityTv. setText( " 湿度： "+todayWeather. getShidu( ) ) ;
        pmDataTv. setText( todayWeather. getPm25( ) ) ;
        pmQualityTv. setText( todayWeather. getQuality( ) ) ;
        weekTv. setText( todayWeather. getDate( ) ) ;
        temperatureTv. setText( todayWeather. getHigh( ) +"~"+todayWeather. getLow( ) ) ;
        climateTv. setText( todayWeather. getType( ) ) ;
        windTv. setText( " 风力 : "+todayWeather. getFengli( ) ) ;
        Toast. makeText( MainActivity. this, " 更新成功！ ", Toast. LENGTH_SHORT) . show( ) ;
    }

        //-------------------  未来天气(更新文字信息)  -------------------
        public void updateFutureWeather(FutureWeather futureWeather) {
            Log.d("myapp2", futureWeather.toString());
            firstdayTemp.setText(futureWeather.getFirstday_low()+ "~" + futureWeather.getFirstday_high());
            nextdayTemp.setText(futureWeather.getNextday_low()+ "~" + futureWeather.getNextday_high());
            thirddayTemp.setText(futureWeather.getThirdday_low()+ "~" + futureWeather.getThirdday_high());
            fourthdayTemp.setText(futureWeather.getFourthday_low()+ "~" + futureWeather.getFourthday_high());
            fifthdayTemp.setText(futureWeather.getFifthday_low()+ "~" + futureWeather.getFifthday_high());
            sisthdayTemp.setText(futureWeather.getSisthday_low()+ "~" + futureWeather.getSisthday_high());

            firstdayWeek.setText(futureWeather.getFirstday_week());
            nextdayWeek.setText(futureWeather.getNextday_week());
            thirddayWeek.setText(futureWeather.getThirdday_week());
            fourthdayWeek.setText(futureWeather.getFourthday_week());
            fifthdayWeek.setText(futureWeather.getFifthday_week());
            sisthdayWeek.setText(futureWeather.getSisthday_week());

            firstdayClimate.setText(futureWeather.getFirstday_climate_day()+"转"+futureWeather.getFirstday_climate_night());
            nextdayClimate.setText(futureWeather.getNextday_climate_day()+"转"+futureWeather.getNextday_climate_night());
            thirddayClimate.setText(futureWeather.getThirdday_climate_day()+"转"+futureWeather.getThirdday_climate_night());
            fourthdayClimate.setText(futureWeather.getFourthday_climate_day()+"转"+futureWeather.getFourthday_climate_night());
            fifthdayClimate.setText(futureWeather.getFifthday_climate_day()+"转"+futureWeather.getFifthday_climate_night());
            sisthdayClimate.setText(futureWeather.getSisthday_climate_day()+"转"+futureWeather.getSisthday_climate_night());

            firstdayWind.setText(futureWeather.getFirstday_wind());
            nextdayWind.setText(futureWeather.getNextday_wind());
            thirddayWind.setText(futureWeather.getThirdday_wind());
            fourthdayWind.setText(futureWeather.getFourthday_wind());
            fifthdayWind.setText(futureWeather.getFifthday_wind());
            sisthdayWind.setText(futureWeather.getSisthday_wind());

            Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
        }
        //更新图片信息
        public void updateImage(TodayWeather todayWeather){
            String climate = todayWeather.getType();
            if(climate.equalsIgnoreCase("暴雪")){
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
            }
            if(climate.equalsIgnoreCase("暴雨")){
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
            }
            if(climate.equalsIgnoreCase("大暴雨")){
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
            }
            if(climate.equalsIgnoreCase("大雪")){
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
            }
            if(climate.equalsIgnoreCase("大雨")){
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
            }
            if(climate.equalsIgnoreCase("中雨")){
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
            }
            if(climate.equalsIgnoreCase("小雨")){
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
            }
            if(climate.equalsIgnoreCase("雷阵雨")){
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
            }
            if(climate.equalsIgnoreCase("雷阵雨冰雹")){
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
            }
            if(climate.equalsIgnoreCase("多云")){
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
            }
            if(climate.equalsIgnoreCase("晴")){
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
            }
            if(climate.equalsIgnoreCase("沙尘暴")){
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
            }
            if(climate.equalsIgnoreCase("特大暴雨")){
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
            }
            if(climate.equalsIgnoreCase("雾")){
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
            }
            if(climate.equalsIgnoreCase("小雪")){
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
            }
            if(climate.equalsIgnoreCase("雨夹雪")){
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
            }
            if(climate.equalsIgnoreCase("阵雪")){
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
            }
            if(climate.equalsIgnoreCase("阵雨")){
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
            }
            if(climate.equalsIgnoreCase("中雪")){
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
            }
            if(climate.equalsIgnoreCase("阴")){
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
            }

            if(todayWeather.getPm25()==null) {
                Log.d("null","null......");
                pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
                return;
            }
            int pm25 = Integer.parseInt(todayWeather.getPm25());
            //*******
            if(pm25 <= 50){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
            }else if(pm25>=51 && pm25<=100){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
            }else if(pm25>=101 && pm25<=150){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
            }else if(pm25>=151 && pm25<=200){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
            }else if(pm25>=201 && pm25<=300){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
            }else{
                pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
            }
        }

        public void updateImage_future(FutureWeather futureWeather) {
            String first_climate = futureWeather.getFirstday_climate_day();
            String next_climate = futureWeather.getNextday_climate_day();
            String third_climate = futureWeather.getThirdday_climate_day();
            String fourth_climate = futureWeather.getFourthday_climate_day();
            String fifth_climate = futureWeather.getFifthday_climate_day();
            String sisth_climate = futureWeather.getSisthday_climate_day();
            if (first_climate.equalsIgnoreCase("暴雪")) {
                firstdayImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
            }
            if (first_climate.equalsIgnoreCase("暴雨")) {
                firstdayImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
            }
            if (first_climate.equalsIgnoreCase("大暴雨")) {
                firstdayImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
            }
            if (first_climate.equalsIgnoreCase("大雪")) {
                firstdayImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
            }
            if (first_climate.equalsIgnoreCase("大雨")) {
                firstdayImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
            }
            if (first_climate.equalsIgnoreCase("中雨")) {
                firstdayImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
            }
            if (first_climate.equalsIgnoreCase("小雨")) {
                firstdayImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
            }
            if (first_climate.equalsIgnoreCase("雷阵雨")) {
                firstdayImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
            }
            if (first_climate.equalsIgnoreCase("雷阵雨冰雹")) {
                firstdayImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
            }
            if (first_climate.equalsIgnoreCase("多云")) {
                firstdayImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
            }
            if (first_climate.equalsIgnoreCase("晴")) {
                firstdayImg.setImageResource(R.drawable.biz_plugin_weather_qing);
            }
            if (first_climate.equalsIgnoreCase("沙尘暴")) {
                firstdayImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
            }
            if (first_climate.equalsIgnoreCase("特大暴雨")) {
                firstdayImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
            }
            if (first_climate.equalsIgnoreCase("雾")) {
                firstdayImg.setImageResource(R.drawable.biz_plugin_weather_wu);
            }
            if (first_climate.equalsIgnoreCase("小雪")) {
                firstdayImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
            }
            if (first_climate.equalsIgnoreCase("雨夹雪")) {
                firstdayImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
            }
            if (first_climate.equalsIgnoreCase("阵雪")) {
                firstdayImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
            }
            if (first_climate.equalsIgnoreCase("阵雨")) {
                firstdayImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
            }
            if (first_climate.equalsIgnoreCase("中雪")) {
                firstdayImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
            }
            if (first_climate.equalsIgnoreCase("阴")) {
                firstdayImg.setImageResource(R.drawable.biz_plugin_weather_yin);
            }
            //----
            if (next_climate.equalsIgnoreCase("暴雪")) {
                nextdayImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
            }
            if (next_climate.equalsIgnoreCase("暴雨")) {
                nextdayImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
            }
            if (next_climate.equalsIgnoreCase("大暴雨")) {
                nextdayImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
            }
            if (next_climate.equalsIgnoreCase("大雪")) {
                nextdayImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
            }
            if (next_climate.equalsIgnoreCase("大雨")) {
                nextdayImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
            }
            if (next_climate.equalsIgnoreCase("中雨")) {
                nextdayImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
            }
            if (next_climate.equalsIgnoreCase("小雨")) {
                nextdayImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
            }
            if (next_climate.equalsIgnoreCase("雷阵雨")) {
                nextdayImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
            }
            if (next_climate.equalsIgnoreCase("雷阵雨冰雹")) {
                nextdayImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
            }
            if (next_climate.equalsIgnoreCase("多云")) {
                nextdayImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
            }
            if (next_climate.equalsIgnoreCase("晴")) {
                nextdayImg.setImageResource(R.drawable.biz_plugin_weather_qing);
            }
            if (next_climate.equalsIgnoreCase("沙尘暴")) {
                nextdayImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
            }
            if (next_climate.equalsIgnoreCase("特大暴雨")) {
                nextdayImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
            }
            if (next_climate.equalsIgnoreCase("雾")) {
                nextdayImg.setImageResource(R.drawable.biz_plugin_weather_wu);
            }
            if (next_climate.equalsIgnoreCase("小雪")) {
                nextdayImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
            }
            if (next_climate.equalsIgnoreCase("雨夹雪")) {
                nextdayImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
            }
            if (next_climate.equalsIgnoreCase("阵雪")) {
                nextdayImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
            }
            if (next_climate.equalsIgnoreCase("阵雨")) {
                nextdayImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
            }
            if (next_climate.equalsIgnoreCase("中雪")) {
                nextdayImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
            }
            if (next_climate.equalsIgnoreCase("阴")) {
                nextdayImg.setImageResource(R.drawable.biz_plugin_weather_yin);
            }
            //-------
            if (third_climate.equalsIgnoreCase("暴雪")) {
                thirddayImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
            }
            if (third_climate.equalsIgnoreCase("暴雨")) {
                thirddayImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
            }
            if (third_climate.equalsIgnoreCase("大暴雨")) {
                thirddayImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
            }
            if (third_climate.equalsIgnoreCase("大雪")) {
                thirddayImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
            }
            if (third_climate.equalsIgnoreCase("大雨")) {
                thirddayImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
            }
            if (third_climate.equalsIgnoreCase("中雨")) {
                thirddayImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
            }
            if (third_climate.equalsIgnoreCase("小雨")) {
                thirddayImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
            }
            if (third_climate.equalsIgnoreCase("雷阵雨")) {
                thirddayImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
            }
            if (third_climate.equalsIgnoreCase("雷阵雨冰雹")) {
                thirddayImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
            }
            if (third_climate.equalsIgnoreCase("多云")) {
                thirddayImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
            }
            if (third_climate.equalsIgnoreCase("晴")) {
                thirddayImg.setImageResource(R.drawable.biz_plugin_weather_qing);
            }
            if (third_climate.equalsIgnoreCase("沙尘暴")) {
                thirddayImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
            }
            if (third_climate.equalsIgnoreCase("特大暴雨")) {
                thirddayImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
            }
            if (third_climate.equalsIgnoreCase("雾")) {
                thirddayImg.setImageResource(R.drawable.biz_plugin_weather_wu);
            }
            if (third_climate.equalsIgnoreCase("小雪")) {
                thirddayImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
            }
            if (third_climate.equalsIgnoreCase("雨夹雪")) {
                thirddayImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
            }
            if (third_climate.equalsIgnoreCase("阵雪")) {
                thirddayImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
            }
            if (third_climate.equalsIgnoreCase("阵雨")) {
                thirddayImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
            }
            if (third_climate.equalsIgnoreCase("中雪")) {
                thirddayImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
            }
            if (third_climate.equalsIgnoreCase("阴")) {
                thirddayImg.setImageResource(R.drawable.biz_plugin_weather_yin);
            }

            //-------
            if (fourth_climate.equalsIgnoreCase("暴雪")) {
                fourthdayImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
            }
            if (fourth_climate.equalsIgnoreCase("暴雨")) {
                fourthdayImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
            }
            if (fourth_climate.equalsIgnoreCase("大暴雨")) {
                fourthdayImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
            }
            if (fourth_climate.equalsIgnoreCase("大雪")) {
                fourthdayImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
            }
            if (fourth_climate.equalsIgnoreCase("大雨")) {
                fourthdayImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
            }
            if (fourth_climate.equalsIgnoreCase("中雨")) {
                fourthdayImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
            }
            if (fourth_climate.equalsIgnoreCase("小雨")) {
                fourthdayImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
            }
            if (fourth_climate.equalsIgnoreCase("雷阵雨")) {
                fourthdayImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
            }
            if (fourth_climate.equalsIgnoreCase("雷阵雨冰雹")) {
                fourthdayImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
            }
            if (fourth_climate.equalsIgnoreCase("多云")) {
                fourthdayImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
            }
            if (fourth_climate.equalsIgnoreCase("晴")) {
                fourthdayImg.setImageResource(R.drawable.biz_plugin_weather_qing);
            }
            if (fourth_climate.equalsIgnoreCase("沙尘暴")) {
                fourthdayImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
            }
            if (fourth_climate.equalsIgnoreCase("特大暴雨")) {
                fourthdayImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
            }
            if (fourth_climate.equalsIgnoreCase("雾")) {
                fourthdayImg.setImageResource(R.drawable.biz_plugin_weather_wu);
            }
            if (fourth_climate.equalsIgnoreCase("小雪")) {
                fourthdayImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
            }
            if (fourth_climate.equalsIgnoreCase("雨夹雪")) {
                fourthdayImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
            }
            if (fourth_climate.equalsIgnoreCase("阵雪")) {
                fourthdayImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
            }
            if (fourth_climate.equalsIgnoreCase("阵雨")) {
                fourthdayImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
            }
            if (fourth_climate.equalsIgnoreCase("中雪")) {
                fourthdayImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
            }
            if (fourth_climate.equalsIgnoreCase("阴")) {
                fourthdayImg.setImageResource(R.drawable.biz_plugin_weather_yin);
            }

            //-------
            if (fifth_climate.equalsIgnoreCase("暴雪")) {
                fifthdayImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
            }
            if (fifth_climate.equalsIgnoreCase("暴雨")) {
                fifthdayImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
            }
            if (fifth_climate.equalsIgnoreCase("大暴雨")) {
                fifthdayImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
            }
            if (fifth_climate.equalsIgnoreCase("大雪")) {
                fifthdayImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
            }
            if (fifth_climate.equalsIgnoreCase("大雨")) {
                fifthdayImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
            }
            if (fifth_climate.equalsIgnoreCase("中雨")) {
                fifthdayImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
            }
            if (fifth_climate.equalsIgnoreCase("小雨")) {
                fifthdayImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
            }
            if (fifth_climate.equalsIgnoreCase("雷阵雨")) {
                fifthdayImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
            }
            if (fifth_climate.equalsIgnoreCase("雷阵雨冰雹")) {
                fifthdayImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
            }
            if (fifth_climate.equalsIgnoreCase("多云")) {
                fifthdayImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
            }
            if (fifth_climate.equalsIgnoreCase("晴")) {
                fifthdayImg.setImageResource(R.drawable.biz_plugin_weather_qing);
            }
            if (fifth_climate.equalsIgnoreCase("沙尘暴")) {
                fifthdayImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
            }
            if (fifth_climate.equalsIgnoreCase("特大暴雨")) {
                fifthdayImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
            }
            if (fifth_climate.equalsIgnoreCase("雾")) {
                fifthdayImg.setImageResource(R.drawable.biz_plugin_weather_wu);
            }
            if (fifth_climate.equalsIgnoreCase("小雪")) {
                fifthdayImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
            }
            if (fifth_climate.equalsIgnoreCase("雨夹雪")) {
                fifthdayImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
            }
            if (fifth_climate.equalsIgnoreCase("阵雪")) {
                fifthdayImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
            }
            if (fifth_climate.equalsIgnoreCase("阵雨")) {
                fifthdayImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
            }
            if (fifth_climate.equalsIgnoreCase("中雪")) {
                fifthdayImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
            }
            if (fifth_climate.equalsIgnoreCase("阴")) {
                fifthdayImg.setImageResource(R.drawable.biz_plugin_weather_yin);
            }
            //----
            if (sisth_climate.equalsIgnoreCase("暴雪")) {
                sisthdayImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
            }
            if (sisth_climate.equalsIgnoreCase("暴雨")) {
                sisthdayImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
            }
            if (sisth_climate.equalsIgnoreCase("大暴雨")) {
                sisthdayImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
            }
            if (sisth_climate.equalsIgnoreCase("大雪")) {
                sisthdayImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
            }
            if (sisth_climate.equalsIgnoreCase("大雨")) {
                sisthdayImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
            }
            if (sisth_climate.equalsIgnoreCase("中雨")) {
                sisthdayImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
            }
            if (sisth_climate.equalsIgnoreCase("小雨")) {
                sisthdayImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
            }
            if (sisth_climate.equalsIgnoreCase("雷阵雨")) {
                sisthdayImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
            }
            if (sisth_climate.equalsIgnoreCase("雷阵雨冰雹")) {
                sisthdayImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
            }
            if (sisth_climate.equalsIgnoreCase("多云")) {
                sisthdayImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
            }
            if (sisth_climate.equalsIgnoreCase("晴")) {
                sisthdayImg.setImageResource(R.drawable.biz_plugin_weather_qing);
            }
            if (sisth_climate.equalsIgnoreCase("沙尘暴")) {
                sisthdayImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
            }
            if (sisth_climate.equalsIgnoreCase("特大暴雨")) {
                sisthdayImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
            }
            if (sisth_climate.equalsIgnoreCase("雾")) {
                sisthdayImg.setImageResource(R.drawable.biz_plugin_weather_wu);
            }
            if (sisth_climate.equalsIgnoreCase("小雪")) {
                sisthdayImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
            }
            if (sisth_climate.equalsIgnoreCase("雨夹雪")) {
                sisthdayImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
            }
            if (sisth_climate.equalsIgnoreCase("阵雪")) {
                sisthdayImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
            }
            if (sisth_climate.equalsIgnoreCase("阵雨")) {
                sisthdayImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
            }
            if (sisth_climate.equalsIgnoreCase("中雪")) {
                sisthdayImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
            }
            if (sisth_climate.equalsIgnoreCase("阴")) {
                sisthdayImg.setImageResource(R.drawable.biz_plugin_weather_yin);
            }
        }
}


