package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.STAGE_WILL_BE_RESET;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.UNEXPECTED_ERROR;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages;

import com.rstepanchuk.miniplant.telegrambot.bot.MessageBuilder;
import com.rstepanchuk.miniplant.telegrambot.exception.ApplicationException;
import com.rstepanchuk.miniplant.telegrambot.exception.GoogleAuthenticationException;
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
  private final DialogStage defaultStage;

  private DialogStage getStage(String stageName) {
    try {
      return context.getBean(stageName, DialogStage.class);
    } catch (BeansException e) {
      log.error("Unable to find stage {}", stageName, e);
      return defaultStage;
    }
  }

  private void updateUserStage(BotUser user, String nextStage) {
    user.setStageId(nextStage);
    userRepository.save(user);
  }


  public void handleStage(
      Update update,
      BotUser user,
      TelegramLongPollingBot bot
  ) throws TelegramApiException {
    try {
      DialogStage currentStage = getStage(user.getStageId());
      String nextStage = currentStage.execute(update, bot);
      updateUserStage(user, nextStage);
    } catch (GoogleAuthenticationException e) {
      bot.execute(MessageBuilder.basicMessage(update, e.getMessage()));
      DialogStage stage = getStage(Stages.GOOGLE_AUTH);
      stage.execute(update, bot);
      handleStage(update, user, bot);
    } catch (ApplicationException e) {
      bot.execute(MessageBuilder.basicMessage(update, e.getMessage()));
      bot.execute(MessageBuilder.basicMessage(update, STAGE_WILL_BE_RESET));
      updateUserStage(user, Stages.MAIN);
    } catch (Exception e) {
      bot.execute(MessageBuilder.basicMessage(update, UNEXPECTED_ERROR));
    }

  }

}
