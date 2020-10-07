package edu.temple.contacttracer;


import java.time.LocalDate;


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
        return "UUID : " + this.UUID + "\n" + "DATE : " + getDate() + "\n" + "La : " + this.latitude + "\n" + "Lo : " + this.longtitude + "\n" + "sedentary_begin : " + this.sedentary_begin + "\n" + "sedentary_end : " + this.sedentary_end + "\n\n\n";
    }


}
