package org.twyszomirski.aoc.solutions

import java.io.File
import java.util.concurrent.atomic.AtomicInteger


class Day_12 {

    fun solve() {
        val lines = File("src/main/resources/input_day_12.txt").readLines()
        println("======== Day 12 ===========")
        part1(lines)
        part2(lines)
    }

    fun part1(input: List<String>) {
        val data = readData(input)

        val indexes = mutableMapOf<String, AtomicInteger>()

        data.flatten().forEach { parcel ->
            if (!parcel.marked) {
                val index = indexes.computeIfAbsent(parcel.symbol) { AtomicInteger(0) }
                markNeighbours(data, parcel, index.andIncrement)
            }
        }

        val regions = data.flatten().groupBy { it.symbol + it.index }

        val metrics = regions.map { (_, parcels) ->
            Pair(parcels.size, parcels.sumOf { countBorders(data, it) })
        }

        val result = metrics.sumOf { it.first * it.second }

        println(result)
    }

    fun part2(input: List<String>) {
        val data = readData(input)

        val indexes = mutableMapOf<String, AtomicInteger>()

        data.flatten().forEach { parcel ->
            if (!parcel.marked) {
                val index = indexes.computeIfAbsent(parcel.symbol) { AtomicInteger(0) }
                markNeighbours(data, parcel, index.andIncrement)
            }
        }

        data.flatten().forEach { markBorders(data, it) }

        val regions = data.flatten().groupBy { Pair(it.symbol, it.index) }

        val metrics = regions.map { (key, parcels) ->
            Pair(
                parcels.size, scanHorizontal(data, key.first, key.second) +
                        scanVertical(data, key.first, key.second)
            )
        }

        val result = metrics.sumOf { it.first * it.second }

        println(result)
    }


    fun markNeighbours(matrix: PlantMatrix, parcel: PlantParcel, index: Int) {
        parcel.marked = true
        parcel.index = index
        val right = getNeighbour(matrix, parcel, 1, 0)
        val left = getNeighbour(matrix, parcel, -1, 0)
        val bottom = getNeighbour(matrix, parcel, 0, 1)
        val up = getNeighbour(matrix, parcel, 0, -1)

        listOfNotNull(left, right, up, bottom).forEach {
            if (it.symbol == parcel.symbol && !it.marked) {
                it.marked
                it.index = index
                markNeighbours(matrix, it, index)
            }
        }
    }

    fun countBorders(matrix: PlantMatrix, parcel: PlantParcel): Int {
        val left = getNeighbour(matrix, parcel, -1, 0)
        val right = getNeighbour(matrix, parcel, 1, 0)
        val up = getNeighbour(matrix, parcel, 0, -1)
        val down = getNeighbour(matrix, parcel, 0, 1)

        return listOf(left, right, up, down).map { neighbour ->
            when {
                neighbour == null -> 1
                neighbour.symbol != parcel.symbol -> 1
                else -> 0
            }
        }.sum()
    }

    fun getNeighbour(matrix: PlantMatrix, parcel: PlantParcel, hor: Int, ver: Int): PlantParcel? {
        val row = parcel.row + ver
        val col = parcel.column + hor

        if (row >= 0 && col >= 0 && row < matrix.size && col < matrix.first().size) {
            return matrix[row][col]
        }
        return null
    }

    fun markBorders(matrix: PlantMatrix, parcel: PlantParcel) {
        val right = getNeighbour(matrix, parcel, 1, 0)
        val left = getNeighbour(matrix, parcel, -1, 0)
        val bottom = getNeighbour(matrix, parcel, 0, 1)
        val up = getNeighbour(matrix, parcel, 0, -1)

        parcel.ver = listOf(
            left to Border.LEFT,
            right to Border.RIGHT
        ).filter { it.first == null || it.first!!.symbol != parcel.symbol }.map { it.second }
        parcel.hor = listOf(
            up to Border.UP,
            bottom to Border.DOWN
        ).filter { it.first == null || it.first!!.symbol != parcel.symbol }.map { it.second }
    }

    fun numberOfBorders(line: List<PlantParcel>, symbol: String, index: Int, check: (PlantParcel) -> Boolean): Int {

        val mapped = line.map { if (it.symbol == symbol && it.index == index && check(it)) 1 else 0 }

        return mapped.joinToString("")
            .split("0").count { it.contains("1") }
    }

    fun scanHorizontal(matrix: PlantMatrix, symbol: String, index: Int): Int {
        return listOf(Border.UP, Border.DOWN).sumOf { border ->
            matrix.sumOf { row ->
                numberOfBorders(row, symbol, index) { it.hor.contains(border) }
            }
        }
    }

    fun scanVertical(matrix: PlantMatrix, symbol: String, index: Int): Int {
        return listOf(Border.LEFT, Border.RIGHT).sumOf { border ->
            (0..<matrix.first().size).map {
                matrix.map { r -> r[it] }
            }.sumOf { numberOfBorders(it, symbol, index) { it.ver.contains(border) } }
        }
    }

    fun readData(lines: List<String>): PlantMatrix {
        return lines.mapIndexed { row, line ->
            line.split("").filter { it.isNotBlank() }.mapIndexed { column, symbol ->
                PlantParcel(row, column, symbol)
            }.toList()
        }.toList()
    }

    fun print(matrix: PlantMatrix, how: (PlantParcel) -> String) {
        matrix.forEach {
            println(it.joinToString(" ") { how(it) })
        }
    }
}

data class PlantParcel(
    val row: Int,
    val column: Int,
    val symbol: String,
    var marked: Boolean = false,
    var index: Int = -1,
    var hor: List<Border> = emptyList(),
    var ver: List<Border> = emptyList()
)

enum class Border {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

typealias PlantMatrix = List<List<PlantParcel>>
