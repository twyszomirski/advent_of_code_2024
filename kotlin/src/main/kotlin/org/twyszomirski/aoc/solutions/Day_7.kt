package org.twyszomirski.aoc.solutions

import java.io.File


class Day_7 {

    fun solve() {
        val lines = File("src/main/resources/input_day_7.txt").readLines()
        println("======== Day 7 ===========")
        part1(lines)
        part2(lines)
    }

    fun part1(input: List<String>) {
        val data = readData(input)
        val res = data.filter { testOperations(it, setOf(Symbol.SUM, Symbol.MULTIPLY))}.sumOf { it.first }
        println(res)
    }

    fun part2(input: List<String>) {
        val data = readData(input)
        val res = data.filter { testOperations(it, setOf(Symbol.SUM, Symbol.MULTIPLY, Symbol.CONCAT))}.sumOf { it.first }
        println(res)
    }

    fun readData(input: List<String>) = input.map {
        it.split(":")
    }.map { Pair(it[0].toLong(), it[1].split(" ").filter { it.isNotBlank() }.map { it.toLong() }) }


    fun testOperations(data: Pair<Long, List<Long>>, symbols: Set<Symbol>): Boolean {
        val combinations = createCombinations(data.second.size - 1, symbols)
        return combinations.any { c ->
            val result = data.second.drop(1).foldIndexed(data.second.first()) { index, acc, element ->
                c[index].process(acc, element)
            }
            result == data.first
        }
    }

    fun createCombinations(length: Int, symbols: Set<Symbol>): List<List<Symbol>> {
        var res: List<List<Symbol>> = symbols.map { listOf(it) }

        repeat(length - 1) {
            res = res.flatMap { pref ->
                symbols.map { sym ->
                    pref + sym
                }
            }
        }
        return res
    }

    enum class Symbol(val operation: (Long, Long) -> Long) {
        SUM ({ a, b -> a + b }),
        MULTIPLY({ a, b -> a * b }),
        CONCAT({ a, b -> (a.toString() + b.toString()).toLong() });

        fun process(a : Long, b: Long) = operation(a, b)
    }

}