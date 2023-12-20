package andrei.mishunin.aoc2023.day20;

import andrei.mishunin.aoc2023.tools.InputReader;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class ModulesComunication {
    private static final String BROADCAST_ID = "broadcaster";
    private static final String FINAL_MACHINE_ID = "rx";
    Map<String, Module> modules = new HashMap<>();
    FinalMachine finalMachine;
    long lowPulses = 0;
    long highPulses = 0;

    public ModulesComunication(String fileName) {
        finalMachine = new FinalMachine();
        modules.put(FINAL_MACHINE_ID, finalMachine);

        InputReader.readAllLines(fileName).forEach(this::parseInputLine);

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

    public long pressTheButtonUntilFinalMachineIsOff() {
        var signalQueue = new ArrayDeque<Signal>();
        long pressCount = 0;
        while (finalMachine.isOff()) {
            pressTheButton(signalQueue);
            pressCount++;
        }
        return pressCount;
    }

    public long pressTheButtonAndCountSignals(int pressCount) {
        var signalQueue = new ArrayDeque<Signal>();
        for (int i = 0; i < pressCount; i++) {
            pressTheButton(signalQueue);
        }
        return lowPulses * highPulses;
    }

    private void pressTheButton(Queue<Signal> signalQueue) {
        signalQueue.clear();
        signalQueue.add(new Signal("button", BROADCAST_ID, -1));
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
        //32000000
        System.out.println(new ModulesComunication("day20/test.txt").pressTheButtonAndCountSignals(1000));
        //11687500
        System.out.println(new ModulesComunication("day20/test2.txt").pressTheButtonAndCountSignals(1000));
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new ModulesComunication("day20/input.txt").pressTheButtonAndCountSignals(1000));

        System.out.println("== SOLUTION 2 ==");
        System.out.println(new ModulesComunication("day20/input.txt").pressTheButtonUntilFinalMachineIsOff());
    }

    private record Signal(String from, String to, int pulse) {
    }

    private interface Module {
        void addPulses(Queue<Signal> signalQueue, Signal signal);

        String getId();

        String[] getDestinations();

        default void addInput(String string) {
        }
    }

    private abstract class AbstractModule implements Module {
        final String id;
        final String[] destinations;

        public AbstractModule(String id, String[] destinations) {
            this.id = id;
            this.destinations = destinations;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String[] getDestinations() {
            return destinations;
        }

        protected void broadcast(Queue<Signal> signalQueue, int pulse) {
            for (String destination : destinations) {
                signalQueue.add(new Signal(id, destination, pulse));
            }
        }
    }

    private class Stuff extends AbstractModule {
        public Stuff(String id) {
            super(id, new String[]{});
        }

        @Override
        public void addPulses(Queue<Signal> signalQueue, Signal signal) {
        }
    }

    private class FinalMachine extends AbstractModule {
        boolean off;

        public FinalMachine() {
            super(FINAL_MACHINE_ID, new String[]{});
            off = true;
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

    private class Broadcast extends AbstractModule {
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

    private class Conjunction extends AbstractModule {
        final Map<String, Integer> receivedPulses = new HashMap<>();
        int highPulses = 0;

        public Conjunction(String id, String[] destinations) {
            super(id, destinations);
            this.highPulses = 0;
        }

        @Override
        public void addInput(String string) {
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

    private class FlipFlop extends AbstractModule {
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
