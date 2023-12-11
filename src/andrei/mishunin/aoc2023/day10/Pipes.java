package andrei.mishunin.aoc2023.day10;

import andrei.mishunin.aoc2023.tools.MatrixUtils;

import java.util.HashSet;
import java.util.Set;

public final class Pipes extends AbstractPipes {
    public Pipes(String file) {
        super(file);
    }

    @Override
    protected void findEnclosedTitles() {
        replaceStartPipe();

        for (int i = 0; i < pipes.length; i++) {
            boolean insideCycle = false;
            for (int j = 0; j < pipes[i].length; j++) {
                if (maxCyclePath[i][j] >= 0) {
                    if (DOWN.contains(pipes[i][j])) {
                        insideCycle = !insideCycle;
                    }
                } else if (insideCycle) {
                    enclosedTitles++;
                }
            }
        }
    }

    private void replaceStartPipe() {
        Set<Character> allDirections = new HashSet<>();
        allDirections.addAll(UP);
        allDirections.addAll(DOWN);
        allDirections.addAll(LEFT);
        allDirections.addAll(RIGHT);
        if (!MatrixUtils.isIndexInMatrix(maxCyclePath, start.i() + 1, start.j())
                || maxCyclePath[start.i() + 1][start.j()] < 0) {
            allDirections.removeAll(UP);
        }
        if (!MatrixUtils.isIndexInMatrix(maxCyclePath, start.i() - 1, start.j())
                || maxCyclePath[start.i() - 1][start.j()] < 0) {
            allDirections.removeAll(DOWN);
        }
        if (!MatrixUtils.isIndexInMatrix(maxCyclePath, start.i(), start.j() + 1)
                || maxCyclePath[start.i()][start.j() + 1] < 0) {
            allDirections.removeAll(RIGHT);
        }
        if (!MatrixUtils.isIndexInMatrix(maxCyclePath, start.i(), start.j() - 1)
                || maxCyclePath[start.i()][start.j() - 1] < 0) {
            allDirections.removeAll(LEFT);
        }
        pipes[start.i()][start.j()] = allDirections.stream().findFirst().orElse('.');
    }

    public void printSolution() {
        for (int i = 0; i < pipes.length; i++) {
            boolean insideCycle = false;
            for (int j = 0; j < pipes[i].length; j++) {
                if (maxCyclePath[i][j] >= 0) {
                    if (DOWN.contains(pipes[i][j])) {
                        insideCycle = !insideCycle;
                    }
                    System.out.print(toPrintablePipeCharacter(pipes[i][j]));
                } else {
                    System.out.print(toPrintablePipeCharacter(insideCycle ? 'I' : '.'));
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        var input = new Pipes("day10/input.txt").calculate();

        System.out.println("== TEST 1 ==");
        System.out.println(new Pipes("day10/test.txt").calculate().getFarthestDistance());
        System.out.println("== SOLUTION 1 ==");
        System.out.println(input.getFarthestDistance());

        System.out.println("== TEST 2 ==");
        System.out.println(new Pipes("day10/test2.txt").calculate().getEnclosedTitles());
        System.out.println("== SOLUTION 2 ==");
        System.out.println(input.getEnclosedTitles());

        input.printSolution();
    }
}
