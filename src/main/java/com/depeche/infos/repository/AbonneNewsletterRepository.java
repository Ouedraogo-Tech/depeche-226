package com.depeche.infos.repository;

import com.depeche.infos.entity.AbonneNewsletter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AbonneNewsletterRepository extends JpaRepository<AbonneNewsletter, Long> {

    Optional<AbonneNewsletter> findByEmail(String email);

    boolean existsByEmail(String email);
}