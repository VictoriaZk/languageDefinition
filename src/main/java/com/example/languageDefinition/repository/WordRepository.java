package com.example.languageDefinition.repository;

import com.example.languageDefinition.model.Language;
import com.example.languageDefinition.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findAllByWord(String word);

    Optional<Word> findByWordAndLanguage(String word, Language language);
}
