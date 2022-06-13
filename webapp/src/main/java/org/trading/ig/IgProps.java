package org.trading.ig;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix="ig")
@Validated
@Data
public class IgProps {
  @NotBlank
  private String identifier;
  @NotBlank
  private String password;
  @NotBlank
  private String apiKey;
  @NotBlank
  private String baseUrl;
  private List<String> epics;
}
