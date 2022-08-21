package com.rstepanchuk.miniplant.telegrambot.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "sheets_pages")
public class SheetsTableCredentialsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "sheet_id")
  String sheetId;

  @Column(name = "page_name")
  String pageName;

  @Column(name = "range")
  String range;
}
