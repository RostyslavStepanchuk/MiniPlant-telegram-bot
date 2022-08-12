package com.rstepanchuk.miniplant.telegrambot.repository.mapper;

import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.AccountingRecordEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
    uses = UserMapper.class)
public interface AccountingRecordMapper {

  AccountingRecord toAccountingRecord(AccountingRecordEntity entity);

  AccountingRecordEntity toAccountingRecordEntity(AccountingRecord accountingRecord);

}
