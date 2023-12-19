package andrei.mishunin.aoc2023.day19;

import andrei.mishunin.aoc2023.tools.InputReader;
import andrei.mishunin.aoc2023.tools.IntPair;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MachinePartsVariations {
    Map<String, FilterChain> filterChains;

    public MachinePartsVariations(String file) {
        List<String> input = InputReader.readAllLines(file);

        filterChains = new HashMap<>();
        for (String line : input) {
            if (line.isBlank()) {
                break;
            }
            FilterChain filterChain = new FilterChain(line);
            filterChains.put(filterChain.name, filterChain);
        }
    }

    public String sumAllCombinationsForRange(int from, int to) {
        IntPair range = new IntPair(from, to);
        return filterChains.get("in").getNextState(filterChains, new PartRatingRanges(range, range, range, range)).toString();
    }


    public static void main(String[] args) {
        //167409079868000
        System.out.println("== TEST 2 ==");
        System.out.println(new MachinePartsVariations("day19/test.txt").sumAllCombinationsForRange(1, 4000));
        System.out.println("== SOLUTION 2 ==");
        System.out.println(new MachinePartsVariations("day19/input.txt").sumAllCombinationsForRange(1, 4000));
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

        public BigInteger getNextState(Map<String, FilterChain> filterChains, PartRatingRanges p) {
            BigInteger sum = BigInteger.ZERO;
            for (Filter filter : chain) {
                SplitPartRatingRanges splitted = filter.split(p);
                if (splitted.ok != null) {
                    if ("A".equals(filter.okExit) || "R".equals(filter.okExit)) {
                        if ("A".equals(filter.okExit)) {
                            sum = sum.add(splitted.ok.getSum());
                        }
                    } else {
                        sum = sum.add(filterChains.get(filter.okExit).getNextState(filterChains, splitted.ok));
                    }
                }
                p = splitted.notOk;
                if (p == null) {
                    break;
                }
            }

            if (p != null) {
                if ("A".equals(lastExit) || "R".equals(lastExit)) {
                    if ("A".equals(lastExit)) {
                        sum = sum.add(p.getSum());
                    }
                } else {
                    sum = sum.add(filterChains.get(lastExit).getNextState(filterChains, p));
                }
            }

            return sum;
        }
    }


    //qhd{a<3942:bfp,s<1085:zkg,a<3963:tkr,pvm}
    private static class Filter {
        char category;
        char predicate;
        int number;
        String okExit;

        public Filter(String input) {
            this.category = input.charAt(0);
            String numberS = input.substring(2, input.indexOf(':'));
            number = Integer.parseInt(numberS);
            predicate = input.charAt(1);
            this.okExit = input.substring(3 + numberS.length());
        }

        public SplitPartRatingRanges split(PartRatingRanges ranges) {
            SplitPartRatingRanges pair = new SplitPartRatingRanges();
            IntPair current = ranges.get(category);
            if (current.i() <= number && current.j() >= number) {
                if (predicate == '>') {
                    pair.ok = PartRatingRanges.create(ranges, category, new IntPair(number + 1, current.j()));
                    pair.notOk = PartRatingRanges.create(ranges, category, new IntPair(current.i(), number));
                } else {
                    pair.ok = PartRatingRanges.create(ranges, category, new IntPair(current.i(), number - 1));
                    pair.notOk = PartRatingRanges.create(ranges, category, new IntPair(number, current.j()));
                }
            } else if (current.i() > number) {
                if (predicate == '>') {
                    pair.ok = ranges;
                } else {
                    pair.notOk = ranges;
                }
            } else {
                if (predicate == '>') {
                    pair.notOk = ranges;
                } else {
                    pair.ok = ranges;
                }
            }
            return pair;
        }
    }

    private static class SplitPartRatingRanges {
        PartRatingRanges ok;
        PartRatingRanges notOk;
    }

    private static class PartRatingRanges {
        IntPair x;
        IntPair m;
        IntPair a;
        IntPair s;

        public PartRatingRanges(IntPair x, IntPair m, IntPair a, IntPair s) {
            this.x = x;
            this.m = m;
            this.a = a;
            this.s = s;
        }

        public static PartRatingRanges create(PartRatingRanges original, char c, IntPair i) {
            if (i.i() > i.j()) {
                return null;
            }
            PartRatingRanges clone = new PartRatingRanges(original.x, original.m, original.a, original.s);
            switch (c) {
                case 'x' -> clone.x = i;
                case 'm' -> clone.m = i;
                case 'a' -> clone.a = i;
                case 's' -> clone.s = i;
                default -> throw new RuntimeException();
            }
            return clone;
        }

        public IntPair get(char c) {
            return switch (c) {
                case 'x' -> x;
                case 'm' -> m;
                case 'a' -> a;
                case 's' -> s;
                default -> throw new RuntimeException();
            };
        }

        public BigInteger getSum() {
            return BigInteger.valueOf(distance(x) * distance(m) * distance(a) * distance(s));
        }

        private static long distance(IntPair p) {
            return p.j() - p.i() + 1;
        }
    }

}
