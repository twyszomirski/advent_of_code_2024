package org.twyszomirski.aoc.solutions

import java.io.File
import java.util.LinkedList


class Day_9 {

    fun solve() {
        val lines = File("src/main/resources/input_day_9.txt").readLines()
        println("======== Day 9 ===========")
        part1(lines)
        part2(lines)
    }

    fun part1(input: List<String>) {
        val diskData = getData(input)

        var currentEmptyPointer = 1
        var currentFilePointer = diskData.size - 1

        while (currentEmptyPointer <= currentFilePointer) {
            val (file, pointer) = nextFile(diskData, currentFilePointer)
            currentEmptyPointer = nextEmpty(diskData, currentEmptyPointer)
            currentFilePointer = pointer

            val prevFile = diskData[currentEmptyPointer - 1]
            if (prevFile.fileId == file.fileId) {
                prevFile.length++
            } else {
                diskData.add(currentEmptyPointer, DiskPart(1, file.fileId))
                currentEmptyPointer++
                currentFilePointer++
            }
            diskData[currentEmptyPointer].length--

            if (diskData[currentEmptyPointer].length == 0) {
                diskData.removeAt(currentEmptyPointer)
                currentFilePointer--
            }
        }

        val sum = diskData.filter { it.fileId != null }
            .flatMap { file -> (0..<file.length).map { file.fileId!! } }
            .foldIndexed(0L) { idx, acc, id ->
                acc + (idx * id)
            }

        println(sum)
    }

    fun part2(input: List<String>) {
        val data = getData(input)

        var currentFilePointer = data.size - 1

        while (currentFilePointer > 0) {
            val file = data[currentFilePointer]
            val currentFileId = file.fileId
            val emptyIdx = matchingEmpty(data, file.length, currentFilePointer)
            if (emptyIdx > 0) {
                val empty = data[emptyIdx]
                if (empty.length == file.length) {
                    data.removeAt(emptyIdx)
                    data.add(emptyIdx, DiskPart(file.length, file.fileId))
                } else {
                    data.add(emptyIdx, DiskPart(file.length, file.fileId))
                    empty.length -= file.length
                    currentFilePointer++
                }
                data[currentFilePointer].fileId = null
            }

            while (currentFilePointer > 0) {
                currentFilePointer--
                if (data[currentFilePointer].fileId != null && data[currentFilePointer].fileId!! < currentFileId!!) {
                    break
                }
            }
        }

        val sum = data.flatMap { file -> (0..<file.length).map { file.fileId ?: 0 } }
            .foldIndexed(0L) { idx, acc, id ->
                acc + (idx * id)
            }
        println(sum)

    }

    fun getData(lines: List<String>): LinkedList<DiskPart> {
        return lines.first().split("").filter {
            it.isNotBlank()
        }.map { it.toInt() }.mapIndexed { idx, size ->
            val fileId = if (idx % 2 == 0) idx / 2 else null
            DiskPart(size, fileId)
        }.toCollection(LinkedList())
    }


    fun nextFile(diskData: MutableList<DiskPart>, currPointer: Int): Pair<DiskPart, Int> {
        val file = diskData[currPointer]
        if (file.length == 1) {
            diskData.removeAt(currPointer)
            return file to currPointer - 2
        } else {
            file.length--
            return file to currPointer
        }
    }

    fun nextEmpty(diskData: MutableList<DiskPart>, currPointer: Int): Int {
        return (currPointer - 1) + diskData.drop(currPointer - 1).indexOfFirst { it.fileId == null && it.length > 0 }
    }

    fun matchingEmpty(data: List<DiskPart>, size: Int, maxId: Int): Int {
        return data.subList(0, maxId).indexOfFirst { it.fileId == null && it.length >= size }
    }

    data class DiskPart(var length: Int, var fileId: Int?) {
        override fun toString(): String {
            val symbol = fileId?.toString() ?: "."
            return (0..<length).joinToString("") { symbol }
        }
    }


}