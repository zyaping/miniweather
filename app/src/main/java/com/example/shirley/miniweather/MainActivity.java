package com.example.shirley.miniweather;

/**
 * Created by shirley on 2017/9/28.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import com.example.shirley.app.MyApplication;
import com.example.shirley.bean.City;
import com.example.shirley.bean.FutureWeather;
import com.example.shirley.bean.TodayWeather;
import com.example.shirley.miniweather.NetUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;






public class MainActivity extends Activity implements View.OnClickListener ,ViewPager.OnPageChangeListener{
    private static final int UPDATE_TODAY_WEATHER = 1;
    private static final int UPDATE_FUTURE_WEATHER = 2;



    // private SharedPreferences sp;

    private ImageView mUpdateBtn;
    private ImageView mCitySelect;




    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv,
            pmQualityTv, temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;
    private ProgressBar pbar;


    private ViewpagerAdapter vpAdapter;
    private List<View> views;

    private ViewPager vp;
    private ImageView[] dots;
    private List<City> mCityList;
    private String cityCode;


    //更新图标旋转
    Animation operatingAnim = null;
    //LinearInterpolator为匀速效果
    LinearInterpolator lin = null;
    TodayWeather todayWeather;
    FutureWeather futureWeather;


    private ImageView firstdayImg,nextdayImg, thirddayImg, fourthdayImg, fifthdayImg, sisthdayImg;
    private TextView firstdayWeek,nextdayWeek, thirddayWeek, fourthdayWeek,wendu, fifthdayWeek, sisthdayWeek;
    private TextView firstdayTemp ,nextdayTemp , thirddayTemp, fourthdayTemp, fifthdayTemp, sisthdayTemp;
    private TextView firstdayWind ,nextdayWind , thirddayWind, fourthdayWind, fifthdayWind,sisthdayWind;
    private TextView firstdayClimate ,nextdayClimate , thirddayClimate, fourthdayClimate,fifthdayClimate,sisthdayClimate;



    private int[] ids = {R.id.iv1, R.id.iv2};



    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break; }
        }
    };








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info); //布局设置在主界面上

        //sp = getSharedPreferences("config", MODE_PRIVATE);

        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn); //更新图标
        mUpdateBtn.setOnClickListener(this); //可点击更新图标

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) { //检查网络连接状况
            Log.d("myWeather", "网络OK");
            Toast.makeText(MainActivity.this,"网络OK!", Toast.LENGTH_LONG).show(); //如果网络连接上，弹出消息"网络OK!"
        }
        else
        {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(MainActivity.this,"网络挂了!", Toast.LENGTH_LONG).show();//如果网络没有连接上，弹出消息"网络挂了!"
        }

        mCitySelect = (ImageView) findViewById(R.id.title_city_manager); //城市管理图片来源
        mCitySelect.setOnClickListener(this); //图片可点击



        //mCityList = ((MyApplication) getApplication()).getCityList();




        startService(new Intent(getBaseContext(), UpdateService.class));


        initViews(); //初始化未来六天天气
        initDots();
        initView(); //初始化主界面上的布局信息

       // MyApplication myApplication = (MyApplication) getApplication();
        //Iterator<City> it = myApplication.getCityList().iterator();



    }








    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_city_manager){
            Intent i = new Intent(this,SelectCity.class);
            // startActivity(i);
            startActivityForResult(i,1);  //修改更新按钮的单击事件处理程序
        }

        if (view.getId() == R.id.title_update_btn){
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code","101010100");
            String cityCode1 = sharedPreferences.getString("main_city_code","101160101");
            Log.d("myWeather",cityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);
            }
            else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了!",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //从激活的SelectCity活动接收返回数据
        if (requestCode == 1 && requestCode == RESULT_OK) {
            String newCitycode= data.getStringExtra("citycode");


            //SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            //SharedPreferences.Editor editor = sharedPreferences.edit(); //SharedPreference中写入数据需要使用Editor
            //editor.putString("main_city_code", newCitycode); //键值对newCityCode——>main_city_code（值）
            //editor.commit();            //点击返回后，使用SharedPreferences保存当前数据


            Log.d("myWeather", "选择的城市代码位"+newCitycode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather","网络OK");
                queryWeatherCode(newCitycode);
            }else{
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }

    /** *
     * @param cityCode
     */
    private void queryWeatherCode(String cityCode) { //通过城市ID请求天气数据
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con=null;
                TodayWeather todayWeather = null;
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str=reader.readLine()) != null){
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr=response.toString();
                    Log.d("myWeather", responseStr);

                    todayWeather = parseXML(responseStr);
                    if (todayWeather != null) {
                        Log.d("myWeather", todayWeather.toString());
                        Message msg =new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj=todayWeather;
                        mHandler.sendMessage(msg);

                    }


                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(con != null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    void initView() { //初始化主界面上的布局
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg =(ImageView) findViewById(R.id.weather_img);



/*
        String city = sp.getString("city", "N/A");
        String updatetime = sp.getString("updatetime","N/A");
        String wendu = sp.getString("wendu","N/A");
        String shidu = sp.getString("shidu","N/A");
        String pm25 = sp.getString("pm25","N/A");
        String quality = sp.getString("quality","N/A");
        String fengxiang = sp.getString("fengxiang","N/A");
        String fengli = sp.getString("fengli","N/A");
        String date = sp.getString("date","N/A");
        String high = sp.getString("high","N/A");
        String low = sp.getString("low","N/A");
        String type = sp.getString("type","N/A");
        Log.d("hazyp", city);



        city_name_Tv.setText(city+"天气");
        cityTv.setText(type);
        timeTv.setText(updatetime+ "发布");
        humidityTv.setText("湿度:"+shidu);
        pmDataTv.setText(pm25);
        pmQualityTv.setText(quality);
        weekTv.setText(date);
        temperatureTv.setText(low+"~"+high);
        climateTv.setText(fengxiang);
        windTv.setText("风力:"+fengli);

*/


        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");




    }


    private TodayWeather parseXML(String xmldata){ //解析网络
        TodayWeather todayWeather = null; //天气信息初始为空
        int fengxiangCount=0;
        int fengliCount =0;
        int dateCount=0;
        int highCount =0;
        int lowCount=0;
        int typeCount =0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    // 判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    // 判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                        //todayWeather.saveData(this);
                        break;
                    // 判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                // 进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;

    }
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


    void updateTodayWeather(TodayWeather todayWeather){ //更新天气情况
        String weatherType = todayWeather.getType();
        int pm25State = Integer.parseInt(todayWeather.getPm25());

        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+ "发布");
        humidityTv.setText("湿度:"+todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getLow()+"~"+todayWeather.getHigh());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:"+todayWeather.getFengli());
        Toast.makeText(MainActivity.this,"更新成功!",Toast.LENGTH_SHORT).show();





        switch (weatherType){ //更新天气图片
                case "晴": weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
                case "暴雪": weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
                case "暴雨": weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
                case "大暴雨": weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
                case "大雪": weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
                case "大雨": weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
                case "多云": weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
                case "雷阵雨": weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
                case "雷阵雨冰雹": weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
                case "沙尘暴": weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
                case "特大暴雨": weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
                case "雾": weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
                case "小雪": weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
                case "小雨": weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
                case "阴": weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
                case "雨夹雪": weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
                case "阵雪": weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
                case "阵雨": weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
                case "中雪": weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
                case "中雨": weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
                default:

                }

                if (pm25State <= 50){ //更新天气PM图片
                    pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);

                }
                else if (pm25State >= 51 && pm25State <=100){
                    pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);

                }
                else if (pm25State >= 101 && pm25State <=150){
                    pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);

                }

                else if (pm25State >= 201 && pm25State <=300){
                    pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
                }
                else
                    pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
    }

         /*

        public void saveData (Activity activity) {
            //sp = activity.getSharedPreferences("config", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sp.edit();
            TodayWeather td = new TodayWeather();
            editor.putString("city", td.getCity());
            editor.putString("updatetime",td.getUpdatetime());
            editor.putString("wendu",td .getWendu());
            editor.putString("shidu",td.getShidu());
            editor.putString("pm25",td.getPm25());
            editor.putString("quality",td.getQuality());
            editor.putString("fengxiang",td.getFengxiang());
            editor.putString("fengli",td.getFengli());
            editor.putString("date",td.getDate());
            editor.putString("high",td.getHigh());
            editor.putString("low",td.getLow());
            editor.putString("type",td.getType());
            editor.commit();
            Log.d("ha" ,"save");
        }
        */

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
        vpAdapter = new ViewpagerAdapter(views,this);
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

}



