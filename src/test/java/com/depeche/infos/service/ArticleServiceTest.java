package com.depeche.infos.service;

import com.depeche.infos.entity.Article;
import com.depeche.infos.entity.Categorie;
import com.depeche.infos.entity.Journaliste;
import com.depeche.infos.entity.ResponsableEditorial;
import com.depeche.infos.entity.Role;
import com.depeche.infos.entity.Utilisateur;
import com.depeche.infos.enums.StatutArticle;
import com.depeche.infos.enums.TypeNotification;
import com.depeche.infos.exception.ActionInterditeException;
import com.depeche.infos.repository.ArticleRepository;
import com.depeche.infos.repository.CategorieRepository;
import com.depeche.infos.repository.CommentaireRepository;
import com.depeche.infos.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private UtilisateurRepository utilisateurRepository;
    @Mock
    private CategorieRepository categorieRepository;
    @Mock
    private CommentaireRepository commentaireRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ArticleService articleService;

    // ---------- Données de test ----------

    private Utilisateur utilisateur(Long id, String email, Utilisateur type, String nomRole) {
        type.setId(id);
        type.setEmail(email);
        type.setNom("Nom" + id);
        type.setPrenom("Prenom" + id);
        type.setRoles(Set.of(new Role(nomRole)));
        return type;
    }

    private Article article(Utilisateur auteur, StatutArticle statut) {
        Article article = new Article();
        article.setId(1L);
        article.setTitre("Titre");
        article.setAuteur(auteur);
        article.setStatut(statut);
        return article;
    }

    // ---------- Tests ----------

    @Test
    void publierArticle_QuandAuteurPublie_DoitChangerStatutEtMettreDate() {
        // ARRANGE
        Utilisateur auteur = utilisateur(10L, "auteur@test.bf", new Journaliste(), "JOURNALISTE");
        Article article = article(auteur, StatutArticle.BROUILLON);

        // On simule le fait que l'article et l'utilisateur existent en base
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        when(utilisateurRepository.findByEmail("auteur@test.bf")).thenReturn(Optional.of(auteur));
        // On simule la sauvegarde en renvoyant l'article modifié
        when(articleRepository.save(any(Article.class))).thenReturn(article);

        // ACT
        Article resultat = articleService.publierArticle(1L, "auteur@test.bf");

        // ASSERT
        assertEquals(StatutArticle.PUBLIE, resultat.getStatut());
        assertNotNull(resultat.getDatePublication());
        verify(articleRepository, times(1)).save(any(Article.class));
        // L'auteur publie lui-même : pas de notification
        verifyNoInteractions(notificationService);
    }

    @Test
    void modifierArticle_QuandJournalisteModifieArticleDunCollegue_DoitEtreInterdit() {
        Utilisateur auteur = utilisateur(10L, "auteur@test.bf", new Journaliste(), "JOURNALISTE");
        Utilisateur collegue = utilisateur(11L, "collegue@test.bf", new Journaliste(), "JOURNALISTE");
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article(auteur, StatutArticle.BROUILLON)));
        when(utilisateurRepository.findByEmail("collegue@test.bf")).thenReturn(Optional.of(collegue));

        assertThrows(ActionInterditeException.class,
                () -> articleService.modifierArticle(1L, new Article(), 1L, "collegue@test.bf"));
        verify(articleRepository, never()).save(any());
    }

    @Test
    void modifierArticle_DoitConserverLaCategorie() {
        // Correction testée : avant, la catégorie était remise à null lors d'une modification
        Utilisateur auteur = utilisateur(10L, "auteur@test.bf", new Journaliste(), "JOURNALISTE");
        Categorie categorie = new Categorie();
        categorie.setId(3L);
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article(auteur, StatutArticle.BROUILLON)));
        when(utilisateurRepository.findByEmail("auteur@test.bf")).thenReturn(Optional.of(auteur));
        when(categorieRepository.findById(3L)).thenReturn(Optional.of(categorie));
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Article modification = new Article();
        modification.setTitre("Nouveau titre");
        Article resultat = articleService.modifierArticle(1L, modification, 3L, "auteur@test.bf");

        assertEquals(categorie, resultat.getCategorie());
        assertEquals("Nouveau titre", resultat.getTitre());
    }

    @Test
    void soumettreArticle_DoitPrevenirLesResponsablesEditoriaux() {
        Utilisateur auteur = utilisateur(10L, "auteur@test.bf", new Journaliste(), "JOURNALISTE");
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article(auteur, StatutArticle.BROUILLON)));
        when(utilisateurRepository.findByEmail("auteur@test.bf")).thenReturn(Optional.of(auteur));
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Article resultat = articleService.soumettreArticle(1L, "auteur@test.bf");

        assertEquals(StatutArticle.SOUMIS, resultat.getStatut());
        verify(notificationService).notifierRole(eq("RESPONSABLE_EDITORIAL"), eq(TypeNotification.ARTICLE_SOUMIS), anyString(), eq(1L));
    }

    @Test
    void demanderModification_SansCommentaire_DoitLeverException() {
        assertThrows(IllegalArgumentException.class, () -> articleService.demanderModification(1L, "   "));
        verify(articleRepository, never()).save(any());
    }

    @Test
    void demanderModification_QuandSoumis_DoitPasserEnAReviserEtNotifierLAuteur() {
        Utilisateur auteur = utilisateur(10L, "auteur@test.bf", new Journaliste(), "JOURNALISTE");
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article(auteur, StatutArticle.SOUMIS)));
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Article resultat = articleService.demanderModification(1L, "Vérifiez vos sources");

        assertEquals(StatutArticle.A_REVISER, resultat.getStatut());
        assertEquals("Vérifiez vos sources", resultat.getCommentaireEditorial());
        verify(notificationService).notifier(eq(auteur), eq(TypeNotification.ARTICLE_A_REVISER), anyString(), eq(1L));
    }

    @Test
    void validerArticle_QuandPasSoumis_DoitLeverException() {
        Utilisateur responsable = utilisateur(20L, "re@test.bf", new ResponsableEditorial(), "RESPONSABLE_EDITORIAL");
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article(responsable, StatutArticle.BROUILLON)));

        assertThrows(IllegalStateException.class, () -> articleService.validerArticle(1L, null));
    }

    @Test
    void publierArticle_QuandSoumis_DoitEtreRefuse() {
        // Un article en relecture ne peut pas être publié directement (la validation serait contournée)
        Utilisateur auteur = utilisateur(10L, "auteur@test.bf", new Journaliste(), "JOURNALISTE");
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article(auteur, StatutArticle.SOUMIS)));
        when(utilisateurRepository.findByEmail("auteur@test.bf")).thenReturn(Optional.of(auteur));

        assertThrows(IllegalStateException.class, () -> articleService.publierArticle(1L, "auteur@test.bf"));
        assertThrows(IllegalStateException.class,
                () -> articleService.planifierArticle(1L, LocalDateTime.now().plusHours(1), "auteur@test.bf"));
        verify(articleRepository, never()).save(any());
    }

    @Test
    void soumettreArticle_QuandRefuse_DoitEtrePossible() {
        // Un article refusé peut être corrigé puis resoumis
        Utilisateur auteur = utilisateur(10L, "auteur@test.bf", new Journaliste(), "JOURNALISTE");
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article(auteur, StatutArticle.REFUSE)));
        when(utilisateurRepository.findByEmail("auteur@test.bf")).thenReturn(Optional.of(auteur));
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LocalDateTime souhaitee = LocalDateTime.now().plusHours(2);
        Article resultat = articleService.soumettreArticle(1L, souhaitee, "auteur@test.bf");

        assertEquals(StatutArticle.SOUMIS, resultat.getStatut());
        assertEquals(souhaitee, resultat.getDatePublicationSouhaitee());
    }

    @Test
    void validerArticle_AvecDateSouhaiteeFuture_DoitPlanifierACetteDate() {
        Utilisateur auteur = utilisateur(10L, "auteur@test.bf", new Journaliste(), "JOURNALISTE");
        Article soumis = article(auteur, StatutArticle.SOUMIS);
        LocalDateTime souhaitee = LocalDateTime.now().plusHours(1);
        soumis.setDatePublicationSouhaitee(souhaitee);
        when(articleRepository.findById(1L)).thenReturn(Optional.of(soumis));
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Article resultat = articleService.validerArticle(1L, null);

        assertEquals(StatutArticle.PLANIFIE, resultat.getStatut());
        assertEquals(souhaitee, resultat.getDatePublication());
        assertNull(resultat.getDatePublicationSouhaitee());
    }

    @Test
    void validerArticle_AvecDateSouhaiteePassee_DoitPublierToutDeSuite() {
        Utilisateur auteur = utilisateur(10L, "auteur@test.bf", new Journaliste(), "JOURNALISTE");
        Article soumis = article(auteur, StatutArticle.SOUMIS);
        soumis.setDatePublicationSouhaitee(LocalDateTime.now().minusMinutes(5));
        when(articleRepository.findById(1L)).thenReturn(Optional.of(soumis));
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Article resultat = articleService.validerArticle(1L, null);

        assertEquals(StatutArticle.PUBLIE, resultat.getStatut());
    }

    @Test
    void refuserArticle_DoitAnnulerLaDateSouhaitee() {
        Utilisateur auteur = utilisateur(10L, "auteur@test.bf", new Journaliste(), "JOURNALISTE");
        Article soumis = article(auteur, StatutArticle.SOUMIS);
        soumis.setDatePublicationSouhaitee(LocalDateTime.now().plusHours(1));
        when(articleRepository.findById(1L)).thenReturn(Optional.of(soumis));
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Article resultat = articleService.refuserArticle(1L, "Sources insuffisantes");

        assertEquals(StatutArticle.REFUSE, resultat.getStatut());
        assertNull(resultat.getDatePublicationSouhaitee());
    }
}
