package edu.temple.contacttracer;

import android.location.Location;
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

    //check if you expose to virus;
    public boolean matching(String id) {
        ArrayList<Token> currentArrayList = new ArrayList<Token>();
        double between_distance = 10;
        for (Token token : Other_tokenArrayList) {
            if (token.uuid.toString().equals(id)) {
                currentArrayList.add(token);
            }
        }
        if (!currentArrayList.isEmpty()) {
            for (Token mytoken : My_tokenArrayList) {
                for (Token histoken : currentArrayList) {
                    if (Math.round(distance_calculator(mytoken.latitude, histoken.latitude, mytoken.longtitude, histoken.longtitude)) < between_distance) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    //Use the formular online
    public float distance_calculator(double latitudeA, double latitudeB, double longtitudeA, double longtitudeB) {
        Location A = new Location("A");
        Location B = new Location("B");

        A.setLatitude(latitudeA);
        B.setLatitude(latitudeB);
        A.setLongitude(longtitudeA);
        B.setLongitude(longtitudeB);
        float distance = A.distanceTo(B);

        return distance;
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
