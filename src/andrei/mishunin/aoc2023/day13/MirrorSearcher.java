package andrei.mishunin.aoc2023.day13;

import andrei.mishunin.aoc2023.tools.InputReader;

import java.util.ArrayList;
import java.util.List;

public class MirrorSearcher {
    List<char[][]> patterns = new ArrayList<>();

    public MirrorSearcher(String file) {
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
        String[] horisontal = new String[pattern.length];
        for (int i = 0; i < pattern.length; i++) {
            horisontal[i] = new String(pattern[i]);
        }
        int mirrorH = 100 * searchMirror(horisontal);

//        int n = pattern[0].length;
//        String[] vertical = new String[n];
//        for (int j = 0; j < n; j++) {
//            char[] r = new char[pattern.length];
//            for (int i = 0; i < pattern.length; i++) {
//                r[i] = pattern[i][j];
//            }
//            vertical[j] = new String(r);
//        }

        int n = pattern.length;
        int m = pattern[0].length;
        char[][] transposed = new char[m][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                transposed[j][i] = pattern[i][j];
            }
        }

        String[] vertical = new String[transposed.length];
        for (int i = 0; i < transposed.length; i++) {
            vertical[i] = new String(transposed[i]);
        }

        int mirrorV = searchMirror(vertical);
        return mirrorH + mirrorV;
    }

    private int searchMirror(String[] values) {
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

    private int searchMirrorR(String[] values) {
        for (int i = 0; i < values.length - 2; i++) {
            int r = i;
            int l = values.length - 1;
            while (r < l && values[r].equals(values[l])) {
                r++;
                l--;
            }
            if (r > l) {
                //.. middle .. l,r ...
//                Arrays.stream(values).forEach(System.out::println);
//                System.out.println(r);
//                System.out.println();
                return r;
            }
        }
        return 0;
    }

    private int searchMirrorL(String[] values) {
        for (int i = values.length - 1; i > 0; i--) {
            int r = 0;
            int l = i;
            while (r < l && values[r].equals(values[l])) {
                r++;
                l--;
            }
            if (r > l) {
                //.. l,r ... middle
                return r;
            }
        }
        return 0;
    }

    public static void main(String[] args) {
//        System.out.println("== TEST 1 ==");
//        System.out.println(new MirrorSearcher("day13/test.txt").getMirrorSum());
        //38100
        //38152
        //36706
        //19638
        //25906
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new MirrorSearcher("day13/input.txt").getMirrorSum());
    }
}
