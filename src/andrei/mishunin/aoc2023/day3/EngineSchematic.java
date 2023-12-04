package andrei.mishunin.aoc2023.day3;

import andrei.mishunin.aoc2023.tools.InputReader;

import java.util.ArrayList;
import java.util.List;

public class EngineSchematic {
    public static int calcSumOfEngine(String file) {
        char[][] schematic = readSchema(file);

        int sum = 0;
        for (int i = 0; i < schematic.length; i++) {
            boolean goodNumber = false;
            int number = 0;
            for (int j = 0; j < schematic[i].length; j++) {
                char c = schematic[i][j];
                if (isNumber(c)) {
                    number = number * 10 + (c - '0');
                    if (hasSymbolNearby(schematic, i, j)) {
                        goodNumber = true;
                    }
                } else {
                    if (goodNumber) {
                        sum += number;
                    }
                    goodNumber = false;
                    number = 0;
                }
            }
            if (goodNumber) {
                sum += number;
            }
        }
        return sum;
    }

    private static char[][] readSchema(String file) {
        List<String> lines = InputReader.readAllLines(file);
        char[][] schematic = new char[lines.size()][];
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.isBlank()) {
                schematic[i] = line.toCharArray();
            }
        }

        return schematic;
    }

    private static boolean hasSymbolNearby(char[][] schematic, int i, int j) {
        for (int ii = i - 1; ii <= i + 1; ii++) {
            for (int jj = j - 1; jj <= j + 1; jj++) {
                if (ii == i && jj == j) {
                    continue;
                }
                if (isSymbol(schematic, ii, jj)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isSymbol(char[][] schematic, int i, int j) {
        if (i < 0 || j < 0 || i >= schematic.length || j >= schematic[i].length) {
            return false;
        }
        return schematic[i][j] != '.' && !isNumber(schematic[i][j]);
    }

    private static boolean isNumber(char[][] schematic, int i, int j) {
        if (i < 0 || j < 0 || i >= schematic.length || j >= schematic[i].length) {
            return false;
        }
        return isNumber(schematic[i][j]);
    }

    private static boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }

    public static int calcGearsRatios(String file) {
        char[][] schematic = readSchema(file);

        int sum = 0;
        for (int i = 0; i < schematic.length; i++) {
            for (int j = 0; j < schematic[i].length; j++) {
                List<int[]> gearNumberPositions = getGearNumberPositions(schematic, i, j);
                if (gearNumberPositions != null) {
                    int n1 = readNumber(schematic, gearNumberPositions.get(0));
                    int n2 = readNumber(schematic, gearNumberPositions.get(1));
                    sum += n1 * n2;
                }
            }
        }
        return sum;
    }

    private static List<int[]> getGearNumberPositions(char[][] schematic, int i, int j) {
        if (schematic[i][j] != '*') {
            return null;
        }

        List<int[]> numbers = new ArrayList<>();

        for (int ii = i - 1; ii <= i + 1; ii++) {
            if (isNumber(schematic, ii, j - 1)) {
                numbers.add(new int[]{ii, j - 1});
                if (!isNumber(schematic, ii, j) && isNumber(schematic, ii, j + 1)) {
                    numbers.add(new int[]{ii, j + 1});
                }
            } else if (isNumber(schematic, ii, j)) {
                numbers.add(new int[]{ii, j});
            } else if (isNumber(schematic, ii, j + 1)) {
                numbers.add(new int[]{ii, j + 1});
            }
        }

        return numbers.size() == 2 ? numbers : null;
    }

    private static int readNumber(char[][] schematic, int[] ij) {
        int i = ij[0];
        int j = ij[1];
        while (j > 0 && isNumber(schematic[i][j - 1])) {
            j--;
        }
        int number = 0;
        for (; j < schematic[i].length && isNumber(schematic[i][j]); j++) {
            number = number * 10 + (schematic[i][j] - '0');
        }
        return number;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(calcSumOfEngine("day3/test.txt"));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(calcSumOfEngine("day3/input.txt"));

        System.out.println("== TEST 2 ==");
        System.out.println(calcGearsRatios("day3/test.txt"));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(calcGearsRatios("day3/input.txt"));
    }
}
