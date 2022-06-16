package org.trading.ig;

    import lombok.Getter;

@Getter
public class IgSessionCreationException extends RuntimeException {

  private String url;

  public IgSessionCreationException(String url) {
    this(url, null);
  }

  public IgSessionCreationException(String url, Throwable cause) {
    super("URL " + url + " is not accessible", cause);
    this.url = url;
  }
}