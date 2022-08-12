package com.rstepanchuk.miniplant.telegrambot.model.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import lombok.Data;

@Data
public class AccountingRecord {

  private Long id;
  private String type;
  private BigDecimal amount;
  private String account;
  private String category;
  private String contractor;
  private String comment;
  private BotUser user;
  private LocalDate entered;

}
