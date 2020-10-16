package com.example.languageDefinition.controller;

import com.example.languageDefinition.model.Language;
import com.example.languageDefinition.model.Method;
import com.example.languageDefinition.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor
@RequestMapping()
public class LanguageController {

    private final LanguageService languageService;

    @PostMapping("define")
    public String getLanguageOfText(@RequestParam(value = "fileToDefine") MultipartFile file, Model model) throws Exception {
        model.addAttribute("searchResult", languageService.defineLanguage(file));

        return "start";
    }

    @PostMapping("upload")
    public String uploadTermsOfLanguage(@RequestParam(value = "fileToUpload") MultipartFile file,
                                        @RequestParam(value = "selectedLanguage")String language,
                                        @RequestParam(value = "selectMethod") String method){
        languageService.uploadTermsLanguage(file, Language.valueOf(language), Method.valueOf(method));

        return "redirect:/";
    }

    @GetMapping
    public String mainPage(){
        return "start";
    }
}
