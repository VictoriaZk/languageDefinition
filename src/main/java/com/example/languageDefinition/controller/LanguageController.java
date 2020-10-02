package com.example.languageDefinition.controller;

import com.example.languageDefinition.model.Language;
import com.example.languageDefinition.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class LanguageController {

    private final LanguageService languageService;

    @PostMapping("define")
    public String getLanguageOfText(@RequestParam(value = "fileToDefine") MultipartFile file, Model model){
        model.addAttribute("language", languageService.defineLanguage(file));
        return "languagePage";
    }

    @PostMapping("upload")
    public String uploadTermsOfLanguage(@RequestParam(value = "fileToUpload") MultipartFile file,
                                        @RequestParam(value = "selectedLanguage")String language){
        languageService.uploadTermsLanguage(file, Language.valueOf(language));

        return "redirect:/";
    }

    @GetMapping
    public String mainPage(){
        return "start";
    }
}
