package edu.temple.contacttracer;

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

    public void remove() {
        tokenArrayList.remove(0);
    }

    public void clear() {
        tokenArrayList.clear();
    }

    public void remove_expire(Token token) {
        LocalDate today = LocalDate.now();
        if (DAYS.between(token.getDate(), today) > 14) {
            remove();
        }
    }

    public void expire_days_checker() {
        for (Token token : tokenArrayList) {
            remove_expire(token);
        }
    }


}
