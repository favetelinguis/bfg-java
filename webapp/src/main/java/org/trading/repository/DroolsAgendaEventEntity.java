package org.trading.repository;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class DroolsAgendaEventEntity {
  @Id
  public String id;
  public LocalDateTime insertTime;
  public String action;
  public String rule;
  public DroolsAgendaEventEntity(String action, String rule) {
    this.insertTime = LocalDateTime.now();
    this.action = action;
    this.rule = rule;
  }
}
