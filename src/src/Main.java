package src;

import java.util.*;

import static src.Game.*;

public class Main {

    // Toggle to run the built-in tests once when the program starts
    private static final boolean RUN_TESTS_ON_START = true;

    public static void main(String[] args) {

        if (RUN_TESTS_ON_START) {
            runSelfTests();
        }

        src.Game.mainMenu();

        Scanner scanner = new Scanner(System.in);
        String scannedInput = scanner.nextLine().trim();

        while (scannedInput.equals("1")) {

            String typedWord;

            String[] wordArray = src.Game.randomWord();
            List<String> wordList = Arrays.asList(wordArray);
            Random random = new Random();
            String randomWord = wordList.get(random.nextInt(wordList.size()));
            char[] arrayRandomWord = randomWord.toCharArray();
            System.out.print("\nLet's play!\n(Type HINT at any time to see how many distinct letters repeat in the answer.)\n");
            System.out.print("Type your first guess: ");

            int tries = 1;
            while (tries <= 6) {
                typedWord = scanner.nextLine().trim().toUpperCase(Locale.ROOT);

                // Support a hint command that does NOT consume a try
                if (typedWord.equals("HINT")) {
                    int repeats = hintRepeatedCharCount(randomWord);
                    System.out.println("Hint: The answer has " + repeats + " repeated character" + (repeats == 1 ? "" : "s") + ".");
                    System.out.print(tries + ". guess (5-letter word): ");
                    continue; // don't increment tries, don't evaluate
                }

                while ((typedWord.length() != 5) || (!wordList.contains(typedWord))) {
                    System.out.print("Invalid guess. Try again (or type HINT): ");
                    typedWord = scanner.nextLine().trim().toUpperCase(Locale.ROOT);
                    if (typedWord.equals("HINT")) {
                        int repeats = hintRepeatedCharCount(randomWord);
                        System.out.println("Hint: The answer has " + repeats + " repeated character" + (repeats == 1 ? "" : "s") + ".");
                        System.out.print(tries + ". guess (5-letter word): ");
                        typedWord = scanner.nextLine().trim().toUpperCase(Locale.ROOT);
                    }
                }

                char[] arrayTypedWord = typedWord.toCharArray();

                System.out.print(tries + ". guess: ");
                if (didWin(arrayTypedWord, arrayRandomWord)) {
                    for (int i = 0; i < 5; i++) {
                        System.out.print(ANSI_GREEN_BACKGROUND + " " + arrayTypedWord[i] + " " + ANSI_RESET);
                    }
                    System.out.println("\n\nCongrats! You solved the WORDLE in \u001B[32m" + tries + "\u001B[0m tries.");
                    tries = 6; // end after printing solution row
                } else {
                    for (int i = 0; i < 5; i++) {

                        int countCharTyped = typedWord.length() - typedWord.replaceAll(String.valueOf(arrayTypedWord[i]), "").length();
                        int countCharRandom = randomWord.length() - randomWord.replaceAll(String.valueOf(arrayTypedWord[i]), "").length();
                        int colorTiles = countCharRandom;

                        if (randomWord.contains(String.valueOf(arrayTypedWord[i]))) {
                            if (arrayTypedWord[i] == arrayRandomWord[i]) {
                                System.out.print(ANSI_GREEN_BACKGROUND + " " + arrayTypedWord[i] + " " + ANSI_RESET);
                            } else {
                                if (countCharTyped <= countCharRandom) {
                                    System.out.print(ANSI_YELLOW_BACKGROUND + " " + arrayTypedWord[i] + " " + ANSI_RESET);
                                } else {
                                    for (int j = 0; j <= i; j++) {
                                        if (arrayTypedWord[j] == arrayTypedWord[i]) colorTiles--;
                                    }
                                    for (int k = 0; k < 5; k++) {
                                        if (arrayTypedWord[k] == arrayRandomWord[i]) colorTiles--;
                                    }
                                    if (colorTiles > 0) {
                                        System.out.print(ANSI_YELLOW_BACKGROUND + " " + arrayTypedWord[i] + " " + ANSI_RESET);
                                    } else {
                                        if (countCharRandom > 0) {
                                            int checkAppearPrevious = 0;
                                            int countGreenTiles = 0;
                                            for (int j = 0; j < i; j++) {
                                                if (arrayTypedWord[j] == arrayTypedWord[i]) {
                                                    checkAppearPrevious++;
                                                }
                                            }
                                            for (int k = 0; k < 5; k++) {
                                                if (arrayTypedWord[k] == arrayRandomWord[k] && arrayTypedWord[i] == arrayTypedWord[k])
                                                    countGreenTiles++;
                                            }
                                            if (checkAppearPrevious == 0 && countGreenTiles == 0)
                                                System.out.print(ANSI_YELLOW_BACKGROUND + " " + arrayTypedWord[i] + " " + ANSI_RESET);
                                            else
                                                System.out.print(ANSI_BLACK_BACKGROUND + " " + arrayTypedWord[i] + " " + ANSI_RESET);
                                        } else {
                                            System.out.print(ANSI_BLACK_BACKGROUND + " " + arrayTypedWord[i] + " " + ANSI_RESET);
                                        }
                                    }
                                }
                            }
                        } else {
                            System.out.print(ANSI_BLACK_BACKGROUND + " " + arrayTypedWord[i] + " " + ANSI_RESET);
                        }
                    }
                    System.out.println();
                }

                if (tries == 6 && !didWin(arrayTypedWord, arrayRandomWord)) {
                    System.out.println("\nLooks like you didn't win this time.");
                }
                tries++;
            }

            System.out.print("The word is: ");
            for (int i = 0; i < 5; i++) {
                System.out.print(ANSI_GREEN_BACKGROUND + " " + arrayRandomWord[i] + " " + ANSI_RESET);
            }

            System.out.println("\n\nEnter 1 to continue.\nEnter 0 to exit.");
            scannedInput = scanner.nextLine().trim();
        }

        System.out.println("Invalid input. Exiting program!");
    }

