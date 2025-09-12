package org.twyszomirski.aoc.solutions

import java.io.File


class Day_14 {

    fun solve() {
        val lines = File("src/main/resources/input_day_14.txt").readLines()
        println("======== Day 13 ===========")
        part1(lines)
        part2(lines)
    }

    fun part1(input: List<String>) {
        val data = getData(input)
        val gridSize = GridSize(103, 101)
        val repetitions = 100
        val finalPositions = data.map { data ->
            var currentPosition = data.first
            repeat(repetitions) {
                currentPosition = movePoint(currentPosition, data.second, gridSize)
            }
            currentPosition
        }.groupBy { point -> pointQuadrant(point, gridSize) }

        val result = finalPositions.filter { it.key > 0 }.map { it.value.size }.reduce { a, b -> a * b }
        println(result)
    }

    fun part2(input: List<String>) {
        val data = getData(input)
        val gridSize = GridSize(103, 101)
        val repetitions = 10000
        var currentState = data
        repeat(repetitions) { idx ->
            currentState = currentState.map { data ->
                Pair(movePoint(data.first, data.second, gridSize), data.second)
            }.toList()
            val grid = dropOnGrid(currentState.map { it.first }, gridSize)
            if(hasLongLine(grid, 15)) {
                println("Iteration number: ${idx+1}")
                print(grid)
            }
        }

    }

    fun pointQuadrant(point: XYPoint, gridSize: GridSize): Int {
        val isOnTheLeft = point.x < gridSize.columns / 2
        val isOnTheRight = point.x > gridSize.columns / 2
        val isOnTheTop = point.y < gridSize.rows / 2
        val isOnTheBottom = point.y > gridSize.rows / 2

        return when {
            isOnTheLeft && isOnTheTop -> 1
            isOnTheRight && isOnTheTop -> 2
            isOnTheLeft && isOnTheBottom -> 3
            isOnTheRight && isOnTheBottom -> 4
            else -> -1
        }
    }

    fun dropOnGrid(points: List<XYPoint>, gridSize: GridSize): List<List<String>> {
        val grid = (0..<gridSize.rows).map {
            (0..<gridSize.columns).map {
                "."
            }.toMutableList()
        }.toList()

        points.forEach {
            grid[it.y][it.x] = "X"
        }
        return grid
    }

    fun hasLongLine(grid: List<List<String>>, length: Int): Boolean {
        return grid.any { line ->
            line.joinToString("").split(".").any { it.length >= length }
        }
    }

    fun print(grid: List<List<String>>) {
        grid.forEach { line ->
            println(line.joinToString(""))
        }
    }

    fun movePoint(point: XYPoint, move: Move, gridSize: GridSize) = XYPoint(
        moveInLine(point.x, move.x, gridSize.columns), moveInLine(point.y, move.y, gridSize.rows)
    )

    fun moveInLine(current: Int, movement: Int, totalLength: Int): Int {
        val newPos = (current + movement) % totalLength
        return if (newPos < 0) {
            totalLength + newPos
        } else newPos
    }


    fun getData(input: List<String>) = input.map { line ->
        val split = line.split(" ")
        val pointCoordinates = split[0].replace("p=", "").split(",")
        val moveCoordinates = split[1].replace("v=", "").split(",")
        Pair(
            XYPoint(pointCoordinates[0].toInt(), pointCoordinates[1].toInt()),
            Move(moveCoordinates[0].toInt(), moveCoordinates[1].toInt())
        )
    }
}

data class XYPoint(val x: Int, val y: Int)
data class Move(val x: Int, val y: Int)
data class GridSize(val rows: Int, val columns: Int)

