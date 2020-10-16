package com.example.languageDefinition.service.impl;

import com.example.languageDefinition.model.*;
import com.example.languageDefinition.repository.WordRepository;
import com.example.languageDefinition.service.DocumentService;
import com.example.languageDefinition.service.LanguageService;
import com.example.languageDefinition.utils.DocumentUtilsImpl;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor

@Service
public class LanguageServiceImpl implements LanguageService {
    private static final int maxLengthSnippet = 300;
    private final String PATH = System.getProperty("user.dir") + "/files";

    private final WordRepository wordRepository;
    private final DocumentService documentService;

    @Override
    public void uploadTermsLanguage(MultipartFile file, Language language, Method method) {

        String termsFile = getTextFromPdfFile(file);
        Map<String, Double> termsProbability = null;
        if(method == Method.FREQUENCY_WORDS) {
            DocumentUtilsImpl.getTermOccurrencesFrequencyWordMethod(termsFile);
        }else if(method == Method.SHORT_WORDS){
            DocumentUtilsImpl.getTermOccurrencesShortWordMethod(termsFile);
        }

        termsProbability.forEach((word, probability) ->
                wordRepository.findByWordAndLanguage(word, language)
                .orElse(wordRepository.save(
                        Word.builder()
                        .language(language)
                        .word(word)
                        .probability(probability)
                        .build())));
    }

    @Override
    public SearchResult defineLanguage(MultipartFile file) {
        String fullText = getTextFromPdfFile(file);
        saveFileStorage(file);

        Document document = documentService.save(
                Document.builder()
                        .title(file.getOriginalFilename())
                        .text(fullText)
                        .language(defineByFrequencyWordsMethod(fullText))
                        .build());

        return SearchResult.builder()
                .document(document)
                .snippet(fullText.substring(0,
                        Math.min(maxLengthSnippet, fullText.length())))
                .build();

    }

    @Override
    public Language defineByShortWordsMethod(String text) {
        HashMap<Language, Double> termsOccurrences = new HashMap<>();
        termsOccurrences.put(Language.RUSSIAN, 0D);
        termsOccurrences.put(Language.ENGLISH, 0D);

        Set<String> termRequest = DocumentUtilsImpl.getTermOccurrences(text);

        termRequest.forEach(word ->
                wordRepository.findAllByWord(word)
                        .forEach(term -> termsOccurrences.computeIfPresent(term.getLanguage(),
                                (key, value) -> value + term.getProbability())));

        return termsOccurrences.entrySet()
                .stream().max(Map.Entry.comparingByValue()).filter(entry -> entry.getValue() != 0)
                .orElse(new AbstractMap.SimpleEntry<>(Language.UNDEFINED, 0D)).getKey();
    }

    @Override
    public Language defineByFrequencyWordsMethod(String text) {
        HashMap<Language, Double> termsOccurrences = new HashMap<>();
        termsOccurrences.put(Language.RUSSIAN, 0D);
        termsOccurrences.put(Language.ENGLISH, 0D);

        Set<String> termRequest = DocumentUtilsImpl.getTermOccurrences(text);

        termRequest.forEach(word ->
                wordRepository.findAllByWordAndMethod(word, Method.FREQUENCY_WORDS).forEach(term ->
                        termsOccurrences.computeIfPresent(term.getLanguage(), (key, value) -> value + term.getProbability())));
        return termsOccurrences.entrySet()
                .stream().max(Map.Entry.comparingByValue()).filter(entry -> entry.getValue() != 0)
                .orElse(new AbstractMap.SimpleEntry<>(Language.UNDEFINED, 0D)).getKey();
    }

    @Override
    public Language defineByOwnMethod(String text) {
        return null;
    }

    private String getTextFromPdfFile(MultipartFile file){
        try {
            PdfReader pdfReader = new PdfReader(file.getBytes());
            PdfTextExtractor parser = new PdfTextExtractor(pdfReader);
            StringBuilder fullText = new StringBuilder();

            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
                fullText.append(parser.getTextFromPage(i));
            }

            return fullText.toString();
        }catch(Exception exception){
            return "";
        }
    }

    private void saveFileStorage(MultipartFile file){
        File fileToSave = new File(PATH + '/' + file.getOriginalFilename());

        try {
            if (!fileToSave.exists()) {
                fileToSave.createNewFile();
            }

            file.transferTo(fileToSave);
        } catch (IOException e) {
            fileToSave.delete();
        }
    }
}
