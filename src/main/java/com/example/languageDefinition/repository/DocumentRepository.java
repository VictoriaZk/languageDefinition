package com.example.languageDefinition.repository;

import com.example.languageDefinition.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findDocumentById(Long id);
}
