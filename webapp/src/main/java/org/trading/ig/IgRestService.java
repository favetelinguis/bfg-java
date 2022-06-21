package org.trading.ig;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trading.command.CreateWorkingOrderCommand;
import org.trading.command.UpdatePositionCommand;
import org.trading.command.UpdateWorkingOrderCommand;
import org.trading.ig.rest.AuthenticationResponseAndConversationContext;
import org.trading.ig.rest.dto.positions.otc.closeOTCPositionV1.CloseOTCPositionV1Request;
import org.trading.ig.rest.dto.positions.otc.updateOTCPositionV2.UpdateOTCPositionV2Request;
import org.trading.ig.rest.dto.prices.getPricesV3.GetPricesV3Response;
import org.trading.ig.rest.dto.workingorders.otc.createOTCWorkingOrderV2.CreateOTCWorkingOrderV2Request;
import org.trading.ig.rest.dto.workingorders.otc.createOTCWorkingOrderV2.Direction;
import org.trading.ig.rest.dto.workingorders.otc.createOTCWorkingOrderV2.TimeInForce;
import org.trading.ig.rest.dto.workingorders.otc.createOTCWorkingOrderV2.Type;
import org.trading.ig.rest.dto.workingorders.otc.updateOTCWorkingOrderV2.UpdateOTCWorkingOrderV2Request;

@Service
@Slf4j
public class IgRestService {
  private final RestAPI restAPI;
  private final AuthenticationResponseAndConversationContext authContext;

  @Autowired
  public IgRestService(RestAPI restAPI, AuthenticationResponseAndConversationContext authContext) {
    this.restAPI = restAPI;
    this.authContext = authContext;
  }

  // end is in Stockholm timezone since that is the timezone of my IG account
  // The answer will have the oldest data first in the list.
  // Both start and end is included HOWEVER the end time is still forming, that will not be a completed candle so
  // most often i want to exclude that.
  public GetPricesV3Response getData(String epic, LocalDateTime start, LocalDateTime end) throws Exception {
    var formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    log.info("GET DATA BETWEEN {} and {}", start.format(formatter), end.format(formatter));
    return restAPI.getPricesV3(authContext.getConversationContext(), null, null, null, epic, start.format(formatter), end.format(formatter), "MINUTE");
  }

  public void createOrder(CreateWorkingOrderCommand command) {
    var request = new CreateOTCWorkingOrderV2Request();
    // Default
    request.setForceOpen(false);
    request.setTimeInForce(TimeInForce.GOOD_TILL_DATE);
    request.setType(Type.LIMIT);
    request.setGuaranteedStop(false);
    // Per market
    request.setEpic(command.getEpic());
    request.setSize(BigDecimal.valueOf(command.getSize()));
    request.setDirection(Direction.valueOf(command.getDirection()));
    request.setStopDistance(BigDecimal.valueOf(command.getStopDistance()));
    request.setLimitDistance(BigDecimal.valueOf(command.getTargetDistance()));
    request.setStopDistance(BigDecimal.valueOf(command.getStopDistance()));
    request.setLevel(BigDecimal.valueOf(command.getLevel()));
    request.setCurrencyCode(command.getCurrencyCode());
    request.setExpiry(command.getExpiry());
    request.setGoodTillDate(command.getGoodTillDate());
    try {
      restAPI.createOTCWorkingOrderV2(authContext.getConversationContext(), request);
    } catch (Exception e) {
      log.error("Failure creating order ", e);
    }
  }

  public void deleteOrder(String dealId) {
    try {
      restAPI.deleteOTCWorkingOrderV2(authContext.getConversationContext(), dealId);
    } catch (Exception e) {
      log.error("Failure when deleting order ", e);
    }
  }

  public void updateOrder(UpdateWorkingOrderCommand command) {
    var request = new UpdateOTCWorkingOrderV2Request();
    // Default
    request.setTimeInForce(
        org.trading.ig.rest.dto.workingorders.otc.updateOTCWorkingOrderV2.TimeInForce.GOOD_TILL_DATE);
    request.setType(org.trading.ig.rest.dto.workingorders.otc.updateOTCWorkingOrderV2.Type.LIMIT);
    // Per market
    request.setStopDistance(BigDecimal.valueOf(command.getStopDistance()));
    request.setLimitDistance(BigDecimal.valueOf(command.getTargetDistance()));
    request.setStopDistance(BigDecimal.valueOf(command.getStopDistance()));
    request.setLevel(BigDecimal.valueOf(command.getLevel()));
    request.setGoodTillDate(command.getGoodTillDate());
    try {
      restAPI.updateOTCWorkingOrderV2(authContext.getConversationContext(), command.getDealId(), request);
    } catch (Exception e) {
      log.error("Fail to update working order", e);
    }
  }
  public void updatePosition(UpdatePositionCommand command) {
    var request = new UpdateOTCPositionV2Request();
    // Default
    request.setTrailingStop(true);
    request.setTrailingStopIncrement(BigDecimal.valueOf(1l));
    // Per market
    request.setLimitLevel(BigDecimal.valueOf(command.getTargetLevel()));
    request.setStopLevel(BigDecimal.valueOf(command.getStopLevel()));
    request.setTrailingStopDistance(BigDecimal.valueOf(command.getTrailingStopDistance()));
    try {
      restAPI.updateOTCPositionV2(authContext.getConversationContext(), command.getDealId(), request);
    } catch (Exception e) {
      log.error("Failed updating position ", e);
    }
  }
  public void closePosition() {
    var request = new CloseOTCPositionV1Request();
//    restAPI.closeOTCPositionV1(authContext.getConversationContext(), request);
  }

  @PreDestroy
  public void destroy() {
    log.info("Delete IG Session");
    try {
      restAPI.deleteSessionV1(authContext.getConversationContext());
    } catch (Exception e) {
      log.warn("Failed to destroy IG session: ", e.getMessage());
    }
  }

}