package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {
    private final List<String> words;

    public WordleDictionary(List<String> words) {
        this.words = new ArrayList<>();

        for (String word : words) {
            String wordClear = normalize(word);
            if (wordClear.length() == 5) {
                this.words.add(wordClear);
            }
        }
    }

    public static String normalize(String word) {
        return word.trim().toLowerCase().replace("ё", "е");
    }

    public String getRandomWord() {

        Random random = new Random();

        int randomIndex = random.nextInt(words.size());

        return words.get(randomIndex);
    }

    public boolean containsWord (String word) {
        return words.contains(normalize(word));
    }

    public boolean isEmpty() {
        return words.isEmpty();
    }

    public List<String> getWords() {
        return List.copyOf(words);
    }
}
