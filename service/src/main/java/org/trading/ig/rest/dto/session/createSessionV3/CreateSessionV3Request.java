package org.trading.ig.rest.dto.session.createSessionV3;

import lombok.Data;

@Data
public class CreateSessionV3Request {
    private String identifier;
    private String password;
}
