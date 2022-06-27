package org.trading.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IndicatorState {
  private Double atr;
  private Double diPlus;
  private Double diMinus;
  private Double adx;
}
