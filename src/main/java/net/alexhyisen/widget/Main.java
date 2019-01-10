package net.alexhyisen.widget;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void genIn() throws IOException {
        var path = Paths.get(".", "in");

        Random rand = new Random(17);
        int min = Math.toIntExact(LocalDate.of(1840, 1, 1).toEpochDay());
        int max = Math.toIntExact(LocalDate.of(2140, 1, 1).toEpochDay());

        final int NUM = 10;
        var one = new long[NUM];
        var two = new int[NUM][1000];
        for (int i = 0; i < one.length; i++) {
            one[i] = min + rand.nextInt(max - min);
            for (int j = 0; j < two[i].length; j++) {
                two[i][j] = rand.nextInt(40000);
            }
        }

        final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy M d ");
        final List<String> collect = IntStream.range(0, NUM).parallel()
                .boxed()
                .flatMap(v -> {
                    var date = LocalDate.ofEpochDay(one[v]).format(pattern);
                    return IntStream.range(0, 1000).mapToObj(i -> date + two[v][i]);
                })
                .collect(Collectors.toList());
        Files.write(path, collect, StandardOpenOption.CREATE_NEW);
    }

    public static void parse() throws IOException {
        final List<String> collect = Files.lines(Paths.get(".", "in"))
                .parallel()
                .map(v -> {
                    final var limb = v.split(" ");
                    var date = new Date(
                            Integer.valueOf(limb[0]),
                            Integer.valueOf(limb[1]),
                            Integer.valueOf(limb[2]));
                    date.elapse(Integer.valueOf(limb[3]));
                    return date.format();
                })
                .collect(Collectors.toList());
        Files.write(Paths.get(".", "out"), collect, StandardOpenOption.CREATE_NEW);
    }

    public static void main(String[] args) throws IOException {
//        genIn();
        parse();
    }
}
