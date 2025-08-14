package org.twyszomirski.aoc.solutions

import java.io.File


class Day_8 {

    fun solve() {
        val lines = File("src/main/resources/input_day_8.txt").readLines()
        println("======== Day 8 ===========")
        part1(lines)
        part2(lines)
    }

    fun part1(input: List<String>) {
        val data = readData(input)
        val numCols = data.first().size
        val numRows = data.size

        val bySymbol = data.flatten().filter { !it.antenna.equals(".") }.groupBy { it.antenna }
        val pairs = bySymbol.mapValues { producePairs(it.value) }
        val antinodes = pairs.mapValues {
            it.value.flatMap { x -> calcAntiNodes(x) }
        }

        val calc = antinodes.values.flatten().toSet()
            .count { it.isInMatrix(numRows, numCols) }

        println(calc)
    }

    fun part2(input: List<String>) {
        val data = readData(input)
        val numCols = data.first().size
        val numRows = data.size

        val bySymbol = data.flatten().filter { !it.antenna.equals(".") }.groupBy { it.antenna }
        val pairs = bySymbol.mapValues { producePairs(it.value) }
        val antinodes = pairs.mapValues {
            it.value.flatMap { x -> calcInfiniteAntiNodes(x, numRows, numCols) }
        }

        val antinodesOfPairs = pairs.values.flatten()
            .flatMap { listOf(Point(it.first.row, it.first.column, "#"), Point(it.second.row, it.second.column, "#")) }
            .toSet()

        val calc = (antinodes.values.flatten().toSet() + antinodesOfPairs)
            .count { it.isInMatrix(numRows, numCols) }

        println(calc)
    }

    fun producePairs(points: List<Point>): List<Pair<Point, Point>> {
        if (points.size < 2) {
            return emptyList()
        }
        val first = points.first()
        val rest = points.subList(1, points.size)
        val currentPairs = rest.map { Pair(first, it) }
        return currentPairs + producePairs(rest)
    }

    fun calcAntiNodes(antennas: Pair<Point, Point>): List<Point> {
        return listOf(calcUpperAntinode(antennas), calcLowerAntinode(antennas))
    }

    fun calcInfiniteAntiNodes(antennas: Pair<Point, Point>, columns: Int, rows: Int): List<Point> {
        val upper = mutableListOf<Point>()
        var nextPair = antennas
        do {
            upper.add(calcUpperAntinode(nextPair))
            nextPair = Pair(upper.last(), nextPair.first)
        } while (upper.last().isInMatrix(rows, columns))

        val lower = mutableListOf<Point>()
        nextPair = antennas
        do {
            lower.add(calcLowerAntinode(nextPair))
            nextPair = Pair(nextPair.second, lower.last())
        } while (lower.last().isInMatrix(rows, columns))

        return upper + lower
    }

    fun calcUpperAntinode(antennas: Pair<Point, Point>): Point {
        val difference = (antennas.first.row - antennas.second.row) to (antennas.first.column - antennas.second.column)
        return Point(antennas.first.row + difference.first, antennas.first.column + difference.second, "#")
    }

    fun calcLowerAntinode(antennas: Pair<Point, Point>): Point {
        val difference = (antennas.first.row - antennas.second.row) to (antennas.first.column - antennas.second.column)
        return Point(antennas.second.row - difference.first, antennas.second.column - difference.second, "#")
    }


    fun readData(input: List<String>) = input.mapIndexed { row, d ->
        d.split("").filter { it.isNotBlank() }
            .mapIndexed { col, antenna -> Point(row, col, antenna) }
    }.toList()


    data class Point(val row: Int, val column: Int, val antenna: String) {
        fun isInMatrix(rows: Int, columns: Int): Boolean {
            return row >= 0 && row < rows && column >= 0 && column < columns
        }
    }

}