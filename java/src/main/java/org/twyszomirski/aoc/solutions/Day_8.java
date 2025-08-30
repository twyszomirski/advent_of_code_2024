package org.twyszomirski.aoc.solutions;


import org.twyszomirski.aoc.utils.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.io.IO.println;

public class Day_8 {

    public static void solve() throws IOException {
        var lines = Files.readAllLines(Paths.get("src/main/resources/input_day_8.txt"));
        println("======== Day 8 ===========");
        part_1(lines);
        part_2(lines);
    }

    private static void part_1(List<String> lines) {
        var data = readData(lines);
        var grouped = data.stream().filter(AntennaPoint::notEmpty)
                .collect(Collectors.groupingBy(AntennaPoint::symbol));

        var count = grouped.values().stream()
                .flatMap(antenna -> calculatePairs(antenna).stream())
                .flatMap(pairs ->
                        calculateAntinodes(pairs, lines.size(), lines.getFirst().length()).stream())
                .collect(Collectors.toSet()).size();
        println(count);
    }

    private static void part_2(List<String> lines) {
        var data = readData(lines);
        var grouped = data.stream().filter(AntennaPoint::notEmpty)
                .collect(Collectors.groupingBy(AntennaPoint::symbol));

        var antinodes = grouped.values().stream()
                .flatMap(antenna -> calculatePairs(antenna).stream())
                .flatMap(pairs ->
                        calcInfiniteAntiNodes(pairs, lines.size(), lines.getFirst().length()).stream())
                .collect(Collectors.toSet());

        var antinodesOfPairs = grouped.values().stream()
                .flatMap(antenna -> calculatePairs(antenna).stream())
                .flatMap(it -> Stream.of(it.left(), it.right()))
                .map(it -> new AntennaPoint(it.row, it.column, "#")).collect(Collectors.toSet());

        antinodes.addAll(antinodesOfPairs);

        println(antinodes.size());
    }

    private static List<AntennaPoint> readData(List<String> lines) {
        return IntStream.range(0, lines.size()).mapToObj(row -> {
            var split = lines.get(row).split("");
            return IntStream.range(0, split.length).mapToObj(col ->
                    new AntennaPoint(row, col, split[col])).toList();
        }).flatMap(Collection::stream).toList();
    }

    private static List<Pair<AntennaPoint>> calculatePairs(List<AntennaPoint> points) {
        if (points.size() < 2) return Collections.emptyList();
        List<Pair<AntennaPoint>> current = points.stream().skip(1).map(p -> new Pair<>(points.getFirst(), p)).toList();
        var result = new ArrayList<>(current);
        result.addAll(calculatePairs(points.stream().skip(1).toList()));
        return result;
    }

    private static List<AntennaPoint> calculateAntinodes(Pair<AntennaPoint> pair, Integer numOfRows, Integer numOfColumns) {
        return Stream.of(calcUpperAntinode(pair), calcLowerAntinode(pair))
                .filter(it -> it.inMatrix(numOfRows, numOfColumns))
                .toList();
    }

    private static List<AntennaPoint> calcInfiniteAntiNodes(Pair<AntennaPoint> pair, Integer numOfRows, Integer numOfColumns) {
        var upper = new ArrayList<AntennaPoint>();
        var nextPair = pair;
        do {
            upper.add(calcUpperAntinode(nextPair));
            nextPair = new Pair(upper.getLast(), nextPair.left());
        } while (upper.getLast().inMatrix(numOfRows, numOfColumns));

        var lower = new ArrayList<AntennaPoint>();
        nextPair = pair;
        do {
            lower.add(calcLowerAntinode(nextPair));
            nextPair = new Pair(nextPair.right(), lower.getLast());
        } while (lower.getLast().inMatrix(numOfRows, numOfColumns));


        var res = new ArrayList<>(upper);
        res.addAll(lower);
        return res.stream().filter(it -> it.inMatrix(numOfRows, numOfColumns)).toList();
    }

    private static AntennaPoint calcUpperAntinode(Pair<AntennaPoint> antennas) {
        var differenceRow = (antennas.left().row - antennas.right().row);
        var differenceCol = (antennas.left().column - antennas.right().column);
        return new AntennaPoint(antennas.left().row + differenceRow, antennas.left().column + differenceCol, "#");
    }

    private static AntennaPoint calcLowerAntinode(Pair<AntennaPoint> antennas) {
        var differenceRow = (antennas.left().row - antennas.right().row);
        var differenceCol = (antennas.left().column - antennas.right().column);
        return new AntennaPoint(antennas.right().row - differenceRow, antennas.right().column - differenceCol, "#");
    }

    record AntennaPoint(Integer row, Integer column, String symbol) {
        private boolean notEmpty() {
            return !".".equals(symbol);
        }

        private boolean inMatrix(Integer numOfRows, Integer numOfColumns) {
            return row >= 0 && column >= 0 && row < numOfRows && column < numOfColumns;
        }
    }


}
