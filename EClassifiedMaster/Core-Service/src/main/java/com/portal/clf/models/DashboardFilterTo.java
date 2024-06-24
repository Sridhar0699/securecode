package com.portal.clf.models;

import java.util.Date;

public class DashboardFilterTo {

	private Integer classifiedType;
	private String requestedDate;
	private String publishedDate;
	private Integer categoryId;
	private Integer edition;
	private boolean downloadConfimFlag;
	private String type;
	private String contentStatus;
	private String paymentStatus;
	private String downloadStatus;
	private Integer bookingUnit;
	private String requestedToDate;
	
	private Date fromDate;
	private Date toDate;
	
	public String getRequestedToDate() {
		return requestedToDate;
	}

	public void setRequestedToDate(String requestedToDate) {
		this.requestedToDate = requestedToDate;
	}

	public Integer getBookingUnit() {
		return bookingUnit;
	}

	public void setBookingUnit(Integer bookingUnit) {
		this.bookingUnit = bookingUnit;
	}

	public Integer getClassifiedType() {
		return classifiedType;
	}

	public void setClassifiedType(Integer classifiedType) {
		this.classifiedType = classifiedType;
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

	public Integer getEdition() {
		return edition;
	}

	public void setEdition(Integer edition) {
		this.edition = edition;
	}

	public boolean isDownloadConfimFlag() {
		return downloadConfimFlag;
	}

	public void setDownloadConfimFlag(boolean downloadConfimFlag) {
		this.downloadConfimFlag = downloadConfimFlag;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContentStatus() {
		return contentStatus;
	}

	public void setContentStatus(String contentStatus) {
		this.contentStatus = contentStatus;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getDownloadStatus() {
		return downloadStatus;
	}

	public void setDownloadStatus(String downloadStatus) {
		this.downloadStatus = downloadStatus;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	

	
	
	
}
