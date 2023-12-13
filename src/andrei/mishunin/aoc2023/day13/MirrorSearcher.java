package andrei.mishunin.aoc2023.day13;

import andrei.mishunin.aoc2023.tools.InputReader;
import andrei.mishunin.aoc2023.tools.MatrixUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

public class MirrorSearcher {
    List<char[][]> patterns = new ArrayList<>();

    public MirrorSearcher(String file) {
        List<String> input = InputReader.readAllLines(file);

        List<String> pattern = new ArrayList<>();
        for (String line : input) {
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
                .mapToInt(pattern -> this.searchMirrors(pattern, this::searchMirror))
                .sum();
    }

    private int searchMirrors(char[][] pattern, ToIntFunction<char[][]> mirrorSearcher) {
        int mirrorH = mirrorSearcher.applyAsInt(pattern);
        if (mirrorH > 0) {
            return mirrorH * 100;
        }
        return mirrorSearcher.applyAsInt(MatrixUtils.transpose(pattern));
    }

    private int searchMirror(char[][] values) {
        for (int i = 1; i < values.length; i++) {
            int r = i - 1;
            int l = i;
            boolean equals = true;
            while (r >= 0 && l < values.length && equals) {
                for (int j = 0; j < values[r].length; j++) {
                    if (values[r][j] != values[l][j]) {
                        equals = false;
                        break;
                    }
                }
                if (equals) {
                    r--;
                    l++;
                }
            }
            if (equals) {
                return r + (l - r) / 2 + 1;
            }
        }
        return 0;
    }

    public int getDirtyMirrorSum() {
        return patterns.stream()
                .mapToInt(pattern -> this.searchMirrors(pattern, this::searchDirtyMirror))
                .sum();
    }

    private int searchDirtyMirror(char[][] values) {
        for (int i = 1; i < values.length; i++) {
            int r = i - 1;
            int l = i;
            boolean equals = true;
            int errorIndex = -1;
            while (r >= 0 && l < values.length && equals) {
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
                    r--;
                    l++;
                }
            }
            if (equals && errorIndex >= 0) {
                return r + (l - r) / 2 + 1;
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(new MirrorSearcher("day13/test.txt").getMirrorSum());
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new MirrorSearcher("day13/input.txt").getMirrorSum());

        System.out.println("== TEST 1 ==");
        System.out.println(new MirrorSearcher("day13/test.txt").getDirtyMirrorSum());
        System.out.println("== SOLUTION 2 ==");
        System.out.println(new MirrorSearcher("day13/input.txt").getDirtyMirrorSum());
    }
}
