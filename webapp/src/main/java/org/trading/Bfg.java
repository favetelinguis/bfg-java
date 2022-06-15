package org.trading;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.trading.ig.IgProps;
import org.trading.ig.RestAPI;
import org.trading.ig.rest.AuthenticationResponseAndConversationContext;
import org.trading.ig.rest.dto.session.createSessionV2.CreateSessionV2Request;

@SpringBootApplication
@EnableConfigurationProperties({IgProps.class, Market.class})
@EnableScheduling
@EnableAsync
public class Bfg {

	public static void main(String[] args) {
		SpringApplication.run(Bfg.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	// TODO Used by ig api to be able to use _method delete with body however resttempalte support this so should migrate and remove this
	@Bean
	public HttpClient httpClient() {
		return HttpClients.createDefault();
	}

	@Bean
	public AuthenticationResponseAndConversationContext authenticationResponseAndConversationContext(
			RestAPI restAPI, IgProps props) {
		var authRequest = new CreateSessionV2Request();
		authRequest.setIdentifier(props.getIdentifier());
		authRequest.setPassword(props.getPassword());
		return restAPI.createSession(authRequest, props.getApiKey());
	}

}
