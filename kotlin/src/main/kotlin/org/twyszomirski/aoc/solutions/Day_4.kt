package org.twyszomirski.aoc.solutions

import java.io.File


class Day_4 {

    fun solve() {
        val lines = File("src/main/resources/input_day_4.txt").readLines()
        println("======== Day 4 ===========")
        part1(lines)
        part2(lines)
    }

    fun part1(input: List<String>) {
        val initialMatrix = input.map { it.split("").filter { it.isNotBlank() } }

        val numRows = initialMatrix.size
        val numCols = initialMatrix[0].size

        val asRows = initialMatrix.map { it.joinToString(separator = "") }
        val asColumns =
            (0..<numCols).map { col -> (0..<numRows).joinToString(separator = "") { row -> initialMatrix[row][col] } }

        val leftDiagonals = (0..<numRows).map {
            leftDiagonals(it, initialMatrix)
        }

        val rightDiagonals = (0..<numRows).map {
            rightDiagonals(it, initialMatrix)
        }

        val allPossible = asRows + asColumns + leftDiagonals.flatten() + rightDiagonals.flatten()

        val sum = allPossible.sumOf { it.windowed(size = 4).count { it == "XMAS" || it == "XMAS".reversed() } }
        println(sum)
    }

    fun leftDiagonals(row: Int, matrix: List<List<String>>): List<String> {
        val result = mutableListOf<String>()
        val numCols = matrix[0].size

        (0..<numCols).forEach {
            result.add(leftDiagonal(row, it, matrix))
            if (row > 0) return result
        }
        return result
    }

    fun leftDiagonal(row: Int, col: Int, matrix: List<List<String>>): String {
        val numRows = matrix.size
        val numCols = matrix[0].size

        var xIdx = col
        var yIdx = row
        val diag = mutableListOf<String>()
        while (xIdx < numCols && yIdx < numRows) {
            diag.add(matrix[yIdx][xIdx])
            xIdx++
            yIdx++
        }
        return diag.joinToString(separator = "")
    }

    fun rightDiagonals(row: Int, matrix: List<List<String>>): List<String> {
        val result = mutableListOf<String>()
        val numCols = matrix[0].size

        (0..<numCols).reversed().forEach {
            result.add(rightDiagonal(row, it, matrix))
            if (row > 0) return result
        }
        return result
    }

    fun rightDiagonal(row: Int, col: Int, matrix: List<List<String>>): String {

        val numRows = matrix.size

        var xIdx = col
        var yIdx = row
        val diag = mutableListOf<String>()
        while (xIdx >= 0 && yIdx < numRows) {
            diag.add(matrix[yIdx][xIdx])
            xIdx--
            yIdx++
        }
        return diag.joinToString(separator = "")
    }

    fun part2(input: List<String>) {
        val initialMatrix = input.map { it.split("").filter { it.isNotBlank() } }

        val numRows = initialMatrix.size
        val numCols = initialMatrix[0].size

        val count = (0..<numRows -2).flatMap { row ->
            (0..<numCols -2 ).map { col ->
                val leftDiagonal = leftDiagonal(row, col, initialMatrix)
                var result = false
                if (leftDiagonal.startsWith("MAS") || leftDiagonal.startsWith("SAM")) {
                    val rightDiagonal = rightDiagonal(row, col + 2, initialMatrix)
                    if (rightDiagonal.startsWith("MAS") || rightDiagonal.startsWith("SAM")) {
                        result = true
                    }
                }
                result
            }
        }.count { it }

        val b = ""

        println(count)
    }

}