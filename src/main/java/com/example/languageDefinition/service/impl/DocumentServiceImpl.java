package com.example.languageDefinition.service.impl;

import com.example.languageDefinition.model.Document;
import com.example.languageDefinition.repository.DocumentRepository;
import com.example.languageDefinition.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    @Override
    public Document save(Document document) {
        return documentRepository.save(document);
    }

    @Override
    @Transactional(readOnly = true)
    public Document findDocument(Long id) {
        return documentRepository.findDocumentById(id).orElseGet(Document::new);
    }

}
