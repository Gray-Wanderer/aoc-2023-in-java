package andrei.mishunin.aoc2023.day8;

import andrei.mishunin.aoc2023.tools.InputReader;
import andrei.mishunin.aoc2023.tools.MyMath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HauntedWasteland {
    private final char[] instructions;
    private final Map<String, Nodes> nodes;

    public HauntedWasteland(String file) {
        List<String> document = InputReader.readAllLines(file);
        this.instructions = document.get(0).toCharArray();
        this.nodes = new HashMap<>();

        for (int i = 2; i < document.size(); i++) {
            if (document.get(i).isBlank()) {
                continue;
            }
            String[] from = document.get(i).split(" = ");
            String[] to = from[1].split(", +");
            nodes.put(from[0], new Nodes(
                    to[0].substring(1),
                    to[1].substring(0, 3))
            );
        }
    }

    public int findDistance() {
        int direction = 0;
        String currentNode = "AAA";
        int steps = 0;
        while (!"ZZZ".equals(currentNode)) {
            if (instructions[direction] == 'L') {
                currentNode = nodes.get(currentNode).l;
            } else {
                currentNode = nodes.get(currentNode).r;
            }
            steps++;
            direction++;
            direction %= instructions.length;
        }
        return steps;
    }

    public long findGhostDistance() {
        List<Integer> start = new ArrayList<>();
        for (String s : nodes.keySet()) {
            if (s.charAt(2) == 'A') {
                start.add(findOneGhostDistance(s));
            }
        }

        long distance = start.get(0);
        for (int i = 1; i < start.size(); i++) {
            distance = MyMath.getLeastCommonMultiple(distance, start.get(i));
        }
        return distance;
    }

    private int findOneGhostDistance(String currentNode) {
        int direction = 0;
        int steps = 0;
        while (currentNode.charAt(2) != 'Z') {
            if (instructions[direction] == 'L') {
                currentNode = nodes.get(currentNode).l;
            } else {
                currentNode = nodes.get(currentNode).r;
            }
            steps++;
            direction++;
            direction %= instructions.length;
        }

        return steps;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(new HauntedWasteland("day8/test.txt").findDistance());
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new HauntedWasteland("day8/input.txt").findDistance());

        System.out.println("== TEST 2 ==");
        System.out.println(new HauntedWasteland("day8/test2.txt").findGhostDistance());
        System.out.println("== SOLUTION 2 ==");
        System.out.println(new HauntedWasteland("day8/input.txt").findGhostDistance());
    }

    private record Nodes(String l, String r) {
    }
}
