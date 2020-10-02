package com.example.languageDefinition.service;

import com.example.languageDefinition.model.Document;

public interface DocumentService {
    Document findDocument(Long id);
    Document save(Document document);
}
