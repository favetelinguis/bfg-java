package org.trading.command;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.trading.event.AccountEquityEvent;
import org.trading.model.MarketInfo;
import org.trading.model.Order;

@Data
@Slf4j
public class UpdateWorkingOrderCommand implements Command {
  private final static DateTimeFormatter goodTillDateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.of("UTC"));
  private String epic;
  private Double level;
  private Double targetDistance;
  private Double stopDistance;
  private String goodTillDate;
  private Double size;
  private String dealId;

  public static Command from(String epic, Order order, MarketInfo marketInfo, AccountEquityEvent accountEquity,
      ZonedDateTime marketClose) {
    log.info("ATR ORDER {}", order.getCurrentAtr().getAtr());
    var command = new UpdateWorkingOrderCommand();
    command.setEpic(epic);
    command.setLevel(order.getWantedEntryPrice());
    command.setStopDistance(order.getCurrentAtr().stopDistance());
    command.setTargetDistance(order.getCurrentAtr().targetDistance());
    command.setSize(order.getCurrentAtr().positionSize(marketInfo, accountEquity));
    command.setGoodTillDate(marketClose.withZoneSameInstant(ZoneId.of("UTC")).format(goodTillDateFormatter));
    command.setDealId(order.getDealId());
    return command;
  }
}
