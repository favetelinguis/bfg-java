package org.trading.ig;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@ConstructorBinding
@ConfigurationProperties(prefix="ig")
@Validated
@AllArgsConstructor
@Getter
public class IgProps {
  @NotBlank
  private String identifier;
  @NotBlank
  private String password;
  @NotBlank
  private String apiKey;
  @NotBlank
  private String baseUrl;
}
