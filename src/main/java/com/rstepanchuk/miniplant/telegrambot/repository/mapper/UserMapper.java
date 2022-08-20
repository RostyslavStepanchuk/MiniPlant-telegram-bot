package com.rstepanchuk.miniplant.telegrambot.repository.mapper;

import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.BotUserEntity;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.MarkupMessageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "messagesWithMarkup", qualifiedByName = "toMarkupMessageEntity")
  BotUserEntity toBotUserEntity(BotUser botUser);

  @Mapping(target = "messagesWithMarkup", qualifiedByName = "toMarkupMessageId")
  BotUser toBotUser(BotUserEntity entity);

  @Named("toMarkupMessageEntity")
  default MarkupMessageEntity toMarkupMessageEntity(Integer messageId) {
    MarkupMessageEntity entity = new MarkupMessageEntity();
    entity.setId(messageId);
    return entity;
  }

  @Named("toMarkupMessageId")
  default Integer toMarkupMessageEntity(MarkupMessageEntity entity) {
    return entity.getId();
  }

}
