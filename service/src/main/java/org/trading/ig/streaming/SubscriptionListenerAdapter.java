package org.trading.ig.streaming;

import com.lightstreamer.client.ItemUpdate;
import com.lightstreamer.client.Subscription;
import com.lightstreamer.client.SubscriptionListener;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscriptionListenerAdapter implements SubscriptionListener {

  private final String subscriptionId;
  private final BiConsumer<String, Map<String, String>> onUpdateLambda;
  public SubscriptionListenerAdapter(String subscriptionId, BiConsumer<String, Map<String, String>> onUpdateLambda) {
    this.subscriptionId = subscriptionId;
    this.onUpdateLambda = onUpdateLambda;
  }

  private static final Logger LOG = LoggerFactory
      .getLogger(SubscriptionListenerAdapter.class);
  @Override
  public void onClearSnapshot(String itemName, int itemPos) {
    LOG.debug("{} onClearSnapshot: {} {}", subscriptionId, itemName, itemPos);
  }

  @Override
  public void onCommandSecondLevelItemLostUpdates(int lostUpdates, String key) {
    LOG.warn("{} onCommandSecondLevelItemLostUpdates: {} {}", subscriptionId, key, lostUpdates);
  }

  @Override
  public void onCommandSecondLevelSubscriptionError(int code, String message, String key) {
    LOG.warn("{} onCommandSecondLevelSubscriptionError: {} {} {}", subscriptionId, code, message, key);
  }

  @Override
  public void onEndOfSnapshot(String itemName, int itemPos) {
    LOG.debug("{} onEndOfSnapshot: {} {}", subscriptionId, itemName, itemPos);
  }

  @Override
  public void onItemLostUpdates(String itemName, int itemPos, int lostUpdates) {
    LOG.warn("{} onItemLostUpdates: {} {} {}", subscriptionId, itemName, itemPos, lostUpdates);
  }

  @Override
  public void onItemUpdate(ItemUpdate itemUpdate) {
    onUpdateLambda.accept(itemUpdate.getItemName(), itemUpdate.getChangedFields());
  }

  @Override
  public void onListenEnd(Subscription subscription) {
    LOG.debug("{} onListenEnd", subscriptionId);
  }

  @Override
  public void onListenStart(Subscription subscription) {
    LOG.debug("{} onListenStart", subscriptionId);
  }

  @Override
  public void onSubscription() {
    LOG.debug("{} onSubscription", subscriptionId);
  }

  @Override
  public void onSubscriptionError(int code, String message) {
    LOG.error("{} onSubscriptionError message: {} code: {} ", subscriptionId, message, code);
  }

  @Override
  public void onUnsubscription() {
    LOG.debug("{} onUnsubscription", subscriptionId);
  }

  @Override
  public void onRealMaxFrequency(String frequency) {
    LOG.debug("{} onRealMaxFrequency: ", subscriptionId, frequency);
  }
}
