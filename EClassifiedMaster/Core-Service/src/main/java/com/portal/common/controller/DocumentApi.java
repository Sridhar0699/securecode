package com.portal.common.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.portal.common.models.GenericApiResponse;
import com.portal.constants.GeneralConstants;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Rms", description = "Rms Documents API")
@RequestMapping(value = GeneralConstants.API_VERSION)
public interface DocumentApi {

	@ApiOperation(value = "Upload Rms Documents", notes = "Upload Rms Documents", response = GenericApiResponse.class, tags = {
	"Documents" })
@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Operation", response = GenericApiResponse.class),
	@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
	@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
@RequestMapping(value = "/rms/docupload", produces = { "application/json" }, method = RequestMethod.POST)
ResponseEntity<?> uploadRmsDocUpload(HttpServletRequest request);

}
