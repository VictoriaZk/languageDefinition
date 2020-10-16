package com.example.languageDefinition.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DocumentUtilsImpl{
    public static String getCleanText(String text){
        return text
                .replaceAll("[–,.;:!?]", "")
                .replaceAll("\n", " ")
                .replaceAll("  ( )*", " ").toLowerCase();
    }
    public  static Set<String> getTermOccurrences(String text) {
        String[] words = getCleanText(text).split(" ");

        return Arrays.stream(words).collect(Collectors.toSet());
    }


    public static Map<String, Double> getTermOccurrencesFrequencyWordMethod(String text) {
        String[] words = getCleanText(text).split(" ");
        Map<String, Integer> initialForms = new HashMap<>();

        for (String word : words) {

            if (initialForms.containsKey(word)) {
                initialForms.put(word, initialForms.get(word) + 1);
            } else {
                initialForms.put(word, 1);
            }
        }

        return initialForms.entrySet().stream()
                .filter(entrySet -> entrySet.getValue() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> Double.valueOf(entry.getValue()) / words.length));
    }

    public static Map<String, Double> getTermOccurrencesShortWordMethod(String text) {
        String[] words = getCleanText(text).split(" ");
        Map<String, Integer> initialForms = new HashMap<>();

        for (String word : words) {

            if (initialForms.containsKey(word)) {
                initialForms.put(word, initialForms.get(word) + 1);
            } else {
                initialForms.put(word, 1);
            }
        }

        return initialForms.entrySet().stream()
                .filter(entrySet -> entrySet.getValue() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> Double.valueOf(entry.getValue()) / words.length));
    }
}
