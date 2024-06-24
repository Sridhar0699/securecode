package com.portal.gd.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portal.basedao.IBaseDao;
import com.portal.clf.entities.ClfRates;
import com.portal.clf.models.ClfRatesModel;
import com.portal.common.models.GenericApiResponse;
import com.portal.common.service.CommonService;
import com.portal.constants.GeneralConstants;
import com.portal.constants.GeneralConstants.SettingType;
import com.portal.constants.MasterData;
import com.portal.constants.MasterDataDuplicateCheckUniqueIds;
import com.portal.constants.MasterDataUniqueIds;
import com.portal.gd.dao.GlobalDataDao;
import com.portal.gd.entities.BookingUnits;
import com.portal.gd.entities.GdCity;
import com.portal.gd.entities.GdSettingTypes;
import com.portal.gd.entities.GdSettingsDefinitions;
import com.portal.gd.entities.GdState;
import com.portal.gd.models.ConfigGroup;
import com.portal.gd.models.ConfigValues;
import com.portal.gd.models.GdMasterResponse;
import com.portal.gd.models.GdSettingsDetails;
import com.portal.gd.models.ListObjectDetails;
import com.portal.gd.models.ListOfAccessObjects;
import com.portal.gd.models.ListOfChildObjs;
import com.portal.gd.models.ListOfConfigGroup;
import com.portal.gd.models.ListOfConfigValues;
import com.portal.gd.models.ListOfObjects;
import com.portal.gd.models.ListOfParentObjects;
import com.portal.gd.models.ListOfSmtpConfigGroup;
import com.portal.gd.models.MasterDataUpdateRequest;
import com.portal.gd.to.GdSettingConfigsTo;
import com.portal.gd.to.GlobalDataTo;
import com.portal.gd.to.ListOfConfigGroupTo;
import com.portal.gd.to.ListOfConfigValuesTo;
import com.portal.nm.dao.NotificationDaoImpl;
import com.portal.nm.model.Notifications;
import com.portal.nm.websocket.service.WebSocketServiceImpl;
import com.portal.org.to.ChildOjectTo;
import com.portal.org.to.ParentObjectTo;
import com.portal.reports.to.ReportsRequest;
import com.portal.reports.utility.CommonUtils;
import com.portal.repository.ClfRatesRepo;
import com.portal.repository.GdSettingsDefinitionsRepository;
import com.portal.security.model.LoggedUser;
import com.portal.security.util.LoggedUserContext;
import com.portal.send.models.EmailsTo;
import com.portal.send.service.SendMessageService;
import com.portal.setting.dao.SettingDao;
import com.portal.setting.to.SettingTo;
import com.portal.user.dao.UserDao;
import com.portal.utils.HelperUtil;

/**
 * Global data service implementation
 * 
 * @author Sathish Babu D
 *
 */
@Service("globalDataService")
@Transactional
@PropertySource(value = { "classpath:/com/portal/messages/messages.properties" })
public class GlobalDataServiceImpl implements GlobalDataService {

	private static final Logger logger = LogManager.getLogger(GlobalDataServiceImpl.class);

	@Autowired(required = true)
	private GlobalDataDao globalDataDao;

	@Autowired(required = true)
	private Environment prop;

	@Autowired(required = true)
	private LoggedUserContext userContext;

	@Autowired(required = true)
	private SettingDao settingDao;

	@Autowired(required = true)
	private SendMessageService sendService;
	
	@Autowired
	private IBaseDao baseDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private WebSocketServiceImpl webSocketService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private NotificationDaoImpl notificationDaoImpl;
	
	@Autowired
	private GdSettingsDefinitionsRepository settingRepo;
	
	@Autowired
	private LoggedUserContext loggedUserContext;
	
	@Autowired
	private ClfRatesRepo clfRatesRepo;

