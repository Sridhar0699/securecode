package com.portal.clf.service;

import javax.validation.constraints.NotNull;

import com.portal.clf.models.BillDeskPaymentResponseModel;
import com.portal.clf.models.CartDetails;
import com.portal.common.models.GenericApiResponse;
import com.portal.security.model.LoggedUser;

public interface PaymentService {

	public GenericApiResponse updatePaymentResponse(BillDeskPaymentResponseModel payload, LoggedUser loggedUser);
	public void updatePendingPaymentStatuses();
	public GenericApiResponse prepareRequest(@NotNull CartDetails payload, LoggedUser loggedUser);
	public void saveEncodedRequest(String encToken, String orderId, String portalOrderId, LoggedUser loggedUser, Integer orderType);
}
