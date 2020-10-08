package edu.temple.contacttracer;


import java.time.LocalDate;
import java.util.UUID;

public class Token {

    public UUID uuid;
    public LocalDate date;
    public double latitude;
    public double longtitude;
    public long sedentary_begin;
    public long sedentary_end;


    public Token(UUID uuid, double latitude, double longtitude, long sedentary_begin, long sedentary_end) {
        if (uuid == null) {
            this.uuid = UUID.randomUUID();
        } else {
            this.uuid = uuid;
        }

        this.latitude = latitude;
        this.longtitude = longtitude;
        this.sedentary_begin = sedentary_begin;
        this.sedentary_end = sedentary_end;
        this.date = getDate();

    }


    public LocalDate getDate() {
        return LocalDate.now();
    }

    public String toString() {
        return "UUID : " + this.uuid + "\n" + "DATE : " + getDate() + "\n" + "La : " + this.latitude + "\n" + "Lo : " + this.longtitude + "\n" + "sedentary_begin : " + this.sedentary_begin + "\n" + "sedentary_end : " + this.sedentary_end + "\n\n\n";
    }


}
