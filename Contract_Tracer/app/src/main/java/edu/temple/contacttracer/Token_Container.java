package edu.temple.contacttracer;

import java.util.ArrayList;

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
}
