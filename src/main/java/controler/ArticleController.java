package com.depeche.infos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticleController {

    @GetMapping("/api/articles")
    public String getArticles() {
        return "Liste des articles de Dépêche.info";
    }
}