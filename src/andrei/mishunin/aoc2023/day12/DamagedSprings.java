package andrei.mishunin.aoc2023.day12;

import andrei.mishunin.aoc2023.tools.InputReader;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class DamagedSprings {
    private final char[][] records;
    private final int[][] metadata;
    private Long[][][] memo = null;

    public DamagedSprings(String file, int multiplication) {
        var input = InputReader.readAllLines(file).stream()
                .filter(s -> !s.isBlank())
                .map(s -> s.split(" "))
                .toList();
        records = input.stream()
                .map(s -> duplicateData(s[0], multiplication, '?').toCharArray())
                .toArray(char[][]::new);
        metadata = input.stream()
                .map(s -> duplicateData(s[1], multiplication, ','))
                .map(s -> Arrays.stream(s.split(",")).mapToInt(Integer::parseInt).toArray())
                .toArray(int[][]::new);
    }

    private String duplicateData(String data, int multiplication, char separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < multiplication; i++) {
            sb.append(data);
            if (i < multiplication - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    public long getSumAllPossibleResults() {
        long sum = 0;
        for (int i = 0; i < records.length; i++) {
            long count = countPossibleResults(records[i], metadata[i]);
            sum += count;
        }
        return sum;
    }

    public long countPossibleResults(char[] s, int[] metadata) {
        Deque<Integer> brokenQueue = new ArrayDeque<>();
        int totalBrokenCount = 0;
        int maxBrokenCount = 0;
        for (int broken : metadata) {
            brokenQueue.add(broken);
            totalBrokenCount += broken;
            maxBrokenCount = Math.max(maxBrokenCount, broken);
        }
        memo = new Long[s.length + 1][2][totalBrokenCount + 1];
        return buildShort(s, 0, totalBrokenCount, -1, brokenQueue);
    }

    public long buildShort(char[] record, int i, int totalBrokenCount, int brokenCount, Deque<Integer> brokenQueue) {
        int brokenMemo = brokenCount == 0 ? 0 : 1;

        if (memo[i][brokenMemo][totalBrokenCount] != null) {
            return memo[i][brokenMemo][totalBrokenCount];
        }

        if (i == record.length) {
            return memo[i][brokenMemo][totalBrokenCount] = brokenCount <= 0 && brokenQueue.isEmpty() ? 1L : 0L;
        }

        char springState = record[i];

        if (brokenCount == 0) {  // must be operational spring
            if (springState == '#') {
                return memo[i][brokenMemo][totalBrokenCount] = 0L;
            } else {
                return memo[i][brokenMemo][totalBrokenCount] = buildShort(record, i + 1, totalBrokenCount, brokenCount - 1, brokenQueue);
            }
        } else if (brokenCount > 0) {  // must be broken spring
            if (springState == '.') {
                return memo[i][brokenMemo][totalBrokenCount] = 0L;
            }
            return memo[i][brokenMemo][totalBrokenCount] = buildShort(record, i + 1, totalBrokenCount - 1, brokenCount - 1, brokenQueue);
        } else {  // can be broken or operational spring
            long sum = 0L;
            if (springState == '?' || springState == '.') {  // count if operational spring
                sum = buildShort(record, i + 1, totalBrokenCount, brokenCount, brokenQueue);
            }
            if (springState == '#' && brokenQueue.isEmpty()) {  // must be operational spring, no broken springs left
                return memo[i][brokenMemo][totalBrokenCount] = 0L;
            }
            if ((springState == '?' || springState == '#') && !brokenQueue.isEmpty()) {  // count if broken spring
                int newBrokenCount = brokenQueue.poll();
                sum += buildShort(record, i + 1, totalBrokenCount - 1, newBrokenCount - 1, brokenQueue);
                brokenQueue.addFirst(newBrokenCount);
            }
            return memo[i][brokenMemo][totalBrokenCount] = sum;
        }
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(new DamagedSprings("day12/test.txt", 1).getSumAllPossibleResults());
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new DamagedSprings("day12/input.txt", 1).getSumAllPossibleResults());

        System.out.println("== TEST 2 ==");
        System.out.println(new DamagedSprings("day12/test.txt", 5).getSumAllPossibleResults());
        System.out.println("== SOLUTION 2 ==");
        System.out.println(new DamagedSprings("day12/input.txt", 5).getSumAllPossibleResults());
    }
}
