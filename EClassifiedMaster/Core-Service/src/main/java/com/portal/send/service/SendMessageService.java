package com.portal.send.service;

import java.util.Map;

import com.portal.send.models.EmailsTo;

public interface SendMessageService {

	/**
	 * Send email communication
	 * 
	 * @param emailUtilsTo
	 * @param emailConfigs
	 */
	public void sendCommunicationMail(EmailsTo emailTo, Map<String, String> emailConfigs);

	public Map<String, String> sendSms(String otp, String mobileNo);

}
