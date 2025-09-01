package org.twyszomirski.aoc.solutions

import java.io.File


class Day_11 {

    fun solve() {
        val lines = File("src/main/resources/input_day_11.txt").readLines()
        println("======== Day 11 ===========")
        part1(lines)
        part2(lines)
    }

    fun part1(input: List<String>) {
        val data = input.first().split(" ").filter { it.isNotBlank() }.map { it.toLong() }
        val sum = data.sumOf { blinkStone(listOf(it), 25) }
        println(sum)
    }

    fun part2(input: List<String>) {
        val data = input.first().split(" ").filter { it.isNotBlank() }.map { it.toLong() }
        val cache = mutableMapOf<String, Long>()
        val sum = data.sumOf { blinkStoneWithImprovments(it, 75, cache) }
        println(sum + data.size)
    }


    fun blinkStone(stones: List<Long>, times: Int): Int {
        var current = stones
        repeat(times) {
            current = current.map { stone ->
                val stoneSize = stone.toString().length
                when {
                    stone == 0L -> listOf(1L)
                    stoneSize % 2 == 0 -> listOf(
                        stone.toString().substring(0, stoneSize / 2).toLong(),
                        stone.toString().substring(stoneSize / 2, stoneSize).toLong()
                    )

                    else -> listOf(stone * 2024)
                }
            }.flatten()
        }
        return current.size
    }


    fun blinkStoneWithImprovments(stone: Long, times: Int, cache: MutableMap<String, Long>): Long {

        if (times == 0) {
            return 0
        }

        val cacheKey = cacheKey(stone, times)
        val hit = cache[cacheKey]
        if (hit != null) {
            return hit
        }

        val stoneSize = stone.toString().length
        var local = 0
        val global = when {
            stone == 0L -> blinkStoneWithImprovments(1L, times - 1, cache)
            stoneSize % 2 == 0 -> {
                local = 1
                blinkStoneWithImprovments(
                    stone.toString().substring(0, stoneSize / 2).toLong(), times - 1, cache
                ) +
                        blinkStoneWithImprovments(
                            stone.toString().substring(stoneSize / 2, stoneSize).toLong(),
                            times - 1,
                            cache
                        )

            }

            else -> blinkStoneWithImprovments(stone * 2024, times - 1, cache)
        }
        val result = global + local
        cache.put(stone.toString() + "_" + times, result)
        return result
    }

    private fun cacheKey(stone: Long, times: Int) = stone.toString() + "_" + times
}
