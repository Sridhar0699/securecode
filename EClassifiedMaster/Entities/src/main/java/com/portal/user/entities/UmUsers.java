package com.portal.user.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.portal.activity.entities.AmActivityLog;
import com.portal.basedao.BaseEntity;
import com.portal.gd.entities.GdUserTypes;

@Entity
@Table(name = "um_users")
public class UmUsers extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "logon_id")
	private String logonId;

	@Column(name = "password")
	private String password;

	@Column(name = "email")
	private String email;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "middle_name")
	private String middleName;

	@Column(name = "mobile")
	private String mobile;

	@ManyToOne
	@JoinColumn(name = "user_type_id")
	private GdUserTypes gdUserTypes;

	@Column(name = "logon_retries")
	private Integer logonRetries;

	@Column(name = "is_user_locked")
	private Boolean userLocked;

	@Column(name = "user_locked_ts")
	private Date userLockedTs;

	@Column(name = "password_expired_ts")
	private Date passwordExpiredTs;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "created_ts")
	private Date createdTs;

	@Column(name = "changed_by")
	private Integer changedBy;

	@Column(name = "changed_ts")
	private Date changedTs;

	@Column(name = "mark_as_delete")
	private Boolean markAsDelete;

	@Column(name = "is_admin")
	private Boolean isAdmin;
	
	@Column(name = "IS_DEACTIVATED")
	private boolean isDeactivated;
	
	@Column(name = "ADDRESS")
	private String address;
	
	@Column(name = "REGION")
	private Integer region;
	
	@Column(name = "REGION_CENTER")
	private Integer regCenter;
	
	@Column(name = "COUNTRY")
	private Integer country;
	
	@Column(name = "EMAIL_IDS")
	private String emailIds;
	
	@Column(name = "profile_id")
	private String profileId;
	
	@Column(name = "emp_code")
	private String empCode;
	
	@Column(name = "booking_office")
	private String bookingOffice;
		
	@Column(name = "sold_to_party")
	private String soldToParty;
	
	@Column(name = "state")
	private String state;

	@Where(clause = "mark_as_delete = 0")
	@OneToMany(mappedBy = "umUsers", fetch = FetchType.LAZY)
	private Set<UmOrgUsers> umOrgUsers;

	@OneToMany(mappedBy = "umUsers", fetch = FetchType.LAZY)
	private Set<UmUserPwdReset> umUserPwdReset;

	@OneToMany(mappedBy = "umUsers", fetch = FetchType.LAZY)
	private Set<AmActivityLog> amActivityHistory;
	
	/*@OneToMany(mappedBy = "umUsers", fetch = FetchType.LAZY)
	private Set<TransporterDetails> transporterDetails;*/
	
	/**
	 * @return the userId
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * @return the logonId
	 */
	public String getLogonId() {
		return logonId;
	}

	/**
	 * @param logonId
	 *            the logonId to set
	 */
	public void setLogonId(String logonId) {
		this.logonId = logonId;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * @param mobile
	 *            the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * @return the logonRetries
	 */
	public Integer getLogonRetries() {
		return logonRetries;
	}

	/**
	 * @param logonRetries
	 *            the logonRetries to set
	 */
	public void setLogonRetries(Integer logonRetries) {
		this.logonRetries = logonRetries;
	}

	/**
	 * @return the createdBy
	 */
	public Integer getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdTs
	 */
	public Date getCreatedTs() {
		return createdTs;
	}

	/**
	 * @param createdTs
	 *            the createdTs to set
	 */
	public void setCreatedTs(Date createdTs) {
		this.createdTs = createdTs;
	}

	/**
	 * @return the changedBy
	 */
	public Integer getChangedBy() {
		return changedBy;
	}

	/**
	 * @param changedBy
	 *            the changedBy to set
	 */
	public void setChangedBy(Integer changedBy) {
		this.changedBy = changedBy;
	}

	/**
	 * @return the changedTs
	 */
	public Date getChangedTs() {
		return changedTs;
	}

	/**
	 * @param changedTs
	 *            the changedTs to set
	 */
	public void setChangedTs(Date changedTs) {
		this.changedTs = changedTs;
	}

	/**
	 * @return the userLocked
	 */
	public Boolean getUserLocked() {
		return userLocked;
	}

	/**
	 * @param userLocked
	 *            the userLocked to set
	 */
	public void setUserLocked(Boolean userLocked) {
		this.userLocked = userLocked;
	}

	/**
	 * @return the userLockedTs
	 */
	public Date getUserLockedTs() {
		return userLockedTs;
	}

	/**
	 * @param userLockedTs
	 *            the userLockedTs to set
	 */
	public void setUserLockedTs(Date userLockedTs) {
		this.userLockedTs = userLockedTs;
	}

	/**
	 * @return the isAdmin
	 */
	public Boolean getIsAdmin() {
		return isAdmin;
	}

	/**
	 * @param isAdmin
	 *            the isAdmin to set
	 */
	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	/**
	 * @return the markAsDelete
	 */
	public Boolean getMarkAsDelete() {
		return markAsDelete;
	}

	/**
	 * @return the markAsDelete
	 */
	public Boolean isMarkAsDelete() {
		return markAsDelete;
	}

	/**
	 * @param markAsDelete
	 *            the markAsDelete to set
	 */
	public void setMarkAsDelete(Boolean markAsDelete) {
		this.markAsDelete = markAsDelete;
	}

	/**
	 * @return the isAdmin
	 */
	public Boolean isAdmin() {
		return isAdmin;
	}

	/**
	 * @param isAdmin
	 *            the isAdmin to set
	 */
	public void setAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	/**
	 * @return the umOrgUsers
	 */
	public Set<UmOrgUsers> getUmOrgUsers() {
		return umOrgUsers;
	}

	/**
	 * @param umOrgUsers
	 *            the umOrgUsers to set
	 */
	public void setUmOrgUsers(Set<UmOrgUsers> umOrgUsers) {
		this.umOrgUsers = umOrgUsers;
	}

	/**
	 * @return the umUserPwdReset
	 */
	public Set<UmUserPwdReset> getUmUserPwdReset() {
		return umUserPwdReset;
	}

	/**
	 * @param umUserPwdReset
	 *            the umUserPwdReset to set
	 */
	public void setUmUserPwdReset(Set<UmUserPwdReset> umUserPwdReset) {
		this.umUserPwdReset = umUserPwdReset;
	}

	/**
	 * @return the gdUserTypes
	 */
	public GdUserTypes getGdUserTypes() {
		return gdUserTypes;
	}

	/**
	 * @param gdUserTypes
	 *            the gdUserTypes to set
	 */
	public void setGdUserTypes(GdUserTypes gdUserTypes) {
		this.gdUserTypes = gdUserTypes;
	}

	/**
	 * @return the passwordExpiredTs
	 */
	public Date getPasswordExpiredTs() {
		return passwordExpiredTs;
	}

	/**
	 * @param passwordExpiredTs
	 *            the passwordExpiredTs to set
	 */
	public void setPasswordExpiredTs(Date passwordExpiredTs) {
		this.passwordExpiredTs = passwordExpiredTs;
	}

	/**
	 * @return the amActivityHistory
	 */
	public Set<AmActivityLog> getAmActivityHistory() {
		return amActivityHistory;
	}

	/**
	 * @param amActivityHistory
	 *            the amActivityHistory to set
	 */
	public void setAmActivityHistory(Set<AmActivityLog> amActivityHistory) {
		this.amActivityHistory = amActivityHistory;
	}

	public boolean isDeactivated() {
		return isDeactivated;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public void setDeactivated(boolean isDeactivated) {
		this.isDeactivated = isDeactivated;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getRegion() {
		return region;
	}

	public void setRegion(Integer region) {
		this.region = region;
	}

	public Integer getRegCenter() {
		return regCenter;
	}

	public void setRegCenter(Integer regCenter) {
		this.regCenter = regCenter;
	}

	public Integer getCountry() {
		return country;
	}

	public void setCountry(Integer country) {
		this.country = country;
	}

	public String getEmailIds() {
		return emailIds;
	}

	public void setEmailIds(String emailIds) {
		this.emailIds = emailIds;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getEmpCode() {
		return empCode;
	}

	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}

	public String getBookingOffice() {
		return bookingOffice;
	}

	public void setBookingOffice(String bookingOffice) {
		this.bookingOffice = bookingOffice;
	}

	public String getSoldToParty() {
		return soldToParty;
	}

	public void setSoldToParty(String soldToParty) {
		this.soldToParty = soldToParty;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
