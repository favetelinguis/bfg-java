package org.trading.ig;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trading.command.ClosePositionCommand;
import org.trading.command.CreateWorkingOrderCommand;
import org.trading.command.UpdatePositionCommand;
import org.trading.ig.rest.AuthenticationResponseAndConversationContext;
import org.trading.ig.rest.dto.positions.otc.closeOTCPositionV1.CloseOTCPositionV1Request;
import org.trading.ig.rest.dto.positions.otc.closeOTCPositionV1.OrderType;
import org.trading.ig.rest.dto.positions.otc.updateOTCPositionV2.UpdateOTCPositionV2Request;
import org.trading.ig.rest.dto.prices.getPricesV3.GetPricesV3Response;
import org.trading.ig.rest.dto.workingorders.otc.createOTCWorkingOrderV2.CreateOTCWorkingOrderV2Request;
import org.trading.ig.rest.dto.workingorders.otc.createOTCWorkingOrderV2.Direction;
import org.trading.ig.rest.dto.workingorders.otc.createOTCWorkingOrderV2.TimeInForce;
import org.trading.ig.rest.dto.workingorders.otc.createOTCWorkingOrderV2.Type;

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
    return restAPI.getPricesV3(authContext.getConversationContext(), null, null, "0", epic, start.format(formatter), end.format(formatter), "MINUTE");
  }

  public void createOrder(CreateWorkingOrderCommand command) {
    var orderType = Type.LIMIT;
    if (orderType.equals(Type.STOP)) { // SET stop to switch system to breakout
      if (command.getDirection().equals("BUY")) {
        command.setDirection("SELL");
      } else {
        command.setDirection("BUY");
      }
    }
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
  public void closePosition(ClosePositionCommand command) {
    var request = new CloseOTCPositionV1Request();
    request.setEpic(command.getEpic());
    request.setExpiry(command.getExpiry());
    request.setTimeInForce(org.trading.ig.rest.dto.positions.otc.closeOTCPositionV1.TimeInForce.EXECUTE_AND_ELIMINATE);
    request.setOrderType(OrderType.MARKET);
    request.setSize(BigDecimal.valueOf(command.getSize()));
    request.setDirection(org.trading.ig.rest.dto.positions.otc.closeOTCPositionV1.Direction.valueOf(command.getDirection()));
    try {
      restAPI.closeOTCPositionV1(authContext.getConversationContext(), request);
    } catch (Exception e) {
      log.error("Failed close of position", e);
    }
  }

  @PreDestroy
  public void destroy() {
    log.info("Delete IG Session");
    try {
//      restAPI.deleteSessionV1(authContext.getConversationContext()); DONT DO THIS IN DEVELOP since i get loged out every time its annoying
    } catch (Exception e) {
      log.warn("Failed to destroy IG session: ", e.getMessage());
    }
  }

}