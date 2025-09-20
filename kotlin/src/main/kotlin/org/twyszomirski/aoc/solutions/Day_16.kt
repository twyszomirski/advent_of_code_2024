package org.twyszomirski.aoc.solutions

import java.io.File
import java.util.PriorityQueue
import kotlin.collections.mutableMapOf


class Day_16 {

    fun solve() {
        val lines = File("src/main/resources/input_day_16.txt").readLines()
        println("======== Day 16 ===========")
        part1(lines)
        part2(lines)
    }

    fun part1(input: List<String>) {
        val maze = getData(input)
        val start = maze.flatten().find { it.isStart() }!!
        val pq = PriorityQueue<MoveCost>()

        val minCosts = mutableMapOf<Movement, Int>()

        Dir.entries.forEach { startDir ->
            val firstMove = nextMove(maze, start, startDir)

            if (!firstMove.mazePoint.isWall()) {
                val cost = if (startDir == Dir.RIGHT) 1 else 1001
                val initialState = MoveCost(firstMove, cost)
                pq.add(initialState)
                minCosts[firstMove] = cost
            }
        }
        var lowestFinalCost: Int? = null

        while (pq.isNotEmpty()) {
            val current = pq.poll()

            if (current.cost > minCosts.getOrDefault(current.movement, Int.MAX_VALUE)) {
                continue
            }

            if (current.movement.mazePoint.isEnd()) {
                if (lowestFinalCost == null || current.cost < lowestFinalCost) {
                    lowestFinalCost = current.cost
                }
                continue
            }

            val possibleNextDirs = Dir.entries - current.movement.dir.opposite()

            possibleNextDirs.forEach { nextDir ->
                val nextMove = nextMove(maze, current.movement.mazePoint, nextDir)

                if (!nextMove.mazePoint.isWall()) {
                    val moveCost = 1 + if (nextDir == current.movement.dir) 0 else 1000
                    val newCost = current.cost + moveCost

                    if (newCost < minCosts.getOrDefault(nextMove, Int.MAX_VALUE)) {
                        minCosts[nextMove] = newCost
                        pq.add(MoveCost(nextMove, newCost))
                    }
                }
            }
        }

        println(lowestFinalCost)
    }


    fun part2(input: List<String>) {
        val maze = getData(input)
        val start = maze.flatten().find { it.isStart() }!!
        val pq = PriorityQueue<MoveCost>()

        val minCosts = mutableMapOf<Movement, Int>()
        val predecessors = mutableMapOf<Movement, MutableList<Movement>>()

        Dir.entries.forEach { startDir ->
            val firstMove = nextMove(maze, start, startDir)
            if (!firstMove.mazePoint.isWall()) {
                val cost = if (startDir == Dir.RIGHT) 1 else 1001
                pq.add(MoveCost(firstMove, cost))
                minCosts[firstMove] = cost
                predecessors[firstMove] = mutableListOf()
            }
        }

        var lowestFinalCost: Int? = null
        val finalMovements = mutableListOf<Movement>()

        while (pq.isNotEmpty()) {
            val current = pq.poll()

            if (current.cost > minCosts.getOrDefault(current.movement, Int.MAX_VALUE)) {
                continue
            }

            if (current.movement.mazePoint.isEnd()) {
                if (lowestFinalCost == null || current.cost < lowestFinalCost) {
                    lowestFinalCost = current.cost
                    finalMovements.clear()
                    finalMovements.add(current.movement)
                } else if (current.cost == lowestFinalCost) {
                    finalMovements.add(current.movement)
                }
                continue
            }

            val possibleNextDirs = Dir.entries - current.movement.dir.opposite()
            possibleNextDirs.forEach { nextDir ->
                val nextMove = nextMove(maze, current.movement.mazePoint, nextDir)

                if (!nextMove.mazePoint.isWall()) {
                    val moveCost = 1 + if (nextDir == current.movement.dir) 0 else 1000
                    val newCost = current.cost + moveCost

                    if (newCost < minCosts.getOrDefault(nextMove, Int.MAX_VALUE)) {
                        minCosts[nextMove] = newCost
                        pq.add(MoveCost(nextMove, newCost))
                        predecessors[nextMove] = mutableListOf(current.movement)
                    } else if (newCost == minCosts.getOrDefault(nextMove, Int.MAX_VALUE)) {
                        predecessors[nextMove]?.add(current.movement)
                    }
                }
            }
        }


        val bestPaths = finalMovements.flatMap { finalMove ->
            reconstructPaths( predecessors, finalMove)
        }

        val uniquePoints = bestPaths.flatten().toSet()
        println(uniquePoints.size+1)

    }

    private fun reconstructPaths(predecessors: Map<Movement, List<Movement>>, finalMove: Movement): List<MutableList<MazePoint>> {
        val prevMoves = predecessors[finalMove]

        if (prevMoves.isNullOrEmpty()) {
            return listOf(mutableListOf(finalMove.mazePoint))
        }

        val paths = mutableListOf<MutableList<MazePoint>>()
        for (prevMove in prevMoves) {
            val partialPaths = reconstructPaths(predecessors, prevMove)
            for (partialPath in partialPaths) {
                partialPath.add(finalMove.mazePoint)
                paths.add(partialPath)
            }
        }
        return paths
    }


    fun nextMove(maze: Maze, mazePoint: MazePoint, dir: Dir): Movement {
        return Movement(maze[mazePoint.row + dir.vertical][mazePoint.column + dir.horizontal], dir)
    }

    fun getData(input: List<String>): Maze {
        return input.mapIndexed { row, line ->
            line.split("").filter { it.isNotBlank() }.mapIndexed { column, symbol ->
                MazePoint(row, column, symbol)
            }
        }
    }

}

data class MoveCost(val movement: Movement, val cost: Int) : Comparable<MoveCost> {
    override fun compareTo(other: MoveCost): Int = this.cost.compareTo(other.cost)
}

data class MazePoint(val row: Int, val column: Int, val symbol: String) {
    fun isStart() = symbol == "S"
    fun isWall() = symbol == "#"
    fun isEnd() = symbol == "E"
}

data class Movement(val mazePoint: MazePoint, val dir: Dir)

enum class Dir(val horizontal: Int, val vertical: Int, val deg: Int) {
    UP(0, -1, 0),
    RIGHT(1, 0, 90),
    DOWN(0, 1, 180),
    LEFT(-1, 0, 270);

    fun opposite(): Dir {
        val d = (deg + 180) % 360
        return Dir.entries.find { it.deg == d }!!
    }
}


typealias Maze = List<List<MazePoint>>

