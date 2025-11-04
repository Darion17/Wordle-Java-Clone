/*
===========================================
- Title:  Wordle Java Clone
- Author: @zerot69
- Date:   15 Feb 2022
============================================
*/

package src;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Game {

    // Declaring text and background colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";

    // Preferred resource path on classpath
    private static final String WORDS_RESOURCE = "/5-letter-words-list.txt";

    // Some common file locations if running from filesystem
    private static final Path[] FALLBACK_PATHS = new Path[] {
            Paths.get("5-letter-words-list.txt"),
            Paths.get("resources/5-letter-words-list.txt"),
            Paths.get("data/5-letter-words-list.txt")
    };

    // Built-in list so the game still runs with no file present
    private static final List<String> BUILTIN = List.of(
            "APPLE","GRAPE","ROBOT","LEVEL","PLANT","SKILL","BRICK","STONE","WATER","MONEY"
    );

    public static void mainMenu() {
        //Menu options
        System.out.println();
        System.out.println("\u001B[32mWORDLE:\u001B[0m");
        System.out.println("Guess the WORDLE in 6 tries.");
        System.out.println("Each guess must be a valid 5 letter word. Hit the enter button to submit.");
        System.out.println("After each guess, the color of the tiles will change to show how close your guess was to the word.");
        System.out.println("\nExamples:");
        System.out.println(ANSI_GREEN_BACKGROUND + " W " + ANSI_YELLOW_BACKGROUND + " O " + ANSI_BLACK_BACKGROUND + " R " + " D " + " S " + ANSI_RESET);
        System.out.println("The letter " + ANSI_GREEN_BACKGROUND + " W " + ANSI_RESET + " is in the word and in the correct spot.");
        System.out.println("The letter " + ANSI_YELLOW_BACKGROUND + " O " + ANSI_RESET + " is in the word but in the wrong spot.");
        System.out.println("The letters " + ANSI_BLACK_BACKGROUND + " R " + ANSI_RESET + ", " + ANSI_BLACK_BACKGROUND + " D " + ANSI_RESET + " and " + ANSI_BLACK_BACKGROUND + " S " + ANSI_RESET + " are not in the word in any spot.");
        System.out.println("\nEnter 1 to play!");
    }

    /**
     * Returns the sanitized list of valid words as an array (UPPERCASE, 5 letters, deduped).
     * Your Main expects this method to produce the candidate dictionary.
     */
    public static String[] randomWord() {
        List<String> words = loadWordList();
        return words.toArray(new String[0]);
    }

    // ---------- Helpers ----------

    private static List<String> loadWordList() {
        // 1) Try classpath
        List<String> cp = readClasspathResource(WORDS_RESOURCE);
        if (!cp.isEmpty()) return cp;

        // 2) Try filesystem fallbacks
        for (Path p : FALLBACK_PATHS) {
            if (Files.exists(p)) {
                try {
                    List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);
                    List<String> cleaned = sanitize(lines);
                    if (!cleaned.isEmpty()) return cleaned;
                } catch (Exception ignored) { /* try the next path */ }
            }
        }

        // 3) Built-in fallback (guarantees the game runs)
        return BUILTIN;
    }

    private static List<String> readClasspathResource(String resourcePath) {
        try (InputStream is = Game.class.getResourceAsStream(resourcePath)) {
            if (is == null) return Collections.emptyList();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                List<String> lines = br.lines().collect(Collectors.toList());
                return sanitize(lines);
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /** Keep only 5-letter A–Z words, uppercase them, dedupe. */
    private static List<String> sanitize(List<String> raw) {
        return raw.stream()
                .map(s -> s == null ? "" : s.trim())
                .filter(s -> s.matches("(?i)^[A-Z]{5}$"))
                .map(String::toUpperCase)
                .distinct()
                .collect(Collectors.toList());
    }
}
