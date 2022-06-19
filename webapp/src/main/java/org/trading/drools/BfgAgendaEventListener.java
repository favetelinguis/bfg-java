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

public class BfgAgendaEventListener implements AgendaEventListener {

  @Override
  public void matchCreated(MatchCreatedEvent matchCreatedEvent) {

  }

  @Override
  public void matchCancelled(MatchCancelledEvent matchCancelledEvent) {

  }

  @Override
  public void beforeMatchFired(BeforeMatchFiredEvent beforeMatchFiredEvent) {

  }

  @Override
  public void afterMatchFired(AfterMatchFiredEvent afterMatchFiredEvent) {

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
