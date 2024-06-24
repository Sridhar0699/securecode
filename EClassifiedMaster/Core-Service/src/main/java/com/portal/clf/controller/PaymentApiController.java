package com.portal.clf.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.portal.clf.models.BillDeskPaymentResponseModel;
import com.portal.clf.models.CartDetails;
import com.portal.clf.service.ClassifiedService;
import com.portal.clf.service.PaymentService;
import com.portal.common.models.GenericApiResponse;
import com.portal.security.model.LoggedUser;
import com.portal.security.util.LoggedUserContext;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

@RestController
public class PaymentApiController implements PaymentApi {

	private static final Logger logger = LogManager.getLogger(PaymentApiController.class);

	@Autowired(required = true)
	private Environment prop;

	@Autowired(required = true)
	private LoggedUserContext userContext;
	
	@Autowired
	private PaymentService paymentService;
	
	@Override
	public ResponseEntity<?> billdeskResponseHandler(@RequestBody BillDeskPaymentResponseModel payload) {
		String METHOD_NAME = "downloadAdsDocument";

		logger.info("Entered into the method: " + METHOD_NAME);

		ResponseEntity<GenericApiResponse> respObj = null;

		GenericApiResponse apiResp = new GenericApiResponse();
		LoggedUser loggedUser = userContext.getLoggedUser();
		try {

			apiResp = paymentService.updatePaymentResponse(payload, loggedUser);

		} catch (Exception e) {

			logger.error("Error while approving classifieds Details: " + ExceptionUtils.getStackTrace(e));

			apiResp.setStatus(1);
			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");
		}

		respObj = new ResponseEntity<GenericApiResponse>(apiResp, HttpStatus.OK);

		logger.info("Exit from the method: " + METHOD_NAME);

		return respObj;
	}

	@Override
	public ResponseEntity<?> showbilldeskModel(@NotNull String encToken) {
		String METHOD_NAME = "showbilldeskModel";

		logger.info("Entered into the method: " + METHOD_NAME);

		ResponseEntity<GenericApiResponse> respObj = null;

		GenericApiResponse apiResp = new GenericApiResponse();
		try {
//			String urlPath = "https://uat1.billdesk.com/u2/payments/ve1_2/orders/create";//UAT
//			String urlPath = "https://pguat.billdesk.io/payments/ve1_2/orders/create"//UAT
//			String urlPath = "https://api.billdesk.com/payments/ve1_2/orders/create";//PRD
			long timeStamp = new Date().getTime();
			Map<String, String> headerMap = new HashMap<>();
			headerMap.put("content-Type", "application/jose");
			headerMap.put("Bd-Timestamp", timeStamp + "");
			headerMap.put("Bd-Traceid", timeStamp + "ABD1K");
			headerMap.put("accept", "application/jose");
			String rsType = "text";
			HttpResponse<String> res = Unirest.post(prop.getProperty("BILLDESK_CREATE_ORDER_API")).headers(headerMap).body(encToken).asString();
			System.out.println("Billdesk res: "+res.getBody());
			if (res.getBody() != null && ("200".equalsIgnoreCase(res.getStatus() + "") || "409".equalsIgnoreCase(res.getStatus() +""))) {
				String[] split_string = res.getBody().split("\\.");
				if (split_string.length > 1) {
					String base64EncodedBody = split_string[1];
					JSONObject decodedRes = new JSONObject(new String(Base64Utils.decode(base64EncodedBody.getBytes())));
					paymentService.saveEncodedRequest(res.getBody(), decodedRes.getString("orderid"), null, null,null);
				}

				apiResp.setData(res.getBody());
				apiResp.setStatus(0);
			}else {
				apiResp.setMessage(res.getBody());
				apiResp.setStatus(1);
			}
		} catch (Exception e) {
			logger.error("Error while getting Billdesk Payment Options: " + ExceptionUtils.getStackTrace(e));

			apiResp.setStatus(1);
			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");
		}
		
		/*
		 * RestTemplate restTemplate = new RestTemplate();
		 * HttpHeaders headers = new HttpHeaders();
		 * headers.setContentType(MediaType.APPLICATION_JSON);
         * headers.set("content-Type", "application/jose");
		 * headers.set("Bd-Timestamp", timeStamp + "");
		 * headers.set("Bd-Traceid", timeStamp + "ABD1K");
		 * headers.set("accept", "application/jose");
		 * HttpEntity<String> httpEntity = new HttpEntity<>(encToken, headers);
		 * ResponseEntity<?> res = restTemplate.exchange(urlPath, HttpMethod.POST, httpEntity, String.class);
		 * if (res.getBody() != null && "200".equalsIgnoreCase(res.getStatusCodeValue()
		 * + "")) { apiResp.setStatus(0); apiResp.setData(res); } else if
		 * (res.getStatusCode() == HttpStatus.UNAUTHORIZED) {
		 * apiResp.setMessage(prop.getProperty("GEN_002"));
		 * apiResp.setErrorcode("GEN_002"); }
		 */
		
		respObj = new ResponseEntity<GenericApiResponse>(apiResp, HttpStatus.OK);

		logger.info("Exit from the method: " + METHOD_NAME);

		return respObj;
	}

	@Override
	public ResponseEntity<?> prepareRequest(@NotNull CartDetails payload) {
		String METHOD_NAME = "prepareRequest";

		logger.info("Entered into the method: " + METHOD_NAME);

		ResponseEntity<GenericApiResponse> respObj = null;
		ResponseEntity<?> resp = null;

		GenericApiResponse apiResp = new GenericApiResponse();
		LoggedUser loggedUser = userContext.getLoggedUser();
		try {
			apiResp = paymentService.prepareRequest(payload,loggedUser);
			if(apiResp != null && apiResp.getStatus() == 0 && apiResp.getData() != null) {
				resp = this.showbilldeskModel(apiResp.getData()+"");
				apiResp = (GenericApiResponse) resp.getBody();
			}else {
				apiResp.setMessage("Error while creating prepare request");
				apiResp.setStatus(1);
			}
		} catch (Exception e) {
			logger.error("Error while creating prepare request: " + ExceptionUtils.getStackTrace(e));

			apiResp.setStatus(1);
			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");
		}
		respObj = new ResponseEntity<GenericApiResponse>(apiResp, HttpStatus.OK);

		logger.info("Exit from the method: " + METHOD_NAME);

		return respObj;
	}

}
