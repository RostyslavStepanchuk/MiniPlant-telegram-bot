package com.rstepanchuk.miniplant.telegrambot.repository.mapper;

import com.rstepanchuk.miniplant.telegrambot.model.SheetsTableCredentials;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.SheetsTableCredentialsEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SheetsTablesCredentialsMapper {

  SheetsTableCredentials toModel(SheetsTableCredentialsEntity entity);

  SheetsTableCredentialsEntity toEntity(SheetsTableCredentials model);
}
