package com.example.findymap.findymap;

import com.baidu.mapapi.map.Overlay;

import java.io.Serializable;

/**
 * Created by apple on 2016/9/21.
 */
public class persons implements Serializable {
    private static final long serialVersionUID = -7060210544600464481L;
    private String Num;
    private String Names;
    private double LatiT;
    private double LongT;

    public persons(String Num,String Names,double LatiT,double LongT){
        this.Num   =   Num ;
        this.Names   =   Names ;
        this.LatiT=LatiT;
        this.LongT=LongT;
    }
    public String getNum(){
        return Num ;
    }
    public void setNum(String Num){
        this.Num   =   Num ;
    }


    public double getLatiT(){
        return LatiT ;
    }
    public void setLatiT(double Lati){
        this.LatiT   =   Lati ;
    }

    public double getLongT(){
        return LongT ;
    }
    public void setLongT(double LongT){
        this.LongT   =   LongT ;
    }

    public String getNames(){
        return Names ;
    }
    public void setNames(String Name){
        this.Names   =  Name ;
    }

}

