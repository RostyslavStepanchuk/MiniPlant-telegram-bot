package com.rstepanchuk.miniplant.telegrambot.repository.dao;

import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.SheetsTableCredentialsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SheetsTableCredentialsDao
    extends JpaRepository<SheetsTableCredentialsEntity, Long> {

  Optional<SheetsTableCredentialsEntity> findByUserIdEquals(Long userId);
}
