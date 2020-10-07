package edu.temple.contacttracer;

import android.util.Log;

import java.time.LocalDate;
import java.util.ArrayList;

import static java.time.temporal.ChronoUnit.DAYS;

public class Token_Container {
    ArrayList<Token> tokenArrayList;

    public Token_Container() {
        tokenArrayList = new ArrayList<Token>();
    }

    public void add(Token token) {
        tokenArrayList.add(token);
    }

    public void remove(Token token) {
        tokenArrayList.remove(token);
    }

    public void clear() {
        tokenArrayList.clear();
    }

    public void remove_expire(Token token) {
        LocalDate today = LocalDate.now();
        if (DAYS.between(token.getDate(), today) > 14) {
            remove(token);
        }
    }

    public void expire_days_checker() {
        for (Token token : tokenArrayList) {
            remove_expire(token);
        }
    }

    public String print() {
        String print = "";
        if (tokenArrayList.isEmpty()) {
            Log.d("ERROR", "EMPTY");
            print = "null";
        } else {
            for (Token token : tokenArrayList) {
                print += token.toString();
            }
        }
        return print;
    }

}
