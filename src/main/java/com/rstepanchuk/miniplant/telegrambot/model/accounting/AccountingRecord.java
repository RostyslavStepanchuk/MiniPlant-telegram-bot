package com.rstepanchuk.miniplant.telegrambot.model.accounting;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Buttons.INCOME;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import com.google.common.annotations.VisibleForTesting;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import lombok.Data;

@Data
public class AccountingRecord {

  @VisibleForTesting
  protected static final String DEFAULT_VALUE = "-";

  private Long id;
  private String type;
  private BigDecimal amount;
  private String account;
  private String category;
  private String contractor;
  private String comment;
  private BotUser user;
  private LocalDate entered;

  public AccountingRecord merge(AccountingRecord incomingRecord) {
    Optional.ofNullable(incomingRecord.getAmount()).ifPresent(this::setAmount);
    Optional.ofNullable(incomingRecord.getCategory()).ifPresent(this::setCategory);
    Optional.ofNullable(incomingRecord.getType()).ifPresent(this::setType);
    Optional.ofNullable(incomingRecord.getAccount()).ifPresent(this::setAccount);
    return this;
  }

  public AccountingRecord closeEmptyFields() {
    this.setCategory(Optional.ofNullable(this.getCategory()).orElse(DEFAULT_VALUE));
    this.setType(Optional.ofNullable(this.getType()).orElse(DEFAULT_VALUE));
    this.setAccount(Optional.ofNullable(this.getAccount()).orElse(DEFAULT_VALUE));
    return this;
  }

  public String inMessageFormat() {
    StringBuilder sb = new StringBuilder();
    Optional.ofNullable(this.amount)
        .ifPresent(a -> sb.append(a).append(" грн\n"));
    Optional.ofNullable(this.type)
        .ifPresent(t -> sb.append("Тип: ").append(t).append("\n"));
    Optional.ofNullable(this.account)
        .ifPresent(a -> sb.append("Рахунок: ").append(a).append("\n"));
    Optional.ofNullable(this.category)
        .ifPresent(c -> sb.append("Категорія: ").append(c));
    return sb.toString();
  }

  public boolean isIncome() {
    if (this.type == null) {
      throw new NullPointerException("Trying to check if record is income while type is null");
    }
    return this.type.equals(INCOME);
  }
}
