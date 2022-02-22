package com.rstepanchuk.miniplant.telegrambot.repository.dao;

import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.SheetPageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SheetsPageDao extends JpaRepository<SheetPageEntity, Long> {

  Optional<SheetPageEntity> findByUserIdEquals(Long userId);
}
