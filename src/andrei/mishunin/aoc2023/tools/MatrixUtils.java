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
}
