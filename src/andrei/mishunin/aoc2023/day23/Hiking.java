package andrei.mishunin.aoc2023.day23;

import andrei.mishunin.aoc2023.tools.InputReader;
import andrei.mishunin.aoc2023.tools.IntPair;
import andrei.mishunin.aoc2023.tools.MatrixUtils;

import java.util.Deque;
import java.util.LinkedList;

public class Hiking {
    char[][] map;
    boolean[][] visited = null;
    IntPair start;
    IntPair end;
    Deque<Point> queue;

    public Hiking(String fileName) {
        map = InputReader.readAllLines(fileName).stream()
                .filter(s -> !s.isBlank())
                .map(String::toCharArray)
                .toArray(char[][]::new);
        start = new IntPair(0, 1);
        end = new IntPair(map.length - 1, map[0].length - 2);
    }

    public int findLongestRoute(boolean climbing) {
        int n = map.length - 1;
        int m = map[0].length - 1;
        visited = new boolean[n + 1][m + 1];
        queue = new LinkedList<>();
        queue.add(new Point(start, 0));

        int steps = 0;
        while (!queue.isEmpty()) {
            var point = queue.poll();
            if (end.equals(point.ij)) {
                steps = Math.max(steps, point.steps);
            } else {
                int i = point.ij.i();
                int j = point.ij.j();
                if (point.steps == -1) {
                    visited[i][j] = false;
                } else {
                    visited[i][j] = true;
                    queue.push(new Point(point.ij, -1));
                    move(i, j, point.steps, climbing);
                }
            }
        }
        return steps;
    }

    private void move(int i, int j, int steps, boolean climbing) {
        int nextStep = steps + 1;
        if (canAddPoint(i - 1, j, 'v', climbing)) {
            queue.push(new Point(new IntPair(i - 1, j), nextStep));
        }
        if (canAddPoint(i, j - 1, '>', climbing)) {
            queue.push(new Point(new IntPair(i, j - 1), nextStep));
        }
        if (canAddPoint(i + 1, j, '^', climbing)) {
            queue.push(new Point(new IntPair(i + 1, j), nextStep));
        }
        if (canAddPoint(i, j + 1, '<', climbing)) {
            queue.push(new Point(new IntPair(i, j + 1), nextStep));
        }
    }

    private boolean canAddPoint(int i, int j, char wall, boolean climbing) {
        return MatrixUtils.isIndexInMatrix(visited, i, j) && !visited[i][j] && map[i][j] != '#' &&
                (climbing || map[i][j] != wall);
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(new Hiking("day23/test.txt").findLongestRoute(false));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new Hiking("day23/input.txt").findLongestRoute(false));

        System.out.println("== TEST 2 ==");
        System.out.println(new Hiking("day23/test.txt").findLongestRoute(true));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(new Hiking("day23/input.txt").findLongestRoute(true));
    }

    private record Point(IntPair ij, int steps) {
    }
}
