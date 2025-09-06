package org.twyszomirski.aoc.solutions;


import org.twyszomirski.aoc.utils.Tuple;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.io.IO.println;

public class Day_9 {

    public static void solve() throws IOException {
        var lines = Files.readAllLines(Paths.get("src/main/resources/input_day_9.txt"));
        println("======== Day 9 ===========");
        part_1(lines);
        part_2(lines);
    }

    private static void part_1(List<String> lines) {
        var data = readData(lines);
        var asLinked = new LinkedList<>(data);
        var nextEmptyIdx = 1;
        var nextFileIdx = asLinked.size() - 1;

        while (nextEmptyIdx < nextFileIdx) {
            var file = nextFile(asLinked, nextFileIdx);
            var empty = nextEmpty(asLinked, nextEmptyIdx);
            nextFileIdx = file.right();
            nextEmptyIdx = empty.right();
            empty.left().size--;
            file.left().size--;

            var prevFile = asLinked.get(empty.right()-1);
            if (prevFile.fileId.equals(file.left().fileId)) {
                prevFile.size++;
            } else {
                asLinked.add(nextEmptyIdx,new DiskBlock(1, file.left().fileId));
                nextFileIdx++;
            }
        }

        var onlyFiles = asLinked.stream().filter(it -> !it.isEmpty())
                .flatMap(file -> IntStream.range(0, file.size).map(a -> file.fileId).boxed())
                .toList();
        var sum = IntStream.range(0, onlyFiles.size()).boxed().mapToLong(idx -> (long) idx * onlyFiles.get(idx)).sum();
        println(sum);
    }

    private static void part_2(List<String> lines) {
        var data = readData(lines);
        var asLinked = new LinkedList<>(data);

        var nextFileIdx = asLinked.size() - 1;
        var currFileId = asLinked.size();
        while (nextFileIdx > 0) {
            var file = nextFile(asLinked, nextFileIdx, currFileId);
            currFileId = file.left().fileId;
            nextFileIdx = file.right();
            var empty = matchingEmpty(asLinked, file.left().size, file.right());
            if(empty != null){
                if(empty.left().size == file.left().size) {
                    empty.left().fileId = file.left().fileId;
                }else{
                    empty.left().size-=file.left().size;
                    asLinked.add(empty.right(), new DiskBlock(file.left().size, currFileId));
                }
                file.left().fileId = null;
            }
        }

        var onlyFiles = asLinked.stream()
                .flatMap(file -> IntStream.range(0, file.size).map(a -> file.fileId !=null ? file.fileId : 0 ).boxed())
                .toList();
        var sum = IntStream.range(0, onlyFiles.size()).boxed().mapToLong(idx -> (long) idx * onlyFiles.get(idx)).sum();
        println(sum);
    }

    private static void print(List<DiskBlock> data) {
        println(data.stream().map(DiskBlock::toString).collect(Collectors.joining("")));
    }

    private static Tuple<DiskBlock, Integer> nextEmpty(List<DiskBlock> data, Integer fromIdx) {
        for (int i = fromIdx; i < data.size(); i++) {
            var next = data.get(i);
            if (next.isEmpty() && next.size > 0) {
                return new Tuple<>(next, i);
            }
        }
        return null;
    }

    private static Tuple<DiskBlock, Integer> matchingEmpty(List<DiskBlock> data, Integer size, Integer maxIndex) {
        for (int i = 0; i < maxIndex; i++) {
            var next = data.get(i);
            if (next.isEmpty() && next.size >= size) {
                return new Tuple<>(next, i);
            }
        }
        return null;
    }

    private static Tuple<DiskBlock, Integer> nextFile(List<DiskBlock> data, Integer fromIdx) {
        for (int i = fromIdx; i > 0; i--) {
            var next = data.get(i);
            if (!next.isEmpty() && next.size > 0) {
                return new Tuple<>(next, i);
            }
        }
        return null;
    }

    private static Tuple<DiskBlock, Integer> nextFile(List<DiskBlock> data, Integer fromIdx, Integer maxFileId) {
        for (int i = fromIdx; i >= 0; i--) {
            var next = data.get(i);
            if (!next.isEmpty() && next.size > 0 && next.fileId < maxFileId) {
                return new Tuple<>(next, i);
            }
        }
        return null;
    }

    private static List<DiskBlock> readData(List<String> lines) {
        var digits = Arrays.stream(lines.getFirst().split(""))
                .mapToInt(Integer::valueOf).boxed().toList();
        return IntStream.range(0, digits.size()).mapToObj(idx ->
                (idx % 2 == 0) ? new DiskBlock(digits.get(idx), idx / 2) : new DiskBlock(digits.get(idx), null)
        ).toList();
    }

    static class DiskBlock {
        Integer size;
        Integer fileId;

        public DiskBlock(Integer size, Integer fileId) {
            this.size = size;
            this.fileId = fileId;
        }

        Boolean isEmpty() {
            return fileId == null;
        }

        @Override
        public String toString() {
            var symbol = isEmpty() ? "." : fileId.toString();
            return size == 0 ? "_" : IntStream.range(0, size).mapToObj(it -> symbol).collect(Collectors.joining());
        }

    }
}
