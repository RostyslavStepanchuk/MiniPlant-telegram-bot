package com.rstepanchuk.miniplant.telegrambot.repository;

import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<BotUser, Long> {
}
