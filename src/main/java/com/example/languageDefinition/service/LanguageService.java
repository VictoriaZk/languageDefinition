package com.example.languageDefinition.service;

import com.example.languageDefinition.model.Document;
import com.example.languageDefinition.model.Language;
import com.example.languageDefinition.model.SearchResult;
import org.springframework.web.multipart.MultipartFile;

public interface LanguageService {
    SearchResult defineLanguage(MultipartFile file) throws Exception;

    void uploadTermsLanguage(MultipartFile file, Language language);
}
