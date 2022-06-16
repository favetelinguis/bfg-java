package org.trading.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;

@Data
@Role(Type.EVENT)
@Expires("5s")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Confirms {
  String direction;
  String epic;
  String dealReference;
  String dealId;
  String dealStatus;
  String reason;
  String status;
  String date;
}
