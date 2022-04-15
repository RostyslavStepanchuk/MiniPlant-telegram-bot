package com.rstepanchuk.miniplant.telegrambot.repository.dao;

import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.AccountingRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountingRecordsJpa extends JpaRepository<AccountingRecordEntity, Long> {

  Optional<AccountingRecordEntity> findByUserIdEquals(Long userId);
}
