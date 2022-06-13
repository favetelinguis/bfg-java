package org.trading.ig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.trading.ig.rest.AuthenticationResponseAndConversationContext;
import org.trading.ig.rest.dto.workingorders.otc.createOTCWorkingOrderV2.CreateOTCWorkingOrderV2Request;
import org.trading.ig.rest.dto.workingorders.otc.createOTCWorkingOrderV2.Direction;

// TODO I should remake the Rest client to be Async, right now fetch data and all other request uses the same thread as drools
// so events are not processed while i make rest calls which is bad.
@Service
public class IgRestService {
  private static Logger LOG = LoggerFactory.getLogger(IgRestService.class);

  private final RestAPI restAPI;
  private final AuthenticationResponseAndConversationContext authContext;

  public IgRestService(RestAPI restAPI, AuthenticationResponseAndConversationContext authContext) {
    this.restAPI = restAPI;
    this.authContext = authContext;
  }

  public void createOrder() {
//    LOG.info("==== IN EXECUTOR REST API");
    var request = new CreateOTCWorkingOrderV2Request();
    request.setDirection(Direction.BUY);
//    restAPI.createOTCWorkingOrderV2(authContext.getConversationContext(), request);
  }
}