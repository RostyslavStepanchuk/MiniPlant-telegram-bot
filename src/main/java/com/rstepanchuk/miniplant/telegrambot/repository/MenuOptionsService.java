package com.rstepanchuk.miniplant.telegrambot.repository;

import java.util.List;

public interface MenuOptionsService {

  List<String> getOptionsForTheStage(String stage);
}
