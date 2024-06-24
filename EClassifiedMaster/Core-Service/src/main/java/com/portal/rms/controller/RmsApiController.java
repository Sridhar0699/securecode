package com.portal.rms.controller;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.portal.clf.controller.ClassifiedApiController;
import com.portal.common.models.GenericApiResponse;
import com.portal.common.service.CommonService;
import com.portal.rms.model.CreateOrder;
import com.portal.rms.model.OtpModel;
import com.portal.rms.model.RmsDashboardFilter;
import com.portal.rms.model.RmsModel;
import com.portal.rms.model.RmsPaymentLinkModel;
import com.portal.rms.model.RmsRateModel;
import com.portal.rms.service.RmsService;
import com.portal.security.model.LoggedUser;
import com.portal.security.util.LoggedUserContext;

@RestController
public class RmsApiController implements RmsApi {

private static final Logger logger = LogManager.getLogger(ClassifiedApiController.class);
	
	@Autowired
	private RmsService rmsService;
	
	@Autowired(required = true)
	private LoggedUserContext userContext;
	
	@Autowired(required = true)
	private Environment prop;
	
	@Autowired
	private CommonService commonService;
	
	@Override
	public ResponseEntity<?> getDashboardCounts(RmsDashboardFilter payload) {
		String METHOD_NAME = "getDashboardCounts";

		logger.info("Entered into the method: " + METHOD_NAME);

		ResponseEntity<GenericApiResponse> respObj = null;

		GenericApiResponse apiResp = new GenericApiResponse();
		LoggedUser loggedUser = userContext.getLoggedUser();
		try {

			apiResp = rmsService.getDashboardCounts(loggedUser, payload);

		} catch (Exception e) {

			logger.error("Error while getting dashboard counts: " + ExceptionUtils.getStackTrace(e));

			apiResp.setStatus(1);
			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");
		}

		respObj = new ResponseEntity<GenericApiResponse>(apiResp, HttpStatus.OK);

		logger.info("Exit from the method: " + METHOD_NAME);

		return respObj;

	}

	@Override
	public ResponseEntity<?> getDashboardList(RmsDashboardFilter payload) {
		String METHOD_NAME ="getDashboardList";
		logger.info("Entered into the method: " + METHOD_NAME);
		ResponseEntity<GenericApiResponse> respObj = null;
		GenericApiResponse apiResp = new GenericApiResponse();
		LoggedUser loggedUser = userContext.getLoggedUser();
		try {
			apiResp=rmsService.getRmsClassifiedList(loggedUser,payload);
		} catch (Exception e) {
			logger.error("Error while getting dashboard list "+ExceptionUtils.getStackTrace(e));
			apiResp.setStatus(1);
			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");
		}
		respObj=new ResponseEntity<GenericApiResponse>(apiResp,HttpStatus.OK);
		logger.info("Exit from the method: " + METHOD_NAME);
		return respObj;
	}
	
	
	@Override
	public ResponseEntity<?> getDetailsById(@NotNull String adId) {
		String METHOD_NAME ="getDetailsById";
		logger.info("Entered into the method: " + METHOD_NAME);
		ResponseEntity<GenericApiResponse> respObj=null;
		GenericApiResponse apiResp = new GenericApiResponse();
		LoggedUser loggedUser = userContext.getLoggedUser();
		
		try {
			apiResp=rmsService.getRmsClassifiedsByAdId(loggedUser,adId);
			
			} catch (Exception e) {
			
				logger.error("Error while getting rms classified items "+ExceptionUtils.getStackTrace(e));
				apiResp.setStatus(1);
				apiResp.setMessage(prop.getProperty("GEN_002"));
				apiResp.setErrorcode("GEN_002");
		}
		
		respObj=new ResponseEntity<GenericApiResponse>(apiResp,HttpStatus.OK);
		logger.info("Exit from the method: " + METHOD_NAME);
		
		return respObj;
	}

	@Override
	public ResponseEntity<?> addRmsClassifiedOrderToCart(CreateOrder payload) {
		String METHOD_NAME = "addRmsClassifiedOrderToCart";

		logger.info("Entered into the method: " + METHOD_NAME);

		ResponseEntity<GenericApiResponse> respObj = null;

		GenericApiResponse apiResp = new GenericApiResponse();
		LoggedUser loggedUser = userContext.getLoggedUser();

		try {

			apiResp = rmsService.addRmsClassifiedItemToCart(payload,loggedUser);

		} catch (Exception e) {

			logger.error("Error while getting add order to cart: " + ExceptionUtils.getStackTrace(e));

			apiResp.setStatus(1);
			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");
		}

		respObj = new ResponseEntity<GenericApiResponse>(apiResp, HttpStatus.OK);

		logger.info("Exit from the method: " + METHOD_NAME);

		return respObj;
	}

