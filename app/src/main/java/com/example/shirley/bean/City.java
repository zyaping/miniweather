package com.example.shirley.bean;

/**
 * Created by shirley on 2017/11/1.
 */

public class City {
    private String province;
    private String city;
    private String number;
    private String firstPY;
    private String allPY;
    private String allFristPY;

    public City(String province, String city, String number, String firstPY, String allPY, String allFristPY) {
        this.province = province;
        this.city = city;
        this.number = number;
        this.firstPY = firstPY;
        this.allPY = allPY;
        this.allFristPY = allFristPY;
    }
    public String getProvince() {return province;}
    public String getCity() {return city;}
    public String getNumber(){return number;}
    public String getFirstPY(){return firstPY;}
    public String getAllPY(){return getAllPY();}
    public String getAllFristPY(){return getAllFristPY();}
}
