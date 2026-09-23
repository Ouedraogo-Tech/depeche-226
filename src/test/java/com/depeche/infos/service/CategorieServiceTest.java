package com.depeche.infos.service;

import com.depeche.infos.dto.CategorieRequestDTO;
import com.depeche.infos.entity.Categorie;
import com.depeche.infos.repository.CategorieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategorieServiceTest {

    @Mock
    private CategorieRepository categorieRepository;

    @InjectMocks
    private CategorieService categorieService;

    @Test
    void creerCategorie_QuandNomExisteDeja_DoitLeverException() {
        // ARRANGE
        CategorieRequestDTO request = new CategorieRequestDTO();
        request.setNom("Politique");

        // On dit au faux repository de répondre "true" (la catégorie existe déjà)
        when(categorieRepository.existsByNom("Politique")).thenReturn(true);

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categorieService.creerCategorie(request);
        });

        assertEquals("Cette catégorie existe déjà !", exception.getMessage());

        // On vérifie qu'on n'a jamais appelé save() car ça a planté
        verify(categorieRepository, never()).save(any(Categorie.class));
    }
}