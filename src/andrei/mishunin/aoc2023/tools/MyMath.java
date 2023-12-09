package andrei.mishunin.aoc2023.tools;

public class MyMath {
    private MyMath() {
    }

    /**
     * Finding Least common multiple
     * Using the greatest common divisor
     *
     * @param n1 first number
     * @param n2 second number
     * @return Least common multiple
     */
    public static long getLeastCommonMultiple(long n1, long n2) {
        return n1 * (n2 / getGreatestCommonDivisor(n1, n2));
    }

    /**
     * Finding Greatest common divisor
     * Stein’s Algorithm (Binary GCD algorithm): <a href="https://en.wikipedia.org/wiki/Binary_GCD_algorithm">Wiki</a>
     *
     * @param n1 first number
     * @param n2 second number
     * @return Greatest common divisor
     */
    public static long getGreatestCommonDivisor(long n1, long n2) {
        if (n1 == 0) {
            return n2;
        }

        if (n2 == 0) {
            return n1;
        }

        int n;
        for (n = 0; ((n1 | n2) & 1) == 0; n++) {
            n1 >>= 1;
            n2 >>= 1;
        }

        while ((n1 & 1) == 0) {
            n1 >>= 1;
        }

        do {
            while ((n2 & 1) == 0) {
                n2 >>= 1;
            }

            if (n1 > n2) {
                long temp = n1;
                n1 = n2;
                n2 = temp;
            }
            n2 = (n2 - n1);
        } while (n2 != 0);
        return n1 << n;
    }
}
