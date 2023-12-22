package andrei.mishunin.aoc2023.day21;

import andrei.mishunin.aoc2023.tools.InputReader;
import andrei.mishunin.aoc2023.tools.IntPair;
import andrei.mishunin.aoc2023.tools.MatrixUtils;

import java.util.Collections;
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

    public long countInfinityGardenPlotsForSteps(int steps) {
        CycleAndSteps cycle = findCycle();

        int[] cycleCount = new int[2];
        Map<IntPair, Set<IntPair>> routeByMaps = fillRoutesMapAndCycles(cycle, steps, cycleCount);

        long sum = 0;
        for (var uniqueGardens : routeByMaps.values()) {
            sum += uniqueGardens.size();
        }

        if (steps % 2 == 0) {
            sum += cycleCount[0] * cycle.evenCycle + cycleCount[1] * cycle.oddCycle;
        } else {
            sum += cycleCount[0] * cycle.oddCycle + cycleCount[1] * cycle.evenCycle;
        }
        return sum;
    }

    private Map<IntPair, Set<IntPair>> fillRoutesMapAndCycles(CycleAndSteps cycle, int steps, int[] cycleCount) {
        Map<IntPair, Set<IntPair>> routeByMaps = new HashMap<>();
        Set<IntPair> blocked = new HashSet<>();
        Set<IntPair> cycleCandidate = new HashSet<>();
        routeByMaps.computeIfAbsent(new IntPair(0, 0), k -> new HashSet<>()).add(startPosition);
        int stepCount = 0;
        while (!routeByMaps.isEmpty() && stepCount < steps) {
            Map<IntPair, Set<IntPair>> next = new HashMap<>();
            for (IntPair map : routeByMaps.keySet()) {
                for (IntPair step : routeByMaps.get(map)) {
                    doStepInInfinityMap(next, blocked, map, step);
                }
            }
            stepCount++;
            routeByMaps = next;
            int cycleInd = stepCount % 2;

            for (IntPair mapInd : new HashSet<>(routeByMaps.keySet())) {
                int size = routeByMaps.get(mapInd).size();
                if (size == cycle.evenCycle) {
                    if (cycleCandidate.contains(mapInd)) {
                        if (cycleCount != null) {
                            cycleCount[cycleInd]++;
                        }
                        routeByMaps.remove(mapInd);
                        blocked.add(mapInd);
                        cycleCandidate.remove(mapInd);
                    } else {
                        cycleCandidate.add(mapInd);
                    }
                } else if (size == cycle.oddCycle) {
                    if (cycleCandidate.contains(mapInd)) {
                        if (cycleCount != null) {
                            cycleCount[(cycleInd + 1) % 2]++;
                        }
                        routeByMaps.remove(mapInd);
                        blocked.add(mapInd);
                    } else {
                        cycleCandidate.add(mapInd);
                    }
                }
            }
        }
        return routeByMaps;
    }

    private CycleAndSteps findCycle() {
        Set<IntPair> route = new HashSet<>();
        route.add(startPosition);
        int stepCount = 0;
        int[][] cycle = new int[2][2];
        int countCycle = 0;
        while (!route.isEmpty() && countCycle < 2) {
            Set<IntPair> next = new HashSet<>();
            for (IntPair step : route) {
                doStep(next, step);
            }
            stepCount++;
            route = next;
            int cycleInd = stepCount % 2;
            if (cycle[0][cycleInd] == route.size()) {
                countCycle++;
            } else {
                cycle[0][cycleInd] = route.size();
                cycle[1][cycleInd] = stepCount;
            }
        }
        return new CycleAndSteps(cycle[0][0], cycle[0][1], cycle[1][0], cycle[1][1]);
    }

    private void doStepInInfinityMap(Map<IntPair, Set<IntPair>> next, Set<IntPair> bannedMaps, IntPair mapInd, IntPair current) {
        int i = current.i();
        int j = current.j();
        int mapI = mapInd.i();
        int mapJ = mapInd.j();
        if (map[i][j] != '.') {
            return;
        }
        if (bannedMaps.contains(mapInd)) {
            return;
        }
        for (int k = -1; k <= 1; k += 2) {
            int nextI = i + k;
            int nextMapI = mapI;
            if (nextI == -1) {
                nextI += map.length;
                nextMapI--;
            } else if (nextI == map.length) {
                nextI = 0;
                nextMapI++;
            }
            if (map[nextI][j] == '.') {
                IntPair nextMap = new IntPair(nextMapI, mapJ);
                if (!bannedMaps.contains(nextMap)) {
                    next.computeIfAbsent(nextMap, __ -> new HashSet<>())
                            .add(new IntPair(nextI, j));
                }
            }
            int nextJ = j + k;
            int nextMapJ = mapJ;
            if (nextJ == -1) {
                nextJ += map[0].length;
                nextMapJ--;
            } else if (nextJ == map[0].length) {
                nextJ = 0;
                nextMapJ++;
            }
            if (map[i][nextJ] == '.') {
                IntPair nextMap = new IntPair(mapI, nextMapJ);
                if (!bannedMaps.contains(nextMap)) {
                    next.computeIfAbsent(nextMap, __ -> new HashSet<>())
                            .add(new IntPair(i, nextJ));
                }
            }
        }
    }

    public long countInfinityGardenPlotsInPerfectMap(int steps) {
        CycleAndSteps cycleAndSteps = findCycle();
        int stepSize = cycleAndSteps.evenSteps;

        int stepCount = stepSize;
        long borderSize = 0;
        long[] cycleCount = new long[2];
        cycleCount[0] = 1;
        while ((stepCount + stepSize + 1) <= steps) {
            borderSize++;
            stepCount += stepSize + 1;
            cycleCount[stepCount % 2] += borderSize * 4;
        }
        int cycleIndex = stepCount % 2;
        int stepLimit;
        int smallSteps;
        if (cycleIndex == 1) {
            stepLimit = (stepSize * 2 + 1) + (steps - stepCount);
            smallSteps = 2;
        } else {
            stepLimit = (stepSize * 3 + 2) + (steps - stepCount);
            smallSteps = 3;
        }

        Map<IntPair, Set<IntPair>> routeByMaps = fillRoutesMapAndCycles(cycleAndSteps, stepLimit, null);
        long diagonalLayers[] = new long[2];
        long corners = 0;
        for (int scan = smallSteps - 1; scan <= smallSteps; scan++) {
            int dI = scan - smallSteps + 1;
            diagonalLayers[dI] += routeByMaps.getOrDefault(new IntPair(scan, 1), Collections.emptySet()).size();
            diagonalLayers[dI] += routeByMaps.getOrDefault(new IntPair(scan, -1), Collections.emptySet()).size();
            diagonalLayers[dI] += routeByMaps.getOrDefault(new IntPair(-scan, 1), Collections.emptySet()).size();
            diagonalLayers[dI] += routeByMaps.getOrDefault(new IntPair(-scan, -1), Collections.emptySet()).size();

            corners += routeByMaps.getOrDefault(new IntPair(scan + 1, 0), Collections.emptySet()).size();
            corners += routeByMaps.getOrDefault(new IntPair(-scan - 1, 0), Collections.emptySet()).size();
            corners += routeByMaps.getOrDefault(new IntPair(0, scan + 1), Collections.emptySet()).size();
            corners += routeByMaps.getOrDefault(new IntPair(0, -scan - 1), Collections.emptySet()).size();
        }
        long diagonals = (borderSize * diagonalLayers[0]) + ((borderSize + 1) * diagonalLayers[1]);

        long sum = 0;
        if (stepCount % 2 == 0) {
            sum += cycleCount[0] * cycleAndSteps.evenCycle + cycleCount[1] * cycleAndSteps.oddCycle;
        } else {
            sum += cycleCount[0] * cycleAndSteps.oddCycle + cycleCount[1] * cycleAndSteps.evenCycle;
        }

        sum += diagonals;
        sum += corners;
        return sum;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(new StepCounter("day21/test.txt").countGardenPlotsForSteps(6));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new StepCounter("day21/input.txt").countGardenPlotsForSteps(64));

        System.out.println("== TEST 2 ==");
        System.out.println(new StepCounter("day21/test.txt").countInfinityGardenPlotsForSteps(1000));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(new StepCounter("day21/input.txt").countInfinityGardenPlotsInPerfectMap(26501365));
    }

    private record CycleAndSteps(int evenCycle, int oddCycle, int evenSteps, int oddSteps) {
    }
}