	@Override
	public ResponseEntity<?> getCustomerDetails(String clientCode,String customerName) {
		
		String METHOD_NAME = "getCustomerDetails";

		logger.info("Entered into the method: " + METHOD_NAME);

		ResponseEntity<GenericApiResponse> respObj = null;

		GenericApiResponse apiResp = new GenericApiResponse();

		try {

			apiResp = rmsService.getCustomerDetails(clientCode,customerName);

		} catch (Exception e) {

			logger.error("Error while getting customer details: " + ExceptionUtils.getStackTrace(e));

			apiResp.setStatus(1);
			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");
		}

		respObj = new ResponseEntity<GenericApiResponse>(apiResp, HttpStatus.OK);

		logger.info("Exit from the method: " + METHOD_NAME);

		return respObj;
	}

	@Override
	public ResponseEntity<?> getRmsRates(RmsRateModel payload) {
		String METHOD_NAME = "getRatesAndLines";

		logger.info("Entered into the method: " + METHOD_NAME);

		ResponseEntity<GenericApiResponse> respObj = null;

		GenericApiResponse apiResp = new GenericApiResponse();
		LoggedUser loggedUser = userContext.getLoggedUser();
		try {

			apiResp = rmsService.getRmsRates(payload);

		} catch (Exception e) {

			logger.error("Error while getting Rms rates: " + ExceptionUtils.getStackTrace(e));

			apiResp.setStatus(1);
			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");
		}

		respObj = new ResponseEntity<GenericApiResponse>(apiResp, HttpStatus.OK);

		logger.info("Exit from the method: " + METHOD_NAME);

		return respObj;
	}

	@Override
	public ResponseEntity<?> genrateOTP(OtpModel payload) {
		String METHOD_NAME = "genrateOTP";

		logger.info("Entered into the method: " + METHOD_NAME);

		ResponseEntity<GenericApiResponse> respObj = null;

		GenericApiResponse apiResp = new GenericApiResponse();
		LoggedUser loggedUser = userContext.getLoggedUser();
		try {

			apiResp = rmsService.genrateOTP(payload);

		} catch (Exception e) {

			logger.error("Error while genrate OTP: " + ExceptionUtils.getStackTrace(e));

			apiResp.setStatus(1);
			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");
		}

		respObj = new ResponseEntity<GenericApiResponse>(apiResp, HttpStatus.OK);

		logger.info("Exit from the method: " + METHOD_NAME);

		return respObj;
	}

	@Override
	public ResponseEntity<?> validateOTP(OtpModel payload) {
		String METHOD_NAME = "validateOTP";

		logger.info("Entered into the method: " + METHOD_NAME);

		ResponseEntity<GenericApiResponse> respObj = null;

		GenericApiResponse apiResp = new GenericApiResponse();
		LoggedUser loggedUser = userContext.getLoggedUser();
		try {

			apiResp = rmsService.validateOTP(payload);

		} catch (Exception e) {

			logger.error("Error while genrate OTP: " + ExceptionUtils.getStackTrace(e));

			apiResp.setStatus(1);
			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");
		}

		respObj = new ResponseEntity<GenericApiResponse>(apiResp, HttpStatus.OK);

		logger.info("Exit from the method: " + METHOD_NAME);

		return respObj;
	}

	@Override
	public ResponseEntity<?> syncronizeRmsSAPData(@NotNull RmsModel payload) {
		@SuppressWarnings("unchecked")
		GenericApiResponse apiResp = rmsService.syncronizeRmsSAPData(commonService.getRequestHeaders(),payload);
		return new ResponseEntity<GenericApiResponse>(apiResp, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> generateSendPaymentLink(RmsPaymentLinkModel payload) {
		String METHOD_NAME = "generateSendPaymentLink";

		logger.info("Entered into the method: " + METHOD_NAME);

		ResponseEntity<GenericApiResponse> respObj = null;

		GenericApiResponse apiResp = new GenericApiResponse();
		try {
			apiResp = rmsService.generateSendPaymentLink(payload);
			
		} catch (Exception e) {
			logger.error("Error while genrating Link: " + ExceptionUtils.getStackTrace(e));

			apiResp.setStatus(1);
			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");
		}
		
		respObj = new ResponseEntity<GenericApiResponse>(apiResp, HttpStatus.OK);

		logger.info("Exit from the method: " + METHOD_NAME);
		return respObj;
	}


}
