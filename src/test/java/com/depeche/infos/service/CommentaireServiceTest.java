package com.depeche.infos.service;

import com.depeche.infos.entity.Commentaire;
import com.depeche.infos.enums.StatutCommentaire;
import com.depeche.infos.repository.ArticleRepository;
import com.depeche.infos.repository.CommentaireRepository;
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
class CommentaireServiceTest {

    @Mock
    private CommentaireRepository commentaireRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private UtilisateurRepository utilisateurRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CommentaireService commentaireService;

    @Test
    void masquerCommentaire_QuandExiste_DoitChangerStatutEnMasque() {
        // ARRANGE
        Commentaire commentaire = new Commentaire();
        commentaire.setId(1L);
        commentaire.setStatut(StatutCommentaire.PUBLIE);

        when(commentaireRepository.findById(1L)).thenReturn(Optional.of(commentaire));
        when(commentaireRepository.save(any(Commentaire.class))).thenReturn(commentaire);

        // ACT
        Commentaire resultat = commentaireService.masquerCommentaire(1L);

        // ASSERT
        assertEquals(StatutCommentaire.MASQUE, resultat.getStatut());
        verify(commentaireRepository, times(1)).save(any(Commentaire.class));
    }
}