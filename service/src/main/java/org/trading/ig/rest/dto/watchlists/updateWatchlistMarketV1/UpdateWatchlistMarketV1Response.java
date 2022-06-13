package org.trading.ig.rest.dto.watchlists.updateWatchlistMarketV1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
Add instrument to watchlist response
*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateWatchlistMarketV1Response {

/*
Status
*/
private Status status;

public Status getStatus() { return status; }
public void setStatus(Status status) { this.status=status; }
}
