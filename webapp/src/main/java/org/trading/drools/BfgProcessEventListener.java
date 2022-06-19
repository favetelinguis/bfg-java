package org.trading.drools;

import org.kie.api.event.process.MessageEvent;
import org.kie.api.event.process.ProcessAsyncNodeScheduledEvent;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.event.process.SLAViolatedEvent;
import org.kie.api.event.process.SignalEvent;

public class BfgProcessEventListener implements ProcessEventListener {

  @Override
  public void beforeProcessStarted(ProcessStartedEvent processStartedEvent) {
    
  }

  @Override
  public void afterProcessStarted(ProcessStartedEvent processStartedEvent) {

  }

  @Override
  public void beforeProcessCompleted(ProcessCompletedEvent processCompletedEvent) {

  }

  @Override
  public void afterProcessCompleted(ProcessCompletedEvent processCompletedEvent) {

  }

  @Override
  public void beforeNodeTriggered(ProcessNodeTriggeredEvent processNodeTriggeredEvent) {

  }

  @Override
  public void afterNodeTriggered(ProcessNodeTriggeredEvent processNodeTriggeredEvent) {

  }

  @Override
  public void beforeNodeLeft(ProcessNodeLeftEvent processNodeLeftEvent) {

  }

  @Override
  public void afterNodeLeft(ProcessNodeLeftEvent processNodeLeftEvent) {

  }

  @Override
  public void beforeVariableChanged(ProcessVariableChangedEvent processVariableChangedEvent) {

  }

  @Override
  public void afterVariableChanged(ProcessVariableChangedEvent processVariableChangedEvent) {

  }

  @Override
  public void beforeSLAViolated(SLAViolatedEvent event) {
    ProcessEventListener.super.beforeSLAViolated(event);
  }

  @Override
  public void afterSLAViolated(SLAViolatedEvent event) {
    ProcessEventListener.super.afterSLAViolated(event);
  }

  @Override
  public void onSignal(SignalEvent event) {
    ProcessEventListener.super.onSignal(event);
  }

  @Override
  public void onMessage(MessageEvent event) {
    ProcessEventListener.super.onMessage(event);
  }

  @Override
  public void onAsyncNodeScheduledEvent(ProcessAsyncNodeScheduledEvent event) {
    ProcessEventListener.super.onAsyncNodeScheduledEvent(event);
  }
}
