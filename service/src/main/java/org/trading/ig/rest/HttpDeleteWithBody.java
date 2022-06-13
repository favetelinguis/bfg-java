package org.trading.ig.rest;


import java.net.URI;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {

	public HttpDeleteWithBody(String uri) {
		super();
		setURI(URI.create(uri));
	}

	public HttpDeleteWithBody(URI uri) {
		super();
		setURI(uri);
	}

	public HttpDeleteWithBody() {
		super();
	}

	@Override
	public String getMethod() {
		return "POST";			// POST with _method=DELETE header
	}
}
