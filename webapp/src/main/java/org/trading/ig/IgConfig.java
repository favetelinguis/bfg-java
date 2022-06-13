package org.trading.ig;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.trading.ig.rest.AuthenticationResponseAndConversationContext;
import org.trading.ig.rest.dto.session.createSessionV2.CreateSessionV2Request;

@Configuration
public class IgConfig {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public HttpClient httpClient() {
    return HttpClients.createDefault();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean
  public AuthenticationResponseAndConversationContext authenticationResponseAndConversationContext(RestAPI restAPI, IgProps props) {
    var authRequest = new CreateSessionV2Request();
    authRequest.setIdentifier(props.getIdentifier());
    authRequest.setPassword(props.getPassword());
    return restAPI.createSession(authRequest, props.getApiKey());
  }
}