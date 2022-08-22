package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.STAGE_WILL_BE_RESET;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.UNEXPECTED_ERROR;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages;

import com.rstepanchuk.miniplant.telegrambot.bot.api.MessageBuilder;
import com.rstepanchuk.miniplant.telegrambot.exception.ApplicationException;
import com.rstepanchuk.miniplant.telegrambot.exception.GoogleAuthenticationException;
import com.rstepanchuk.miniplant.telegrambot.exception.SheetsNotSetUpException;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
@Slf4j
public class DialogStageHandler {

  private final UserRepository userRepository;
  private final ApplicationContext context;
  private final DialogStageUndefined defaultStage;
  private final DialogStageGoogleAuth googleAuthStage;
  private final DialogStageSheetsConfig sheetsSetupStage;

  private DialogStage getStage(String stageName) {
    try {
      return context.getBean(stageName, DialogStage.class);
    } catch (BeansException e) {
      log.error("Unable to find stage {}", stageName, e);
      return defaultStage;
    }
  }

  private void saveUserChangesDuringStage(BotUser user) {
    userRepository.save(user);
  }

  public void handleStage(
      Update update,
      BotUser user,
      TelegramLongPollingBot bot
  ) throws TelegramApiException {
    try {
      DialogStage currentStage = getStage(user.getStageId());
      String nextStage = currentStage.execute(update, bot, user);
      user.setStageId(nextStage);
    } catch (GoogleAuthenticationException e) {
      log.error("Google Authentication error: ", e);
      bot.execute(MessageBuilder.basicMessage(user.getId(), e.getMessage()));
      googleAuthStage.execute(update, bot, user);
      handleStage(update, user, bot);
    } catch (SheetsNotSetUpException e) {
      log.error("Google Sheets not set up error: ", e);
      bot.execute(MessageBuilder.basicMessage(user.getId(), e.getMessage()));
      user.setStageId(sheetsSetupStage.execute(update, bot, user));
    } catch (ApplicationException e) {
      log.error("Application error: ", e);
      bot.execute(MessageBuilder.basicMessage(user.getId(), e.getMessage()));
      bot.execute(MessageBuilder.basicMessage(user.getId(), STAGE_WILL_BE_RESET));
      user.setStageId(Stages.MAIN);
    } catch (Exception e) {
      log.error("Error: ", e);
      bot.execute(MessageBuilder.basicMessage(user.getId(), UNEXPECTED_ERROR));
    } finally {
      saveUserChangesDuringStage(user);
    }

  }

}
