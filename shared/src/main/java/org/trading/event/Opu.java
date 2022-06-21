package org.trading.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Opu {
  private String status;
  private String dealStatus;
  private String timestamp;
  private String direction;
  private String dealId;
  private String epic;
  private String dealReference;
  private Double level;
  private String orderType;
  public boolean isPositionEntry() {
    return isOrderCreatedSuccess() && isPositionUpdate();
  }
  public boolean isPositionExit() {
    return isOrderDeletedSuccess() && isPositionUpdate();
  }

  private boolean isPositionUpdate() {
    return orderType == null;
  }
  private boolean isOrderCreatedSuccess() {
    return isStatusOpen() && isDealStatusAccepted();
  }

  private boolean isOrderDeletedSuccess() {
    return isStatusDeleted() && isDealStatusAccepted();
  }

  private boolean isOrderUpdatedSuccess() {
    return isStatusAmended() && isDealStatusAccepted();
  }

  private boolean isStatusOpen() {
    return status.equals("OPEN");
  }

  private boolean isStatusAmended() {
    return status.equals("UPDATED");
  }

  private boolean isStatusDeleted() {
    return status.equals("DELETED");
  }

  private boolean isDealStatusAccepted() {
    return dealStatus.equals("ACCEPTED");
  }
}
