package com.example.languageDefinition.controller;

import com.example.languageDefinition.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/document")
public class DocumentController {
    private final DocumentService documentService;

    @GetMapping("{id}")
    public String getDocument(@PathVariable("id") Long DocumentId, Model model){
        model.addAttribute("document", documentService.findDocument(DocumentId));

        return "document";
    }
}
