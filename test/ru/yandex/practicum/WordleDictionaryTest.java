package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class WordleDictionaryTest {

    private WordleDictionary dictionary;

    @BeforeEach
    void setUp() {
        List<String> testWords = List.of("актёр", "поток", "книга", "город", "лампа");
        dictionary = new WordleDictionary(testWords);
    }

    @Test
    void constructorShouldFilterAndNormalizeWords() {
        List<String> testWords = List.of("Поток", "  голова    ", "актёр", "ёж", " ЖИЛЬЁ");
        List<String> expectedWords = List.of("поток", "актер", "жилье");

        WordleDictionary customDictionary = new WordleDictionary(testWords);

        assertEquals(expectedWords, customDictionary.getWords());
    }

    @Test
    void normalizeShouldTrimSpaces() {
        assertEquals("слово", WordleDictionary.normalize("   слово     "));
    }

    @Test
    void normalizeShouldToLowerCase() {
        assertEquals("слово", WordleDictionary.normalize("СЛОВО"));
    }

    @Test
    void normalizeShouldReplaceLetter() {
        assertEquals("жилье", WordleDictionary.normalize("жильё"));
    }

    @Test
    void getRandomWordShouldReturnWordFromDictionary() {
        String randomWord = dictionary.getRandomWord();

        assertTrue(dictionary.containsWord(randomWord));
    }

    @Test
    void containsWordShouldReturnTrueForExactWord() {
        assertTrue(dictionary.containsWord("поток"));
    }

    @Test
    void containsWordShouldNormalizeInputBeforeChecking() {
        assertTrue(dictionary.containsWord("  ПОТОК  "));
    }

    @Test
    void containsWordShouldReturnFalseForUnknownWord() {
        assertFalse(dictionary.containsWord("облако"));
    }
}
