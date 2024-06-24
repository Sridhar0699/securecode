package com.portal.clf.models;

import java.math.BigDecimal;
import java.util.List;

public class Classifieds {

	private String contactNo;
	private String adId;
	private String requestedDate;
	private String publishedDate;
	private Integer categoryId;
	private String category;
	private String subCategory;
	private List<String> editions;
	private BigDecimal paidAmount;
	private String approvalStatus;
	private String paymentStatus;
	private boolean downloadStatus;
	private String matter;
	private String itemId;
	private String orderId;
	private String clfPaymentStatus;
	private String scheme;
	private String erpOrderId;
	private Integer userTypeId;
	private Integer createdBy;
	private Integer lines;
	
	private String downloadStatues;

	private String edition;
	
	

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getUserTypeId() {
		return userTypeId;
	}

	public void setUserTypeId(Integer userTypeId) {
		this.userTypeId = userTypeId;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public List<String> getEditions() {
		return editions;
	}

	public void setEditions(List<String> edition) {
		this.editions = edition;
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

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getErpOrderId() {
		return erpOrderId;
	}

	public void setErpOrderId(String erpOrderId) {
		this.erpOrderId = erpOrderId;
	}

	public Integer getLines() {
		return lines;
	}

	public void setLines(Integer lines) {
		this.lines = lines;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getDownloadStatues() {
		return downloadStatues;
	}

	public void setDownloadStatues(String downloadStatues) {
		this.downloadStatues = downloadStatues;
	}
	
	
	
	
}
