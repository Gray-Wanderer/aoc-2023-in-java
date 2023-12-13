package andrei.mishunin.aoc2023.day13;

import andrei.mishunin.aoc2023.tools.InputReader;
import andrei.mishunin.aoc2023.tools.MatrixUtils;

import java.util.ArrayList;
import java.util.List;

public class ErroredMirrorSearcher {
    List<char[][]> patterns = new ArrayList<>();

    public ErroredMirrorSearcher(String file) {
        List<String> input = InputReader.readAllLines(file);

        List<String> pattern = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            String line = input.get(i);
            if (line.isBlank()) {
                patterns.add(pattern.stream()
                                     .map(String::toCharArray)
                                     .toArray(char[][]::new)
                );
                pattern.clear();
            } else {
                pattern.add(line);
            }
        }
        if (!pattern.isEmpty()) {
            patterns.add(pattern.stream()
                                 .map(String::toCharArray)
                                 .toArray(char[][]::new)
            );
        }
    }

    public int getMirrorSum() {
        return patterns.stream()
                .mapToInt(this::searchMirrors)
                .sum();
    }

    private int searchMirrors(char[][] pattern) {
        int mirrorH = 100 * searchMirror(pattern);
        int mirrorV = searchMirror(MatrixUtils.transpose(pattern));
        return mirrorH + mirrorV;
    }

    private int searchMirror(char[][] values) {
        int r = searchMirrorR(values);
        int sizeR = values.length - r;
        int l = searchMirrorL(values);
        int sizeL = l;
        if (r == 0 && l == 0) {
            return 0;
        }
        if (r != 0 && l != 0) {
            if (sizeR > sizeL) {
                return r;
            } else {
                return l;
            }
        } else {
            return l != 0 ? l : r;
        }
    }

    private int searchMirrorR(char[][] values) {
        for (int i = 0; i < values.length - 1; i++) {
            int rowsBeforeReflection = getRowsBeforeReflection(values, i, values.length - 1);
            if (rowsBeforeReflection > 0) {
                return rowsBeforeReflection;
            }
        }
        return 0;
    }

    private int searchMirrorL(char[][] values) {
        for (int i = values.length - 1; i > 0; i--) {
            int rowsBeforeReflection = getRowsBeforeReflection(values, 0, i);
            if (rowsBeforeReflection > 0) {
                return rowsBeforeReflection;
            }
        }
        return 0;
    }

    private int getRowsBeforeReflection(char[][] values, int r, int l) {
        boolean equals = true;
        int errorIndex = -1;
        while (r < l && equals) {
            for (int j = 0; j < values[r].length; j++) {
                if (values[r][j] != values[l][j]) {
                    if (errorIndex < 0) {
                        errorIndex = j;
                    } else if (errorIndex != j) {
                        equals = false;
                        break;
                    }
                }
            }
            if (equals) {
                r++;
                l--;
            }
        }
        if (equals && errorIndex >= 0 && r > l) {
            return r;
        }
        return 0;
    }

    public static void main(String[] args) {
        //400
        System.out.println("== TEST 2 ==");
        System.out.println(new ErroredMirrorSearcher("day13/test.txt").getMirrorSum());
        //27587
        System.out.println("== SOLUTION 2 ==");
        System.out.println(new ErroredMirrorSearcher("day13/input.txt").getMirrorSum());
    }
}
