package com.example.languageDefinition.service.impl;

import com.example.languageDefinition.model.Document;
import com.example.languageDefinition.repository.DocumentRepository;
import com.example.languageDefinition.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    @Override
    public Document save(Document document) {
        return documentRepository.save(document);
    }

    @Override
    public Document findDocument(Long id) {
        return documentRepository.findDocumentById(id).orElseGet(Document::new);
    }

}