    /**
     * Requirement 1: Return true if the guess exactly equals the answer.
     */
    public static boolean didWin(char[] arrayTypedWord, char[] arrayRandomWord) {
        return Arrays.equals(arrayTypedWord, arrayRandomWord);
    }

    /**
     * Requirement 2: Count how many DISTINCT letters in 'answer' occur 2+ times.
     * Example: "BANANA" -> 2 (A and N). Case-insensitive.
     */
    public static int hintRepeatedCharCount(String answer) {
        if (answer == null) return 0;
        String s = answer.toUpperCase(Locale.ROOT);
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : s.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }
        int repeats = 0;
        for (int count : freq.values()) {
            if (count >= 2) repeats++;
        }
        return repeats;
    }

    /**
     * Minimal self-tests (prints results to console).
     * Satisfies: "Test with at least 3 different game boards."
     */
    private static void runSelfTests() {
        System.out.println("\n--- Running self tests ---");

        // didWin tests on 3 different boards
        System.out.println("didWin tests:");
        System.out.println("1) GUESS=APPLE, ANSWER=APPLE -> " +
                didWin("APPLE".toCharArray(), "APPLE".toCharArray())); // true
        System.out.println("2) GUESS=GRAPE, ANSWER=GRAPH -> " +
                didWin("GRAPE".toCharArray(), "GRAPH".toCharArray())); // false
        System.out.println("3) GUESS=SKILL, ANSWER=SKULL -> " +
                didWin("SKILL".toCharArray(), "SKULL".toCharArray())); // false

        // hint tests
        System.out.println("\nhintRepeatedCharCount tests:");
        System.out.println("BANANA -> " + hintRepeatedCharCount("BANANA") + " (expected 2)");
        System.out.println("LEVEL -> " + hintRepeatedCharCount("LEVEL") + " (expected 2: L and E)");
        System.out.println("ROBOT -> " + hintRepeatedCharCount("ROBOT") + " (expected 1: O)");
        System.out.println("PLANT -> " + hintRepeatedCharCount("PLANT") + " (expected 0)");

        System.out.println("--- End self tests ---\n");
    }
}
