package com.rstepanchuk.miniplant.telegrambot.repository.dao;

import com.rstepanchuk.miniplant.telegrambot.repository.entity.BotUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<BotUserEntity, Long> {
}
