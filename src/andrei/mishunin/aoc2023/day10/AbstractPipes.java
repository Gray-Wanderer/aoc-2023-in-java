package andrei.mishunin.aoc2023.day10;

import andrei.mishunin.aoc2023.tools.InputReader;
import andrei.mishunin.aoc2023.tools.IntPair;
import andrei.mishunin.aoc2023.tools.MatrixUtils;

import java.util.Arrays;
import java.util.Set;

public sealed abstract class AbstractPipes permits Pipes, PipesExpand {
    /*
    '|' is a vertical pipe connecting north and south.
    '-' is a horizontal pipe connecting east and west.
    'L' is a 90-degree bend connecting north and east.
    'J' is a 90-degree bend connecting north and west.
    '7' is a 90-degree bend connecting south and west.
    'F' is a 90-degree bend connecting south and east.
    '.' is ground; there is no pipe in this tile.
    'S' is the starting position of the animal; there is a pipe on this tile, but your sketch doesn't show what shape the pipe has.
     */

    public static final Set<Character> UP = Set.of('|', 'L', 'J');
    public static final Set<Character> DOWN = Set.of('|', '7', 'F');
    public static final Set<Character> LEFT = Set.of('-', '7', 'J');
    public static final Set<Character> RIGHT = Set.of('-', 'L', 'F');

    protected final char[][] pipes;
    protected IntPair start;
    protected int longestCycle = -1;
    protected int[][] maxCyclePath = null;
    protected int enclosedTitles = 0;


    public AbstractPipes(String file) {
        pipes = InputReader.readAllLines(file).stream()
                .filter(s -> !s.isBlank())
                .map(String::toCharArray)
                .toArray(char[][]::new);
    }

    public AbstractPipes calculate() {
        findMaxCycle();
        findEnclosedTitles();
        return this;
    }

    private void findMaxCycle() {
        findStart();
        checkLoop(new IntPair(start.i() + 1, start.j()));
        checkLoop(new IntPair(start.i(), start.j() + 1));
        checkLoop(new IntPair(start.i() - 1, start.j()));
        checkLoop(new IntPair(start.i(), start.j() - 1));
    }

    private void findStart() {
        for (int i = 0; i < pipes.length; i++) {
            for (int j = 0; j < pipes[i].length; j++) {
                if (pipes[i][j] == 'S') {
                    start = new IntPair(i, j);
                    break;
                }
            }
        }
    }

    private void checkLoop(IntPair nextPoint) {
        int[][] distances = getEmptyDistances();
        int len = getLoopLen(distances, nextPoint);
        if (len > longestCycle) {
            longestCycle = len;
            maxCyclePath = distances;
        }
    }

    private int[][] getEmptyDistances() {
        int[][] distances = new int[pipes.length][pipes[0].length];
        for (int[] distance : distances) {
            Arrays.fill(distance, -1);
        }
        distances[start.i()][start.j()] = 0;
        return distances;
    }

    private int getLoopLen(int[][] distances, IntPair current) {
        if (!MatrixUtils.isIndexInMatrix(distances, current)) {
            return -1;
        }
        int len = 1;
        IntPair prev = start;
        distances[current.i()][current.j()] = 1;
        while (distances[current.i()][current.j()] != 0) {
            len++;
            IntPair next = moveNext(prev, current);
            if (isValid(distances, current, next)) {
                if (distances[next.i()][next.j()] < 0) {
                    distances[next.i()][next.j()] = len;
                }
            } else {
                return -1;
            }
            prev = current;
            current = next;
        }
        return len;
    }

    private IntPair moveNext(IntPair prev, IntPair current) {
        switch (pipes[current.i()][current.j()]) {
            case '|':
                if (prev.i() + 1 == current.i()) {
                    return new IntPair(current.i() + 1, current.j());
                } else {
                    return new IntPair(current.i() - 1, current.j());
                }
            case '-':
                if (prev.j() + 1 == current.j()) {
                    return new IntPair(current.i(), current.j() + 1);
                } else {
                    return new IntPair(current.i(), current.j() - 1);
                }
            case 'L':
                if (prev.j() - 1 == current.j()) {
                    return new IntPair(current.i() - 1, current.j());
                } else {
                    return new IntPair(current.i(), current.j() + 1);
                }
            case 'J':
                if (prev.j() + 1 == current.j()) {
                    return new IntPair(current.i() - 1, current.j());
                } else {
                    return new IntPair(current.i(), current.j() - 1);
                }
            case '7':
                if (prev.j() + 1 == current.j()) {
                    return new IntPair(current.i() + 1, current.j());
                } else {
                    return new IntPair(current.i(), current.j() - 1);
                }
            case 'F':
                if (prev.j() - 1 == current.j()) {
                    return new IntPair(current.i() + 1, current.j());
                } else {
                    return new IntPair(current.i(), current.j() + 1);
                }
            default:
                return null;
        }
    }

    private boolean isValid(int[][] distances, IntPair prev, IntPair current) {
        if (!MatrixUtils.isIndexInMatrix(distances, current)) {
            return false;
        }
        if (distances[current.i()][current.j()] > 0) {
            return false;
        }

        Set<Character> fromUp = Set.of('|', 'L', 'J');
        Set<Character> fromDown = Set.of('|', '7', 'F');
        Set<Character> fromLeft = Set.of('-', '7', 'J');
        Set<Character> fromRight = Set.of('-', 'L', 'F');

        char pipe = pipes[current.i()][current.j()];
        switch (pipe) {
            case 'S':
                return true;
            case '|':
                if (prev.i() + 1 == current.i()) {
                    return fromUp.contains(pipe);
                } else {
                    return fromDown.contains(pipe);
                }
            case '-':
                if (prev.j() - 1 == current.j()) {
                    return fromRight.contains(pipe);
                } else {
                    return fromLeft.contains(pipe);
                }
            case 'L':
                if (prev.j() - 1 == current.j()) {
                    return fromUp.contains(pipe);
                } else {
                    return fromRight.contains(pipe);
                }
            case 'J':
                if (prev.j() + 1 == current.j()) {
                    return fromUp.contains(pipe);
                } else {
                    return fromLeft.contains(pipe);
                }
            case '7':
                if (prev.j() + 1 == current.j()) {
                    return fromDown.contains(pipe);
                } else {
                    return fromLeft.contains(pipe);
                }
            case 'F':
                if (prev.j() - 1 == current.j()) {
                    return fromDown.contains(pipe);
                } else {
                    return fromRight.contains(pipe);
                }
            default:
                return false;
        }
    }

    protected static char toPrintablePipeCharacter(char c) {
        return switch (c) {
            case '|' -> '│';
            case '-' -> '─';
            case 'L' -> '└';
            case 'J' -> '┘';
            case '7' -> '┐';
            case 'F' -> '┌';
            case 'S' -> '┼';
            case 'I' -> '░';
            default -> ' ';
        };
    }

    abstract public void printSolution();

    protected abstract void findEnclosedTitles();

    public int getFarthestDistance() {
        return longestCycle / 2 + (longestCycle % 2);
    }

    public int getEnclosedTitles() {
        return enclosedTitles;
    }
}
