package com.portal.rms.service;

import javax.validation.constraints.NotNull;

import com.portal.common.models.GenericApiResponse;
import com.portal.common.models.GenericRequestHeaders;
import com.portal.rms.model.CreateOrder;
import com.portal.rms.model.OtpModel;
import com.portal.rms.model.RmsDashboardFilter;
import com.portal.rms.model.RmsModel;
import com.portal.rms.model.RmsPaymentLinkModel;
import com.portal.rms.model.RmsRateModel;
import com.portal.security.model.LoggedUser;

public interface RmsService {

	public GenericApiResponse getDashboardCounts(LoggedUser loggedUser, RmsDashboardFilter payload);

	public GenericApiResponse getRmsClassifiedList(LoggedUser loggedUser,RmsDashboardFilter payload);
	
	public GenericApiResponse getRmsClassifiedsByAdId(LoggedUser loggedUser, @NotNull String adId);

	public GenericApiResponse addRmsClassifiedItemToCart(CreateOrder payload, LoggedUser loggedUser);

	public GenericApiResponse getCustomerDetails(String clientCode,String customerName);

	public GenericApiResponse getRmsRates(RmsRateModel payload);

	public GenericApiResponse genrateOTP(OtpModel payload);

	public GenericApiResponse validateOTP(OtpModel payload);

	public GenericApiResponse syncronizeRmsSAPData(GenericRequestHeaders requestHeaders, @NotNull RmsModel payload);

	public GenericApiResponse generateSendPaymentLink(RmsPaymentLinkModel payload);
}
