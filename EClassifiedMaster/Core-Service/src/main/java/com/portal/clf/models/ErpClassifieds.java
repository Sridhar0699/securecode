package com.portal.clf.models;

import java.math.BigDecimal;
import java.util.List;

public class ErpClassifieds {
	
	private String contactNo;
	private String adId;
	private String requestedDate;
	private String publishedDate;
	private Integer categoryId;
	private String categoryStr;
	private String subCategoryStr;
	private String childCategoryStr;
	private List<String> editions;
	private List<String> editionsErpRefId;
	private BigDecimal paidAmount;
	private String approvalStatus;
	private String paymentStatus;
	private boolean downloadStatus;
	private String matter;
	private String itemId;
	private String orderId;
	private String clfPaymentStatus;
	private String schemeStr;
	private String classifiedTypeStr;
	
	private Integer classifiedType;
	private Integer classifiedAdsType;
	private Integer scheme;
	private Integer category;
	private Integer subCategory;
	private Integer childCategory;
	private Integer lang;
	private String content;
	private Integer createdBy;
	private String createdTs;
	private Integer changedBy;
	private String changedTs;
	private Integer classifiedAdsSubType;
	private String customerId;
	private Integer userTypeId;
	private String classifiedTypeErpRefId;
	private String adsType;
	private String adsTypeErpRefId;
	private String adsSubType;
	private String adsSubTypeErpRefId;
	private String schemeErpRefId;
	private String langStr;
	private List<String> publishDates;
	private String categoryErpRefId;
	private String subCategoryErpRefId;
	private String childCategoryErpRefId;
	
	private String customerName;
	private String mobileNumber;
	private String emailId;
	private String address1;
	private String address2;
	private String address3;
	private String pinCode;
	private String officeLocation;
	private String gstNo;
	private String panNumber;
	private String aadharNumber;
	private String erpRefId;
	private String state;
	private String city;
	private String houseNo;
	private Integer lineCount;
	private String keyword;
	
	private Integer group;
	private Integer subGroup;
	private Integer childGroup;
	private String groupStr;
	private String subGroupStr;
	private String childGroupStr;
	private String groupErpRefId;
	private String subGroupErpRefId;
	private String childGroupErpRefId;
	private Integer bookingUnit;
	private String typeOfCustomer;
	private String createdTime;
	private String createdDate;
	private String orderIdentification;
	private String publishLang;
	private String bookingDate;
	private String bookingUnitStr;
	private String productHierarchy;
	private String salesOffice;
	private String empCode;
	private String soldToParty;
	private String userTypeIdErpRefId;
	private String customerName2;
	private String contentStatus;
	private String bookingUnitEmail;
	
	private Integer noOfInsertions;
	private Double sizeWidth;
	private Double sizeHeight;
	private Double spaceWidth;
	private Double spaceHeight;
	private Integer pagePosition;
	private Integer formatType;
	private Integer fixedFormat;
	private Integer pageNumber;
	private Integer categoryDiscount;
	private BigDecimal multiDiscount;
	private BigDecimal additionalDiscount;
	private BigDecimal surchargeRate;
	private String fixedFormatsize;
	private String fixedFormatErpRefId;
	private String formatTypeStr;
	private String multiDiscountStr;
	private String pagePositionpageName;
	private String pagePositionErpRefId;
	private Double gstTotalAmount;
	private Double rate;
	private Double extraLineRateAmount;
	private Double agencyCommition;
	
	
	private String clientCode;
	private String cityDisc;
	private String stateDisc;
	private String positioningDesc;
	private Double discountValue;
	private Double cgst;
	private Double sgst;
	private Double igst;
	private BigDecimal  cgstValue;
	private BigDecimal sgstValue;
	private BigDecimal igstValue;
	private BigDecimal totalValue;
	private String bankOrBranch;
	private String cashReceiptNo;
	
