package org.trading.ig.rest;

public abstract class ConversationContext {

	private String apiKey;

	public ConversationContext(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
}
