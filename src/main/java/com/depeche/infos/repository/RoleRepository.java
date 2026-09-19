package com.depeche.infos.repository;


import com.depeche.infos.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Pour chercher un rôle par son nom (ex: "JOURNALISTE")
    Optional<Role> findByName(String name);
}

