package com.portal.clf.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import com.portal.clf.entities.ClfPaymentResponseTracking;
import com.portal.clf.models.BillDeskPaymentResponseModel;
import com.portal.clf.models.CartDetails;
import com.portal.clf.models.ErpClassifieds;
import com.portal.clf.models.PaymentGatewayDetails;
import com.portal.common.models.GenericApiResponse;
import com.portal.common.service.CommonService;
import com.portal.constants.GeneralConstants;
import com.portal.gd.dao.GlobalDataDao;
import com.portal.repository.ClfOrdersRepo;
import com.portal.repository.ClfPaymentResponseTrackingRepo;
import com.portal.security.model.LoggedUser;
import com.portal.send.models.EmailsTo;
import com.portal.send.service.SendMessageService;
import com.portal.setting.dao.SettingDao;
import com.portal.setting.to.SettingTo;
import com.portal.setting.to.SettingTo.SettingType;
import com.portal.user.dao.UserDao;
import com.portal.user.entities.UmOrgUsers;
import com.portal.utils.JWTUtils;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;


@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private ClfPaymentResponseTrackingRepo clfPaymentResponseTrackingRepo;
	
	@Autowired
	private ClfOrdersRepo clfOrdersRepo;
	
	@Autowired
	private ClassifiedService classifiedService;
	
	@Autowired	
	private CommonService commonService;
	
	@Autowired(required = true)
	private SettingDao settingDao;
	
	@Autowired(required = true)
	private SendMessageService sendService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private GlobalDataDao globalDataDao;
	
	@Autowired
	private JWTUtils jWTUtils;
	
	@Autowired
	private Environment prop;
	
	@Override
	public GenericApiResponse updatePaymentResponse(BillDeskPaymentResponseModel payload, LoggedUser loggedUser) {
		GenericApiResponse genericApiResponse = new GenericApiResponse();
		genericApiResponse.setStatus(0);
		genericApiResponse.setMessage(GeneralConstants.SUCCESS);
		try {
			if (payload.getTransaction_response() != null && !payload.getTransaction_response().isEmpty()) {
				//verify encToken
				PaymentGatewayDetails paymentGatewayDetails = commonService.populatePaymentGatewayDetails("PRD");
				if(paymentGatewayDetails.getClientId() != null && paymentGatewayDetails.getClientSecret() != null && !paymentGatewayDetails.getClientId().isEmpty() && !paymentGatewayDetails.getClientSecret().isEmpty()){
					if(!jWTUtils.verifyJwtToken(payload.getTransaction_response(), paymentGatewayDetails.getClientSecret()))
					{
						genericApiResponse.setStatus(1);
						genericApiResponse.setMessage("Signature verification failed");
						return genericApiResponse;
					}
				}
				String[] split_string = payload.getTransaction_response().split("\\.");
				if (split_string.length > 1) {
					String base64EncodedBody = split_string[1];

					JSONObject decodedRes = new JSONObject(
							new String(Base64Utils.decode(base64EncodedBody.getBytes())));
//					ClfPaymentResponseTracking clfPaymentResponseTracking = new ClfPaymentResponseTracking();
					ClfPaymentResponseTracking clfPaymentResponseTracking = clfPaymentResponseTrackingRepo
							.getPaymentsByOrderId(decodedRes.getString("orderid"));
					payload.setClfPaymentResponseTracking(clfPaymentResponseTracking);
					if(payload.getClfPaymentResponseTracking() != null){
						BeanUtils.copyProperties(payload.getClfPaymentResponseTracking(), clfPaymentResponseTracking);
					} else {
						clfPaymentResponseTracking = new ClfPaymentResponseTracking();
						clfPaymentResponseTracking.setId(UUID.randomUUID().toString());
					}
					clfPaymentResponseTracking.setSecOrderId(payload.getSecOrderId());
					clfPaymentResponseTracking.setOrderId(payload.getOrderid());
					clfPaymentResponseTracking.setAmount(decodedRes.getString("amount"));
					clfPaymentResponseTracking.setBankRefNo(decodedRes.getString("bank_ref_no"));
					clfPaymentResponseTracking.setGateWayId(decodedRes.getString("bank_ref_no"));
					clfPaymentResponseTracking.setPaymentMethodType(decodedRes.getString("payment_method_type"));
					clfPaymentResponseTracking.setPaymentStatus(decodedRes.getString("transaction_error_type"));
					clfPaymentResponseTracking.setAuthStatus(decodedRes.getString("auth_status"));
					clfPaymentResponseTracking.setTransactionDate(decodedRes.getString("transaction_date"));
					clfPaymentResponseTracking.setTransactionId(decodedRes.getString("transactionid"));
					clfPaymentResponseTracking.setTxnProcessType(
							decodedRes.has("txn_process_type") ? decodedRes.getString("txn_process_type") : null);
					clfPaymentResponseTracking.setResponse(payload.getTransaction_response());
					clfPaymentResponseTracking.setCreatedBy(loggedUser.getUserId());
					clfPaymentResponseTracking.setCreatedTs(new Date());
					clfPaymentResponseTracking.setTransactionErrorDesc(decodedRes.getString("transaction_error_desc"));
					clfPaymentResponseTrackingRepo.save(clfPaymentResponseTracking);
					genericApiResponse.setData(clfPaymentResponseTracking);
					if("0399".equalsIgnoreCase(decodedRes.get("auth_status")+"")){
						genericApiResponse.setStatus(1);
						genericApiResponse.setMessage(decodedRes.getString("transaction_error_type"));
						return genericApiResponse;
					}
					if (clfPaymentResponseTracking.getPaymentStatus() != null
							&& !clfPaymentResponseTracking.getPaymentStatus().isEmpty()
							&& clfPaymentResponseTracking.getSecOrderId() != null
							&& !clfPaymentResponseTracking.getSecOrderId().isEmpty()
							&& "success".equalsIgnoreCase(clfPaymentResponseTracking.getPaymentStatus())) {
						List<String> orderIds = new ArrayList<>();
						orderIds.add(clfPaymentResponseTracking.getSecOrderId());
						clfOrdersRepo.updateClfOrdersPaymentStatus(
								"CLOSED", loggedUser.getUserId(),
								new Date(), orderIds);
						if("success".equalsIgnoreCase(clfPaymentResponseTracking.getPaymentStatus()) && clfPaymentResponseTracking.getOrderType() == 2) {
							Map<String, ErpClassifieds> erpClassifiedsMap = classifiedService.getOrderDetailsForErp(orderIds);
							sendMailToCustomer(erpClassifiedsMap,payload,loggedUser,clfPaymentResponseTracking);
						}else {
							sendPaymentDetailsToCustomer(loggedUser,decodedRes,payload.getSecOrderId());
						}
						
//						ClassifiedStatus classifiedStatus = new ClassifiedStatus();
//						classifiedStatus.setOrderId(orderIds);
//						classifiedService.syncronizeSAPData(commonService.getRequestHeaders(), classifiedStatus);
					}
				}
			}else {
				genericApiResponse.setStatus(1);
				genericApiResponse.setMessage("Error while updating payment status update");
			}
		} catch (Exception e) {
			genericApiResponse.setStatus(1);
			genericApiResponse.setMessage("Error while updating payment status update:"+e.getMessage());
		}
		return genericApiResponse;
	}

	private void sendPaymentDetailsToCustomer(LoggedUser loggedUser, JSONObject decodedRes, String orderId) {
		try {
			
			Object[] obj = clfOrdersRepo.getCustomerEmail(orderId);
			
			Map<String, Object> params = new HashMap<>();
			params.put("stype", SettingType.APP_SETTING.getValue());
			params.put("grps", Arrays.asList(GeneralConstants.EMAIL_SET_GP, GeneralConstants.ENV_SET_GP));
			SettingTo settingTo = settingDao.getSMTPSettingValues(params);
			Map<String, String> emailConfigs = settingTo.getSettings();

			Map<String, Object> mapProperties = new HashMap<String, Object>();
			EmailsTo emailTo = new EmailsTo();
			emailTo.setFrom(emailConfigs.get("EMAIL_FROM"));
			emailTo.setOrgId("1000");
//			emailTo.setTo(loggedUser.getEmail());
			mapProperties.put("action_url", emailConfigs.get("WEB_URL"));
			mapProperties.put("logo_url", emailConfigs.get("LOGO_URL"));
			mapProperties.put("userName", loggedUser.getLogonId());// created by userName
			mapProperties.put("userId", loggedUser.getLogonId());// new userName
			mapProperties.put("transactionId", decodedRes.get("transactionid"));
			mapProperties.put("orderId", decodedRes.get("orderid"));
			mapProperties.put("amount", decodedRes.get("amount"));
			mapProperties.put("authStatus", decodedRes.get("auth_status"));
			mapProperties.put("paymentStatus", decodedRes.get("transaction_error_type"));
			mapProperties.put("transactionDate", decodedRes.get("transaction_date"));
			
			if(obj.length > 0 && obj[0] != null) {
				emailTo.setTo(obj[0]+"");
				String [] ccMails = {loggedUser.getEmail()};
				emailTo.setBcc(ccMails);
			}else {
				emailTo.setTo(loggedUser.getEmail());
			}

			emailTo.setTemplateName(GeneralConstants.RMS_PAYMENT_INVOICE);
			emailTo.setTemplateProps(mapProperties);

			sendService.sendCommunicationMail(emailTo, emailConfigs);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void sendMailToCustomer(Map<String, ErpClassifieds> erpClassifiedsMap, BillDeskPaymentResponseModel payload,
			LoggedUser loggedUser, ClfPaymentResponseTracking clfPaymentResponseTracking) {
		// TODO Auto-generated method stub
		try {
			
//			List<String> adminEmail = new ArrayList<String>();
//			List<UmOrgUsers> umOrgU = userDao.getAdminAndHqUsers();
//			for(UmOrgUsers umOrg : umOrgU) {
//				if("ADMIN".equalsIgnoreCase(umOrg.getUmOrgRoles().getRoleShortId()))
//				adminEmail.add(umOrg.getUmUsers().getEmail());
//			}
			
//			String adminEmails = adminEmail.stream().map(Object::toString)
//					.collect(Collectors.joining(","));
//			String[] adminCcMails = {adminEmails};
			
			Map<String, Object> params = new HashMap<>();
			params.put("stype", SettingType.APP_SETTING.getValue());
			params.put("grps", Arrays.asList(GeneralConstants.EMAIL_SET_GP, GeneralConstants.ENV_SET_GP));
			SettingTo settingTo = settingDao.getSMTPSettingValues(params);
			Map<String, String> emailConfigs = settingTo.getSettings();
			
			Map<String, Object> mapProperties = new HashMap<String, Object>();
			EmailsTo emailTo = new EmailsTo();
			emailTo.setFrom(emailConfigs.get("EMAIL_FROM"));
//			emailTo.setBcc(adminCcMails);
			emailTo.setOrgId("1000");
			mapProperties.put("action_url",emailConfigs.get("WEB_URL"));
			mapProperties.put("logo_url", emailConfigs.get("LOGO_URL"));
			mapProperties.put("userName", loggedUser.getLogonId());//created by userName
			mapProperties.put("userId", loggedUser.getLogonId());//new userName
			if("AGENCY_USER".equalsIgnoreCase(loggedUser.getRoleName())) {
				mapProperties.put("agencyUsername", loggedUser.getFirstName());
				mapProperties.put("agencyMobileNo", loggedUser.getMobile());
				mapProperties.put("agencyEmail", loggedUser.getEmail());
				mapProperties.put("isAgencyUser", "inline-block");
				mapProperties.put("isCustomerUser", "none");
				mapProperties.put("isAgencyUserCommition", true);
			}else {
				mapProperties.put("isAgencyUser", "none");
				mapProperties.put("isCustomerUser", "inline-block");
			}
			erpClassifiedsMap.entrySet().forEach(erpData -> {
				emailTo.setTo(erpData.getValue().getEmailId());
				mapProperties.put("adId", erpData.getValue().getAdId());
				mapProperties.put("adType", erpData.getValue().getAdsType());
				mapProperties.put("adSubType", erpData.getValue().getAdsSubType());
				mapProperties.put("pack", erpData.getValue().getSchemeStr());
				mapProperties.put("lineCount", erpData.getValue().getLineCount());
				mapProperties.put("amount", erpData.getValue().getPaidAmount());
				mapProperties.put("langStr", erpData.getValue().getLangStr());
				mapProperties.put("category", erpData.getValue().getCategoryStr());
				mapProperties.put("subCategory", erpData.getValue().getSubCategoryStr());
				mapProperties.put("printAdMatter", erpData.getValue().getContent());
				mapProperties.put("approvalStatus", "PENDING");
				mapProperties.put("customerName", erpData.getValue().getCustomerName());
				mapProperties.put("customerEmail", erpData.getValue().getEmailId());
				mapProperties.put("customerMobile", erpData.getValue().getMobileNumber());
				mapProperties.put("transactionDate", clfPaymentResponseTracking.getTransactionDate());
				mapProperties.put("transactionId", clfPaymentResponseTracking.getTransactionId());
				mapProperties.put("transactionType", clfPaymentResponseTracking.getTxnProcessType());
				mapProperties.put("paymentStatus", clfPaymentResponseTracking.getPaymentStatus());
				mapProperties.put("bankRefId", clfPaymentResponseTracking.getBankRefNo());
				mapProperties.put("bookingDate", erpData.getValue().getBookingDate());
				mapProperties.put("gstAmount", erpData.getValue().getGstTotalAmount());
				mapProperties.put("rate", erpData.getValue().getRate());
				mapProperties.put("extraLineAmount", erpData.getValue().getExtraLineRateAmount());
				mapProperties.put("agencyCommition", erpData.getValue().getAgencyCommition());
				
				String [] ccMails = {loggedUser.getEmail() ,erpData.getValue().getBookingUnitEmail()};
				emailTo.setBcc(ccMails);
				
				mapProperties.put("rate", erpData.getValue().getRate() + erpData.getValue().getExtraLineRateAmount());
				
				if("AGENCY_USER".equalsIgnoreCase(loggedUser.getRoleName())) {
					Double totValue = erpData.getValue().getRate() + erpData.getValue().getExtraLineRateAmount();
					totValue = totValue - erpData.getValue().getAgencyCommition();
					totValue = totValue + erpData.getValue().getGstTotalAmount();
					Double roundingOff = erpData.getValue().getPaidAmount().doubleValue() - totValue;
					double roundedDifference = Math.round(roundingOff * 100.0) / 100.0;
					mapProperties.put("roundingOff", roundedDifference);
				} else {
					Double totValue = erpData.getValue().getRate() + erpData.getValue().getExtraLineRateAmount()
							+ erpData.getValue().getGstTotalAmount();
					Double roundingOff = erpData.getValue().getPaidAmount().doubleValue() - totValue;
					double roundedDifference = Math.round(roundingOff * 100.0) / 100.0;
					mapProperties.put("roundingOff", roundedDifference);
				}
				
				if("11".equalsIgnoreCase(erpData.getValue().getSalesOffice())) {
					mapProperties.put("isTelangana", "inline-block");
				}else {
					mapProperties.put("isTelangana", "none");
				}
				if("25".equalsIgnoreCase(erpData.getValue().getSalesOffice())) {
					mapProperties.put("isAndhraPradesh", "inline-block");
				}else {
					mapProperties.put("isAndhraPradesh", "none");
				}
				List<String> pubDates = erpData.getValue().getPublishDates();
				List<String> pubDatesList = new ArrayList<String>();
				for(String pubD : pubDates) {
					 Date date;
					 String formattedDate = null;
					try {
						date = new SimpleDateFormat("yyyyMMdd").parse(pubD);
						formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					pubDatesList.add(formattedDate);
				}
				String publishDates = pubDatesList.stream().map(Object::toString)
						.collect(Collectors.joining(","));
				mapProperties.put("pubDates", publishDates);
				List<String> editionsList = erpData.getValue().getEditions();
				String editions = editionsList.stream().map(Object::toString)
						.collect(Collectors.joining(","));
				mapProperties.put("editions", editions);
				if("AGENCY_USER".equalsIgnoreCase(loggedUser.getRoleName())) {
					emailTo.setTemplateName(GeneralConstants.PAYMENT_AGENCY);
				}else {
					emailTo.setTemplateName(GeneralConstants.PAYMENT);
				}
				emailTo.setTemplateProps(mapProperties);
				
				sendService.sendCommunicationMail(emailTo, emailConfigs);
			});	
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	public void updatePendingPaymentStatuses() {
		List<String> transactionIds = new ArrayList<>();
		Map<String, ClfPaymentResponseTracking> transactionMap = new HashMap<>();
		LoggedUser loggedUser = new LoggedUser();
		PaymentGatewayDetails paymentGatewayDetails = commonService.populatePaymentGatewayDetails("PRD");
		if (paymentGatewayDetails.getClientId() != null && !paymentGatewayDetails.getClientId().isEmpty()
				&& paymentGatewayDetails.getClientSecret() != null
				&& !paymentGatewayDetails.getClientSecret().isEmpty()) {
			Map<String, Object> header = new HashMap<>();
			header.put("alg", "HS256");
			header.put("typ", "JWT");
			header.put("clientid", paymentGatewayDetails.getClientId());
			header.put("kid", "HMAC");
			List<String> statusList = new ArrayList<>();
			statusList.add("success");
			statusList.add("payment_processing_error");
			List<Object[]> transactionList = clfPaymentResponseTrackingRepo.getPendingStatusUpdateOrders(statusList);
			JSONObject retrievePayload = new JSONObject();
			for (Object[] obj : transactionList) {
				transactionIds.add((String) obj[0]);
			}
			if (!transactionIds.isEmpty()) {
				List<ClfPaymentResponseTracking> clfPaymentResponseTrackingList = clfPaymentResponseTrackingRepo
						.getPaymentsByTransactionIds(transactionIds);
				for (ClfPaymentResponseTracking prt : clfPaymentResponseTrackingList) {
					transactionMap.put(prt.getTransactionId(), prt);
				}
			}
			for (Object[] obj : transactionList) {
				try {
					long timeStamp = new Date().getTime();
					Map<String, String> headerMap = new HashMap<>();
					headerMap.put("content-Type", "application/jose");
					headerMap.put("Bd-Timestamp", timeStamp + "");
					headerMap.put("Bd-Traceid", timeStamp + "ABD1K");
					headerMap.put("accept", "application/jose");
					retrievePayload.put("mercid", paymentGatewayDetails.getMercId());
					retrievePayload.put("transactionid", (String) obj[0]);
					String encToken = jWTUtils.generateJwtToken(retrievePayload, header,
							paymentGatewayDetails.getClientSecret());
					HttpResponse<String> res = Unirest
							.post(prop.getProperty("BILLDESK_HOST") + "" + prop.getProperty("BILLDESK_RETRIEVE"))
							.headers(headerMap).body(encToken).asString();
					BillDeskPaymentResponseModel billDeskPaymentResponseModel = new BillDeskPaymentResponseModel();
					billDeskPaymentResponseModel.setMercid(paymentGatewayDetails.getMercId());
					billDeskPaymentResponseModel.setSecOrderId((String) obj[1]);
					billDeskPaymentResponseModel.setOrderid((String) obj[2]);
					billDeskPaymentResponseModel.setTransaction_response(res.getBody());
					billDeskPaymentResponseModel.setClfPaymentResponseTracking(transactionMap.get((String) obj[0]));
					loggedUser.setUserId(((Short) obj[4]).intValue());
					updatePaymentResponse(billDeskPaymentResponseModel, loggedUser);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public GenericApiResponse prepareRequest(@NotNull CartDetails payload,LoggedUser loggedUser) {
		GenericApiResponse apiResp = new GenericApiResponse();
		JSONObject jsonObject = new JSONObject();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		JSONObject additionalInfo = new JSONObject();
		JSONObject device = new JSONObject();
		JSONObject splitPayment = new JSONObject();
		JSONArray splitPaymentArray = new JSONArray();
		Map<String, Object> header = new HashMap<>();
		try {
			PaymentGatewayDetails paymentGatewayDetails = commonService.populatePaymentGatewayDetails("PRD");
			if (paymentGatewayDetails.getClientId() != null && !paymentGatewayDetails.getClientId().isEmpty()
					&& paymentGatewayDetails.getClientSecret() != null
					&& !paymentGatewayDetails.getClientSecret().isEmpty()) {

				header.put("alg", "HS256");
				header.put("typ", "JWT");
				header.put("clientid", paymentGatewayDetails.getClientId());
				header.put("kid", "HMAC");

				Date currentDateTime = new Date();
				String formattedDate = formatter.format(currentDateTime);
				String orderId = classifiedService.generateSeries("ORDER");
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate1 = dateFormat.format(currentDateTime);

				List<Object[]> bookingUnitDetails = clfOrdersRepo.getBookingDetailsForPrepareReq(payload.getOrderDetails().getOrderId());
                
                additionalInfo.put("additional_info1", !bookingUnitDetails.isEmpty() && bookingUnitDetails.get(0) != null && bookingUnitDetails.get(0)[0] != null ? bookingUnitDetails.get(0)[0] : "NA");
                additionalInfo.put("additional_info2", !bookingUnitDetails.isEmpty() && bookingUnitDetails.get(0) != null && bookingUnitDetails.get(0)[1] != null ? bookingUnitDetails.get(0)[1] : "NA");
                additionalInfo.put("additional_info3", formattedDate1);
                additionalInfo.put("additional_info4", String.format("%.2f",payload.getOrderDetails().getGrandTotal()));
                additionalInfo.put("additional_info5", !bookingUnitDetails.isEmpty() && bookingUnitDetails.get(0) != null && bookingUnitDetails.get(0)[2] != null ? bookingUnitDetails.get(0)[2] : "NA");
                additionalInfo.put("additional_info6", orderId);
                additionalInfo.put("additional_info7", "NA");

				device.put("init_channel", prop.getProperty("INIT_CHANNEL"));
				device.put("ip", prop.getProperty("IP"));
				device.put("user_agent", prop.getProperty("USER_AGENT"));
				device.put("accept_header", prop.getProperty("ACCEPT_HEADER"));
				device.put("fingerprintid", prop.getProperty("FINGER_PRINT_ID"));
				device.put("browser_tz", prop.getProperty("BROWSER_TZ"));
				device.put("browser_color_depth", prop.getProperty("BROWSER_COLOR_DEPTH"));
				device.put("browser_java_enabled", prop.getProperty("BROWSER_JAVA_ENABLED"));
				device.put("browser_screen_height", prop.getProperty("BROWSER_SCREEN_HEIGHT"));
				device.put("browser_screen_width", prop.getProperty("BROWSER_SCREEN_WIDTH"));
				device.put("browser_language", prop.getProperty("BROWSER_LANGUAGE"));
				device.put("browser_javascript_enabled", prop.getProperty("BROWSER_JAVASCRIPT_ENABLED"));

				splitPayment.put("mercid", payload.getOrderDetails().getPaymentChildId());
				splitPayment.put("amount", String.format("%.2f",payload.getOrderDetails().getGrandTotal()));
				splitPaymentArray.put(splitPayment);

//				jsonObject.put("mercid", payload.getMercid());
				jsonObject.put("mercid", paymentGatewayDetails.getMercId());
				jsonObject.put("orderid", orderId);
				jsonObject.put("amount", String.format("%.2f",payload.getOrderDetails().getGrandTotal()));
				jsonObject.put("order_date", formattedDate);
				jsonObject.put("currency", prop.getProperty("CURRENCY"));
				jsonObject.put("ru", prop.getProperty("RU"));
				jsonObject.put("additional_info", additionalInfo);
				jsonObject.put("split_payment", splitPaymentArray);
				jsonObject.put("itemcode", prop.getProperty("ITEM_CODE"));
				jsonObject.put("device", device);

				String encToken = jWTUtils.generateJwtToken(jsonObject, header,
						paymentGatewayDetails.getClientSecret());
				this.saveEncodedRequest(encToken,orderId,payload.getOrderDetails().getOrderId(),loggedUser,payload.getOrderType());		
				if (encToken != null) {
					apiResp.setData(encToken);
					apiResp.setStatus(0);
				} else {
					apiResp.setStatus(1);
					apiResp.setMessage("Something went wrong");
				}
			}
		} catch (Exception e) {
			apiResp.setStatus(1);
			apiResp.setMessage("Something went wrong");
			e.printStackTrace();
		}
		return apiResp;
	}

	@SuppressWarnings("null")
	public void saveEncodedRequest(String encToken, String orderId, String portalOrderId, LoggedUser loggedUser, Integer orderType) {
		try {
			ClfPaymentResponseTracking clfPaymentResponseTracking = clfPaymentResponseTrackingRepo
					.getPaymentsByOrderId(orderId);
			if (clfPaymentResponseTracking != null) {
				clfPaymentResponseTracking.setEncodedResponse(encToken);
			} else {
				clfPaymentResponseTracking = new ClfPaymentResponseTracking();
				clfPaymentResponseTracking.setId(UUID.randomUUID().toString());
				clfPaymentResponseTracking.setPaymentStatus("pending");
				clfPaymentResponseTracking.setSecOrderId(portalOrderId);
				clfPaymentResponseTracking.setOrderId(orderId);
				clfPaymentResponseTracking.setEncodedRequest(encToken);
				clfPaymentResponseTracking.setCreatedBy(loggedUser.getUserId());
				clfPaymentResponseTracking.setCreatedTs(new Date());
				clfPaymentResponseTracking.setOrderType(orderType);
			}
			clfPaymentResponseTrackingRepo.save(clfPaymentResponseTracking);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
}
