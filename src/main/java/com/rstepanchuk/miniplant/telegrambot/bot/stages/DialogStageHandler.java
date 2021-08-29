package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.UNDEFINED;

import java.util.Map;
import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.exception.UserNotAllowedException;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public class DialogStageHandler {

  private final Map<String, DialogStage> stagesContainer;
  private final UserRepository userRepository;

  public DialogStageHandler(Map<String, DialogStage> stagesContainer,
                            UserRepository userRepository) {
    this.stagesContainer = stagesContainer;
    this.userRepository = userRepository;
  }

  private DialogStage getStage(String stageName) {
    return stagesContainer.getOrDefault(stageName, stagesContainer.get(UNDEFINED));
  }

  private void updateUserStage(BotUser user, String nextStage) {
    user.setStageId(nextStage);
    userRepository.save(user);
  }

  public Optional<? extends BotApiMethod> handleStage(Update update) {
    Long userId = update.getMessage().getFrom().getId();
    BotUser user = userRepository.findById(userId)
        .orElseThrow(UserNotAllowedException::new);
    DialogStage currentStage = getStage(user.getStageId());
    Optional<? extends BotApiMethod> chatOutput = currentStage.execute(update);
    updateUserStage(user, currentStage.getNextStage());
    return chatOutput;
  }

}