	public Double getAgencyCommition() {
		return agencyCommition;
	}
	public void setAgencyCommition(Double agencyCommition) {
		this.agencyCommition = agencyCommition;
	}
	public Double getExtraLineRateAmount() {
		return extraLineRateAmount;
	}
	public void setExtraLineRateAmount(Double extraLineRateAmount) {
		this.extraLineRateAmount = extraLineRateAmount;
	}
	public Double getRate() {
		return rate;
	}
	public void setRate(Double rate) {
		this.rate = rate;
	}
	public Double getGstTotalAmount() {
		return gstTotalAmount;
	}
	public void setGstTotalAmount(Double gstTotalAmount) {
		this.gstTotalAmount = gstTotalAmount;
	}
	public Double getSizeWidth() {
		return sizeWidth;
	}
	public void setSizeWidth(Double sizeWidth) {
		this.sizeWidth = sizeWidth;
	}
	public Double getSizeHeight() {
		return sizeHeight;
	}
	public void setSizeHeight(Double sizeHeight) {
		this.sizeHeight = sizeHeight;
	}
	public Integer getCategoryDiscount() {
		return categoryDiscount;
	}
	public void setCategoryDiscount(Integer categoryDiscount) {
		this.categoryDiscount = categoryDiscount;
	}
	
	public Integer getNoOfInsertions() {
		return noOfInsertions;
	}
	public void setNoOfInsertions(Integer noOfInsertions) {
		this.noOfInsertions = noOfInsertions;
	}
	
