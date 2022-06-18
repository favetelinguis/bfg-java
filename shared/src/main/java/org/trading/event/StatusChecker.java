package org.trading.event;

import lombok.Data;

@Data
public abstract class StatusChecker {
  protected String status;
  protected String dealStatus;

  public boolean isPositionEntry() {
    return isOrderCreatedSuccess();
  }
  public boolean isPositionExit() {
    return isOrderDeletedSuccess();
  }

  public boolean isOrderCreatedSuccess() {
    return isStatusOpen() && isDealStatusAccepted();
  }

  public boolean isOrderCreatedRejected() {
    return isStatusOpen() && isDealStatusRejected();
  }
  public boolean isOrderDeletedSuccess() {
    return isStatusDeleted() && isDealStatusAccepted();
  }

  public boolean isOrderDeletedRejected() {
    return isStatusDeleted() && isDealStatusRejected();
  }

  public boolean isOrderUpdatedSuccess() {
    return isStatusAmended() && isDealStatusAccepted();
  }

  public boolean isOrderUpdatedRejected() {
    return isStatusAmended() && isDealStatusRejected();
  }
  private boolean isStatusOpen() {
    return status.equals("OPEN");
  }

  private boolean isStatusAmended() {
    return status.equals("AMENDED");
  }

  private boolean isStatusDeleted() {
    return status.equals("DELETED");
  }

  private boolean isDealStatusAccepted() {
    return dealStatus.equals("ACCEPTED");
  }

  private boolean isDealStatusRejected() {
    return dealStatus.equals("REJECTED");
  }
}
