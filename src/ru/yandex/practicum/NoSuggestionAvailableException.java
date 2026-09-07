package ru.yandex.practicum;

public class NoSuggestionAvailableException extends Exception {
    public NoSuggestionAvailableException(String message) {
        super(message);
    }
}