package org.twyszomirski.aoc.solutions;


import io.vavr.Tuple2;
import io.vavr.collection.Stream;
import org.twyszomirski.aoc.utils.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Gatherers;
import java.util.stream.IntStream;

import static java.io.IO.println;

public class Day_13 {

    public static void solve() throws IOException {
        var lines = Files.readAllLines(Paths.get("src/main/resources/input_day_13.txt"));
        println("======== Day 13 ===========");
        part_1(lines);
        part_2(lines);
    }

    private static void part_1(List<String> lines) {
        var games = getData(lines);
        var sum = games.stream().map(Day_13::solve).filter(Objects::nonNull)
                .mapToLong(buttons -> buttons.left() * 3 + buttons.right()).sum();
        println(sum);
    }

    private static void part_2(List<String> lines) {
        var addition = 10000000000000L;
        var games = getData(lines);
        var withAddition = games.stream().map(game -> new Game(game.buttonA, game.buttonB, new Prize(game.prize.x+addition, game.prize.y + addition)));
        var sum = withAddition.map(Day_13::solve).filter(Objects::nonNull)
                .mapToLong(buttons -> buttons.left() * 3 + buttons.right()).sum();
        println(sum);
    }

    private static Pair<Long> solve(Game game) {
        /*
            a * Ax + b* Bx = Prize.x
            a * Ay + b* By = Prize.y
        */

        var w = (game.buttonA.x * game.buttonB.y) - (game.buttonB.x * game.buttonA.y);
        var wX = (game.prize.x * game.buttonB.y) - (game.prize.y * game.buttonB.x);
        var wY = (game.buttonA.x * game.prize.y) - (game.buttonA.y * game.prize.x);
        if (w != 0L) {
            if ((wX % w == 0L && wY % w == 0L)) return new Pair<>((wX / w), (wY / w));
            else return null;
        }
        return null;
    }

    private static List<Game> getData(List<String> lines) {
        return lines.stream().filter(it -> !it.isBlank())
                .gather(Gatherers.windowFixed(3)).map(data ->
                {
                    var a = data.get(0);
                    var b = data.get(1);
                    var p = data.get(2);
                    var buttonA = new Button(Long.parseLong(a.substring(a.indexOf("X+") + 2, a.indexOf(","))), Long.parseLong(a.substring(a.indexOf("Y+") + 2)));
                    var buttonB = new Button(Long.parseLong(b.substring(b.indexOf("X+") + 2, b.indexOf(","))), Long.parseLong(b.substring(b.indexOf("Y+") + 2)));
                    var prize = new Prize(Long.parseLong(p.substring(p.indexOf("X=") + 2, p.indexOf(","))), Long.parseLong(p.substring(p.indexOf("Y=") + 2)));
                    return new Game(buttonA, buttonB, prize);
                }).toList();
    }

    record Button(long x, long y) {
    }

    record Prize(long x, long y) {
    }

    record Game(Button buttonA, Button buttonB, Prize prize) {
    }

}
