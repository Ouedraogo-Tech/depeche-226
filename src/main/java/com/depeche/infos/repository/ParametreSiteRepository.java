package com.depeche.infos.repository;

import com.depeche.infos.entity.ParametreSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParametreSiteRepository extends JpaRepository<ParametreSite, Long> {

    // Il n'y a qu'une seule ligne de paramètres : on prend la première
    Optional<ParametreSite> findFirstByOrderByIdAsc();
}
