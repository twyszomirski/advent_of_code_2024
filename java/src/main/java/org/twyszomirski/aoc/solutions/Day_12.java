package org.twyszomirski.aoc.solutions;


import io.vavr.Tuple2;
import io.vavr.collection.Stream;
import org.twyszomirski.aoc.utils.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;

import static java.io.IO.println;

public class Day_12 {

    public static void solve() throws IOException {
        var lines = Files.readAllLines(Paths.get("src/main/resources/input_day_12.txt"));
        println("======== Day 12 ===========");
        part_1(lines);
        part_2(lines);
    }

    private static void part_1(List<String> lines) {
        var data = getData(lines);
        var processed = new HashSet<Plant>();
        var regions = data.stream().flatMap(Collection::stream)
                .filter(it -> !processed.contains(it))
                .map(it -> findRegion(data, it, processed));

        var sum = regions.map(region -> new Pair<>((long) region.size(),
                        region.stream().mapToLong(plant -> (long) countBorders(data, plant)).sum()))
                .mapToLong(metric -> metric.left() * metric.right()).sum();
        println(sum);
    }

    private static void part_2(List<String> lines) {
        var data = getData(lines);
        var processed = new HashSet<Plant>();
        var regions = data.stream().flatMap(Collection::stream)
                .filter(it -> !processed.contains(it))
                .map(it -> findRegion(data, it, processed));

        var regionBorders = regions.map(region -> new Tuple2<>(region, dropBordersOnGrid(data, region)));
        var metrics = regionBorders.map(regionAndBorders -> new Tuple2<>(regionAndBorders._1.size(), (scanHorizontal(regionAndBorders._2) + scanVertical(regionAndBorders._2))));
        var sum = metrics.mapToLong(m -> m._1 * m._2).sum();

        println(sum);
    }

    private static List<Plant> findRegion(List<List<Plant>> grid, Plant plant, Set<Plant> processed) {
        var left = getNeighbour(grid, plant, -1, 0);
        var right = getNeighbour(grid, plant, 1, 0);
        var up = getNeighbour(grid, plant, 0, -1);
        var down = getNeighbour(grid, plant, 0, 1);

        var result = new ArrayList<Plant>();
        processed.add(plant);
        result.add(plant);

        result.addAll(Stream.of(left, right, up, down).filter(Objects::nonNull)
                .filter(it -> Objects.equals(it.symbol, plant.symbol))
                .filter(it -> !processed.contains(it))
                .map(next -> {
                    processed.add(next);
                    return next;
                })
                .flatMap(next ->
                        Stream.ofAll(findRegion(grid, next, processed))

                ).toJavaList());

        return result;
    }

    private static Integer countBorders(List<List<Plant>> grid, Plant plant) {
        return getBorders(grid, plant).size();
    }

    private static List<List<List<Border>>> dropBordersOnGrid(List<List<Plant>> grid, List<Plant> region) {
        return IntStream.range(0, grid.size()).mapToObj(r -> {
                    return IntStream.range(0, grid.getFirst().size()).mapToObj(c -> {
                        var p = region.stream().filter(x -> x.row == r && x.column == c).findFirst();
                        return p.map(plant -> getBorders(grid, plant)).orElse(null);
                    }).toList();
                }
        ).toList();
    }

    private static Long scanHorizontal(List<List<List<Border>>> grid) {
        return Stream.of(Border.UP, Border.DOWN).map(border -> Stream.ofAll(grid)
                .map(line -> countBorders(Stream.ofAll(line).map(pos -> {
                    if (pos != null && pos.contains(border)) return "1";
                    else return "0";
                }).toJavaList())).sum().longValue()).sum().longValue();
    }

    private static Long scanVertical(List<List<List<Border>>> grid) {
        return Stream.of(Border.LEFT, Border.RIGHT)
                .map(border -> {
                            return IntStream.range(0, grid.getFirst().size()).mapToLong(col ->
                            {
                                var column = IntStream.range(0, grid.size()).mapToObj(r -> grid.get(r).get(col));
                                var a = countBorders(column.map(pos -> {
                                    if (pos != null && pos.contains(border)) return "1";
                                    else return "0";
                                }).toList());
                                return a;
                            }).sum();

                        }
                ).sum().longValue();
    }

    private static Long countBorders(List<String> line) {
        return Arrays.stream(String.join("", line).split("0")).filter(it -> it.contains("1")).count();
    }

    private static List<Border> getBorders(List<List<Plant>> grid, Plant plant) {
        var left = getNeighbour(grid, plant, -1, 0);
        var right = getNeighbour(grid, plant, 1, 0);
        var up = getNeighbour(grid, plant, 0, -1);
        var down = getNeighbour(grid, plant, 0, 1);

        var ver = Stream.of(
                        new Tuple2<>(left, Border.LEFT),
                        new Tuple2<>(right, Border.RIGHT)
                ).filter(it -> it._1 == null || !Objects.equals(it._1.symbol, plant.symbol))
                .map(Tuple2::_2).toJavaList();
        var hor = Stream.of(
                        new Tuple2<>(up, Border.UP),
                        new Tuple2<>(down, Border.DOWN)
                ).filter(it -> it._1 == null || !Objects.equals(it._1.symbol, plant.symbol))
                .map(Tuple2::_2).toJavaList();
        return Stream.ofAll(ver).appendAll(hor).toJavaList();
    }

    private static Plant getNeighbour(List<List<Plant>> grid, Plant point, int horizontal, int vertical) {
        var column = point.column + horizontal;
        var row = point.row + vertical;

        if (row >= 0 && column >= 0 && row < grid.size() && column < grid.getFirst().size()) {
            return grid.get(row).get(column);
        }
        return null;
    }

    private static List<List<Plant>> getData(List<String> lines) {
        return Stream.ofAll(lines).zipWithIndex().map(row ->
                Stream.ofAll(Arrays.asList(row._1.split("")))
                        .zipWithIndex().map(column ->
                                new Plant(row._2, column._2, column._1)).toJavaList()).toJavaList();

    }


    record Plant(Integer row, Integer column, String symbol) {
    }

    enum Border {
        UP,
        DOWN,
        RIGHT,
        LEFT
    }

}
