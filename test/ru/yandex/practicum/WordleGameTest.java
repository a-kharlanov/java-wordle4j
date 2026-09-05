package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WordleGameTest {

    private WordleDictionary dictionary;

    @BeforeEach
    void setUp() {
        List<String> testWords = List.of("манго", "насос", "сахар", "парта");
        dictionary = new WordleDictionary(testWords);
    }

    @Test
    void checkGuessShouldReturnAllPlusesForExactMatch() {
        WordleGame game = new WordleGame(dictionary, "манго");

        String result = game.checkGuess("манго");

        assertEquals("+++++", result);
    }

    @Test
    void checkGuessShouldReturnAllMinusForExactMismatch() {
        WordleGame game = new WordleGame(dictionary, "манго");

        String result = game.checkGuess("юрист");

        assertEquals("-----", result);
    }

    @Test
    void checkGuessShouldReturnPlusesAndMinusesForPartialMatch() {
        WordleGame game = new WordleGame(dictionary, "манго");

        String result = game.checkGuess("насос");

        assertEquals("^+-^-", result);

    }

    @Test
    void checkGuessShouldHandleRepeatedLetterInAnswer() {
        WordleGame game = new WordleGame(dictionary, "сахар");

        String result = game.checkGuess("начос");

        assertEquals("-+--^", result);

    }

    @Test
    void makeGuessShouldDecreaseStepsOnValidWord() throws WordNotFoundInDictionary {
        WordleGame game = new WordleGame(dictionary, "манго");

        game.makeGuess("манго");

        assertEquals(5, game.getSteps());
    }
    @Test
    void makeGuessShouldNotDecreaseStepsOnInvalidWord() {
        WordleGame game = new WordleGame(dictionary, "манго");

        assertThrows(WordNotFoundInDictionary.class, () -> game.makeGuess("зюзюк"));
        assertEquals(6, game.getSteps());
    }
    @Test
    void isWonShouldReturnFalseBeforeAnyGuess() {
        WordleGame game = new WordleGame(dictionary, "манго");

        assertFalse(game.isWon());
    }

    @Test
    void isWonShouldReturnTrueWhenGuessMatchesAnswer() throws WordNotFoundInDictionary {
        WordleGame game = new WordleGame(dictionary, "манго");

        game.makeGuess("манго");

        assertTrue(game.isWon());
    }
    @Test
    void updateConstraintsShouldNotExcludeLetterWithMatchElsewhere() throws WordNotFoundInDictionary {
        List<String> testWords = List.of("сахар", "такса", "парта");
        WordleDictionary customDictionary = new WordleDictionary(testWords);
        WordleGame game = new WordleGame(customDictionary, "сахар");

        game.makeGuess("такса");
        String suggestion = game.suggestWord();

        assertTrue(customDictionary.containsWord(suggestion));
    }
    @Test
    void suggestWordShouldNotReturnAlreadyUsedWord() throws WordNotFoundInDictionary {
        List<String> testWords = List.of("парта", "марка");
        WordleDictionary customDictionary = new WordleDictionary(testWords);
        WordleGame game = new WordleGame(customDictionary, "парта");

        game.makeGuess("парта");

        assertThrows(RuntimeException.class, game::suggestWord);
    }
}
