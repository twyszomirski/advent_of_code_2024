package org.twyszomirski.aoc.solutions

import java.io.File
import java.util.Stack


class Day_15 {

    fun solve() {
        val lines = File("src/main/resources/input_day_15.txt").readLines()
        println("======== Day 15 ===========")
        part1(lines)
        part2(lines)
    }

    fun part1(input: List<String>) {
        val (grid, moves) = getData(input)
        moves.forEach { move ->
            when (move) {
                Move.RIGHT -> moveRight(grid.first { it.any { it.isRobot() } })
                Move.LEFT -> moveRight(grid.first { it.any { it.isRobot() } }.reversed())
                Move.UP -> moveRight(verticalWithSymbol(grid).reversed())
                Move.DOWN -> moveRight(verticalWithSymbol(grid))
            }
        }
        val sum = grid.flatten().filter { it.symbol == "O" }.sumOf { (100L * it.row) + it.column }
        println(sum)
    }

    fun part2(input: List<String>) {
        val (grid, moves) = getData(input, true)
        moves.forEach { move ->
            when (move) {
                Move.RIGHT -> moveRight(grid.first { it.any { it.isRobot() } })
                Move.LEFT -> moveRight(grid.first { it.any {it.isRobot() } }.reversed())
                Move.UP -> upTree(grid)
                Move.DOWN -> downTree(grid)
            }
        }

        val sum = grid.flatten().filter { it.symbol == "[" }.sumOf { (100L * it.row) + it.column }
        println(sum)
    }

    fun downTree(grid: List<List<GridPoint>>) {
        val robot = grid.flatten().first { it.isRobot() }
        val root = nextDown(grid, robot)
        val leafs = findLeafs(root)
        if(leafs.all { it.point.isEmpty() }) {
            pushNodesDown(grid, root)
        }
    }

    fun upTree(grid: List<List<GridPoint>>) {
        val robot = grid.flatten().first { it.isRobot() }
        val root = nextUp(grid, robot)
        val leafs = findLeafs(root)
        if(leafs.all { it.point.isEmpty() }) {
            pushNodesUp(grid, root)
        }
    }

    fun pushNodesUp(grid: List<List<GridPoint>>,root: Node?){
        pushNodes(grid, root, -1)
    }

    fun pushNodesDown(grid: List<List<GridPoint>>,root: Node?){
        pushNodes(grid, root, 1)
    }

    fun pushNodes(grid: List<List<GridPoint>>,root: Node?, dir: Int){
        if(root == null) return
        if(root.point.isEmpty()) return
        pushNodes(grid,root.left, dir)
        pushNodes(grid, root.right, dir)
        grid[root.point.row +dir][root.point.column].symbol = root.point.symbol

        if(root.point.symbol == "["){
            grid[root.point.row +dir][root.point.column+1].symbol = "]"
            grid[root.point.row][root.point.column+1].symbol = "."
        }

        if(root.point.symbol == "]"){
            grid[root.point.row +dir][root.point.column-1].symbol = "["
            grid[root.point.row][root.point.column-1].symbol = "."
        }

        root.point.symbol = "."
    }

    fun findLeafs(root: Node ): List<Node>{
        val stack = Stack<Node>()
        val leafs = mutableListOf<Node>()
        stack.push(root)
        while (!stack.isEmpty()){
            val item = stack.pop()
            if(item.left == null && item.right == null){
                leafs.add(item)
            }
            if(item.right != null){
                stack.push(item.right)
            }
            if(item.left != null){
                stack.push(item.left)
            }
        }
        return leafs
    }

    fun nextUp(grid: List<List<GridPoint>>, point: GridPoint): Node{
        return nextInTree(grid, point, -1)
    }

    fun nextDown(grid: List<List<GridPoint>>, point: GridPoint): Node{
        return nextInTree(grid, point, 1)
    }

    fun nextInTree(grid: List<List<GridPoint>>, point: GridPoint, dir: Int): Node{
        if(point.isEmpty() || point.isWall()){
            return Node(null, null, point)
        }

        val next = grid[point.row +dir][point.column]
        if(point.symbol == "[") return Node(nextInTree(grid,next,dir),
            nextInTree(grid, grid[point.row +dir][point.column +1], dir), point)
        if(point.symbol == "]") return Node(
            nextInTree(grid, grid[point.row +dir][point.column -1], dir), nextInTree(grid,next, dir), point)

        return Node(nextInTree(grid, next, dir), null, point)
    }

    fun verticalWithSymbol(grid: List<List<GridPoint>>): List<GridPoint> {
        val robot = grid.flatten().first { it.isRobot() }
        return grid.map { it[robot.column] }
    }

    fun moveRight(line: List<GridPoint>) {
        val robot = line.indexOfFirst { it.isRobot() }
        val robotTail = line.subList(robot + 1, line.size).takeWhile { !it.isWall() && !it.isEmpty() }
        val nextPosIdx = robot + robotTail.size + 1
        val nextPos = line[nextPosIdx]
        if (nextPos.isEmpty()) {
            for (i in nextPosIdx downTo robot + 1) {
                line[i].symbol = line[i - 1].symbol
            }
            line[robot].symbol = "."
        }
    }

    fun getData(input: List<String>, expanded: Boolean = false): Pair<List<List<GridPoint>>, List<Move>> {
        val map = input.takeWhile { it.contains("#") }
        val moves = input.dropWhile { it.contains("#") }.filter { it.isNotBlank() }

        val mapFinal = if (!expanded) map else {
            map.map { line ->
                line.split("").filter { it.isNotBlank() }.flatMap { symbol ->
                    when (symbol) {
                        "#" -> listOf("#", "#")
                        "@" -> listOf("@", ".")
                        "." -> listOf(".", ".")
                        "O" -> listOf("[", "]")
                        else -> throw RuntimeException("Unknown symbol $symbol")
                    }
                }.joinToString("")
            }
        }

        val mapParsed = mapFinal.mapIndexed { row, line ->
            line.split("").filter { it.isNotBlank() }.mapIndexed { column, symbol ->
                GridPoint(row, column, symbol)
            }
        }

        val movesParsed = moves.flatMap { it.split("") }.filter { it.isNotBlank() }.map { it -> Move.ofSymbol(it) }
        return Pair(mapParsed, movesParsed)
    }

    fun print(grid: List<List<GridPoint>>) {
        println()
        grid.forEach {
            println(it.joinToString("") { it.symbol })
        }
    }

    data class GridPoint(val row: Int, val column: Int, var symbol: String) {
        fun isWall() = symbol == "#"
        fun isEmpty() = symbol == "."
        fun isRobot() = symbol == "@"
    }

    data class Node(val left: Node?, val right: Node?, val point: GridPoint)

    enum class Move {
        UP, DOWN, LEFT, RIGHT;

        companion object {
            fun ofSymbol(symbol: String): Move {
                return when (symbol) {
                    "<" -> LEFT
                    "v" -> DOWN
                    ">" -> RIGHT
                    "^" -> UP
                    else -> throw RuntimeException("Unknown symbol $symbol")
                }
            }
        }
    }
}

