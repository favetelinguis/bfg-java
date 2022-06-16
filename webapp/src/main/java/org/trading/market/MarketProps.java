package org.trading.market;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;
import org.trading.model.MarketInfo;

@ConstructorBinding
@ConfigurationProperties(prefix = "market")
@Validated
@AllArgsConstructor
@Getter
public class MarketProps {
  @NotNull
  private List<MarketInfo> epics;
}
