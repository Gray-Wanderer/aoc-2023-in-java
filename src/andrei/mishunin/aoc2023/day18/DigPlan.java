package andrei.mishunin.aoc2023.day18;

import andrei.mishunin.aoc2023.tools.InputReader;

import java.math.BigInteger;
import java.util.List;
import java.util.function.Function;

public class DigPlan {
    private static final int RIGHT = 0b0001;
    private static final int DOWN = 0b0010;
    private static final int LEFT = 0b0100;
    private static final int UP = 0b1000;

    private final List<DigLine> digPlan;

    DigPlan(String file, Function<String, DigLine> digCellParser) {
        this.digPlan = InputReader.readAllLines(file).stream()
                .filter(s -> !s.isBlank())
                .map(digCellParser)
                .toList();
    }

    public String getSquare() {
        int minCol = 0;
        int maxCol = 0;
        int minRow = 0;
        int maxRow = 0;

        int row = 0;
        int col = 0;
        for (DigLine digLine : digPlan) {
            switch (digLine.direction) {
                case RIGHT:
                    col += digLine.count;
                    maxCol = Math.max(maxCol, col);
                    break;
                case LEFT:
                    col -= digLine.count;
                    minCol = Math.min(minCol, col);
                    break;
                case UP:
                    row -= digLine.count;
                    minRow = Math.min(minRow, row);
                    break;
                case DOWN:
                    row += digLine.count;
                    maxRow = Math.max(maxRow, row);
                    break;
                default:
                    throw new RuntimeException();
            }
        }

        int i1 = 0;
        int j1 = 0;
        int i2 = 0;
        int j2 = 0;
        BigInteger square = BigInteger.ZERO;
        long s;
        int prevDir = digPlan.getLast().direction;
        int nextCell = 1;
        for (DigLine digLine : digPlan) {
            int nextDir = digPlan.get(nextCell).direction;
            nextCell++;
            nextCell %= digPlan.size();
            switch (digLine.direction) {
                case RIGHT:
                    j2 += digLine.count;
                    if (prevDir == DOWN) {
                        j2--;
                    }
                    if (nextDir == DOWN) {
                        j2++;
                    }
                    s = (long) (j2 - j1) * (maxRow - minRow - i1 + 1);
                    square = square.add(BigInteger.valueOf(s));
                    j1 = j2;
                    break;
                case LEFT:
                    j2 -= digLine.count;
                    if (prevDir == UP) {
                        j2++;
                    }
                    if (nextDir == UP) {
                        j2--;
                    }
                    s = (long) (j1 - j2) * (maxRow - minRow - i1 + 1);
                    square = square.subtract(BigInteger.valueOf(s));
                    j1 = j2;
                    break;
                case UP:
                    i2 -= digLine.count;
                    if (prevDir == RIGHT) {
                        i2++;
                    }
                    if (nextDir == RIGHT) {
                        i2--;
                    }
                    i1 = i2;
                    break;
                case DOWN:
                    i2 += digLine.count;
                    if (prevDir == LEFT) {
                        i2--;
                    }
                    if (nextDir == LEFT) {
                        i2++;
                    }
                    i1 = i2;
                    break;
                default:
                    throw new RuntimeException();
            }
            prevDir = digLine.direction;
        }

        return square.toString();
    }

    public static void main(String[] args) {
        //62
        System.out.println("== TEST 1 ==");
        System.out.println(new DigPlan("day18/test.txt", DigLine::fromString).getSquare());
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new DigPlan("day18/input.txt", DigLine::fromString).getSquare());

        //952408144115
        System.out.println("== TEST 2 ==");
        System.out.println(new DigPlan("day18/test.txt", DigLine::fromHexString).getSquare());
        System.out.println("== SOLUTION 2 ==");
        System.out.println(new DigPlan("day18/input.txt", DigLine::fromHexString).getSquare());
    }


    private record DigLine(int direction, int count) {
        static DigLine fromString(String s) {
            String[] parts = s.split(" ");
            int direction = switch (parts[0].charAt(0)) {
                case 'R' -> RIGHT;
                case 'L' -> LEFT;
                case 'U' -> UP;
                case 'D' -> DOWN;
                default -> throw new RuntimeException();
            };
            return new DigLine(direction, Integer.parseInt(parts[1]));
        }


        static DigLine fromHexString(String s) {
            String digCode = s.replaceFirst(".*\\(#(.+)\\)", "$1");
            String hex = digCode.substring(0, 5);
            int direction = switch (digCode.charAt(5)) {
                case '0' -> RIGHT;
                case '1' -> DOWN;
                case '2' -> LEFT;
                case '3' -> UP;
                default -> throw new RuntimeException();
            };
            return new DigLine(direction, Integer.parseInt(hex, 16));
        }
    }
}
