package andrei.mishunin.aoc2023.day20;

import andrei.mishunin.aoc2023.tools.InputReader;
import andrei.mishunin.aoc2023.tools.MyMath;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class ModulesCommunication {
    private static final String BROADCAST_ID = "broadcaster";
    private static final String FINAL_MACHINE_ID = "rx";
    Map<String, Module> modules = new HashMap<>();
    FinalMachine finalMachine;
    long lowPulses = 0;
    long highPulses = 0;

    public ModulesCommunication(String fileName) {
        finalMachine = new FinalMachine();
        modules.put(FINAL_MACHINE_ID, finalMachine);

        InputReader.readAllLines(fileName).stream()
                .filter(s -> !s.isBlank())
                .forEach(this::parseInputLine);

        for (Module module : new ArrayList<>(modules.values())) {
            for (String destination : module.getDestinations()) {
                if (modules.containsKey(destination)) {
                    modules.get(destination).addInput(module.getId());
                } else {
                    System.out.println("Destination module is not found: " + destination);
                    modules.put(destination, new Stuff(destination));
                }
            }
        }
    }

    private void parseInputLine(String line) {
        String[] parts = line.split(" -> ");
        String[] destinations = parts[1].split(", *");
        if (BROADCAST_ID.equals(parts[0])) {
            modules.put(BROADCAST_ID, new Broadcast(destinations));
        } else {
            char type = parts[0].charAt(0);
            String id = parts[0].substring(1);
            if (type == '%') {
                modules.put(id, new FlipFlop(id, destinations));
            } else if (type == '&') {
                modules.put(id, new Conjunction(id, destinations));
            } else {
                throw new RuntimeException();
            }
        }
    }

    public long findClicksCountToTurnOnFinalMachine() {
        int i = 0;
        Map<String, FinalMachine> coreToFinal = new HashMap<>();
        if (finalMachine.getSources().size() > 1) {
            throw new RuntimeException();
        }
        Module accumulator = modules.get(finalMachine.getSources().iterator().next());
        for (String source : accumulator.getSources()) {
            Module inverter = modules.get(source);
            String cycleCore = inverter.getSources().iterator().next();
            FinalMachine machine = new FinalMachine(Integer.toString(i));
            modules.get(cycleCore).addDestinations(machine.getId());
            machine.addInput(cycleCore);
            coreToFinal.put(cycleCore, machine);
            modules.put(machine.getId(), machine);
            i++;
        }

        long combinations = 1;
        for (String startId : modules.get(BROADCAST_ID).getDestinations()) {
            Module start = modules.get(startId);
            if (start.getDestinations().size() > 2) {
                throw new RuntimeException();
            }
            String cycleCore = null;
            for (String startDestination : start.getDestinations()) {
                if (modules.get(startDestination) instanceof Conjunction) {
                    cycleCore = startDestination;
                    break;
                }
            }

            long cycle = pressTheButtonUntilMachineIsOff(startId, coreToFinal.get(cycleCore));
            combinations = MyMath.getLeastCommonMultiple(combinations, cycle);
        }
        return combinations;
    }

    private long pressTheButtonUntilMachineIsOff(String enterId, FinalMachine finalModule) {
        var signalQueue = new ArrayDeque<Signal>();
        long pressCount = 0;
        while (finalModule.isOff() && pressCount >= 0) {
            pressTheButton(signalQueue, enterId);
            pressCount++;
        }
        return pressCount;
    }

    public long pressTheButtonAndCountSignals(int pressCount) {
        var signalQueue = new ArrayDeque<Signal>();
        for (int i = 0; i < pressCount; i++) {
            pressTheButton(signalQueue, BROADCAST_ID);
        }
        return lowPulses * highPulses;
    }

    private void pressTheButton(Queue<Signal> signalQueue, String broadcastId) {
        signalQueue.clear();
        signalQueue.add(new Signal("button", broadcastId, -1));
        while (!signalQueue.isEmpty()) {
            Signal signal = signalQueue.poll();
            if (signal.pulse == -1) {
                lowPulses++;
            } else {
                highPulses++;
            }
            modules.get(signal.to).addPulses(signalQueue, signal);
        }
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(new ModulesCommunication("day20/test.txt").pressTheButtonAndCountSignals(1000));
        System.out.println(new ModulesCommunication("day20/test2.txt").pressTheButtonAndCountSignals(1000));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new ModulesCommunication("day20/input.txt").pressTheButtonAndCountSignals(1000));

        System.out.println("== TEST 2 ==");
        System.out.println(new ModulesCommunication("day20/test3.txt").findClicksCountToTurnOnFinalMachine());
        System.out.println(new ModulesCommunication("day20/test4.txt").findClicksCountToTurnOnFinalMachine());
        System.out.println("== SOLUTION 2 ==");
        System.out.println(new ModulesCommunication("day20/input.txt").findClicksCountToTurnOnFinalMachine());
    }

    private record Signal(String from, String to, int pulse) {
    }

    private interface Module {
        void addPulses(Queue<Signal> signalQueue, Signal signal);

        String getId();

        List<String> getDestinations();

        void addDestinations(String d);

        Set<String> getSources();

        default void addInput(String string) {
        }
    }

    private static abstract class AbstractModule implements Module {
        final String id;
        final List<String> destinations;
        final Set<String> sources = new HashSet<>();

        public AbstractModule(String id, String[] destinations) {
            this.id = id;
            this.destinations = new ArrayList<>(Arrays.asList(destinations));
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public List<String> getDestinations() {
            return destinations;
        }

        @Override
        public void addDestinations(String d) {
            destinations.add(d);
        }

        @Override
        public void addInput(String string) {
            sources.add(string);
        }

        public Set<String> getSources() {
            return sources;
        }

        protected void broadcast(Queue<Signal> signalQueue, int pulse) {
            for (String destination : destinations) {
                signalQueue.add(new Signal(id, destination, pulse));
            }
        }
    }

    private static class Stuff extends AbstractModule {
        public Stuff(String id) {
            super(id, new String[]{});
        }

        @Override
        public void addPulses(Queue<Signal> signalQueue, Signal signal) {
        }
    }

    private static class FinalMachine extends AbstractModule {
        boolean off;

        public FinalMachine() {
            this(FINAL_MACHINE_ID);
        }

        public FinalMachine(String id) {
            super(id, new String[]{});
            this.off = true;
        }

        @Override
        public void addPulses(Queue<Signal> signalQueue, Signal signal) {
            if (signal.pulse == -1) {
                off = false;
            }
        }

        public boolean isOff() {
            return off;
        }
    }

    private static class Broadcast extends AbstractModule {
        public Broadcast(String[] destinations) {
            super(BROADCAST_ID, destinations);
        }

        @Override
        public void addPulses(Queue<Signal> signalQueue, Signal signal) {
            for (String destination : destinations) {
                signalQueue.add(new Signal(id, destination, signal.pulse));
            }
        }
    }

    private static class Conjunction extends AbstractModule {
        final Map<String, Integer> receivedPulses = new HashMap<>();
        int highPulses = 0;

        public Conjunction(String id, String[] destinations) {
            super(id, destinations);
            this.highPulses = 0;
        }

        @Override
        public void addInput(String string) {
            super.addInput(string);
            receivedPulses.put(string, -1);
        }

        @Override
        public void addPulses(Queue<Signal> signalQueue, Signal signal) {
            int prevPulse = receivedPulses.put(signal.from, signal.pulse);
            if (prevPulse != signal.pulse) {
                highPulses += signal.pulse;
            }

            if (highPulses == receivedPulses.size()) {
                broadcast(signalQueue, -1);
            } else {
                broadcast(signalQueue, 1);
            }
        }
    }

    private static class FlipFlop extends AbstractModule {
        boolean on;

        public FlipFlop(String id, String[] destinations) {
            super(id, destinations);
            on = false;
        }

        @Override
        public void addPulses(Queue<Signal> signalQueue, Signal signal) {
            if (signal.pulse == -1) {
                on = !on;
                broadcast(signalQueue, on ? 1 : -1);
            }
        }
    }
}
