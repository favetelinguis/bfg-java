package org.trading.ig;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class IgSessionCreationExceptionFailureAnalyzer extends AbstractFailureAnalyzer<IgSessionCreationException> {

  @Override
  protected FailureAnalysis analyze(Throwable rootFailure, IgSessionCreationException cause) {
    return new FailureAnalysis("Unable to create a session with IG: " + cause.getCause(),
        "Validate the URL and ensure it is accessible", cause);
  }
}