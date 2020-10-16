package com.example.languageDefinition.service;

import com.example.languageDefinition.model.Language;
import com.example.languageDefinition.model.Method;
import com.example.languageDefinition.model.SearchResult;
import org.springframework.web.multipart.MultipartFile;

public interface LanguageService {
    SearchResult defineLanguage(MultipartFile file, Method method) throws Exception;

    Language defineByShortWordsMethod(String text);

    Language defineByFrequencyWordsMethod(String text);

    Language defineByOwnMethod(String text);

    void uploadTermsLanguage(MultipartFile file, Language language, Method method);

}
