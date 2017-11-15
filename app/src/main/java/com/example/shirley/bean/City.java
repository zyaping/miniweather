package com.example.shirley.bean;

import java.io.Serializable;

/**
 * Created by shirley on 2017/11/1.
 */

public class City implements Serializable {
    private String province;
    private String city;
    private String number;
    private String firstPY;
    private String allPY;
    private String allFristPY;

    public City()
    {

    }

    public City( String province, String city, String number, String
            firstPY, String allPY, String allFristPY)     //增加一个构造函数City
    {
        this. province = province;
        this. city = city;
        this. number = number;
        this. firstPY = firstPY;
        this. allPY = allPY;
        this. allFristPY = allFristPY;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFirstPY() {
        return firstPY;
    }

    public void setFirstPY(String firstPY) {
        this.firstPY = firstPY;
    }

    public String getAllPY() {
        return allPY;
    }

    public void setAllPY(String allPY) {
        this.allPY = allPY;
    }

    public String getAllFristPY() {
        return allFristPY;
    }

    public void setAllFristPY(String allFristPY) {
        this.allFristPY = allFristPY;
    }
}
