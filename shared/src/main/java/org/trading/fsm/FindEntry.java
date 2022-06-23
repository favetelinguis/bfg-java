package org.trading.fsm;

import java.util.stream.Stream;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.trading.command.CreateWorkingOrderCommand;
import org.trading.event.AtrEvent;
import org.trading.event.Confirms;
import org.trading.event.MarketClose;
import org.trading.event.MidPriceEvent;
import org.trading.event.Opu;
import org.trading.event.SystemData;
import org.trading.model.Order;

@Slf4j
@Data
public class FindEntry implements SystemState {

  @Override
  public void handleMidPriceEvent(SystemData s, MidPriceEvent event) {
    if (s.getCurrentMidPrice().isOver(s.getOpeningRange(), s.getCurrentAtr())) {
      maybeOpenOrder("BUY_HIGH", "BUY", s);
    } else if (s.getCurrentMidPrice().isInside(s.getOpeningRange(), s.getCurrentAtr())) {
      // TODO could be that we take BUY_LOW but cant afford SELL_HIGH
      maybeOpenOrder("BUY_LOW", "BUY", s);
      maybeOpenOrder("SELL_HIGH", "SELL", s);
    } else if (s.getCurrentMidPrice().isUnder(s.getOpeningRange(), s.getCurrentAtr())) {
      maybeOpenOrder("SELL_LOW", "SELL", s);
    }
  }

  private void maybeOpenOrder(String entryType, String direction, SystemData s) {
//    Stream.of(s.getCurrentAtr().positionSize(s.getMarketInfo(), s.getCurrentAccountEquity()))
    Stream.of(s.getMarketInfo().getLotSize())
        .filter(size -> {
          var nonZeroSize = size >= s.getMarketInfo().getLotSize();
          if (!nonZeroSize) {
            log.warn("Order skipped, size not larger then market min size");
          }
          return nonZeroSize;
        })
        .filter(size -> {
          var hasEnoughMargin = s.currentAccountEquity.hasEnoughMarginLeft(size, s.getCurrentMidPrice(), s.getMarketInfo());
          if (!hasEnoughMargin) {
            log.warn("Order skipped due to not enough margin left");
          }
          return hasEnoughMargin;
        })
        .filter(size -> {
          var isStopLargeEnough = s.getCurrentAtr().stopDistance() >= s.getMarketInfo().getMinStop();
          if (!isStopLargeEnough) {
            log.warn("Order skipped due to stop is to small");
          }
          return isStopLargeEnough;
        })
        .filter(size -> {
          var oneRValueAccount = s.getSystemProperties().percentageRiskPerOrder * s.getCurrentAccountEquity().getEquity();
          var riskOfTrade = size * s.getCurrentAtr().stopDistance() * s.getMarketInfo().getValueOfOnePip();
          var isRiskSmallEnough = riskOfTrade <= oneRValueAccount;
          if (!isRiskSmallEnough) {
            log.warn("Order skipped since it would risk more then {} of account", s.getSystemProperties().percentageRiskPerOrder);
          }
          return isRiskSmallEnough;
        })
        .forEach(size -> {
          s.setState(new AwaitCreateWorkingOrder());
          var wantedEntryLevel = s.getOpeningRange()
              .getWantedEntryLevel(entryType, s.getCurrentMidPrice());
          var targetDistance = s.getCurrentAtr().targetDistance();
          var stopDistance= s.getCurrentAtr().stopDistance();
          var newOrder = new Order(direction, wantedEntryLevel, size, targetDistance, stopDistance);
          s.getOrderHandler().setOrder(newOrder);
          s.getCommandExecutor().accept(CreateWorkingOrderCommand.from(
              newOrder.getDirection(),
              s.getMarketInfo(),
              s.getSystemProperties().getTodayMarketClose(s.getMarketInfo()),
              targetDistance,
              stopDistance,
              size,
              newOrder.getWantedEntryPrice()
          ));
        });
  }

  @Override
  public void handleAtrEvent(SystemData s, AtrEvent event) {

  }

  @Override
  public void handleOpuEvent(SystemData s, Opu event) {

  }

  @Override
  public void handleConfirmsEvent(SystemData s, Confirms event) {

  }

}
