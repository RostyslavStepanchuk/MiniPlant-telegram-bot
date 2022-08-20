package com.rstepanchuk.miniplant.telegrambot.repository;

import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.model.SheetsTableCredentials;
import com.rstepanchuk.miniplant.telegrambot.repository.dao.SheetsTableCredentialsDao;
import com.rstepanchuk.miniplant.telegrambot.repository.mapper.SheetsTablesCredentialsMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SheetsTableCredentialsRepositoryImpl implements
    SheetsTableCredentialsRepository {

  private final SheetsTableCredentialsDao sheetsTableCredentialsDao;
  private final SheetsTablesCredentialsMapper mapper;

  public Optional<SheetsTableCredentials> findByUserId(Long userId) {
    return sheetsTableCredentialsDao.findByUserIdEquals(userId)
        .map(mapper::toModel);
  }

}
