package org.twyszomirski.aoc.solutions

import org.twyszomirski.aoc.solutions.Day_10.TrailPoint
import java.io.File


class Day_10 {

    fun solve() {
        val lines = File("src/main/resources/input_day_10.txt").readLines()
        println("======== Day 10 ===========")
        part1(lines)
        part2(lines)
    }

    fun part1(input: List<String>) {
        val data = getData(input)
        val starters = data.flatten().filter { it.inclination == 0 }
        val sum = starters.map { listOf(it) }.map { getTrails(data, it) }
            .map { it.map { it.last() }.toSet() }.sumOf { it.size }

        println(sum)
    }


    fun part2(input: List<String>) {
        val data = getData(input)
        val starters = data.flatten().filter { it.inclination == 0 }
        val sum = starters.map { listOf(it) }.map { getTrails(data, it) }
            .sumOf { it.size }

        println(sum)
    }

    private fun getTrails(matrix: TrailMatrix, trail: List<TrailPoint>): List<Trail> {
        val head = trail.last()
        val candidates = listOf(
            getCandidate(matrix, head, 0, -1),
            getCandidate(matrix, head, 1, 0),
            getCandidate(matrix, head, 0, 1),
            getCandidate(matrix, head, -1, 0),
        ).filter { it != null }
            .filter { it!!.inclination == head.inclination + 1 }
            .map { trail + listOf(it!!) }
        if (trail.size == 9) {
            return candidates
        }
        return candidates.flatMap { it -> getTrails(matrix, it) }
    }

    fun getCandidate(matrix: TrailMatrix, previous: TrailPoint, horizontal: Int, vertical: Int): TrailPoint? {
        val nextRow = previous.row + vertical
        val nextCol = previous.column + horizontal
        if (nextRow >= 0 && nextRow < matrix.size && nextCol >= 0 && nextCol < matrix.first().size) {
            return matrix[nextRow][nextCol]
        }
        return null
    }

    private fun getData(lines: List<String>): TrailMatrix {
        return lines.mapIndexed { row, line ->
            line.split("").filter { it.isNotBlank() }
                .map { if (".".equals(it)) -1 else it.toInt() }
                .mapIndexed { col, p -> TrailPoint(row, col, p) }.toList()
        }.toList()
    }


    data class TrailPoint(val row: Int, val column: Int, val inclination: Int)

}

typealias TrailMatrix = List<List<TrailPoint>>
typealias Trail = List<TrailPoint>
