package org.twyszomirski.aoc.solutions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.io.IO.println;

public class Day_4 {

    public static void solve() throws IOException {
        var lines = Files.readAllLines(Paths.get("src/main/resources/input_day_4.txt"));
        println("======== Day 4 ===========");
        part_1(lines);
        part_2(lines);
    }

    private static void part_1(List<String> lines) {
        var matrix = lines.stream().map(line -> line.split("")).toList().toArray(new String[lines.size()][]);
        var rows = lines;
        var columns = IntStream.range(0, matrix[0].length)
                .mapToObj(col -> IntStream.range(0, matrix.length)
                        .boxed().map(idx -> matrix[idx][col]).reduce("", (acc, op) -> acc + op)).toList();
        var leftDiag = leftDiag(matrix);
        var rightDiag = rightDiag(matrix);

        var allDiags = Stream.concat(leftDiag.stream(), rightDiag.stream()).toList();
        var rowsAndCols = Stream.concat(rows.stream(), columns.stream()).toList();
        var all = Stream.concat(allDiags.stream(), rowsAndCols.stream()).toList();
        var sum = all.stream().mapToInt(line -> countOccurrences(line, "XMAS") + countOccurrences(line, "SAMX")).sum();

        println(sum);
    }

    private static void part_2(List<String> lines) {
        var matrix = lines.stream().map(line -> line.split("")).toList().toArray(new String[lines.size()][]);
        var count = 0;
        for (int row = 0; row < matrix.length - 2; row++) {
            for (int col = 0; col < matrix[0].length - 2; col++) {
                var leftDiag = leftDiag(matrix, row, col);
                if (leftDiag.startsWith("MAS") || leftDiag.startsWith("SAM")) {
                    var rightDiag = rightDiag(matrix, row, col + 2);
                    if (rightDiag.startsWith("MAS") || rightDiag.startsWith("SAM")) {
                        count++;
                    }
                }
            }
        }
        println(count);
    }

    private static Integer countOccurrences(String source, String toMatch) {
        if (toMatch.length() > source.length()) return 0;

        var count = 0;
        for (int i = 0; i <= source.length() - toMatch.length(); i++) {
            var window = source.substring(i, i + toMatch.length());
            if (window.equals(toMatch)) count++;
        }

        return count;
    }

    private static List<String> leftDiag(String[][] matrix) {
        var onRow = IntStream.range(0, matrix[0].length).mapToObj(col -> leftDiag(matrix, 0, col)).toList();
        var onColumn = IntStream.range(1, matrix.length).mapToObj(row -> leftDiag(matrix, row, 0)).toList();
        return Stream.concat(onRow.stream(), onColumn.stream()).toList();
    }

    private static String leftDiag(String[][] matrix, int row, int col) {
        StringBuilder result = new StringBuilder();
        int r = row, c = col;
        while (c < matrix[0].length && r < matrix.length) {
            result.append(matrix[r][c]);
            r++;
            c++;
        }
        return result.toString();
    }

    private static List<String> rightDiag(String[][] matrix) {
        var onRow = IntStream.range(0, matrix[0].length).boxed().sorted(Collections.reverseOrder())
                .map(col -> rightDiag(matrix, 0, col)).toList();
        var onColumn = IntStream.range(1, matrix.length).mapToObj(row -> rightDiag(matrix, row, matrix[0].length - 1)).toList();
        return Stream.concat(onRow.stream(), onColumn.stream()).toList();
    }

    private static String rightDiag(String[][] matrix, int row, int col) {
        StringBuilder result = new StringBuilder();
        int r = row, c = col;
        while (c >= 0 && r < matrix.length) {
            result.append(matrix[r][c]);
            r++;
            c--;
        }
        return result.toString();
    }


}
