package org.twyszomirski.aoc.solutions;

import org.twyszomirski.aoc.utils.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


import static java.io.IO.println;

public class Day_5 {

    public static void solve() throws IOException {
        var lines = Files.readAllLines(Paths.get("src/main/resources/input_day_5.txt"));
        println("======== Day 5 ===========");
        part_1(lines);
        part_2(lines);
    }

    private static void part_1(List<String> lines) {
        var data = readData(lines);

        var mustProceed = data.precedence.stream()
                .collect(Collectors.groupingBy(Pair::left, Collectors.mapping(Pair::right, Collectors.toList())));

        var sum = (Integer) data.pages.stream().filter(it -> isPrecedenceCorrect(it, mustProceed))
                .map(it -> it.get(it.size() / 2)).mapToInt(it -> it).sum();
        println(sum);
    }

    private static void part_2(List<String> lines) {
        var data = readData(lines);

        var mustProceed = data.precedence.stream()
                .collect(Collectors.groupingBy(Pair::left, Collectors.mapping(Pair::right, Collectors.toList())));

        var incorrect = data.pages.stream().filter(it -> !isCorrectOrder(it, mustProceed))
                .map(it -> sorted(it, mustProceed)).mapToInt(it -> it.get(it.size() / 2)).sum();
        println(incorrect);
    }


    private static boolean isPrecedenceCorrect(List<Integer> page, Map<Integer, List<Integer>> precedence) {
        for (int i = 1; i < page.size(); i++) {
            var shouldProceed = precedence.getOrDefault(page.get(i), Collections.emptyList());
            var prefixList = page.subList(0, i);
            var any = prefixList.stream().anyMatch(shouldProceed::contains);
            if (any) {
                return false;
            }
        }
        return true;
    }

    private static boolean isCorrectOrder(List<Integer> page, Map<Integer, List<Integer>> precedence) {
        var sorted = sorted(page, precedence);
        for (int i = 0; i < sorted.size(); i++) {
            if (!sorted.get(i).equals(page.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static List<Integer> sorted(List<Integer> page, Map<Integer, List<Integer>> precedence) {
        return page.stream().sorted((o1, o2) -> {
            var shouldProceed = precedence.getOrDefault(o1, Collections.emptyList());
            return shouldProceed.contains(o2) ? -1 : 1;
        }).toList();
    }

    private static PrintingData readData(List<String> lines) {
        var partitioned = lines.stream().filter(it -> !it.isBlank())
                .collect(Collectors.groupingBy(it -> it.contains("|")));
        var precedence = partitioned.get(true).stream().map(it -> it.split("\\|"))
                .map(it -> new Pair<Integer>(Integer.valueOf(it[0]), Integer.valueOf(it[1]))).toList();
        var pages = partitioned.get(false).stream().map(it -> Arrays.stream(it.split(",")).map(Integer::valueOf).toList()).toList();
        return new PrintingData(precedence, pages);
    }

    record PrintingData(List<Pair<Integer>> precedence, List<List<Integer>> pages) {
    }

}
