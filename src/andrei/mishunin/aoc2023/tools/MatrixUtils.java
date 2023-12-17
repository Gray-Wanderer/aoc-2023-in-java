package andrei.mishunin.aoc2023.tools;

public class MatrixUtils {
    private MatrixUtils() {
    }

    public static <T> boolean isIndexInMatrix(T[][] matrix, IntPair ij) {
        return ij != null && isIndexInMatrix(matrix, ij.i(), ij.j());
    }

    public static <T> boolean isIndexInMatrix(T[][] matrix, int i, int j) {
        return i >= 0 && i < matrix.length && j >= 0 && j < matrix[0].length;
    }

    public static boolean isIndexInMatrix(char[][] matrix, IntPair ij) {
        return ij != null && isIndexInMatrix(matrix, ij.i(), ij.j());
    }

    public static boolean isIndexInMatrix(char[][] matrix, int i, int j) {
        return i >= 0 && i < matrix.length && j >= 0 && j < matrix[0].length;
    }

    public static boolean isIndexInMatrix(int[][] matrix, IntPair ij) {
        return ij != null && isIndexInMatrix(matrix, ij.i(), ij.j());
    }

    public static boolean isIndexInMatrix(int[][] matrix, int i, int j) {
        return i >= 0 && i < matrix.length && j >= 0 && j < matrix[0].length;
    }

    public static char[][] transpose(char[][] matrix) {
        int n = matrix.length;
        int m = matrix[0].length;
        char[][] transposed = new char[m][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
        return transposed;
    }

    public static void transposeSquared(char[][] matrix) {
        int n = matrix.length;
        int m = matrix[0].length;
        if (n != m) {
            throw new RuntimeException("Matrix is not squared");
        }
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < m; j++) {
                char swap = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = swap;
            }
        }
    }

    public static void swapColumns(char[][] matrix) {
        int n = matrix.length;
        int m = matrix[0].length;
        int m2 = m / 2;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m2; j++) {
                char temp = matrix[i][j];
                matrix[i][j] = matrix[i][m - j - 1];
                matrix[i][m - j - 1] = temp;
            }
        }
    }

    public static void rotate(char[][] matrix) {
        transposeSquared(matrix);
        swapColumns(matrix);
    }

    public static int sumCells(int[][] matrix, int i1, int j1, int i2, int j2) {
        int startI = Math.min(i1, i2);
        int startJ = Math.min(j1, j2);
        int endI = Math.max(i1, i2);
        int endJ = Math.max(j1, j2);

        int sum = 0;
        for (int i = startI; i <= endI; i++) {
            for (int j = startJ; j <= endJ; j++) {
                sum += matrix[i][j];
            }
        }
        return sum;
    }

    public static int compareManhattanDistance(IntPair p1, IntPair p2) {
        return p1.i() - p2.i() + p1.j() - p2.j();
    }

    public static int getManhattanDistance(int i, int j, int i2, int j2) {
        return Math.abs(i2 - i) + Math.abs(j2 - j);
    }
}
