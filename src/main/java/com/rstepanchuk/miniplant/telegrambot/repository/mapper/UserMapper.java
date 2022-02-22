package com.rstepanchuk.miniplant.telegrambot.repository.mapper;

import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.BotUserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  BotUserEntity toBotUserEntity(BotUser botUser);

  BotUser toBotUser(BotUserEntity entity);

}
