package org.trading.drools;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.trading.repository.DroolsAgendaEventEntity;
import org.trading.repository.DroolsAgendaRepository;
import org.trading.repository.DroolsRuleRuntimeRepository;

public class BfgAgendaEventListener implements AgendaEventListener {

  private final DroolsAgendaRepository repository;

  public BfgAgendaEventListener(DroolsAgendaRepository repository) {
    this.repository = repository;
  }
  @Override
  public void matchCreated(MatchCreatedEvent matchCreatedEvent) {
    repository.save(new DroolsAgendaEventEntity("matchCreated", matchCreatedEvent.getMatch().getRule().getName()));
  }

  @Override
  public void matchCancelled(MatchCancelledEvent matchCancelledEvent) {

    repository.save(new DroolsAgendaEventEntity("matchCancelled", matchCancelledEvent.getMatch().getRule().getName()));
  }

  @Override
  public void beforeMatchFired(BeforeMatchFiredEvent beforeMatchFiredEvent) {

    repository.save(new DroolsAgendaEventEntity("beforeMatchFired", beforeMatchFiredEvent.getMatch().getRule().getName()));
  }

  @Override
  public void afterMatchFired(AfterMatchFiredEvent afterMatchFiredEvent) {
    repository.save(new DroolsAgendaEventEntity("afterMatchFired", afterMatchFiredEvent.getMatch().getRule().getName()));
  }

  @Override
  public void agendaGroupPopped(AgendaGroupPoppedEvent agendaGroupPoppedEvent) {

  }

  @Override
  public void agendaGroupPushed(AgendaGroupPushedEvent agendaGroupPushedEvent) {

  }

  @Override
  public void beforeRuleFlowGroupActivated(
      RuleFlowGroupActivatedEvent ruleFlowGroupActivatedEvent) {

  }

  @Override
  public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent ruleFlowGroupActivatedEvent) {

  }

  @Override
  public void beforeRuleFlowGroupDeactivated(
      RuleFlowGroupDeactivatedEvent ruleFlowGroupDeactivatedEvent) {

  }

  @Override
  public void afterRuleFlowGroupDeactivated(
      RuleFlowGroupDeactivatedEvent ruleFlowGroupDeactivatedEvent) {

  }
}
