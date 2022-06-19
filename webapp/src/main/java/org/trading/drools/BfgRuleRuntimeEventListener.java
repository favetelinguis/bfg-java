package org.trading.drools;

import lombok.extern.slf4j.Slf4j;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.trading.repository.DroolsRuleRuntimeEventEntity;
import org.trading.repository.DroolsRuleRuntimeRepository;

@Slf4j
public class BfgRuleRuntimeEventListener implements RuleRuntimeEventListener {

  private final DroolsRuleRuntimeRepository repository;

  public BfgRuleRuntimeEventListener(DroolsRuleRuntimeRepository repository) {
    this.repository = repository;
  }

  @Override
  public void objectInserted(ObjectInsertedEvent objectInsertedEvent) {
    var ruleName = objectInsertedEvent.getRule() != null ? objectInsertedEvent.getRule().getName() : "NORULE";
    repository.save(new DroolsRuleRuntimeEventEntity("insert", objectInsertedEvent.getObject(), ruleName, objectInsertedEvent.getFactHandle().toExternalForm()));
  }

  @Override
  public void objectUpdated(ObjectUpdatedEvent objectUpdatedEvent) {
    var ruleName = objectUpdatedEvent.getRule() != null ? objectUpdatedEvent.getRule().getName() : "NORULE";
    repository.save(new DroolsRuleRuntimeEventEntity("update", objectUpdatedEvent.getObject(), ruleName, objectUpdatedEvent.getFactHandle().toExternalForm()));
  }

  @Override
  public void objectDeleted(ObjectDeletedEvent objectDeletedEvent) {
    var ruleName = objectDeletedEvent.getRule() != null ? objectDeletedEvent.getRule().getName() : "NORULE";
    repository.save(new DroolsRuleRuntimeEventEntity("delete", objectDeletedEvent.getOldObject(), ruleName, objectDeletedEvent.getFactHandle().toExternalForm()));
  }
}
