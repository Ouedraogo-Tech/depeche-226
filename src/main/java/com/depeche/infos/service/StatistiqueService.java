package com.depeche.infos.service;

import com.depeche.infos.dto.StatistiquesDTO;
import com.depeche.infos.enums.StatutArticle;
import com.depeche.infos.enums.StatutCommentaire;
import com.depeche.infos.repository.*;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

// Calcule les chiffres affichés dans les cartes des dashboards.
@Service
public class StatistiqueService {

    private final ArticleRepository articleRepository;
    private final CategorieRepository categorieRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final CommentaireRepository commentaireRepository;
    private final AbonneNewsletterRepository abonneRepository;

    public StatistiqueService(ArticleRepository articleRepository, CategorieRepository categorieRepository,
                              UtilisateurRepository utilisateurRepository, RoleRepository roleRepository,
                              CommentaireRepository commentaireRepository, AbonneNewsletterRepository abonneRepository) {
        this.articleRepository = articleRepository;
        this.categorieRepository = categorieRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.commentaireRepository = commentaireRepository;
        this.abonneRepository = abonneRepository;
    }

    public StatistiquesDTO calculerStatistiques() {
        StatistiquesDTO stats = new StatistiquesDTO();

        Map<String, Long> parStatut = new LinkedHashMap<>();
        for (StatutArticle statut : StatutArticle.values()) {
            parStatut.put(statut.name(), articleRepository.countByStatut(statut));
        }
        stats.setArticlesParStatut(parStatut);
        stats.setTotalArticles(articleRepository.count());
        stats.setTotalCategories(categorieRepository.count());

        stats.setTotalUtilisateurs(utilisateurRepository.count());
        stats.setUtilisateursActifs(utilisateurRepository.countByActif(true));
        stats.setUtilisateursDesactives(utilisateurRepository.countByActif(false));
        stats.setTotalRoles(roleRepository.count());

        stats.setTotalCommentaires(commentaireRepository.count());
        stats.setCommentairesMasques(commentaireRepository.countByStatut(StatutCommentaire.MASQUE));

        stats.setAbonnesActifs(abonneRepository.countByActif(true));
        return stats;
    }
}
