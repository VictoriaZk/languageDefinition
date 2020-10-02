package com.example.languageDefinition.utils;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class DocumentUtilsImpl{
    public  static Set<String> getTermOccurrences(String text) {
        String cleanText = text
                .replaceAll("[–,.;:!?]", "")
                .replaceAll("\n", " ")
                .replaceAll("  ( )*", " ").toLowerCase();
        String[] words = cleanText.split(" ");

        return Arrays.stream(words).collect(Collectors.toSet());
    }
}
