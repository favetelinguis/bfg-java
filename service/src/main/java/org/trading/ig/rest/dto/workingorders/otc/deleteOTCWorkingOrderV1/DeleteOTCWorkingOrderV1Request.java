package org.trading.ig.rest.dto.workingorders.otc.deleteOTCWorkingOrderV1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
Delete working order request.
 N.B.: This request requires a payload with no request attributes (e.g. empty json string:  {} )
*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeleteOTCWorkingOrderV1Request {

}
