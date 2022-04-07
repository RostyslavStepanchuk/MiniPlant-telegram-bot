package com.rstepanchuk.miniplant.telegrambot.repository;

import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;

public interface UserRepository {

  void save(BotUser user);

  Optional<BotUser> findById(Long userId);
}
