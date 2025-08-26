package org.twyszomirski.aoc.solutions;

import org.twyszomirski.aoc.utils.Tuple;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.io.IO.println;

public class Day_6 {

    public static void solve() throws IOException {
        var lines = Files.readAllLines(Paths.get("src/main/resources/input_day_6.txt"));
        println("======== Day 6 ===========");
        part_1(lines);
        part_2(lines);
    }

    private static void part_1(List<String> lines) {
        var data = readData(lines);
        var start = data.stream().flatMap(Collection::stream)
                .filter(MatrixPoint::isStart).findFirst().get();

        var result = start.move(data, Direction.UP);
        Set<MatrixPoint> points = new HashSet<>();
        points.add(start);
        while (result.left() != null) {
            points.add(result.left());
            result = result.left().move(data, result.right());
        }

        println(points.size());
    }

    private static void part_2(List<String> lines) {
        var data = readData(lines);

        long count = 0;
        for (int row = 0; row < lines.size(); row++) {
            for (int col = 0; col < data.get(row).size(); col++) {
                var copy = copyWithObstruction(data, row, col);
                if (copy != null && hasCycle(copy)) {
                    count++;
                }
            }
        }
        println(count);
    }


    private static List<List<MatrixPoint>> readData(List<String> lines) {
        List<List<MatrixPoint>> matrix = new ArrayList<>();
        for (int row = 0; row < lines.size(); row++) {
            var r = Arrays.stream(lines.get(row).split("")).filter(it -> !it.isBlank()).toList();
            var c = new ArrayList<MatrixPoint>();
            for (int column = 0; column < r.size(); column++) {
                c.add(new MatrixPoint(row, column, r.get(column)));
            }
            matrix.add(c);
        }
        return matrix;
    }

    record MatrixPoint(Integer row, Integer column, String symbol) {

        public boolean isObstacle() {
            return "#".equals(symbol);
        }

        public boolean isStart() {
            return "^".equals(symbol);
        }

        public Tuple<MatrixPoint, Direction> move(List<List<MatrixPoint>> matrix, Direction direction) {
            var row = this.row + direction.vertical;
            var column = this.column + direction.horizontal;
            MatrixPoint next;
            if (row >= 0 && row < matrix.size() && column >= 0 && column < matrix.getFirst().size()) {
                next = matrix.get(row).get(column);
            } else {
                return new Tuple<>(null, direction);
            }
            if (next.isObstacle()) {
                return this.move(matrix, direction.rotate());
            }

            return new Tuple<>(next, direction);
        }
    }

    private static List<List<MatrixPoint>> copyWithObstruction(List<List<MatrixPoint>> initial, Integer row, Integer column) {
        MatrixPoint originalPoint = initial.get(row).get(column);
        if (originalPoint.isObstacle() || originalPoint.isStart()) {
            return null;
        }
        return initial.stream()
                .map(r -> r.stream()
                        .map(p -> (p.row.equals(row) && p.column.equals(column)) ? new MatrixPoint(p.row, p.column, "#") : p)
                        .toList())
                .toList();
    }

    private static boolean hasCycle(List<List<MatrixPoint>> matrix) {
        var start = matrix.stream().flatMap(Collection::stream)
                .filter(MatrixPoint::isStart).findFirst().get();
        var direction = Direction.UP;
        var fastResult = new Tuple<MatrixPoint, Direction>(start, direction);
        var slowResult = new Tuple<MatrixPoint, Direction>(start, direction);

        var hasCycle = false;

        while (fastResult.left() != null) {
            fastResult = fastResult.left().move(matrix, fastResult.right());
            if (fastResult.left() == null) {
                break;
            }
            fastResult = fastResult.left().move(matrix, fastResult.right());
            slowResult = slowResult.left().move(matrix, slowResult.right());

            if (fastResult.left() == slowResult.left() && fastResult.right() == slowResult.right()) {
                hasCycle = true;
                break;
            }
        }
        return hasCycle;
    }

    enum Direction {
        UP(-1, 0),
        RIGHT(0, 1),
        DOWN(1, 0),
        LEFT(0, -1);

        private Integer vertical;
        private Integer horizontal;


        Direction(Integer vertical, Integer horizontal) {
            this.vertical = vertical;
            this.horizontal = horizontal;
        }

        public Direction rotate() {
            return switch (this) {
                case UP -> RIGHT;
                case RIGHT -> DOWN;
                case DOWN -> LEFT;
                case LEFT -> UP;
            };
        }

    }
}
