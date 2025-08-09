package org.twyszomirski.aoc.solutions

import java.io.File


class Day_6 {

    fun solve() {
        val lines = File("src/main/resources/input_day_6.txt").readLines()
        println("======== Day 6 ===========")
        part1(lines)
        part2(lines)
    }

    fun part1(input: List<String>) {
        val matrix = readMatrix(input)
        val direction = Direction(0, -1)
        val start = matrix.flatten().first { it.visited }

        var result = start to direction
        while (result.first.isInsideMatrix(matrix)) {
            result = result.first.moveOnMatrix(matrix, result.second)
        }
        val sum = matrix.flatten().count { it.visited }
        println(sum)
    }

    fun part2(input: List<String>) {
        val initial = readMatrix(input)
        val direction = Direction(0, -1)
        val start = initial.flatten().first { it.visited }

        val count = initial.flatten().map {
            var cycle = false
            if (!it.obstacle && it != start) {
                val withAdditionalObstacle = readMatrix(input, it)

                var resultSlow = start to direction
                var resultFast = start to direction

                while (resultFast.first.isInsideMatrix(initial)) {
                    resultFast = resultFast.first.moveOnMatrix(withAdditionalObstacle, resultFast.second)
                    resultFast = resultFast.first.moveOnMatrix(withAdditionalObstacle, resultFast.second)

                    resultSlow = resultSlow.first.moveOnMatrix(withAdditionalObstacle, resultSlow.second)

                    if (resultSlow.first == resultFast.first && resultSlow.second == resultFast.second) {
                        cycle = true
                        break
                    }
                }
            }
            cycle
        }.count { it }
        println(count)
    }

    fun readMatrix(input: List<String>, additionalObstacle: Point? = null): List<List<Point>> {
        return input.mapIndexed { row, data ->
            data.split("").filter { it.isNotBlank() }
                .mapIndexed { col, data ->
                    val type = if (additionalObstacle?.x == col && additionalObstacle?.y == row) "#" else data
                    Point.ofData(col, row, type)
                }
        }
    }

}

data class Point(val x: Int, val y: Int, val obstacle: Boolean = false, var visited: Boolean = false) {

    fun isInsideMatrix(matrix: Matrix) = x >= 0 && y >= 0 && x < matrix.first().size && y < matrix.size

    fun moveOnMatrix(matrix: Matrix, direction: Direction): Pair<Point, Direction> {
        val newPoint = Point(x + direction.horizontal, y + direction.vertical)
        if (newPoint.isInsideMatrix(matrix)) {
            val onMatrix = matrix[newPoint.y][newPoint.x]
            if (onMatrix.obstacle) {
                return moveOnMatrix(matrix, direction.rotated())
            } else {
                onMatrix.visited = true
                return Pair(onMatrix, direction)
            }
        }
        return Pair(newPoint, direction)
    }

    companion object {
        fun ofData(x: Int, y: Int, data: String): Point {
            return when (data) {
                "." -> Point(x, y, false)
                "#" -> Point(x, y, true)
                "^" -> Point(x, y, false, true)
                else -> throw RuntimeException("Unknown point.")
            }
        }
    }
}

data class Direction(val horizontal: Int, val vertical: Int) {
    fun rotated(): Direction {
        return when (this) {
            Direction(0, 1) -> Direction(-1, 0)
            Direction(-1, 0) -> Direction(0, -1)
            Direction(0, -1) -> Direction(1, 0)
            Direction(1, 0) -> Direction(0, 1)
            else -> throw RuntimeException("Not in this universe")
        }
    }
}

typealias Matrix = List<List<Point>>