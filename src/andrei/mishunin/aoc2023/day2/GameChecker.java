package andrei.mishunin.aoc2023.day2;

import andrei.mishunin.aoc2023.tools.InputReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameChecker {
    private static final Map<String, Integer> TOTAL_CUBES = Map.of(
            "red", 12,
            "green", 13,
            "blue", 14
    );

    public static int checkGame(String file) {
        var games = readGameSets(file);

        int result = 0;
        for (int gameNumber = 0; gameNumber < games.size(); gameNumber++) {
            String[] gameSets = games.get(gameNumber);

            boolean correctGame = true;
            for (String gameSet : gameSets) {
                if (!isSetCorrect(gameSet.split(", ?"))) {
                    correctGame = false;
                    break;
                }
            }

            if (correctGame) {
                result += gameNumber + 1;
            }
        }
        return result;
    }

    private static boolean isSetCorrect(String[] gameSet) {
        Map<String, Integer> gameCubes = getEmptyGameMap();

        for (String cubes : gameSet) {
            var p = cubes.split(" +");
            gameCubes.computeIfPresent(p[1], (k, v) -> v + Integer.parseInt(p[0]));
        }
        for (String color : TOTAL_CUBES.keySet()) {
            if (gameCubes.get(color) > TOTAL_CUBES.get(color)) {
                return false;
            }
        }
        return true;
    }

    public static int calcMinPowerOfGames(String file) {
        var games = readGameSets(file);

        int result = 0;
        for (String[] gameSets : games) {
            Map<String, Integer> minPossibleGameCubes = getEmptyGameMap();

            for (String gameSetStr : gameSets) {
                String[] gameSet = gameSetStr.split(", ?");
                for (String cubes : gameSet) {
                    var p = cubes.split(" +");
                    minPossibleGameCubes.computeIfPresent(p[1], (k, v) -> Math.max(v, Integer.parseInt(p[0])));
                }
            }

            int power = 1;
            for (Integer minCubes : minPossibleGameCubes.values()) {
                power *= minCubes;
            }
            result += power;
        }
        return result;
    }

    private static Map<String, Integer> getEmptyGameMap() {
        Map<String, Integer> emptyGameMap = new HashMap<>();
        emptyGameMap.put("red", 0);
        emptyGameMap.put("green", 0);
        emptyGameMap.put("blue", 0);
        return emptyGameMap;
    }

    private static List<String[]> readGameSets(String file) {
        return InputReader.readAllLines(file).stream()
                .filter(s -> !s.isBlank())
                .map(s -> s.replaceAll("^Game \\d+: ", ""))
                .map(s -> s.split("; ?"))
                .toList();
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(checkGame("day2/test.txt"));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(checkGame("day2/input.txt"));

        System.out.println("== TEST 2 ==");
        System.out.println(calcMinPowerOfGames("day2/test.txt"));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(calcMinPowerOfGames("day2/input.txt"));
    }
}
