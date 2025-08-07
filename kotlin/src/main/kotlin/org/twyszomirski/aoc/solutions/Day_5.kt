package org.twyszomirski.aoc.solutions

import java.io.File


class Day_5 {

    fun solve() {
        val lines = File("src/main/resources/input_day_5.txt").readLines()
        println("======== Day 5 ===========")
        part1(lines)
        part2(lines)
    }

    fun part1(input: List<String>) {
        val (defs, printouts) = parseLines(input)
        val sum = printouts.filter { isOrderCorrect(defs, it) }
            .sumOf { it.elementAt(it.size / 2) }
        println(sum)
    }

    fun part2(input: List<String>) {
        val (defs, printouts) = parseLines(input)
        val sum = printouts.filter { !isOrderCorrect(defs, it) }
            .map {  defs.sorted(it) }
            .sumOf { it.elementAt(it.size / 2) }
        println(sum)
    }

    fun parseLines(input: List<String>): Pair<Definitions, List<List<Long>>> {
        val (d, p) = input.filter { it.isNotBlank() }.partition { it.contains("|") }
        val defs = d.map { it.split("|") }.map { Pair(it[0].toLong(), it[1].toLong()) }
        val pages = p.map { it.split(",").map { it.toLong() } }
        return Pair(Definitions(defs), pages)
    }

    fun isOrderCorrect(definitions: Definitions, input: List<Long>): Boolean {
        return input.zip(definitions.sorted(input)).all { it.first == it.second }
    }

    class Definitions(defs: List<Pair<Long, Long>>) {
        val mustProceed = defs.groupBy(keySelector = { it.first }, { it.second })

        fun sorted(input: List<Long>) = input.sortedWith { left, right -> if (isCorrectPrecedence(left, right)) 1 else -1 }

        private fun isCorrectPrecedence(value: Long, other: Long): Boolean {
            return mustProceed[value] == null || !mustProceed[value]!!.contains(other)
        }
    }

}