package com.example.languageDefinition.service.impl;

import com.example.languageDefinition.model.Language;
import com.example.languageDefinition.model.Word;
import com.example.languageDefinition.repository.WordRepository;
import com.example.languageDefinition.service.LanguageService;
import com.example.languageDefinition.utils.DocumentUtilsImpl;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RequiredArgsConstructor

@Service
public class LanguageServiceImpl implements LanguageService {

    private final WordRepository wordRepository;

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
    public String defineLanguage(MultipartFile file){

        String fullText = getTextFromPdfFile(file);

        HashMap<Language, Integer> termsOccurrences = new HashMap<>();
        termsOccurrences.put(Language.RUSSIAN, 0);
        termsOccurrences.put(Language.ENGLISH, 0);

        Set<String> termRequest = DocumentUtilsImpl.getTermOccurrences(fullText);

        termRequest.forEach(word ->
                wordRepository.findAllByWord(word).forEach(term ->
                        termsOccurrences.computeIfPresent(term.getLanguage(), (key, value) -> value + 1)));
        try {
            return termsOccurrences.entrySet()
                    .stream().max(Map.Entry.comparingByValue())
                    .orElseThrow(Exception::new).getKey().name();
        } catch (Exception exception){
            return Language.UNDEFINED.name();
        }
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
}
