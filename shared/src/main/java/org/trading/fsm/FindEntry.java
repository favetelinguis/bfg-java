package org.trading.fsm;

import org.trading.command.CreateWorkingOrderCommand;
import org.trading.event.AtrEvent;
import org.trading.event.Confirms;
import org.trading.event.MarketClose;
import org.trading.event.MidPriceEvent;
import org.trading.event.Opu;
import org.trading.event.SystemData;
import org.trading.model.Order;

public class FindEntry implements SystemState {

  @Override
  public void handleMidPriceEvent(SystemData s, MidPriceEvent event) {
    if (s.getCurrentMidPrice().isOver(s.getOpeningRange(), s.getCurrentAtr())) {
      var wantedEntryLevel = s.getOpeningRange()
          .getWantedEntryLevel("SELL_HIGH", s.getCurrentMidPrice());
      var buyOrder = new Order("BUY", wantedEntryLevel, s.getCurrentAtr());
      s.getOrderHandler().setOrder(buyOrder);
      s.getCommandExecutor().accept(CreateWorkingOrderCommand.from(
              buyOrder.getDirection(),
              s.getMarketInfo(),
              s.getTodayMarketClose(s.getMarketInfo()),
              buyOrder.getCurrentAtr().targetDistance(),
              buyOrder.getCurrentAtr().stopDistance(),
              buyOrder.getCurrentAtr().positionSize(s.getMarketInfo(), s.getCurrentAccountEquity()),
              buyOrder.getWantedEntryPrice()
          )
      );
      s.setState(new AwaitCreateWorkingOrder());
    } else if (s.getCurrentMidPrice().isInside(s.getOpeningRange(), s.getCurrentAtr())) {
      var wantedEntryLevelBuy = s.getOpeningRange()
          .getWantedEntryLevel("BUY_LOW", s.getCurrentMidPrice());
      var wantedEntryLevelSell = s.getOpeningRange()
          .getWantedEntryLevel("SELL_HIGH", s.getCurrentMidPrice());
      var buyOrder = new Order("BUY", wantedEntryLevelBuy, s.getCurrentAtr());
      var sellOrder = new Order("SELL", wantedEntryLevelSell, s.getCurrentAtr());
      s.getOrderHandler().setOrder(buyOrder);
      s.getOrderHandler().setOrder(sellOrder);
      s.getCommandExecutor().accept(CreateWorkingOrderCommand.from(
              buyOrder.getDirection(),
              s.getMarketInfo(),
              s.getTodayMarketClose(s.getMarketInfo()),
              buyOrder.getCurrentAtr().targetDistance(),
              buyOrder.getCurrentAtr().stopDistance(),
              buyOrder.getCurrentAtr().positionSize(s.getMarketInfo(), s.getCurrentAccountEquity()),
              buyOrder.getWantedEntryPrice()
          )
      );
      s.getCommandExecutor().accept(CreateWorkingOrderCommand.from(
          sellOrder.getDirection(),
          s.getMarketInfo(),
          s.getTodayMarketClose(s.getMarketInfo()),
          sellOrder.getCurrentAtr().targetDistance(),
          sellOrder.getCurrentAtr().stopDistance(),
          sellOrder.getCurrentAtr().positionSize(s.getMarketInfo(), s.getCurrentAccountEquity()),
          sellOrder.getWantedEntryPrice()
      ));
      s.setState(new AwaitCreateWorkingOrder());
    } else if (s.getCurrentMidPrice().isUnder(s.getOpeningRange(), s.getCurrentAtr())) {
      var wantedEntryLevel = s.getOpeningRange()
          .getWantedEntryLevel("SELL_LOW", s.getCurrentMidPrice());
      var sellOrder = new Order("SELL", wantedEntryLevel, s.getCurrentAtr());
      s.getOrderHandler().setOrder(sellOrder);
      s.getCommandExecutor().accept(CreateWorkingOrderCommand.from(
          sellOrder.getDirection(),
          s.getMarketInfo(),
          s.getTodayMarketClose(s.getMarketInfo()),
          sellOrder.getCurrentAtr().targetDistance(),
          sellOrder.getCurrentAtr().stopDistance(),
          sellOrder.getCurrentAtr().positionSize(s.getMarketInfo(), s.getCurrentAccountEquity()),
          sellOrder.getWantedEntryPrice()
      ));
      s.setState(new AwaitCreateWorkingOrder());
    }
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

  @Override
  public void handleMarketClose(SystemData s, MarketClose event) {

  }
}
