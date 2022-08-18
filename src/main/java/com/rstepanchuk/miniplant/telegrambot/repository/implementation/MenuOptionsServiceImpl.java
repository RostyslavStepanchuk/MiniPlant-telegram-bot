package com.rstepanchuk.miniplant.telegrambot.repository.implementation;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.ACCOUNT_SELECTION;

import java.util.Collections;
import java.util.List;
import com.rstepanchuk.miniplant.telegrambot.repository.MenuOptionsService;

// TODO: replace stub with real service
public class MenuOptionsServiceImpl implements MenuOptionsService {

  @Override
  public List<String> getOptionsForTheStage(String stage) {
    if (stage.equals(ACCOUNT_SELECTION)) {
      return List.of("нал", "приват", "моно", "каса");
    }
    return Collections.emptyList();
  }

}
