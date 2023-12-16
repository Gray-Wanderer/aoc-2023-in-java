package andrei.mishunin.aoc2023.day15;

import andrei.mishunin.aoc2023.tools.InputReader;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LensCalculator {
    public String initializationSequence;

    public LensCalculator(String file) {
        List<String> input = InputReader.readAllLines(file);
        initializationSequence = input.get(0);
    }

    public int getSumOfHashes() {
        return Arrays.stream(initializationSequence.split(","))
                .filter(s -> !s.isBlank())
                .mapToInt(this::calcHash)
                .sum();
    }

    public int placeLensAndGetPowerSum() {
        List<String[]> instructions = Arrays.stream(initializationSequence.split(","))
                .filter(s -> !s.isBlank())
                .map(s -> s.split("[=-]"))
                .toList();


        Map<Integer, LensesBox> lensesInTheBox = new HashMap<>();
        for (String[] instruction : instructions) {
            int box = calcHash(instruction[0]);
            if (instruction.length > 1) {
                lensesInTheBox.computeIfAbsent(box, b -> new LensesBox())
                        .put(instruction[0], Integer.parseInt(instruction[1]));

            } else {
                lensesInTheBox.computeIfAbsent(box, b -> new LensesBox())
                        .remove(instruction[0]);
            }
        }

        int sum = 0;
        for (Integer box : lensesInTheBox.keySet()) {
            var lenses = lensesInTheBox.get(box).labelsAndLenses.values().toArray(Lens[]::new);
            Arrays.sort(lenses, Comparator.comparingInt(l -> l.orderIndex));
            int i = 1;
            for (Lens lens : lenses) {
                sum += (box + 1) * i * lens.power;
                i++;
            }
        }
        return sum;
    }

    private int calcHash(String s) {
        int hash = 0;
        for (char c : s.toCharArray()) {
            hash = ((hash + c) * 17) % 256;
        }
        return hash;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(new LensCalculator("day15/test.txt").getSumOfHashes());
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new LensCalculator("day15/input.txt").getSumOfHashes());

        System.out.println("== TEST 2 ==");
        System.out.println(new LensCalculator("day15/test.txt").placeLensAndGetPowerSum());
        System.out.println("== SOLUTION 2 ==");
        System.out.println(new LensCalculator("day15/input.txt").placeLensAndGetPowerSum());
    }

    private static class LensesBox {
        int index = 0;
        Map<String, Lens> labelsAndLenses = new HashMap<>();

        void put(String label, int power) {
            if (labelsAndLenses.containsKey(label)) {
                labelsAndLenses.get(label).power = power;
            } else {
                labelsAndLenses.put(label, new Lens(index, power));
                index++;
            }
        }

        void remove(String label) {
            labelsAndLenses.remove(label);
        }
    }

    private static class Lens {
        int orderIndex;
        int power;

        public Lens(int orderIndex, int power) {
            this.orderIndex = orderIndex;
            this.power = power;
        }
    }
}
