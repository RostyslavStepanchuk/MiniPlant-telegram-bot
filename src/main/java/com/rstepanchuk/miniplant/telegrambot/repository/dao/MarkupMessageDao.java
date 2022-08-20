package com.rstepanchuk.miniplant.telegrambot.repository.dao;

import com.rstepanchuk.miniplant.telegrambot.repository.entity.MarkupMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarkupMessageDao extends JpaRepository<MarkupMessageEntity, Integer> {
}
