package org.trading.ig;

import com.lightstreamer.client.ClientListener;
import com.lightstreamer.client.Subscription;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;
import org.trading.ig.rest.ConversationContextV2;
import org.trading.ig.streaming.SubscriptionListenerAdapter;
import org.trading.ig.streaming.ClientListenerAdapter;
import com.lightstreamer.client.LightstreamerClient;

@Component
public class StreamingAPI {
	private final Map<String, Subscription> subscriptionRegistry = new HashMap<>();

	private static final String TRADE_PATTERN = "TRADE:{accountId}";
	private static final String ACCOUNT_BALANCE_INFO_PATTERN = "ACCOUNT:{accountId}";
	private static final String MARKET_L1_PATTERN = "MARKET:{epic}";
	private static final String SPRINT_MARKET_PATTERN = "MARKET:{epic}";
	private static final String CHART_TICK_PATTERN = "CHART:{epic}:TICK";
	private static final String CHART_CANDLE_PATTERN = "CHART:{epic}:{scale}";

	private LightstreamerClient lsClient;

	public void connect(String account, ConversationContextV2 conversationContextV2,
												 String lightstreamerEndpoint) {

		lsClient = new LightstreamerClient(lightstreamerEndpoint, null);
		lsClient.connectionDetails.setUser(account);
		lsClient.connectionDetails.setPassword("CST-" + conversationContextV2.getClientSecurityToken() + "|XST-" + conversationContextV2.getAccountSecurityToken());
		final ClientListener adapter = new ClientListenerAdapter();
		lsClient.addListener(adapter);
		lsClient.connect();
	}

	public void disconnect() {
		if(lsClient != null) {
			lsClient.disconnect();
		}
	}

	public void unsubscribe(String subscriptionId) {
		var subscription = subscriptionRegistry.remove(subscriptionId);
		if(lsClient != null && subscription != null) {
			lsClient.unsubscribe(subscription);
		}
	}

	public String subscribeForConfirms(String accountId, BiConsumer<String, Map<String, String>> onUpdateLambda) {
		var subscriptionKey = "CONFIRMS";
		String tradeKey = TRADE_PATTERN.replace("{accountId}", accountId);
		var subscription = new Subscription("DISTINCT", new String[]{tradeKey}, new String[]{subscriptionKey});
		subscription.addListener(new SubscriptionListenerAdapter(subscriptionKey, onUpdateLambda));
		lsClient.subscribe(subscription);
		var replacedSubscription = subscriptionRegistry.put(subscriptionKey, subscription);
		if (replacedSubscription!= null) {
			lsClient.unsubscribe(replacedSubscription);
		}
		return subscriptionKey;
	}

	public String subscribeForAccountBalanceInfo(String accountId, BiConsumer<String, Map<String, String>> onUpdateLambda) {
		String subscriptionKey = ACCOUNT_BALANCE_INFO_PATTERN.replace(
				"{accountId}", accountId);
		var subscription = new Subscription("MERGE", new String[]{subscriptionKey}, new String[]{"PNL", "AVAILABLE_CASH", "FUNDS", "MARGIN", "EQUITY_USED"});
		subscription.addListener(new SubscriptionListenerAdapter(subscriptionKey, onUpdateLambda));
		lsClient.subscribe(subscription);
		var replacedSubscription = subscriptionRegistry.put(subscriptionKey, subscription);
		if (replacedSubscription != null) {
			lsClient.unsubscribe(replacedSubscription);
		}
		return subscriptionKey;
	}

	public String subscribeForMarkets(String[] epics, BiConsumer<String, Map<String, String>> onUpdateLambda) {
		var subscriptionKey = "MARKETS";
		var items = Arrays.stream(epics)
				.map(epic ->MARKET_L1_PATTERN.replace("{epic}", epic))
				.toArray(String[]::new);

		var subscription = new Subscription("MERGE", items, new String[]{"MARKET_STATE", "MARKET_DELAY", "UPDATE_TIME"});
		subscription.addListener(new SubscriptionListenerAdapter(subscriptionKey, onUpdateLambda));
		lsClient.subscribe(subscription);
		var replacedSubscription = subscriptionRegistry.put(subscriptionKey, subscription);
		if (replacedSubscription != null) {
			lsClient.unsubscribe(replacedSubscription);
		}
		return subscriptionKey;
	}

	public String subscribeForOPUs(String accountId, BiConsumer<String, Map<String, String>> onUpdateLambda) {
		String subscriptionKey = "OPU";
		String tradeKey = TRADE_PATTERN.replace("{accountId}", accountId);

		var subscription = new Subscription("DISTINCT", new String[]{tradeKey}, new String[]{"OPU"});
		subscription.addListener(new SubscriptionListenerAdapter(subscriptionKey, onUpdateLambda));
		lsClient.subscribe(subscription);
		var replacedSubscription = subscriptionRegistry.put(subscriptionKey, subscription);
		if (replacedSubscription != null) {
			lsClient.unsubscribe(replacedSubscription);
		}
		return subscriptionKey;
	}

	public String subscribeForWOUs(String accountId, BiConsumer<String, Map<String, String>> onUpdateLambda) {
		String subscriptionKey = "WOU";
		String tradeKey = TRADE_PATTERN.replace("{accountId}", accountId);

		var subscription = new Subscription("DISTINCT", new String[]{tradeKey}, new String[]{subscriptionKey});
		subscription.addListener(new SubscriptionListenerAdapter(subscriptionKey, onUpdateLambda));
		lsClient.subscribe(subscription);
		var replacedSubscription = subscriptionRegistry.put(subscriptionKey, subscription);
		if (replacedSubscription != null) {
			lsClient.unsubscribe(replacedSubscription);
		}
		return subscriptionKey;
	}

	public String subscribeForChartCandles(String[] epics,
			String scale, BiConsumer<String, Map<String, String>> onUpdateLambda) {
		String subscriptionKey = "CANDLE";
		var items = Arrays.stream(epics)
				.map(epic -> CHART_CANDLE_PATTERN.replace("{epic}", epic).replace("{scale}", scale))
				.toArray(String[]::new);
		var fields = new String[]{"OFR_OPEN", "OFR_HIGH", "OFR_LOW", "OFR_CLOSE", "BID_OPEN", "BID_HIGH", "BID_LOW", "BID_CLOSE", "CONS_END", "UTM", "CONS_TICK_COUNT"};
		var subscription = new Subscription("MERGE", items,fields);
		subscription.addListener(new SubscriptionListenerAdapter(subscriptionKey, onUpdateLambda));
		lsClient.subscribe(subscription);
		var replacedSubscription = subscriptionRegistry.put(subscriptionKey, subscription);
		if (replacedSubscription != null) {
			lsClient.unsubscribe(replacedSubscription);
		}
		return subscriptionKey;
	}

}
