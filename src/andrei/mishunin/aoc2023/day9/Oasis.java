package andrei.mishunin.aoc2023.day9;

import andrei.mishunin.aoc2023.tools.InputReader;

import java.util.Arrays;
import java.util.Collections;

public class Oasis {
    public static long getSumExtrapolatedValues(String file, boolean reverse) {
        var valuesHistoryStream = InputReader.readAllLines(file)
                .stream().filter(s -> !s.isBlank())
                .map(s -> s.split(" "))
                .map(Arrays::asList);
        if (reverse) {
            valuesHistoryStream = valuesHistoryStream.peek(Collections::reverse);
        }
        return valuesHistoryStream
                .map(a -> a.stream().mapToInt(Integer::parseInt).toArray())
                .peek(Oasis::prepareForPredict)
                .mapToInt(a -> Arrays.stream(a).sum())
                .sum();
    }

    private static void prepareForPredict(int[] values) {
        int n = values.length - 1;
        for (int c = 0; c < n - 1; c++) {
            for (int i = 0; i < n - c; i++) {
                values[i] = values[i + 1] - values[i];
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(Oasis.getSumExtrapolatedValues("day9/test.txt", false));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(Oasis.getSumExtrapolatedValues("day9/input.txt", false));

        System.out.println("== TEST 2 ==");
        System.out.println(Oasis.getSumExtrapolatedValues("day9/test.txt", true));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(Oasis.getSumExtrapolatedValues("day9/input.txt", true));
    }
}
