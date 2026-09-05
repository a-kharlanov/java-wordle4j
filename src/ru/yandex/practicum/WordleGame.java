package ru.yandex.practicum;

import java.util.*;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {
    private static final int MAX_STEPS = 6;
    private static final int WORD_LENGTH = 5;

    private final String answer;

    private int steps;

    private final WordleDictionary dictionary;

    private String lastGuess;

    private final Set<Character> excludedLetters; // буквы, которых точно нет в слове
    private final Map<Integer, Character> exactPositions; // позиция -> точная буква на ней
    private final Map<Character, Set<Integer>> wrongPositions; // буква -> множество позиций, где она есть, но не должна там быть
    private final Set<String> usedWords;

    public WordleGame(WordleDictionary dictionary) {
        this(dictionary, dictionary.getRandomWord());
    }

    public WordleGame(WordleDictionary dictionary, String answer) {
        this.answer = answer;
        this.steps = MAX_STEPS;
        this.dictionary = dictionary;
        this.excludedLetters = new HashSet<>();
        this.exactPositions = new HashMap<>();
        this.wrongPositions = new HashMap<>();
        this.usedWords = new HashSet<>();
    }

    public String checkGuess(String word) {
        if (word.length() != answer.length()) {
            throw new IllegalStateException(
                    "checkGuess вызван со словом некорректной длины: " + word);
        }

        char[] result = new char[WORD_LENGTH];

        Map<Character, Integer> availableLetters = new HashMap<>();

        for (int i = 0; i < word.length(); i++) {
            char currentAnswer = this.answer.charAt(i);
            char currentWord = word.charAt(i);
            if (currentAnswer == currentWord) {
                result[i] = '+';
            } else {
                availableLetters.put(currentAnswer, availableLetters.getOrDefault(currentAnswer, 0) + 1);
            }
        }

        for (int i = 0; i < word.length(); i++) {
            if (result[i] == '\u0000') {
                char currentWord = word.charAt(i);
                if (availableLetters.getOrDefault(currentWord, 0) > 0) {
                    result[i] = '^';
                    availableLetters.put(currentWord, availableLetters.get(currentWord) - 1);
                } else {
                    result[i] = '-';
                }
            }
        }
        return new String(result);
    }

    public String makeGuess(String word) throws WordNotFoundInDictionary, InvalidWordLengthException {
        String normalizedWord = WordleDictionary.normalize(word);

        if (normalizedWord.length() != WORD_LENGTH) {
            throw new InvalidWordLengthException("Слово должно состоять из 5 букв");
        }

        if (!this.dictionary.containsWord(normalizedWord)) {
            throw new WordNotFoundInDictionary("Такого слова в словаре нет");
        }

        this.steps--;
        return evaluateGuess(normalizedWord);
    }

    public String requestHint() throws NoSuggestionAvailableException {
        String suggestedWord = suggestWord();
        return evaluateGuess(suggestedWord);
    }

    private String evaluateGuess(String normalizedWord) {
        String result = checkGuess(normalizedWord);
        this.lastGuess = normalizedWord;
        updateConstraints(normalizedWord, result);
        usedWords.add(normalizedWord);
        return result;
    }


    public boolean isWon() {
        return this.answer.equals(this.lastGuess);
    }

    public int getSteps() {
        return steps;
    }

    public String getAnswer() {
        return answer;
    }

    public String getLastGuess() {
        return lastGuess;
    }

    public void updateConstraints(String word, String result) {
        for (int i = 0; i < result.length(); i++) {
            char hint = result.charAt(i);
            char letter = word.charAt(i);

            switch (hint) {
                case '+' -> exactPositions.put(i, letter);
                case '^' -> wrongPositions.computeIfAbsent(letter, k -> new HashSet<>()).add(i);
                case '-' -> {
                    if (!letterHasMatchElsewhere(word, result, letter)) {
                        excludedLetters.add(letter);
                    }
                }
            }
        }
    }

    private boolean letterHasMatchElsewhere(String word, String result, char letter) {
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter && result.charAt(i) != '-') {
                return true;
            }
        }
        return false;
    }

    public String suggestWord() throws NoSuggestionAvailableException {
        List<String> candidates = new ArrayList<>();

        for (String candidate : dictionary.getWords()) {
            if (usedWords.contains(candidate)) {
                continue;
            }

            boolean hasExcludedLetter = false;

            for (int i = 0; i < candidate.length(); i++) {
                char letter = candidate.charAt(i);
                if (excludedLetters.contains(letter)) {
                    hasExcludedLetter = true;
                    break;
                }
            }
            if (!hasExcludedLetter) {
                candidates.add(candidate);
            }
        }

        List<String> filteredByWrongPositions = new ArrayList<>();

        for (String candidate : candidates) {
            boolean isValid = true;

            for (char letter : wrongPositions.keySet()) {
                boolean letterFound = false;
                for (int i = 0; i < candidate.length(); i++) {
                    if (candidate.charAt(i) == letter) {
                        letterFound = true;
                        if (wrongPositions.get(letter).contains(i)) {
                            isValid = false;
                            break;
                        }
                    }
                }
                if (!letterFound) {
                    isValid = false;
                }
            }

            if (isValid) {
                filteredByWrongPositions.add(candidate);
            }
        }

        List<String> filteredByExactPosition = filterByExactPositions(filteredByWrongPositions);

        Random random = new Random();

        int randomIndex = random.nextInt(filteredByExactPosition.size());

        return filteredByExactPosition.get(randomIndex);
    }

    private List<String> filterByExactPositions(List<String> filteredByWrongPositions)
            throws NoSuggestionAvailableException {
        List<String> filteredByExactPosition = new ArrayList<>();

        for (String candidate : filteredByWrongPositions) {
            boolean isValid = true;

            for (Map.Entry<Integer, Character> entry : exactPositions.entrySet()) {
                int position = entry.getKey();
                char letter = entry.getValue();

                if (candidate.charAt(position) != letter) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) {
                filteredByExactPosition.add(candidate);
            }
        }

        if (filteredByExactPosition.isEmpty()) {
            throw new NoSuggestionAvailableException("Подходящих слов не осталось");
        }
        return filteredByExactPosition;
    }
}
