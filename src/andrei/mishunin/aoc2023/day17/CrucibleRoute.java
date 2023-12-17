package andrei.mishunin.aoc2023.day17;

import andrei.mishunin.aoc2023.tools.InputReader;
import andrei.mishunin.aoc2023.tools.IntPair;
import andrei.mishunin.aoc2023.tools.MatrixUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;

public class CrucibleRoute {
    private static final int RIGHT = 0b0001;
    private static final int DOWN = 0b0010;
    private static final int LEFT = 0b0100;
    private static final int UP = 0b1000;
    private static final int LR = LEFT | RIGHT;
    private static final int UD = UP | DOWN;
    int[][] heatLossMap;
    private final int crucibleInertia;
    private final int crucibleMovements;

    public CrucibleRoute(String file, int crucibleInertia, int crucibleMovements) {
        heatLossMap = InputReader.readAllLines(file).stream()
                .filter(s -> !s.isBlank())
                .map(s -> s.chars().map(c -> c - '0').toArray())
                .toArray(int[][]::new);
        this.crucibleInertia = crucibleInertia;
        this.crucibleMovements = crucibleMovements;
    }

    public int getShortestPathLen() {
        int n = heatLossMap.length;
        int m = heatLossMap[0].length;
        HeatLoss[][] visited = new HeatLoss[n][m];
        for (HeatLoss[] heatLosses : visited) {
            for (int i = 0; i < heatLosses.length; i++) {
                heatLosses[i] = new HeatLoss();
            }
        }

        PriorityQueue<IntPair> queue = new PriorityQueue<>(MatrixUtils::compareManhattanDistance);
        visited[0][0].add(new CrucibleMovement(LEFT, 0, 0));
        visited[0][0].add(new CrucibleMovement(DOWN, 0, 0));
        addNextMovementWithHeat(visited, queue, 0, 0, 0, crucibleInertia, new CrucibleMovement(LEFT, crucibleInertia, 0));
        addNextMovementWithHeat(visited, queue, 0, 0, crucibleInertia, 0, new CrucibleMovement(DOWN, crucibleInertia, 0));
        HeatLoss end = visited[n - 1][m - 1];

        while (!queue.isEmpty()) {
            IntPair ij = queue.poll();
            int i = ij.i();
            int j = ij.j();
            List<CrucibleMovement> crucibleMovements = visited[ij.i()][ij.j()].movements;
            for (CrucibleMovement crucibleMovement : crucibleMovements) {
                if (end.getMin() > crucibleMovement.heat + MatrixUtils.getManhattanDistance(i, j, n - 1, m - 1)) {
                    createAndAddNextMovements(visited, queue, i, j, crucibleMovement);
                }
            }
        }

        return end.getMin();
    }

    private void createAndAddNextMovements(HeatLoss[][] visited, Collection<IntPair> queue, int i, int j, CrucibleMovement crucibleMovement) {
        int heat = crucibleMovement.heat;
        if ((crucibleMovement.direction & LR) > 0) {
            addNextMovementWithHeat(visited, queue, i, j, i - crucibleInertia, j, new CrucibleMovement(UP, crucibleInertia, heat));
            addNextMovementWithHeat(visited, queue, i, j, i + crucibleInertia, j, new CrucibleMovement(DOWN, crucibleInertia, heat));

            if (crucibleMovement.speed < crucibleMovements) {
                if ((crucibleMovement.direction & LEFT) > 0) {
                    addNextMovementWithHeat(visited, queue, i, j, i, j - 1, new CrucibleMovement(LEFT, crucibleMovement.speed + 1, heat));
                }
                if ((crucibleMovement.direction & RIGHT) > 0) {
                    addNextMovementWithHeat(visited, queue, i, j, i, j + 1, new CrucibleMovement(RIGHT, crucibleMovement.speed + 1, heat));
                }
            }
        }
        if ((crucibleMovement.direction & UD) > 0) {
            addNextMovementWithHeat(visited, queue, i, j, i, j - crucibleInertia, new CrucibleMovement(LEFT, crucibleInertia, heat));
            addNextMovementWithHeat(visited, queue, i, j, i, j + crucibleInertia, new CrucibleMovement(RIGHT, crucibleInertia, heat));

            if (crucibleMovement.speed < crucibleMovements) {
                if ((crucibleMovement.direction & UP) > 0) {
                    addNextMovementWithHeat(visited, queue, i, j, i - 1, j, new CrucibleMovement(UP, crucibleMovement.speed + 1, heat));
                }
                if ((crucibleMovement.direction & DOWN) > 0) {
                    addNextMovementWithHeat(visited, queue, i, j, i + 1, j, new CrucibleMovement(DOWN, crucibleMovement.speed + 1, heat));
                }
            }
        }
    }

    private void addNextMovementWithHeat(
            HeatLoss[][] visited, Collection<IntPair> queue,
            int i, int j, int nextI, int nextJ,
            CrucibleMovement crucibleMovement
    ) {
        if (MatrixUtils.isIndexInMatrix(heatLossMap, nextI, nextJ)) {
            crucibleMovement.heat += MatrixUtils.sumCells(heatLossMap, i, j, nextI, nextJ) - heatLossMap[i][j];
            if (visited[nextI][nextJ].add(crucibleMovement)) {
                queue.add(new IntPair(nextI, nextJ));
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(new CrucibleRoute("day17/test.txt", 1, 3).getShortestPathLen());
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new CrucibleRoute("day17/input.txt", 1, 3).getShortestPathLen());

        System.out.println("== TEST 2 ==");
        System.out.println(new CrucibleRoute("day17/test.txt", 4, 10).getShortestPathLen());
        System.out.println(new CrucibleRoute("day17/test2.txt", 4, 10).getShortestPathLen());
        System.out.println("== SOLUTION 2 ==");
        System.out.println(new CrucibleRoute("day17/input.txt", 4, 10).getShortestPathLen());
    }

    private static class HeatLoss {
        List<CrucibleMovement> movements = new ArrayList<>();

        public boolean add(CrucibleMovement c) {
            boolean add = true;
            int newDirection = c.direction;
            for (CrucibleMovement movement : movements) {
                if (movement.heat == c.heat && movement.speed == c.speed && (movement.direction & c.direction) == 0) {
                    newDirection |= movement.direction;
                }
                if (movement.heat <= c.heat && movement.speed <= c.speed && (c.direction & movement.direction) > 0) {
                    add = false;
                }
            }

            if (add) {
                List<CrucibleMovement> newMovements = new ArrayList<>();
                c.direction = newDirection;
                for (CrucibleMovement movement : movements) {
                    if (movement.heat < c.heat || movement.speed < c.speed || (c.direction & movement.direction) == 0) {
                        newMovements.add(movement);
                    }
                }
                newMovements.add(c);
                movements = newMovements;
            }
            return add;
        }

        public int getMin() {
            int min = Integer.MAX_VALUE;
            for (var h : movements) {
                if (h.heat < min) {
                    min = h.heat;
                }
            }
            return min;
        }
    }

    private static class CrucibleMovement {
        int direction;
        int speed;
        int heat;

        public CrucibleMovement(int direction, int speed, int heat) {
            this.direction = direction;
            this.speed = speed;
            this.heat = heat;
        }
    }
}
