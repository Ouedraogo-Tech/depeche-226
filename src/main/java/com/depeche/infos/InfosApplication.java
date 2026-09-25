package com.depeche.infos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// @EnableScheduling : active les tâches automatiques (@Scheduled),
// ici la publication automatique des articles planifiés (ArticleService.publierArticlesPlanifies)
@SpringBootApplication
@EnableScheduling
public class InfosApplication {
	// Test du bouton Commit and Push
	public static void main(String[] args) {
		SpringApplication.run(InfosApplication.class, args);
	}

}
