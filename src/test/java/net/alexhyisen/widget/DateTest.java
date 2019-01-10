package net.alexhyisen.widget;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateTest {
    private static void single(LocalDate from, int days) {
        try {
            var date = new Date(from.getYear(), from.getMonthValue(), from.getDayOfMonth());
            date.elapse(days);
            assertEquals(from.plusDays(days).toString(), date.toString());
        } catch (AssertionFailedError e) {
            System.out.println(from);
            System.out.println(days);
            throw e;
        }
    }

    @Test
    void shouldDetect() {
        assertThrows(RuntimeException.class, () -> {
            new Date(1900, 2, 29);
        });
        assertThrows(RuntimeException.class, () -> {
            new Date(1900, 29, 1);
        });

    }


    @Test
    void shouldSimple() {
        single(LocalDate.of(1945, 8, 15), 1);
    }

    @Test
    void shouldImpossible() {
        single(LocalDate.of(2000, 2, 29), -365 * 2);
        single(LocalDate.of(2020, 2, 29), 14);
    }

    @Test
    void shouldImpossibleZero() {
        single(LocalDate.of(2000, 2, 29), 0);
    }

    @Test
    void shouldMultiple() {
        single(LocalDate.of(1945, 8, 15), 365);
    }

    @Test
    void shouldMore() {
        single(LocalDate.of(1947, 8, 15), 365 * 2);
    }

    @Test
    void shouldMinusMore() {
        single(LocalDate.of(1948, 8, 15), -365 * 2);
    }

    @Test
    void shouldMinus() {
        single(LocalDate.of(1945, 8, 15), -1);
    }

    @Test
    void shouldNextMonth() {
        single(LocalDate.of(2019, 8, 31), 1);
    }

    @Test
    void shouldPrevMonth() {
        single(LocalDate.of(2019, 12, 1), -1);
    }

    @Test
    void shouldNextYear() {
        single(LocalDate.of(2050, 12, 31), 1);
    }

    @Test
    void shouldPrevYear() {
        single(LocalDate.of(2077, 1, 1), -1);
    }

    @Test
    void shouldLeap() {
        single(LocalDate.of(2008, 2, 28), 1);
    }

    @Test
    void shouldNotLeap() {
        single(LocalDate.of(2007, 2, 28), 1);
    }

    @Test
    void shouldMoreLeap() {
        single(LocalDate.of(2008, 2, 28), 365);
    }

    @Test
    void random() {
        Random rand = new Random(17);
        int min = Math.toIntExact(LocalDate.of(1840, 1, 1).toEpochDay());
        int max = Math.toIntExact(LocalDate.of(2140, 1, 1).toEpochDay());

        final int NUM = 10;
        var one = new long[NUM];
        var two = new int[NUM][1000];
        for (int i = 0; i < one.length; i++) {
            one[i] = min + rand.nextInt(max - min);
            for (int j = 0; j < two[i].length; j++) {
                two[i][j] = rand.nextInt(80000) - 40000;
            }
        }

        IntStream.range(0, NUM).parallel().forEach(i -> {
            LocalDate from = LocalDate.ofEpochDay(one[i]);
            Arrays.stream(two[i]).forEach(v -> single(from, v));
//            System.out.println(i+" completed");
        });
    }
}