	public Integer getPagePosition() {
		return pagePosition;
	}
	public void setPagePosition(Integer pagePosition) {
		this.pagePosition = pagePosition;
	}
	public Integer getFormatType() {
		return formatType;
	}
	public void setFormatType(Integer formatType) {
		this.formatType = formatType;
	}
	public Integer getFixedFormat() {
		return fixedFormat;
	}
	public void setFixedFormat(Integer fixedFormat) {
		this.fixedFormat = fixedFormat;
	}
	public Integer getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	public String getFixedFormatsize() {
		return fixedFormatsize;
	}
	public void setFixedFormatsize(String fixedFormatsize) {
		this.fixedFormatsize = fixedFormatsize;
	}
	public String getFixedFormatErpRefId() {
		return fixedFormatErpRefId;
	}
	public void setFixedFormatErpRefId(String fixedFormatErpRefId) {
		this.fixedFormatErpRefId = fixedFormatErpRefId;
	}
	public String getFormatTypeStr() {
		return formatTypeStr;
	}
	public void setFormatTypeStr(String formatTypeStr) {
		this.formatTypeStr = formatTypeStr;
	}
	public String getMultiDiscountStr() {
		return multiDiscountStr;
	}
	public void setMultiDiscountStr(String multiDiscountStr) {
		this.multiDiscountStr = multiDiscountStr;
	}
	public String getPagePositionpageName() {
		return pagePositionpageName;
	}
	public void setPagePositionpageName(String pagePositionpageName) {
		this.pagePositionpageName = pagePositionpageName;
	}
	public String getPagePositionErpRefId() {
		return pagePositionErpRefId;
	}
	public void setPagePositionErpRefId(String pagePositionErpRefId) {
		this.pagePositionErpRefId = pagePositionErpRefId;
	}
	public String getBookingUnitEmail() {
		return bookingUnitEmail;
	}
	public void setBookingUnitEmail(String bookingUnitEmail) {
		this.bookingUnitEmail = bookingUnitEmail;
	}
	public String getContentStatus() {
		return contentStatus;
	}
	public void setContentStatus(String contentStatus) {
		this.contentStatus = contentStatus;
	}
	public String getCustomerName2() {
		return customerName2;
	}
	public void setCustomerName2(String customerName2) {
		this.customerName2 = customerName2;
	}
	public String getContactNo() {
		return contactNo;
	}
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	public String getAdId() {
		return adId;
	}
	public void setAdId(String adId) {
		this.adId = adId;
	}
	public String getRequestedDate() {
		return requestedDate;
	}
	public void setRequestedDate(String requestedDate) {
		this.requestedDate = requestedDate;
	}
	public String getPublishedDate() {
		return publishedDate;
	}
	public void setPublishedDate(String publishedDate) {
		this.publishedDate = publishedDate;
	}
	public Integer getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
	public List<String> getEditions() {
		return editions;
	}
	public void setEditions(List<String> editions) {
		this.editions = editions;
	}
	public BigDecimal getPaidAmount() {
		return paidAmount;
	}
	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}
	public String getApprovalStatus() {
		return approvalStatus;
	}
	public void setApprovalStatus(String approvalStatus) {
		this.approvalStatus = approvalStatus;
	}
	public String getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public boolean isDownloadStatus() {
		return downloadStatus;
	}
	public void setDownloadStatus(boolean downloadStatus) {
		this.downloadStatus = downloadStatus;
	}
	public String getMatter() {
		return matter;
	}
	public void setMatter(String matter) {
		this.matter = matter;
	}
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getClfPaymentStatus() {
		return clfPaymentStatus;
	}
	public void setClfPaymentStatus(String clfPaymentStatus) {
		this.clfPaymentStatus = clfPaymentStatus;
	}
	public Integer getScheme() {
		return scheme;
	}
	public void setScheme(Integer scheme) {
		this.scheme = scheme;
	}
	public Integer getClassifiedType() {
		return classifiedType;
	}
	public void setClassifiedType(Integer classifiedType) {
		this.classifiedType = classifiedType;
	}
	public Integer getClassifiedAdsType() {
		return classifiedAdsType;
	}
	public void setClassifiedAdsType(Integer classifiedAdsType) {
		this.classifiedAdsType = classifiedAdsType;
	}
	public String getScheme1() {
		return schemeStr;
	}
	public void setScheme1(String schemeStr) {
		this.schemeStr = schemeStr;
	}
	public String getCategoryStr() {
		return categoryStr;
	}
	public void setCategoryStr(String categoryStr) {
		this.categoryStr = categoryStr;
	}
	public String getSubCategoryStr() {
		return subCategoryStr;
	}
	public void setSubCategoryStr(String subCategoryStr) {
		this.subCategoryStr = subCategoryStr;
	}
	public String getSchemeStr() {
		return schemeStr;
	}
	public void setSchemeStr(String schemeStr) {
		this.schemeStr = schemeStr;
	}
	public Integer getLang() {
		return lang;
	}
	public void setLang(Integer lang) {
		this.lang = lang;
	}
	public void setCategory(Integer category) {
		this.category = category;
	}
	public void setSubCategory(Integer subCategory) {
		this.subCategory = subCategory;
	}
	public Integer getCategory() {
		return category;
	}
	public Integer getSubCategory() {
		return subCategory;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedTs() {
		return createdTs;
	}
	public void setCreatedTs(String createdTs) {
		this.createdTs = createdTs;
	}
	public Integer getChangedBy() {
		return changedBy;
	}
	public void setChangedBy(Integer changedBy) {
		this.changedBy = changedBy;
	}
	public String getChangedTs() {
		return changedTs;
	}
	public void setChangedTs(String changedTs) {
		this.changedTs = changedTs;
	}
	public Integer getClassifiedAdsSubType() {
		return classifiedAdsSubType;
	}
	public void setClassifiedAdsSubType(Integer classifiedAdsSubType) {
		this.classifiedAdsSubType = classifiedAdsSubType;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public Integer getUserTypeId() {
		return userTypeId;
	}
	public void setUserTypeId(Integer userTypeId) {
		this.userTypeId = userTypeId;
	}
	public String getClassifiedTypeStr() {
		return classifiedTypeStr;
	}
	public void setClassifiedTypeStr(String classifiedTypeStr) {
		this.classifiedTypeStr = classifiedTypeStr;
	}
	public String getClassifiedTypeErpRefId() {
		return classifiedTypeErpRefId;
	}
	public void setClassifiedTypeErpRefId(String classifiedTypeErpRefId) {
		this.classifiedTypeErpRefId = classifiedTypeErpRefId;
	}
	public String getAdsType() {
		return adsType;
	}
	public void setAdsType(String adsType) {
		this.adsType = adsType;
	}
	public String getAdsTypeErpRefId() {
		return adsTypeErpRefId;
	}
	public void setAdsTypeErpRefId(String adsTypeErpRefId) {
		this.adsTypeErpRefId = adsTypeErpRefId;
	}
	public String getAdsSubType() {
		return adsSubType;
	}
	public void setAdsSubType(String adsSubType) {
		this.adsSubType = adsSubType;
	}
	public String getAdsSubTypeErpRefId() {
		return adsSubTypeErpRefId;
	}
	public void setAdsSubTypeErpRefId(String adsSubTypeErpRefId) {
		this.adsSubTypeErpRefId = adsSubTypeErpRefId;
	}
	public String getSchemeErpRefId() {
		return schemeErpRefId;
	}
	public void setSchemeErpRefId(String schemeErpRefId) {
		this.schemeErpRefId = schemeErpRefId;
	}
	public String getLangStr() {
		return langStr;
	}
	public void setLangStr(String langStr) {
		this.langStr = langStr;
	}
	public List<String> getEditionsErpRefId() {
		return editionsErpRefId;
	}
	public void setEditionsErpRefId(List<String> editionsErpRefId) {
		this.editionsErpRefId = editionsErpRefId;
	}
	public List<String> getPublishDates() {
		return publishDates;
	}
	public void setPublishDates(List<String> publishDates) {
		this.publishDates = publishDates;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getAddress3() {
		return address3;
	}
	public void setAddress3(String address3) {
		this.address3 = address3;
	}
	public String getPinCode() {
		return pinCode;
	}
	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}
	public String getOfficeLocation() {
		return officeLocation;
	}
	public void setOfficeLocation(String officeLocation) {
		this.officeLocation = officeLocation;
	}
	public String getGstNo() {
		return gstNo;
	}
	public void setGstNo(String gstNo) {
		this.gstNo = gstNo;
	}
	public String getPanNumber() {
		return panNumber;
	}
	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}
	public String getAadharNumber() {
		return aadharNumber;
	}
	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}
	public String getErpRefId() {
		return erpRefId;
	}
	public void setErpRefId(String erpRefId) {
		this.erpRefId = erpRefId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getHouseNo() {
		return houseNo;
	}
	public void setHouseNo(String houseNo) {
		this.houseNo = houseNo;
	}
	public Integer getLineCount() {
		return lineCount;
	}
	public void setLineCount(Integer lineCount) {
		this.lineCount = lineCount;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getChildCategoryStr() {
		return childCategoryStr;
	}
	public void setChildCategoryStr(String childCategoryStr) {
		this.childCategoryStr = childCategoryStr;
	}
	public Integer getChildCategory() {
		return childCategory;
	}
	public void setChildCategory(Integer childCategory) {
		this.childCategory = childCategory;
	}
	public String getCategoryErpRefId() {
		return categoryErpRefId;
	}
	public void setCategoryErpRefId(String categoryErpRefId) {
		this.categoryErpRefId = categoryErpRefId;
	}
	public String getSubCategoryErpRefId() {
		return subCategoryErpRefId;
	}
	public void setSubCategoryErpRefId(String subCategoryErpRefId) {
		this.subCategoryErpRefId = subCategoryErpRefId;
	}
	public String getChildCategoryErpRefId() {
		return childCategoryErpRefId;
	}
	public void setChildCategoryErpRefId(String childCategoryErpRefId) {
		this.childCategoryErpRefId = childCategoryErpRefId;
	}
	public Integer getGroup() {
		return group;
	}
	public void setGroup(Integer group) {
		this.group = group;
	}
	public Integer getSubGroup() {
		return subGroup;
	}
	public void setSubGroup(Integer subGroup) {
		this.subGroup = subGroup;
	}
	public Integer getChildGroup() {
		return childGroup;
	}
	public void setChildGroup(Integer childGroup) {
		this.childGroup = childGroup;
	}
	public String getGroupStr() {
		return groupStr;
	}
	public void setGroupStr(String groupStr) {
		this.groupStr = groupStr;
	}
	public String getSubGroupStr() {
		return subGroupStr;
	}
	public void setSubGroupStr(String subGroupStr) {
		this.subGroupStr = subGroupStr;
	}
	public String getChildGroupStr() {
		return childGroupStr;
	}
	public void setChildGroupStr(String childGroupStr) {
		this.childGroupStr = childGroupStr;
	}
	public String getGroupErpRefId() {
		return groupErpRefId;
	}
	public void setGroupErpRefId(String groupErpRefId) {
		this.groupErpRefId = groupErpRefId;
	}
	public String getSubGroupErpRefId() {
		return subGroupErpRefId;
	}
	public void setSubGroupErpRefId(String subGroupErpRefId) {
		this.subGroupErpRefId = subGroupErpRefId;
	}
	public String getChildGroupErpRefId() {
		return childGroupErpRefId;
	}
	public void setChildGroupErpRefId(String childGroupErpRefId) {
		this.childGroupErpRefId = childGroupErpRefId;
	}
	public String getTypeOfCustomer() {
		return typeOfCustomer;
	}
	public void setTypeOfCustomer(String typeOfCustomer) {
		this.typeOfCustomer = typeOfCustomer;
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getOrderIdentification() {
		return orderIdentification;
	}
	public void setOrderIdentification(String orderIdentification) {
		this.orderIdentification = orderIdentification;
	}
	public String getPublishLang() {
		return publishLang;
	}
	public void setPublishLang(String publishLang) {
		this.publishLang = publishLang;
	}
	public String getBookingDate() {
		return bookingDate;
	}
	public void setBookingDate(String bookingDate) {
		this.bookingDate = bookingDate;
	}
	public Integer getBookingUnit() {
		return bookingUnit;
	}
	public void setBookingUnit(Integer bookingUnit) {
		this.bookingUnit = bookingUnit;
	}
	public String getBookingUnitStr() {
		return bookingUnitStr;
	}
	public void setBookingUnitStr(String bookingUnitStr) {
		this.bookingUnitStr = bookingUnitStr;
	}
	public String getProductHierarchy() {
		return productHierarchy;
	}
	public void setProductHierarchy(String productHierarchy) {
		this.productHierarchy = productHierarchy;
	}
	public String getSalesOffice() {
		return salesOffice;
	}
	public void setSalesOffice(String salesOffice) {
		this.salesOffice = salesOffice;
	}
	public String getEmpCode() {
		return empCode;
	}
	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}
	public String getSoldToParty() {
		return soldToParty;
	}
	public void setSoldToParty(String soldToParty) {
		this.soldToParty = soldToParty;
	}
	public String getUserTypeIdErpRefId() {
		return userTypeIdErpRefId;
	}
	public void setUserTypeIdErpRefId(String userTypeIdErpRefId) {
		this.userTypeIdErpRefId = userTypeIdErpRefId;
	}
	public BigDecimal getMultiDiscount() {
		return multiDiscount;
	}
	public void setMultiDiscount(BigDecimal multiDiscount) {
		this.multiDiscount = multiDiscount;
	}
	public BigDecimal getAdditionalDiscount() {
		return additionalDiscount;
	}
	public void setAdditionalDiscount(BigDecimal additionalDiscount) {
		this.additionalDiscount = additionalDiscount;
	}
	public BigDecimal getSurchargeRate() {
		return surchargeRate;
	}
	public void setSurchargeRate(BigDecimal surchargeRate) {
		this.surchargeRate = surchargeRate;
	}
	public String getClientCode() {
		return clientCode;
	}
	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}
	public String getCityDisc() {
		return cityDisc;
	}
	public void setCityDisc(String cityDisc) {
		this.cityDisc = cityDisc;
	}
	public String getStateDisc() {
		return stateDisc;
	}
	public void setStateDisc(String stateDisc) {
		this.stateDisc = stateDisc;
	}
	public String getPositioningDesc() {
		return positioningDesc;
	}
	public void setPositioningDesc(String positioningDesc) {
		this.positioningDesc = positioningDesc;
	}
	public Double getDiscountValue() {
		return discountValue;
	}
	public void setDiscountValue(Double discountValue) {
		this.discountValue = discountValue;
	}
	public Double getCgst() {
		return cgst;
	}
	public void setCgst(Double cgst) {
		this.cgst = cgst;
	}
	public Double getSgst() {
		return sgst;
	}
	public void setSgst(Double sgst) {
		this.sgst = sgst;
	}
	public Double getIgst() {
		return igst;
	}
	public void setIgst(Double igst) {
		this.igst = igst;
	}
	public BigDecimal getCgstValue() {
		return cgstValue;
	}
	public void setCgstValue(BigDecimal cgstValue) {
		this.cgstValue = cgstValue;
	}
	public BigDecimal getSgstValue() {
		return sgstValue;
	}
	public void setSgstValue(BigDecimal sgstValue) {
		this.sgstValue = sgstValue;
	}
	public BigDecimal getIgstValue() {
		return igstValue;
	}
	public void setIgstValue(BigDecimal igstValue) {
		this.igstValue = igstValue;
	}
	public BigDecimal getTotalValue() {
		return totalValue;
	}
	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}
	public String getBankOrBranch() {
		return bankOrBranch;
	}
	public void setBankOrBranch(String bankOrBranch) {
		this.bankOrBranch = bankOrBranch;
	}
	public String getCashReceiptNo() {
		return cashReceiptNo;
	}
	public void setCashReceiptNo(String cashReceiptNo) {
		this.cashReceiptNo = cashReceiptNo;
	}
	public Double getSpaceWidth() {
		return spaceWidth;
	}
	public void setSpaceWidth(Double spaceWidth) {
		this.spaceWidth = spaceWidth;
	}
	public Double getSpaceHeight() {
		return spaceHeight;
	}
	public void setSpaceHeight(Double spaceHeight) {
		this.spaceHeight = spaceHeight;
	}
	
	

}
