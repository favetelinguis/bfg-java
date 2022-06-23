package org.trading;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.trading.ig.IgProps;
import org.trading.ig.IgSessionCreationException;
import org.trading.ig.RestAPI;
import org.trading.ig.rest.AuthenticationResponseAndConversationContext;
import org.trading.ig.rest.dto.session.createSessionV2.CreateSessionV2Request;
import org.trading.market.MarketProps;

@SpringBootApplication
@EnableConfigurationProperties({IgProps.class, MarketProps.class})
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
		try {
			return restAPI.createSession(authRequest, props.getApiKey());
		} catch (Exception e) {
			throw new IgSessionCreationException("IG", e);
		}
	}

	@Bean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		var threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(8);
		threadPoolTaskScheduler.setThreadNamePrefix("marketScheduler");
		return threadPoolTaskScheduler;
	}
}
