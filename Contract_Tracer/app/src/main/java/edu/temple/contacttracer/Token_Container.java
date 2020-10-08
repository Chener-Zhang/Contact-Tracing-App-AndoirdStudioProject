package edu.temple.contacttracer;

import android.util.Log;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;

public class Token_Container {
    ArrayList<Token> My_tokenArrayList;
    ArrayList<Token> Other_tokenArrayList;


    public Token_Container() {
        My_tokenArrayList = new ArrayList<Token>();
        Other_tokenArrayList = new ArrayList<Token>();
    }

    public void discard_repeate() {

        for (Token my_token : My_tokenArrayList) {
            for (Token other_token : Other_tokenArrayList) {
                if (String.valueOf(my_token.uuid).equals(String.valueOf(other_token.uuid))) {
                    Other_tokenArrayList.remove(other_token);
                }
            }
        }
    }

    public Token get_my_token(UUID my_uuid) {
        Token return_value = null;
        for (Token token : My_tokenArrayList) {
            if (token.uuid == my_uuid) {
                return_value = token;
            } else {
                return_value = null;
            }
        }
        return return_value;
    }

    // add
    public void mine_add(Token token) {
        My_tokenArrayList.add(token);
    }

    public void others_add(Token token) {
        Other_tokenArrayList.add(token);
    }


    //remove
    public void mine_remove(Token token) {
        My_tokenArrayList.remove(token);
    }

    public void other_remove(Token token) {
        Other_tokenArrayList.remove(token);
    }


    //clear
    public void clear_mine() {
        My_tokenArrayList.clear();
    }

    public void clear_others() {
        Other_tokenArrayList.clear();
    }

    //remove expire
    public void mine_remove_expire(Token token) {
        LocalDate today = LocalDate.now();
        if (DAYS.between(token.getDate(), today) > 14) {
            mine_remove(token);
        }
    }

    public void others_remove_expire(Token token) {
        LocalDate today = LocalDate.now();
        if (DAYS.between(token.getDate(), today) > 14) {
            mine_remove(token);
        }
    }

    //both cheker
    public void expire_days_checker() {
        for (Token token : My_tokenArrayList) {
            mine_remove_expire(token);
        }

        for (Token token : Other_tokenArrayList) {
            others_remove_expire(token);
        }

    }

    //print
    public String print_mine_tokens() {
        String print = "";
        if (My_tokenArrayList.isEmpty()) {
            Log.d("ERROR", "EMPTY");
            print = "null";
        } else {
            for (Token token : My_tokenArrayList) {
                print += token.toString();
            }
        }
        return print;
    }

    public String print_others_tokens() {
        String print = "";
        if (Other_tokenArrayList.isEmpty()) {
            Log.d("ERROR", "EMPTY");
            print = "null";
        } else {
            for (Token token : Other_tokenArrayList) {
                print += token.toString();
            }
        }
        return print;
    }


}
