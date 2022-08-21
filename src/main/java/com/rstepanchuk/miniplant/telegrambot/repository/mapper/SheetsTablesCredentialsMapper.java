package com.rstepanchuk.miniplant.telegrambot.repository.mapper;

import com.rstepanchuk.miniplant.telegrambot.model.SheetsTableCredentials;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.SheetsTableCredentialsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SheetsTablesCredentialsMapper {

  @Mapping(target = "merge", ignore = true)
  SheetsTableCredentials toModel(SheetsTableCredentialsEntity entity);

  SheetsTableCredentialsEntity toEntity(SheetsTableCredentials model);
}
