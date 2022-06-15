package org.trading.ig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trading.ig.rest.AuthenticationResponseAndConversationContext;
import org.trading.ig.rest.dto.prices.getPricesV3.GetPricesV3Response;

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

  // end is in Stockholm timezone since that is the timezone of my IG account
  public GetPricesV3Response getData(String epic, LocalDateTime start, LocalDateTime end) throws Exception {
    var formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    LOG.info("GET DATA BETWEEN {} and {}", start.format(formatter), end.format(formatter));
    return restAPI.getPricesV3(authContext.getConversationContext(), null, null, null, epic, start.format(formatter), end.format(formatter), "MINUTE");
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