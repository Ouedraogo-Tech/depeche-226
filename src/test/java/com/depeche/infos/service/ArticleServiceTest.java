package com.depeche.infos.service;

import com.depeche.infos.entity.Article;
import com.depeche.infos.enums.StatutArticle;
import com.depeche.infos.repository.ArticleRepository;
import com.depeche.infos.repository.CategorieRepository;
import com.depeche.infos.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private UtilisateurRepository utilisateurRepository;
    @Mock
    private CategorieRepository categorieRepository;

    @InjectMocks
    private ArticleService articleService;

    @Test
    void publierArticle_QuandArticleExiste_DoitChangerStatutEtMettreDate() {
        // ARRANGE
        Article article = new Article();
        article.setId(1L);
        article.setStatut(StatutArticle.BROUILLON);

        // On simule le fait que l'article existe en base
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        // On simule la sauvegarde en renvoyant l'article modifié
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        // ACT
        Article resultat = articleService.publierArticle(1L);

        // ASSERT
        // On vérifie que le statut est bien PUBLIE
        assertEquals(StatutArticle.PUBLIE, resultat.getStatut());
        // On vérifie que la date de publication a été remplie
        assertNotNull(resultat.getDatePublication());
        // On vérifie que la méthode save a bien été appelée 1 fois
        verify(articleRepository, times(1)).save(any(Article.class));
    }
}