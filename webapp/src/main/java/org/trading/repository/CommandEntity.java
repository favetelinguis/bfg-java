package org.trading.repository;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.trading.command.TradeResultCommand;

@Document
public class CommandEntity {
  @Id
  public String id;
  public LocalDateTime insertTime;
  public String command;
  public Object item;
  public CommandEntity(String command, Object item) {
    this.insertTime = insertTime = LocalDateTime.now();
    this.command = command;
    this.item = item;
  }
}
