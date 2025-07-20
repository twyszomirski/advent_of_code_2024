package org.twyszomirski.aoc.solutions

import java.io.File
import kotlin.math.abs
import kotlin.math.sign


class Day_2 {

    fun solve(){
        val lines = File("src/main/resources/input_day_2.txt").readLines()
        println("======== Day 2 ===========")
        part1(lines)
        part2(lines)
    }

    fun part1(input: List<String>) {
        val count = input.map { it.split(" ").map { it.toLong() }
        }.count { isGoodReport(it) }

        println(count)
    }

    fun part2(input: List<String>) {
        val count = input.map { it.split(" ").map { it.toLong() }
        }.count { isGoodReportWithRemovalOption(it) }

        println(count)
    }

    fun isGoodReportWithRemovalOption(report: List<Long>): Boolean {
        if(isGoodReport(report)) return true

        (0 .. report.size).forEach {
            val isGood = isGoodReport(report.filterIndexed { idx, _ -> idx != it })
            if(isGood) return true
        }
        return false
    }

    fun isGoodReport(report: List<Long>): Boolean {
        val direction = (report[0] - report[1]).sign
        (0..<report.size - 1).forEach {
            val distance = report[it] - report[it + 1]
            val distanceAbs = abs(distance)
            if (distanceAbs < 1 || distanceAbs > 3) {
                return false
            }
            if (direction != distance.sign) {
                return false
            }
        }
        return true
    }
}