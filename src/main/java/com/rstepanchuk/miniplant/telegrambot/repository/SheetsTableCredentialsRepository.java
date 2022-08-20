package com.rstepanchuk.miniplant.telegrambot.repository;

import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.model.SheetsTableCredentials;

public interface SheetsTableCredentialsRepository {

  Optional<SheetsTableCredentials> findByUserId(Long userId);
}
