package org.trading.ig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PreDestroy;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.DecimalNum;
import org.trading.market.CompleteCandle;
import org.trading.market.MarketProps;
import org.trading.event.AccountEquity;
import org.trading.event.Confirms;
import org.trading.event.MidPrice;
import org.trading.event.Opu;
import org.trading.ig.rest.AuthenticationResponseAndConversationContext;
import org.trading.ig.rest.ConversationContextV2;
import org.trading.model.MarketInfo;

@Service
public class IgStreamService {

  private static Logger LOG = LoggerFactory.getLogger(IgStreamService.class);

  private final StreamingAPI streamingAPI;
  private AuthenticationResponseAndConversationContext authContext;
  private final ApplicationEventPublisher publisher;
  private final ObjectMapper objectMapper;
  private final List<String> subscriptionsIds = new ArrayList<>();
  private final String[] epics;
  private final MarketCache candleCache = new MarketCache();

  @Autowired
  public IgStreamService(StreamingAPI streamingAPI,
      AuthenticationResponseAndConversationContext authContext, MarketProps epics,
      ApplicationEventPublisher publisher, ObjectMapper objectMapper) {
    this.streamingAPI = streamingAPI;
    this.epics = epics.getEpics().stream().map(MarketInfo::getEpic).toArray(String[]::new);
    this.authContext = authContext;
    this.publisher = publisher;
    this.objectMapper = objectMapper;
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
    LOG.info("Unsubscribing and disconnecting from IG Stream");
    for (var id : subscriptionsIds) {
      streamingAPI.unsubscribe(id);
    }
    streamingAPI.disconnect();
  }

  private void subscribeToAccount() {
    subscriptionsIds.add(
        streamingAPI.subscribeForAccountBalanceInfo(authContext.getAccountId(), (item, update) -> {
          var account = item.split(":")[1];
          var equity = update.get("EQUITY");
          if (equity != null) {
            publisher.publishEvent(new AccountEquity(account, Double.parseDouble(equity)));
          }
        }));
  }

