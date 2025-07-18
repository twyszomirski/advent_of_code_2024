package org.twyszomirski.org.twyszomirski

import org.twyszomirski.aoc.solutions.Day_1
import java.io.File


fun main() {
    val lines = File("src/main/resources/input_day_1.txt").readLines()
    Day_1().part1(lines)
    Day_1().part2(lines)
}

