package andrei.mishunin.aoc2023.day25;

import andrei.mishunin.aoc2023.tools.InputReader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DisconnectSearcher {
    Map<String, Set<String>> graph = new HashMap<>();

    public DisconnectSearcher(String fileName) {
        List<String> input = InputReader.readAllLines(fileName);
        for (String line : input) {
            if (line.isBlank()) {
                continue;
            }
            String[] kv = line.split(": *");
            String k = kv[0];
            for (String v : kv[1].split(" +")) {
                graph.computeIfAbsent(k, kk -> new HashSet<>()).add(v);
                graph.computeIfAbsent(v, vv -> new HashSet<>()).add(k);
            }
        }
    }

    public int getGroupSizesMultiplicationAfterDisconnection() {
        String mainNodeForGroup = getMostConnectedNode();

        Map<String, Set<String>> collapsedGraph = graph;

        Set<String> mainGroupNodes = getNodesFromShortestLoop(collapsedGraph, mainNodeForGroup);
        mainGroupNodes.add(mainNodeForGroup);

        //Adding nodes to the main group until we can disconnect it
        while (collapsedGraph.get(mainNodeForGroup).size() != 3) {
            Map<String, Integer> connectionsToMainGroup = new HashMap<>();
            collapsedGraph = collapseGraphAndCountConnectionsToMainGroup(mainGroupNodes, mainNodeForGroup, connectionsToMainGroup);

            boolean updated = addNodesToMainGroupGroup(mainGroupNodes, connectionsToMainGroup);
            if (!updated) {
                mainGroupNodes.addAll(getNodesFromShortestLoop(collapsedGraph, mainNodeForGroup));
            }
        }

        int group2Size = collapsedGraph.keySet().size() - 1;
        int group1Size = graph.keySet().size() - group2Size;
        return group1Size * group2Size;
    }

    private String getMostConnectedNode() {
        int maxConnections = 0;
        String node = "";
        for (var kV : graph.entrySet()) {
            if (kV.getValue().size() > maxConnections) {
                node = kV.getKey();
                maxConnections = kV.getValue().size();
            }
        }
        return node;
    }

    private static Set<String> getNodesFromShortestLoop(Map<String, Set<String>> graph, String startNode) {
        Set<String> level1 = graph.get(startNode);
        Set<String> levelCurrent = new HashSet<>();

        for (String l1Node : level1) {
            for (String l2Node : graph.get(l1Node)) {
                if (!l2Node.equals(startNode)) {
                    levelCurrent.add(l2Node);
                }
            }
        }

        if (levelCurrent.isEmpty()) {
            throw new RuntimeException();
        }

        Set<String> connectedNodes = new HashSet<>();
        while (connectedNodes.isEmpty()) {
            Set<String> levelNext = new HashSet<>();
            for (String currentNode : levelCurrent) {
                var nextLevelNodes = graph.get(currentNode);
                if (nextLevelNodes.contains(startNode)) {
                    connectedNodes.add(currentNode);
                } else {
                    levelNext.addAll(nextLevelNodes);
                }
            }
            levelCurrent = levelNext;
        }
        return connectedNodes;
    }

    private Map<String, Set<String>> collapseGraphAndCountConnectionsToMainGroup(
            Set<String> mainGroupNodes,
            String mainNodeForGroup,
            Map<String, Integer> connectionsToUnion
    ) {
        Map<String, Set<String>> collapsedGraph = new HashMap<>();
        for (String k : graph.keySet()) {
            String from = mainGroupNodes.contains(k) ? mainNodeForGroup : k;
            for (String v : graph.get(k)) {
                String to = mainGroupNodes.contains(v) ? mainNodeForGroup : v;
                if (!from.equals(to)) {
                    collapsedGraph.computeIfAbsent(from, f -> new HashSet<>()).add(to);
                    collapsedGraph.computeIfAbsent(to, t -> new HashSet<>()).add(from);
                    if (to.equals(mainNodeForGroup)) {
                        connectionsToUnion.compute(from, (f, c) -> c == null ? 1 : (c + 1));
                    }
                }
            }
        }
        return collapsedGraph;
    }

    private boolean addNodesToMainGroupGroup(Set<String> mainGroupNodes, Map<String, Integer> connectionsToUnion) {
        boolean updated = false;
        for (var nodeAndPower : connectionsToUnion.entrySet()) {
            if (nodeAndPower.getValue() > 1) {
                mainGroupNodes.add(nodeAndPower.getKey());
                updated = true;
            }
        }
        return updated;
    }


    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(new DisconnectSearcher("day25/test.txt").getGroupSizesMultiplicationAfterDisconnection());
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new DisconnectSearcher("day25/input.txt").getGroupSizesMultiplicationAfterDisconnection());
    }
}
