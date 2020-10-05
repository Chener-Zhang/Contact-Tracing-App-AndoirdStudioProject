package edu.temple.contacttracer;


import android.util.Log;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class Token {
    public String UUID;
    public LocalDate date;
    public double latitude;
    public double longtitude;
    public long sedentary_begin;
    public long sedentary_end;


    public Token(double latitude, double longtitude, long sedentary_begin, long sedentary_end) {
        this.UUID = Token_generator();
        this.date = getDate();
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.sedentary_begin = sedentary_begin;
        this.sedentary_end = sedentary_end;
    }

    public String Token_generator() {
        return CONSTANT.MY_UUID;
    }

    public LocalDate getDate() {
        return LocalDate.now();
    }

    public String toString() {
        return this.UUID + "\n" + getDate() + "\n" + this.latitude + "\n" + this.longtitude + "\n" + this.sedentary_begin + "\n" + this.sedentary_end;
    }


}
