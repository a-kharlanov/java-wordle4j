package ru.yandex.practicum;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WordleDictionaryLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void loadWordsShouldReadAllLinesFromFile() throws IOException {
        Path testFile = tempDir.resolve("test_words.txt");
        Files.writeString(testFile, "слово1\nслово2\nслово3");

        WordleDictionaryLoader loader = new WordleDictionaryLoader();
        List<String> words = loader.loadWords(testFile.toString());

        assertEquals(List.of("слово1", "слово2", "слово3"), words);
    }

    @Test
    void loadWordsShouldReturnEmptyList() throws IOException {
        Path testFile = tempDir.resolve("test_words.txt");
        Files.writeString(testFile, "");

        WordleDictionaryLoader loader = new WordleDictionaryLoader();
        List<String> words = loader.loadWords(testFile.toString());

        assertTrue(words.isEmpty());
    }

    @Test
    void loadWordsShouldThrowExceptionForMissingFile() {
        WordleDictionaryLoader loader = new WordleDictionaryLoader();

        assertThrows(IOException.class, () -> loader.loadWords("несуществующий_файл.txt"));
    }
}
