package andrei.mishunin.aoc2023.day22;

import andrei.mishunin.aoc2023.tools.InputReader;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BrickDesintegrator {
    private final List<Brick> bricks;
    private final Brick[][][] brickColumn;

    public BrickDesintegrator(String fileName) {
        bricks = InputReader.readAllLines(fileName).stream()
                .filter(s -> !s.isBlank())
                .map(Brick::new)
                .collect(Collectors.toList());
        int maxX = bricks.stream().mapToInt(r -> r.upCorner.x).max().orElse(0);
        int maxY = bricks.stream().mapToInt(r -> r.upCorner.y).max().orElse(0);
        int maxZ = bricks.stream().mapToInt(r -> r.upCorner.z).max().orElse(0);
        brickColumn = new Brick[maxX + 1][maxY + 1][maxZ + 1];

        fallDownBricksAndSetIds();
    }

    private void fallDownBricksAndSetIds() {
        bricks.sort(Comparator.comparingInt(r -> r.downCorner.z));
        for (Brick brick : bricks) {
            fallBrickDown(brickColumn, brick);
        }

        bricks.sort((r1, r2) -> Integer.compare(r2.upCorner.z, r1.upCorner.z));
        for (Brick brick : bricks) {
            markDownBricksRemovable(brick);
        }
    }

    public long countSafeBrickToDisintegrate() {
        return bricks.stream().filter(r -> r.removable).count();
    }

    public long countSumOfBricksWouldFallIfDisintegrate() {
        long countRemovable = 0;
        for (Brick brick : bricks) {
            countRemovable += countUpperDependedBricks(brick);
        }

        return countRemovable;
    }

    private void fallBrickDown(Brick[][][] brickColumn, Brick brick) {
        int z = brick.downCorner.z;
        while (z > 0 && isRowEmpty(brickColumn, brick, z)) {
            z--;
        }
        z++;
        int height = brick.upCorner.z - brick.downCorner.z;
        brick.downCorner.z = z;
        brick.upCorner.z = z + height;
        placeBrick(brickColumn, brick);
    }

    private boolean isRowEmpty(Brick[][][] brickColumn, Brick brick, int row) {
        for (int i = brick.downCorner.x; i <= brick.upCorner.x; i++) {
            for (int j = brick.downCorner.y; j <= brick.upCorner.y; j++) {
                if (brickColumn[i][j][row] != null) {
                    return false;
                }
            }
        }
        return true;
    }

    private void placeBrick(Brick[][][] brickColumn, Brick brick) {
        for (int x = brick.downCorner.x; x <= brick.upCorner.x; x++) {
            for (int y = brick.downCorner.y; y <= brick.upCorner.y; y++) {
                for (int z = brick.downCorner.z; z <= brick.upCorner.z; z++) {
                    if (brickColumn[x][y][z] != null) {
                        throw new RuntimeException();
                    }
                    brickColumn[x][y][z] = brick;
                }
            }
        }
    }

    private void markDownBricksRemovable(Brick brick) {
        Set<Brick> downBrickIds = new HashSet<>();
        int nextZ = brick.downCorner.z - 1;
        if (nextZ == 0) {
            return;
        }

        for (int x = brick.downCorner.x; x <= brick.upCorner.x; x++) {
            for (int y = brick.downCorner.y; y <= brick.upCorner.y; y++) {
                if (brickColumn[x][y][nextZ] != null) {
                    downBrickIds.add(brickColumn[x][y][nextZ]);
                }
            }
        }

        brick.downBricks = downBrickIds;
        for (Brick supportBrick : downBrickIds) {
            supportBrick.upBricks.add(brick);
            if (downBrickIds.size() == 1) {
                supportBrick.removable = false;
            }
        }
    }

    private int countUpperDependedBricks(Brick brick) {
        if (brick.removable) {
            return 0;
        }

        Set<Brick> allUpperDependedBrickIds = new HashSet<>();
        allUpperDependedBrickIds.add(brick);

        Set<Brick> currentBrickIds = new HashSet<>();
        currentBrickIds.add(brick);

        while (!currentBrickIds.isEmpty()) {
            Set<Brick> nextUpperDependedBrickIds = new HashSet<>();
            for (Brick currentBrick : currentBrickIds) {
                for (Brick upBrick : currentBrick.upBricks) {
                    if (allUpperDependedBrickIds.containsAll(upBrick.downBricks) && !allUpperDependedBrickIds.contains(upBrick)) {
                        nextUpperDependedBrickIds.add(upBrick);
                    }
                }
            }
            currentBrickIds = nextUpperDependedBrickIds;
            allUpperDependedBrickIds.addAll(currentBrickIds);
        }

        return allUpperDependedBrickIds.size() - 1;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(new BrickDesintegrator("day22/test.txt").countSafeBrickToDisintegrate());
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new BrickDesintegrator("day22/input.txt").countSafeBrickToDisintegrate());

        System.out.println("== TEST 2 ==");
        System.out.println(new BrickDesintegrator("day22/test.txt").countSumOfBricksWouldFallIfDisintegrate());
        System.out.println("== SOLUTION 2 ==");
        System.out.println(new BrickDesintegrator("day22/input.txt").countSumOfBricksWouldFallIfDisintegrate());
    }

    private static class Brick {
        Point3D downCorner;
        Point3D upCorner;
        boolean removable = true;
        Set<Brick> upBricks = new HashSet<>();
        Set<Brick> downBricks = new HashSet<>();

        public Brick(String s) {
            String[] split = s.split("~");
            this.downCorner = new Point3D(split[0]);
            this.upCorner = new Point3D(split[1]);
        }
    }

    private static class Point3D {
        int x;
        int y;
        int z;

        public Point3D(String s) {
            String[] split = s.split(", *");
            this.x = Integer.parseInt(split[0]);
            this.y = Integer.parseInt(split[1]);
            this.z = Integer.parseInt(split[2]);
        }
    }
}
