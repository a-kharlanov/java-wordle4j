package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WordleExceptionsTest {

    @Test
    void wordNotFoundInDictionaryShouldStoreMessage() {
        WordNotFoundInDictionary exception = new WordNotFoundInDictionary("Такого слова в словаре нет");

        assertEquals("Такого слова в словаре нет", exception.getMessage());
    }

    @Test
    void emptyDictionaryExceptionShouldStoreMessage() {
        EmptyDictionaryException exception = new EmptyDictionaryException("Список пуст");

        assertEquals("Список пуст", exception.getMessage());
    }
}