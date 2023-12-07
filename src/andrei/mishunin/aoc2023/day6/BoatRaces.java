package andrei.mishunin.aoc2023.day6;

import andrei.mishunin.aoc2023.tools.InputReader;

import java.util.Arrays;

public class BoatRaces {
    private final int[] times;
    private final int[] distances;

    private final long superTime;
    private final long superDistance;

    public BoatRaces(String file) {
        var lines = InputReader.readAllLines(file).stream()
                .map(s -> s.replaceAll("^\\w+: +", ""))
                .toList();
        times = Arrays.stream(lines.get(0).split(" +")).mapToInt(Integer::valueOf).toArray();
        distances = Arrays.stream(lines.get(1).split(" +")).mapToInt(Integer::valueOf).toArray();

        superTime = Long.parseLong(lines.get(0).replaceAll(" +", ""));
        superDistance = Long.parseLong(lines.get(1).replaceAll(" +", ""));
    }

    public int multNumberOfRecord() {
        int result = 1;
        for (int i = 0; i < times.length; i++) {
            result *= wayToWinRace(times[i], distances[i]);
        }
        return result;
    }

    public long wayToWinSuperRace() {
        return wayToWinRace(superTime, superDistance);
    }

    public long wayToWinRace(long time, long distance) {
        double d = Math.sqrt(time * time - 4 * distance);
        long first = (long) Math.ceil((time - d) / 2);
        if ((time - first) * first <= distance) {
            first++;
        }

        long last = (long) Math.floor((time + d) / 2);
        if ((time - last) * last <= distance) {
            last--;
        }

        return last - first + 1;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(new BoatRaces("day6/test.txt").multNumberOfRecord());
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new BoatRaces("day6/input.txt").multNumberOfRecord());

        System.out.println("== TEST 2 ==");
        System.out.println(new BoatRaces("day6/test.txt").wayToWinSuperRace());
        System.out.println("== SOLUTION 2 ==");
        System.out.println(new BoatRaces("day6/input.txt").wayToWinSuperRace());
    }
}
