package org.twyszomirski.aoc.solutions;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


import static java.io.IO.println;

public class Day_11 {

    public static void solve() throws IOException {
        var lines = Files.readAllLines(Paths.get("src/main/resources/input_day_11.txt"));
        println("======== Day 11 ===========");
        part_1(lines);
        part_2(lines);
    }

    private static void part_1(List<String> lines) {
        var stones = Arrays.stream(lines.getFirst().split(" ")).mapToLong(Long::valueOf).boxed().toList();
        var sum = stones.stream().mapToLong(s -> blinkStone(s, 25, new HashMap<>())).sum();
        println(sum + stones.size());
    }

    private static void part_2(List<String> lines) {
        var stones = Arrays.stream(lines.getFirst().split(" ")).mapToLong(Long::valueOf).boxed().toList();
        var sum = stones.stream().mapToLong(s -> blinkStone(s, 75, new HashMap<>())).sum();
        println(sum + stones.size());
    }

    private static Long blinkStone(Long stone, Integer times, Map<String, Long> cache) {

        var cached = cache.get(getKey(stone,times));
        if(cached != null){
            return cached;
        }

        if (times == 1) {
            return (long)blinkStone(stone).size() - 1;
        }
        var result = 0L;
        var stones = blinkStone(stone);
        result += blinkStone(stone).size() - 1;
        result += stones.stream().map(s -> blinkStone(s, times - 1, cache)).mapToLong(it -> it).sum();

        cache.put(getKey(stone, times), result);

        return result;
    }

    private static String getKey(Long stone, Integer times){
        return stone.toString() + "_" + times;
    }

    private static List<Long> blinkStone(Long stone) {
        if (stone == 0L) return Collections.singletonList(1L);
        var asString = stone.toString();

        if (asString.length() % 2 == 0) {
            var split = asString.split("");
            var left = Long.valueOf(Arrays.stream(split).limit(split.length / 2).collect(Collectors.joining("")));
            var right = Long.valueOf(Arrays.stream(split).skip(split.length / 2).collect(Collectors.joining("")));
            return List.of(left, right);
        } else {
            return Collections.singletonList(stone * 2024);
        }
    }

}