	/**
	 * Get List object values by criteria
	 * 
	 * @param listObj
	 * @param listObjId
	 * @return
	 */
	@Override
	public GenericApiResponse getListValuesByCriteria(String listObj, String listObjId) {

		String METHOD_NAME = "getListValuesByCriteria";

		logger.info("Entered into the method: " + METHOD_NAME);

		GenericApiResponse apiResp = new GenericApiResponse();

		try {

			List<Object[]> listValues = globalDataDao.getListObjValues(listObj, listObjId);

			if (listValues != null) {

				ListOfObjects lObjVales = new ListOfObjects();

				List<ListObjectDetails> lLobj = new ArrayList<ListObjectDetails>();

				for (Object[] obj : listValues) {

					ListObjectDetails lObj = new ListObjectDetails();

					if (obj[0] != null && obj[1] != null) {

						lObj.setId(obj[0].toString());
						lObj.setValue(obj[1].toString());
						if ("sap_states".equalsIgnoreCase(listObj) && obj[2] != null) {
							lObj.setStateId(obj[2].toString());
						}
					}

					if (lObj != null)
						lLobj.add(lObj);
				}

				if (lLobj != null) {

					lObjVales.setObjs(lLobj);
				}

				apiResp.setStatus(0);
				apiResp.setData(lObjVales);

			} else {

				apiResp.setMessage(MessageFormat.format(prop.getProperty("GD_001"), listObj));
				apiResp.setErrorcode("GD_001");
			}

		} catch (Exception e) {

			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");

			logger.error("Error occured while getting list values by object: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return apiResp;
	}

	/**
	 * Get the left side menu of application
	 * 
	 * @param orgId
	 * @param bpId
	 * @param deviceId
	 * @return
	 */
	@Override
	public GenericApiResponse getAccessObjects(String orgId, String bpId, Integer deviceId) {

		String METHOD_NAME = "getAccessObjects";

		logger.info("Entered into the method: " + METHOD_NAME);

		GenericApiResponse apiResp = new GenericApiResponse();

		try {
			GlobalDataTo globalDataTo = new GlobalDataTo();
			List<ParentObjectTo> globalDataList = new ArrayList<ParentObjectTo>();
			globalDataTo.setOrgId(orgId);
			globalDataTo.setBpId(bpId);
			globalDataTo.setDeviceId(deviceId);
			globalDataTo.setLoggedUser(userContext.getLoggedUser().getUserId());
			globalDataTo.setRoleId(Integer.parseInt(userContext.getLoggedUser().getRoleId()));
			if(userContext.getLoggedUser().getSecondaryRoles()!=null) {
				List<String> sIds = new ArrayList<String>(Arrays.asList(userContext.getLoggedUser().getSecondaryRoles().split(",")));
				globalDataTo.setSecondaryRoles(sIds);
			}
			globalDataList = globalDataDao.getAccessObjects(globalDataTo);

			// Notification Count
            webSocketService.getNotificationUnreadCount(commonService.getRequestHeaders());
			
			if (globalDataList != null && !globalDataList.isEmpty()) {

				List<ListOfParentObjects> list = new ArrayList<ListOfParentObjects>();

				for (ParentObjectTo gcv : globalDataList) {

					ListOfParentObjects parentGroup = new ListOfParentObjects();

					BeanUtils.copyProperties(gcv, parentGroup);
					if (gcv.getChildObjs() != null) {
						List<ListOfChildObjs> childObjs = new ArrayList<>();
						for (ChildOjectTo cObjs : gcv.getChildObjs()) {
							ListOfChildObjs lcObjs = new ListOfChildObjs();
							BeanUtils.copyProperties(cObjs, lcObjs);
							childObjs.add(lcObjs);
						}
						parentGroup.setChildObjs(childObjs);
					}

					list.add(parentGroup);
				}
				if (list.size() > 0) {
					Comparator<ListOfParentObjects> compareById = (ListOfParentObjects o1, ListOfParentObjects o2) -> o1
							.getSeqNum().compareTo(o2.getSeqNum());

					Collections.sort(list, compareById);
				}
				ListOfAccessObjects accessObjects = new ListOfAccessObjects();
				accessObjects.setRoleName(userContext.getLoggedUser().getRoleName());
				accessObjects.setAccessObjs(list);

				apiResp.setStatus(0);
				apiResp.setData(accessObjects);

			}
		} catch (Exception e) {

			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");

			logger.error("Error while getting access objects: " + ExceptionUtils.getStackTrace(e));
		}

		/*
		 * try {
		 * 
		 * GlobalDataTo globalDataTo = new GlobalDataTo(); List<ParentObjectTo>
		 * globalDataList = new ArrayList<ParentObjectTo>();
		 * globalDataTo.setOrgId(orgId); globalDataTo.setBpId(bpId);
		 * globalDataTo.setDeviceId(deviceId);
		 * globalDataTo.setLoggedUser(userContext.getLoggedUser().getUserId());
		 * globalDataTo.setRoleId(userContext.getLoggedUser().getRoleId());
		 * globalDataList = globalDataDao.getAccessObjects(globalDataTo);
		 * 
		 * ListOfAccessObjects accessObjects = new ListOfAccessObjects();
		 * 
		 * accessObjects.setRoleName(globalDataTo.getRoleName());
		 * 
		 * List<GdAccessObjects> gdAccessObjects = globalDataTo.getGdAccessObjects();
		 * 
		 * //List<String> finalAccessObjs = globalDataTo.getFinalAccessObjs();
		 * 
		 * if (gdAccessObjects != null) {
		 * 
		 * List<String> parentList = new ArrayList<String>();
		 * 
		 * LinkedHashMap<String, ListOfParentObjects> parentObjects = new
		 * LinkedHashMap<String, ListOfParentObjects>();
		 * 
		 * LinkedHashMap<String, List<ListOfChildObjs>> childObjects = new
		 * LinkedHashMap<String, List<ListOfChildObjs>>();
		 * 
		 * for (GdAccessObjects gdAccessObjects2 : gdAccessObjects) {
		 * 
		 * if (globalDataList.contains(gdAccessObjects2.getAccessObjId())) {
		 * 
		 * if (gdAccessObjects2.getParentObjId() == null ||
		 * gdAccessObjects2.getParentObjId().isEmpty()) {
		 * 
		 * ListOfParentObjects parentObjs = new ListOfParentObjects();
		 * parentObjs.setParentObjId(gdAccessObjects2.getAccessObjId());
		 * parentObjs.setParentObjName(gdAccessObjects2.getMenuTitle());
		 * parentObjs.setSeqNum(gdAccessObjects2.getSeqNo());
		 * parentObjs.setNavLink(gdAccessObjects2.getNavLink());
		 * //parentObjs.set(gdAccessObjects2.getUmOrgRolesPermissions()); if
		 * (gdAccessObjects2.getMenuIcon().contains(",")) {
		 * 
		 * String menuIcon[] = gdAccessObjects2.getMenuIcon().split(",");
		 * 
		 * if (deviceId != null && (deviceId == GeneralConstants.MOB_ANDRIOD_APP ||
		 * deviceId == GeneralConstants.TAB_ANDRIOD_APP)) {
		 * 
		 * parentObjs.setMenuIcon(menuIcon[1]);
		 * 
		 * } else {
		 * 
		 * parentObjs.setMenuIcon(menuIcon[0]); }
		 * 
		 * } else {
		 * 
		 * parentObjs.setMenuIcon(gdAccessObjects2.getMenuIcon()); }
		 * 
		 * parentObjects.put(gdAccessObjects2.getAccessObjId(), parentObjs);
		 * 
		 * } else {
		 * 
		 * if (childObjects.containsKey(gdAccessObjects2.getParentObjId())) {
		 * 
		 * ListOfChildObjs childAccessObject = new ListOfChildObjs();
		 * childAccessObject.setObjId(gdAccessObjects2.getAccessObjId());
		 * childAccessObject.setObjName(gdAccessObjects2.getMenuTitle());
		 * childAccessObject.setSeqNum(gdAccessObjects2.getSeqNo());
		 * childAccessObject.setNavLink(gdAccessObjects2.getNavLink());
		 * childAccessObject.setPermissionLevel(1);
		 * 
		 * if (gdAccessObjects2.getMenuIcon().contains(",")) {
		 * 
		 * String menuIcon[] = gdAccessObjects2.getMenuIcon().split(",");
		 * 
		 * if (deviceId != null && (deviceId == GeneralConstants.MOB_ANDRIOD_APP ||
		 * deviceId == GeneralConstants.TAB_ANDRIOD_APP)) {
		 * 
		 * childAccessObject.setMenuIcon(menuIcon[1]);
		 * 
		 * } else {
		 * 
		 * childAccessObject.setMenuIcon(menuIcon[0]); }
		 * 
		 * } else {
		 * 
		 * childAccessObject.setMenuIcon(gdAccessObjects2.getMenuIcon()); }
		 * 
		 * childObjects.get(gdAccessObjects2.getParentObjId()).add(childAccessObject);
		 * 
		 * } else {
		 * 
		 * List<ListOfChildObjs> childAccessObjects = new ArrayList<ListOfChildObjs>();
		 * 
		 * ListOfChildObjs childAccessObject = new ListOfChildObjs();
		 * childAccessObject.setObjId(gdAccessObjects2.getAccessObjId());
		 * childAccessObject.setObjName(gdAccessObjects2.getMenuTitle());
		 * childAccessObject.setSeqNum(gdAccessObjects2.getSeqNo());
		 * childAccessObject.setNavLink(gdAccessObjects2.getNavLink());
		 * childAccessObject.setPermissionLevel(1);
		 * 
		 * if (gdAccessObjects2.getMenuIcon().contains(",")) {
		 * 
		 * String menuIcon[] = gdAccessObjects2.getMenuIcon().split(",");
		 * 
		 * if (deviceId != null && (deviceId == GeneralConstants.MOB_ANDRIOD_APP ||
		 * deviceId == GeneralConstants.TAB_ANDRIOD_APP)) {
		 * 
		 * childAccessObject.setMenuIcon(menuIcon[1]);
		 * 
		 * } else {
		 * 
		 * childAccessObject.setMenuIcon(menuIcon[0]); }
		 * 
		 * } else {
		 * 
		 * childAccessObject.setMenuIcon(gdAccessObjects2.getMenuIcon()); }
		 * 
		 * childAccessObjects.add(childAccessObject);
		 * childObjects.put(gdAccessObjects2.getParentObjId(), childAccessObjects); }
		 * 
		 * parentList.add(gdAccessObjects2.getParentObjId()); } } }
		 * 
		 * List<ListOfParentObjects> parentObjs = new ArrayList<ListOfParentObjects>();
		 * 
		 * for (Map.Entry<String, ListOfParentObjects> map : parentObjects.entrySet()) {
		 * 
		 * ListOfParentObjects accessObjectsInner = (ListOfParentObjects)
		 * map.getValue();
		 * 
		 * List<ListOfChildObjs> childObjList = childObjects.get(map.getKey());
		 * 
		 * if (childObjList != null) {
		 * 
		 * Collections.sort(childObjList, new Comparator<ListOfChildObjs>() {
		 * 
		 * @Override public int compare(ListOfChildObjs o1, ListOfChildObjs o2) { return
		 * Integer.compare(o1.getSeqNum(), o2.getSeqNum()); } });
		 * 
		 * accessObjectsInner.setChildObjs(childObjList);
		 * 
		 * if (childObjList.size() > 0) {
		 * 
		 * accessObjectsInner.setPermissionLevel(1); parentObjs.add(accessObjectsInner);
		 * } } }
		 * 
		 * if (parentObjs != null) {
		 * 
		 * Collections.sort(parentObjs, new Comparator<ListOfParentObjects>() {
		 * 
		 * public int compare(ListOfParentObjects o1, ListOfParentObjects o2) { return
		 * Integer.compare(o1.getSeqNum(), o2.getSeqNum()); } }); }
		 * 
		 * accessObjects.setAccessObjs(parentObjs);
		 * 
		 * apiResp.setStatus(0); apiResp.setData(accessObjects);
		 * 
		 * } else {
		 * 
		 * apiResp.setMessage(prop.getProperty("GD_002"));
		 * apiResp.setErrorcode("GD_002"); }
		 * 
		 * } catch (Exception e) {
		 * 
		 * apiResp.setMessage(prop.getProperty("GEN_002"));
		 * apiResp.setErrorcode("GEN_002");
		 * 
		 * logger.error("Error while getting access objects: " +
		 * ExceptionUtils.getStackTrace(e)); }
		 */

		logger.info("Exit from the method: " + METHOD_NAME);

		return apiResp;
	}

	/**
	 * Get Configuration values
	 * 
	 * @param orgId
	 * @param bpId
	 * @param dataType
	 * @param parentGroup
	 * @return
	 */
	@Override
	public GenericApiResponse getConfigValues(String orgId, String bpId, String group, String parentGroup,
			Integer pgSize, Integer pgNum, String addField3, String addField7, String addField2, String multiBpIds,
			String addField1) {

		String METHOD_NAME = "getConfigValues";

		logger.info("Entered into the method: " + METHOD_NAME);

		GenericApiResponse apiResp = new GenericApiResponse();

		try {

			Integer ttl_cnt = null;
			ListOfConfigValuesTo configval = new ListOfConfigValuesTo();
//			configval.setOrgId(orgId);
			configval.setBpId(bpId);
			configval.setConfigGroupId(group);
			configval.setParentValId(parentGroup);
			configval.setAddField1(addField1);
			configval.setAddField2(addField2);
			configval.setAddField3(addField3);
			configval.setAddField7(addField7);
			configval.setMultiBpIds(multiBpIds);
			configval.setPgNum(pgNum);
			configval.setPgSize(pgSize);

			List<ListOfConfigValuesTo> listconfgroup = globalDataDao.getConfigValues(configval);

			if (listconfgroup != null && !listconfgroup.isEmpty()) {

				List<ConfigValues> list = new ArrayList<ConfigValues>();
				for (ListOfConfigValuesTo gcv : listconfgroup) {
					ConfigValues configValue = new ConfigValues();

					BeanUtils.copyProperties(gcv, configValue);

					if (ttl_cnt == null)
						ttl_cnt = gcv.getTtlCnt();

					list.add(configValue);
				}

				ListOfConfigValues listOfConfigValues = new ListOfConfigValues();

				listOfConfigValues.setConfigValues(list);
				listOfConfigValues.setTtl_cnt(ttl_cnt);
				listOfConfigValues.setPg_num(pgNum);
				listOfConfigValues.setPg_size(pgSize);

				apiResp.setStatus(0);
				apiResp.setData(listOfConfigValues);

			} else {

				apiResp.setMessage(prop.getProperty("GD_003"));
				apiResp.setErrorcode("GD_003");
			}

		} catch (Exception e) {

			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");

			logger.error("Error while getting config values: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return apiResp;
	}

	/**
	 * Create or Update Configuration Values
	 * 
	 * @param payload
	 * @return
	 */
	public GenericApiResponse createOrUpdateConfigValues(ListOfConfigValues payload) {

		String METHOD_NAME = "createOrUpdateConfigValues";

		logger.info("Entered into the method: " + METHOD_NAME);

		GenericApiResponse apiResp = new GenericApiResponse();

		try {

			if (payload != null && payload.getConfigValues() != null && !payload.getConfigValues().isEmpty()) {

				ListOfConfigValuesTo configval = new ListOfConfigValuesTo();
				configval.setOrgId(payload.getConfigValues().get(0).getOrgId());
				configval.setConfigGroupId(payload.getConfigValues().get(0).getConfigGroupId());
				configval.setMultiBpIds(payload.getConfigValues().get(0).getBpId());
				configval.setPgNum(-1);

				List<ListOfConfigValuesTo> listconfgroup = globalDataDao.getConfigValues(configval);

				Map<String, Map<String, ListOfConfigValuesTo>> map = new HashMap<>();
				if (listconfgroup != null) {
					for (ListOfConfigValuesTo dbList : listconfgroup) {
						if (map.containsKey("bpId")) {// dbList.getBpId()
							Map<String, ListOfConfigValuesTo> bpidMap = map.get("bpId");
							bpidMap.put(dbList.getKey(), dbList);
						} else {
							Map<String, ListOfConfigValuesTo> configData = new HashMap<>();
							configData.put(dbList.getKey(), dbList);
							map.put("bpId", configData);
						}
					}
				}

				List<ListOfConfigValuesTo> list = new ArrayList<ListOfConfigValuesTo>();
				if (payload.getConfigValues().get(0).getBpId() != null
						&& !payload.getConfigValues().get(0).getBpId().isEmpty()) {

					String[] bpid = payload.getConfigValues().get(0).getBpId().split(",");
					for (ConfigValues gcv : payload.getConfigValues()) {
						for (String id : bpid) {
							ListOfConfigValuesTo configValue = new ListOfConfigValuesTo();
							if (map.containsKey(id.substring(1, id.length() - 1))) {
								Map<String, ListOfConfigValuesTo> bpData = map.get(id.substring(1, id.length() - 1));
								if (bpData.containsKey(gcv.getKey())) {
									ListOfConfigValuesTo data = bpData.get(gcv.getKey());
									BeanUtils.copyProperties(gcv, configValue);
									configValue.setConfigValId(data.getConfigValId());
									configValue.setBpId(id.substring(1, id.length() - 1));
									list.add(configValue);
								} else {
									if (!gcv.getMarkAsDelete()) {
										BeanUtils.copyProperties(gcv, configValue);
										configValue.setBpId(id.substring(1, id.length() - 1));
										configValue.setConfigValId(null);
										list.add(configValue);
									}
								}
							} else {
								if (!gcv.getMarkAsDelete()) {
									BeanUtils.copyProperties(gcv, configValue);
									configValue.setConfigValId(null);
									configValue.setBpId(id.substring(1, id.length() - 1));
									list.add(configValue);
								}
							}
						}
					}
				} else {
					Map<String, ListOfConfigValuesTo> bp = map.get("bpId");
					for (ConfigValues gcv : payload.getConfigValues()) {
						ListOfConfigValuesTo configValue = new ListOfConfigValuesTo();
						ListOfConfigValuesTo data = null;
						if (bp != null) {
							data = bp.get(gcv.getKey());
						}
						BeanUtils.copyProperties(gcv, configValue);
						configValue.setConfigValId(data != null ? data.getConfigValId() : null);
						list.add(configValue);
					}
				}

				String retMessage = globalDataDao.createOrUpdateConfigValues(list,
						userContext.getLoggedUser().getUserId());

				if (retMessage != null) {

					apiResp.setStatus(0);

					if (retMessage.equals("SUCCESS_CREATE")) {

						apiResp.setMessage(prop.getProperty("GD_005"));

					} else if (retMessage.equals("SUCCESS_UPDATE")) {

						apiResp.setMessage(prop.getProperty("GD_009"));

					} else if (retMessage.equals("EXISTED")) {

						apiResp.setStatus(1);
						apiResp.setErrorcode("GD_008");
						apiResp.setMessage(prop.getProperty("GD_008"));
					}

				} else {

					apiResp.setMessage(prop.getProperty("GD_007"));
					apiResp.setErrorcode("GD_007");
				}

			} else {

				apiResp.setMessage(prop.getProperty("GD_007"));
				apiResp.setErrorcode("GD_007");
			}

		} catch (Exception e) {

			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");

			logger.error("Error while getting config values: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return apiResp;
	}

	/**
	 * Create or Update Configuration Group
	 * 
	 * @param payload
	 * @return
	 */
	public GenericApiResponse createOrUpdateConfigGroup(ListOfConfigGroup payload) {

		String METHOD_NAME = "createOrUpdateConfigGroup";

		logger.info("Entered into the method: " + METHOD_NAME);

		GenericApiResponse apiResp = new GenericApiResponse();

		try {

			if (payload != null && payload.getConfigGroups() != null && !payload.getConfigGroups().isEmpty()) {

				List<ListOfConfigGroupTo> list = new ArrayList<ListOfConfigGroupTo>();

				for (ConfigGroup gcg : payload.getConfigGroups()) {

					ListOfConfigGroupTo configGroup = new ListOfConfigGroupTo();

					BeanUtils.copyProperties(gcg, configGroup);

					list.add(configGroup);
				}

				String status = globalDataDao.createOrUpdateConfigGroup(list, userContext.getLoggedUser().getUserId());

				if (status != null) {

					apiResp.setStatus(0);

					if ("SUCCESS_CREATE".equals(status)) {

						apiResp.setMessage(prop.getProperty("GD_004"));

					} else if ("SUCCESS_UPDATE".equals(status)) {

						apiResp.setMessage(prop.getProperty("GD_010"));
					}

				} else {

					apiResp.setMessage(prop.getProperty("GD_006"));
					apiResp.setErrorcode("GD_006");
				}

			} else {

				apiResp.setMessage(prop.getProperty("GD_006"));
				apiResp.setErrorcode("GD_006");
			}

		} catch (Exception e) {

			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");

			logger.error("Error while getting config group details: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return apiResp;
	}

	/**
	 * Get Configuration Group Details
	 *
	 * @return
	 */
	public GenericApiResponse getConfigGroup() {

		String METHOD_NAME = "getConfigGroup";

		logger.info("Entered into the method: " + METHOD_NAME);

		GenericApiResponse apiResp = new GenericApiResponse();

		try {

			List<ListOfConfigGroupTo> listconfgroup = globalDataDao.getConfigGroup();

			if (listconfgroup != null && !listconfgroup.isEmpty()) {

				List<ConfigGroup> list = new ArrayList<ConfigGroup>();

				for (ListOfConfigGroupTo gcv : listconfgroup) {

					ConfigGroup configGroup = new ConfigGroup();

					BeanUtils.copyProperties(gcv, configGroup);

					list.add(configGroup);
				}

				ListOfConfigGroup listOfConfigGroup = new ListOfConfigGroup();

				listOfConfigGroup.setConfigGroups(list);

				apiResp.setStatus(0);
				apiResp.setData(listOfConfigGroup);

			} else {

				apiResp.setMessage(prop.getProperty("GD_003"));
				apiResp.setErrorcode("GD_003");
			}

		} catch (Exception e) {

			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");

			logger.error("Error while getting config group details : " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return apiResp;
	}

	@Override
	public GenericApiResponse getConfigValuesWithFilter(ConfigValues payload) {

		String METHOD_NAME = "getConfigValues";

		logger.info("Entered into the method: " + METHOD_NAME);

		GenericApiResponse apiResp = new GenericApiResponse();

		try {

			List<ListOfConfigValuesTo> listconfgroup = globalDataDao.getConfigValuesWithFilter(payload.getOrgId(),
					payload.getBpId(), payload.getKey(), payload.getValue(), payload.getConfigGroupId(),
					payload.getParentValId(), payload.getAddField1(), payload.getAddField2(), payload.getAddField3(),
					payload.getAddField4(), payload.getAddField5(), payload.getAddField6(), payload.getAddField7(),
					payload.getAddField8());

			if (listconfgroup != null && !listconfgroup.isEmpty()) {

				List<ConfigValues> list = new ArrayList<ConfigValues>();

				for (ListOfConfigValuesTo gcv : listconfgroup) {

					ConfigValues configValue = new ConfigValues();

					BeanUtils.copyProperties(gcv, configValue);

					list.add(configValue);
				}

				ListOfConfigValues listOfConfigValues = new ListOfConfigValues();

				listOfConfigValues.setConfigValues(list);

				apiResp.setStatus(0);
				apiResp.setData(listOfConfigValues);

			} else {

				apiResp.setMessage(prop.getProperty("GD_003"));
				apiResp.setErrorcode("GD_003");
			}

		} catch (Exception e) {

			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");

			logger.error("Error while getting config values: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return apiResp;
	}

	@Override
	public GenericApiResponse getListValues(String obj_name, String filterKey, String filterValue, boolean isMapping) {

		String METHOD_NAME = "getListValuesByCriteria";
		logger.info("Entered into the method: " + METHOD_NAME);

		GenericApiResponse apiResp = new GenericApiResponse();
		try {
			List<Map<String, Object>> listValues = globalDataDao.getTablelist(obj_name, filterKey, filterValue,
					isMapping);
			apiResp.setData(listValues);
			apiResp.setStatus(0);
		} catch (Exception e) {
			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");
			logger.error("Error occured while getting list values by object: " + ExceptionUtils.getStackTrace(e));
		}
		logger.info("Exit from the method: " + METHOD_NAME);

		return apiResp;
	}

	@Override
	public GenericApiResponse exportConfigGroupReport(String group, String orgId, String bpId) {

		String METHOD_NAME = "exportConfigGroupReport";

		logger.info("Entered into the method: " + METHOD_NAME);

		GenericApiResponse apiResp = new GenericApiResponse();

		try {
			ListOfConfigValuesTo configval = new ListOfConfigValuesTo();
			configval.setOrgId(orgId);
			configval.setBpId(bpId);
			configval.setConfigGroupId(group);

			List<ListOfConfigValuesTo> listconfgroup = globalDataDao.getConfigValues(configval);

			XSSFWorkbook workbook = new XSSFWorkbook();

			this.prepareExcelSheet(workbook, listconfgroup, group);

			apiResp.setData(HelperUtil.getWorkBookAsBytes(workbook));
			apiResp.setStatus(0);

		} catch (Exception e) {

			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");
			apiResp.setStatus(1);

			logger.error("Error while Download config group report: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return apiResp;
	}

	public void prepareExcelSheet(XSSFWorkbook workbook, List<ListOfConfigValuesTo> listconfgroup, String group) {

		String headers[] = null;
		XSSFSheet sheet = null;
		if (GeneralConstants.CNF_GRP_MTRLS.equalsIgnoreCase(group)) {
			headers = new String[] { "KEY", "VALUE", "Box", "Case", "SS", "KAD" };
			sheet = workbook.createSheet(GeneralConstants.CNF_GRP_MTRLS);
		}
		if (GeneralConstants.CNF_GRP_SALESPERSONS.equalsIgnoreCase(group)) {
			headers = new String[] { "KEY", "VALUE", "SP TYPE", "SP NAME", "SM CODE", "SM NAME", "HEAD QUARTER",
					"RM NAME", "ZM CODE", "ZM NAME" };
			sheet = workbook.createSheet(GeneralConstants.CNF_GRP_SALESPERSONS);
		}

		if (GeneralConstants.CNF_GRP_STKSTMAP.equalsIgnoreCase(group)) {
			headers = new String[] { "KEY", "VALUE", "SM CODE", "SM NAME", "ZM CODE", "ZM NAME" };
			sheet = workbook.createSheet(GeneralConstants.CNF_GRP_STKSTMAP);
		}

		Row header = sheet.createRow(0);

		CellStyle headerStyle = workbook.createCellStyle();
		Font headerFont = workbook.createFont();
		//headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headerStyle.setFont(headerFont);
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
			Cell cell = header.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerStyle);
		}

		int cnt = 1;
		for (ListOfConfigValuesTo cfgVals : listconfgroup) {

			Row row = sheet.createRow(cnt);

			// row.createCell(0).setCellValue(cfgVals.getConfigValId());
			row.createCell(0).setCellValue(cfgVals.getKey());
			row.createCell(1).setCellValue(cfgVals.getValue());

			if (!GeneralConstants.CNF_GRP_STKSTMAP.equalsIgnoreCase(group)) {
				row.createCell(2).setCellValue(cfgVals.getAddField1());
				row.createCell(3).setCellValue(cfgVals.getAddField2());
				row.createCell(4).setCellValue(cfgVals.getAddField3());

				row.createCell(5).setCellValue(cfgVals.getAddField4());
			} else {
				row.createCell(2).setCellValue(cfgVals.getAddField3());
				row.createCell(3).setCellValue(cfgVals.getAddField4());
				row.createCell(4).setCellValue(cfgVals.getAddField7());
				row.createCell(5).setCellValue(cfgVals.getAddField8());

			}

			if (GeneralConstants.CNF_GRP_SALESPERSONS.equalsIgnoreCase(group)) {
				row.createCell(6).setCellValue(cfgVals.getAddField5());
				row.createCell(7).setCellValue(cfgVals.getAddField6());
				row.createCell(8).setCellValue(cfgVals.getAddField7());
				row.createCell(9).setCellValue(cfgVals.getAddField8());
			}

			for (int i = 0; i <= 4; i++) {
				sheet.autoSizeColumn(i);
			}
			cnt++;
		}

	}

	/* Upload bulk master data */
	public static String TYPE = "text/csv";

	public GenericApiResponse generateMasterDetails(MultipartFile uploadfile, String plantId, String groupId) {
		logger.info("Entered in generateMasterDetails method ");
		GenericApiResponse apiResp = new GenericApiResponse();
		try {
			if (!uploadfile.isEmpty() && !TYPE.contains(uploadfile.getContentType())) {
				apiResp.setMessage("Invalid file format found");
				apiResp.setStatus(0);
				logger.info(" Invalid File format error");
				return apiResp;
			}
			CSVParser parser = new CSVParser(new InputStreamReader(new ByteArrayInputStream(uploadfile.getBytes())),
					CSVFormat.DEFAULT.withHeader());

			try {

				List<ListOfConfigValuesTo> list = new ArrayList<ListOfConfigValuesTo>();
				boolean validFlag = true;
				String message = null;
				long rowNumber = 0;

				for (CSVRecord record : parser) {

					ListOfConfigValuesTo configValue = new ListOfConfigValuesTo();

					String[] recordarray = record.get(0).split(";");
					// BeanUtils.copyProperties(record, configValue);
					configValue.setBpId(plantId);
					configValue.setConfigGroupId(groupId);
					rowNumber = record.getRecordNumber();

					// Validations
					if (groupId.equalsIgnoreCase("MACHINE")) {

						if (recordarray[0] != null && recordarray.length > 0 && recordarray[0].length() <= 13) {
							configValue.setKey(recordarray[0]);
						} else {
							validFlag = false;
							message = "MachineId is greaterthan 13 at row number " + rowNumber;
						}
						if (recordarray.length > 1 && recordarray[1] != null && recordarray[1].length() <= 30) {
							configValue.setValue(recordarray[1]);
						} else {
							validFlag = false;
							message = "Machine Description is greaterthan 30 at row number " + rowNumber;
						}

					}
					if (groupId.equalsIgnoreCase("MATERIAL")) {

						if (recordarray[0] != null && recordarray.length > 0 && recordarray[0].length() <= 9) {
							configValue.setKey(recordarray[0]);
						} else {
							validFlag = false;
							message = "MaterialId is greaterthan 9 at row number " + rowNumber;
						}
						if (recordarray.length > 1 && recordarray[1] != null && recordarray[1].length() <= 40) {
							configValue.setValue(recordarray[1]);
						} else {
							validFlag = false;
							message = "Material Description is greaterthan 40 at row number " + rowNumber;
						}

					}

					if (groupId.equalsIgnoreCase("MOULD")) {

						if (recordarray[0] != null && recordarray.length > 0 && recordarray[0].length() <= 12) {
							configValue.setKey(recordarray[0]);
						} else {
							validFlag = false;
							message = "MouldId is greaterthan 9 at row number " + rowNumber;
						}
						if (recordarray.length > 1 && recordarray[1] != null && recordarray[1].length() <= 30) {
							configValue.setValue(recordarray[1]);
						} else {
							validFlag = false;
							message = "Mould Description is greaterthan 30 at row number " + rowNumber;
						}

					}

					if (groupId.equalsIgnoreCase("OPERATOR")) {

						if (recordarray[0] != null && recordarray.length > 0 && recordarray[0].length() <= 9) {
							configValue.setKey(recordarray[0]);
						} else {
							validFlag = false;
							message = "OperatorId is greaterthan 9 at row number " + rowNumber;
						}
						if (recordarray.length > 1 && recordarray[1] != null && recordarray[1].length() <= 30) {
							configValue.setValue(recordarray[1]);
						} else {
							validFlag = false;
							message = "Operator Name is greaterthan 30 at row number " + rowNumber;
						}

					}

					if (groupId.equalsIgnoreCase("VEHICLE")) {

						if (recordarray[0] != null && recordarray.length > 0 && recordarray[0].length() <= 10) {
							configValue.setKey(recordarray[0]);
						} else {
							validFlag = false;
							message = "Vehicle Id is greaterthan 10 at row number " + rowNumber;
						}
						if (recordarray.length > 1 && recordarray[1] != null) {
							configValue.setValue(recordarray[1]);
						}

					}
					if (validFlag) {
						if (recordarray.length > 2 && recordarray[2] != null) {
							configValue.setAddField1(recordarray[2]);
						}
						if (recordarray.length > 3 && recordarray[3] != null) {
							configValue.setAddField2(recordarray[3]);
						}
						if (recordarray.length > 4 && recordarray[4] != null) {
							configValue.setAddField3(recordarray[4]);
						}
						if (recordarray.length > 5 && recordarray[5] != null) {
							configValue.setAddField4(recordarray[5]);
						}
						if (recordarray.length > 6 && recordarray[6] != null) {
							configValue.setAddField5(recordarray[6]);
						}
						if (recordarray.length > 7 && recordarray[7] != null) {
							configValue.setAddField6(recordarray[7]);
						}
						if (recordarray.length > 8 && recordarray[8] != null) {
							configValue.setAddField7(recordarray[8]);
						}
						if (recordarray.length > 9 && recordarray[9] != null) {
							configValue.setAddField8(recordarray[9]);
						}
						if (recordarray.length > 10 && recordarray[10] != null) {
							configValue.setAddField9(recordarray[10]);
						}
						if (recordarray.length > 11 && recordarray[11] != null) {
							configValue.setAddField10(recordarray[11]);
						}
						if (recordarray.length > 12 && recordarray[12] != null) {
							configValue.setAddField11(recordarray[12]);
						}

						if (recordarray.length > 13 && recordarray[13] != null) {
							configValue.setAddField12(recordarray[13]);
						}
						configValue.setMarkAsDelete(false);
						list.add(configValue);
					}
				}
				if (validFlag) {
					String retMessage = globalDataDao.createOrUpdateConfigValues(list,
							userContext.getLoggedUser().getUserId());

					if (retMessage != null) {

						apiResp.setStatus(0);

						if (retMessage.equals("SUCCESS_CREATE")) {

							apiResp.setMessage(prop.getProperty("GD_005"));

						} else if (retMessage.equals("SUCCESS_UPDATE")) {

							apiResp.setMessage(prop.getProperty("GD_009"));

						} else if (retMessage.equals("EXISTED")) {

							apiResp.setStatus(1);
							apiResp.setErrorcode("GD_008");
							apiResp.setMessage(prop.getProperty("GD_008"));
						}

					} else {

						apiResp.setMessage(prop.getProperty("GD_007"));
						apiResp.setErrorcode("GD_007");
					}
				} else {

					apiResp.setMessage(message);
					apiResp.setErrorcode("GD_007");
				}
			} catch (Exception e) {
				logger.error("Exception :" + ExceptionUtils.getStackTrace(e));
			} finally {
				parser.close();
			}

		} catch (IOException e) {
			logger.error("Exception :" + ExceptionUtils.getStackTrace(e));
		}
		return apiResp;
	}

	@Override
	public GenericApiResponse getMasterData(String masterType, Map<String, Object> filter) {

		String METHOD_NAME = "getMasterData";

		logger.info("Entered into the method: " + METHOD_NAME);

		GenericApiResponse apiResp = new GenericApiResponse();
		LoggedUser loggedUser = userContext.getLoggedUser();

		try {
			if (MasterData.valueOf(masterType) != null) {
				GdMasterResponse gdMasterResponse = new GdMasterResponse();
				apiResp.setStatus(0);
				gdMasterResponse.setData(globalDataDao.getMasterData(MasterData.valueOf(masterType).getValue(), loggedUser, filter, masterType));
				Map<String, Object> params = new HashMap<>();
				params.put("stype", com.portal.constants.GeneralConstants.SettingType.APP_SETTING.getValue());
				params.put("grps", Arrays.asList(GeneralConstants.MASTERDATA));
				Map<String, GdSettingConfigsTo> configsTos = globalDataDao.getGdConfigDetailsMap(params);
				String displayClm = configsTos.get(GeneralConstants.MASTERDATA_CLMDS).getSettingDefaultValue();
				JSONObject displayObj = new JSONObject(new String (Base64.getDecoder().decode(displayClm.getBytes())));
				if(displayObj.has(masterType)){
					gdMasterResponse.setDisplayColumns(Base64.getEncoder().encodeToString(displayObj.getJSONArray(masterType).toString().getBytes()));
				} else {
					gdMasterResponse.setDisplayColumns(Base64.getEncoder().encodeToString("{}".getBytes()));
				}
				gdMasterResponse.setKeyColumn(MasterDataUniqueIds.valueOf(masterType).getValue());
				apiResp.setData(gdMasterResponse);
			} else {
				apiResp.setStatus(1);
			}

		} catch (Exception e) {

			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");

			logger.error("Error while getting regional centers: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return apiResp;
	}

	/**
	 * Get SMTP Configuration Details
	 *
	 * @return
	 */
	public GenericApiResponse getSmtpConfigDetails() {

		String METHOD_NAME = "getSmtpConfigDetails";

		logger.info("Entered into the method: " + METHOD_NAME);

		GenericApiResponse apiResp = new GenericApiResponse();

		try {
			Map<String, Object> params = new HashMap<>();
			params.put("stype", com.portal.constants.GeneralConstants.SettingType.APP_SETTING.getValue());
			params.put("grps", Arrays.asList(GeneralConstants.EMAIL_SET_GP, GeneralConstants.ENV_SET_GP));
			List<GdSettingConfigsTo> listconfgroup = globalDataDao.getGdConfigDetails(params);

			if (listconfgroup != null && !listconfgroup.isEmpty()) {
				ListOfSmtpConfigGroup listOfSmtpConfigGroup = new ListOfSmtpConfigGroup();

				listOfSmtpConfigGroup.setConfigGroups(listconfgroup);

				apiResp.setStatus(0);
				apiResp.setData(listOfSmtpConfigGroup);

			} else {

				apiResp.setMessage(prop.getProperty("GD_003"));
				apiResp.setErrorcode("GD_003");
			}

		} catch (Exception e) {

			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");

			logger.error("Error while getting config group details : " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return apiResp;
	}

	/**
	 * Update SMTP Configuration Details
	 *
	 * @return
	 */
	public GenericApiResponse updateSmtpConfigDetails(GdSettingConfigsTo smtpConfigsTo) {

		String METHOD_NAME = "updateSmtpConfigDetails";

		logger.info("Entered into the method: " + METHOD_NAME);

		GenericApiResponse apiResp = new GenericApiResponse();

		try {
			Integer loginId = userContext.getLoggedUser().getUserId();

			boolean updated = globalDataDao.updateSmtpConfigDetails(smtpConfigsTo, loginId);
			if (updated) {
				apiResp.setStatus(0);
				apiResp.setMessage("Updated successfully!");
			} else {
				apiResp.setStatus(1);
				apiResp.setMessage("Updated failed!");
			}
		} catch (Exception e) {

			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");

			logger.error("Error while updating config group details : " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return apiResp;
	}

	@Override
	public GenericApiResponse sendTestMail(String toMails, String ccMails, String bccMails, String org_id) {
		String METHOD_NAME = "sendTestMail";

		logger.info("Entered into the method: " + METHOD_NAME);

		GenericApiResponse apiResp = new GenericApiResponse();

		try {
			Integer loginId = userContext.getLoggedUser().getUserId();
			Map<String, Object> params = new HashMap<>();
			params.put("stype", SettingType.APP_SETTING.getValue());
			params.put("grps", Arrays.asList(GeneralConstants.EMAIL_SET_GP, GeneralConstants.ENV_SET_GP));
			SettingTo settingTo = settingDao.getSMTPSettingValues(params);
			Map<String, String> emailConfigs = settingTo.getSettings();
			String toEmailIds = "", fromEmailId = "";
			EmailsTo emailsTo = new EmailsTo();

			toEmailIds = toMails;

			fromEmailId = emailConfigs.get("EMAIL_FROM");

			if (ccMails != null && ccMails.length() > 0) {
				String cc = ccMails;
				if (cc.contains(",")) {
					String[] cca = cc.split(",");
					emailsTo.setCc(cca);
				} else {
					String[] cca = { cc };
					emailsTo.setCc(cca);
				}
			}
			if (bccMails != null && bccMails.length() > 0) {
				String bcc = bccMails;
				if (bcc.contains(",")) {
					String[] bcca = bcc.split(",");
					emailsTo.setBcc(bcca);
				} else {
					String[] bcca = { bcc };
					emailsTo.setBcc(bcca);
				}
			}
			emailsTo.setFrom(fromEmailId);
			emailsTo.setTo(toEmailIds);
			emailsTo.setLoginId("" + loginId);
			emailsTo.setOrgId(org_id);
			emailsTo.setTemplateName("TEST_MAIL");
			sendService.sendCommunicationMail(emailsTo, emailConfigs);
			apiResp.setData(emailsTo.getEmailLogTo());
			apiResp.setStatus(0);
			apiResp.setMessage("Mail Triggered!");
		} catch (Exception e) {
			apiResp.setMessage("Mail sending failure!");
			apiResp.setErrorcode("GEN_002");

			logger.error("Error while sending test mail : " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return apiResp;
	}

	private void sendNotification(Map<String, String> reqPayload) {
		String METHOD_NAME = "sendNotification";

		logger.info("Entered into the method: " + METHOD_NAME);

		GenericApiResponse apiResp = new GenericApiResponse();
		try {
			LoggedUser loggedUser = userContext.getLoggedUser();
			Notifications notificationCommonModal = new Notifications();
			notificationCommonModal.setUserId(loggedUser.getUserId());
			notificationCommonModal.setUserName(loggedUser.getFirstName() + " " + loggedUser.getLastName());
			notificationCommonModal.setNotificationMessage(reqPayload.get("note"));
			notificationCommonModal.setObjectRefId(reqPayload.get("objectRefId"));
			notificationCommonModal.setActionStatus(reqPayload.get("actionStatus"));
			notificationCommonModal.setType(reqPayload.get("type"));
			notificationCommonModal.setOrgId(commonService.getRequestHeaders().getOrgId());
			notificationDaoImpl.createNotifications(notificationCommonModal);
		} catch (Exception e) {
			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");

			logger.error("Error while sending notification: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);
		
	}

	@SuppressWarnings("unchecked")
	public GenericApiResponse createOrUpdateMasterdata(MasterDataUpdateRequest masterDataUpdateRequest) {
		GenericApiResponse genericApiResponse = new GenericApiResponse();
		try {
			String finalQuery = "";
			HashMap<String, Object> data = new ObjectMapper().readValue(
					new String(Base64.getDecoder().decode(masterDataUpdateRequest.getRequestData())), HashMap.class);
			if (GeneralConstants.CREATE_ACTION.equalsIgnoreCase(masterDataUpdateRequest.getAction())) {
				finalQuery = insertData(data, MasterData.valueOf(masterDataUpdateRequest.getMasterType()).getValue(), masterDataUpdateRequest.getMasterType());
			} else if (GeneralConstants.UPDATE_ACTION.equalsIgnoreCase(masterDataUpdateRequest.getAction())) {
				finalQuery = updateData(data, MasterData.valueOf(masterDataUpdateRequest.getMasterType()).getValue(), masterDataUpdateRequest.getMasterType());
			} else if (GeneralConstants.DELETE_ACTION.equalsIgnoreCase(masterDataUpdateRequest.getAction())) {
				// verify dependency check
				/*if (verifyMasterDataDependency(data,
						MasterData.valueOf(masterDataUpdateRequest.getMasterType()).getValue(),
						masterDataUpdateRequest.getMasterType())) {
					genericApiResponse.setStatus(1);
					genericApiResponse.setMessage(GeneralConstants.ERROR);
					genericApiResponse.setMessage(
							"Unable to process your request, checklist masters or  linked with the given master data");
					return genericApiResponse;
				}*/
				finalQuery = deleteData(data, MasterData.valueOf(masterDataUpdateRequest.getMasterType()).getValue(), masterDataUpdateRequest.getMasterType());
			}
			if(finalQuery == null){
				genericApiResponse.setStatus(1);
				genericApiResponse.setMessage("Unable to process your request, please contact your administrator");
				return genericApiResponse;
			}
			if(!finalQuery.isEmpty()){
				try{
				baseDao.updateBySQLQuery(finalQuery, new Object[]{});
				}catch(ConstraintViolationException cva){
					genericApiResponse.setStatus(1);
					genericApiResponse.setMessage(GeneralConstants.ERROR);
					genericApiResponse.setMessage("The given data is already exists in the master or invalid data");
					return genericApiResponse;
				}
			} else {
				genericApiResponse.setStatus(1);
				genericApiResponse.setMessage(GeneralConstants.ERROR);
				genericApiResponse.setMessage("The given data is already exists in the master or invalid data");
				return genericApiResponse;
			}
			genericApiResponse.setStatus(0);
			genericApiResponse.setMessage(GeneralConstants.SUCCESS);
			return genericApiResponse;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return genericApiResponse;
	}
	
	public String insertData(HashMap<String, Object> dataMap, String tableName, String masterType) {
		if(checkForDuplicateMaster(dataMap, tableName, masterType)){
			return "";
		}
		LoggedUser loggedUser = userContext.getLoggedUser();
		String sql = "INSERT INTO " + tableName + " ";
		String columnStr = "";
		String valuesStr = "";
		Iterator<Entry<String, Object>> it = dataMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			if("oldPK".equalsIgnoreCase((String)pairs.getKey()))
				continue;
			
			if (columnStr.isEmpty()) {
				columnStr = columnStr + pairs.getKey();
			} else {
				columnStr = columnStr + "," + pairs.getKey();
			}

			if (valuesStr.isEmpty()) {
				valuesStr = valuesStr + "'" + pairs.getValue() + "'";
			} else {
				valuesStr = valuesStr + ",'" + pairs.getValue() + "'";
			}
		}
		columnStr = columnStr + ", MARK_AS_DELETE";
		columnStr = columnStr + ", CREATED_BY";
		columnStr = columnStr + ", CREATED_TS";
		valuesStr = valuesStr + "," + false + "";
		valuesStr = valuesStr + "," + loggedUser.getUserId() + "";
		valuesStr = valuesStr + ", '" + CommonUtils.dateFormatter(new Date(), "yyyy-MM-dd HH:mm:ss") + "'";
		sql = sql + " ( " + columnStr + " )" + " VALUES (" + valuesStr + ")";
		sql += ";";
		System.out.println(sql);
		return sql;
	}
	
	public boolean checkForDuplicateMaster(HashMap<String, Object> dataMap, String tableName, String masterType){
		String keyClm = "";
		String keyvl = "";
		String ignoreSameObjCheck = "";
		Iterator<Entry<String, Object>> it = dataMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			if("oldPK".equalsIgnoreCase((String)pairs.getKey()))
				continue;
			if (MasterDataDuplicateCheckUniqueIds.valueOf(masterType).getValue().equalsIgnoreCase((String) pairs.getKey())) {
				keyClm = (String) pairs.getKey();
				keyvl = (String)pairs.getValue();
			}
			if(dataMap.containsKey(MasterDataUniqueIds.valueOf(masterType).getValue())){
				ignoreSameObjCheck = " and " +MasterDataUniqueIds.valueOf(masterType).getValue() +" != '"+ dataMap.get(MasterDataUniqueIds.valueOf(masterType).getValue())+"' ";
			}
		}
		return baseDao.findBySQLQueryWithIndexedParams("select * from "+ tableName + " where  upper("+ keyClm + ") = upper ('" + keyvl + "') "+ignoreSameObjCheck  , new Object[]{}).isEmpty() ? false : true;
	}
	
	public String updateData(HashMap<String, Object> dataMap, String tableName, String masterType) {
		GenericApiResponse genericApiResponse = new GenericApiResponse();
		if(checkForDuplicateMaster(dataMap, tableName, masterType)){
			return "";
		}
		LoggedUser loggedUser = userContext.getLoggedUser();
		String sql = "UPDATE " + tableName + " ";
		String valuesStr = "";
		Object id = null;
		Object oldPK = dataMap.get("oldPK");
		if(!dataMap.containsKey(MasterDataUniqueIds.valueOf(masterType).getValue())){
			return null;
		}
		Iterator<Entry<String, Object>> it = dataMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			if("oldPK".equalsIgnoreCase((String)pairs.getKey()))
				continue;
			if (!MasterDataUniqueIds.valueOf(masterType).getValue().equalsIgnoreCase((String) pairs.getKey())) {
				if (valuesStr.isEmpty()) {
					valuesStr = valuesStr + pairs.getKey() + " = '" + pairs.getValue() + "'";
				} else {
					valuesStr = valuesStr + "," + pairs.getKey() + " = '" + pairs.getValue() + "'";
				}
			} else {
				id = pairs.getValue();
			}
		}
		if(valuesStr.isEmpty())
			valuesStr = valuesStr + " MARK_AS_DELETE = " + false + "";
		else
			valuesStr = valuesStr + ", MARK_AS_DELETE = " + false + "";
		valuesStr = valuesStr + ", CHANGED_BY = " + loggedUser.getUserId() + "";
		valuesStr = valuesStr + ", CHANGED_TS = '" + CommonUtils.dateFormatter(new Date(), "yyyy-MM-dd HH:mm:ss") + "'";
		if(oldPK != null ){
			valuesStr = valuesStr + ", " +MasterDataUniqueIds.valueOf(masterType).getValue()+ " = '" + id + "'";
			id = oldPK;
		}
		sql = sql + "SET " + valuesStr +" where "+MasterDataUniqueIds.valueOf(masterType).getValue()+" = '"+id+"'";
		sql += ";";
		System.out.println(sql);
		return sql;
	}
	
	public String deleteData(HashMap<String, Object> dataMap, String tableName, String masterType) {
		String sql = "DELETE FROM " + tableName + " ";
		if (!dataMap.containsKey(MasterDataUniqueIds.valueOf(masterType).getValue())) {
			return null;
		}
		try{
			sql = sql + " where "+MasterDataUniqueIds.valueOf(masterType).getValue()+" = " + (Integer)dataMap.get(MasterDataUniqueIds.valueOf(masterType).getValue())+"";
		}catch(Exception e){
			sql = sql + " where "+MasterDataUniqueIds.valueOf(masterType).getValue()+" = '" + dataMap.get(MasterDataUniqueIds.valueOf(masterType).getValue())+"'";
		}
		sql += ";";
		System.out.println(sql);
		return sql;
	}
	
	
	public static void main(String a[]){
		GlobalDataServiceImpl gds = new GlobalDataServiceImpl();
		MasterDataUpdateRequest masterDataUpdateRequest = new MasterDataUpdateRequest();
		masterDataUpdateRequest.setMasterType("REGION");
		masterDataUpdateRequest.setAction("UPDATE_ACTION");
		masterDataUpdateRequest.setRequestData("eyJSRUdJT05fTkFNRSI6IlRlc3QifQ==");
		gds.createOrUpdateMasterdata(masterDataUpdateRequest);
	}

	@SuppressWarnings("unchecked")
	public GenericApiResponse getSettingsData(List<String> sGroup){
		GenericApiResponse genericApiResponse = new GenericApiResponse();
		genericApiResponse.setStatus(0);
		List<GdSettingsDetails> settingsDetails = new ArrayList<>();
		String query = "from GdSettingsDefinitions where markAsDelete = false and settingGroupName in (:settingGroupName)";
		Map<String, Object> params = new HashMap<>();
		params.put("settingGroupName",sGroup);
		List<GdSettingsDefinitions> gdSettingsDefinitions = (List<GdSettingsDefinitions>) baseDao.findByHQLQueryWithNamedParams(query, params);
		GdSettingsDetails gdSettingsDetails = new GdSettingsDetails();
		for(GdSettingsDefinitions gsd : gdSettingsDefinitions){
			gdSettingsDetails = new GdSettingsDetails();
			BeanUtils.copyProperties(gsd, gdSettingsDetails);
			gdSettingsDetails.setSettingType(gsd.getGdSettingTypes().getSettingTypeId());
			settingsDetails.add(gdSettingsDetails);
		}
		genericApiResponse.setData(settingsDetails);
		return genericApiResponse;
	}

	public GenericApiResponse createOrUpdateSettingsData(List<GdSettingsDetails> gdSettingsDetails) {
		GenericApiResponse genericApiResponse = new GenericApiResponse();
		genericApiResponse.setStatus(0);
		LoggedUser loggedUser = loggedUserContext.getLoggedUser();
		List<Integer> settingIds = gdSettingsDetails.stream().filter(sd -> sd.getSettingId() != null)
				.map(GdSettingsDetails::getSettingId).collect(Collectors.toList());
		List<GdSettingsDefinitions> gdSettingsDefinitions = settingRepo.getSettingsBySettingIds(settingIds);
		Map<Integer, GdSettingsDefinitions> settingIdVsSettingDetails = gdSettingsDefinitions.stream().collect(
				Collectors.toMap(GdSettingsDefinitions::getSettingId, settingsDefinition -> settingsDefinition));
		List<GdSettingsDetails> settingsDetails = new ArrayList<>();
		for (GdSettingsDetails gsd : gdSettingsDetails) {
			GdSettingsDefinitions tmp = settingIdVsSettingDetails.containsKey(gsd.getSettingId())
					? settingIdVsSettingDetails.get(gsd.getSettingId()) : new GdSettingsDefinitions();
			BeanUtils.copyProperties(gsd, tmp);
			if (gsd.getSettingId() != null) {
				tmp.setChangedBy(loggedUser.getUserId());
				tmp.setChangedTs(new Date());
			} else {
				tmp.setCreatedBy(loggedUser.getUserId());
				tmp.setCreatedTs(new Date());
			}
			List<GdSettingsDefinitions> extGdSettingsDefinitions = settingRepo
					.getSettingsByShortIdGroup(tmp.getSettingGroupName().trim(), tmp.getSettingShortId().trim());
			if (!extGdSettingsDefinitions.isEmpty()
					&& extGdSettingsDefinitions.get(0).getSettingId() != tmp.getSettingId()) {
				genericApiResponse.setStatus(1);
				genericApiResponse
						.setMessage("Unable to process the request, the given setting & group already available");
				return genericApiResponse;
			}
			GdSettingTypes gdSettingTypes = new GdSettingTypes();
			gdSettingTypes.setSettingTypeId(gsd.getSettingType());
			tmp.setGdSettingTypes(gdSettingTypes);
			settingRepo.save(tmp);
		}
		genericApiResponse.setData(settingsDetails);
		return genericApiResponse;
	}
	
	public GenericApiResponse deleteSettingData(List<Integer> settingIds) {
		GenericApiResponse genericApiResponse = new GenericApiResponse();
		genericApiResponse.setStatus(0);
		try {
			for (Integer settingId : settingIds) {
				GdSettingsDefinitions extObj = (GdSettingsDefinitions) baseDao.findByPK(GdSettingsDefinitions.class,
						settingId);
				extObj.setMarkAsDelete(true);
				settingRepo.save(extObj);
			}
		} catch (Exception e) {
			genericApiResponse.setStatus(1);
			genericApiResponse.setMessage("Unable to process your request, please contact your administrator");
			logger.error("Unable to process your request, please contact your administrator, "+ e.getMessage());
		}
		return genericApiResponse;
	}

	@Override
	public GenericApiResponse getGdStates() {
		String METHOD_NAME = "getGdStates";

		logger.info("Entered into the method: " + METHOD_NAME);
		GenericApiResponse apiResp = new GenericApiResponse();
		try {
				List<GdState> gdState = globalDataDao.getGdStates();
				if(gdState != null) {
					apiResp.setData(gdState);
					apiResp.setStatus(0);
				}else {
					apiResp.setStatus(0);
					apiResp.setMessage(prop.getProperty("GEN_002"));
				}
		} catch (Exception e) {
			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");

			logger.error("Error while getting states details: " + ExceptionUtils.getStackTrace(e));
		}
		return apiResp;
	}

	@Override
	public GenericApiResponse getGdCity() {
		String METHOD_NAME = "getGdCity";

		logger.info("Entered into the method: " + METHOD_NAME);
		GenericApiResponse apiResp = new GenericApiResponse();
		try {
				List<GdCity> gdCity = globalDataDao.getGdCity();
				if(gdCity != null) {
					apiResp.setData(gdCity);
					apiResp.setStatus(0);
				}else {
					apiResp.setStatus(0);
					apiResp.setMessage(prop.getProperty("GEN_002"));
				}
		} catch (Exception e) {
			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");

			logger.error("Error while getting city details: " + ExceptionUtils.getStackTrace(e));
		}
		return apiResp;
	}

	@SuppressWarnings({ "unused", "unchecked" })
	@Override
	public GenericApiResponse getTemplateHeadersSettings() {
		String METHOD_NAME = "getMasterData";

		logger.info("Entered into the method: " + METHOD_NAME);

		GenericApiResponse apiResp = new GenericApiResponse();
		LoggedUser loggedUser = userContext.getLoggedUser();

		try {
			GenericApiResponse gdMasterResponse = new GenericApiResponse();
			apiResp.setStatus(0);
			Map<String, Object> params = new HashMap<>();
			params.put("stype", com.portal.constants.GeneralConstants.SettingType.APP_SETTING.getValue());
			params.put("grps", Arrays.asList(GeneralConstants.TEMPLATESDATA));
			Map<String, GdSettingConfigsTo> configsTos = globalDataDao.getGdConfigDetailsMap(params);
			String displayClm = configsTos.get(GeneralConstants.TEMPLATES_HEADERS).getSettingDefaultValue();
			byte[] decodedBytes = Base64.getDecoder().decode(displayClm);
			String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Map<String, String>> resultMap = objectMapper.readValue(decodedString,
					new com.fasterxml.jackson.core.type.TypeReference<Map<String, Map<String, String>>>() {
					});
			apiResp.setData(resultMap);

		} catch (Exception e) {

			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");

			logger.error("Error while getting regional centers: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return apiResp;
	}

	@SuppressWarnings({ "unchecked", "resource" })
	@Override
	public GenericApiResponse genrateExcelForHeaders(ReportsRequest payload, HttpServletResponse response,
			LoggedUser loggedUser) {
		GenericApiResponse apiResp = new GenericApiResponse();
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet(payload.getMasterDataId());
			Map<String, Object> headerData = payload.getHeadersData();
			Map<String, String> headerFileds = (Map<String, String>) headerData.get(payload.getMasterDataId());
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerCellStyle.setLocked(true);
			// Create header cells
			Row headerRow = sheet.createRow(0);
			int colIndex = 0;
			for (String key : headerFileds.values()) {
				Cell headerCell = headerRow.createCell(colIndex);
				headerCell.setCellValue(key);
				headerCell.setCellStyle(headerCellStyle);
				colIndex++;
			}
			try (FileOutputStream fileOut = new FileOutputStream("E:\\headers.xlsx")) {
				workbook.write(fileOut);
			} catch (IOException e) {
				e.printStackTrace();
			}
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			workbook.write(byteArrayOutputStream);
			apiResp.setData(byteArrayOutputStream.toByteArray());
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiResp;
	}

	@SuppressWarnings("resource")
	@Override
	public GenericApiResponse uploadMasterData(String type, String action, HttpServletRequest request) {
		String finalQuery = "";
		String tableName = MasterData.valueOf(type).getValue();
		GenericApiResponse apiResp = new GenericApiResponse();
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("stype", com.portal.constants.GeneralConstants.SettingType.APP_SETTING.getValue());
			params.put("grps", Arrays.asList(GeneralConstants.TEMPLATESDATA));
			Map<String, GdSettingConfigsTo> configsTos = globalDataDao.getGdConfigDetailsMap(params);
			String displayClm = configsTos.get(GeneralConstants.TEMPLATES_HEADERS).getSettingDefaultValue();
			byte[] decodedBytes = Base64.getDecoder().decode(displayClm);
			String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Map<String, String>> resultMap = objectMapper.readValue(decodedString,
					new com.fasterxml.jackson.core.type.TypeReference<Map<String, Map<String, String>>>() {
					});
			Map<String, String> fields = resultMap.get(type);
			List<String> expectedHeaders = new ArrayList<>(fields.values());
			List<String> expectedFields = new ArrayList<>(fields.keySet());
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
			MultipartFile uploadfile = multipartHttpServletRequest.getFile("file");
			if ("UPLOAD".equalsIgnoreCase(action)) {
				Workbook workbook = null;
				try {
					workbook = new XSSFWorkbook(uploadfile.getInputStream());
				} catch (Exception e) {
					workbook = new HSSFWorkbook(uploadfile.getInputStream());
				}
				Sheet sheet = workbook.getSheetAt(0);
				Row headerRow = sheet.getRow(0);
				if (headerRow != null) {
					for (int colIndex = 0; colIndex < headerRow.getPhysicalNumberOfCells(); colIndex++) {
						Cell headerCell = headerRow.getCell(colIndex);
						String actualHeader = headerCell.toString().trim();
						String expectedHeader = expectedHeaders.get(colIndex);

						if (!actualHeader.equals(expectedHeader)) {
							// header doesn't match the expected value
							apiResp.setStatus(1);
							apiResp.setMessage("Headers doesn't match, Please download the template");
							return apiResp;
						}
					}
				} else {
					apiResp.setStatus(1);
					apiResp.setMessage("Header row not found");
					return apiResp;
				}
				LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
				Iterator<Row> rowIterator = sheet.iterator();
				if (rowIterator.hasNext()) {
					rowIterator.next();
				}
				if(!rowIterator.hasNext()) {
					apiResp.setStatus(1);
					apiResp.setMessage("Empty sheet");
				}
				while (rowIterator.hasNext()) {
					Row row = rowIterator.next();
					for (int colIndex = 0; colIndex < row.getPhysicalNumberOfCells(); colIndex++) {
						Cell cell = row.getCell(colIndex);
						String sCell = new String();
						if(cell != null) {
							if (cell.getCellType() == CellType.NUMERIC && cell.getNumericCellValue() == (int) cell.getNumericCellValue()) {
								 sCell = String.valueOf((int) cell.getNumericCellValue());
							}else {
								sCell = cell.toString().trim();
							}
						}
						String key = expectedFields.get(colIndex);
						data.put(key, sCell);
					}
					finalQuery = this.insertData(data, tableName, type);
					if (finalQuery == null) {
						apiResp.setStatus(1);
						apiResp.setMessage("Unable to process your request, please contact your administrator");
						return apiResp;
					}
					if (!finalQuery.isEmpty()) {
						try {
							baseDao.updateBySQLQuery(finalQuery, new Object[] {});
							apiResp.setStatus(0);
							apiResp.setMessage(GeneralConstants.SUCCESS);
						} catch (ConstraintViolationException cva) {
							apiResp.setStatus(1);
							apiResp.setMessage(GeneralConstants.ERROR);
							apiResp.setMessage("The given data is already exists in the master or invalid data");
//								return apiResp;
						}
					} else {
						apiResp.setStatus(1);
						apiResp.setMessage(GeneralConstants.ERROR);
						apiResp.setMessage("The given data is already exists in the master or invalid data");
//						return apiResp;
					}
				}
				workbook.close();
				return apiResp;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiResp;
	}

	@Override
	public GenericApiResponse getBookingUnits() {
		String METHOD_NAME = "getBookingUnits";

		logger.info("Entered into the method: " + METHOD_NAME);
		GenericApiResponse apiResp = new GenericApiResponse();
		try {
				List<BookingUnits> bookingUnits = globalDataDao.getBookingUnits();
				if(bookingUnits != null) {
					apiResp.setData(bookingUnits);
					apiResp.setStatus(0);
				}else {
					apiResp.setStatus(0);
					apiResp.setMessage(prop.getProperty("GEN_002"));
				}
		} catch (Exception e) {
			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");

			logger.error("Error while getting Booking Unit details: " + ExceptionUtils.getStackTrace(e));
		}
		return apiResp;
	}

	@Override
	public GenericApiResponse getClfRatesList() {
		String METHOD_NAME = "getClfRatesList";

		logger.info("Entered into the method: " + METHOD_NAME);
		GenericApiResponse apiResp = new GenericApiResponse();
		List<ClfRatesModel> clfRatesModelList = new ArrayList<ClfRatesModel>();
		try {
			List<Object[]> clfRates = globalDataDao.getClfRatesList();
			if (clfRates != null) {
				for (Object[] obj : clfRates) {
					ClfRatesModel clfRatesModel = new ClfRatesModel();
					clfRatesModel.setClassifiedAdsType(((Short) obj[0]).intValue());
					clfRatesModel.setClassifiedAdsSubtype(((Short) obj[1]).intValue());
					clfRatesModel.setEditionId(((Short) obj[2]).intValue());
					clfRatesModel.setRate(new Double((Float) obj[3]));
					clfRatesModel.setExtraLineAmount(new Double((Float) obj[4]));
					clfRatesModel.setMinLines(((Short) obj[5]).intValue());
					clfRatesModel.setCharCountPerLine(((Short) obj[6]).intValue());
					clfRatesModel.setClassifiedAdsTypeStr((String) obj[7]);
					clfRatesModel.setClassifiedAdsSubtypeStr((String) obj[8]);
					clfRatesModel.setEditionStr((String) obj[9]);
					clfRatesModel.setRateId((String) obj[10]);
					clfRatesModelList.add(clfRatesModel);
				}
				apiResp.setData(clfRatesModelList);
				apiResp.setStatus(1);
			} else {
				apiResp.setStatus(0);
				apiResp.setMessage(prop.getProperty("GEN_002"));
			}
		} catch (Exception e) {
			apiResp.setMessage(prop.getProperty("GEN_002"));
			apiResp.setErrorcode("GEN_002");

			logger.error("Error while getting Clf Rates details: " + ExceptionUtils.getStackTrace(e));
		}
		return apiResp;
	}

	@Override
	public GenericApiResponse addClfRates(ClfRatesModel payload) {
		GenericApiResponse genericApiResponse = new GenericApiResponse();
		genericApiResponse.setStatus(0);
		genericApiResponse.setMessage("Successfully added");
		// check duplicate rate card
		List<ClfRates> clfRates = clfRatesRepo.getClfRates(payload.getClassifiedAdsType(),
				payload.getClassifiedAdsSubtype(), payload.getEditionId());
		if (!clfRates.isEmpty()) {
			if ("add".equalsIgnoreCase(payload.getModalType())) {
				genericApiResponse.setMessage("Already Master is available, Please edit the Master");
				genericApiResponse.setStatus(1);
				return genericApiResponse;
			} else {
				ClfRates clf = clfRates.get(0);
				clf.setClassifiedAdsType(payload.getClassifiedAdsType());
				clf.setClassifiedAdsSubtype(payload.getClassifiedAdsSubtype());
				clf.setEditionId(payload.getEditionId());
				clf.setRate(payload.getRate());
				clf.setExtraLineAmount(payload.getExtraLineAmount());
				clf.setMinLines(payload.getMinLines());
				clf.setCharCountPerLine(payload.getCharCountPerLine());
				clf.setChangedBy(userContext.getLoggedUser().getUserId());
				clf.setChangedTs(new Date());
				clf.setMarkAsDelete(false);
				baseDao.saveOrUpdate(clf);
			}
		} else {
			ClfRates clf = new ClfRates();
			clf.setRateId(UUID.randomUUID().toString());
			clf.setClassifiedAdsType(payload.getClassifiedAdsType());
			clf.setClassifiedAdsSubtype(payload.getClassifiedAdsSubtype());
			clf.setEditionId(payload.getEditionId());
			clf.setRate(payload.getRate());
			clf.setExtraLineAmount(payload.getExtraLineAmount());
			clf.setMinLines(payload.getMinLines());
			clf.setCharCountPerLine(payload.getCharCountPerLine());
			clf.setCreatedBy(userContext.getLoggedUser().getUserId());
			clf.setCreatedTs(new Date());
			clf.setMarkAsDelete(false);
			clfRatesRepo.save(clf);
		}
		return genericApiResponse;
	}

	@Override
	public GenericApiResponse DeleteClfRates(ClfRatesModel payload) {
		GenericApiResponse genericApiResponse = new GenericApiResponse();
		genericApiResponse.setStatus(0);
		genericApiResponse.setMessage("Successfully Deleted");
		if (payload.getRateId() != null && !payload.getRateId().isEmpty()) {
			ClfRates clf = (ClfRates) baseDao.findByPK(ClfRates.class, payload.getRateId());
			if (clf != null) {
				clf.setMarkAsDelete(true);
				clf.setChangedBy(userContext.getLoggedUser().getUserId());
				clf.setChangedTs(new Date());
				clfRatesRepo.save(clf);
			}
		} else {
			genericApiResponse.setStatus(1);
			genericApiResponse.setMessage("Something went wrong, please contact administrator");
		}
		return genericApiResponse;
	}
}
