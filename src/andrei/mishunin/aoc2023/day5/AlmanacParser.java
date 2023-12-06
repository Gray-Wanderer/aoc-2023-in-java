package andrei.mishunin.aoc2023.day5;

import andrei.mishunin.aoc2023.tools.InputReader;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlmanacParser {
    private final BigInteger[] seeds;
    private SourceMap[] seedToSoil;
    private SourceMap[] soilToFertilizer;
    private SourceMap[] fertilizerToWater;
    private SourceMap[] waterToLight;
    private SourceMap[] lightToTemperature;
    private SourceMap[] temperatureToHumidity;
    private SourceMap[] humidityToLocation;
    private SourceMap[][] mappers;

    public AlmanacParser(String file) {
        List<String> almanac = InputReader.readAllLines(file);
        seeds = Arrays.stream(almanac.get(0).substring(7).split(" +"))
                .map(BigInteger::new)
                .toList().toArray(new BigInteger[0]);

        int i = 0;
        for (; i < almanac.size(); i++) {
            if (almanac.get(i).isBlank()) {
                continue;
            }
            if (almanac.get(i).startsWith("seed-to-soil")) {
                seedToSoil = readMap(almanac, i + 1);
                i += seedToSoil.length;
            } else if (almanac.get(i).startsWith("soil-to-fertilizer")) {
                soilToFertilizer = readMap(almanac, i + 1);
                i += soilToFertilizer.length;
            } else if (almanac.get(i).startsWith("fertilizer-to-water")) {
                fertilizerToWater = readMap(almanac, i + 1);
                i += fertilizerToWater.length;
            } else if (almanac.get(i).startsWith("water-to-light")) {
                waterToLight = readMap(almanac, i + 1);
                i += waterToLight.length;
            } else if (almanac.get(i).startsWith("light-to-temperature")) {
                lightToTemperature = readMap(almanac, i + 1);
                i += lightToTemperature.length;
            } else if (almanac.get(i).startsWith("temperature-to-humidity")) {
                temperatureToHumidity = readMap(almanac, i + 1);
                i += temperatureToHumidity.length;
            } else if (almanac.get(i).startsWith("humidity-to-location")) {
                humidityToLocation = readMap(almanac, i + 1);
                i += humidityToLocation.length;
            }
        }
        this.mappers = new SourceMap[][]{
                seedToSoil,
                soilToFertilizer,
                fertilizerToWater,
                waterToLight,
                lightToTemperature,
                temperatureToHumidity,
                humidityToLocation
        };
    }

    private static SourceMap[] readMap(List<String> almanac, int from) {
        List<SourceMap> sourceMaps = new ArrayList<>();
        for (int i = from; i < almanac.size(); i++) {
            if (almanac.get(i).isBlank()) {
                break;
            }
            String[] map = Arrays.stream(almanac.get(i).split(" +"))
                    .toList().toArray(new String[0]);
            sourceMaps.add(new SourceMap(map));
        }
        SourceMap[] map = sourceMaps.toArray(new SourceMap[0]);
        Arrays.sort(map);
        return map;
    }

    public BigInteger getSimpleClosestLocation() {
        var mapping = Arrays.stream(seeds);
        for (SourceMap[] mapper : mappers) {
            mapping = mapping.map(s -> mapSourceToTarget(mapper, s));
        }
        return mapping.min(BigInteger::compareTo).orElse(BigInteger.ZERO);
    }

    private BigInteger mapSourceToTarget(SourceMap[] sourceMaps, BigInteger source) {
        for (SourceMap map : sourceMaps) {
            if (source.compareTo(map.source) >= 0 && source.compareTo(map.sourceMax) <= 0) {
                return map.target.add(source).subtract(map.source);
            }
        }
        return source;
    }

    public BigInteger getRangedClosestLocation() {
        List<BigInteger[]> sourceRange = new ArrayList<>();
        for (int i = 0; i < seeds.length; i += 2) {
            sourceRange.add(new BigInteger[]{seeds[i], seeds[i].add(seeds[i + 1]).subtract(BigInteger.ONE)});
        }

        for (SourceMap[] mapper : mappers) {
            List<BigInteger[]> mappingResult = new ArrayList<>();
            for (BigInteger[] range : sourceRange) {
                mappingResult.addAll(mapSourceRangeToTarget(mapper, range));
            }
            sourceRange = mappingResult;
        }

        return sourceRange.stream()
                .map(s -> s[0])
                .min(BigInteger::compareTo)
                .orElse(BigInteger.ZERO);
    }

    private List<BigInteger[]> mapSourceRangeToTarget(SourceMap[] sourceMaps, BigInteger[] sourceRange) {
        List<BigInteger[]> result = new ArrayList<>();
        BigInteger start = sourceRange[0];
        BigInteger end = sourceRange[1];
        for (SourceMap map : sourceMaps) {
            if (start.compareTo(map.source) < 0) {
                result.add(new BigInteger[]{start, map.source.subtract(BigInteger.ONE)});
                start = map.source;
            }
            if (start.compareTo(map.source) >= 0 && start.compareTo(map.sourceMax) <= 0) {
                BigInteger targetStart = map.target.add(start).subtract(map.source);
                if (end.compareTo(map.sourceMax) <= 0) {
                    BigInteger targetEnd = map.target.add(end).subtract(map.source);
                    result.add(new BigInteger[]{targetStart, targetEnd});
                    return result;
                } else {
                    result.add(new BigInteger[]{targetStart, map.targetMax});
                    start = map.sourceMax.add(BigInteger.ONE);
                }
            }
        }
        if (start.compareTo(end) <= 0) {
            result.add(new BigInteger[]{start, end});
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println("== TEST 1 ==");
        System.out.println(new AlmanacParser("day5/test.txt").getSimpleClosestLocation());
        System.out.println("== SOLUTION 1 ==");
        System.out.println(new AlmanacParser("day5/input.txt").getSimpleClosestLocation());

        System.out.println("== TEST 2 ==");
        System.out.println(new AlmanacParser("day5/test.txt").getRangedClosestLocation());
        System.out.println("== SOLUTION 2 ==");
        System.out.println(new AlmanacParser("day5/input.txt").getRangedClosestLocation());
    }

    private static class SourceMap implements Comparable<SourceMap> {
        private final BigInteger target;
        private final BigInteger source;
        private final BigInteger sourceMax;
        private final BigInteger targetMax;

        public SourceMap(BigInteger target, BigInteger source, BigInteger count) {
            this.target = target;
            this.source = source;
            this.sourceMax = source.add(count).subtract(BigInteger.ONE);
            this.targetMax = target.add(count).subtract(BigInteger.ONE);
        }

        private SourceMap(String[] map) {
            this(new BigInteger(map[0]), new BigInteger(map[1]), new BigInteger(map[2]));
        }

        @Override
        public int compareTo(SourceMap o) {
            return source.compareTo(o.source);
        }
    }
}
