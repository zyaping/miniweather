package com.example.shirley.bean;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.shirley.miniweather.MainActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by shirley on 2017/10/13.
 */

public class TodayWeather {

    private SharedPreferences sp;

    private String city;
    private String updatetime;
    private String wendu;
    private String shidu;
    private String pm25;
    private String quality;
    private String fengxiang;
    private String fengli;
    private String date;
    private String high;
    private String low;
    private String type;

    public String getCity() {return city;}
    public String getUpdatetime() {
        return updatetime;
    }
    public String getWendu() {
        return  wendu;
    }
    public String getShidu() {
        return  shidu;
    }
    public String getPm25() {
        return  pm25;
    }
    public String getQuality() {
        return  quality;
    }
    public String getDate() {
        return  date;
    }
    public String getHigh() {
        return  high;
    }
    public String getLow() {return  low;}
    public String getType() {
        return type;
    }
    public String getFengli() {
        return  fengli;
    }
    public String getFengxiang() {
        return  fengxiang;
    }





    public void setDate(String date) {
        this.date = date;
    }
    public void setHigh(String high) {
        this.high = high;
    }
    public void setLow(String low) {
        this.low = low;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }
    public void setShidu(String shidu) {
        this.shidu = shidu;
    }
    public void setWendu(String wendu) {
        this.wendu = wendu;
    }
    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }
    public void setQuality(String quality) {
        this.quality = quality;
    }
    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }
    public void setFengli(String fengli) {
        this.fengli = fengli;
    }




    @Override
    public String toString(){
        return "TodayWeather{" +
                "city='" + city + '\'' +
                ", updatetime='" + updatetime + '\'' +
                ", wendu='" + wendu + '\'' +
                ", shidu='" + shidu + '\'' +
                ", pm25='" + pm25 + '\'' +
                ", quality='" + quality + '\'' +
                ", fengxiang='" + fengxiang + '\'' +
                ", fengli='" + fengli + '\'' +
                ", date='" + date + '\'' +
                ", high='" + high + '\'' +
                ", low='" + low + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
/*

    public void saveData (Activity activity) {
        sp = activity.getSharedPreferences("config", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("city",city);
        editor.putString("updatetime",updatetime);
        editor.putString("wendu",wendu);
        editor.putString("shidu",shidu);
        editor.putString("pm25",pm25);
        editor.putString("quality",quality);
        editor.putString("fengxiang",fengxiang);
        editor.putString("fengli",fengli);
        editor.putString("date",date);
        editor.putString("high",high);
        editor.putString("low",low);
        editor.putString("type",type);
        editor.commit();
        Log.d("ha" ,"save");
    }

*/
}












