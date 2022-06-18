package org.trading.repository;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.trading.command.TradeResultCommand;

@Document
public class TradeResultEntity {
  @Id
  public String id;
  public LocalDateTime insertTime;
  public TradeResultCommand item;
  public TradeResultEntity(LocalDateTime insertTime, TradeResultCommand item) {
    this.insertTime = insertTime;
    this.item = item;
  }
}
