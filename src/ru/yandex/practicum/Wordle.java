package ru.yandex.practicum;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        try (PrintWriter log = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream("wordle.log"),
                        StandardCharsets.UTF_8))) {

            try {
                WordleDictionaryLoader wordleDictionaryLoader = new WordleDictionaryLoader();
                List<String> words = wordleDictionaryLoader.loadWords("words_ru.txt");

                WordleDictionary wordleDictionary = new WordleDictionary(words);

                if (wordleDictionary.isEmpty()) {
                    throw new EmptyDictionaryException("Список пуст");
                }

                WordleGame wordleGame = new WordleGame(wordleDictionary);

                while (!wordleGame.isWon() && wordleGame.getSteps() != 0) {
                    System.out.println("Попыток: " + wordleGame.getSteps());
                    System.out.println("Введите слово: ");
                    try {
                        String userWord = scanner.nextLine();

                        if (userWord.trim().isEmpty()) {
                            userWord = wordleGame.suggestWord();
                        }
                        String result = wordleGame.makeGuess(userWord);

                        System.out.println(userWord);
                        System.out.println(result);
                    } catch (WordNotFoundInDictionary e) {
                        System.out.println("Такого слова нет в словаре");
                    }
                }

                if (wordleGame.isWon()) {
                    System.out.printf("Поздравляем, вы выиграли! Загаданное слово: %s", wordleGame.getAnswer());
                } else {
                    System.out.printf("К сожалению, вы проиграли, загаданное слово: %s", wordleGame.getAnswer());
                }
            } catch (Exception e) {
                log.println("Критическая ошибка: " + e.getMessage());
            }


        } catch (IOException e) {
            System.out.println("Не удалось создать лог-файл: " + e.getMessage());
        }
    }
}
