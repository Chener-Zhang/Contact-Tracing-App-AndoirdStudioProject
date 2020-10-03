package com.example.contacttracer;


import java.time.LocalDateTime;


public class Token {
    public int UUID;
    public LocalDateTime date;

    public static int min = 0;
    public static int max = 1000;

    public Token() {
        this.UUID = Token_generator();
        this.date = getDate();
        System.out.println("UUID : " + this.UUID + "\n" + "DATE : " + this.date + " \n");
    }

    public int Token_generator() {
        int random_int = (int) (Math.random() * (max - min + 1) + min);
        return random_int;
    }

    public LocalDateTime getDate() {
        LocalDateTime date = java.time.LocalDateTime.now();
        return date;
    }

    public String toString() {
        return "UUIS : " + this.UUID + "was generated by :" + this.date + "\n\n";
    }


}
