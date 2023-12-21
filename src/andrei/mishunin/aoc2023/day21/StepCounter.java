package andrei.mishunin.aoc2023.day21;

import andrei.mishunin.aoc2023.tools.InputReader;
import andrei.mishunin.aoc2023.tools.IntPair;
import andrei.mishunin.aoc2023.tools.MatrixUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StepCounter {
    char[][] map;
    IntPair startPosition;

    public StepCounter(String fileName) {
        map = InputReader.readAllLines(fileName).stream()
                .filter(s -> !s.isBlank())
                .map(String::toCharArray)
                .toArray(char[][]::new);
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 'S') {
                    startPosition = new IntPair(i, j);
                    map[i][j] = '.';
                }
            }
        }
    }

    public int countGardenPlotsForSteps(int steps) {
        Set<IntPair> route = new HashSet<>();
        route.add(startPosition);
        int stepCount = 0;
        while (!route.isEmpty() && stepCount < steps) {
            Set<IntPair> next = new HashSet<>();
            for (IntPair step : route) {
                doStep(next, step);
            }
            stepCount++;
            route = next;
        }

        return route.size();
    }

    private void doStep(Set<IntPair> next, IntPair current) {
        int i = current.i();
        int j = current.j();
        if (map[i][j] != '.') {
            return;
        }
        for (int k = -1; k <= 1; k += 2) {
            if (MatrixUtils.isIndexInMatrix(map, i + k, j) && map[i + k][j] == '.') {
                next.add(new IntPair(i + k, j));
            }
            if (MatrixUtils.isIndexInMatrix(map, i, j + k) && map[i][j + k] == '.') {
                next.add(new IntPair(i, j + k));
            }
        }
    }

    public int countInfinityGardenPlotsForSteps(int steps) {
        int[] cycle = findCycle();
        System.out.println("Cycle: " + cycle[0] + " " + cycle[1]);

        Map<IntPair, Set<IntPair>> routeByMaps = new HashMap<>();
        routeByMaps.computeIfAbsent(new IntPair(0, 0), k -> new HashSet<>()).add(startPosition);
        int[] cycleCount = new int[2];
        int stepCount = 0;
        while (!routeByMaps.isEmpty() && stepCount < steps) {
            Map<IntPair, Set<IntPair>> next = new HashMap<>();
            for (IntPair step : routeByMaps) {
                doStep(next, step);
            }
            stepCount++;
            routeByMaps = next;
        }

        return routeByMaps.size();
    }

    private int[] findCycle() {
        Set<IntPair> route = new HashSet<>();
        route.add(startPosition);
        int stepCount = 0;
        int[] cycle = new int[2];
        int countCycle = 0;
        while (!route.isEmpty() && countCycle < 2) {
            Set<IntPair> next = new HashSet<>();
            for (IntPair step : route) {
                doStep(next, step);
            }
            stepCount++;
            route = next;
            if (cycle[stepCount % 2] == route.size()) {
                countCycle++;
            } else {
                cycle[stepCount % 2] = route.size();
            }
        }
        return cycle;
    }


    private void doStep4D(Set<Int4D> next, Int4D current) {
        int i = current.i();
        int j = current.j();
        int mapI = current.mapI();
        int mapJ = current.mapJ();
        if (map[i][j] != '.') {
            return;
        }
        int nextInd = i - 1;
        int nextMap = mapI;
        if (!MatrixUtils.isIndexInMatrix(map, nextInd, j)) {
            nextInd += map.length;
            nextMap--;
        }
        if (map[nextInd][j] == '.') {
            next.add(new Int4D(nextInd, j, nextMap, mapJ));
        }

        nextInd = i + 1;
        nextMap = mapI;
        if (nextInd == map.length) {
            nextInd = 0;
            nextMap++;
        }
        if (map[nextInd][j] == '.') {
            next.add(new Int4D(nextInd, j, nextMap, mapJ));
        }

        nextInd = j - 1;
        nextMap = mapJ;
        if (nextInd == -1) {
            nextInd += map[0].length;
            nextMap--;
        }
        if (map[i][nextInd] == '.') {
            next.add(new Int4D(i, nextInd, mapI, nextMap));
        }

        nextInd = j + 1;
        nextMap = mapI;
        if (nextInd == map[0].length) {
            nextInd = 0;
            nextMap++;
        }
        if (map[i][nextInd] == '.') {
            next.add(new Int4D(i, nextInd, mapI, nextMap));
        }
    }


    public static void main(String[] args) {
//        System.out.println("== TEST 1 ==");
//        System.out.println(new StepCounter("day21/test.txt").countGardenPlotsForSteps(6));
//        System.out.println("== SOLUTION 1 ==");
//        System.out.println(new StepCounter("day21/input.txt").countGardenPlotsForSteps(64));


        System.out.println("== TEST 2 ==");
        System.out.println(new StepCounter("day21/test.txt").countInfinityGardenPlotsForSteps(5000));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(new StepCounter("day21/input.txt").countInfinityGardenPlotsForSteps(26501365));
    }

    record Int4D(int i, int j, int mapI, int mapJ) {
    }
}
