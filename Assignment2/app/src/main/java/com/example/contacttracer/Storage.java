package com.example.contacttracer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Storage {

    public String FILENAME = "store.txt";
    public String PATH = "src/main/java/com/example/contacttracer/store.txt";

    public Storage() {

    }

    public void write_file(String s) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILENAME));
        bufferedWriter.write(s);
        bufferedWriter.close();
    }


    public void append_message(String filename, String message) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename, true));
        bufferedWriter.write(message);
        bufferedWriter.close();

    }

    public boolean check_file(String directory) {
        File file = new File(directory);
        return file.exists();
    }
}
