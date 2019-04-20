package com.gpch.login.repository;

import com.gpch.login.model.PrKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("prKeyRepository")
public interface PrKeyRepository extends JpaRepository<PrKey, Integer> {
}
