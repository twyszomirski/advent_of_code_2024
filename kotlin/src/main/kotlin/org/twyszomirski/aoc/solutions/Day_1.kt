package org.twyszomirski.aoc.solutions

import java.io.File
import kotlin.collections.mutableListOf
import kotlin.math.abs

class Day_1 {

    fun solve(){
        val lines = File("src/main/resources/input_day_1.txt").readLines()
        println("======== Day 1 ===========")
        part1(lines)
        part2(lines)
    }

    fun part1(input: List<String>) {
        val folded = input
            .map { it.split(" ").filter { it.isNotBlank() } }
            .map { Pair(it[0].toLong(), it[1].toLong()) }
            .fold(Pair(mutableListOf<Long>(), mutableListOf<Long>()), operation = { acc, pair ->
                acc.first.add(pair.first)
                acc.second.add(pair.second)
                acc
            })
        val leftSorted = folded.first.sorted()
        val rightSorted = folded.second.sorted()

        val sum = leftSorted.zip(rightSorted).sumOf { abs(it.first - it.second) }
        println(sum)
    }

    fun part2(input: List<String>) {
        val folded = input
            .map { it.split(" ").filter { it.isNotBlank() } }
            .map { Pair(it[0].toLong(), it[1].toLong()) }
            .fold(Pair(mutableListOf<Long>(), mutableListOf<Long>()), operation = { acc, pair ->
                acc.first.add(pair.first)
                acc.second.add(pair.second)
                acc
            })

        val leftList = folded.first
        val spectrum = folded.second.groupingBy { it }.eachCount()

        val sum = leftList.sumOf { it * (spectrum[it] ?: 0) }

        println(sum)
    }
}