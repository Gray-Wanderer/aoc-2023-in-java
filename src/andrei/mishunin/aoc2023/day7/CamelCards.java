package andrei.mishunin.aoc2023.day7;

import andrei.mishunin.aoc2023.tools.InputReader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CamelCards {
    private final HandAndBid[] handAndBids;
    private final boolean useJokers;

    public CamelCards(String file, boolean useJokers) {
        this.useJokers = useJokers;
        this.handAndBids = InputReader.readAllLines(file).stream()
                .filter(s -> !s.isBlank())
                .map(s -> {
                    var split = s.split(" +");
                    return new HandAndBid(split[0], Integer.parseInt(split[1]));
                })
                .toArray(HandAndBid[]::new);
    }

    public long getTotalWinningsResult() {
        Arrays.sort(handAndBids, (h1, h2) -> compareHands(h1.card, h2.card));

        int result = 0;
        for (int i = 0; i < handAndBids.length; i++) {
            result += (i + 1) * handAndBids[i].bid;
        }
        return result;
    }

    private int compareHands(String s1, String s2) {
        int i1 = handToRank(s1);
        int i2 = handToRank(s2);
        if (i1 == i2) {
            for (int i = 0; i < s1.length(); i++) {
                char c1 = normalizeCard(s1.charAt(i));
                char c2 = normalizeCard(s2.charAt(i));
                if (c1 != c2) {
                    return c1 - c2;
                }
            }
        }
        return i1 - i2;
    }

    private char normalizeCard(char c) {
        return switch (c) {
            case 'T' -> '9' + 1;
            case 'J' -> useJokers ? '1' : ('9' + 2);
            case 'Q' -> '9' + 3;
            case 'K' -> '9' + 4;
            case 'A' -> '9' + 5;
            default -> c;
        };
    }

    private int handToRank(String card) {
        Map<Character, Integer> combinations = new HashMap<>();
        int jokers = 0;
        for (char c : card.toCharArray()) {
            if (useJokers && c == 'J') {
                jokers++;
            } else {
                combinations.compute(c, (k, v) -> v == null ? 1 : v + 1);
            }
        }
        if (combinations.size() <= 1) {
            return 6;
        }
        if (combinations.size() == 2) {
            int max = combinations.values().stream().mapToInt(Integer::intValue).max().orElse(0) + jokers;
            if (max == 4) {
                return 5;
            } else {
                return 4;
            }
        }
        if (combinations.size() == 3) {
            int max = combinations.values().stream().mapToInt(Integer::intValue).max().orElse(0) + jokers;
            if (max == 3) {
                return 3;
            } else {
                return 2;
            }
        }
        if (combinations.size() == 4) {
            return 1;
        }
        return 0;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(new CamelCards("day7/test.txt", false).getTotalWinningsResult());
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new CamelCards("day7/input.txt", false).getTotalWinningsResult());

        System.out.println("== TEST 2 ==");
        System.out.println(new CamelCards("day7/test.txt", true).getTotalWinningsResult());
        System.out.println("== SOLUTION 2 ==");
        System.out.println(new CamelCards("day7/input.txt", true).getTotalWinningsResult());
    }

    private record HandAndBid(String card, int bid) {
    }
}
