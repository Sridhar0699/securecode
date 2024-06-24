
package com.portal.user.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.portal.basedao.IBaseDao;
import com.portal.constants.GeneralConstants;
import com.portal.gd.entities.GdUserTypes;
import com.portal.org.entities.OmOrgBusinessPartners;
import com.portal.org.entities.OmOrganizations;
import com.portal.org.to.BusinessPartnerTo;
import com.portal.org.to.ListOfOrganizationsTo;
import com.portal.org.to.OrganizationTo;
import com.portal.repository.UmCustomersRepo;
import com.portal.repository.UmOrgRolesRepo;
import com.portal.repository.UmOrgUsersRepository;
import com.portal.repository.UmUserPwdResetRepository;
import com.portal.repository.UmUsersRepository;
import com.portal.rms.entity.UserLoginHistory;
import com.portal.rms.repository.UserLoginHistoryRepo;
import com.portal.security.repo.LoginHistoryRepository;
import com.portal.setting.dao.SettingDao;
import com.portal.setting.to.SettingTo;
import com.portal.setting.to.SettingTo.SettingType;
import com.portal.user.entities.UmCustomers;
import com.portal.user.entities.UmOrgRoles;
import com.portal.user.entities.UmOrgUsers;
import com.portal.user.entities.UmUserPwdReset;
import com.portal.user.entities.UmUsers;
import com.portal.user.to.UserTo;


/**
 * User DAO implementation
 * 
 * @author Sathish Babu D
 *
 */
@Service("userDao")
@PropertySource(value = { "classpath:/com/portal/queries/user_db.properties" })
public class UserDaoImpl implements UserDao {

