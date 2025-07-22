package org.twyszomirski.aoc.solutions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.io.IO.println;

public class Day_2 {

    public static void solve() throws IOException {
        var lines = Files.readAllLines(Paths.get("src/main/resources/input_day_2.txt"));
        println("======== Day 2 ===========");
        part_1(lines);
        part_2(lines);
    }

    public static void part_1(List<String> lines) {
        var count = lines.stream().map(it -> Arrays.stream(it.split(" ")).map(Long::valueOf).toList())
                .filter(Day_2::isGoodReport).count();

        println(count);
    }

    private static boolean isGoodReport(List<Long> report) {
        var direction = report.get(0) - report.get(1) > 0 ? 1 : -1;

        for (int i = 0; i < report.size() - 1; i++) {
            var distance = Math.abs(report.get(i) - report.get(i + 1));
            if (distance < 1 || distance > 3) {
                return false;
            }
            var currentDir = report.get(i) - report.get(i + 1) > 0 ? 1 : -1;
            if (direction != currentDir) {
                return false;
            }
        }
        return true;
    }

    public static void part_2(List<String> lines) {
        var count = lines.stream().map(it -> Arrays.stream(it.split(" ")).map(Long::valueOf).toList())
                .filter(Day_2::isGoodReportWithReplacement).count();

        println(count);
    }

    private static boolean isGoodReportWithReplacement(List<Long> report) {
        if (isGoodReport(report)) {
            return true;
        }

        for (int i = 1; i < report.size(); i++) {
            var newList = new ArrayList<>(report);
            newList.remove(i);
            if (isGoodReport(newList)) {
                return true;
            }
        }
        return false;
    }
}
