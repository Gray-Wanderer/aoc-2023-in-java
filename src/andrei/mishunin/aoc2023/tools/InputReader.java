package andrei.mishunin.aoc2023.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class InputReader {
    public static List<String> readAllLines(String inputFile) {
        try {
            return readAllLines(InputReader.class.getResourceAsStream("../" + inputFile));
        } catch (Exception e) {
            System.out.printf("Error on process fil '%s': '%s'%n", inputFile, e.getMessage());
            e.printStackTrace(System.out);
            throw new RuntimeException(e);
        }
    }

    private static List<String> readAllLines(InputStream resource) {
        return new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.toList());
    }
}
