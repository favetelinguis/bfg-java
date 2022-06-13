package org.trading.ig.streaming;

import com.lightstreamer.client.ClientListener;
import com.lightstreamer.client.LightstreamerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientListenerAdapter implements ClientListener {

	private static final Logger LOG = LoggerFactory
			.getLogger(ClientListenerAdapter.class);

	@Override
	public void onListenEnd(LightstreamerClient client) {
		LOG.debug("onListenEnd");
	}

	@Override
	public void onListenStart(LightstreamerClient client) {
		LOG.debug("onListenStart");
	}

	@Override
	public void onServerError(int errorCode, String errorMessage) {
		LOG.error("onServerError: {}, {}", errorCode, errorMessage);
	}

	@Override
	public void onStatusChange(String status) {
		LOG.info("onStatusChange: " + status);
	}

	@Override
	public void onPropertyChange(String property) {
		LOG.debug("onPropertyChange: " + property);
	}
}
