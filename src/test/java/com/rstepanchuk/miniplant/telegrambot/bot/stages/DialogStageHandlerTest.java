package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUser.DEFAULT_USER_ID;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.ACCOUNTING_INC_EXP;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.MAIN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.description;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import com.rstepanchuk.miniplant.telegrambot.exception.UserNotAllowedException;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;


@ExtendWith(MockitoExtension.class)
class DialogStageHandlerTest {

  @InjectMocks
  private DialogStageHandler dialogStageHandler;
  @Mock
  private Map<String, DialogStage> stagesContainer;
  @Mock
  private UserRepository userRepository;
  @Mock
  private DialogStageMain dialogStageMain;

  private void mockStagesContainer() {
    when(stagesContainer.getOrDefault(eq(MAIN), any(DialogStage.class)))
        .thenReturn(dialogStageMain);
    when(stagesContainer.get(any(String.class)))
        .thenReturn(dialogStageMain);
    when(dialogStageMain.getNextStage())
        .thenReturn(ACCOUNTING_INC_EXP);
  }

  @Test
  void handleStage_shouldExecuteStageForCurrentUser() {
    Update update = TelegramTestUpdate.getBasicUpdate();

    // user setup
    BotUser user = new BotUser();
    user.setStageId(MAIN);
    user.setId(DEFAULT_USER_ID);
    ArgumentCaptor<BotUser> userCaptor = ArgumentCaptor.forClass(BotUser.class);

    mockStagesContainer();
    when(userRepository.findById(DEFAULT_USER_ID))
        .thenReturn(Optional.of(user));

    Optional<BotApiMethod> result = dialogStageHandler.handleStage(update);

    verify(userRepository,
        description("Request to user repository expected to define current stage id"))
        .findById(DEFAULT_USER_ID);
    verify(dialogStageMain,
        description("Main stage expected to be executed"))
        .execute(update);

    verify(userRepository,
        description("User expected to be saved to repository"))
        .save(userCaptor.capture());

    BotUser updatedUser = userCaptor.getValue();
    assertEquals(updatedUser.getStageId(), dialogStageMain.getNextStage());
    assertThat(result, instanceOf(Optional.class));
  }

  @Test
  void handleStage_whenUserIdIsNotFound_shouldThrowUserNotAllowedException() {
    Update update = TelegramTestUpdate.getBasicUpdate();

    when(userRepository.findById(DEFAULT_USER_ID))
        .thenReturn(Optional.empty());

    assertThrows(UserNotAllowedException.class, ()->dialogStageHandler.handleStage(update));
  }
}