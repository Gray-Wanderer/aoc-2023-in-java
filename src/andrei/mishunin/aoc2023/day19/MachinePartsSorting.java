package andrei.mishunin.aoc2023.day19;

import andrei.mishunin.aoc2023.tools.InputReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntPredicate;

public class MachinePartsSorting {
    Map<String, FilterChain> filterChains;
    List<PartRating> partRatings;

    public MachinePartsSorting(String file) {
        List<String> input = InputReader.readAllLines(file);

        filterChains = new HashMap<>();
        var lines = input.iterator();
        while (lines.hasNext()) {
            String line = lines.next();
            if (line.isBlank()) {
                break;
            }
            FilterChain filterChain = new FilterChain(line);
            filterChains.put(filterChain.name, filterChain);
        }

        partRatings = new ArrayList<>();
        while (lines.hasNext()) {
            String line = lines.next();
            if (!line.isBlank()) {
                partRatings.add(new PartRating(line));
            }
        }
    }

    public int getRatingsSum() {
        int sum = 0;
        for (PartRating partRating : partRatings) {
            String state = "in";
            while (!"A".equals(state) && !"R".equals(state)) {
                String nstate = filterChains.get(state).getNextState(partRating);
                state = nstate;
            }
            if ("A".equals(state)) {
                sum += partRating.getSum();
            }
        }
        return sum;
    }


    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(new MachinePartsSorting("day19/test.txt").getRatingsSum());
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new MachinePartsSorting("day19/input.txt").getRatingsSum());
    }

    private static class FilterChain {
        String name;
        List<Filter> chain;
        String lastExit;

        public FilterChain(String input) {
            name = input.replaceAll("^(\\w+)\\{.+", "$1");
            String[] chains = input.replaceAll(".+\\{(.+)}", "$1").split(",");
            this.chain = new ArrayList<>();
            for (int i = 0; i < chains.length - 1; i++) {
                chain.add(new Filter(chains[i]));
            }
            lastExit = chains[chains.length - 1];
        }

        public String getNextState(PartRating p) {
            for (Filter filter : chain) {
                if (filter.test(p)) {
                    return filter.ok;
                }
            }
            return lastExit;
        }
    }

    private static class Filter {
        char category;
        IntPredicate tester;
        String ok;

        public Filter(String input) {
            this.category = input.charAt(0);
            String numberS = input.substring(2, input.indexOf(':'));
            int number = Integer.parseInt(numberS);
            this.tester = switch (input.charAt(1)) {
                case '>' -> t -> t > number;
                case '<' -> t -> t < number;
                default -> throw new RuntimeException();
            };
            this.ok = input.substring(3 + numberS.length());
        }

        public boolean test(PartRating p) {
            return tester.test(p.get(category));
        }
    }

    private static class PartRating {
        int x;
        int m;
        int a;
        int s;

        public PartRating(String input) {
            String[] categories = input.substring(1, input.length() - 1).split(", *");
            for (String category : categories) {
                int value = Integer.parseInt(category.substring(2));
                switch (category.charAt(0)) {
                    case 'x' -> x = value;
                    case 'm' -> m = value;
                    case 'a' -> a = value;
                    case 's' -> s = value;
                    default -> throw new RuntimeException();
                }
            }
        }

        public int get(char c) {
            return switch (c) {
                case 'x' -> x;
                case 'm' -> m;
                case 'a' -> a;
                case 's' -> s;
                default -> throw new RuntimeException();
            };
        }

        public int getSum() {
            return x + m + a + s;
        }
    }

}
