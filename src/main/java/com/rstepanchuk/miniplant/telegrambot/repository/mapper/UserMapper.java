package com.rstepanchuk.miniplant.telegrambot.repository.mapper;

import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.BotUserEntity;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.MarkupMessageEntity;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.SheetsTableCredentialsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
    uses = SheetsTablesCredentialsMapper.class)
public abstract class UserMapper {

  @Autowired
  protected SheetsTablesCredentialsMapper sheetsCredentialsMapper;

  @Mapping(target = "messagesWithMarkup", qualifiedByName = "toMarkupMessageEntity")
  @Mapping(target = "sheetsCredentials", source = ".",
      qualifiedByName = "toSheetsCredentialsEntity")
  public abstract BotUserEntity toBotUserEntity(BotUser botUser);

  @Mapping(target = "messagesWithMarkup", qualifiedByName = "toMarkupMessageId")
  public abstract BotUser toBotUser(BotUserEntity entity);

  @Named("toMarkupMessageEntity")
  protected MarkupMessageEntity toMarkupMessageEntity(Integer messageId) {
    MarkupMessageEntity entity = new MarkupMessageEntity();
    entity.setId(messageId);
    return entity;
  }

  @Named("toMarkupMessageId")
  protected Integer toMarkupMessageEntity(MarkupMessageEntity entity) {
    return entity.getId();
  }

  @Named("toSheetsCredentialsEntity")
  protected SheetsTableCredentialsEntity toSheetsCredentialsEntity(BotUser user) {
    return user.getSheetsTableCredentials()
        .map(sheetsCredentialsMapper::toEntity)
        .orElse(null);
  }
}
