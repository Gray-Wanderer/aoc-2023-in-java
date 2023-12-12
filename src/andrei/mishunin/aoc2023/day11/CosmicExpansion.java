package andrei.mishunin.aoc2023.day11;

import andrei.mishunin.aoc2023.tools.InputReader;
import andrei.mishunin.aoc2023.tools.IntPair;

import java.util.ArrayList;
import java.util.List;

public class CosmicExpansion {
    private final char[][] rawUniverse;
    private final int[] rowOffset;
    private final int[] columnOffset;
    private final List<IntPair> galaxyCoordinates;

    public CosmicExpansion(String file, int expand) {
        rawUniverse = readRawUniverse(file);
        rowOffset = calcRowOffset(expand - 1);
        columnOffset = calcColumnOffset(expand - 1);
        galaxyCoordinates = calcGalaxyCoordinates();
    }

    private char[][] readRawUniverse(String file) {
        return InputReader.readAllLines(file)
                .stream().filter(s -> !s.isBlank())
                .map(String::toCharArray)
                .toArray(char[][]::new);
    }

    private int[] calcRowOffset(int expand) {
        int[] rowOffset = new int[rawUniverse.length];
        for (int i = 0; i < rawUniverse.length; i++) {
            if (i > 0) {
                rowOffset[i] += rowOffset[i - 1];
            }
            boolean empty = true;
            for (int j = 0; j < rawUniverse[0].length; j++) {
                if (rawUniverse[i][j] == '#') {
                    empty = false;
                    break;
                }
            }
            if (empty) {
                rowOffset[i] += expand;
            }
        }
        return rowOffset;
    }

    private int[] calcColumnOffset(int expand) {
        int[] columnOffset = new int[rawUniverse[0].length];
        for (int j = 0; j < rawUniverse[0].length; j++) {
            if (j > 0) {
                columnOffset[j] += columnOffset[j - 1];
            }
            boolean empty = true;
            for (char[] universeRow : rawUniverse) {
                if (universeRow[j] == '#') {
                    empty = false;
                    break;
                }
            }
            if (empty) {
                columnOffset[j] += expand;
            }
        }
        return columnOffset;
    }

    private List<IntPair> calcGalaxyCoordinates() {
        var galaxyCoordinates = new ArrayList<IntPair>();
        for (int i = 0; i < rawUniverse.length; i++) {
            for (int j = 0; j < rawUniverse[i].length; j++) {
                if (rawUniverse[i][j] == '#') {
                    galaxyCoordinates.add(new IntPair(i + rowOffset[i], j + columnOffset[j]));
                }
            }
        }
        return galaxyCoordinates;
    }

    public long getGalaxyDistancesSum() {
        long sum = 0;
        for (int i = 1; i < galaxyCoordinates.size(); i++) {
            IntPair first = galaxyCoordinates.get(i);
            for (int j = 0; j < i; j++) {
                IntPair second = galaxyCoordinates.get(j);
                sum += Math.abs(second.i() - first.i()) + Math.abs(second.j() - first.j());
            }
        }
        return sum;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(new CosmicExpansion("day11/test.txt", 2).getGalaxyDistancesSum());
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new CosmicExpansion("day11/input.txt", 2).getGalaxyDistancesSum());

        System.out.println("== TEST 2 ==");
        System.out.println(new CosmicExpansion("day11/test.txt", 10).getGalaxyDistancesSum());
        System.out.println("== SOLUTION 2 ==");
        System.out.println(new CosmicExpansion("day11/input.txt", 1000000).getGalaxyDistancesSum());
    }
}
