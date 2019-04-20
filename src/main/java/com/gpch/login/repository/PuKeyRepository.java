package com.gpch.login.repository;

import com.gpch.login.model.PuKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("puKeyRepository")
public interface PuKeyRepository extends JpaRepository<PuKey, Integer> {
}


