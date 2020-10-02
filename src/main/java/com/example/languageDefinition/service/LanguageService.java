package com.example.languageDefinition.service;

import com.example.languageDefinition.model.Language;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface LanguageService {
    String defineLanguage(MultipartFile file);

    void uploadTermsLanguage(MultipartFile file, Language language);
}
