package andrei.mishunin.aoc2023.day10;

public final class PipesExpand extends AbstractPipes {

    private char[][] extendedPipes = null;

    public PipesExpand(String file) {
        super(file);
    }

    @Override
    protected void findEnclosedTitles() {
        createExtendedPipes();
        findEnclosedTitlesInExtendedPath();
    }

    private void createExtendedPipes() {
        int n = pipes.length * 2 + 1;
        int m = pipes[0].length * 2 + 1;
        extendedPipes = new char[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (i == 0 || j == 0 || i == n - 1 || j == m - 1) {
                    extendedPipes[i][j] = '.';
                } else {
                    extendedPipes[i][j] = 'I';
                }
            }
        }
        for (int i = 0; i < pipes.length; i++) {
            for (int j = 0; j < pipes[i].length; j++) {
                int eI = i * 2 + 1;
                int eJ = j * 2 + 1;
                char pipe = pipes[i][j];
                if (maxCyclePath[i][j] < 0) {
                    pipe = '.';
                }
                extendedPipes[eI][eJ] = pipe;
                switch (pipe) {
                    case 'S':
                        extendedPipes[eI - 1][eJ] = extendedPipes[eI + 1][eJ] = '|';
                        extendedPipes[eI][eJ - 1] = extendedPipes[eI][eJ + 1] = '-';
                        break;
                    case '|':
                        extendedPipes[eI - 1][eJ] = extendedPipes[eI + 1][eJ] = '|';
                        break;
                    case '-':
                        extendedPipes[eI][eJ - 1] = extendedPipes[eI][eJ + 1] = '-';
                        break;
                    case 'L':
                        extendedPipes[eI - 1][eJ] = '|';
                        extendedPipes[eI][eJ + 1] = '-';
                        break;
                    case 'J':
                        extendedPipes[eI - 1][eJ] = '|';
                        extendedPipes[eI][eJ - 1] = '-';
                        break;
                    case '7':
                        extendedPipes[eI + 1][eJ] = '|';
                        extendedPipes[eI][eJ - 1] = '-';
                        break;
                    case 'F':
                        extendedPipes[eI + 1][eJ] = '|';
                        extendedPipes[eI][eJ + 1] = '-';
                        break;
                    case '.':
                        extendedPipes[eI][eJ] = 'I';
                }
            }
        }
    }

    private void findEnclosedTitlesInExtendedPath() {
        int n = extendedPipes.length;
        int m = extendedPipes[0].length;

        boolean hasChanges = true;
        while (hasChanges) {
            hasChanges = false;
            for (int i = 1; i < n - 1; i++) {
                for (int j = 1; j < m - 1; j++) {
                    if (extendedPipes[i][j] != 'I') {
                        continue;
                    }
                    if (extendedPipes[i - 1][j] == '.'
                            || extendedPipes[i][j + 1] == '.'
                            || extendedPipes[i + 1][j] == '.'
                            || extendedPipes[i][j - 1] == '.') {
                        hasChanges = true;
                        extendedPipes[i][j] = '.';
                    }
                }
            }
        }

        for (int i = 1; i < extendedPipes.length; i += 2) {
            for (int j = 1; j < extendedPipes[i].length; j += 2) {
                if (extendedPipes[i][j] == 'I') {
                    enclosedTitles++;
                }
            }
        }
    }

    @Override
    public void printSolution() {
        for (int i = 1; i < extendedPipes.length; i += 2) {
            for (int j = 1; j < extendedPipes[i].length; j += 2) {
                System.out.print(toPrintablePipeCharacter(extendedPipes[i][j]));
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        var input = new PipesExpand("day10/input.txt").calculate();

        System.out.println("== TEST 1 ==");
        System.out.println(new PipesExpand("day10/test.txt").calculate().getFarthestDistance());
        System.out.println("== SOLUTION 1 ==");
        System.out.println(input.getFarthestDistance());

        System.out.println("== TEST 2 ==");
        System.out.println(new PipesExpand("day10/test2.txt").calculate().getEnclosedTitles());
        System.out.println("== SOLUTION 2 ==");
        System.out.println(input.getEnclosedTitles());

        input.printSolution();
    }
}
