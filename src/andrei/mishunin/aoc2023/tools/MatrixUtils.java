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
}
