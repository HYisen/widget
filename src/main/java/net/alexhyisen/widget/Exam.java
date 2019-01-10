package net.alexhyisen.widget;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Exam {
    public static void main(String[] args) throws IOException {
        var pass = 0;
        var fail = 0;
        var in = Files.readAllLines(Paths.get(".", "in"));
        var out = Files.readAllLines(Paths.get(".", "out"));
        for (int k = 0; k < in.size(); k++) {

        }
    }
}
