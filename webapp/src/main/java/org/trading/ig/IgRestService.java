package org.trading.ig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trading.ig.rest.AuthenticationResponseAndConversationContext;
import org.trading.ig.rest.dto.prices.getPricesV3.GetPricesV3Response;

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