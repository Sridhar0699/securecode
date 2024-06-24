package com.portal.gd.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.portal.clf.models.ClfRatesModel;
import com.portal.common.models.GenericApiResponse;
import com.portal.constants.GeneralConstants;
import com.portal.gd.models.ConfigValues;
import com.portal.gd.models.GdSettingsDetails;
import com.portal.gd.models.ListOfAccessObjects;
import com.portal.gd.models.ListOfConfigGroup;
import com.portal.gd.models.ListOfConfigValues;
import com.portal.gd.models.ListOfObjects;
import com.portal.gd.models.ListOfSmtpConfigGroup;
import com.portal.gd.models.MasterDataUpdateRequest;
import com.portal.gd.models.RegionalCentersList;
import com.portal.gd.to.GdSettingConfigsTo;
import com.portal.reports.to.ReportsRequest;
import com.portal.user.models.UserDetails;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Global Data API
 * 
 * @author Sathish Babu D
 *
 */
@Api(value = "Global Data", description = "Global Data management APIs")
@RequestMapping(value = GeneralConstants.API_VERSION)
public interface GlobalDataApi {

	/**
	 * Get specific value from the list object in the form of key and value pair
	 * 
	 * @param obj_name
	 * @param obj_id
	 * @return
	 */
	@ApiOperation(value = "Get global data list by id", notes = "Get specific value from the list object in the form of key and value pair", response = ListOfObjects.class, tags = {
			"Global" })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Operation", response = ListOfObjects.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/gd/list/{obj_name}/{obj_id}", produces = {
			"application/json" }, method = RequestMethod.GET)
	ResponseEntity<?> getListObjValueById(
			@ApiParam(value = "List object name", required = true) @PathVariable(value = "obj_name", required = true) String obj_name,
			@ApiParam(value = "List object id", required = true) @PathVariable(value = "obj_id", required = true) String obj_id);

	/**
	 * Get all values from the list object in the form of key and value pair
	 * 
	 * @param obj_name
	 * @return
	 */
	@ApiOperation(value = "Get list objects", notes = "Get all values from the list object in the form of key and value pair", response = ListOfObjects.class, tags = {
			"Global" })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Operation", response = ListOfObjects.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/gd/list/{obj_name}", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<?> getListObjValues(
			@ApiParam(value = "List object name", required = true) @PathVariable(value = "obj_name", required = true) String obj_name,
			@ApiParam(value = "Filter Key", required = false) @RequestParam(value = "filterKey", required = false) String filterKey,
			@ApiParam(value = "Filter Value", required = false) @RequestParam(value = "filterValue", required = false) String filterValue,
			@ApiParam(value = "isMapping", required = false) @RequestParam(value = "isMapping", required = false) boolean isMapping);

	/**
	 * Get the left side menu of application
	 * 
	 * @param org_id
	 * @param bp_id
	 * @param device_id
	 * @return
	 */
	@ApiOperation(value = "Get left side menu", notes = "Get the left side menu of application", response = ListOfAccessObjects.class, tags = {
			"Global", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful Operation", response = ListOfAccessObjects.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/gd/access/objs", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<?> getAccessObjects(
			@ApiParam(value = "Device id", required = false) @RequestParam(value = "device_id", required = false) Integer device_id);

	/**
	 * Get Configuration values
	 * 
	 * @param org_id
	 * @param bp_id
	 * @param data_type
	 * @param parent_group
	 * @return
	 */
	@ApiOperation(value = "Get Configuration values", notes = "Get Configuration values", response = ListOfConfigValues.class, tags = {
			"Global", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful Operation", response = ListOfConfigValues.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/gd/config/values", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<?> getConfigValues(
			@ApiParam(value = "Config group", required = false) @RequestParam(value = "group", required = false) String group,
			@ApiParam(value = "Parent Group", required = false) @RequestParam(value = "parent_group", required = false) String parent_group,
			@ApiParam(value = "Page number", required = false) @RequestParam(value = "pg_num", required = false) Integer pg_num,
			@ApiParam(value = "Page size", required = false) @RequestParam(value = "pg_size", required = false) Integer pg_size,
			@ApiParam(value = "Add Field3", required = false) @RequestParam(value = "add_field3", required = false) String addField3,
			@ApiParam(value = "Add Field7", required = false) @RequestParam(value = "add_field7", required = false) String addField7,
			@ApiParam(value = "Add Field2", required = false) @RequestParam(value = "add_field2", required = false) String addField2,
			@ApiParam(value = "Add Field1", required = false) @RequestParam(value = "add_field1", required = false) String addField1,
			@ApiParam(value = "multi BpIds", required = false) @RequestParam(value = "multiBpIds", required = false) String multiBpIds);

	/**
	 * Create or Update Configuration Values
	 * 
	 * @param payload
	 * @return
	 */
	@ApiOperation(value = "Create/Update Configuration Values", notes = "Create or Update Configuration Values", response = Void.class, tags = {
			"Global", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful Operation", response = GenericApiResponse.class),
			@ApiResponse(code = 201, message = "Created"), @ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 405, message = "Inavlid input") })
	@RequestMapping(value = "/gd/config/values", produces = { "application/json" }, method = RequestMethod.POST)
	ResponseEntity<?> createOrUpdateConfigValues(
			@ApiParam(value = "Config values", required = true) @RequestBody ListOfConfigValues payload);

	/**
	 * Create or Update Configuration Group
	 * 
	 * @param payload
	 * @return
	 */
	@ApiOperation(value = "Create/Update Configuration group", notes = "Create or Update Configuration Group details", response = Void.class, tags = {
			"Global", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful Operation", response = GenericApiResponse.class),
			@ApiResponse(code = 201, message = "Created"), @ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 405, message = "Inavlid input") })
	@RequestMapping(value = "/gd/config/group", produces = { "application/json" }, method = RequestMethod.POST)
	ResponseEntity<?> createOrUpdateConfigGroup(
			@ApiParam(value = "Config values", required = true) @RequestBody ListOfConfigGroup payload);

	/**
	 * Get Configuration Group Details
	 *
	 * @return
	 */
	@ApiOperation(value = "Get Configuration values", notes = "Get Configuration values", response = ListOfConfigGroup.class, tags = {
			"Global", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful Operation", response = ListOfConfigGroup.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/gd/config/group", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<?> getConfigGroup();

	@ApiOperation(value = "Get Configuration Values with Filters", notes = "Get Configuration Values with Filters", response = ListOfConfigValues.class, tags = {
			"Global", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful Operation", response = ListOfConfigValues.class),
			@ApiResponse(code = 201, message = "Created"), @ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 405, message = "Inavlid input") })
	@RequestMapping(value = "/gd/config/filters", produces = { "application/json" }, method = RequestMethod.POST)
	ResponseEntity<?> getConfigValuesWithFilter(
			@ApiParam(value = "Config values", required = true) @RequestBody ConfigValues payload);

	@ApiOperation(value = "Get Configuration values", notes = "Get Configuration values", response = ListOfConfigGroup.class, tags = {
			"Global", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful Operation", response = ListOfConfigGroup.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/download/config/group", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<?> exportConfigGroupReport(
			@ApiParam(value = "Config group", required = true) @RequestParam(value = "group", required = true) String group,
			HttpServletResponse response);

	@ApiOperation(value = "Upload Config Master file", notes = "Upload the csv file", response = Void.class, tags = {
			"Global", })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Operation", response = Void.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "No data found", response = GenericApiResponse.class) })
	@RequestMapping(value = "/dataentry/uploaddata/{plantId}", method = RequestMethod.POST)
	ResponseEntity<?> uploadConfigData(HttpServletRequest request,
			@ApiParam(value = "Plant ID", required = true) @PathVariable("plantId") String plantId,
			@ApiParam(value = "Group Id", required = true) @RequestParam("groupId") String groupId);

	/**
	 * Get SMTP Configuration Details
	 *
	 * @return
	 */
	@ApiOperation(value = "Get SMTP Configuration Details", notes = "Get SMTP Configuration Details", response = ListOfSmtpConfigGroup.class, tags = {
			"Global", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful Operation", response = ListOfSmtpConfigGroup.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/gd/smtp/config", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<?> getSmtpConfig();

	/**
	 * Update SMTP Configuration Details
	 *
	 * @return
	 */
	@ApiOperation(value = "Update SMTP Configuration Detail", notes = "Update SMTP Configuration Detail", response = Void.class, tags = {
			"Global", })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Operation", response = Void.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/gd/updatesmtp/config", produces = { "application/json" }, method = RequestMethod.POST)
	ResponseEntity<?> updateSmtpConfig(
			@ApiParam(value = "Smtp config", required = true) @RequestBody GdSettingConfigsTo payload);

	/**
	 * Test Mail
	 *
	 * @return
	 */
	@ApiOperation(value = "Test Mail", notes = "Test Mail", response = Void.class, tags = { "Global", })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Operation", response = Void.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/gd/testmail", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<?> sendTestMail(
			@NotNull @ApiParam(value = "To mails", required = true) @RequestParam(value = "to_mails", required = true) String to_mails,
			@NotNull @ApiParam(value = "CC mails", required = false) @RequestParam(value = "cc_mails", required = false) String cc_mails,
			@NotNull @ApiParam(value = "BCC mails", required = false) @RequestParam(value = "bcc_mails", required = false) String bcc_mails);

	@ApiOperation(value = "Get Regional Centers", notes = "Get the regional centers of application", response = RegionalCentersList.class, tags = {
			"Global", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful Operation", response = RegionalCentersList.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/gd/masters/{master_type}", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<?> getMasterData(
			@NotNull @ApiParam(value = "Master type", required = true) @PathVariable(value = "master_type", required = true) String master_type);

	/**
	 * Update SMTP Configuration Details
	 *
	 * @return
	 */
	@ApiOperation(value = "Update SMTP Configuration Detail", notes = "Update SMTP Configuration Detail", response = Void.class, tags = {
			"Global", })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Operation", response = Void.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/gd/masterdataupd", produces = { "application/json" }, method = RequestMethod.POST)
	ResponseEntity<?> createOrUpdateMasterData(
			@ApiParam(value = "Master Data Request", required = true) @RequestBody MasterDataUpdateRequest payload);
	
	
	@ApiOperation(value = "Get Allowed Masters", notes = "Get Allowed Masters", response = GenericApiResponse.class, tags = {
			"Global", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful Operation", response = RegionalCentersList.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/gd/allowedmasters", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<?> getAllowedMasters();
	
	/**
	 * Get setting definitions
	 * 
	 * @param obj_name
	 * @param obj_id
	 * @return
	 */
	@ApiOperation(value = "Get Setting definitions", notes = "Get Setting Definitions", response = ListOfObjects.class, tags = {
			"Global" })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Operation", response = ListOfObjects.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/gd/settings/{group}", produces = {
			"application/json" }, method = RequestMethod.GET)
	ResponseEntity<?> getSettings(
			@ApiParam(value = "Group", required = true) @PathVariable(value = "group", required = true) List<String> group);
	
	/**
	 * Create Or Update Setting Definitions
	 * 
	 * @param obj_name
	 * @param obj_id
	 * @return
	 */
	@ApiOperation(value = "Get Setting definitions", notes = "Get Setting Definitions", response = ListOfObjects.class, tags = {
			"Global" })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Operation", response = ListOfObjects.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/gd/settings/addorupdate", produces = {
			"application/json" }, method = RequestMethod.POST)
	ResponseEntity<?> createOrUpdateSettings(
			@NotNull @ApiParam(value = "payload", required = true) @RequestBody GdSettingsDetails payload);
	
	/**
	 * Delete Setting Data
	 * 
	 * @param obj_name
	 * @param obj_id
	 * @return
	 */
	@ApiOperation(value = "Delete Setting Data", notes = "Delete Setting Data", response = ListOfObjects.class, tags = {
			"Global" })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Operation", response = ListOfObjects.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/gd/settings/delete", produces = {
			"application/json" }, method = RequestMethod.POST)
	ResponseEntity<?> deleteSettingData(
			@NotNull @ApiParam(value = "payload", required = true) @RequestBody GdSettingsDetails payload);
	
	@ApiOperation(value = "Get GDSTATES", notes = "Get States Data", response = UserDetails.class, tags = {
			"Users", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful Operation", response = UserDetails.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/process/gd/states", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<?> getGdStates();
	
	@ApiOperation(value = "Get GDCITYS", notes = "Get City Data", response = UserDetails.class, tags = {
			"Users", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful Operation", response = UserDetails.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/process/gd/city", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<?> getGdCity();
	
	@ApiOperation(value = "Get Masters with filter", notes = "Get Masters with filter", response = RegionalCentersList.class, tags = {
			"Global", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful Operation", response = RegionalCentersList.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/gd/masters/filter/{master_type}", produces = { "application/json" }, method = RequestMethod.POST)
	ResponseEntity<?> getMasterDataByFilter(
			@NotNull @ApiParam(value = "Master type", required = true) @PathVariable(value = "master_type", required = true) String master_type,
			@RequestBody Map<String, Object> payload);
			
	
	/**
	 * Get Templates Headers Settings
	 * 
	 * @param obj_name
	 * @param obj_id
	 * @return
	 */
	@ApiOperation(value = "Get Templates Headers Settings", notes = "Get Templates Headers Settings", response = ListOfObjects.class, tags = {
			"Global" })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Operation", response = ListOfObjects.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/gd/templates/headers", produces = {
			"application/json" }, method = RequestMethod.GET)
	ResponseEntity<?> getTemplateHeadersSettings();
	
	/**
	 * Download Excel
	 * 
	 * @param access_obj_id
	 * @return
	 */
	@ApiOperation(value = "Excel Download", notes = "Excel Download", response = Void.class, tags = { "Template", })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful Operation", response = Void.class),
			@ApiResponse(code = 201, message = "Created"), @ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 405, message = "Inavlid input") })
	@PostMapping(value = "/gd/generate/excel/headers")
	public ResponseEntity<?> generateExcelForHeaders(
			@ApiParam(value = "Reports Request Body", required = false) @RequestBody(required = false) ReportsRequest payload,
			HttpServletRequest request, HttpServletResponse response);
	
	/**
	 * Upload Master data 
	 * 
	 * @param vins_effetced
	 * @param action
	 * @return
	 */
	@ApiOperation(value = "Upload Master Data", notes = "Upload Master Data", response = Void.class, tags = {
			"Global" })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful Operation", response = GenericApiResponse.class),
			@ApiResponse(code = 201, message = "Created"), @ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 405, message = "Inavlid input") })
	@RequestMapping(value = "/gd/upload/master")
	public ResponseEntity<?> uploadMasterData(
			@NotNull @ApiParam(value = "masterTypeData", required = true) @RequestParam("masterTypeData") String masterTypeData,
			@NotNull @ApiParam(value = "Upload Action", required = true) @RequestParam("action") String action,
			HttpServletRequest request);
	
	@ApiOperation(value = "Get Booking Units", notes = "Get Booking Units", response = UserDetails.class, tags = {
			"Global", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful Operation", response = UserDetails.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/process/gd/bookingunits", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<?> getBookingUnits();
	
	@ApiOperation(value = "Get Clf Rates", notes = "Get Clf Rates", response = UserDetails.class, tags = {
			"Global", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful Operation", response = UserDetails.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/gd/clf/rates", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<?> getClfRatesList();
	
	@ApiOperation(value = "Add Clf Rates", notes = "Add Clf Rates", response = RegionalCentersList.class, tags = {
			"Global", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful Operation", response = RegionalCentersList.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/gd/add/clfrates", produces = { "application/json" }, method = RequestMethod.POST)
	ResponseEntity<?> addClfRates(
			@ApiParam(value = "Clf Rates", required = true) @RequestBody ClfRatesModel payload);
	
	@ApiOperation(value = "Delete Clf Rates", notes = "Delete Clf Rates", response = RegionalCentersList.class, tags = {
			"Global", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successful Operation", response = RegionalCentersList.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 404, message = "No data found") })
	@RequestMapping(value = "/gd/delete/clfrates", produces = { "application/json" }, method = RequestMethod.POST)
	ResponseEntity<?> DeleteClfRates(
			@ApiParam(value = "Clf Rates", required = true) @RequestBody ClfRatesModel payload);

}