	private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);

	@Autowired
	private UmUsersRepository umUsersRepository;
	
	@Autowired
	private UmOrgUsersRepository umOrgUsersRepository;
	
	@Autowired
	private UmUserPwdResetRepository umUserPwdResetRepository;
	
	@Autowired
	private LoginHistoryRepository loginHistoryRepository;
	
	@Autowired
	private UmOrgRolesRepo umOrgRolesRepo;
	
	@Autowired
	private UmCustomersRepo umCustomersRepo;

	@Autowired(required = true)
	private Environment prop;

	@Autowired(required = true)
	private SettingDao settingDao;

	@Autowired(required = true)
	private HttpServletRequest request;
	
	@Autowired(required = true)
	private IBaseDao baseDao;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private UserLoginHistoryRepo userLoginHistoryRepo;
	

	/**
	 * Find by logon id
	 * 
	 * @param Class,String
	 * 
	 * @return the BaseEntity
	 */
	@SuppressWarnings("unchecked")
	public UserTo findByLogonId(String logonId) {

		String METHOD_NAME = "findByLogonId";

		logger.info("Entered into the method: " + METHOD_NAME);

		UserTo user = null;

		try {
			/*Map<String, Object> params = new HashMap<>();
			params.put("logonId", logonId);
			params.put("markAsDelete", false);
			List<UmUsers> users = (List<UmUsers>) baseDao.findByHQLQueryWithNamedParams(
					prop.getProperty("GET_USER_LOGONID"), params);*/
			
			List<UmUsers> users = umUsersRepository.getUserLoginId(logonId, false);

			if (users != null && !users.isEmpty()) {

				UmUsers umUsers = users.get(0);
				user = new UserTo();
				BeanUtils.copyProperties(umUsers, user);
				GdUserTypes userTypes = umUsers.getGdUserTypes();

				user.setUserType(userTypes.getTypeShortId());
				user.setUserTypeId(userTypes.getUserTypeId());
				user.setUserLocked(umUsers.getUserLocked());
				user.setPwd(umUsers.getPassword());
				user.setCreated_ts(umUsers.getCreatedTs());

				List<String> bpIds = new ArrayList<String>();
				List<UmOrgUsers> umOrgUsers	= umOrgUsersRepository.getUmOrgDetailsByUserId(umUsers.getUserId(), false);
				for (UmOrgUsers umOrgUser : umOrgUsers) {

					if (!umOrgUser.isMarkAsDelete()) {

						bpIds.add(umOrgUser.getOmOrgBusinessPartners().getOrgBpId());
						user.setRole(umOrgUser.getUmOrgRoles().getRoleType());
						user.setRoleId(umOrgUser.getUmOrgRoles().getRoleId().toString());
						user.setRoleDesc(umOrgUser.getUmOrgRoles().getRoleDesc());
						user.setRoleType(umOrgUser.getUmOrgRoles().getRoleShortId());
					}
				}

				user.setBpIds(bpIds);
			}

		} catch (Exception e) {
			logger.error("Error while finding user detais by Logonid: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return user;
	}

	/**
	 * Get the user details of given userId
	 * 
	 * @param logonId
	 * @return
	 */
	public UserTo getUserDetails(String logonId) {

		String METHOD_NAME = "getUserDetails";

		logger.info("Entered into the method: " + METHOD_NAME);

		UserTo user = null;

		try {

			user = this.findByLogonId(logonId);
			
			if(user.isDeactivated()){
				user = null;
			}

		} catch (Exception e) {
			logger.error("Error while finding user detais by user id: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return user;
	}

	/**
	 * Update existed user details
	 * 
	 * @param user
	 * @return
	 */
	public boolean updateUserDetails(UserTo user) {

		String METHOD_NAME = "updateUserDetails";

		logger.info("Entered into the method: " + METHOD_NAME);

		boolean isUpdated = false;

		try {

			Optional<UmUsers> umUser = umUsersRepository.findById(user.getUserId());

			if (umUser != null) {

				BeanUtils.copyProperties(user, umUser.get());

				umUser.get().setMarkAsDelete(false);
				umUser.get().setChangedBy(user.getLoggedUser());
				umUser.get().setChangedTs(new Date());

				umUsersRepository.save(umUser.get());

				isUpdated = true;
			}

		} catch (Exception e) {
			logger.error("Error while updating user detais: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return isUpdated;
	}

	/**
	 * Send the reset password link for forgot password
	 * 
	 * @param logonId
	 * @param loggedUser
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserTo> generateRestPasswordLink(String action, String logonId, List<Integer> users, String emailId) {

		String METHOD_NAME = "sendRestPasswordLink";

		logger.info("Entered into the method: " + METHOD_NAME);

		List<UserTo> usersTo = new ArrayList<UserTo>();

		try {

			if ("RESETPWD".equals(action)) {

				for (Integer user : users) {

					/*List<UmUsers> umUsers = (List<UmUsers>) baseDao.findByHQLQueryWithIndexedParams(
							prop.getProperty("GET_USER_USERID"), new Object[] { user, false });*/
					
					List<UmUsers> umUsers = umUsersRepository.getUserByUserId(user, false);

					if (!umUsers.isEmpty()) {

						UmUsers umUser = umUsers.get(0);
						
						/*Map<String, Object> params= new HashMap<String, Object>();
						params.put("userId", umUser.getUserId());
						params.put("markAsDelete", false);
						String query = prop.getProperty("GET_ORG_DETAILS");
						List<UmOrgUsers> umOrgUsers	= (List<UmOrgUsers>) baseDao.findByHQLQueryWithNamedParams(query, params);*/
						List<UmOrgUsers> umOrgUsers	= umOrgUsersRepository.getUmOrgDetailsByUserId(umUser.getUserId(), false);
						UmOrgUsers	umOrgUser=	umOrgUsers.get(0);

						UserTo userTo = new UserTo();

						BeanUtils.copyProperties(umUser, userTo);

						userTo.setResetKey(this.createPasswordResetKey(umUser));
						
						userTo.setCurrentOrgId(umOrgUser.getOmOrganizations().getOrgId());


						usersTo.add(userTo);
					}
				}

			} else if ("FORGOTPWD".equals(action)) {
				/*String str = "from UmUsers us where upper(us.logonId)= upper( ? ) and us.markAsDelete=? ";
				List<UmUsers> umUsers = (List<UmUsers>) baseDao.findByHQLQueryWithIndexedParams(
						str, new Object[] { logonId, false });*/
				List<UmUsers> umUsers = umUsersRepository.getUserbyLoginId(logonId, false);

				if (!umUsers.isEmpty()) {
					UmUsers umUser = umUsers.get(0);
					List<String> ids = new ArrayList<String>();
					ids.add(umUser.getEmail());

					if(ids.contains(emailId) || (umUser.getEmailIds() != null && umUser.getEmailIds().contains(emailId))){
						/*Map<String, Object> params= new HashMap<String, Object>();
						params.put("userId", umUser.getUserId());
						params.put("markAsDelete", false);
						String query = prop.getProperty("GET_ORG_DETAILS");
						List<UmOrgUsers> umOrgUsers	= (List<UmOrgUsers>) baseDao.findByHQLQueryWithNamedParams(query, params);*/
						List<UmOrgUsers> umOrgUsers	= umOrgUsersRepository.getUmOrgDetailsByUserId(umUser.getUserId(), false);
						UmOrgUsers	umOrgUser=	umOrgUsers.get(0);
					
	
						UserTo userTo = new UserTo();
						BeanUtils.copyProperties(umUser, userTo);
						userTo.setEmail(emailId);
						userTo.setCurrentOrgId(umOrgUser.getOmOrganizations().getOrgId());
						userTo.setResetKey(this.createPasswordResetKey(umUser));
						usersTo.add(userTo);
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error while updating user detais: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return usersTo;
	}

	/**
	 * Create password rest key
	 * 
	 * @param user
	 * @param loggedUser
	 * @throws Exception
	 */
	private String createPasswordResetKey(UmUsers user) throws Exception {

		UmUserPwdReset umPwdReset = new UmUserPwdReset();

		if (user != null) {

			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_YEAR, 3);

			umPwdReset.setResetKey(UUID.randomUUID().toString());
			umPwdReset.setUmUsers(user);
			umPwdReset.setCreatedTs(new Date());
			umPwdReset.setMarkAsDelete(false);
			umPwdReset.setCreatedBy(user.getUserId());
			umPwdReset.setValidTill(c.getTime());
			umUserPwdResetRepository.save(umPwdReset);
		}

		return umPwdReset.getResetKey();
	}

	/**
	 * Validate the password reset key status
	 * 
	 * @param resetKey
	 * @return
	 */
	public UserTo validatePasswordResetKey(String resetKey) {

		String METHOD_NAME = "validatePasswordResetKey";

		logger.info("Entered into the method: " + METHOD_NAME);

		UserTo userPwd = null;

		try {

			Optional<UmUserPwdReset> pwdReset = umUserPwdResetRepository.findById(resetKey);

			if (pwdReset != null && !pwdReset.get().getMarkAsDelete()) {

				userPwd = new UserTo();
				userPwd.setUserId(pwdReset.get().getUmUsers().getUserId());
				userPwd.setKeyValidTime(pwdReset.get().getValidTill());
			}

		} catch (Exception e) {
			logger.error("Error while validating password reset key: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return userPwd;
	}

	/**
	 * Update the user password details
	 * 
	 * @param userPwd
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public UserTo updateUserPassword(UserTo userPwd, String action) {

		String METHOD_NAME = "updateUserPassword";

		logger.info("Entered into the method: " + METHOD_NAME);

		boolean isUpdated = false, isPwdMatched = false;

		UserTo userTo = new UserTo();

		try {

			UmUsers umUsers = null;

			if (userPwd.getLogonId() != null) {

				/*List<UmUsers> users = (List<UmUsers>) baseDao.findByHQLQueryWithIndexedParams(
						prop.getProperty("GET_USER_LOGONID"), new Object[] { userPwd.getLogonId(), false });*/
				List<UmUsers> users = umUsersRepository.getUserLoginId(userPwd.getLogonId(), false);

				if (!users.isEmpty())
					umUsers = users.get(0);

			} else if (userPwd.getUserId() != null) {

				Optional<UmUsers> tmpUmUsers = umUsersRepository.findById(userPwd.getUserId());
				umUsers = tmpUmUsers.isPresent() ? tmpUmUsers.get() : null;
			}

			if (umUsers != null) {

				userTo.setExisted(true);

				if ("RESETPWD".equals(action)) {

					isPwdMatched = userPwd.isPwdMatched();
					
					
				} else if ("FORGOTPWD".equals(action) || "CREATEPWD".equals(action)) {

					if (userPwd.getResetKey() != null && !userPwd.getResetKey().trim().isEmpty()) {

						Optional<UmUserPwdReset> pwdReset = umUserPwdResetRepository.findById(userPwd.getResetKey());
						pwdReset.get().setMarkAsDelete(true);
						pwdReset.get().setChangedTs(new Date());
						pwdReset.get().setChangedBy(umUsers.getUserId());

						umUserPwdResetRepository.save(pwdReset.get());

						isPwdMatched = true;
					}
				}
				else if ("UPDATEPWD".equals(action)) {
					
					if (userPwd.getResetKey() != null && !userPwd.getResetKey().trim().isEmpty()) {

						Optional<UmUserPwdReset> pwdReset = umUserPwdResetRepository.findById(userPwd.getResetKey());
						pwdReset.get().setMarkAsDelete(true);
						pwdReset.get().setChangedTs(new Date());
						pwdReset.get().setChangedBy(umUsers.getUserId());

						umUserPwdResetRepository.save(pwdReset.get());

						isPwdMatched = true;
					}
					
				}

				if (isPwdMatched) {

					int expireDays = 180;

					Map<String, String> userConfigs = this.getUserApplicationSettings();

					if (userConfigs!=null && userConfigs.get(GeneralConstants.PWD_EXPIRE_DAYS) != null)
						expireDays = Integer.parseInt(userConfigs.get(GeneralConstants.PWD_EXPIRE_DAYS));

					Calendar c = Calendar.getInstance();
					c.add(Calendar.DATE, expireDays);

					umUsers.setPassword(userPwd.getEncryNewPwd());
					umUsers.setPasswordExpiredTs(c.getTime());
					umUsers.setChangedTs(new Date());
					umUsers.setChangedBy(umUsers.getUserId());

					umUsersRepository.save(umUsers);

					isUpdated = true;
				}
			}

			userTo.setUpdated(isUpdated);
			userTo.setPwdMatched(isPwdMatched);

		} catch (Exception e) {
			logger.error("Error while updating the password details: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return userTo;
	}

	/**
	 * Update logon retries
	 * 
	 * @param logonId
	 * @param action
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void updateLogonRetries(String logonId, String action) {

		String METHOD_NAME = "updateLogonRetries";

		logger.info("Entered into the method: " + METHOD_NAME);

		try {

			/*List<UmUsers> users = (List<UmUsers>) baseDao.findByHQLQueryWithIndexedParams(
					prop.getProperty("GET_USER_LOGONID"), new Object[] { logonId, false });*/
			List<UmUsers> users = umUsersRepository.getUserLoginId(logonId, false);

			if (!users.isEmpty()) {

				int max_retries = 3;

				Map<String, String> userConfigs = this.getUserApplicationSettings();

				if (userConfigs.get(GeneralConstants.LOGON_RETRIES) != null)
					max_retries = Integer.parseInt(userConfigs.get(GeneralConstants.LOGON_RETRIES));

				UmUsers umUsers = users.get(0);

				boolean isRetriesUpdate = false;

				int retriesCnt = umUsers.getLogonRetries();

				int userRetries = 0;

				if ("FAILED".equals(action)) {

					userRetries = retriesCnt + 1;

					isRetriesUpdate = userRetries <= max_retries ? true : false;

				} else if ("RESET".equals(action)) {

					isRetriesUpdate = retriesCnt != 0 ? true : false;
				}

				if (isRetriesUpdate) {

					if (max_retries == userRetries) {

						umUsers.setUserLocked(true);
						umUsers.setUserLockedTs(new Date());
					}

					umUsers.setLogonRetries(userRetries);
					umUsers.setChangedBy(umUsers.getUserId());
					umUsers.setChangedTs(new Date());

					umUsersRepository.save(umUsers);
				}
			}

		} catch (Exception e) {
			logger.error("Error while updating logon retries: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);
	}

	/**
	 * Get the list of business partners by user
	 * 
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ListOfOrganizationsTo getBusinessPartnersByUser(Integer userId) {

		String METHOD_NAME = "getBusinessPartnersByUser";

		logger.info("Entered into the method: " + METHOD_NAME);

		ListOfOrganizationsTo organizations = new ListOfOrganizationsTo();

		try {

			Optional<UmUsers> umUsers = umUsersRepository.findById(userId);

			if (umUsers != null && !umUsers.get().isMarkAsDelete()) {

				boolean isRedirect = false;

				/*String query = prop.getRequiredProperty("GET_ORG_BY_USER");

				List<OmOrganizations> organizationList = (List<OmOrganizations>) baseDao
						.findByHQLQueryWithIndexedParams(query, new Object[] { userId, false });*/
				List<OmOrganizations> organizationList = umOrgUsersRepository.getOrgByUserId(userId, false);

				List<OrganizationTo> orgList = new ArrayList<OrganizationTo>();

				for (OmOrganizations orgDetails : organizationList) {

					OrganizationTo organizationTo = new OrganizationTo();

					BeanUtils.copyProperties(orgDetails, organizationTo);

					//GdCountries countries = orgDetails.getGdCountries();


					//organizationTo.setCountry(countries.getCountryName());
					//organizationTo.setCountryId(countries.getCountryId());
					organizationTo.setUserId(orgDetails.getCreatedBy());

					/*String query = prop.getRequiredProperty("GET_BP_BY_USER");

					List<Object[]> bpS = (List<Object[]>) baseDao.findByHQLQueryWithIndexedParams(query,
							new Object[] { orgDetails.getOrgId(), userId });*/
					List<Object[]> bpS = umOrgUsersRepository.getBpByUserId(orgDetails.getOrgId(), userId);

					List<BusinessPartnerTo> bpList = new ArrayList<BusinessPartnerTo>();

					List<String> orgBpIds = new ArrayList<String>();

					orgBpIds.add(orgDetails.getOrgId());

					for (Object[] obj : bpS) {

						OmOrgBusinessPartners bp = (OmOrgBusinessPartners) obj[0];

						orgBpIds.add(bp.getOrgBpId());
					}

					Map<String, Object> params = new HashMap<>();
					params.put("stype", SettingType.ORG_BP_SETTING.getValue());
					params.put("refObjId", orgBpIds);
					params.put("grps", Arrays.asList(GeneralConstants.IPW_SETTINGS));

					Map<String, String> ipMap = settingDao.getSettingsByListOfBp(params);

					for (Object[] obj : bpS) {

						BusinessPartnerTo orgBp = new BusinessPartnerTo();

						OmOrgBusinessPartners bp = (OmOrgBusinessPartners) obj[0];

						UmOrgUsers orgUsers = (UmOrgUsers) obj[1];

						orgBp.setBpId(bp.getOrgBpId());

						orgBp.setName(bp.getBpLegalName());

						orgBp.setBpGstinNumber(bp.getBpGstinNumber());

						orgBp.setBpType(bp.getBpType());

						orgBp.setDrugLicNum(bp.getDrugLicNum());
					

						orgBp.setBpAccess(true);

						if (ipMap.containsKey(orgBp.getBpId())
								&& !ipMap.get(orgBp.getBpId()).contains(request.getRemoteAddr())) {

							orgBp.setBpAccess(false);
						}

						if (ipMap.containsKey(orgDetails.getOrgId())
								&& !ipMap.get(orgDetails.getOrgId()).contains(request.getRemoteAddr())) {

							orgBp.setBpAccess(false);
						}

						String role = orgUsers.getUmOrgRoles().getRoleShortId();

						if (GeneralConstants.ROLE_ORG_ADMIN.equals(role)) {

							if (GeneralConstants.RMS_BP_TYPE.equals(orgBp.getBpType())) {

								isRedirect = umUsers.get().isAdmin();

								bpList.add(orgBp);
							}

						} else {

							bpList.add(orgBp);
						}
					}

					if (organizationList.size() == 1 && bpList.size() == 1) {
						isRedirect = true;
					}

					organizationTo.setBusinessPartners(bpList);

					orgList.add(organizationTo);
				}

				organizations.setRedirect(isRedirect);

				organizations.setOrgs(orgList);
			}

		} catch (Exception ex) {
			logger.error("Error while getting the business partners by user: " + ExceptionUtils.getStackTrace(ex));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return organizations;
	}

	/**
	 * Get user settings
	 * 
	 * @return
	 */
	private Map<String, String> getUserApplicationSettings() {

		Map<String, Object> params = new HashMap<>();
		params.put("stype", SettingType.APP_SETTING.getValue());
		params.put("grps", Arrays.asList(GeneralConstants.USER_SETTINGS));

		Map<String, String> userConfigs = ((SettingTo) settingDao.getSettingValues(params)).getSettings();

		return userConfigs;
	}

	/**
	 * Lock or Unlock users account
	 * 
	 * @param user_id
	 * @param action
	 * @return
	 */
	@Override
	public boolean lockOrUnlockUser(Integer user_id, String action) {

		String METHOD_NAME = "lockOrUnlockUser";

		logger.info("Entered into the method: " + METHOD_NAME);

		boolean lockOrUnlock = false;

		try {

			Optional<UmUsers> user = umUsersRepository.findById(user_id);

			if (user != null) {

				if ("LOCK".equals(action)) {

					user.get().setUserLocked(true);
					user.get().setUserLockedTs(new Date());

				} else if ("UNLOCK".equals(action)) {

					user.get().setUserLocked(false);
					user.get().setLogonRetries(0);
				}

				user.get().setChangedTs(new Date());

				umUsersRepository.save(user.get());

				lockOrUnlock = true;
			}

		} catch (Exception e) {
			logger.error("Error while updating user lock or unlock status: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return lockOrUnlock;
	}

	/**
	 * Get Online users
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserTo> getOnlineUsers() {

		String METHOD_NAME = "getOnlineUsers";

		logger.info("Entered into the method: " + METHOD_NAME);

		List<UserTo> users = new ArrayList<UserTo>();

		try {

			/*List<Object[]> usrList = (List<Object[]>) baseDao
					.findBySQLQueryWithoutParams(prop.getProperty("GET_ONLINE_USERS"));*/
			List<Object[]> usrList = loginHistoryRepository.getOnlineUsers();

			for (Object[] usr : usrList) {

				UserTo userTo = new UserTo();

				if (usr[0] != null)
					userTo.setLogonId(String.valueOf(usr[0]));
				if (usr[1] != null)
					userTo.setLogonTs(String.valueOf(usr[1]));
				if (usr[2] != null)
					userTo.setIpAddress(String.valueOf(usr[2]));
				if (usr[3] != null)
					userTo.setBrowserName(String.valueOf(usr[3]));
				if (usr[4] != null)
					userTo.setBrowserVersion(String.valueOf(usr[4]));

				users.add(userTo);
			}

		} catch (Exception e) {
			logger.error("Error while getting online users: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return users;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserTo> getUserLoginHistory(Integer loginId, String frmDate, String toDate) {

		//GET_USER_LOGIN_HSTRY
		String METHOD_NAME = "getUserLoginHistory";

		logger.info("Entered into the method: " + METHOD_NAME);

		List<UserTo> users = new ArrayList<UserTo>();
		try {

			frmDate = frmDate+" 00:00:00";
			toDate = toDate +" 23:59:59";
			/*List<Object[]> usrList = (List<Object[]>) baseDao.findBySQLQueryWithIndexedParams(prop.getProperty("GET_USER_LOGIN_HSTRY"),
					new Object[] { loginId,frmDate,toDate });*/
			List<Object[]> usrList = loginHistoryRepository.getUserLoginHistory(loginId, frmDate, toDate);

			for (Object[] usr : usrList) {

				UserTo userTo = new UserTo();

				if (usr[0] != null)
					userTo.setLogonId(String.valueOf(usr[0]));
				if (usr[1] != null)
					userTo.setLogonTs(String.valueOf(usr[1]));
				if (usr[2] != null)
					userTo.setIpAddress(String.valueOf(usr[2]));
				if (usr[3] != null)
					userTo.setBrowserName(String.valueOf(usr[3]));
				if (usr[4] != null)
					userTo.setBrowserVersion(String.valueOf(usr[4]));

				users.add(userTo);
			}

		} catch (Exception e) {
			logger.error("Error while getting online users: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return users;
		
	}

	@Override
	public UmUsers getUserById(Integer userId) {
		String METHOD_NAME = "getUserById";

		logger.info("Entered into the method: " + METHOD_NAME);
		UmUsers umUsers = new UmUsers();
		try {
			umUsers  = (UmUsers) baseDao.findByPK(UmUsers.class, userId);

		} catch (Exception e) {
			logger.error("Error while getting online users: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return umUsers;
	}

	@Override
	public List<UmOrgUsers> getAdminAndHqUsers() {
		String METHOD_NAME = "getAdminAndHqUsers";

		logger.info("Entered into the method: " + METHOD_NAME);
		List<UmOrgUsers> umOrgUsers = new ArrayList<UmOrgUsers>();
		try {
			List<String> roleIds = new ArrayList<>();
			roleIds.add("ADMIN");//Admin
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("roleShortId", roleIds);
			String query = "from UmOrgUsers um where um.markAsDelete = false and um.umUsers.markAsDelete = false and um.umOrgRoles.roleShortId in (:roleShortId)";
			umOrgUsers = (List<UmOrgUsers>) baseDao.findByHQLQueryWithNamedParams(query,params);
//			umUsers  = (List<UmUsers>) baseDao.findByPK(UmUsers.class, userId);

		} catch (Exception e) {
			logger.error("Error while getting online users: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return umOrgUsers;
	}

	@Override
	public Boolean selfRegister(UserTo user) {
		String METHOD_NAME = "register";

		logger.info("Entered into the method: " + METHOD_NAME);

		boolean isSaved = false;

		try {
			if (user != null) {
				UmUsers umUsers = new UmUsers();
				GdUserTypes gdUserTypes = new GdUserTypes();
				gdUserTypes.setUserTypeId(user.getUserTypeId());

				BeanUtils.copyProperties(user, umUsers);
				umUsers.setBookingOffice(user.getCity());
				//for customer setting deafult empcode
				umUsers.setEmpCode("800010");
				umUsers.setGdUserTypes(gdUserTypes);
				umUsers.setPassword(encoder.encode(user.getNewPwd()));
				umUsers.setCreatedTs(new Date());
				umUsers.setIsAdmin(false);
				umUsers.setMarkAsDelete(false);

				umUsersRepository.save(umUsers);

				UmOrgUsers orgUsers = new UmOrgUsers();
				OmOrganizations organizations = new OmOrganizations();
				organizations.setOrgId(user.getCurrentOrgId());

				OmOrgBusinessPartners businessPartners = new OmOrgBusinessPartners();
				businessPartners.setOrgBpId(user.getBpId());

				UmOrgRoles orgRole = umOrgRolesRepo.findByRoleShortId(user.getRoleShortId()).get();

				orgUsers.setOmOrganizations(organizations);
				orgUsers.setOmOrgBusinessPartners(businessPartners);
				orgUsers.setCreatedTs(new Date());
				orgUsers.setUmOrgRoles(orgRole);
				orgUsers.setUmUsers(umUsers);
				orgUsers.setCreatedBy(umUsers.getUserId());
				orgUsers.setMarkAsDelete(false);

				Set<UmOrgUsers> orgUserSet = new HashSet<>();
				orgUserSet.add(orgUsers);
				umUsers.setUmOrgUsers(orgUserSet);
				
				umOrgUsersRepository.save(orgUsers);

				UmUserPwdReset umPwdReset = new UmUserPwdReset();
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DAY_OF_YEAR, 3);

				umPwdReset.setResetKey(UUID.randomUUID().toString());
				umPwdReset.setUmUsers(umUsers);
				umPwdReset.setCreatedTs(new Date());
				umPwdReset.setMarkAsDelete(false);
				umPwdReset.setValidTill(c.getTime());
				umPwdReset.setCreatedBy(umUsers.getUserId());

				Set<UmUserPwdReset> pwdResetSet = new HashSet<>();
				pwdResetSet.add(umPwdReset);
				umUsers.setUmUserPwdReset(pwdResetSet);

				umUserPwdResetRepository.save(umPwdReset);
				user.setResetPwd(true);
				user.setResetKey(umPwdReset.getResetKey());

				this.saveCustomerDetails(user, umUsers);

				isSaved = true;

			}

		} catch (Exception e) {
			logger.error("Error while getting online users: " + ExceptionUtils.getStackTrace(e));

		}

		logger.info("Exit from the method: " + METHOD_NAME);

		return isSaved;
	}

	private void saveCustomerDetails(UserTo user, UmUsers umUsers) {
		String METHOD_NAME = "saveCustomerDetails";

		logger.info("Entered into the method: " + METHOD_NAME);
		UmCustomers umCustomers = new UmCustomers();
		try {
			umCustomers.setCustomerId(UUID.randomUUID().toString());
			umCustomers.setCustomerName(user.getFirstName());
			umCustomers.setMobileNo(user.getMobile());
			umCustomers.setEmailId(user.getEmail());
			umCustomers.setAddress1(user.getAddress1());
			umCustomers.setAddress2(user.getAddress2());
			umCustomers.setAddress3(user.getAddress3());
			umCustomers.setOfficeLocation(user.getOfficeLocation());
			umCustomers.setPanNumber(user.getPanNumber());
			umCustomers.setPinCode(user.getPinCode());
			umCustomers.setGstNo(user.getGstNo());
			umCustomers.setState(user.getState());
			umCustomers.setCity(user.getCity());
			umCustomers.setUserId(umUsers.getUserId());
			umCustomers.setCreatedBy(umUsers.getUserId());
			umCustomers.setCreatedTs(new Date());
			umCustomers.setMarkAsDelete(false);
			umCustomers.setHouseNo(user.getHouseNo());
			umCustomers.setAadharNumber(user.getAadharNumber());
			
			umCustomersRepo.save(umCustomers);

		} catch (Exception e) {
			logger.error("Error while getting online users: " + ExceptionUtils.getStackTrace(e));
		}

		logger.info("Exit from the method: " + METHOD_NAME);
		
	}

	@Override
	public boolean saveLoggedUserDetails(UserTo user, String action) {
		boolean flag=false;
		try {	
			UserLoginHistory loginHistory=new UserLoginHistory();
			loginHistory.setLoginhistoryId(UUID.randomUUID().toString());
			loginHistory.setUserId(user.getUserId());
			loginHistory.setLogonId(user.getLogonId());
			loginHistory.setUserTypeId(user.getUserTypeId());
			loginHistory.setEntryTime(new Date());
			loginHistory.setAction(action);
			userLoginHistoryRepo.save(loginHistory);
			flag=true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return flag;
	}
		
}