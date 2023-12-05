package andrei.mishunin.aoc2023.day4;

import andrei.mishunin.aoc2023.tools.InputReader;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Scratchcard {
    public Scratchcard() {
    }

    public int calcPoints(String file) {
        return getRawScratchcards(file)
                .mapToInt(s -> this.getCardScore(s, this::doubleScoreCounter))
                .sum();
    }

    private Stream<String> getRawScratchcards(String file) {
        return InputReader.readAllLines(file).stream()
                .map(s -> s.replaceAll("^Card +\\d+: ", ""));
    }

    private int getCardScore(String rawScratchcard, IntToIntFunction scoreCalculator) {
        String[] game = rawScratchcard.split(" \\| ");
        Set<Integer> winnerCodes = Arrays.stream(game[0].split(" +"))
                .filter(s -> !s.isBlank())
                .map(Integer::valueOf)
                .collect(Collectors.toSet());

        int score = 0;
        for (String s : game[1].split(" +")) {
            if (s.isBlank()) {
                continue;
            }
            if (winnerCodes.contains(Integer.valueOf(s))) {
                score = scoreCalculator.apply(score);
            }
        }
        return score;
    }

    private int doubleScoreCounter(int score) {
        return score == 0 ? 1 : score << 1;
    }

    public int calcCards(String file) {
        List<String> cards = getRawScratchcards(file).toList();

        int[] cardCount = new int[cards.size()];
        Arrays.fill(cardCount, 1);

        for (int i = 0; i < cardCount.length; i++) {
            int newCards = getCardScore(cards.get(i), this::incrementScoreCounter);
            for (int j = 1; j <= newCards; j++) {
                cardCount[i + j] += cardCount[i];
            }
        }
        return Arrays.stream(cardCount).sum();
    }

    private int incrementScoreCounter(int score) {
        return score + 1;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(new Scratchcard().calcPoints("day4/test.txt"));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new Scratchcard().calcPoints("day4/input.txt"));

        System.out.println("== TEST 2 ==");
        System.out.println(new Scratchcard().calcCards("day4/test.txt"));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(new Scratchcard().calcCards("day4/input.txt"));
    }

    private interface IntToIntFunction {
        int apply(int v);
    }
}
