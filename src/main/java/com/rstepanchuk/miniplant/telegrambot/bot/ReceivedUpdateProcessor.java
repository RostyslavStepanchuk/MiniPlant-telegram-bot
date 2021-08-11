package com.rstepanchuk.miniplant.telegrambot.bot;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

public interface ReceivedUpdateProcessor {
  <Method extends BotApiMethod> Optional<Method> processUpdate(Update update);
}
