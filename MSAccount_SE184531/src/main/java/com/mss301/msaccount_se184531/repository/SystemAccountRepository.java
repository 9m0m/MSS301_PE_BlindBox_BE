package com.mss301.msaccount_se184531.repository;

import com.mss301.msaccount_se184531.entity.SystemAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemAccountRepository extends JpaRepository<SystemAccount, Integer> {
    Optional<SystemAccount> findByEmail(String email);
}
