package org.twyszomirski.aoc.solutions;

import org.twyszomirski.aoc.utils.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.io.IO.println;

public class Day_3 {

    public static void solve() throws IOException {
        var lines = Files.readAllLines(Paths.get("src/main/resources/input_day_3.txt"));
        println("======== Day 3 ===========");
        part_1(lines);
        part_2(lines);
    }

    public static void part_1(List<String> lines) {
        var input = String.join("", lines);
        var mulPattern = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)");
        Matcher m = mulPattern.matcher(input);

        var results = new ArrayList<Pair<Integer>>();
        while (m.find()) {
            var group = m.group(2);
            results.add(new Pair<>(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2))));
        }

        var sum = results.stream().mapToInt(it -> it.left() * it.right()).sum();
        println(sum);

    }

    public static void part_2(List<String> lines) {
        var input = String.join("", lines);
        var dontPattern = Pattern.compile("don't\\(\\)");
        Matcher dontMatcher = dontPattern.matcher(input);

        var doPattern = Pattern.compile("do\\(\\)");
        Matcher doMatcher = doPattern.matcher(input);

        var results = new ArrayList<String>();
        var currentIdx = 0;

        while (currentIdx < input.length()) {
            var nextDont = dontMatcher.find(currentIdx) ? dontMatcher.start() : input.length();
            results.add(input.substring(currentIdx, nextDont));
            currentIdx = doMatcher.find(nextDont) ? doMatcher.start() : input.length();
        }

        part_1(results);
    }

}
