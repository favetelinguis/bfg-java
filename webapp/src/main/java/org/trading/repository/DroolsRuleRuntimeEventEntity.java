package org.trading.repository;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class DroolsRuleRuntimeEventEntity {
  @Id
  public String id;
  public LocalDateTime insertTime;
  public String action;
  public Object objectChanged;
  public String rule;
  public String factHandler;
  public DroolsRuleRuntimeEventEntity(String action, Object objectChanged, String rule, String factHandler) {
    this.insertTime = LocalDateTime.now();
    this.action = action;
    this.objectChanged = objectChanged;
    this.rule = rule;
    this.factHandler = factHandler;
  }
}
