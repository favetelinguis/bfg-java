package org.trading.ig.rest.dto.session.createSessionV3;

import org.trading.ig.rest.dto.session.createSessionV2.AccountType;
import lombok.Data;

@Data
public class CreateSessionV3Response {
private AccountType accountType;
    private String clientId;
    private String accountId;
    private int timezoneOffset;
    private String lightstreamerEndpoint;
    private AccessTokenResponse oauthToken;
}
