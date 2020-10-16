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
    private static final double minFrequency = 0.01;
    private final String PATH = System.getProperty("user.dir") + "/files";

    private final WordRepository wordRepository;
    private final DocumentService documentService;

    @Override
    public void uploadTermsLanguage(MultipartFile file, Language language, Method method) {

        String termsFile = getTextFromPdfFile(file);
        Map<String, Double> termsProbability = null;
        if(method == Method.FREQUENCY_WORDS) {
            termsProbability = DocumentUtilsImpl.getTermsFrequencyWordMethod(termsFile);
        }else if(method == Method.SHORT_WORDS){
            termsProbability = DocumentUtilsImpl.getTermsShortWordMethod(termsFile);
        }

        termsProbability.forEach((word, probability) ->
                wordRepository.findByWordAndLanguage(word, language)
                .orElse(wordRepository.save(
                        Word.builder()
                        .language(language)
                        .method(method)
                        .word(word)
                        .probability(probability)
                        .build())));
    }

    @Override
    public SearchResult defineLanguage(MultipartFile file, Method method) {
        String fullText = getTextFromPdfFile(file);
        saveFileStorage(file);

        Language language = null;
        if(method == Method.FREQUENCY_WORDS){
            language = defineByFrequencyWordsMethod(fullText);
        }else if(method == Method.SHORT_WORDS){
            language = defineByShortWordsMethod(fullText);
        }else if(method == Method.OWN_METHOD){
            language = defineByOwnMethod(fullText);
        }

        Document document = documentService.save(
                Document.builder()
                        .title(file.getOriginalFilename())
                        .text(fullText)
                        .language(language)
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
        termsOccurrences.put(Language.RUSSIAN, Math.pow(100, 150D));
        termsOccurrences.put(Language.ENGLISH, Math.pow(100, 150D));

        Set<String> termRequest = DocumentUtilsImpl.getAllTerms(text);

        termRequest.forEach(word ->
        {
            termsOccurrences.computeIfPresent(Language.ENGLISH,
                    (key, value) -> value * getTermProbability(word, Method.SHORT_WORDS, Language.ENGLISH));

            termsOccurrences.computeIfPresent(Language.RUSSIAN,
                    (key, value) -> value * getTermProbability(word, Method.SHORT_WORDS, Language.RUSSIAN));
        });

        return termsOccurrences.entrySet()
                .stream().max(Map.Entry.comparingByValue()).filter(entry -> entry.getValue() != 0)
                .orElse(new AbstractMap.SimpleEntry<>(Language.UNDEFINED, 0D)).getKey();
    }

    private Double getTermProbability(String word, Method method, Language language){
        Optional<Word> englishTerm = wordRepository
                .findByWordAndMethodAndLanguage(word, method, language);
        if(englishTerm.isPresent()){
            return englishTerm.get().getProbability();
        }else {
            return minFrequency;
        }
    }

    @Override
    public Language defineByFrequencyWordsMethod(String text) {
        HashMap<Language, Double> termsOccurrences = new HashMap<>();
        termsOccurrences.put(Language.RUSSIAN, 0D);
        termsOccurrences.put(Language.ENGLISH, 0D);

        Set<String> termRequest = DocumentUtilsImpl.getAllTerms(text);

        termRequest.forEach(word ->
                wordRepository.findAllByWordAndMethod(word, Method.FREQUENCY_WORDS).forEach(term ->
                        termsOccurrences.computeIfPresent(term.getLanguage(),
                                (key, value) -> value + term.getProbability())));

        return termsOccurrences.entrySet()
                .stream().max(Map.Entry.comparingByValue()).filter(entry -> entry.getValue() != 0)
                .orElse(new AbstractMap.SimpleEntry<>(Language.UNDEFINED, 0D)).getKey();
    }

    @Override
    public Language defineByOwnMethod(String text) {
        if(text.replaceAll("[A-Z]|[a-z]", "").length() < text.length() / 2){
            return Language.ENGLISH;
        }else if(text.replaceAll("[А-Я]|[а-я]", "").length() < text.length() / 2){
            return Language.RUSSIAN;
        }
        return Language.UNDEFINED;
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
