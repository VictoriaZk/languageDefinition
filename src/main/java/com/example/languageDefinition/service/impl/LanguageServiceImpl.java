package com.example.languageDefinition.service.impl;

import com.example.languageDefinition.model.Document;
import com.example.languageDefinition.model.Language;
import com.example.languageDefinition.model.SearchResult;
import com.example.languageDefinition.model.Word;
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
    public void uploadTermsLanguage(MultipartFile file, Language language) {
        String termsFile = getTextFromPdfFile(file);
        Set<String> terms = DocumentUtilsImpl.getTermOccurrences(termsFile);
        terms.forEach(word ->
                wordRepository.findByWordAndLanguage(word, language)
                .orElse(wordRepository.save(
                        Word.builder()
                        .language(language)
                        .word(word)
                        .build())));
    }

    @Override
    public SearchResult defineLanguage(MultipartFile file) throws Exception {
        String fullText = getTextFromPdfFile(file);

        HashMap<Language, Integer> termsOccurrences = new HashMap<>();
        termsOccurrences.put(Language.RUSSIAN, 0);
        termsOccurrences.put(Language.ENGLISH, 0);

        Set<String> termRequest = DocumentUtilsImpl.getTermOccurrences(fullText);

        termRequest.forEach(word ->
                wordRepository.findAllByWord(word).forEach(term ->
                        termsOccurrences.computeIfPresent(term.getLanguage(), (key, value) -> value + 1)));
        Language documentLanguage = termsOccurrences.entrySet()
                .stream().max(Map.Entry.comparingByValue()).filter(entry -> entry.getValue() != 0)
                .orElse(new AbstractMap.SimpleEntry<Language, Integer>(Language.UNDEFINED, 0)).getKey();

        saveFileStorage(file);
        Document document = documentService.save(
                Document.builder()
                        .title(file.getOriginalFilename())
                        .text(fullText)
                        .language(documentLanguage)
                        .build());

        return SearchResult.builder()
                .document(document)
                .snippet(fullText.substring(0,
                        Math.min(maxLengthSnippet, fullText.length())))
                .build();

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
