package org.trading.repository;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class IgStreamEntity {
  @Id
  public String id;
  public LocalDateTime insertTime;
  public String item;
  public Map<String, String> update;
  public IgStreamEntity(LocalDateTime insertTime, String item, Map<String, String> update) {
    this.insertTime = insertTime;
    this.item = item;
    this.update = update;
  }
}
