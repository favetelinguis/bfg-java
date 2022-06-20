package org.trading.event;

import lombok.Data;
import org.kie.api.definition.type.ClassReactive;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Modifies;
import org.kie.api.definition.type.PropertyChangeSupport;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;
import org.trading.SystemProperties;
import org.trading.event.OpeningRange;
import org.trading.model.MarketInfo;

@Data
@Role(Type.EVENT)
@Expires("9h")
public class SystemData extends SystemProperties {
  private final String epic;
  private final MarketInfo marketInfo;
  private final OpeningRange openingRange;
  private Double currentMidPrice;
  private Double currentSpread;
  public Double currentAtr;

  public SystemData(String epic, MarketInfo marketInfo, OpeningRange openingRange) {
    this.epic = epic;
    this.marketInfo = marketInfo;
    this.openingRange = openingRange;
  }
}
