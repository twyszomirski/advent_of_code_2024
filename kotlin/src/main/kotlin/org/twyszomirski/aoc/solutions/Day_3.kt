package org.twyszomirski.aoc.solutions

import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign


class Day_3 {

    fun solve() {
        val lines = File("src/main/resources/input_day_3.txt").readLines()
        println("======== Day 3 ===========")
        part1(lines)
        part2(lines)
    }

    fun part1(lines: List<String>) {
        val sum = lines.joinToString(separator = "")
            .windowedSequence(12)
            .mapNotNull { window ->
                if (window.startsWith("mul(")) {
                    parse(window.substring(4)).result
                } else null
            }
            .sumOf { it.first * it.second }

        println(sum)
    }

    fun parse(candidate: String): ParseResult {
        val firstNumber = getPostFixedNumber(candidate.substring(0, min(4, candidate.length)), ',')
        if (firstNumber != null) {
            val secondNumber = getPostFixedNumber(candidate.substring(firstNumber.length + 1), ')')
            if (secondNumber != null) {
                return ParseResult(
                    firstNumber.length + secondNumber.length + 2,
                    Pair(firstNumber.toInt(), secondNumber.toInt())
                )
            }
        }
        return ParseResult(0, null)
    }

    fun getPostFixedNumber(input: String, postfix: Char): String? {
        if (!input.contains(postfix)) return null
        return input.substringBefore(postfix).takeIf { it.all { char -> char.isDigit() } }
    }


    fun part2(lines: List<String>) {
        val input = lines.joinToString(separator = "")

        var currentIdx = 0
        val result = mutableListOf<String>()
        var nextDont = input.indexOf("don't()")

        while (currentIdx < input.length) {
            result.add(input.substring(currentIdx, nextDont))
            currentIdx = input.indexOf("do()", nextDont).takeIf { it > 0 } ?: input.length
            nextDont = input.indexOf("don't()", currentIdx).takeIf { it > 0 } ?: input.length
        }
        val trimmed = result.joinToString(separator = "")
        part1(listOf(trimmed))
    }


}

data class ParseResult(val advance: Int, val result: Pair<Int, Int>?)