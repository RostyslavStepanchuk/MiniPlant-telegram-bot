package com.rstepanchuk.miniplant.telegrambot.repository;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Buttons.EXPENSES;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Buttons.INCOME;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.ACCOUNT_SELECTION;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.CATEGORY_SELECTION;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.TYPE_SELECTION;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: replace stub with real service
public class MenuOptionsRepositoryImpl implements MenuOptionsRepository {

  @Override
  public List<String> getOptionsByMenuName(String menuName) {

    switch (menuName) {
      case TYPE_SELECTION:
        return List.of("приход", "списание");
      case ACCOUNT_SELECTION:
        return List.of("нал", "приват", "моно", "каса");
      case CATEGORY_SELECTION + "-" + INCOME:
        return Stream.of("Instagram", "Crafta", "Etsy", "Prom", "Посетители", "Маркет",
                "Корпоратив", "Facebook", "Розетка", "НЛО", "Епіцентр")
            .sorted()
            .collect(Collectors.toList());
      case CATEGORY_SELECTION + "-" + EXPENSES:
        return Stream.of("Горшки", "Растения", "Новая  Почта", "Аренда", "Налоги", "ЗП", "Упаковка",
                "Расходники", "Другое", "Маркет", "Crafta", "Розетка", "Админ", "Оборудование",
                "Укрпочта", "Грунт", "НЛО", "Обналичить себе", "Такси")
            .sorted()
            .collect(Collectors.toList());
      default:
        return Collections.emptyList();
    }
  }
}
