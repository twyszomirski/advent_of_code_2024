package org.twyszomirski.aoc.solutions;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static java.io.IO.println;

public class Day_7 {

    public static void solve() throws IOException {
        var lines = Files.readAllLines(Paths.get("src/main/resources/input_day_7.txt"));
        println("======== Day 7 ===========");
        part_1(lines);
        part_2(lines);
    }

    private static void part_1(List<String> lines) {
        var data = readData(lines);
        var sum = (Long) data.stream().filter(it -> isResultCorrect(it, List.of(Operation.ADD, Operation.MULTIPLY))).mapToLong(it -> it.result).sum();
        println(sum);
    }

    private static void part_2(List<String> lines) {
        var data = readData(lines);
        var sum = (Long) data.stream().filter(it -> isResultCorrect(it, List.of(Operation.ADD, Operation.MULTIPLY, Operation.CONCAT))).mapToLong(it -> it.result).sum();
        println(sum);
    }

    private static List<OperationData> readData(List<String> lines) {
        return lines.stream().map(it -> it.split(":")).map(it -> new OperationData(Long.valueOf(it[0]),
                Arrays.stream(it[1].split(" ")).filter(op -> !op.isBlank()).map(Long::valueOf).toList())).toList();
    }

    private static List<? extends List<Operation>> providePermutations(int length, List<Operation> variants) {
        if (length == 1) {
            return variants.stream().map(Collections::singletonList).map(ArrayList::new).toList();
        }

        var prev = providePermutations(length - 1, variants);
        return prev.stream().flatMap(p -> {
            return variants.stream().map(variant ->
            {
                var copy = new ArrayList<>(p);
                copy.add(variant);
                return copy;
            });
        }).toList();
    }

    private static boolean isResultCorrect(OperationData operationData, List<Operation> variants) {
        var permutations = providePermutations(operationData.operands.size() - 1, variants);
        return permutations.stream().anyMatch(operationData::addsUp);
    }

    record OperationData(Long result, List<Long> operands) {

        public boolean addsUp(List<Operation> operations) {
            var result = operands.getFirst();

            for (int i = 0; i < operations.size(); i++) {
                result = operations.get(i).apply(result, operands.get(i + 1));
            }
            return Objects.equals(result, this.result);
        }
    }

    enum Operation {
        ADD(Long::sum),
        MULTIPLY((a, b) -> a * b),
        CONCAT((a, b) -> Long.valueOf(a.toString() + b.toString()));

        private BiFunction<Long, Long, Long> op;

        private Operation(BiFunction<Long, Long, Long> op) {
            this.op = op;
        }

        public Long apply(Long a, Long b) {
            return op.apply(a, b);
        }
    }

}
