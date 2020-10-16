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

    public static String[] getSplitWords(String text){
        return getCleanText(text).split(" ");
    }
    public  static Set<String> getAllTerms(String text) {
        String[] words = getCleanText(text).split(" ");

        return Arrays.stream(words).collect(Collectors.toSet());
    }

    public static Map<String, Integer> getTermsOccurrences(String[] words){

        Map<String, Integer> initialForms = new HashMap<>();

        for (String word : words) {

            if (initialForms.containsKey(word)) {
                initialForms.put(word, initialForms.get(word) + 1);
            } else {
                initialForms.put(word, 1);
            }
        }

        return initialForms;
    }

    public static Map<String, Double> getTermsFrequencyWordMethod(String text) {
        String[] allWords = getSplitWords(text);
        Map<String, Integer> termsOccurrences = getTermsOccurrences(allWords);
        return termsOccurrences.entrySet().stream()
                .filter(entrySet -> entrySet.getValue() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> Double.valueOf(entry.getValue()) / allWords.length));
    }

    public static Map<String, Double> getTermsShortWordMethod(String text) {
        String[] allWords = getSplitWords(text);
        Map<String, Integer> shortWordsTerms =
                getTermsOccurrences(allWords).entrySet().stream().filter(entrySet ->
                        entrySet.getKey().length() < 5 && entrySet.getValue() > 3)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        int sumFrequencies = shortWordsTerms.values().stream().mapToInt(value -> value).sum();

        return shortWordsTerms.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey,
                                entry -> Double.valueOf(entry.getValue()) / sumFrequencies));
    }
}
