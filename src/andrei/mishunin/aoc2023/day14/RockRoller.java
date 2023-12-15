package andrei.mishunin.aoc2023.day14;

import andrei.mishunin.aoc2023.tools.InputReader;
import andrei.mishunin.aoc2023.tools.MatrixUtils;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RockRoller {
    char[][] rocksPlate;

    public RockRoller(String file) {
        List<String> input = InputReader.readAllLines(file)
                .stream().filter(s -> !s.isBlank()).toList();
        rocksPlate = input.stream()
                .map(String::toCharArray)
                .toArray(char[][]::new);
    }

    public int rollToNorthAndSumTheNorthLoad() {
        rollToNorth();
        return sumTheNorthLoad();
    }

    private int sumTheNorthLoad() {
        int sum = 0;
        int n = rocksPlate.length;
        for (char[] chars : rocksPlate) {
            for (char aChar : chars) {
                if (aChar == 'O') {
                    sum += n;
                }
            }
            n--;
        }
        return sum;
    }

    public int rollAndSumTheNorthLoad(int rollCount) {
        Map<Integer, Integer> rollingStates = new HashMap<>();
        Map<Integer, Integer> loads = new HashMap<>();

        int fromState = rocksToHash();
        int load = sumTheNorthLoad();
        loads.put(fromState, load);

        int cycle = -1;
        for (int i = 0; i < rollCount; i++) {
            if (rollingStates.containsKey(fromState)) {
                if (cycle < 0) {
                    cycle = getCycleLen(rollingStates, fromState);
                    i += ((rollCount - i) / cycle) * cycle;
                }

                fromState = rollingStates.get(fromState);
                load = loads.get(fromState);
            } else {
                fullRoll();

                int nextState = rocksToHash();
                rollingStates.put(fromState, nextState);

                load = sumTheNorthLoad();
                loads.put(nextState, load);

                fromState = nextState;
            }
        }
        return load;
    }

    private int getCycleLen(Map<Integer, Integer> linkedMap, Integer start) {
        int cycle = 1;
        Integer next = linkedMap.get(start);
        while (!next.equals(start)) {
            next = linkedMap.get(next);
            cycle++;
        }
        return cycle;
    }

    private void fullRoll() {
        for (int i = 0; i < 4; i++) {
            rollToNorth();
            MatrixUtils.rotate(rocksPlate);
        }
    }

    public int rocksToHash() {
        return Arrays.deepHashCode(rocksPlate);
    }

    private void rollToNorth() {
        Deque<Integer> emptyRows = new ArrayDeque<>();
        for (int j = 0; j < rocksPlate[0].length; j++) {
            for (int i = 0; i < rocksPlate.length; i++) {
                if (rocksPlate[i][j] == '.') {
                    emptyRows.add(i);
                } else if (rocksPlate[i][j] == '#') {
                    emptyRows.clear();
                } else if (!emptyRows.isEmpty()) {
                    Integer freeI = emptyRows.poll();
                    rocksPlate[freeI][j] = rocksPlate[i][j];
                    rocksPlate[i][j] = '.';
                    emptyRows.add(i);
                }
            }
            emptyRows.clear();
        }
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(new RockRoller("day14/test.txt").rollToNorthAndSumTheNorthLoad());
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new RockRoller("day14/input.txt").rollToNorthAndSumTheNorthLoad());


        System.out.println("== TEST 2 ==");
        System.out.println(new RockRoller("day14/test.txt").rollAndSumTheNorthLoad(1000000000));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(new RockRoller("day14/input.txt").rollAndSumTheNorthLoad(1000000000));
    }

}
