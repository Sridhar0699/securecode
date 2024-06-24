package com.portal.common.service;

import com.portal.clf.models.PaymentGatewayDetails;
import com.portal.common.models.GenericRequestHeaders;

public interface CommonService {
	
	public GenericRequestHeaders getRequestHeaders();
	
	public String getReqHeader(String headerName);
	
	public PaymentGatewayDetails populatePaymentGatewayDetails(String env);
	
}
