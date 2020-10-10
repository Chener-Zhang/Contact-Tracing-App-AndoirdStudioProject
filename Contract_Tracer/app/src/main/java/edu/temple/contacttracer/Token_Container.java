package edu.temple.contacttracer;

import android.util.Log;

import org.json.JSONArray;

import java.time.LocalDate;
import java.util.ArrayList;

import static java.time.temporal.ChronoUnit.DAYS;

public class Token_Container {
    ArrayList<Token> My_tokenArrayList = null;
    ArrayList<Token> Other_tokenArrayList = null;
    JSONArray my_jsonArray = null;


    public Token_Container() {
        My_tokenArrayList = new ArrayList<Token>();
        Other_tokenArrayList = new ArrayList<Token>();
        my_jsonArray = new JSONArray();
    }

    public JSONArray get_all_my_uuid() {
        for (Token token : My_tokenArrayList) {
            my_jsonArray.put(token.uuid);
        }

        return my_jsonArray;
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
