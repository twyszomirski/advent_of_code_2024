package org.twyszomirski.aoc.solutions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.io.IO.println;

public class Day_1 {

    public static void solve() throws IOException {
        var lines = Files.readAllLines(Paths.get("src/main/resources/input_day_1.txt"));
        println("======== Day 1 ===========");
        part_1(lines);
        part_2(lines);
    }

    public static void part_1(List<String> lines) {
        var rows = lines.stream()
                .map(it -> Arrays.stream(it.split(" ")).filter(v -> !v.isEmpty()).toList())
                .map(row -> new Pair<>(Long.valueOf(row.getFirst()), Long.valueOf(row.getLast())))
                .toList();


        var leftList = rows.stream().map(Pair::left).sorted().toList();
        var rightList = rows.stream().map(Pair::right).sorted().toList();

        var sum = 0L;
        for (int i = 0; i < leftList.size(); i++) {
            sum += Math.abs(leftList.get(i) - rightList.get(i));
        }

        System.out.println(sum);
    }

    public static void part_2(List<String> lines) {
        var rows = lines.stream()
                .map(it -> Arrays.stream(it.split(" ")).filter(v -> !v.isEmpty()).toList())
                .map(row -> new Pair<>(Long.valueOf(row.getFirst()), Long.valueOf(row.getLast())))
                .toList();


        var leftList = rows.stream().map(Pair::left).sorted().toList();
        var frequency = rows.stream().map(Pair::right).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        var sum = leftList.stream().mapToLong(v -> v * frequency.getOrDefault(v, 0L)).sum();

        println(sum);
    }

    record Pair<T>(T left, T right) { }
}
