package com.depeche.infos.service;

import com.depeche.infos.dto.CategorieRequestDTO;
import com.depeche.infos.entity.Categorie;
import com.depeche.infos.repository.ArticleRepository;
import com.depeche.infos.repository.CategorieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategorieServiceTest {

    @Mock
    private CategorieRepository categorieRepository;
    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private CategorieService categorieService;

    @Test
    void creerCategorie_QuandNomExisteDeja_DoitLeverException() {
        // ARRANGE : "Politique" existe déjà ; on essaie de créer " politique " (minuscules + espaces)
        Categorie existante = new Categorie();
        existante.setId(1L);
        existante.setNom("Politique");
        when(categorieRepository.findAll()).thenReturn(List.of(existante));

        CategorieRequestDTO request = new CategorieRequestDTO();
        request.setNom(" politique ");

        // ACT & ASSERT
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            categorieService.creerCategorie(request);
        });

        assertEquals("La catégorie « politique » existe déjà.", exception.getMessage());

        // On vérifie qu'on n'a jamais appelé save() car ça a planté
        verify(categorieRepository, never()).save(any(Categorie.class));
    }

    @Test
    void creerCategorie_QuandSeulsLesAccentsChangent_DoitLeverException() {
        // "Économie" existe déjà ; "Economie" (sans accent) est un doublon
        Categorie existante = new Categorie();
        existante.setId(2L);
        existante.setNom("Économie");
        when(categorieRepository.findAll()).thenReturn(List.of(existante));

        CategorieRequestDTO request = new CategorieRequestDTO();
        request.setNom("Economie");

        assertThrows(IllegalStateException.class, () -> categorieService.creerCategorie(request));
        verify(categorieRepository, never()).save(any(Categorie.class));
    }
}