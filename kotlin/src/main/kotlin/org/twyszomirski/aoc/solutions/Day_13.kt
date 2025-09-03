package org.twyszomirski.aoc.solutions

import java.io.File


class Day_13 {

    fun solve() {
        val lines = File("src/main/resources/input_day_13.txt").readLines()
        println("======== Day 13 ===========")
        part1(lines)
        part2(lines)
    }

    fun part1(input: List<String>) {
        val data = readData(input)

        val sum = data.map {
            val onX = findSolutions(game = it, pointAccessor = { button -> button.x }, prizeAccessor = { p -> p.x })
            val onY = findSolutions(game = it, pointAccessor = { button -> button.y }, prizeAccessor = { p -> p.y })
            onX.intersect(onY)
        }.map { it.sortedBy { it.first * 3 + it.second } }
            .mapNotNull { it.firstOrNull() }.sumOf { it.first * 3 + it.second }

        println(sum)
    }

    fun part2(input: List<String>) {
        val data = readData(input)
        val addition = 10000000000000L
        val sum = data
            .map { Game(it.buttonA, it.buttonB, Prize(it.prize.x + addition, it.prize.y + addition)) }
            .mapNotNull {
                findSolutionsByEquation(it)
            }
            .sumOf { it.first * 3 + it.second }

        println(sum)
    }

    fun findSolutions(
        game: Game,
        pointAccessor: (Button) -> Long,
        prizeAccessor: (Prize) -> Long
    ): List<Pair<Long, Long>> {
        val prize = prizeAccessor(game.prize)
        val aValue = pointAccessor(game.buttonA)
        val bValue = pointAccessor(game.buttonB)
        val sol = if (aValue > bValue) {
            findSolutions(aValue, bValue, prize)
        } else {
            findSolutions(bValue, aValue, prize)
                .map { Pair(it.second, it.first) }
        }
        return sol
    }

    fun findSolutions(bigger: Long, lower: Long, result: Long): List<Pair<Long, Long>> {
        var countOfBigger = result / bigger
        val matches = mutableListOf<Pair<Long, Long>>()
        while (countOfBigger > 0) {
            val rest = result - (countOfBigger * bigger)
            val countOfLower = rest / lower
            if (addsUp(bigger, lower, result, countOfBigger, countOfLower)) {
                matches.add(Pair(countOfBigger, countOfLower))
            }
            countOfBigger--
        }
        return matches
    }

    fun findSolutionsByEquation(
        game: Game
    ): Pair<Long, Long>? {
        /*
            a * Ax + b* Bx = Prize.x
            a * Ay + b* By = Prize.y
        */

        val w = (game.buttonA.x * game.buttonB.y) - (game.buttonB.x * game.buttonA.y)
        val wX = (game.prize.x * game.buttonB.y) - (game.prize.y * game.buttonB.x)
        val wY = (game.buttonA.x * game.prize.y) - (game.buttonA.y * game.prize.x)
        if (w != 0L) {
            return if ((wX % w == 0L && wY % w == 0L)) Pair((wX / w), (wY / w)) else null
        }
        return null
    }

    fun addsUp(bigger: Long, lower: Long, result: Long, countOfBigger: Long, countOfLower: Long): Boolean {
        return (bigger * countOfBigger) + (lower * countOfLower) == result
    }


    private fun readData(lines: List<String>): List<Game> {
        return lines.filter { it.isNotBlank() }.windowed(3, 3).map {
            val buttonA = it.get(0)
            val buttonB = it.get(1)
            val prize = it.get(2)

            val a = Button(
                buttonA.substring(buttonA.indexOf("X+") + 2, buttonA.indexOf(",")).toLong(),
                buttonA.substring(buttonA.indexOf("Y+") + 2).toLong())

            val b = Button(
                buttonB.substring(buttonB.indexOf("X+") + 2, buttonB.indexOf(",")).toLong(),
                buttonB.substring(buttonB.indexOf("Y+") + 2).toLong())

            val p = Prize(
                prize.substring(prize.indexOf("X=") + 2, prize.indexOf(",")).toLong(),
                prize.substring(prize.indexOf("Y=") + 2).toLong()
            )

            Game(a, b, p)
        }
    }
}

data class Button(val x: Long, val y: Long)
data class Prize(val x: Long, val y: Long)
data class Game(val buttonA: Button, val buttonB: Button, val prize: Prize)


