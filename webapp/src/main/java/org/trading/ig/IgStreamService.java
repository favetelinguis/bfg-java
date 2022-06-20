package org.trading.ig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.trading.event.AccountEquityEvent;
import org.trading.market.MarketProps;
import org.trading.event.Confirms;
import org.trading.event.Opu;
import org.trading.ig.rest.AuthenticationResponseAndConversationContext;
import org.trading.ig.rest.ConversationContextV2;
import org.trading.market.data.BarUpdate;
import org.trading.model.MarketInfo;
import org.trading.repository.IgStreamRepository;
import org.trading.repository.IgStreamEntity;

@Service
@Slf4j
public class IgStreamService {

  private final StreamingAPI streamingAPI;
  private AuthenticationResponseAndConversationContext authContext;
  private final ApplicationEventPublisher publisher;
  private final ObjectMapper objectMapper;
  private final IgStreamRepository igStreamRepository;
  private final List<String> subscriptionsIds = new ArrayList<>();
  private final String[] epics;

  @Autowired
  public IgStreamService(StreamingAPI streamingAPI,
      AuthenticationResponseAndConversationContext authContext, MarketProps epics,
      ApplicationEventPublisher publisher, ObjectMapper objectMapper, IgStreamRepository igStreamRepository) {
    this.streamingAPI = streamingAPI;
    this.epics = epics.getEpics().stream().map(MarketInfo::getEpic).toArray(String[]::new);
    this.authContext = authContext;
    this.publisher = publisher;
    this.objectMapper = objectMapper;
    this.igStreamRepository = igStreamRepository;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void run() {
    streamingAPI.connect(authContext.getAccountId(),
        (ConversationContextV2) authContext.getConversationContext(),
        authContext.getLightstreamerEndpoint());
    subscribeToAccount();
    subscribeToConfirms();
    subscribeToOPU();
    subscribeToWOU();
    subscribeToMarkets();
    subscribeToCandles();
  }

  @PreDestroy
  public void destroy() {
    log.info("Unsubscribing and disconnecting from IG Stream");
    for (var id : subscriptionsIds) {
      streamingAPI.unsubscribe(id);
    }
    streamingAPI.disconnect();
  }

  private void subscribeToAccount() {
    subscriptionsIds.add(
        streamingAPI.subscribeForAccountBalanceInfo(authContext.getAccountId(), (item, update) -> {
          var account = item.split(":")[1];
          try {
            igStreamRepository.save(new IgStreamEntity(LocalDateTime.now(), account, update));
          } catch (Exception e) {
            log.error("Failure ", e);
          }
          var equity = update.get("FUNDS");
          if (equity != null) {
            publisher.publishEvent(new AccountEquityEvent(account, Double.parseDouble(equity)));
          }
        }));
  }

  private void subscribeToConfirms() {
    subscriptionsIds.add(
        streamingAPI.subscribeForConfirms(authContext.getAccountId(), (item, update) -> {
          var account = item.split(":")[1];
          igStreamRepository.save(new IgStreamEntity(LocalDateTime.now(), account, update));
          var rawConfirms = update.get("CONFIRMS");
          if (rawConfirms != null) {
            try {
              var confirms = objectMapper.readValue(rawConfirms, Confirms.class);
              if (!isOld(confirms)) {
                publisher.publishEvent(confirms);
              }
            } catch (JsonProcessingException | ParseException e) {
              log.error("Failed to parse Confirms", e);
            }
          }
        }));
  }

  private void subscribeToOPU() {
    subscriptionsIds.add(
        streamingAPI.subscribeForOPUs(authContext.getAccountId(), (item, update) -> {
          var account = item.split(":")[1];
          igStreamRepository.save(new IgStreamEntity(LocalDateTime.now(), account, update));
          var rawOpu = update.get("OPU");
          if (rawOpu != null) {
            try {
              var opu = objectMapper.readValue(rawOpu, Opu.class);
              publisher.publishEvent(opu);
            } catch (JsonProcessingException e) {
              log.error("Failed to parse OPU", e);
            }
          }
        }));
  }

  private void subscribeToWOU() {
    subscriptionsIds.add(
        streamingAPI.subscribeForWOUs(authContext.getAccountId(), (item, update) -> {
          var account = item.split(":")[1];
          igStreamRepository.save(new IgStreamEntity(LocalDateTime.now(), account, update));
          var rawWou = update.get("OPU");
          if (rawWou != null) {
            log.warn("WOU never get anything here but did now: {} {}", item, update.toString());
          }
        }));
  }

  private void subscribeToMarkets() {
    subscriptionsIds.add(streamingAPI.subscribeForMarkets(epics, (item, update) -> {
      var epic = item.split(":")[1];
      igStreamRepository.save(new IgStreamEntity(LocalDateTime.now(), epic, update));
      // TODO add market state and delay as input to drool to control system if market state changes or data is delayed
    }));
  }

  private void subscribeToCandles() {
    subscriptionsIds.add(streamingAPI.subscribeForChartCandles(epics, "1MINUTE", (item, update) -> {
      var epic = item.split(":")[1];
      igStreamRepository.save(new IgStreamEntity(LocalDateTime.now(), epic, update));
      publisher.publishEvent(new BarUpdate(epic, update));
    }));
  }

  // Check if this confirs is older then 10seconds then its assumed to be an old confirms.
  // The confirms subscripts always sends the last confirms when staring so I need to filter it here.
  // I have tried to disable snapshot in subscription but to no affect.
  private boolean isOld(Confirms confirms) throws ParseException {
    var dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    var utcNow = Instant.now();
    var confirmsUpdate = confirms.getDate().replace("T", " ");
    var utcConfirmsUpdate = dtf.parse(confirmsUpdate).toInstant();
    return utcConfirmsUpdate.plusSeconds(10).isBefore(utcNow);
  }


}

