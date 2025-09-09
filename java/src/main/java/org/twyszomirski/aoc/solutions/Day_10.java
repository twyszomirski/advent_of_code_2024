package org.twyszomirski.aoc.solutions;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.io.IO.println;

public class Day_10 {

    public static void solve() throws IOException {
        var lines = Files.readAllLines(Paths.get("src/main/resources/input_day_10.txt"));
        println("======== Day 10 ===========");
        part_1(lines);
        part_2(lines);
    }

    private static void part_1(List<String> lines) {
        var data = getData(lines);
        var startingPoints = data.stream().flatMap(Collection::stream).filter(it -> it.value == 0);

        var trails = startingPoints.map(start -> findTrails(data, Collections.singletonList(start)));
        var trailHeads = trails.map(t -> t.stream().map(List::getLast).collect(Collectors.toSet())).map(Collection::size);
        var sum = trailHeads.mapToInt(it -> it).sum();
        println(sum);
    }

    private static void part_2(List<String> lines) {
        var data = getData(lines);
        var startingPoints = data.stream().flatMap(Collection::stream).filter(it -> it.value == 0);

        var trails = startingPoints.map(start -> findTrails(data, Collections.singletonList(start)));
        var sum = trails.map(List::size).mapToInt(it -> it).sum();
        println(sum);
    }

    private static List<List<TrailPoint>> findTrails(List<List<TrailPoint>> grid, List<TrailPoint> head) {
        if (head.size() == 10) {
            return Collections.singletonList(head);
        }
        var result = new ArrayList<List<TrailPoint>>();

        var left = getNeighbour(grid, head.getLast(), -1, 0);
        var right = getNeighbour(grid, head.getLast(), 1, 0);
        var up = getNeighbour(grid, head.getLast(), 0, -1);
        var down = getNeighbour(grid, head.getLast(), 0, 1);

        Stream.of(left, right, up, down).filter(Objects::nonNull).forEach(
                point -> {
                    if (point.value - 1 == head.getLast().value) {
                        var copy = head.stream().collect(Collectors.toList());
                        copy.add(point);
                        result.addAll(findTrails(grid, copy));
                    }
                });
        return result;
    }

    private static TrailPoint getNeighbour(List<List<TrailPoint>> grid, TrailPoint point, int horizontal, int vertical) {
        var column = point.column + horizontal;
        var row = point.row + vertical;

        if (row >= 0 && column >= 0 && row < grid.size() && column < grid.getFirst().size()) {
            return grid.get(row).get(column);
        }

        return null;
    }


    private static List<List<TrailPoint>> getData(List<String> lines) {
        var grid = new ArrayList<List<TrailPoint>>();
        for (int row = 0; row < lines.size(); row++) {
            var currentLine = lines.get(row).split("");
            grid.add(new ArrayList<>());
            for (int column = 0; column < currentLine.length; column++) {
                grid.getLast().add(new TrailPoint(row, column, Objects.equals(currentLine[column], ".") ? -1 : Integer.parseInt(currentLine[column])));
            }
        }
        return grid;
    }

    record TrailPoint(Integer row, Integer column, Integer value) {
    }

}
