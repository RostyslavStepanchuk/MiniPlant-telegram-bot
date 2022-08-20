package com.rstepanchuk.miniplant.telegrambot.repository;

import java.util.List;

public interface MenuOptionsRepository {

  List<String> getOptionsByMenuName(String stage);
}
