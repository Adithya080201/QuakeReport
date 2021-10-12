package com.example.quakereport;

public class Earthquake {

    private Double mMagValue;

    private String mCityName;

    private Long mTimeInMilliSeconds;

    private String mEarthQuakeURL;

    public Earthquake(Double magValue, String cityName, Long timeInMilliSeconds, String earthQuakeURL){
        mMagValue = magValue;
        mCityName = cityName;
        mTimeInMilliSeconds = timeInMilliSeconds;
        mEarthQuakeURL = earthQuakeURL;
    }

    public Double getMagValue(){
        return mMagValue;
    }

    public String  getCityName(){
        return mCityName;
    }

    public Long getTimeInMilliSeconds(){
        return mTimeInMilliSeconds;
    }

    public String getEarthQuakeURL(){
        return mEarthQuakeURL;
    }
}
