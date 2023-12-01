package andrei.mishunin.aoc2023.day1;

import andrei.mishunin.aoc2023.tools.InputReader;

import java.util.function.ToIntFunction;

public class CalibrationSearcher {
    private static final String[][] NUMBERS = new String[][]{
            {"__", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"},
            {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"}
    };

    public static int search(String file, ToIntFunction<String> searcher) {
        var input = InputReader.readAllLines(file);
        return input.stream().mapToInt(searcher).sum();
    }

    private static int searcher1(String line) {
        if (line.isBlank()) {
            return 0;
        }

        int minI = line.length();
        int maxI = -1;
        for (char i = '0'; i <= '9'; i++) {
            int first = line.indexOf(i);
            if (first >= 0 && first < minI) {
                minI = first;
            }
            int last = line.lastIndexOf(i);
            if (last >= 0 && last > maxI) {
                maxI = last;
            }
        }
        return (line.charAt(minI) - '0') * 10 + line.charAt(maxI) - '0';
    }


    private static int searcher2(String line) {
        if (line.isBlank()) {
            return 0;
        }

        int firstNumber = 0;
        int minI = line.length();
        int lastNumber = 0;
        int maxI = -1;

        for (String[] numbers : NUMBERS) {
            for (int j = 0; j < numbers.length; j++) {
                String number = numbers[j];
                int first = line.indexOf(number);
                int last = line.lastIndexOf(number);
                if (first >= 0) {
                    if (first < minI) {
                        firstNumber = j;
                        minI = first;
                    }
                    if (last > maxI) {
                        lastNumber = j;
                        maxI = last;
                    }
                }
            }
        }
        return firstNumber * 10 + lastNumber;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(search("day1/test.txt", CalibrationSearcher::searcher1));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(search("day1/input.txt", CalibrationSearcher::searcher1));
        System.out.println("== TEST 2 ==");
        System.out.println(search("day1/test2.txt", CalibrationSearcher::searcher2));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(search("day1/input.txt", CalibrationSearcher::searcher2));
    }
}
