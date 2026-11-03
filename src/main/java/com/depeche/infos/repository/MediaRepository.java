package com.depeche.infos.repository;

import com.depeche.infos.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MediaRepository extends JpaRepository<Media, Long> {

    Optional<Media> findByNom(String nom);

    boolean existsByNom(String nom);
}
