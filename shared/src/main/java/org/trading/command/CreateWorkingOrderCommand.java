package org.trading.command;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.trading.model.MarketInfo;

@Data
@Slf4j
public class CreateWorkingOrderCommand implements Command {
  private final static DateTimeFormatter goodTillDateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.of("UTC"));
  private String epic;
  private String direction;
  private Double level;
  private Double targetDistance;
  private Double stopDistance;
  private String goodTillDate;
  private String expiry;
  private Double size;
  private String currencyCode;

  public static Command from(String direction, MarketInfo marketInfo, ZonedDateTime goodTillDate, Double targetDistance, Double stopDistance,
      Double positionSize, Double wantedEntryLevel) {
    var command = new CreateWorkingOrderCommand();
    command.setEpic(marketInfo.getEpic());
    command.setLevel(wantedEntryLevel);
    command.setStopDistance(stopDistance);
    command.setTargetDistance(targetDistance);
    command.setSize(positionSize);
    command.setExpiry(marketInfo.getExpiry());
    command.setCurrencyCode(marketInfo.getCurrency());
    command.setDirection(direction);
    command.setGoodTillDate(goodTillDate.withZoneSameInstant(ZoneId.of("UTC")).format(goodTillDateFormatter));
    return command;
  }
}
