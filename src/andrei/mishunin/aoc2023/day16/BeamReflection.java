package andrei.mishunin.aoc2023.day16;

import andrei.mishunin.aoc2023.tools.InputReader;
import andrei.mishunin.aoc2023.tools.MatrixUtils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public class BeamReflection {
    private static final int RIGHT = 0b0001;
    private static final int DOWN = 0b0010;
    private static final int LEFT = 0b0100;
    private static final int UP = 0b1000;
    private final char[][] contraption;

    public BeamReflection(String file) {
        contraption = InputReader.readAllLines(file).stream()
                .filter(s -> !s.isBlank())
                .map(String::toCharArray)
                .toArray(char[][]::new);
    }

    public int countEnergizedTilesForUpRight() {
        return countEnergizedTiles(new BeamPosition(0, 0, RIGHT));
    }

    public int bestCountEnergizedTiles() {
        int n = contraption.length - 1;
        int m = contraption[0].length - 1;
        int best = 0;
        for (int i = 0; i <= n; i++) {
            best = Math.max(best, countEnergizedTiles(new BeamPosition(i, 0, RIGHT)));
            best = Math.max(best, countEnergizedTiles(new BeamPosition(i, m, LEFT)));
        }
        for (int j = 0; j <= m; j++) {
            best = Math.max(best, countEnergizedTiles(new BeamPosition(0, j, DOWN)));
            best = Math.max(best, countEnergizedTiles(new BeamPosition(m, j, UP)));
        }
        return best;
    }

    private int countEnergizedTiles(BeamPosition start) {
        int n = contraption.length;
        int m = contraption[0].length;
        int[][] visited = new int[n][m];

        Deque<BeamPosition> beamStack = new ArrayDeque<>();
        beamStack.add(start);

        while (!beamStack.isEmpty()) {
            moveBeam(beamStack, visited, beamStack.poll());
        }

        int count = 0;
        for (int[] row : visited) {
            for (int e : row) {
                if (e > 0) {
                    count++;
                }
            }
        }
        return count;
    }

    private void moveBeam(Deque<BeamPosition> beamStack, int[][] visited, BeamPosition beam) {
        if ((visited[beam.i][beam.j] & beam.direction) > 0) {
            return;
        } else {
            visited[beam.i][beam.j] |= beam.direction;
        }

        char point = contraption[beam.i][beam.j];
        if (point == '-') {
            if (beam.direction == RIGHT || beam.direction == LEFT) {
                nextStraightBeam(beam).ifPresent(beamStack::add);
            } else {
                beamStack.add(new BeamPosition(beam.i, beam.j, LEFT));
                beamStack.add(new BeamPosition(beam.i, beam.j, RIGHT));
            }
        } else if (point == '|') {
            if (beam.direction == UP || beam.direction == DOWN) {
                nextStraightBeam(beam).ifPresent(beamStack::add);
            } else {
                beamStack.add(new BeamPosition(beam.i, beam.j, UP));
                beamStack.add(new BeamPosition(beam.i, beam.j, DOWN));
            }
        } else if (point == '\\') {
            nextLeftReflectedBeam(beam).ifPresent(beamStack::add);
        } else if (point == '/') {
            nextRightReflectedBeam(beam).ifPresent(beamStack::add);
        } else {  // '.'
            nextStraightBeam(beam).ifPresent(beamStack::add);
        }
    }

    private Optional<BeamPosition> nextStraightBeam(BeamPosition b) {
        BeamPosition next = switch (b.direction) {
            case RIGHT -> new BeamPosition(b.i, b.j + 1, b.direction);
            case DOWN -> new BeamPosition(b.i + 1, b.j, b.direction);
            case LEFT -> new BeamPosition(b.i, b.j - 1, b.direction);
            default -> new BeamPosition(b.i - 1, b.j, b.direction);
        };
        return isInContraption(next);
    }


    private Optional<BeamPosition> nextLeftReflectedBeam(BeamPosition b) {
        BeamPosition next = switch (b.direction) {
            case RIGHT -> new BeamPosition(b.i + 1, b.j, DOWN);
            case DOWN -> new BeamPosition(b.i, b.j + 1, RIGHT);
            case LEFT -> new BeamPosition(b.i - 1, b.j, UP);
            default -> new BeamPosition(b.i, b.j - 1, LEFT);
        };
        return isInContraption(next);
    }

    private Optional<BeamPosition> nextRightReflectedBeam(BeamPosition b) {
        BeamPosition next = switch (b.direction) {
            case RIGHT -> new BeamPosition(b.i - 1, b.j, UP);
            case DOWN -> new BeamPosition(b.i, b.j - 1, LEFT);
            case LEFT -> new BeamPosition(b.i + 1, b.j, DOWN);
            default -> new BeamPosition(b.i, b.j + 1, RIGHT);
        };
        return isInContraption(next);
    }

    private Optional<BeamPosition> isInContraption(BeamPosition beam) {
        if (MatrixUtils.isIndexInMatrix(contraption, beam.i, beam.j)) {
            return Optional.of(beam);
        }
        return Optional.empty();
    }

    private record BeamPosition(int i, int j, int direction) {
    }


    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(new BeamReflection("day16/test.txt").countEnergizedTilesForUpRight());
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new BeamReflection("day16/input.txt").countEnergizedTilesForUpRight());

        System.out.println("== TEST 2 ==");
        System.out.println(new BeamReflection("day16/test.txt").bestCountEnergizedTiles());
        System.out.println("== SOLUTION 2 ==");
        System.out.println(new BeamReflection("day16/input.txt").bestCountEnergizedTiles());
    }
}
