package org.trading.ig.rest;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponseAndConversationContext {

	private ConversationContext conversationContext;
	private String accountId;
	private String lightstreamerEndpoint;
}
