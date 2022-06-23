package org.trading.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Confirms {
  private String status;
  private String dealStatus;
  private String direction;
  private String epic;
  private String dealReference;
  private String dealId;
  private String reason;
  private String date;

  public boolean isOrderCreatedSuccess() {
    return isStatusOpen() && isDealStatusAccepted();
  }

  public boolean isRejected() {
    return isDealStatusRejected();
  }
  public boolean isOrderDeletedSuccess() {
    return isStatusDeleted() && isDealStatusAccepted();
  }

  public boolean isOrderUpdatedSuccess() {
    return isStatusAmended() && isDealStatusAccepted();
  }

  private boolean isStatusOpen() {
    return status.equals("OPEN");
  }

  private boolean isStatusAmended() {
    return status.equals("AMENDED"); // In Confirms updates are AMENDED
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
