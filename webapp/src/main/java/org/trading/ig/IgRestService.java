package org.trading.ig;

import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.trading.command.FetchOpeningRangeCommand;
import org.trading.ig.rest.AuthenticationResponseAndConversationContext;
import org.trading.ig.rest.dto.workingorders.otc.createOTCWorkingOrderV2.CreateOTCWorkingOrderV2Request;
import org.trading.ig.rest.dto.workingorders.otc.createOTCWorkingOrderV2.Direction;

@Service
public class IgRestService {
  private static Logger LOG = LoggerFactory.getLogger(IgRestService.class);

  private final RestAPI restAPI;
  private final AuthenticationResponseAndConversationContext authContext;

  @Autowired
  public IgRestService(RestAPI restAPI, AuthenticationResponseAndConversationContext authContext) {
    this.restAPI = restAPI;
    this.authContext = authContext;
  }

  @EventListener(FetchOpeningRangeCommand.class)
  public void createOrder() {
    LOG.info("==== IN EXECUTOR REST API");
    var request = new CreateOTCWorkingOrderV2Request();
    request.setDirection(Direction.BUY);
//    restAPI.createOTCWorkingOrderV2(authContext.getConversationContext(), request);
  }

  @PreDestroy
  public void destroy() {
    LOG.info("Delete IG Session");
    try {
      restAPI.deleteSessionV1(authContext.getConversationContext());
    } catch (Exception e) {
      LOG.warn("Failed to destroy IG session: ", e.getMessage());
    }
  }
}