  private void subscribeToConfirms() {
    subscriptionsIds.add(
        streamingAPI.subscribeForConfirms(authContext.getAccountId(), (item, update) -> {
          var rawConfirms = update.get("CONFIRMS");
          if (rawConfirms != null) {
            try {
              var confirms = objectMapper.readValue(rawConfirms, Confirms.class);
              if (!isOld(confirms)) {
                publisher.publishEvent(confirms);
              }
            } catch (JsonProcessingException | ParseException e) {
              LOG.error("Failed to parse Confirms", e);
            }
          }
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

  private void subscribeToOPU() {
    subscriptionsIds.add(
        streamingAPI.subscribeForOPUs(authContext.getAccountId(), (item, update) -> {
          var rawOpu = update.get("OPU");
          if (rawOpu != null) {
            try {
              var opu = objectMapper.readValue(rawOpu, Opu.class);
              publisher.publishEvent(opu);
            } catch (JsonProcessingException e) {
              LOG.error("Failed to parse OPU", e);
            }
          }
        }));
  }

  private void subscribeToWOU() {
    subscriptionsIds.add(
        streamingAPI.subscribeForWOUs(authContext.getAccountId(), (item, update) -> {
          var rawWou = update.get("OPU");
          if (rawWou != null) {
            LOG.warn("WOU never get anything here but did now: {} {}", item, update.toString());
          }
        }));
  }

  private void subscribeToMarkets() {
    subscriptionsIds.add(streamingAPI.subscribeForMarkets(epics, (item, update) -> {
      // TODO add market state and delay as input to drool to control system if market state changes or data is delayed
    }));
  }

  private void subscribeToCandles() {
    subscriptionsIds.add(streamingAPI.subscribeForChartCandles(epics, "1MINUTE", (item, update) -> {
//      LOG.info(update.toString());
      var epic = item.split(":")[1];
      // Update candle for epic
      var maybeCompletedCandle = candleCache.update(epic, update);
      if (maybeCompletedCandle.isPresent()) {
        publisher.publishEvent(maybeCompletedCandle.get());
      }
      // Publish price change
      if (update.containsKey("OFR_CLOSE") && update.containsKey("BID_CLOSE")) {
        var ofrClose = Double.parseDouble(update.get("OFR_CLOSE"));
        var bidClose = Double.parseDouble(update.get("BID_CLOSE"));
        publisher.publishEvent(new MidPrice(epic, (ofrClose + bidClose) / 2));
      }
    }));
  }

  class MarketCache {

    private final ConcurrentHashMap<String, Candle> candleMap = new ConcurrentHashMap<>();

    // Update candle and return a candle if this is a completed candle
    // TODO this is probably not enough thread safe
    Optional<CompleteCandle> update(String epic, Map<String, String> newCandle) {
      candleMap.putIfAbsent(epic, new Candle(epic));
      var oldCandle = candleMap.get(epic);
      return oldCandle.mergeAndReturnIfComplete(newCandle);
    }
  }

    @Data
    private class Candle {
      private final String epic;
      private Double askOpen;
      private Double askHigh;
      private Double askLow;
      private Double askClose;
      private Double bidOpen;
      private Double bidHigh;
      private Double bidLow;
      private Double bidClose;
      private Long numberTicks;
      private Instant updateTime;

      public Candle(String epic) {
        this.epic = epic;
      }

      CompleteCandle getCompleteCandle() {
        var bar = BaseBar.builder(DecimalNum::valueOf, Number.class)
            .timePeriod(Duration.ofMinutes(1))
            .endTime(this.getUpdateTime().atZone(ZoneId.of("Europe/Stockholm")).plusMinutes(1))
            .openPrice((this.getAskOpen() + this.getBidOpen()) / 2)
            .highPrice((this.getAskHigh() + this.getBidHigh()) / 2)
            .lowPrice((this.getAskLow() + this.getBidLow()) / 2)
            .closePrice((this.getAskClose() + this.getBidClose()) / 2)
            .volume(this.getNumberTicks())
            .build();
        return new CompleteCandle(epic, bar);
      }

      public synchronized Optional<CompleteCandle> mergeAndReturnIfComplete(Map<String, String> newCandle) {
        for (Map.Entry<String, String> entry : newCandle.entrySet()) {
          switch (entry.getKey()) {
            case "OFR_OPEN":
              this.setAskOpen(Double.parseDouble(entry.getValue()));
              break;
            case "OFR_HIGH":
              this.setAskHigh(Double.parseDouble(entry.getValue()));
              break;
            case "OFR_LOW":
              this.setAskLow(Double.parseDouble(entry.getValue()));
              break;
            case "OFR_CLOSE":
              this.setAskClose(Double.parseDouble(entry.getValue()));
              break;
            case "BID_OPEN":
              this.setBidOpen(Double.parseDouble(entry.getValue()));
              break;
            case "BID_HIGH":
              this.setBidHigh(Double.parseDouble(entry.getValue()));
              break;
            case "BID_LOW":
              this.setBidLow(Double.parseDouble(entry.getValue()));
              break;
            case "BID_CLOSE":
              this.setBidClose(Double.parseDouble(entry.getValue()));
              break;
            case "CONS_TICK_COUNT":
              this.setNumberTicks(Long.parseLong(entry.getValue()));
              break;
            case "UTM":
              this.setUpdateTime(Instant.ofEpochMilli(Long.parseLong(entry.getValue())));
              break;
            default:
              break;
          }
      }
        var maybeHasConsUpdate = newCandle.get("CONS_END") ;
        if (maybeHasConsUpdate != null && maybeHasConsUpdate.equals("1")) {
          return Optional.of(getCompleteCandle());
        }
        return Optional.empty();
    }
  }
}

